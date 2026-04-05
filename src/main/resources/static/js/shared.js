/* ============================================================
   UTC CINEMA – Shared JavaScript Utilities (JWT Integrated)
   ============================================================ */

// Cấu hình địa chỉ Backend
const BACKEND = (window.location.protocol === 'file:' || window.location.port !== '8080')
  ? 'http://localhost:8080'
  : '';

const API_BASE = BACKEND + '/api';

const PAGE_URLS = {
  home: 'menu.html',
  movie: 'main.html',
  detail: 'chitietphim.html',
  booking: 'DatVe.html',
  login: '../lor/dang-nhap.html',
  membership: '../lor/the-thanh-vien.html',
  support: '../lor/ho-tro.html',
  cinema: '../lor/rap-chieu-phim.html',
  promo: '../lor/uu-dai.html',
  contact: '../lor/lien-he.html'
};

/* =========================
   AUTHENTICATION & USER
========================= */
function getUser() {
  const userData = sessionStorage.getItem('utc_user');
  return userData ? JSON.parse(userData) : null;
}

function getCustomerCode(user = getUser()) {
  if (!user) return '';
  // Ưu tiên lấy maKhachHang từ JWT Response
  return user.maKhachHang || user.customerCode || user.username || '';
}

function isAdmin() {
  const u = getUser();
  return u && u.role === 'ROLE_ADMIN';
}

function logout() {
  sessionStorage.removeItem('utc_user');
  window.location.href = PAGE_URLS.home;
}

/* =========================
   API FETCH (Hỗ trợ JWT)
========================= */
async function apiFetch(path, options = {}) {
  const url = API_BASE + path;
  const user = getUser();

  // Khởi tạo headers mặc định
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  // ✅ TỰ ĐỘNG GẮN TOKEN NẾU NGƯỜI DÙNG ĐÃ ĐĂNG NHẬP
  if (user && user.token) {
    headers['Authorization'] = `Bearer ${user.token}`;
  }

  const res = await fetch(url, {
    ...options,
    headers: headers
  });

  // Xử lý lỗi hệ thống
  if (!res.ok) {
    // Nếu bị lỗi 401 (Hết hạn token) hoặc 403 (Không có quyền)
    if (res.status === 401 || res.status === 403) {
      console.warn("Phiên đăng nhập hết hạn hoặc không hợp lệ.");
      // Tùy chọn: Tự động logout nếu cần
      // logout();
    }

    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) {
      const data = await res.json();
      throw new Error(data.message || data.error || `Lỗi ${res.status}`);
    }
    const text = await res.text();
    throw new Error(text || `Lỗi ${res.status}`);
  }

  const contentType = res.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    return await res.json();
  }
  return null;
}

/* =========================
   UI RENDERING (HEADER/FOOTER)
========================= */
function renderHeader(activePage) {
  const user = getUser();
  const pages = [
    { key: 'home', label: 'TRANG CHỦ', href: PAGE_URLS.home },
    { key: 'mua-ve', label: 'MUA VÉ', href: PAGE_URLS.booking },
    { key: 'phim', label: 'PHIM', href: PAGE_URLS.movie },
    { key: 'rap', label: 'RẠP CHIẾU PHIM', href: PAGE_URLS.cinema },
    { key: 'uu-dai', label: 'TIN MỚI & ƯU ĐÃI', href: PAGE_URLS.promo },
    { key: 'lien-he', label: 'LIÊN HỆ', href: PAGE_URLS.contact }
  ];

  const loginArea = user
    ? `<span>Xin chào, <strong>${escapeHtml(user.username)}</strong></span>
       <span>|</span>
       <a href="#" onclick="logout();return false">Đăng xuất</a>`
    : `<a href="${PAGE_URLS.login}">Đăng nhập</a>
       <span>|</span>
       <a href="${PAGE_URLS.login}#register">Đăng ký</a>`;

  return `
    <div class="top-bar">
      <div class="container">
        <div class="top-bar-left">${loginArea}</div>
        <div class="top-bar-right">
          <a href="${PAGE_URLS.membership}">Thẻ thành viên</a>
          <a href="${PAGE_URLS.support}">Hỗ trợ khách hàng</a>
        </div>
      </div>
    </div>
    <div class="header-main">
      <a href="${PAGE_URLS.home}" class="header-logo">
        <img src="${BACKEND}/poster/logo.png" alt="UTC Cinema" id="headerLogo">
        <span class="logo-text">UTC CINEMA</span>
      </a>
    </div>
    <nav class="main-nav" id="mainNav">
      <div class="container">
        <button class="nav-toggle" onclick="toggleNav()"><i class="fas fa-bars"></i></button>
        <div class="nav-links" id="navLinks">
          ${pages.map((p, i) => {
            const cls = p.key === activePage ? ' active' : '';
            const div = i > 0 ? '<span class="nav-divider">|</span>' : '';
            return `${div}<a href="${p.href}" class="${cls}">${p.label}</a>`;
          }).join('')}
        </div>
      </div>
    </nav>`;
}

function renderFooter() {
  return `
    <footer class="site-footer">
      <div class="container">
        <div class="footer-top">
          <div class="footer-brand">
            <div class="footer-logo">UTC CINEMA</div>
            <p>Hệ thống rạp chiếu phim hiện đại với trải nghiệm đỉnh cao.</p>
          </div>
          <div class="footer-links">
            <h4>Khám phá</h4>
            <a href="${PAGE_URLS.movie}">Phim đang chiếu</a>
            <a href="${PAGE_URLS.booking}">Mua vé online</a>
          </div>
          <div class="footer-links">
            <h4>Hỗ trợ</h4>
            <a href="${PAGE_URLS.support}">Câu hỏi thường gặp</a>
            <a href="${PAGE_URLS.membership}">Thẻ thành viên</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2026 UTC CINEMA. All rights reserved.</p>
        </div>
      </div>
    </footer>`;
}

function renderQuickActions() {
  return `
    <aside class="quick-actions">
      <a href="${PAGE_URLS.booking}" class="quick-action-btn"><i class="fas fa-ticket-alt"></i>Đặt vé nhanh</a>
      <a href="${PAGE_URLS.cinema}" class="quick-action-btn"><i class="fas fa-map-marker-alt"></i>Nơi đặt vé</a>
      <button class="quick-action-btn" onclick="scrollToTop()"><i class="fas fa-chevron-up"></i>TOP</button>
    </aside>`;
}

function initPage(activePage) {
  const header = document.getElementById('site-header');
  if (header) header.innerHTML = renderHeader(activePage);
  const footer = document.getElementById('site-footer');
  if (footer) footer.innerHTML = renderFooter();
  const quick = document.getElementById('quick-actions');
  if (quick) quick.innerHTML = renderQuickActions();
}

/* =========================
   FORMATTERS & HELPERS
========================= */
function formatDate(d) {
  return d ? new Date(d).toLocaleDateString('vi-VN') : '';
}

function formatTime(t) {
  return t ? String(t).substring(0, 5) : '';
}

function formatCurrency(num) {
  return Number(num || 0).toLocaleString('vi-VN') + '₫';
}

function getPosterUrl(poster) {
  if (!poster) return `${BACKEND}/poster/default.jpg`;
  return poster.startsWith('http') ? poster : `${BACKEND}${poster.startsWith('/') ? '' : '/poster/'}${poster}`;
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

function showToast(message, type = 'success') {
  let container = document.querySelector('.toast-container') || document.createElement('div');
  if (!container.parentElement) {
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => toast.classList.add('show'), 10);
  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => toast.remove(), 300);
  }, 3000);
}

function scrollToTop() { window.scrollTo({ top: 0, behavior: 'smooth' }); }
function toggleNav() { document.getElementById('navLinks')?.classList.toggle('open'); }

function formatISODate(date) {
  if (!(date instanceof Date) || Number.isNaN(date.getTime())) return '';

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function getDayName(date) {
  if (!(date instanceof Date) || Number.isNaN(date.getTime())) return '';
  return ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'][date.getDay()];
}

function getAgeBadge(age) {
  if (!age) return '';

  const normalizedAge = String(age).trim().toUpperCase();
  let badgeClass = 'age-P';

  if (normalizedAge.includes('18')) badgeClass = 'age-18';
  else if (normalizedAge.includes('16')) badgeClass = 'age-16';
  else if (normalizedAge.includes('13')) badgeClass = 'age-13';

  return `<span class="age-badge ${badgeClass}">${escapeHtml(normalizedAge)}</span>`;
}

function formatDateTime(value) {
  if (!value) return '';

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);

  return date.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}
