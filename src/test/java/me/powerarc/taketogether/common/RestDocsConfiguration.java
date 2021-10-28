package me.powerarc.taketogether.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@TestConfiguration // Test 에서 Import 해줘야 적용됨
public class RestDocsConfiguration {

    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer() {
        return configurer -> { // 람다식
            configurer.operationPreprocessors()
                    .withRequestDefaults(prettyPrint()) // 이쁘게
                    .withResponseDefaults(prettyPrint()); // 이쁘게
        };
    }
}
