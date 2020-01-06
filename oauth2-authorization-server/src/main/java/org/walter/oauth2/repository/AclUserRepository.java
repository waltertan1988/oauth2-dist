package org.walter.oauth2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.walter.oauth2.entity.JpaAclUser;

import java.util.List;

public interface AclUserRepository extends JpaRepository<JpaAclUser, Long>{

	@Query("FROM JpaAclUser o WHERE o.username=:usernameOrMobile OR o.mobile=:usernameOrMobile")
	public JpaAclUser findByUsernameOrMobile(String usernameOrMobile);
	
	public List<JpaAclUser> findByUserRealName(String userRealName);
}
