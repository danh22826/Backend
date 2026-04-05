package com.example.demo.controller;

import com.example.demo.dto.response.AdminLedgerTransactionResponse;
import com.example.demo.dto.response.AdminUserSummaryResponse;
import com.example.demo.dto.response.HoaDonDetailResponse;
import com.example.demo.service.AdminUserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    public List<AdminUserSummaryResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @GetMapping("/users/{maKhachHang}/history")
    public List<HoaDonDetailResponse> getCustomerHistory(@PathVariable String maKhachHang) {
        return adminUserService.getCustomerHistory(maKhachHang);
    }

    @GetMapping("/financial-ledger")
    public List<AdminLedgerTransactionResponse> getFinancialLedger() {
        return adminUserService.getFinancialLedger();
    }
}
