CREATE TABLE `acl_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8_bin NOT NULL,
  `password` varchar(255) COLLATE utf8_bin NOT NULL,
  `user_real_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `mobile` varchar(11) COLLATE utf8_bin DEFAULT NULL,
  `gender` varchar(1) COLLATE utf8_bin NOT NULL,
  `is_expired` bit(1) NOT NULL,
  `is_locked` bit(1) NOT NULL,
  `is_password_expired` bit(1) NOT NULL,
  `is_enabled` bit(1) NOT NULL,
  `created_by` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `last_modified_by` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_51bvuyvihefoh4kp5syh2jpi4` (`username`),
  UNIQUE KEY `UK_51bvuyvihefoh4kp5syh2jpi5` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
insert into `acl_user` (`id`, `username`, `password`, `user_real_name`, `mobile`, `gender`, `is_expired`, `is_locked`, `is_password_expired`, `is_enabled`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`) values(null,'0009785','$2a$10$8r2tGdMtSLfEHbYV/3ZmE.90ivYb7h1y7TyHy86l9mDADMA89SH2O','walter.tan','13123456789','M','','','',b'1','*ADMIN',now(),'*ADMIN',now());
insert into `acl_user` (`id`, `username`, `password`, `user_real_name`, `mobile`, `gender`, `is_expired`, `is_locked`, `is_password_expired`, `is_enabled`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`) values(null,'walter','$2a$10$8r2tGdMtSLfEHbYV/3ZmE.90ivYb7h1y7TyHy86l9mDADMA89SH2O','waltertan','13987654321','M','','','',b'1','*ADMIN',now(),'*ADMIN',now());

CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) NOT NULL,
  `resource_ids` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `authorized_grant_types` varchar(255) DEFAULT NULL,
  `web_server_redirect_uri` varchar(255) DEFAULT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` text,
  `autoapprove` varchar(255) DEFAULT 'false',
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `oauth_client_details`
(`client_id`, `resource_ids`, `client_secret`, `scope`, `authorized_grant_types`, `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) values
('resServer',NULL,'resServerSecret',NULL,NULL,'http://localhost:7081/resServer/oauth2/redirect',NULL,'3600','86400',NULL,'false');
