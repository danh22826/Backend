package com.example.demo.repository;

import com.example.demo.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    // Hàm này giúp Spring Security tìm tài khoản trong DB khi người dùng gõ chữ đăng nhập
    Optional<NguoiDung> findByUsername(String username);

    Optional<NguoiDung> findByUsernameIgnoreCase(String username);

    boolean existsByUsername(String username);
}
