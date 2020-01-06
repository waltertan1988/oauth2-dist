package org.walter.oauth2.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan("org.walter.oauth2.entity")
@EnableJpaRepositories("org.walter.oauth2.repository")
public class JpaConfig {

}
