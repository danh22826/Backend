USE [Test];
GO

SET XACT_ABORT ON;
GO

/*
Huong dan chay:
1. Full backup DB truoc khi chay.
2. Tam dung ghi du lieu tu app trong thoi gian migrate.
3. Chay tung STEP, kiem tra ket qua roi moi sang STEP tiep theo.
4. Script nay uu tien an toan: giu bang legacy thay vi xoa cung ngay lap tuc.
*/

/* =========================================================
   STEP 0 - KIEM TRA SO BO
   ========================================================= */

PRINT N'STEP 0 - Bao cao nhanh truoc migrate';
SELECT 'Ghe' AS Bang, COUNT(*) AS SoLuong FROM dbo.Ghe
UNION ALL
SELECT 'SuatChieu', COUNT(*) FROM dbo.SuatChieu
UNION ALL
SELECT 'HoaDon', COUNT(*) FROM dbo.HoaDon
UNION ALL
SELECT 'Ve', COUNT(*) FROM dbo.Ve;
GO

/* =========================================================
   STEP 1 - CHUAN HOA THE LOAI VA BANG TRUNG
   ========================================================= */

PRINT N'STEP 1 - Chuan hoa TheLoai / PhimTheLoai';

IF OBJECT_ID('dbo.the_loai', 'U') IS NOT NULL
BEGIN
    INSERT INTO dbo.TheLoai (MaTheLoai, TenTheLoai)
    SELECT DISTINCT
        CAST(src.ma_the_loai AS varchar(20)),
        CONVERT(nvarchar(100), src.ten_the_loai)
    FROM dbo.the_loai src
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.TheLoai dst
        WHERE dst.MaTheLoai = CAST(src.ma_the_loai AS varchar(20))
    );
END;
GO

IF OBJECT_ID('dbo.phim_the_loai', 'U') IS NOT NULL
BEGIN
    INSERT INTO dbo.PhimTheLoai (MaPhim, MaTheLoai)
    SELECT DISTINCT
        CAST(src.ma_phim AS varchar(20)),
        CAST(src.ma_the_loai AS varchar(20))
    FROM dbo.phim_the_loai src
    WHERE EXISTS (
        SELECT 1 FROM dbo.Phim p
        WHERE p.MaPhim = CAST(src.ma_phim AS varchar(20))
    )
      AND EXISTS (
        SELECT 1 FROM dbo.TheLoai t
        WHERE t.MaTheLoai = CAST(src.ma_the_loai AS varchar(20))
    )
      AND NOT EXISTS (
        SELECT 1
        FROM dbo.PhimTheLoai dst
        WHERE dst.MaPhim = CAST(src.ma_phim AS varchar(20))
          AND dst.MaTheLoai = CAST(src.ma_the_loai AS varchar(20))
    );
END;
GO

IF OBJECT_ID('dbo.the_loai', 'U') IS NOT NULL
   AND OBJECT_ID('dbo.the_loai_legacy', 'U') IS NULL
BEGIN
    EXEC sp_rename 'dbo.the_loai', 'the_loai_legacy';
END;
GO

IF OBJECT_ID('dbo.phim_the_loai', 'U') IS NOT NULL
   AND OBJECT_ID('dbo.phim_the_loai_legacy', 'U') IS NULL
BEGIN
    EXEC sp_rename 'dbo.phim_the_loai', 'phim_the_loai_legacy';
END;
GO

/* =========================================================
   STEP 2 - LAM SACH GHE, PHONG, LOAI GHE, PHIM
   ========================================================= */

PRINT N'STEP 2 - Lam sach Ghe / PhongChieu / LoaiGhe / Phim';

UPDATE dbo.Ghe
SET MaGhe = CONCAT(MaPhong, '-', SoHang, CAST(SoCot AS varchar(10)))
WHERE MaGhe IS NULL
  AND MaPhong IS NOT NULL
  AND SoHang IS NOT NULL
  AND SoCot IS NOT NULL;
GO

IF EXISTS (SELECT 1 FROM dbo.LoaiGhe WHERE MaLoaiGhe = 'LG01')
BEGIN
    UPDATE dbo.Ghe
    SET MaLoaiGhe = 'LG01'
    WHERE MaLoaiGhe IS NULL;
END;
GO

IF EXISTS (
    SELECT 1
    FROM dbo.Ghe
    WHERE MaGhe IS NULL
       OR MaPhong IS NULL
       OR SoHang IS NULL
       OR SoCot IS NULL
       OR MaLoaiGhe IS NULL
)
BEGIN
    THROW 51000, N'BANG Ghe CON GIA TRI NULL. CAN XU LY TAY TRUOC KHI ALTER.', 1;
END;
GO

IF EXISTS (
    SELECT MaGhe
    FROM dbo.Ghe
    GROUP BY MaGhe
    HAVING COUNT(*) > 1
)
BEGIN
    THROW 51001, N'BANG Ghe CO MA GHE TRUNG. CAN XU LY TAY TRUOC KHI THEM PK.', 1;
END;
GO

IF EXISTS (
    SELECT MaPhong, SoHang, SoCot
    FROM dbo.Ghe
    GROUP BY MaPhong, SoHang, SoCot
    HAVING COUNT(*) > 1
)
BEGIN
    THROW 51002, N'BANG Ghe CO VI TRI GHE TRUNG (MaPhong, SoHang, SoCot).', 1;
END;
GO

IF COL_LENGTH('dbo.Ghe', 'TrangThai') IS NULL
BEGIN
    EXEC('ALTER TABLE dbo.Ghe ADD TrangThai varchar(20) NULL;');
END;
GO

IF COL_LENGTH('dbo.Ghe', 'TrangThai') IS NOT NULL
BEGIN
    UPDATE dbo.Ghe SET TrangThai = 'ACTIVE' WHERE TrangThai IS NULL;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.Ghe')
          AND name = 'TrangThai'
          AND is_nullable = 1
    )
    BEGIN
        EXEC('ALTER TABLE dbo.Ghe ALTER COLUMN TrangThai varchar(20) NOT NULL;');
    END;
END;
GO

IF COL_LENGTH('dbo.Ghe', 'MaGhe') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM dbo.Ghe WHERE MaGhe IS NULL)
   AND EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.Ghe')
          AND name = 'MaGhe'
          AND is_nullable = 1
   )
BEGIN
    EXEC('ALTER TABLE dbo.Ghe ALTER COLUMN MaGhe varchar(20) NOT NULL;');
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.key_constraints
    WHERE parent_object_id = OBJECT_ID('dbo.Ghe')
      AND [type] = 'PK'
)
BEGIN
    ALTER TABLE dbo.Ghe
    ADD CONSTRAINT PK_Ghe PRIMARY KEY CLUSTERED (MaGhe);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_Ghe_PhongChieu'
)
   AND EXISTS (
        SELECT 1
        FROM sys.columns c1
        JOIN sys.columns c2
          ON c2.object_id = OBJECT_ID('dbo.PhongChieu')
         AND c2.name = 'MaPhong'
        WHERE c1.object_id = OBJECT_ID('dbo.Ghe')
          AND c1.name = 'MaPhong'
          AND c1.user_type_id = c2.user_type_id
          AND c1.max_length = c2.max_length
   )
BEGIN
    ALTER TABLE dbo.Ghe
    ADD CONSTRAINT FK_Ghe_PhongChieu
        FOREIGN KEY (MaPhong) REFERENCES dbo.PhongChieu(MaPhong);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.Ghe')
      AND name IN ('uk_phong_hang_cot', 'UQ_Ghe_Phong_Hang_Cot')
)
BEGIN
    ALTER TABLE dbo.Ghe
    ADD CONSTRAINT UQ_Ghe_Phong_Hang_Cot UNIQUE (MaPhong, SoHang, SoCot);
END;
GO

UPDATE pc
SET pc.SucChua = seat_count.SoGhe
FROM dbo.PhongChieu pc
JOIN (
    SELECT MaPhong, COUNT(*) AS SoGhe
    FROM dbo.Ghe
    GROUP BY MaPhong
) seat_count
    ON seat_count.MaPhong = pc.MaPhong;
GO

IF COL_LENGTH('dbo.PhongChieu', 'TrangThai') IS NULL
BEGIN
    EXEC('ALTER TABLE dbo.PhongChieu ADD TrangThai varchar(20) NULL;');
END;
GO

IF COL_LENGTH('dbo.PhongChieu', 'TrangThai') IS NOT NULL
BEGIN
    UPDATE dbo.PhongChieu SET TrangThai = 'ACTIVE' WHERE TrangThai IS NULL;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.PhongChieu')
          AND name = 'TrangThai'
          AND is_nullable = 1
    )
    BEGIN
        EXEC('ALTER TABLE dbo.PhongChieu ALTER COLUMN TrangThai varchar(20) NOT NULL;');
    END;
END;
GO

IF COL_LENGTH('dbo.Phim', 'TrangThai') IS NULL
BEGIN
    EXEC('ALTER TABLE dbo.Phim ADD TrangThai varchar(20) NULL;');
END;
GO

IF COL_LENGTH('dbo.Phim', 'TrangThai') IS NOT NULL
BEGIN
    UPDATE dbo.Phim SET TrangThai = 'ACTIVE' WHERE TrangThai IS NULL;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.Phim')
          AND name = 'TrangThai'
          AND is_nullable = 1
    )
    BEGIN
        EXEC('ALTER TABLE dbo.Phim ALTER COLUMN TrangThai varchar(20) NOT NULL;');
    END;
END;
GO

/* =========================================================
   STEP 3 - CHUAN HOA SUAT CHIEU
   ========================================================= */

PRINT N'STEP 3 - Chuan hoa SuatChieu';

IF COL_LENGTH('dbo.SuatChieu', 'MaRap') IS NOT NULL
BEGIN
    UPDATE sc
    SET sc.MaRap = pc.MaRap
    FROM dbo.SuatChieu sc
    JOIN dbo.PhongChieu pc
        ON pc.MaPhong = sc.MaPhong
    WHERE sc.MaRap IS NULL OR sc.MaRap <> pc.MaRap;
END;
GO

IF EXISTS (SELECT 1 FROM dbo.SuatChieu WHERE MaPhong IS NULL)
BEGIN
    THROW 51003, N'BANG SuatChieu CON MaPhong NULL. CAN XU LY TAY TRUOC.', 1;
END;
GO

IF COL_LENGTH('dbo.SuatChieu', 'CreatedAt') IS NULL
BEGIN
    EXEC('ALTER TABLE dbo.SuatChieu ADD CreatedAt datetime2(0) NULL;');
END;
GO

IF COL_LENGTH('dbo.SuatChieu', 'CreatedAt') IS NOT NULL
BEGIN
    UPDATE dbo.SuatChieu
    SET CreatedAt = DATEADD(MINUTE, -5, CAST(NgayChieu AS datetime2(0)))
    WHERE CreatedAt IS NULL;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.SuatChieu')
          AND name = 'CreatedAt'
          AND is_nullable = 1
    )
    BEGIN
        EXEC('ALTER TABLE dbo.SuatChieu ALTER COLUMN CreatedAt datetime2(0) NOT NULL;');
    END;
END;
GO

UPDATE dbo.SuatChieu
SET TrangThai = CASE
    WHEN TrangThai IN (N'DANG_CHIEU', N'DANG_MO_BAN', N'DANG BAN', N'DANG_BAN') THEN 'DANG_MO_BAN'
    WHEN TrangThai IN (N'SAP_CHIEU', N'SAP CHIEU') THEN 'SAP_CHIEU'
    WHEN TrangThai IN (N'NGUNG_BAN', N'NGUNG BAN') THEN 'NGUNG_BAN'
    WHEN TrangThai IN (N'DA_CHIEU', N'DA CHIEU') THEN 'DA_CHIEU'
    WHEN TrangThai IS NULL THEN 'DANG_MO_BAN'
    ELSE TrangThai
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.SuatChieu')
      AND name = 'IX_SuatChieu_Phong_Ngay_Gio'
)
BEGIN
    CREATE INDEX IX_SuatChieu_Phong_Ngay_Gio
        ON dbo.SuatChieu(MaPhong, NgayChieu, GioChieu);
END;
GO

/* =========================================================
   STEP 4 - CHUAN HOA KHACH HANG / NGUOI DUNG
   ========================================================= */

PRINT N'STEP 4 - Chuan hoa KhachHang / NguoiDung';

IF COL_LENGTH('dbo.KhachHang', 'NgayTao') IS NULL
BEGIN
    EXEC('ALTER TABLE dbo.KhachHang ADD NgayTao datetime2(0) NULL;');
END;
GO

IF COL_LENGTH('dbo.KhachHang', 'NgayTao') IS NOT NULL
BEGIN
    UPDATE dbo.KhachHang SET NgayTao = sysdatetime() WHERE NgayTao IS NULL;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.KhachHang')
          AND name = 'NgayTao'
          AND is_nullable = 1
    )
    BEGIN
        EXEC('ALTER TABLE dbo.KhachHang ALTER COLUMN NgayTao datetime2(0) NOT NULL;');
    END;
END;
GO

IF COL_LENGTH('dbo.KhachHang', 'TrangThai') IS NULL
BEGIN
    EXEC('ALTER TABLE dbo.KhachHang ADD TrangThai varchar(20) NULL;');
END;
GO

IF COL_LENGTH('dbo.KhachHang', 'TrangThai') IS NOT NULL
BEGIN
    UPDATE dbo.KhachHang SET TrangThai = 'ACTIVE' WHERE TrangThai IS NULL;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.KhachHang')
          AND name = 'TrangThai'
          AND is_nullable = 1
    )
    BEGIN
        EXEC('ALTER TABLE dbo.KhachHang ALTER COLUMN TrangThai varchar(20) NOT NULL;');
    END;
END;
GO

IF COL_LENGTH('dbo.NguoiDung', 'MaKhachHang') IS NULL
    ALTER TABLE dbo.NguoiDung ADD MaKhachHang varchar(10) NULL;
GO

IF COL_LENGTH('dbo.NguoiDung', 'Email') IS NULL
    ALTER TABLE dbo.NguoiDung ADD Email nvarchar(100) NULL;
GO

IF COL_LENGTH('dbo.NguoiDung', 'TrangThai') IS NULL
BEGIN
    EXEC('ALTER TABLE dbo.NguoiDung ADD TrangThai varchar(20) NULL;');
END;
GO

IF COL_LENGTH('dbo.NguoiDung', 'TrangThai') IS NOT NULL
BEGIN
    UPDATE dbo.NguoiDung SET TrangThai = 'ACTIVE' WHERE TrangThai IS NULL;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.NguoiDung')
          AND name = 'TrangThai'
          AND is_nullable = 1
    )
    BEGIN
        EXEC('ALTER TABLE dbo.NguoiDung ALTER COLUMN TrangThai varchar(20) NOT NULL;');
    END;
END;
GO

IF COL_LENGTH('dbo.NguoiDung', 'NgayTao') IS NULL
BEGIN
    EXEC('ALTER TABLE dbo.NguoiDung ADD NgayTao datetime2(0) NULL;');
END;
GO

IF COL_LENGTH('dbo.NguoiDung', 'NgayTao') IS NOT NULL
BEGIN
    UPDATE dbo.NguoiDung SET NgayTao = sysdatetime() WHERE NgayTao IS NULL;

    IF EXISTS (
        SELECT 1
        FROM sys.columns
        WHERE object_id = OBJECT_ID('dbo.NguoiDung')
          AND name = 'NgayTao'
          AND is_nullable = 1
    )
    BEGIN
        EXEC('ALTER TABLE dbo.NguoiDung ALTER COLUMN NgayTao datetime2(0) NOT NULL;');
    END;
END;
GO

IF COL_LENGTH('dbo.NguoiDung', 'LanDangNhapCuoi') IS NULL
    ALTER TABLE dbo.NguoiDung ADD LanDangNhapCuoi datetime2(0) NULL;
GO

IF OBJECT_ID('dbo.TaiKhoan', 'U') IS NOT NULL
BEGIN
    IF OBJECT_ID('dbo.TaiKhoan_PendingReset', 'U') IS NOT NULL
        DROP TABLE dbo.TaiKhoan_PendingReset;

    SELECT
        MaTaiKhoan,
        MaKhachHang,
        Email,
        VaiTro,
        NgayTao,
        MatKhau
    INTO dbo.TaiKhoan_PendingReset
    FROM dbo.TaiKhoan
    WHERE MatKhau NOT LIKE '$2%';
END;
GO

IF OBJECT_ID('dbo.TaiKhoan', 'U') IS NOT NULL
BEGIN
    UPDATE nd
    SET nd.Email = tk.Email,
        nd.MaKhachHang = tk.MaKhachHang,
        nd.TrangThai = CASE WHEN nd.TrangThai IS NULL THEN 'ACTIVE' ELSE nd.TrangThai END
    FROM dbo.NguoiDung nd
    JOIN dbo.TaiKhoan tk
        ON tk.Email = nd.username;
END;
GO

IF OBJECT_ID('dbo.TaiKhoan', 'U') IS NOT NULL
BEGIN
    INSERT INTO dbo.NguoiDung (username, [password], [role], MaKhachHang, Email, TrangThai, NgayTao)
    SELECT
        tk.Email,
        tk.MatKhau,
        CASE
            WHEN tk.VaiTro IN (N'ADMIN', N'ROLE_ADMIN') THEN 'ROLE_ADMIN'
            ELSE 'ROLE_USER'
        END,
        tk.MaKhachHang,
        tk.Email,
        'ACTIVE',
        COALESCE(tk.NgayTao, sysdatetime())
    FROM dbo.TaiKhoan tk
    WHERE tk.MatKhau LIKE '$2%'
      AND NOT EXISTS (
        SELECT 1
        FROM dbo.NguoiDung nd
        WHERE nd.username = tk.Email
    );
END;
GO

IF OBJECT_ID('dbo.TaiKhoan', 'U') IS NOT NULL
BEGIN
    INSERT INTO dbo.NguoiDung (username, [password], [role], MaKhachHang, Email, TrangThai, NgayTao)
    SELECT
        tk.Email,
        '$2a$10$7wI9N0T9Vvx5A8S3Uj3Y7unVdW5n5C9P6r6a0wSxv2eH5YfH4Qm7e',
        CASE
            WHEN tk.VaiTro IN (N'ADMIN', N'ROLE_ADMIN') THEN 'ROLE_ADMIN'
            ELSE 'ROLE_USER'
        END,
        tk.MaKhachHang,
        tk.Email,
        'PENDING_RESET',
        COALESCE(tk.NgayTao, sysdatetime())
    FROM dbo.TaiKhoan tk
    WHERE tk.MatKhau NOT LIKE '$2%'
      AND NOT EXISTS (
        SELECT 1
        FROM dbo.NguoiDung nd
        WHERE nd.username = tk.Email
    );
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_NguoiDung_KhachHang'
)
   AND EXISTS (
        SELECT 1
        FROM sys.columns c1
        JOIN sys.columns c2
          ON c2.object_id = OBJECT_ID('dbo.KhachHang')
         AND c2.name = 'MaKhachHang'
        WHERE c1.object_id = OBJECT_ID('dbo.NguoiDung')
          AND c1.name = 'MaKhachHang'
          AND c1.user_type_id = c2.user_type_id
          AND c1.max_length = c2.max_length
   )
BEGIN
    ALTER TABLE dbo.NguoiDung
    ADD CONSTRAINT FK_NguoiDung_KhachHang
        FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.NguoiDung')
      AND name = 'UX_NguoiDung_Email'
)
BEGIN
    CREATE UNIQUE INDEX UX_NguoiDung_Email
        ON dbo.NguoiDung(Email)
        WHERE Email IS NOT NULL;
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.NguoiDung')
      AND name = 'UX_NguoiDung_MaKhachHang'
)
BEGIN
    CREATE UNIQUE INDEX UX_NguoiDung_MaKhachHang
        ON dbo.NguoiDung(MaKhachHang)
        WHERE MaKhachHang IS NOT NULL;
END;
GO

/* =========================================================
   STEP 5 - CHUAN HOA HOA DON
   ========================================================= */

PRINT N'STEP 5 - Chuan hoa HoaDon';

UPDATE dbo.HoaDon
SET TrangThai = CASE
    WHEN TrangThai IN (N'Chờ thanh toán', N'CHO_THANH_TOAN', N'CHUA_THANH_TOAN') THEN 'CHUA_THANH_TOAN'
    WHEN TrangThai IN (N'Đã thanh toán', N'DA_THANH_TOAN') THEN 'DA_THANH_TOAN'
    WHEN TrangThai IN (N'Đã hủy', N'DA_HUY') THEN 'DA_HUY'
    ELSE TrangThai
END;
GO

UPDATE dbo.HoaDon
SET PhuongThucThanhToan = CASE
    WHEN PhuongThucThanhToan IN (N'Tiền mặt', N'TIEN_MAT') THEN 'TIEN_MAT'
    WHEN PhuongThucThanhToan IN (N'MOMO', N'MoMo') THEN 'MOMO'
    WHEN PhuongThucThanhToan IN (N'VNPAY', N'VN_PAY') THEN 'VNPAY'
    WHEN PhuongThucThanhToan IN (N'ZALOPAY', N'ZALO_PAY') THEN 'ZALOPAY'
    ELSE PhuongThucThanhToan
END;
GO

UPDATE dbo.HoaDon
SET TrangThai = CASE
    WHEN TrangThai IN (N'Chờ thanh toán', N'CHO_THANH_TOAN', N'CHUA_THANH_TOAN') THEN 'CHUA_THANH_TOAN'
    WHEN TrangThai IN (N'Đã thanh toán', N'DA_THANH_TOAN') THEN 'DA_THANH_TOAN'
    WHEN TrangThai IN (N'Đã hủy', N'DA_HUY') THEN 'DA_HUY'
    ELSE TrangThai
END;
GO

UPDATE dbo.HoaDon
SET PhuongThucThanhToan = CASE
    WHEN PhuongThucThanhToan IN (N'Tiền mặt', N'TIEN_MAT') THEN 'TIEN_MAT'
    WHEN PhuongThucThanhToan IN (N'MOMO', N'MoMo') THEN 'MOMO'
    WHEN PhuongThucThanhToan IN (N'VNPAY', N'VN_PAY') THEN 'VNPAY'
    WHEN PhuongThucThanhToan IN (N'ZALOPAY', N'ZALO_PAY') THEN 'ZALOPAY'
    ELSE PhuongThucThanhToan
END;
GO

UPDATE dbo.HoaDon
SET TrangThai = CASE
    WHEN TrangThai COLLATE Latin1_General_CI_AI = 'Cho thanh toan' THEN 'CHUA_THANH_TOAN'
    WHEN TrangThai COLLATE Latin1_General_CI_AI = 'Da thanh toan' THEN 'DA_THANH_TOAN'
    WHEN TrangThai COLLATE Latin1_General_CI_AI = 'Da huy' THEN 'DA_HUY'
    ELSE TrangThai
END;
GO

UPDATE dbo.HoaDon
SET PhuongThucThanhToan = CASE
    WHEN PhuongThucThanhToan COLLATE Latin1_General_CI_AI = 'Tien mat' THEN 'TIEN_MAT'
    ELSE PhuongThucThanhToan
END;
GO

IF COL_LENGTH('dbo.HoaDon', 'ThoiGianHetHan') IS NULL
BEGIN
    ALTER TABLE dbo.HoaDon ADD ThoiGianHetHan datetime2(0) NULL;
END;
GO

IF COL_LENGTH('dbo.HoaDon', 'GhiChu') IS NULL
BEGIN
    ALTER TABLE dbo.HoaDon ADD GhiChu nvarchar(255) NULL;
END;
GO

UPDATE dbo.HoaDon
SET ThoiGianHetHan = DATEADD(MINUTE, 10, CAST(ThoiGianDat AS datetime2(0)))
WHERE ThoiGianHetHan IS NULL
  AND TrangThai = 'CHUA_THANH_TOAN';
GO

/* =========================================================
   STEP 6 - BUILD LAI Ve THEO MO HINH MOI
   ========================================================= */

PRINT N'STEP 6 - Rebuild bang Ve';

IF OBJECT_ID('dbo.Ve_MigrateSkipped', 'U') IS NOT NULL
    DROP TABLE dbo.Ve_MigrateSkipped;
GO

SELECT *
INTO dbo.Ve_MigrateSkipped
FROM dbo.Ve
WHERE ISNULL(UPPER(TrangThaiVe), '') <> 'TRONG'
  AND (
        MaDon IS NULL
        OR MaGhe IS NULL
        OR MaSuat IS NULL
      );
GO

IF OBJECT_ID('dbo.Ve_Moi', 'U') IS NOT NULL
BEGIN
    THROW 51004, N'Bang Ve_Moi da ton tai. Kiem tra lai truoc khi chay lai STEP 6.', 1;
END;
GO

CREATE TABLE dbo.Ve_Moi (
    MaVe varchar(20) NOT NULL,
    MaGhe varchar(20) NOT NULL,
    MaDon varchar(20) NOT NULL,
    MaSuat varchar(20) NOT NULL,
    TrangThaiVe varchar(20) NOT NULL,
    GiaVeGoc decimal(12, 2) NOT NULL,
    GiaPhuThu decimal(12, 2) NOT NULL,
    ThanhTien decimal(12, 2) NOT NULL,
    ThoiGianGiuCho datetime2(0) NOT NULL,
    ThoiGianThanhToan datetime2(0) NULL
);
GO

INSERT INTO dbo.Ve_Moi (
    MaVe,
    MaGhe,
    MaDon,
    MaSuat,
    TrangThaiVe,
    GiaVeGoc,
    GiaPhuThu,
    ThanhTien,
    ThoiGianGiuCho,
    ThoiGianThanhToan
)
SELECT
    v.MaVe,
    v.MaGhe,
    v.MaDon,
    v.MaSuat,
    CASE
        WHEN v.TrangThaiVe = 'DA_HUY' OR hd.TrangThai = 'DA_HUY' THEN 'DA_HUY'
        WHEN v.TrangThaiVe = 'DA_THANH_TOAN' OR hd.TrangThai = 'DA_THANH_TOAN' THEN 'DA_THANH_TOAN'
        ELSE 'DA_DAT'
    END,
    CAST(sc.Gia AS decimal(12, 2)),
    CAST(ISNULL(lg.GiaPhuThu, 0) AS decimal(12, 2)),
    CAST(sc.Gia + ISNULL(lg.GiaPhuThu, 0) AS decimal(12, 2)),
    CAST(COALESCE(v.NgayDat, hd.ThoiGianDat, sysdatetime()) AS datetime2(0)),
    CASE
        WHEN hd.ThoiGianThanhToan IS NOT NULL THEN CAST(hd.ThoiGianThanhToan AS datetime2(0))
        ELSE NULL
    END
FROM dbo.Ve v
JOIN dbo.SuatChieu sc
    ON sc.MaSuat = v.MaSuat
JOIN dbo.Ghe g
    ON g.MaGhe = v.MaGhe
JOIN dbo.LoaiGhe lg
    ON lg.MaLoaiGhe = g.MaLoaiGhe
JOIN dbo.HoaDon hd
    ON hd.MaDon = v.MaDon
WHERE ISNULL(UPPER(v.TrangThaiVe), '') <> 'TRONG'
  AND v.MaDon IS NOT NULL
  AND v.MaGhe IS NOT NULL
  AND v.MaSuat IS NOT NULL;
GO

IF EXISTS (
    SELECT MaSuat, MaGhe
    FROM dbo.Ve_Moi
    GROUP BY MaSuat, MaGhe
    HAVING COUNT(*) > 1
)
BEGIN
    THROW 51005, N'Ve_Moi sinh ra ban ghi trung (MaSuat, MaGhe). Dung migrate va xu ly du lieu.', 1;
END;
GO

IF OBJECT_ID('dbo.Ve_Legacy', 'U') IS NULL
BEGIN
    EXEC sp_rename 'dbo.Ve', 'Ve_Legacy';
END;
GO

IF OBJECT_ID('dbo.Ve', 'U') IS NULL AND OBJECT_ID('dbo.Ve_Moi', 'U') IS NOT NULL
BEGIN
    EXEC sp_rename 'dbo.Ve_Moi', 'Ve';
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.key_constraints
    WHERE parent_object_id = OBJECT_ID('dbo.Ve')
      AND [type] = 'PK'
)
BEGIN
    ALTER TABLE dbo.Ve
    ADD CONSTRAINT PK_Ve PRIMARY KEY CLUSTERED (MaVe);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_Ve_Model_Ghe'
)
   AND EXISTS (
        SELECT 1
        FROM sys.columns c1
        JOIN sys.columns c2
          ON c2.object_id = OBJECT_ID('dbo.Ghe')
         AND c2.name = 'MaGhe'
        WHERE c1.object_id = OBJECT_ID('dbo.Ve')
          AND c1.name = 'MaGhe'
          AND c1.user_type_id = c2.user_type_id
          AND c1.max_length = c2.max_length
   )
BEGIN
    ALTER TABLE dbo.Ve
    ADD CONSTRAINT FK_Ve_Model_Ghe
        FOREIGN KEY (MaGhe) REFERENCES dbo.Ghe(MaGhe);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_Ve_Model_HoaDon'
)
   AND EXISTS (
        SELECT 1
        FROM sys.columns c1
        JOIN sys.columns c2
          ON c2.object_id = OBJECT_ID('dbo.HoaDon')
         AND c2.name = 'MaDon'
        WHERE c1.object_id = OBJECT_ID('dbo.Ve')
          AND c1.name = 'MaDon'
          AND c1.user_type_id = c2.user_type_id
          AND c1.max_length = c2.max_length
   )
BEGIN
    ALTER TABLE dbo.Ve
    ADD CONSTRAINT FK_Ve_Model_HoaDon
        FOREIGN KEY (MaDon) REFERENCES dbo.HoaDon(MaDon);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_Ve_Model_SuatChieu'
)
   AND EXISTS (
        SELECT 1
        FROM sys.columns c1
        JOIN sys.columns c2
          ON c2.object_id = OBJECT_ID('dbo.SuatChieu')
         AND c2.name = 'MaSuat'
        WHERE c1.object_id = OBJECT_ID('dbo.Ve')
          AND c1.name = 'MaSuat'
          AND c1.user_type_id = c2.user_type_id
          AND c1.max_length = c2.max_length
   )
BEGIN
    ALTER TABLE dbo.Ve
    ADD CONSTRAINT FK_Ve_Model_SuatChieu
        FOREIGN KEY (MaSuat) REFERENCES dbo.SuatChieu(MaSuat);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.Ve')
      AND name = 'UQ_Ve_Model_Suat_Ghe'
)
BEGIN
    ALTER TABLE dbo.Ve
    ADD CONSTRAINT UQ_Ve_Model_Suat_Ghe UNIQUE (MaSuat, MaGhe);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.Ve')
      AND name = 'IX_Ve_MaDon'
)
BEGIN
    CREATE INDEX IX_Ve_MaDon ON dbo.Ve(MaDon);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.Ve')
      AND name = 'IX_Ve_MaSuat_TrangThaiVe'
)
BEGIN
    CREATE INDEX IX_Ve_MaSuat_TrangThaiVe ON dbo.Ve(MaSuat, TrangThaiVe);
END;
GO

/* =========================================================
   STEP 7 - VIEW HO TRO THAY CHO TU DUY "Ve = TRONG"
   ========================================================= */

PRINT N'STEP 7 - Tao view ghe trong theo suat';

CREATE OR ALTER VIEW dbo.vw_GheTrongTheoSuat
AS
SELECT
    sc.MaSuat,
    g.MaGhe,
    g.MaPhong,
    g.SoHang,
    g.SoCot,
    g.MaLoaiGhe
FROM dbo.SuatChieu sc
JOIN dbo.Ghe g
    ON g.MaPhong = sc.MaPhong
LEFT JOIN dbo.Ve v
    ON v.MaSuat = sc.MaSuat
   AND v.MaGhe = g.MaGhe
   AND v.TrangThaiVe IN ('DA_DAT', 'DA_THANH_TOAN')
LEFT JOIN dbo.HoaDon hd
    ON hd.MaDon = v.MaDon
WHERE v.MaVe IS NULL
   OR v.TrangThaiVe = 'DA_HUY'
   OR hd.TrangThai IN ('DA_HUY', 'HET_HAN');
GO

/* =========================================================
   STEP 8 - CLEANUP CUOI CUNG (CHAY SAU KHI APP DA DOI SANG MODEL MOI)
   ========================================================= */

/*
-- 8.1 - Chi drop SuatChieu.MaRap sau khi chac chan app/report khong dung nua
IF COL_LENGTH('dbo.SuatChieu', 'MaRap') IS NOT NULL
BEGIN
    ALTER TABLE dbo.SuatChieu DROP CONSTRAINT FK_SuatChieu_Rap;
    ALTER TABLE dbo.SuatChieu DROP COLUMN MaRap;
END;
GO

-- 8.2 - Doi ten TaiKhoan thanh legacy, sau do bo han khi da reset het password
IF OBJECT_ID('dbo.TaiKhoan', 'U') IS NOT NULL
   AND OBJECT_ID('dbo.TaiKhoan_Legacy', 'U') IS NULL
BEGIN
    EXEC sp_rename 'dbo.TaiKhoan', 'TaiKhoan_Legacy';
END;
GO

-- 8.3 - Sau 2-4 tuan on dinh moi xoa bang legacy
-- DROP TABLE dbo.Ve_Legacy;
-- DROP TABLE dbo.TaiKhoan_Legacy;
-- DROP TABLE dbo.the_loai_legacy;
-- DROP TABLE dbo.phim_the_loai_legacy;
*/
