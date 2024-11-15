package com.openfuture.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @NotNull(message = "Title is required")
    @Size(min = 5, message = "Title should be between 5 and 100 characters")
    private String newsTitle;

    private String newsContent;

    private LocalDate publishedDate = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "adminId", nullable = true)
    @JsonIgnoreProperties
    @JsonBackReference
    private Admin admin;

//    public Long getAdmin() {
//        return admin != null ? admin.getId() : null;
//    }
}
