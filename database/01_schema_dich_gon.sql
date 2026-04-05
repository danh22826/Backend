USE [QuanLyRapPhim];
GO

/*
Schema dich gon cho he thong QuanLyRapPhim.

Muc tieu:
1. Bo bang trung nghia: TaiKhoan, the_loai, phim_the_loai.
2. Giu NguoiDung lam bang dang nhap duy nhat de it anh huong code Spring hien tai.
3. Ve chi la "ghe da giu/da mua" trong 1 suat chieu, khong luu san TRONG.
4. SuatChieu bo MaRap vi co the suy ra tu MaPhong -> PhongChieu -> Rap.
*/

CREATE TABLE dbo.ThanhPho (
    MaThanhPho varchar(20) NOT NULL,
    TenThanhPho nvarchar(100) NOT NULL,
    CONSTRAINT PK_ThanhPho PRIMARY KEY CLUSTERED (MaThanhPho),
    CONSTRAINT UQ_ThanhPho_Ten UNIQUE (TenThanhPho)
);
GO

CREATE TABLE dbo.Rap (
    MaRap varchar(20) NOT NULL,
    TenRap nvarchar(100) NOT NULL,
    DiaChi nvarchar(255) NULL,
    MaThanhPho varchar(20) NOT NULL,
    TrangThai varchar(20) NOT NULL CONSTRAINT DF_Rap_TrangThai DEFAULT ('ACTIVE'),
    CONSTRAINT PK_Rap PRIMARY KEY CLUSTERED (MaRap),
    CONSTRAINT FK_Rap_ThanhPho FOREIGN KEY (MaThanhPho) REFERENCES dbo.ThanhPho(MaThanhPho),
    CONSTRAINT CK_Rap_TrangThai CHECK (TrangThai IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT UQ_Rap_ThanhPho_Ten UNIQUE (MaThanhPho, TenRap)
);
GO

CREATE TABLE dbo.LoaiPhong (
    MaLoaiPhong varchar(20) NOT NULL,
    TenLoaiPhong nvarchar(100) NOT NULL,
    CONSTRAINT PK_LoaiPhong PRIMARY KEY CLUSTERED (MaLoaiPhong),
    CONSTRAINT UQ_LoaiPhong_Ten UNIQUE (TenLoaiPhong)
);
GO

CREATE TABLE dbo.PhongChieu (
    MaPhong varchar(20) NOT NULL,
    MaRap varchar(20) NOT NULL,
    TenPhong nvarchar(100) NOT NULL,
    SucChua int NOT NULL,
    MaLoaiPhong varchar(20) NOT NULL,
    TrangThai varchar(20) NOT NULL CONSTRAINT DF_PhongChieu_TrangThai DEFAULT ('ACTIVE'),
    CONSTRAINT PK_PhongChieu PRIMARY KEY CLUSTERED (MaPhong),
    CONSTRAINT FK_PhongChieu_Rap FOREIGN KEY (MaRap) REFERENCES dbo.Rap(MaRap),
    CONSTRAINT FK_PhongChieu_LoaiPhong FOREIGN KEY (MaLoaiPhong) REFERENCES dbo.LoaiPhong(MaLoaiPhong),
    CONSTRAINT CK_PhongChieu_SucChua CHECK (SucChua > 0),
    CONSTRAINT CK_PhongChieu_TrangThai CHECK (TrangThai IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT UQ_PhongChieu_Rap_TenPhong UNIQUE (MaRap, TenPhong)
);
GO

CREATE TABLE dbo.LoaiGhe (
    MaLoaiGhe varchar(20) NOT NULL,
    TenLoaiGhe nvarchar(100) NOT NULL,
    GiaPhuThu decimal(12, 2) NOT NULL CONSTRAINT DF_LoaiGhe_GiaPhuThu DEFAULT ((0)),
    CONSTRAINT PK_LoaiGhe PRIMARY KEY CLUSTERED (MaLoaiGhe),
    CONSTRAINT UQ_LoaiGhe_Ten UNIQUE (TenLoaiGhe),
    CONSTRAINT CK_LoaiGhe_GiaPhuThu CHECK (GiaPhuThu >= 0)
);
GO

CREATE TABLE dbo.Ghe (
    MaGhe varchar(20) NOT NULL,
    MaPhong varchar(20) NOT NULL,
    SoHang varchar(10) NOT NULL,
    SoCot int NOT NULL,
    MaLoaiGhe varchar(20) NOT NULL,
    TrangThai varchar(20) NOT NULL CONSTRAINT DF_Ghe_TrangThai DEFAULT ('ACTIVE'),
    CONSTRAINT PK_Ghe PRIMARY KEY CLUSTERED (MaGhe),
    CONSTRAINT FK_Ghe_PhongChieu FOREIGN KEY (MaPhong) REFERENCES dbo.PhongChieu(MaPhong),
    CONSTRAINT FK_Ghe_LoaiGhe FOREIGN KEY (MaLoaiGhe) REFERENCES dbo.LoaiGhe(MaLoaiGhe),
    CONSTRAINT CK_Ghe_SoCot CHECK (SoCot > 0),
    CONSTRAINT CK_Ghe_TrangThai CHECK (TrangThai IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT UQ_Ghe_Phong_Hang_Cot UNIQUE (MaPhong, SoHang, SoCot)
);
GO

CREATE TABLE dbo.TheLoai (
    MaTheLoai varchar(20) NOT NULL,
    TenTheLoai nvarchar(100) NOT NULL,
    CONSTRAINT PK_TheLoai PRIMARY KEY CLUSTERED (MaTheLoai),
    CONSTRAINT UQ_TheLoai_Ten UNIQUE (TenTheLoai)
);
GO

CREATE TABLE dbo.Phim (
    MaPhim varchar(20) NOT NULL,
    TenPhim nvarchar(255) NOT NULL,
    MoTa nvarchar(max) NULL,
    Poster nvarchar(1000) NULL,
    ThoiLuong int NOT NULL,
    NgayKhoiChieu date NOT NULL,
    DoTuoiPhuHop nvarchar(20) NULL,
    NgonNgu nvarchar(50) NULL,
    TrailerUrl nvarchar(500) NULL,
    TrangThai varchar(20) NOT NULL CONSTRAINT DF_Phim_TrangThai DEFAULT ('ACTIVE'),
    CONSTRAINT PK_Phim PRIMARY KEY CLUSTERED (MaPhim),
    CONSTRAINT CK_Phim_ThoiLuong CHECK (ThoiLuong > 0),
    CONSTRAINT CK_Phim_TrangThai CHECK (TrangThai IN ('ACTIVE', 'INACTIVE'))
);
GO

CREATE TABLE dbo.PhimTheLoai (
    MaPhim varchar(20) NOT NULL,
    MaTheLoai varchar(20) NOT NULL,
    CONSTRAINT PK_PhimTheLoai PRIMARY KEY CLUSTERED (MaPhim, MaTheLoai),
    CONSTRAINT FK_PhimTheLoai_Phim FOREIGN KEY (MaPhim) REFERENCES dbo.Phim(MaPhim),
    CONSTRAINT FK_PhimTheLoai_TheLoai FOREIGN KEY (MaTheLoai) REFERENCES dbo.TheLoai(MaTheLoai)
);
GO

CREATE TABLE dbo.KhachHang (
    MaKhachHang varchar(20) NOT NULL,
    TenKhachHang nvarchar(100) NOT NULL,
    SoDienThoai varchar(15) NULL,
    GioiTinh nvarchar(10) NULL,
    Email nvarchar(100) NULL,
    NgayTao datetime2(0) NOT NULL CONSTRAINT DF_KhachHang_NgayTao DEFAULT (sysdatetime()),
    TrangThai varchar(20) NOT NULL CONSTRAINT DF_KhachHang_TrangThai DEFAULT ('ACTIVE'),
    CONSTRAINT PK_KhachHang PRIMARY KEY CLUSTERED (MaKhachHang),
    CONSTRAINT CK_KhachHang_TrangThai CHECK (TrangThai IN ('ACTIVE', 'INACTIVE'))
);
GO

CREATE UNIQUE INDEX UX_KhachHang_Email
    ON dbo.KhachHang(Email)
    WHERE Email IS NOT NULL;
GO

CREATE UNIQUE INDEX UX_KhachHang_SoDienThoai
    ON dbo.KhachHang(SoDienThoai)
    WHERE SoDienThoai IS NOT NULL;
GO

CREATE TABLE dbo.NguoiDung (
    id bigint IDENTITY(1,1) NOT NULL,
    username varchar(255) NOT NULL,
    [password] varchar(255) NOT NULL,
    [role] varchar(50) NOT NULL,
    MaKhachHang varchar(20) NULL,
    Email nvarchar(100) NULL,
    TrangThai varchar(20) NOT NULL CONSTRAINT DF_NguoiDung_TrangThai DEFAULT ('ACTIVE'),
    NgayTao datetime2(0) NOT NULL CONSTRAINT DF_NguoiDung_NgayTao DEFAULT (sysdatetime()),
    LanDangNhapCuoi datetime2(0) NULL,
    CONSTRAINT PK_NguoiDung PRIMARY KEY CLUSTERED (id),
    CONSTRAINT FK_NguoiDung_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang),
    CONSTRAINT CK_NguoiDung_Role CHECK ([role] IN ('ROLE_ADMIN', 'ROLE_USER')),
    CONSTRAINT CK_NguoiDung_TrangThai CHECK (TrangThai IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'PENDING_RESET')),
    CONSTRAINT UQ_NguoiDung_Username UNIQUE (username)
);
GO

CREATE UNIQUE INDEX UX_NguoiDung_Email
    ON dbo.NguoiDung(Email)
    WHERE Email IS NOT NULL;
GO

CREATE UNIQUE INDEX UX_NguoiDung_MaKhachHang
    ON dbo.NguoiDung(MaKhachHang)
    WHERE MaKhachHang IS NOT NULL;
GO

CREATE TABLE dbo.SuatChieu (
    MaSuat varchar(20) NOT NULL,
    MaPhim varchar(20) NOT NULL,
    NgayChieu date NOT NULL,
    GioChieu time(0) NOT NULL,
    TrangThai varchar(20) NOT NULL CONSTRAINT DF_SuatChieu_TrangThai DEFAULT ('DANG_MO_BAN'),
    Gia decimal(12, 2) NOT NULL CONSTRAINT DF_SuatChieu_Gia DEFAULT ((75000)),
    MaPhong varchar(20) NOT NULL,
    CreatedAt datetime2(0) NOT NULL CONSTRAINT DF_SuatChieu_CreatedAt DEFAULT (sysdatetime()),
    CONSTRAINT PK_SuatChieu PRIMARY KEY CLUSTERED (MaSuat),
    CONSTRAINT FK_SuatChieu_Phim FOREIGN KEY (MaPhim) REFERENCES dbo.Phim(MaPhim),
    CONSTRAINT FK_SuatChieu_PhongChieu FOREIGN KEY (MaPhong) REFERENCES dbo.PhongChieu(MaPhong),
    CONSTRAINT CK_SuatChieu_TrangThai CHECK (TrangThai IN ('SAP_CHIEU', 'DANG_MO_BAN', 'NGUNG_BAN', 'DA_CHIEU')),
    CONSTRAINT CK_SuatChieu_Gia CHECK (Gia >= 0)
);
GO

CREATE INDEX IX_SuatChieu_Phong_Ngay_Gio
    ON dbo.SuatChieu(MaPhong, NgayChieu, GioChieu);
GO

CREATE INDEX IX_SuatChieu_Phim_Ngay
    ON dbo.SuatChieu(MaPhim, NgayChieu);
GO

CREATE TABLE dbo.HoaDon (
    MaDon varchar(20) NOT NULL,
    MaKhachHang varchar(20) NOT NULL,
    TongTien decimal(12, 2) NOT NULL CONSTRAINT DF_HoaDon_TongTien DEFAULT ((0)),
    ThoiGianDat datetime2(0) NOT NULL CONSTRAINT DF_HoaDon_ThoiGianDat DEFAULT (sysdatetime()),
    ThoiGianHetHan datetime2(0) NULL,
    ThoiGianThanhToan datetime2(0) NULL,
    TrangThai varchar(20) NOT NULL CONSTRAINT DF_HoaDon_TrangThai DEFAULT ('CHUA_THANH_TOAN'),
    PhuongThucThanhToan varchar(30) NULL,
    GhiChu nvarchar(255) NULL,
    CONSTRAINT PK_HoaDon PRIMARY KEY CLUSTERED (MaDon),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang),
    CONSTRAINT CK_HoaDon_TongTien CHECK (TongTien >= 0),
    CONSTRAINT CK_HoaDon_TrangThai CHECK (TrangThai IN ('CHUA_THANH_TOAN', 'DA_THANH_TOAN', 'DA_HUY', 'HET_HAN')),
    CONSTRAINT CK_HoaDon_PhuongThuc CHECK (PhuongThucThanhToan IS NULL OR PhuongThucThanhToan IN ('TIEN_MAT', 'MOMO', 'VNPAY', 'ZALOPAY', 'THE_NGAN_HANG'))
);
GO

CREATE INDEX IX_HoaDon_KhachHang_ThoiGianDat
    ON dbo.HoaDon(MaKhachHang, ThoiGianDat DESC);
GO

CREATE TABLE dbo.Ve (
    MaVe varchar(20) NOT NULL,
    MaGhe varchar(20) NOT NULL,
    MaDon varchar(20) NOT NULL,
    MaSuat varchar(20) NOT NULL,
    TrangThaiVe varchar(20) NOT NULL CONSTRAINT DF_Ve_TrangThaiVe DEFAULT ('DA_DAT'),
    GiaVeGoc decimal(12, 2) NOT NULL,
    GiaPhuThu decimal(12, 2) NOT NULL CONSTRAINT DF_Ve_GiaPhuThu DEFAULT ((0)),
    ThanhTien decimal(12, 2) NOT NULL,
    ThoiGianGiuCho datetime2(0) NOT NULL CONSTRAINT DF_Ve_ThoiGianGiuCho DEFAULT (sysdatetime()),
    ThoiGianThanhToan datetime2(0) NULL,
    CONSTRAINT PK_Ve PRIMARY KEY CLUSTERED (MaVe),
    CONSTRAINT FK_Ve_Ghe FOREIGN KEY (MaGhe) REFERENCES dbo.Ghe(MaGhe),
    CONSTRAINT FK_Ve_HoaDon FOREIGN KEY (MaDon) REFERENCES dbo.HoaDon(MaDon),
    CONSTRAINT FK_Ve_SuatChieu FOREIGN KEY (MaSuat) REFERENCES dbo.SuatChieu(MaSuat),
    CONSTRAINT CK_Ve_TrangThai CHECK (TrangThaiVe IN ('DA_DAT', 'DA_THANH_TOAN', 'DA_HUY')),
    CONSTRAINT CK_Ve_GiaVeGoc CHECK (GiaVeGoc >= 0),
    CONSTRAINT CK_Ve_GiaPhuThu CHECK (GiaPhuThu >= 0),
    CONSTRAINT CK_Ve_ThanhTien CHECK (ThanhTien = GiaVeGoc + GiaPhuThu),
    CONSTRAINT UQ_Ve_Suat_Ghe UNIQUE (MaSuat, MaGhe)
);
GO

CREATE INDEX IX_Ve_MaDon
    ON dbo.Ve(MaDon);
GO

CREATE INDEX IX_Ve_MaSuat_TrangThaiVe
    ON dbo.Ve(MaSuat, TrangThaiVe);
GO
