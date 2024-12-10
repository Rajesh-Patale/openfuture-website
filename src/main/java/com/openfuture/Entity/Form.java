// class form
package com.openfuture.Entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

// Used to Apply JOb which will upload by Admin
public class Form {

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
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$", message = "Email must be in lowercase")
    private String email;
    @NotBlank(message = "Country is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Country must contain only alphabetic characters")
    private String country;
    @NotNull(message = "Phone number is required")@Min(value = 1000000000L, message = "Phone number must be 10 digits")
    @Max(value = 9999999999L, message = "Phone number must be 10 digits")
    private Long mobileNumber;

    private String position;
    private String location;
    @Lob
    private byte[] cv;

    @ManyToOne(fetch =FetchType.LAZY )
    @JoinColumn(name = "admin_id",nullable = false)
    @JsonBackReference
    private Admin admin;
}
