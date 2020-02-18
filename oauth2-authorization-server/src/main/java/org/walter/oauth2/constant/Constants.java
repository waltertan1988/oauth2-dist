package org.walter.oauth2.constant;

public class Constants {
    private Constants(){}
    public static class LoginFormFieldName {
        private LoginFormFieldName(){}
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String GRANT_TYPE = "grant-type";
        public static final String CLIENT_ID = "client-id";
        public static final String CLIENT_SECRET = "client-secret";
    }

    public static class Cookie{
        private Cookie(){}
        public static final String LOGIN_REDIRECT_URL_COOKIE_KEY = "loginRedirectUrl";
        public static final String AUTHENTICATION_COOKIE_KEY = "authentication";
    }

    public static class Cache {
        private Cache(){}
        public static class Security {
            private Security(){}

            /** %s为授权码的值 */
            public static final String AUTHORIZATION_CODE_PATTERN = "security:authCode:%s";
            /** %s为username */
            public static final String AUTHENTICATION_KEY_PATTERN = "security:authentication:%s";
        }
    }
}
