package com.example.demo.repository;

import com.example.demo.entity.ChiTietDoAn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietDoAnRepository extends JpaRepository<ChiTietDoAn, String> {
    List<ChiTietDoAn> findByHoaDon_MaDon(String maDon);
}
