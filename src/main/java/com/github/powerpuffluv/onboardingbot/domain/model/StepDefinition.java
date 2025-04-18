package com.github.powerpuffluv.onboardingbot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepDefinition {
    private String info;
    private List<StepContent> contents;
}
