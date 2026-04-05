/* ============================================================
   UTC CINEMA – Shared JavaScript Utilities
   ============================================================ */
const BACKEND = (window.location.protocol === 'file:' || window.location.port !== '8080')
  ? 'http://localhost:8080'
  : '';

const API_BASE = BACKEND + '/api';

/* =========================
   HEADER
========================= */
function renderHeader(activePage) {
  const user = JSON.parse(sessionStorage.getItem('utc_user') || 'null');

  const pages = [
    { key: 'home', label: 'TRANG CHỦ', href: 'index.html' },
    { key: 'mua-ve', label: 'MUA VÉ', href: 'mua-ve.html' },
    { key: 'phim', label: 'PHIM', href: 'phim.html' },
    { key: 'rap', label: 'RẠP CHIẾU PHIM', href: 'rap-chieu-phim.html' },
    { key: 'uu-dai', label: 'TIN MỚI & ƯU ĐÃI', href: 'uu-dai.html' },
    { key: 'lien-he', label: 'LIÊN HỆ', href: 'lien-he.html' }
  ];

  const loginArea = user
    ? `<span>Xin chào, <strong>${escapeHtml(user.username)}</strong></span>
       <span>|</span>
       <a href="#" onclick="logout();return false">Đăng xuất</a>`
    : `<a href="dang-nhap.html">Đăng nhập</a>
       <span>|</span>
       <a href="dang-nhap.html#register">Đăng ký</a>`;

  const rightLinks = `
    <a href="the-thanh-vien.html">Thẻ thành viên</a>
    <a href="ho-tro.html">Hỗ trợ khách hàng</a>
  `;

  return `
    <div class="top-bar">
      <div class="container">
        <div class="top-bar-left">${loginArea}</div>
        <div class="top-bar-right">${rightLinks}</div>
      </div>
    </div>

    <div class="header-main">
      <a href="index.html" class="header-logo">
        <img src="" alt="UTC Cinema" onerror="this.style.display='none'" id="headerLogo">
        <span class="logo-text">UTC CINEMA</span>
      </a>
    </div>

    <nav class="main-nav" id="mainNav">
      <div class="container">
        <button class="nav-toggle" onclick="toggleNav()" aria-label="Menu">
          <i class="fas fa-bars"></i>
        </button>
        <div class="nav-links" id="navLinks">
          ${pages.map((p, i) => {
            const cls = p.key === activePage ? ' active' : '';
            const div = i > 0 ? '<span class="nav-divider">|</span>' : '';
            return `${div}<a href="${p.href}" class="${cls}">${p.label}</a>`;
          }).join('')}
        </div>
      </div>
    </nav>
  `;
}

/* =========================
   FOOTER
========================= */
function renderFooter() {
  return `
    <footer class="site-footer">
      <div class="container">
        <div class="footer-top">
          <div class="footer-brand">
            <div class="footer-logo">UTC CINEMA</div>
            <p>Hệ thống rạp chiếu phim hiện đại<br>với trải nghiệm điện ảnh đỉnh cao.</p>
          </div>

          <div class="footer-links">
            <h4>Khám phá</h4>
            <a href="phim.html">Phim đang chiếu</a>
            <a href="mua-ve.html">Mua vé online</a>
            <a href="rap-chieu-phim.html">Hệ thống rạp</a>
            <a href="uu-dai.html">Tin mới & Ưu đãi</a>
          </div>

          <div class="footer-links">
            <h4>Hỗ trợ</h4>
            <a href="ho-tro.html">Câu hỏi thường gặp</a>
            <a href="lien-he.html">Liên hệ</a>
            <a href="the-thanh-vien.html">Thẻ thành viên</a>
          </div>

          <div class="footer-links">
            <h4>Kết nối</h4>
            <a href="#"><i class="fab fa-facebook"></i> Facebook</a>
            <a href="#"><i class="fab fa-instagram"></i> Instagram</a>
            <a href="#"><i class="fab fa-youtube"></i> YouTube</a>
          </div>
        </div>

        <div class="footer-bottom">
          <div class="footer-legal">
            <a href="#">Chính sách bảo mật</a>
            <a href="#">Điều khoản sử dụng</a>
            <a href="#">Chính sách khách hàng</a>
          </div>
          <div>
            <p>&copy; 2026 UTC CINEMA. All rights reserved.</p>
          </div>
        </div>
      </div>
    </footer>
  `;
}

/* =========================
   QUICK ACTIONS
========================= */
function renderQuickActions() {
  return `
    <aside class="quick-actions" id="quickActions">
      <a href="mua-ve.html" class="quick-action-btn">
        <i class="fas fa-ticket-alt"></i>Đặt vé nhanh
      </a>
      <a href="rap-chieu-phim.html" class="quick-action-btn">
        <i class="fas fa-map-marker-alt"></i>Nơi đặt vé
      </a>
      <a href="the-thanh-vien.html" class="quick-action-btn">
        <i class="fas fa-trophy"></i>Membership
      </a>
      <a href="ho-tro.html" class="quick-action-btn">
        <i class="fas fa-question-circle"></i>Hỗ trợ
      </a>
      <button class="quick-action-btn" onclick="scrollToTop()">
        <i class="fas fa-chevron-up"></i>TOP
      </button>
    </aside>
  `;
}

/* =========================
   INIT PAGE
========================= */
function initPage(activePage) {
  const headerEl = document.getElementById('site-header');
  if (headerEl) headerEl.innerHTML = renderHeader(activePage);

  const logoEl = document.getElementById('headerLogo');
  if (logoEl) logoEl.src = BACKEND + '/poster/logo.png';

  const footerEl = document.getElementById('site-footer');
  if (footerEl) footerEl.innerHTML = renderFooter();

  const quickActionsEl = document.getElementById('quick-actions');
  if (quickActionsEl) quickActionsEl.innerHTML = renderQuickActions();
}

/* =========================
   NAV / AUTH
========================= */
function toggleNav() {
  document.getElementById('navLinks')?.classList.toggle('open');
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function logout() {
  sessionStorage.removeItem('utc_user');
  window.location.href = 'index.html';
}

function getUser() {
  return JSON.parse(sessionStorage.getItem('utc_user') || 'null');
}

function isAdmin() {
  const u = getUser();
  return u && u.role === 'ROLE_ADMIN';
}

/* =========================
   API
========================= */
async function apiFetch(path, options = {}) {
  const url = API_BASE + path;

  const res = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }

  const ct = res.headers.get('content-type') || '';
  if (ct.includes('application/json')) {
    return await res.json();
  }

  return null;
}

/* =========================
   FORMATTERS
========================= */
function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  return d.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
}

function formatTime(timeStr) {
  if (!timeStr) return '';
  return String(timeStr).substring(0, 5);
}

function formatCurrency(num) {
  return Number(num || 0).toLocaleString('vi-VN') + '₫';
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

function getAgeBadge(age) {
  if (!age) return '';
  const a = String(age).toUpperCase();

  let cls = 'age-P';
  if (a.includes('18')) cls = 'age-18';
  else if (a.includes('16')) cls = 'age-16';
  else if (a.includes('13')) cls = 'age-13';

  return `<span class="age-badge ${cls}">${escapeHtml(a)}</span>`;
}

function getPosterUrl(poster) {
  if (!poster) return BACKEND + '/poster/default.jpg';
  if (poster.startsWith('http')) return poster;
  return BACKEND + '/poster/' + poster;
}

/* =========================
   TOAST
========================= */
function showToast(message, type = 'success') {
  let container = document.querySelector('.toast-container');

  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;
  container.appendChild(toast);

  requestAnimationFrame(() => toast.classList.add('show'));

  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => toast.remove(), 300);
  }, 3000);
}

/* =========================
   DATE HELPERS
========================= */
function getDayName(date) {
  const days = ['CN', 'Hai', 'Ba', 'Tư', 'Năm', 'Sáu', 'Bảy'];
  return days[date.getDay()];
}

function getMonthYear(date) {
  return `${date.getMonth() + 1}/${date.getFullYear()}`;
}

function formatISODate(date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}