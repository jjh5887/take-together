package me.powerarc.taketogether.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Getter
@Setter
public class AppProperties {
    @Value("#{${domain}}")
    private Map<String, List<String>> domain = new HashMap<>();
}
