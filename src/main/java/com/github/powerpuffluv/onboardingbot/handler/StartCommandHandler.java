package com.github.powerpuffluv.onboardingbot.handler;

import com.github.powerpuffluv.onboardingbot.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@RequiredArgsConstructor
@Component
public class StartCommandHandler {
    private final OnboardingService onboardingService;

    public List<Object> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        return onboardingService.startOnboarding(chatId);
    }
}
