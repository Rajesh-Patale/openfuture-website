package com.openfuture.Repository;

import com.openfuture.Entity.Admin;
import com.openfuture.Entity.ForgotPasswordOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ForgotPasswordOtpRepository extends JpaRepository<ForgotPasswordOtp, Long> {

    Optional<ForgotPasswordOtp> findByOtp(String otp);

    Optional<ForgotPasswordOtp> findByAdmin(Admin Admin);

}