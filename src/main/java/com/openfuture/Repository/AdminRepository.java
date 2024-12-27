package com.openfuture.Repository;

import com.openfuture.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    boolean existsByMobileNo(String mobileNo);
    Optional<Admin> findByUsernameAndPassword(String username, String password);
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByEmailIgnoreCase(String email);
}
