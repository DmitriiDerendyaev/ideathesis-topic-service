package ru.derendyaev.ideathesis_topic_service.restUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.auth.GigaToken;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageRequest;
import ru.derendyaev.ideathesis_topic_service.gigaChat.models.message.GigaMessageResponse;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class Client {

    private WebClient webClientToken;
    private WebClient webClientChat;
    private volatile GigaToken cachedToken;

    @Value("${app.values.api.giga-chat.chat-settings.scope}")
    private String scope;

    @Value("${app.values.api.giga-chat.base-url.auth}")
    private String baseUrlAuth;

    @Value("${app.values.api.giga-chat.base-url.chat}")
    private String baseUrlGigaChat;

    @Value("${app.values.api.giga-chat.auth-key}")
    private String authKey;

    @PostConstruct
    public void init() {
        this.webClientChat = WebClientFactory.createWebClient(baseUrlGigaChat);
        this.webClientToken = WebClientFactory.createWebClient(baseUrlAuth);
    }

    public synchronized GigaToken getToken() {
        if (cachedToken == null || isTokenExpired(cachedToken)) {
            log.info("Получение нового токена");
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            tokenHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            tokenHeaders.set("Authorization", "Basic " + authKey);
            tokenHeaders.put("RqUID", Collections.singletonList(getUUID()));

            cachedToken = webClientToken
                    .post()
                    .uri("/api/v2/oauth")
                    .headers(httpHeaders -> httpHeaders.addAll(tokenHeaders))
                    .body(BodyInserters.fromFormData("scope", scope))
                    .retrieve()
                    .bodyToMono(GigaToken.class)
                    .block();
        }
        return cachedToken;
    }

    private boolean isTokenExpired(GigaToken token) {
        return token.getExpiresAt() <= System.currentTimeMillis() + 60_000; // 1 минута запаса
    }

    public GigaMessageResponse gigaMessageGenerate(GigaMessageRequest request) {
        HttpHeaders messageHeaders = new HttpHeaders();
        messageHeaders.setContentType(MediaType.APPLICATION_JSON);
        messageHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        messageHeaders.put("X-Request-ID", Collections.singletonList(getUUID()));
        messageHeaders.setBearerAuth(getToken().getAccessToken());

        log.info("Запрос к GigaChat: {}", request);

        try {
            return webClientChat
                    .post()
                    .uri("/api/v1/chat/completions")
                    .headers(httpHeaders -> httpHeaders.addAll(messageHeaders))
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GigaMessageResponse.class).log()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Ошибка GigaChat: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }
}