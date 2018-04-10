package com.minlia.cloud.ratelimited;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConditionalOnProperty(prefix = "system.rater.",value = "enabled",havingValue = "true")
public class RequestRateLimitAutoConfigure {



  @Configuration
  public static class RequestRateLimitConfiguration extends WebMvcConfigurerAdapter {

    @Value(value = "${system.rater.endpointPackage:.}")
    private String basePackage=".";

    @Bean
    @ConditionalOnMissingBean
    public RateLimitInterceptor rateLimitInterceptor(){
      return new RateLimitInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public LimitAnnotationBean limitAnnotationBean() {
      return new LimitAnnotationBean(basePackage);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(rateLimitInterceptor()).addPathPatterns("/**");
    }
  }

}
