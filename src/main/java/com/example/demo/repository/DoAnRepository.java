package com.example.demo.repository;

import com.example.demo.entity.DoAn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoAnRepository extends JpaRepository<DoAn, String> {
    List<DoAn> findByTrangThaiIgnoreCase(String trangThai);
}
