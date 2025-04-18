package com.github.powerpuffluv.onboardingbot.utils;

import com.github.powerpuffluv.onboardingbot.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;

import static com.github.powerpuffluv.onboardingbot.constants.MessageUtilsConstants.IMAGES_DIRECTORY;
import static com.github.powerpuffluv.onboardingbot.constants.MessageUtilsConstants.VIDEOS_DIRECTORY;

@Slf4j
@Component
public class MessageUtils {

    public SendMessage generateSendMessageWithText(Long chatId, String text) {
        log.debug("Создание текстового сообщения для чата {}", chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");
        sendMessage.setDisableWebPagePreview(true);
        return sendMessage;
    }

    public SendSticker generateSendStickerWithStickerId(Long chatId, String stickerId) {
        log.debug("Создание стикера для чата {}, ID стикера: {}", chatId, stickerId);
        return new SendSticker(chatId.toString(), new InputFile(stickerId));
    }

    public SendPhoto generateSendPhotoWithFileName(Long chatId, String fileName) {
        log.debug("Создание фото для чата {}, файл: {}", chatId, fileName);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());

        InputStream stream = getClass().getClassLoader().getResourceAsStream(IMAGES_DIRECTORY + fileName);
        if (stream == null) {
            String errorMessage = String.format("Изображение не найдено, полный путь: %s", IMAGES_DIRECTORY + fileName);
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        sendPhoto.setPhoto(new InputFile(stream, fileName));

        return sendPhoto;
    }

    public SendVideo generateSendVideoWithFileName(Long chatId, String fileName) {
        log.debug("Создание видео для чата {}, файл: {}", chatId, fileName);
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId.toString());

        InputStream stream = getClass().getClassLoader().getResourceAsStream(VIDEOS_DIRECTORY + fileName);
        if (stream == null) {
            String errorMessage = String.format("Видео не найдено, полный путь: %s", VIDEOS_DIRECTORY + fileName);
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        sendVideo.setVideo(new InputFile(stream, fileName));

        return sendVideo;
    }
}
