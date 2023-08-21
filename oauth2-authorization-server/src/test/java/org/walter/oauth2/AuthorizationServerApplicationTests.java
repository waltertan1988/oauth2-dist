package org.walter.oauth2;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;

@SpringBootTest(classes = AuthorizationServerApplication.class)
class AuthorizationServerApplicationTests {

	@Test
	@Disabled
	void contextLoads() {
	}

	@Nested
	@DisplayName("测试UserDetailsService类")
	class UserDetailsServiceTest {

		@Resource(name = "userDetailsServiceImpl")
		private UserDetailsService userDetailsService;

		private static final String USERNAME_EXIST = "0009785";
		private static final String USERNAME_NOT_EXIST = StringUtils.EMPTY;

		@ParameterizedTest
		@ValueSource(strings = {USERNAME_EXIST, USERNAME_NOT_EXIST})
		void loadUserByUsername(String username) {
			switch (username) {
				case USERNAME_EXIST:
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					Assertions.assertAll(
							() -> Assertions.assertNotNull(userDetails),
							() -> Assertions.assertEquals(username, userDetails.getUsername())
					);
					break;
				case USERNAME_NOT_EXIST:
					Assertions.assertThrows(UsernameNotFoundException.class,
							() -> userDetailsService.loadUserByUsername(username)
					);
					break;
				default:
					Assertions.fail("没有测试逻辑的参数：" + username);
			}
		}
	}
}
