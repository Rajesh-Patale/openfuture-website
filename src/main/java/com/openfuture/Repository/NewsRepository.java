package com.openfuture.Repository;

import com.openfuture.Entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<News,Long> {

    Optional<News> findByAdminId(Long adminId);
}