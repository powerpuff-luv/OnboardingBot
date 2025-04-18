package com.github.powerpuffluv.onboardingbot.configuration;

import com.github.powerpuffluv.onboardingbot.domain.model.StepDefinition;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Configuration
@ConfigurationProperties
public class StepContentConfig {
    private LinkedHashMap<String, StepDefinition> steps;

    private List<String> stepOrder;
    private Map<String, String> stepInfoMap;

    @PostConstruct
    public void init() {
        stepOrder = new ArrayList<>(steps.keySet());
        stepInfoMap = steps.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Optional.ofNullable(e.getValue().getInfo()).orElse("")
                ));
    }

    public StepDefinition getStep(String stepKey) {
        return steps.get(stepKey);
    }
}
