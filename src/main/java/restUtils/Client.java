package ru.derendyaev.mospolytech.restUtils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.derendyaev.mospolytech.exceptions.TokenException;
import ru.derendyaev.mospolytech.gigaChat.models.auth.GigaToken;
import ru.derendyaev.mospolytech.gigaChat.models.message.GigaMessageRequest;
import ru.derendyaev.mospolytech.gigaChat.models.message.GigaMessageResponse;
import ru.derendyaev.mospolytech.gigaChat.models.message.Message;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static ru.derendyaev.mospolytech.gigaChat.models.GigaChatConstant.*;

@Slf4j
@Service
public class Client {

    private WebClient webClientToken;
    private WebClient webClientChat;

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

    public GigaToken getToken() {
        log.info("Создан новый токен!");
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.put("RqUID", Collections.singletonList(getUUID()));
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tokenHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        tokenHeaders.set("Authorization", "Basic " + authKey);

        return this.webClientToken
                .post()
                .uri("/api/v2/oauth")
                .headers(httpHeaders -> httpHeaders.addAll(tokenHeaders))
                .body(BodyInserters.fromFormData("scope", scope))
                .retrieve()
                .bodyToMono(GigaToken.class)
                .block();
    }

    public GigaMessageResponse gigaMessageGenerate(String context, String userRequest, @Nullable String token) {
//        checkToken(token);
        HttpHeaders messageHeaders = new HttpHeaders();
        messageHeaders.setContentType(MediaType.APPLICATION_JSON);
        messageHeaders.put("X-Request-ID", Collections.singletonList(getUUID()));
        messageHeaders.put("X-Session-ID", Collections.singletonList(getUUID()));
        messageHeaders.put("X-Client-ID", Collections.singletonList(getUUID()));
        messageHeaders.setBearerAuth(getToken().getAccessToken());

        log.info("Создан запрос в GigaChat c контекстом: {} и тематикой: {}", context, userRequest);

        return this.webClientChat
                .post()
                .uri("/api/v1/chat/completions")
                .headers(httpHeaders -> httpHeaders.addAll(messageHeaders))
                .bodyValue(new GigaMessageRequest(
                        GIGA_CHAT_MODEL,
                        false,
                        0,
                        List.of(new Message(SYSTEM_ROLE, context),
                                new Message(USER_ROLE, userRequest))
                        ))
                .retrieve()
                .bodyToMono(GigaMessageResponse.class).block();
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private void checkToken(String token) {
        if (token == null) {
            throw new TokenException("Expired token!");
        }
    }


}
