package com.openfuture.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name should be between 2 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only alphabetic characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30, message = "Last name should be between 2 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must contain only alphabetic characters")
    private String lastName;

    @NotBlank(message = "Country is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Country must contain only alphabetic characters")
    private String country;

    @NotBlank(message = "Designation is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Designation must contain only alphabetic characters")
    private String designation;

    @NotBlank(message = "Company is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Company name must contain only alphabetic characters")
    private String company;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$", message = "Email must be in lowercase")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone number should be 10 digits")
    private String phone;

    private String industry;
    private String message;

    private LocalDate dateCreated;

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDate.now();
    }
}
