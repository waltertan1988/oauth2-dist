package org.walter.oauth2.service;

public interface ClientInfoService {

    String getOauth2ClientId();
    String getOauth2ClientSecret();
    String getOauth2RedirectURl();
    String getOauth2State();
    String getOauth2Scope();
}
