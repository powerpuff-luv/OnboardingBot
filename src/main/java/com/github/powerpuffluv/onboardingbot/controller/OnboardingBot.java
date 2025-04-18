package com.github.powerpuffluv.onboardingbot.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OnboardingBot extends TelegramWebhookBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.uri}")
    private String botUri;

    private final UpdateDispatcher updateDispatcher;

    @PostConstruct
    public void init() {
        updateDispatcher.registerBot(this);
        try {
            SetWebhook setWebhook = SetWebhook.builder()
                    .url(botUri)
                    .build();
            this.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendAnswerMessage(Object message) {
        if (message != null) {
            try {
                log.trace("Отправка сообщения");
                switch (message) {
                    case SendMessage sendMessage -> execute(sendMessage);
                    case SendSticker sendSticker -> execute(sendSticker);
                    case SendPhoto sendPhoto -> execute(sendPhoto);
                    case SendVideo sendVideo -> execute(sendVideo);
                    default -> log.error("Неизвестный тип сообщения: {}", message.getClass().getSimpleName());
                }
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке сообщения", e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }
}
