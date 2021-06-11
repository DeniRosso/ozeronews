package com.example.ozeronews.security.oauth2;

import com.example.ozeronews.exception.OAuth2AuthenticationProcessingException;
import com.example.ozeronews.models.AuthProvider;
import com.example.ozeronews.models.Role;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepo;
import com.example.ozeronews.security.UserPrincipal;
import com.example.ozeronews.security.oauth2.user.UserInfo;
import com.example.ozeronews.security.oauth2.user.UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            String authProvider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
            UserInfo userInfo = UserInfoFactory.getUserInfo(authProvider, oAuth2User.getAttributes());

            return processAuthorizationUser(userInfo, authProvider);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    public UserPrincipal processAuthorizationUser(UserInfo userInfo, String authProvider) {

        // Проверка существования email у авторизованного пользователя
        if (StringUtils.isEmpty(userInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        // Проверка существования пользователя с email авторизованного пользователя
        User user = userRepo.findByEmail(userInfo.getEmail());

        if(user != null) {
            if(!user.getAuthProvider().stream().findFirst().get().equals(AuthProvider.valueOf(authProvider))) {
                System.err.println("Looks like you're signed up with " + user.getAuthProvider() + " account. " +
                        "Please use your " + user.getAuthProvider() + " account to login.");
                throw new OAuth2AuthenticationProcessingException(
                        "Looks like you're signed up with " + user.getAuthProvider() + " account. " +
                                " Please use your " + user.getAuthProvider() + " account to login.");
            }
            user = updateAuthorizationUser(userInfo, user);
        } else {
            user = createAuthorizationUser(userInfo, authProvider);
        }
        return UserPrincipal.create(user);
    }

    private User createAuthorizationUser(UserInfo userInfo, String authProvider) {
        User user = new User();
        user.setUsername(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setPassword(null);
        user.setAvatar(userInfo.getPicture());
        user.setRole(Collections.singleton(Role.USER));
        user.setAuthProvider(Collections.singleton(AuthProvider.valueOf(authProvider)));
        user.setActive(true);
        user.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        return userRepo.save(user);
    }

    private User updateAuthorizationUser(UserInfo userInfo, User user) {
        user.setUsername(userInfo.getName());
        user.setAvatar(userInfo.getPicture());
        user.setActive(true);
        return userRepo.save(user);
    }

}
