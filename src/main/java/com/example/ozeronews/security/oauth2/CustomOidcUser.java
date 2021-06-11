package com.example.ozeronews.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

public class CustomOidcUser implements OidcUser {

    private OidcUser oidcUser;

    public CustomOidcUser(OidcUser oidcUser) {
        this.oidcUser = oidcUser;
    }

    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getGivenName() {
        return OidcUser.super.getGivenName();
    }

    @Override
    public String getFamilyName() {
        return OidcUser.super.getFamilyName();
    }

    @Override
    public String getPicture() {
        return OidcUser.super.getPicture();
    }

    @Override
    public String getEmail() {
        return OidcUser.super.getEmail();
    }
}
