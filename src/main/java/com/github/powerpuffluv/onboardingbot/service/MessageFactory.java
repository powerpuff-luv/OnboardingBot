package com.github.powerpuffluv.onboardingbot.service;

import com.github.powerpuffluv.onboardingbot.configuration.StepContentConfig;
import com.github.powerpuffluv.onboardingbot.domain.model.StepContent;
import com.github.powerpuffluv.onboardingbot.domain.model.StepDefinition;
import com.github.powerpuffluv.onboardingbot.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageFactory {

    private final MessageUtils messageUtils;

    private final StepContentConfig stepContentConfig;

    public List<Object> createStepMessages(Long chatId, String stepKey) {
        log.debug("Создание сообщений для шага {} (чат: {})", stepKey, chatId);
        StepDefinition stepDefinition = stepContentConfig.getStep(stepKey);
        List<Object> result = new ArrayList<>();

        if (stepDefinition == null || stepDefinition.getContents() == null) {
            log.warn("Шаг {} не найден или не содержит контента", stepKey);
            result.add(createTextMessage(chatId, "Завершено!"));
            return result;
        }

        for (StepContent content : stepDefinition.getContents()) {
            String buttonText = content.getButton();
            switch (content.getType()) {
                case TEXT -> result.add(createTextMessage(chatId, content.getContent(), buttonText, stepKey));
                case PHOTO -> result.add(createPhotoMessage(chatId, content.getContent(), buttonText, stepKey));
                case VIDEO -> result.add(createVideoMessage(chatId, content.getContent(), buttonText, stepKey));
                case STICKER -> result.add(createStickerMessage(chatId, content.getContent(), buttonText, stepKey));
            }
        }
        return result;
    }

    private SendMessage createTextMessage(Long chatId, String text) {
        return createTextMessage(chatId, text, null, null);
    }

    private SendMessage createTextMessage(Long chatId, String text, String buttonText, String callbackData) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(chatId, text);
        sendMessage.setReplyMarkup(createInlineKeyboard(buttonText, callbackData));
        return sendMessage;
    }

    private SendSticker createStickerMessage(Long chatId, String stickerId, String buttonText, String callbackData) {
        SendSticker sendSticker = messageUtils.generateSendStickerWithStickerId(chatId, stickerId);
        sendSticker.setReplyMarkup(createInlineKeyboard(buttonText, callbackData));
        return sendSticker;
    }

    private SendPhoto createPhotoMessage(Long chatId, String fileName, String buttonText, String callbackData) {
        SendPhoto sendPhoto = messageUtils.generateSendPhotoWithFileName(chatId, fileName);
        sendPhoto.setReplyMarkup(createInlineKeyboard(buttonText, callbackData));
        return sendPhoto;
    }

    private SendVideo createVideoMessage(Long chatId, String fileName, String buttonText, String callbackData) {
        SendVideo sendVideo = messageUtils.generateSendVideoWithFileName(chatId, fileName);
        sendVideo.setReplyMarkup(createInlineKeyboard(buttonText, callbackData));
        return sendVideo;
    }

    private InlineKeyboardMarkup createInlineKeyboard(String buttonText, String callbackData) {
        if (buttonText != null && callbackData != null) {
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData(callbackData);
            return new InlineKeyboardMarkup(List.of(List.of(button)));
        }
        return null;
    }

}
