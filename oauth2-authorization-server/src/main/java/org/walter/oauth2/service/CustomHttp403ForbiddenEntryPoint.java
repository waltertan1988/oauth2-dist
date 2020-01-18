package org.walter.oauth2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomHttp403ForbiddenEntryPoint extends Http403ForbiddenEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		String message = String.format("你暂无权限访问该资源：%s", request.getRequestURI());

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpStatus.FORBIDDEN.value());
		PrintWriter out = response.getWriter();
		out.println(message);
		out.println(new ObjectMapper().writeValueAsString(exception.getMessage()));
		out.flush();
	}
}
