package com.example.demo.repository;

import com.example.demo.entity.LoaiGhe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiGheRepository extends JpaRepository<LoaiGhe, String> {

    boolean existsByTenLoaiGhe(String tenLoaiGhe);
}