/* ============================================================
   UTC CINEMA – Membership Page Logic
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {
    // Khởi tạo Header, Footer và các thành phần dùng chung từ shared.js
    if (typeof initPage === 'function') {
        initPage('the-thanh-vien');
    }

    // Bạn có thể bổ sung các logic như kiểm tra hạng thành viên hiện tại
    // của người dùng đã đăng nhập để hiển thị các ưu đãi tương ứng tại đây.
});