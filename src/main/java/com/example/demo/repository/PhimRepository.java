package com.example.demo.repository;

import com.example.demo.entity.Phim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhimRepository extends JpaRepository<Phim, String> {

    List<Phim> findByTenPhimContainingIgnoreCase(String keyword);
}