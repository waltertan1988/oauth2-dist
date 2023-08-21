package org.walter.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest(classes = AuthorizationServerApplication.class)
class AuthorizationServerApplicationTests {

	@Resource(name = "userDetailsServiceImpl")
	private UserDetailsService userDetailsService;

	@Test
	@Disabled
	void contextLoads() {
	}

	@Test
	void loadUserByUsername() {
		String username = "0009785";
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		Assertions.assertNotNull(userDetails);
		Assertions.assertEquals(username, userDetails.getUsername());
	}
}
