package com.example.ozeronews.config;

import com.example.ozeronews.models.Head;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${company.name}")
    private String companyName;

    @Value("${website.name}")
    private String websiteName;

    @Value("${website.url}")
    private String websiteURL;

    @Value("${website.logo}")
    private String websiteLogo;

    @Value("${website.email.help}")
    private String emailHelp;

    @Value("${website.email.support}")
    private String emailSupport;

    @Value("${website.description}")
    private String websiteDescription;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }


    @Bean
    public Head getHead() {
        return new Head(companyName, websiteName, websiteURL, websiteLogo, emailHelp, emailSupport,
                websiteName, websiteDescription, websiteLogo);
    }




//    @Bean
    public LocaleResolver localeResolver() {

//        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of("Europe/Moscow"));
//
//        SessionLocaleResolver slr = new SessionLocaleResolver();
//        slr.setDefaultTimeZone(timeZone);

        CookieLocaleResolver cls = new CookieLocaleResolver();
//        cls.setDefaultTimeZone(timeZone);
        cls.setDefaultLocale(new Locale("en"));
        cls.setCookieName("timezone");

        return cls;
    }

//    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
//        lci.setParamName("timezone");

        return lci;
    }


}
