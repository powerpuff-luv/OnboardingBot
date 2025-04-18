package com.github.powerpuffluv.onboardingbot.service.impl;

import com.github.powerpuffluv.onboardingbot.service.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InMemoryUserStateService implements UserStateService {
    private final Map<Long, String> userStates = new ConcurrentHashMap<>();

    @Override
    public String getState(Long chatId) {
        return userStates.get(chatId);
    }

    @Override
    public void setState(Long chatId, String state) {
        log.debug("Обновление состояния для чата {}: {}", chatId, state);
        userStates.put(chatId, state);
    }
}
