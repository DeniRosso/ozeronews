package com.example.ozeronews.config;

import com.example.ozeronews.security.CustomUserDetailsService;
import com.example.ozeronews.security.oauth2.CustomOAuth2AuthenticationSuccess;
import com.example.ozeronews.security.oauth2.CustomOAuth2UserService;
import com.example.ozeronews.security.oauth2.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomOidcUserService customOidcUserService;

    private CustomUserDetailsService customUserDetailsService;
    private CustomOAuth2AuthenticationSuccess customOAuth2AuthenticationSuccess;

    @Autowired
    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService,
                             CustomOAuth2AuthenticationSuccess customOAuth2AuthenticationSuccess) {
        this.customUserDetailsService = customUserDetailsService;
        this.customOAuth2AuthenticationSuccess = customOAuth2AuthenticationSuccess;
    }

    //    @Autowired
//    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

//    @Autowired
//    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

//    @Autowired
//    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

//    @Bean
//    public TokenAuthenticationFilter tokenAuthenticationFilter() {
//        return new TokenAuthenticationFilter();
//    }

//    @Bean
//    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
//        return new HttpCookieOAuth2AuthorizationRequestRepository();
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(10);
//    }

    // Redirect to HTTPS
//    @Bean
//    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//                // ...
//                .redirectToHttps()
//                .httpsRedirectWhen(e -> e.getRequest().getHeaders().containsKey("X-Forwarded-Proto"));
//        return http.build();
//    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        auth.userDetailsService(customUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // For redirect to https:// !!!Need to testing https://www.baeldung.com/spring-channel-security-https
//        http
//            .requiresChannel()
//                .antMatchers("/**")
//                .requiresSecure();
//                .and()

            // For Heroku
//            .requiresChannel()
//                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
//                .requiresSecure()
//                .and()
        http
//            .requiresChannel()
//                .antMatchers("/**")
//                .requiresSecure()
//                .and()
            .authorizeRequests()
                .antMatchers(
                        "/static/**",
                        "/",
                        "/news/**",
                        "/about/**",
                        "/about/contact",
                        "/login",
//                        "/login/**",
//                        "/oauth2/**",
//                        "/oauth_login",
                        "/logout",
                        "/registration",
                        "/registrationsuccess",
                        "/activate/*",
                        "/recovery/**")
                .permitAll()
//                .mvcMatchers("/administration").hasRole("ADMIN")
                .anyRequest()
                    .authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error")
                .permitAll()
                .and()
            .oauth2Login()
                .loginPage("/login")
//                .loginPage("/oauth_login")
//                .defaultSuccessUrl("/users/loginSuccess")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error")
//                .permitAll()
//                .authorizationEndpoint()
//                    .baseUri("/oauth2/authorization")
//                    .authorizationRequestRepository(cookieAuthorizationRequestRepository())
//                    .and()
//                .redirectionEndpoint()
//                    .baseUri("/login/oauth2/code/*")
//                    .and()
//                .tokenEndpoint()
//                    .and()
                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                    .oidcUserService(customOidcUserService)
                    .and()
//                .successHandler(oAuth2AuthenticationSuccess)
//                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
            .rememberMe()
//                .rememberMeParameter("remember-me")
//                .tokenValiditySeconds(86400)
            .and()
//                .exceptionHandling(e -> e
//                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
//                .oauth2Login()
//            .and()
//                .oauth2Login().redirectionEndpoint().baseUri("/custom-callback")
//                .and()
            .logout()
                .logoutUrl("/logout")
//                .logoutSuccessUrl("/")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
//                .deleteCookies("JSESSIONID")
//              .and()
//                .exceptionHandling().accessDeniedPage("/403")
        ;
    }
}
