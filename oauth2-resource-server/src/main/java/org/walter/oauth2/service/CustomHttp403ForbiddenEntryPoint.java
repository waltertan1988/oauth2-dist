package org.walter.oauth2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;
import org.walter.oauth2.properties.OAuth2SecurityProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomHttp403ForbiddenEntryPoint extends Http403ForbiddenEntryPoint {
	@Autowired
	private OAuth2SecurityProperties oAuth2SecurityProperties;
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpStatus.FORBIDDEN.value());
		PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");
		out.println(String.format("你暂无权限访问资源[%s]，请先<a href='%s'>授权</a><br>",
				request.getRequestURI(), oAuth2SecurityProperties.getOauth2AuthorizeRequest()));
		out.println(new ObjectMapper().writeValueAsString(exception.getMessage()));
		out.flush();
	}
}
