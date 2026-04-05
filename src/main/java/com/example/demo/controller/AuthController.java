package com.example.demo.controller;

import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PasswordResetToken;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final int RESET_TOKEN_MINUTES = 15;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final NguoiDungRepository nguoiDungRepository;
    private final KhachHangRepository khachHangRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          NguoiDungRepository nguoiDungRepository,
                          KhachHangRepository khachHangRepository,
                          PasswordResetTokenRepository passwordResetTokenRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.nguoiDungRepository = nguoiDungRepository;
        this.khachHangRepository = khachHangRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String username = normalize(loginRequest.get("username"));
        String password = normalize(loginRequest.get("password"));

        if (username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Ten dang nhap va mat khau khong duoc de trong"
            ));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "username", userDetails.getUsername(),
                    "role", role,
                    "maKhachHang", resolveCustomerId(userDetails.getUsername())
            ));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "Sai ten dang nhap hoac mat khau"
            ));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "Khong the dang nhap bang tai khoan nay"
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registerRequest) {
        String username = normalize(registerRequest.get("username"));
        String password = normalize(registerRequest.get("password"));
        String email = normalizeEmail(registerRequest.get("email"));

        if (username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Ten dang nhap va mat khau khong duoc de trong"
            ));
        }

        if (username.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Ten dang nhap phai co it nhat 4 ky tu"
            ));
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Mat khau phai co it nhat 6 ky tu"
            ));
        }

        if (!email.isBlank() && !isValidEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Email khong dung dinh dang"
            ));
        }

        if (nguoiDungRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "Ten dang nhap da ton tai"
            ));
        }

        if (!email.isBlank() && khachHangRepository.findFirstByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "Email da duoc su dung"
            ));
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setUsername(username);
        nguoiDung.setPassword(passwordEncoder.encode(password));
        nguoiDung.setRole("ROLE_USER");
        nguoiDungRepository.save(nguoiDung);

        KhachHang khachHang = findCustomerByUsername(username)
                .orElseGet(() -> {
                    KhachHang customer = new KhachHang();
                    customer.setMaKhachHang(generateCustomerId());
                    customer.setTenKhachHang(username);
                    customer.setUsername(username);
                    return customer;
                });

        khachHang.setUsername(username);
        if (!email.isBlank()) {
            khachHang.setEmail(email);
        }
        khachHangRepository.save(khachHang);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Dang ky thanh cong"
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String identifier = normalize(request.get("identifier"));

        if (identifier.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Vui long nhap ten dang nhap hoac email"
            ));
        }

        Optional<NguoiDung> optionalNguoiDung = findUserByIdentifier(identifier);
        if (optionalNguoiDung.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "Khong tim thay tai khoan phu hop"
            ));
        }

        NguoiDung nguoiDung = optionalNguoiDung.get();
        if (isAdmin(nguoiDung)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", "Tai khoan admin khong duoc doi mat khau bang chuc nang nay"
            ));
        }

        LocalDateTime now = LocalDateTime.now();
        invalidateActiveResetTokens(nguoiDung.getId(), now);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(generateResetTokenValue());
        resetToken.setNguoiDung(nguoiDung);
        resetToken.setCreatedAt(now);
        resetToken.setExpiresAt(now.plusMinutes(RESET_TOKEN_MINUTES));
        passwordResetTokenRepository.save(resetToken);

        return ResponseEntity.ok(Map.of(
                "message", "Xac minh thanh cong. Hay dat lai mat khau moi.",
                "resetToken", resetToken.getToken(),
                "expiresAt", resetToken.getExpiresAt().toString(),
                "username", nguoiDung.getUsername()
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = normalize(request.get("token"));
        String newPassword = normalize(request.get("newPassword"));

        if (token.isBlank() || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Token va mat khau moi khong duoc de trong"
            ));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Mat khau moi phai co it nhat 6 ky tu"
            ));
        }

        Optional<PasswordResetToken> optionalResetToken = passwordResetTokenRepository.findByToken(token);
        if (optionalResetToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "Lien ket dat lai mat khau khong hop le"
            ));
        }

        PasswordResetToken resetToken = optionalResetToken.get();
        LocalDateTime now = LocalDateTime.now();

        if (resetToken.getUsedAt() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "Lien ket dat lai mat khau da duoc su dung"
            ));
        }

        if (resetToken.getExpiresAt() == null || !resetToken.getExpiresAt().isAfter(now)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "Lien ket dat lai mat khau da het han"
            ));
        }

        NguoiDung nguoiDung = resetToken.getNguoiDung();
        if (nguoiDung == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "Khong tim thay tai khoan can dat lai mat khau"
            ));
        }

        if (isAdmin(nguoiDung)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", "Tai khoan admin khong duoc doi mat khau bang chuc nang nay"
            ));
        }

        nguoiDung.setPassword(passwordEncoder.encode(newPassword));
        nguoiDungRepository.save(nguoiDung);

        resetToken.setUsedAt(now);
        passwordResetTokenRepository.save(resetToken);
        invalidateActiveResetTokens(nguoiDung.getId(), now);

        return ResponseEntity.ok(Map.of(
                "message", "Dat lai mat khau thanh cong"
        ));
    }

    private Optional<NguoiDung> findUserByIdentifier(String identifier) {
        Optional<NguoiDung> byUsername = nguoiDungRepository.findByUsernameIgnoreCase(identifier);
        if (byUsername.isPresent()) {
            return byUsername;
        }

        return khachHangRepository.findFirstByEmailIgnoreCase(identifier)
                .map(khachHang -> defaultIfBlank(khachHang.getUsername(), khachHang.getTenKhachHang()))
                .flatMap(nguoiDungRepository::findByUsernameIgnoreCase);
    }

    private void invalidateActiveResetTokens(Long nguoiDungId, LocalDateTime usedAt) {
        if (nguoiDungId == null) {
            return;
        }

        List<PasswordResetToken> activeTokens = passwordResetTokenRepository.findByNguoiDung_IdAndUsedAtIsNull(nguoiDungId);
        if (activeTokens.isEmpty()) {
            return;
        }

        for (PasswordResetToken activeToken : activeTokens) {
            activeToken.setUsedAt(usedAt);
        }
        passwordResetTokenRepository.saveAll(activeTokens);
    }

    private boolean isAdmin(NguoiDung nguoiDung) {
        return nguoiDung != null && "ROLE_ADMIN".equalsIgnoreCase(normalize(nguoiDung.getRole()));
    }

    private String resolveCustomerId(String username) {
        Optional<KhachHang> existingCustomer = findCustomerByUsername(username);
        if (existingCustomer.isPresent()) {
            return existingCustomer.get().getMaKhachHang();
        }

        Optional<NguoiDung> optionalNguoiDung = nguoiDungRepository.findByUsernameIgnoreCase(username);
        if (optionalNguoiDung.isPresent() && isAdmin(optionalNguoiDung.get())) {
            return "";
        }

        KhachHang customer = new KhachHang();
        customer.setMaKhachHang(generateCustomerId());
        customer.setTenKhachHang(username);
        customer.setUsername(username);
        khachHangRepository.save(customer);
        return customer.getMaKhachHang();
    }

    private Optional<KhachHang> findCustomerByUsername(String username) {
        Optional<KhachHang> byUsername = khachHangRepository.findFirstByUsernameIgnoreCase(username);
        if (byUsername.isPresent()) {
            return byUsername;
        }

        Optional<KhachHang> byLegacyName = khachHangRepository.findFirstByTenKhachHang(username);
        if (byLegacyName.isPresent()) {
            KhachHang customer = byLegacyName.get();
            if (customer.getUsername() == null || customer.getUsername().isBlank()) {
                customer.setUsername(username);
                khachHangRepository.save(customer);
            }
            return Optional.of(customer);
        }

        return Optional.empty();
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
    private String generateCustomerId() {
        for (int i = 0; i < 20; i++) {
            String candidate = "KH" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();

            if (!khachHangRepository.existsById(candidate)) {
                return candidate;
            }
        }

        throw new AuthenticationServiceException("Khong the tao ma khach hang moi");
    }

    private String generateResetTokenValue() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.indexOf('@') > 0 && email.indexOf('@') < email.length() - 1;
    }

    private String normalizeEmail(String value) {
        return normalize(value).toLowerCase();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}


