package com.example.demo.repository;

import com.example.demo.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findFirstByTenKhachHang(String tenKhachHang);

    Optional<KhachHang> findFirstByEmailIgnoreCase(String email);

    Optional<KhachHang> findFirstByUsernameIgnoreCase(String username);
}
