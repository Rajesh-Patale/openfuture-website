package com.openfuture.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Ensure uniqueness
    private String otp;

    @OneToOne(targetEntity = Admin.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Admin admin;

    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;
}