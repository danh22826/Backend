package com.example.demo.service;

import com.example.demo.constant.HoaDonStatus;
import com.example.demo.dto.response.AdminLedgerTransactionResponse;
import com.example.demo.dto.response.AdminUserSummaryResponse;
import com.example.demo.dto.response.HoaDonDetailResponse;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NguoiDung;
import com.example.demo.repository.HoaDonRepository;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.NguoiDungRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final NguoiDungRepository nguoiDungRepository;
    private final KhachHangRepository khachHangRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonService hoaDonService;

    public AdminUserService(NguoiDungRepository nguoiDungRepository,
                            KhachHangRepository khachHangRepository,
                            HoaDonRepository hoaDonRepository,
                            HoaDonService hoaDonService) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.khachHangRepository = khachHangRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.hoaDonService = hoaDonService;
    }

    public List<AdminUserSummaryResponse> getAllUsers() {
        return nguoiDungRepository.findAll().stream()
                .sorted(Comparator
                        .comparingInt((NguoiDung user) -> "ROLE_ADMIN".equalsIgnoreCase(user.getRole()) ? 0 : 1)
                        .thenComparing(NguoiDung::getUsername, String.CASE_INSENSITIVE_ORDER))
                .map(this::toSummary)
                .toList();
    }

    public List<HoaDonDetailResponse> getCustomerHistory(String maKhachHang) {
        return hoaDonService.getByCustomer(maKhachHang);
    }

    public List<AdminLedgerTransactionResponse> getFinancialLedger() {
        List<AdminUserSummaryResponse> users = getAllUsers();
        List<AdminLedgerTransactionResponse> results = new ArrayList<>();

        for (AdminUserSummaryResponse user : users) {
            String maKhachHang = user.getMaKhachHang();
            if (maKhachHang == null || maKhachHang.isBlank()) {
                continue;
            }

            List<HoaDonDetailResponse> invoices = hoaDonService.getByCustomer(maKhachHang);
            for (HoaDonDetailResponse invoice : invoices) {
                results.add(toLedgerTransaction(user, invoice));
            }
        }

        results.sort(Comparator.comparing(AdminLedgerTransactionResponse::getEventTime, Comparator.nullsLast(LocalDateTime::compareTo)).reversed());
        return results;
    }

    private AdminUserSummaryResponse toSummary(NguoiDung nguoiDung) {
        AdminUserSummaryResponse response = new AdminUserSummaryResponse();
        response.setId(nguoiDung.getId());
        response.setUsername(nguoiDung.getUsername());
        response.setRole(nguoiDung.getRole());

        Optional<KhachHang> khachHang = findCustomerForUser(nguoiDung.getUsername());
        khachHang.ifPresent(value -> {
            response.setMaKhachHang(value.getMaKhachHang());
            response.setTenKhachHang(value.getTenKhachHang());
            response.setEmail(value.getEmail());
        });

        if (response.getMaKhachHang() == null || response.getMaKhachHang().isBlank()) {
            response.setSoHoaDon(0);
            response.setTongChiTieu(BigDecimal.ZERO);
            response.setLanMuaCuoi(null);
            return response;
        }

        List<HoaDonDetailResponse> invoices = hoaDonService.getByCustomer(response.getMaKhachHang());
        List<HoaDonDetailResponse> paidInvoices = invoices.stream()
                .filter(invoice -> HoaDonStatus.DA_THANH_TOAN.name().equalsIgnoreCase(invoice.getTrangThai()))
                .toList();

        response.setSoHoaDon(invoices.size());
        response.setTongChiTieu(
                paidInvoices.stream()
                        .map(AdminUserService::resolvePaidAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        response.setLanMuaCuoi(
                paidInvoices.stream()
                        .map(HoaDonDetailResponse::getThoiGianThanhToan)
                        .filter(Objects::nonNull)
                        .max(LocalDateTime::compareTo)
                        .orElse(null)
        );
        return response;
    }

    private Optional<KhachHang> findCustomerForUser(String username) {
        return khachHangRepository.findFirstByUsernameIgnoreCase(username)
                .or(() -> khachHangRepository.findFirstByTenKhachHang(username));
    }

    private AdminLedgerTransactionResponse toLedgerTransaction(AdminUserSummaryResponse user, HoaDonDetailResponse invoice) {
        AdminLedgerTransactionResponse response = new AdminLedgerTransactionResponse();
        String rawStatus = normalize(invoice.getTrangThai());
        BigDecimal invoiceAmount = resolveInvoiceAmount(invoice);
        BigDecimal foodAmount = defaultIfNull(invoice.getTongTienDoAn());
        LocalDateTime eventTime = invoice.getThoiGianThanhToan() != null ? invoice.getThoiGianThanhToan() : invoice.getThoiGianDat();
        boolean wasPaidBeforeCancel = "DA_HUY".equals(rawStatus) && invoice.getThoiGianThanhToan() != null;
        BigDecimal cashImpact = "DA_THANH_TOAN".equals(rawStatus)
                ? invoiceAmount
                : wasPaidBeforeCancel ? invoiceAmount.negate() : BigDecimal.ZERO;

        response.setId(defaultIfBlank(invoice.getMaDon(), "---"));
        response.setCustomerName(defaultIfBlank(user.getTenKhachHang(), user.getUsername()));
        response.setCustomerCode(defaultIfBlank(user.getMaKhachHang(), defaultIfBlank(invoice.getMaKhachHang(), "")));
        response.setCustomerUsername(defaultIfBlank(user.getUsername(), "Khach hang"));
        response.setEventTime(eventTime);
        response.setTypeKey(resolveTypeKey(invoice, rawStatus));
        response.setTypeLabel(resolveTypeLabel(response.getTypeKey()));
        response.setDescription(buildDescription(invoice));
        response.setMethodLabel(buildMethodLabel(invoice, rawStatus));
        response.setStatusLabel(resolveStatusLabel(rawStatus));
        response.setRawStatus(rawStatus);
        response.setInvoiceAmount(invoiceAmount);
        response.setCashImpact(cashImpact);
        response.setFoodAmount(foodAmount);
        return response;
    }

    private static BigDecimal resolvePaidAmount(HoaDonDetailResponse invoice) {
        if (invoice.getTongTienThanhToan() != null) {
            return invoice.getTongTienThanhToan();
        }
        if (invoice.getTongTien() != null) {
            return invoice.getTongTien();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal resolveInvoiceAmount(HoaDonDetailResponse invoice) {
        if (invoice.getTongTienThanhToan() != null) {
            return invoice.getTongTienThanhToan();
        }
        if (invoice.getTongTien() != null) {
            return invoice.getTongTien();
        }
        return BigDecimal.ZERO;
    }

    private String resolveTypeKey(HoaDonDetailResponse invoice, String rawStatus) {
        boolean hasTickets = invoice.getDsVe() != null && !invoice.getDsVe().isEmpty();
        boolean hasFoods = invoice.getDsDoAn() != null && !invoice.getDsDoAn().isEmpty();

        if ("CHUA_THANH_TOAN".equals(rawStatus)) {
            return "reservation";
        }
        if (hasTickets && hasFoods) {
            return "combo";
        }
        if (hasTickets) {
            return "ticket";
        }
        if (hasFoods) {
            return "food";
        }
        return "reservation";
    }

    private String resolveTypeLabel(String typeKey) {
        return switch (typeKey) {
            case "combo" -> "Ve + do an";
            case "ticket" -> "Ve xem phim";
            case "food" -> "Do an";
            default -> "Giu cho";
        };
    }

    private String resolveStatusLabel(String rawStatus) {
        return switch (rawStatus) {
            case "DA_THANH_TOAN" -> "Da thanh toan";
            case "DA_HUY" -> "Da huy";
            default -> "Chua thanh toan";
        };
    }

    private String buildDescription(HoaDonDetailResponse invoice) {
        List<String> details = new ArrayList<>();

        if (invoice.getTenPhim() != null && !invoice.getTenPhim().isBlank()) {
            details.add(invoice.getTenPhim());
        }

        String location = List.of(defaultIfBlank(invoice.getTenRap(), null), defaultIfBlank(invoice.getTenPhong(), null)).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" - "));
        if (!location.isBlank()) {
            details.add(location);
        }

        if (invoice.getDsVe() != null && !invoice.getDsVe().isEmpty()) {
            String seatNames = invoice.getDsVe().stream()
                    .map(ticket -> ticket.getTenGhe())
                    .filter(Objects::nonNull)
                    .filter(name -> !name.isBlank())
                    .collect(Collectors.joining(", "));
            if (!seatNames.isBlank()) {
                details.add("Ghe: " + seatNames);
            }
        }

        if (invoice.getDsDoAn() != null && !invoice.getDsDoAn().isEmpty()) {
            String foodNames = invoice.getDsDoAn().stream()
                    .map(food -> {
                        if (food.getTenDoAn() == null || food.getTenDoAn().isBlank()) {
                            return null;
                        }
                        int quantity = food.getSoLuong() == null ? 0 : food.getSoLuong();
                        return quantity > 1 ? food.getTenDoAn() + " x" + quantity : food.getTenDoAn();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
            if (!foodNames.isBlank()) {
                details.add("Do an: " + foodNames);
            }
        }

        return details.isEmpty() ? "Hoa don chua co mo ta chi tiet" : String.join(" - ", details);
    }

    private String buildMethodLabel(HoaDonDetailResponse invoice, String rawStatus) {
        if (invoice.getPhuongThucThanhToan() != null && !invoice.getPhuongThucThanhToan().isBlank()) {
            return invoice.getPhuongThucThanhToan();
        }
        if ("CHUA_THANH_TOAN".equals(rawStatus)) {
            return "Chua thanh toan";
        }
        return "Dang cap nhat";
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}