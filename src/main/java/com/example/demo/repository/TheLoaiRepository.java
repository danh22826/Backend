package com.example.demo.repository;

import com.example.demo.entity.TheLoai;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheLoaiRepository extends JpaRepository<TheLoai, String> {

    List<TheLoai> findByTenTheLoaiContainingIgnoreCase(String keyword);

    List<TheLoai> findByMaTheLoaiIn(List<String> ids);
}