package com.github.powerpuffluv.onboardingbot.controller;

import com.github.powerpuffluv.onboardingbot.handler.CallbackHandler;
import com.github.powerpuffluv.onboardingbot.handler.StartCommandHandler;
import com.github.powerpuffluv.onboardingbot.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.powerpuffluv.onboardingbot.constants.UpdateDispatcherConstants.UNSUPPORTED_MESSAGE_TYPE;
import static com.github.powerpuffluv.onboardingbot.constants.UpdateDispatcherConstants.UNSUPPORTED_TEXT_MESSAGE;


@Slf4j
@RequiredArgsConstructor
@Component
public class UpdateDispatcher {

    private OnboardingBot telegramBot;

    private final StartCommandHandler startHandler;

    private final CallbackHandler callbackHandler;

    private final MessageUtils messageUtils;

    public void registerBot(OnboardingBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void dispatch(Update update) {
        if (update == null) {
            log.warn("Получен пустой update");
            return;
        }

        if (update.hasMessage()) {
            distributeMessageByType(update);
        } else if (update.hasCallbackQuery()) {
            log.debug("Обработка callback от чата {}", update.getCallbackQuery().getMessage().getChatId());
            processCallbackMessage(update);
        } else {
            log.warn("Получен неподдерживаемый тип сообщения: {}", update);
        }
    }

    private void distributeMessageByType(Update update) {
        Message message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else {
            log.debug("Неподдерживаемый тип медиа в сообщении от чата {}", message.getChatId());
            setUnsupportedMessageTypeView(update);
        }
    }

    private void processTextMessage(Update update) {
        String text = update.getMessage().getText();
        if ("/start".equals(text)) {
            log.info("Обработка команды /start для чата {}, пользователь - {}", update.getMessage().getChatId(), update.getMessage().getChat().getUserName());
            startHandler.handle(update).forEach(this::setView);
        } else {
            log.debug("Получена неподдерживаемая текстовая команда '{}' от чата {}", text, update.getMessage().getChatId());
            setUnsupportedTexMessageView(update);
        }
    }

    private void processCallbackMessage(Update update) {
        callbackHandler.handle(update).forEach(this::setView);
    }

    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update.getMessage().getChatId(), UNSUPPORTED_MESSAGE_TYPE);
        setView(sendMessage);
    }

    private void setUnsupportedTexMessageView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update.getMessage().getChatId(), UNSUPPORTED_TEXT_MESSAGE);
        setView(sendMessage);
    }

    private void setView(Object model) {
        telegramBot.sendAnswerMessage(model);
    }
}
