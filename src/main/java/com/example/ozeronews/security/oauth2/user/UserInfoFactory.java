package com.example.ozeronews.security.oauth2.user;

import com.example.ozeronews.exception.OAuth2AuthenticationProcessingException;
import com.example.ozeronews.models.AuthProvider;

import java.util.Map;

public class UserInfoFactory {

    public static UserInfo getUserInfo(String registrationId, Map<String, Object> attributes) throws OAuth2AuthenticationProcessingException {
        if(registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleUserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.toString())) {
            return new FacebookUserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
