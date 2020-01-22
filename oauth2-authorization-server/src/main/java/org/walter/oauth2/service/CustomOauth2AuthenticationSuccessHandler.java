package org.walter.oauth2.service;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.walter.oauth2.constant.Constants;
import org.walter.oauth2.constant.GrantType;
import org.walter.oauth2.properties.CustomSecurityProperties;
import org.walter.oauth2.utils.SerializerUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CustomOauth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Autowired
	private CustomSecurityProperties customSecurityProperties;
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Autowired
	private AuthorizationServerTokenServices authorizationServerTokenServices;
	@Autowired
	private CookieService cookieService;

	/**
	 * 认证成功后的处理逻辑
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		cookieService.addAuthenticationCookie(response, authentication);

		String grantType = request.getParameter(Constants.LoginFormFieldName.GRANT_TYPE);

		if(StringUtils.equals(grantType, GrantType.CUSTOM.getValue())){
			String clientId = request.getParameter(Constants.LoginFormFieldName.CLIENT_ID);
			String clientSecret = request.getParameter(Constants.LoginFormFieldName.CLIENT_SECRET);
			ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
			if(null == clientDetails){
				throw new OAuth2Exception("clientId not exists: " + clientId);
			}else if(!clientDetails.getClientSecret().equals(clientSecret)){
				throw new OAuth2Exception("clientSecret error: " + clientSecret);
			}

			TokenRequest tokenRequest = new TokenRequest(Maps.newHashMap(), clientId, clientDetails.getScope(), customSecurityProperties.getOauth2GrantType());
			OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
			OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
			OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
			PrintWriter out = response.getWriter();
			out.print(SerializerUtil.toJson(oAuth2AccessToken));
			out.flush();
		}else{
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}
}
