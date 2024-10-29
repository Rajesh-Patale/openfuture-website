package com.openfuture.Entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String mobileNo;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Lob
    @Column(name = "profile_picture",columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    @Nullable
    private byte[] profilePicture;

}
