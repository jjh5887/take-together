package me.powerarc.taketogether.config;

import me.powerarc.taketogether.event.EventPagedResourceAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public EventPagedResourceAssembler eventPagedResourceAssembler() {
        return new EventPagedResourceAssembler(new HateoasPageableHandlerMethodArgumentResolver(), null);
    }
}
