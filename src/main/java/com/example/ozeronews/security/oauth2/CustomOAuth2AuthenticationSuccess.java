package com.example.ozeronews.security.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomOAuth2AuthenticationSuccess extends SimpleUrlAuthenticationSuccessHandler {

//    @Autowired
//    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("onAuthenticationSuccess (request) = " + request);
        System.out.println("onAuthenticationSuccess (response) = " + response);
        System.out.println("onAuthenticationSuccess (authentication) = " + authentication);

        if (authentication.getPrincipal() instanceof OAuth2User) {
            System.out.println("onAuthenticationSuccess = OAuth2User");
            OAuth2User user = (OAuth2User) authentication.getPrincipal();
            System.out.println("authentication.getPrincipal().getClass() = "
                    + authentication.getPrincipal().getClass().getSimpleName());
            System.out.println("email = " + user.getAttributes());
        }
        if (authentication.getPrincipal() instanceof OidcUser) {
            System.out.println("onAuthenticationSuccess = OidcUser");
            OidcUser user = (OidcUser) authentication.getPrincipal();
            System.out.println("authentication.getPrincipal().getClass() = " + authentication.getPrincipal().getClass());
            System.out.println("email = " + user.getEmail());
        }




//        userService.processOAuthPostLogin(oauthUser.getEmail());


        super.onAuthenticationSuccess(request, response, authentication);
    }


//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        FilterChain chain,
//                                        Authentication authentication) throws IOException, ServletException {
//
//        System.out.println("onAuthenticationSuccess (request) = " + request);
//        System.out.println("onAuthenticationSuccess (response) = " + response);
//        System.out.println("onAuthenticationSuccess (chain) = " + chain);
//        System.out.println("onAuthenticationSuccess (authentication) = " + authentication);
//
//        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
//
//        System.out.println("email = " + ((CustomOAuth2User) authentication.getPrincipal()).getEmail());
//
////        userService.processOAuthPostLogin(oauthUser.getEmail());
//
//
////        super.onAuthenticationSuccess(request, response, chain, authentication);
//    }
}
