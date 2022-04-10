package com.ozm.tmall.config;

import com.ozm.tmall.interceptor.LoginInterceptor;
import com.ozm.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    

    @Bean
    public OtherInterceptor getOtherIntercepter() {
        return new OtherInterceptor();
    }

    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(getLoginIntercepter()).
                addPathPatterns("/buy", "/alipay", "/payed", "/cart",
                        "/bought", "/confirmPay", "/orderConfirmed", "/forebuyone",
                        "/forebuy", "/foreaddCart", "/forecart", "/forechangeOrderItem",
                        "/foredeleteOrderItem", "/forecreateOrder", "/forepayed", "/forebought",
                        "/foreconfirmPay", "/foreorderConfirmed", "/foredeleteOrder", "/forereview", "/foredoreview"
                );


                registry.addInterceptor(getOtherIntercepter())
                .addPathPatterns("/**");

    }
}
