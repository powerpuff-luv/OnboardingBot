package com.github.powerpuffluv.onboardingbot.service;

public interface UserStateService {
    String getState(Long chatId);

    void setState(Long chatId, String state);
}
