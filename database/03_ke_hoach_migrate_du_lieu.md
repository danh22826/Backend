# Ke hoach migrate du lieu tu schema hien tai sang schema moi

## 1. Muc tieu migrate

- Gop bang dang nhap ve 1 cho duy nhat: `NguoiDung`.
- Bo bang trung nghia: `TaiKhoan`, `the_loai`, `phim_the_loai`.
- Bien `Ve` thanh bang "ghe da giu / da mua", khong luu san trang thai `TRONG`.
- Dung `vw_GheTrongTheoSuat` de tinh ghe trong theo thoi gian thuc.
- Chuan hoa trang thai hoa don, ve, suat chieu de tranh text tu do.

## 2. Mapping du lieu cu -> moi

### 2.1 Danh muc

- `the_loai` -> merge vao `TheLoai`
- `phim_the_loai` -> merge vao `PhimTheLoai`
- `LoaiGhe`, `LoaiPhong`, `ThanhPho`, `Rap`, `Phim` giu nguyen, chi lam sach kieu du lieu

### 2.2 Cấu truc rap

- `PhongChieu` giu lai
- `Ghe` giu lai, them PK/FK/day du `NOT NULL`
- `SucChua` trong `PhongChieu` duoc dong bo lai tu `COUNT(Ghe)`

### 2.3 Auth va khach hang

- `KhachHang` giu lai, them `NgayTao`, `TrangThai`
- `NguoiDung` tro thanh bang dang nhap duy nhat
- `TaiKhoan` chi dung lam nguon migrate:
  - neu `MatKhau` da hash bcrypt (`LIKE '$2%'`) -> migrate thang vao `NguoiDung`
  - neu `MatKhau` la plain text -> tao account `PENDING_RESET` voi placeholder hash, dong thoi ghi vao `TaiKhoan_PendingReset`

### 2.4 Don hang va ve

- `HoaDon` giu lai, them `ThoiGianHetHan`, `GhiChu`
- `Ve` cu:
  - ban ghi `TRONG` khong migrate sang bang `Ve` moi
  - chi migrate cac dong co y nghia dat/giu/da thanh toan/huy
  - gia ve moi dong duoc snapshot lai tu `SuatChieu.Gia + LoaiGhe.GiaPhuThu`

## 3. Thu tu chay de xuat

### Giai doan A - Freeze va backup

- Full backup database.
- Dung tat ca job ghi vao DB.
- Tam tat app hoac chuyen app sang maintenance mode.

### Giai doan B - Migrate schema trung gian

Chay file:

1. `database/02_alter_tung_buoc.sql`

Khi chay:

- Chay tung STEP.
- Sau moi STEP, kiem tra row count va sample du lieu.
- Neu co `THROW`, dung lai va xu ly tay truoc khi chay tiep.

### Giai doan C - Doi app sang model moi

- App backend doc:
  - ghe trong qua `vw_GheTrongTheoSuat` hoac query ghe minus `Ve active`
  - khong duoc tao san `Ve = TRONG`
  - login dung bang `NguoiDung`
- App frontend:
  - tao `HoaDon` pending
  - tao `Ve` khi user chon ghe
  - het han giu cho -> doi `HoaDon` sang `HET_HAN` hoac `DA_HUY`

### Giai doan D - Quan sat va cleanup

- Giu cac bang legacy trong 2-4 tuan:
  - `Ve_Legacy`
  - `the_loai_legacy`
  - `phim_the_loai_legacy`
  - `TaiKhoan` (hoac doi ten thanh `TaiKhoan_Legacy` sau khi xac nhan reset password)
- Khi app on dinh, moi chay block cleanup o cuoi file `02_alter_tung_buoc.sql`

## 4. Diem can kiem thu sau migrate

### 4.1 Kiem thu du lieu co ban

- So luong `TheLoai` sau merge co dung khong
- So luong `PhimTheLoai` sau merge co bi mat dong nao khong
- `PhongChieu.SucChua = COUNT(Ghe)` cho moi phong
- `Ghe` khong con `NULL` o cac cot khoa

### 4.2 Kiem thu auth

- Tai khoan admin hien co dang nhap duoc
- User migrate tu `TaiKhoan` co bcrypt dang nhap duoc
- User nam trong `TaiKhoan_PendingReset` bi chan dang nhap va duoc yeu cau reset password

### 4.3 Kiem thu dat ve

- Chon 1 suat chieu, danh sach ghe trong khong con doc tu `Ve(TRONG)`
- Tao hoa don pending -> tao duoc `Ve`
- Thanh toan hoa don -> `HoaDon = DA_THANH_TOAN`, `Ve = DA_THANH_TOAN`
- Huy hoa don pending -> `Ve` duoc giai phong
- Khong the dat trung 1 ghe trong cung `MaSuat`

### 4.4 Kiem thu bao cao

- Tong tien hoa don = tong `ThanhTien` cac ve lien quan
- Dem so ghe trong theo suat khop voi `vw_GheTrongTheoSuat`
- Trang thai suat chieu, hoa don, ve chi con nam trong bo gia tri cho phep

## 5. Cac query doi soat nen chay

```sql
-- 1. Kiem tra suc chua phong
SELECT pc.MaPhong, pc.SucChua, COUNT(g.MaGhe) AS SoGheThucTe
FROM dbo.PhongChieu pc
LEFT JOIN dbo.Ghe g ON g.MaPhong = pc.MaPhong
GROUP BY pc.MaPhong, pc.SucChua
HAVING pc.SucChua <> COUNT(g.MaGhe);

-- 2. Kiem tra trung ghe theo suat trong bang Ve moi
SELECT MaSuat, MaGhe, COUNT(*) AS SoLan
FROM dbo.Ve
GROUP BY MaSuat, MaGhe
HAVING COUNT(*) > 1;

-- 3. Kiem tra account can reset password
SELECT *
FROM dbo.TaiKhoan_PendingReset;

-- 4. Kiem tra cac ve khong migrate duoc
SELECT *
FROM dbo.Ve_MigrateSkipped;
```

## 6. Rollback plan

- Neu loi truoc STEP 6: restore tu backup la nhanh nhat.
- Neu loi trong STEP 6:
  - neu `Ve` chua bi rename -> rollback bang `DROP TABLE dbo.Ve_Moi`
  - neu da rename:
    - drop `dbo.Ve` moi
    - rename `dbo.Ve_Legacy` ve lai `dbo.Ve`
- Khong xoa bang legacy trong ngay migrate dau tien.

## 7. Khuyen nghi sau migrate

- Refactor code de bo han enum `VeStatus.TRONG`.
- Bo han doc/ghi bang `TaiKhoan`.
- Sau khi app on dinh, xoa cot `SuatChieu.MaRap`.
- Neu muon theo doi giao dich thanh toan chi tiet, them bang `ThanhToan` o phase 2.
