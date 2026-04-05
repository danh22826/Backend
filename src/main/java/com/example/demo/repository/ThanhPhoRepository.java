package com.example.demo.repository;

import com.example.demo.entity.ThanhPho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThanhPhoRepository extends JpaRepository<ThanhPho, String> {
   boolean existsByTenThanhPho(String tenThanhPho);
}