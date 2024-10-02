package com.ayds.zeday.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ayds.zeday.config.projection.UserDtoFiller;

@Configuration
public class ProjectionsConfig {

    @Bean
    UserDtoFiller userDtoFiller() {
        return new UserDtoFiller();
    }
}
