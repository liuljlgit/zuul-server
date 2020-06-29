package com.cloud.personal.zuulserver.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public CustomFilter customFilter(){
        return new CustomFilter();
    }
}
