package org.walter.oauth2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.walter.oauth2.entity.JpaAclUserRole;

import java.util.List;

public interface AclUserRoleRepository extends JpaRepository<JpaAclUserRole, Long>{

	public List<JpaAclUserRole> findByUsername(String username);
}
