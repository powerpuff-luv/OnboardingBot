package com.github.powerpuffluv.onboardingbot.domain.model;

import com.github.powerpuffluv.onboardingbot.domain.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepContent {
    private ContentType type;
    private String content;
    private String button;
}
