package com.github.powerpuffluv.onboardingbot.service;

import com.github.powerpuffluv.onboardingbot.configuration.StepContentConfig;
import com.github.powerpuffluv.onboardingbot.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.powerpuffluv.onboardingbot.constants.OnboardingServiceConstants.ALREADY_COMPLETED_MSG;
import static com.github.powerpuffluv.onboardingbot.constants.OnboardingServiceConstants.INFO_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Service
public class OnboardingService {

    private final UserStateService userStateService;

    private final MessageFactory messageFactory;

    private final MessageUtils messageUtils;

    private final StepContentConfig stepContentConfig;


    public List<Object> startOnboarding(Long chatId) {
        log.info("Начало онбординга для чата {}", chatId);
        String firstStep = stepContentConfig.getStepOrder().getFirst();
        userStateService.setState(chatId, firstStep);
        return messageFactory.createStepMessages(chatId, firstStep);
    }

    public List<Object> processOnboardingCallback(Long chatId, String callbackData) {
        String currentStep = userStateService.getState(chatId);

        if (currentStep == null) {
            log.info("Текущий шаг не найден для чата {}", chatId);
            return startOnboarding(chatId);
        }

        String nextStep = getNextStep(currentStep);

        if (nextStep == null) {
            return List.of(messageUtils.generateSendMessageWithText(chatId, ALREADY_COMPLETED_MSG));
        }

        if (callbackData.equals(currentStep)) {
            log.debug("Переход к следующему шагу для чата {}: {} -> {}", chatId, currentStep, nextStep);
            userStateService.setState(chatId, nextStep);
            return messageFactory.createStepMessages(chatId, nextStep);
        }

        String infoAboutStep = stepContentConfig.getStepInfoMap().getOrDefault(currentStep, "");
        return List.of(messageUtils.generateSendMessageWithText(chatId, INFO_PREFIX + infoAboutStep));
    }


    private String getNextStep(String currentStep) {
        List<String> stepOrder = stepContentConfig.getStepOrder();
        int index = stepOrder.indexOf(currentStep);
        if (index >= 0 && index < stepOrder.size() - 1) {
            return stepOrder.get(index + 1);
        }
        return null;
    }
}
