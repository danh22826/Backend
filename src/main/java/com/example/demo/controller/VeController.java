//package com.example.demo.controller;
//
//import com.example.demo.constant.VeStatus;
//import com.example.demo.entity.Ve;
//import com.example.demo.service.VeService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/ve")
//public class VeController {
//
//    private final VeService veService;
//
//    public VeController(VeService veService) {
//        this.veService = veService;
//    }
//
//    /**
//     * Lấy danh sách ghế còn trống của một suất chiếu
//     */
//    @GetMapping("/ghe-trong/{maSuat}")
//    public ResponseEntity<List<Ve>> getGheTrong(@PathVariable String maSuat) {
//        List<Ve> gheTrong = veService.getGheTrong(maSuat);
//        return ResponseEntity.ok(gheTrong);
//    }
//
//    /**
//     * Đặt vé
//     */
//    @PostMapping("/dat-ve")
//    public ResponseEntity<Ve> datVe(@RequestBody Ve ve) {
//        Ve veDat = veService.datVe(ve);
//        return ResponseEntity.ok(veDat);
//    }
//
//    /**
//     * Thanh toán vé
//     */
//    @PostMapping("/thanh-toan/{maVe}")
//    public ResponseEntity<Ve> thanhToanVe(@PathVariable String maVe) {
//        Ve ve = veService.thanhToanVe(maVe);
//        return ResponseEntity.ok(ve);
//    }
//
//    /**
//     * Hủy vé
//     */
//    @PostMapping("/huy-ve/{maVe}")
//    public ResponseEntity<Ve> huyVe(@PathVariable String maVe) {
//        Ve ve = veService.huyVe(maVe);
//        return ResponseEntity.ok(ve);
//    }
//}