/* ============================================================
   ĐẶT VÉ – Page Logic
   API thực tế:
     GET /api/suat-chieu/{maSuat}
       → { maSuat, gioChieu, ngayChieu, gia, maPhim, tenPhim,
           maPhong, tenPhong, maRap, tenRap }
     GET /api/ghe?maPhong={maPhong}
       → [{ maGhe, tenGhe, maPhong, maLoaiGhe, tenLoaiGhe, giaPhuThu }]
   ============================================================ */

/* ---------- State ---------- */
let suatChieu   = null;   // object suất chiếu
let allSeats    = [];     // toàn bộ ghế trong phòng
let selectedSeats = [];   // ghế đang chọn: [{ maGhe, tenGhe, phuThu }]

const BASE_PRICE = 75000; // giá vé cơ bản, đồng bộ với backend

/* ---------- Init ---------- */
document.addEventListener('DOMContentLoaded', async () => {
  initPage('mua-ve');

  const params  = new URLSearchParams(location.search);
  const maSuat  = params.get('suat');

  if (!maSuat) {
    showError('Không tìm thấy mã suất chiếu.',
              '<a href="mua-ve.html" class="btn-primary" style="text-decoration:none;display:inline-block;margin-top:16px;">Chọn lịch chiếu</a>');
    return;
  }

  try {
    // 1. Lấy thông tin suất chiếu
    suatChieu = await apiFetch('/suat-chieu/' + encodeURIComponent(maSuat));

    // 2. Lấy ghế — maPhong có thể null với một số suất
    const maPhong = suatChieu.maPhong;
    if (maPhong) {
      allSeats = await apiFetch('/ghe?maPhong=' + encodeURIComponent(maPhong));
    }

    renderPage();
  } catch (e) {
    console.error('dat-ve init error:', e);
    showError('Lỗi tải dữ liệu suất chiếu. Vui lòng thử lại.');
  }
});

/* ============================================================
   RENDER TRANG
   ============================================================ */
function renderPage() {
  const el = document.getElementById('bookingContent');
  if (!el) return;

  el.innerHTML = `
    <div class="dv-wrapper">
      ${renderSeatSection()}
      ${renderSummarySection()}
    </div>`;
}

/* ----- Cột trái: Sơ đồ ghế ----- */
function renderSeatSection() {
  return `
    <div class="dv-seat-section">
      <h2 class="dv-movie-title">${escapeHtml(suatChieu.tenPhim || 'Phim')}</h2>
      <div class="dv-session-info">
        <span><i class="fas fa-map-marker-alt"></i> ${escapeHtml(suatChieu.tenRap || '')}</span>
        <span><i class="fas fa-door-open"></i> ${escapeHtml(suatChieu.tenPhong || 'Chưa xếp phòng')}</span>
        <span><i class="fas fa-clock"></i> ${formatTimeSafe(suatChieu.gioChieu)}</span>
        <span><i class="fas fa-calendar"></i> ${formatDateSafe(suatChieu.ngayChieu)}</span>
      </div>

      <div class="dv-screen">
        <div class="dv-screen-line"></div>
        <span class="dv-screen-label">Màn hình</span>
      </div>

      <div class="dv-seat-map" id="seatMap">
        ${allSeats.length ? renderSeatMap() : '<p style="color:var(--text-muted);text-align:center;padding:40px 0;">Chưa có sơ đồ ghế cho suất chiếu này</p>'}
      </div>

      <div class="dv-legend">
        <div class="dv-legend-item"><div class="dv-legend-box"></div>Thường</div>
        <div class="dv-legend-item"><div class="dv-legend-box vip"></div>VIP (+${formatCurrency(20000)})</div>
        <div class="dv-legend-item"><div class="dv-legend-box sweetbox"></div>Sweetbox</div>
        <div class="dv-legend-item"><div class="dv-legend-box sel"></div>Đang chọn</div>
        <div class="dv-legend-item"><div class="dv-legend-box booked"></div>Đã đặt</div>
      </div>
    </div>`;
}

function renderSeatMap() {
  // Nhóm ghế theo hàng (ký tự đầu của tenGhe: A1 → A, B3 → B)
  const rows = {};
  allSeats.forEach(s => {
    const rowKey = (s.tenGhe || '?').replace(/[0-9]/g, '').trim() || '?';
    if (!rows[rowKey]) rows[rowKey] = [];
    rows[rowKey].push(s);
  });

  // Sắp xếp ghế trong hàng theo số
  Object.values(rows).forEach(arr =>
    arr.sort((a, b) => {
      const na = parseInt((a.tenGhe || '').replace(/\D/g, '')) || 0;
      const nb = parseInt((b.tenGhe || '').replace(/\D/g, '')) || 0;
      return na - nb;
    })
  );

  // Sắp xếp hàng theo alphabet
  const sortedRows = Object.keys(rows).sort();

  return sortedRows.map(rowKey => {
    const seats = rows[rowKey];
    return `
      <div class="dv-row">
        <span class="dv-row-label">${escapeHtml(rowKey)}</span>
        ${seats.map(s => renderSeat(s)).join('')}
        <span class="dv-row-label">${escapeHtml(rowKey)}</span>
      </div>`;
  }).join('');
}

function renderSeat(s) {
  // Xác định loại ghế từ tenLoaiGhe
  const loai = (s.tenLoaiGhe || '').toLowerCase();
  const phuThu = parseFloat(s.giaPhuThu) || 0;

  // Trạng thái đặt: API /ghe không trả trangThai, nên mặc định là trống
  // Sau này nếu có API trạng thái ghế theo suất thì cập nhật ở đây
  const isBooked = false;

  let cls = 'dv-seat';
  if (isBooked)              cls += ' booked';
  if (loai.includes('vip')) cls += ' vip';
  if (loai.includes('sweet') || loai.includes('đôi')) cls += ' sweetbox';

  // Hiển thị số ghế (bỏ ký tự hàng, chỉ giữ số)
  const displayNum = (s.tenGhe || '').replace(/[A-Za-z]/g, '');

  return `<button
    class="${cls}"
    data-ma="${escapeHtml(s.maGhe)}"
    data-ten="${escapeHtml(s.tenGhe || '')}"
    data-phu-thu="${phuThu}"
    title="${escapeHtml(s.tenGhe || '')} – ${escapeHtml(s.tenLoaiGhe || 'Thường')}"
    ${isBooked ? 'disabled' : 'onclick="toggleSeat(this)"'}
  >${escapeHtml(displayNum)}</button>`;
}

/* ----- Cột phải: Tóm tắt ----- */
function renderSummarySection() {
  const posterSrc = suatChieu.poster
    ? getPosterUrl(suatChieu.poster)
    : `${BACKEND}/poster/default.jpg`;

  return `
    <div class="dv-summary" id="dvSummary">
      <div class="dv-summary-movie">
        <img class="dv-summary-poster"
             src="${posterSrc}"
             alt="${escapeHtml(suatChieu.tenPhim || '')}"
             onerror="this.src='https://via.placeholder.com/90x130?text=No+Image'">
        <div class="dv-summary-info">
          <h3>${escapeHtml(suatChieu.tenPhim || '')}</h3>
          <p><i class="fas fa-map-marker-alt"></i> ${escapeHtml(suatChieu.tenRap || '')}</p>
          <p><i class="fas fa-door-open"></i> ${escapeHtml(suatChieu.tenPhong || 'Chưa xếp phòng')}</p>
          <p><i class="fas fa-clock"></i> ${formatTimeSafe(suatChieu.gioChieu)} · ${formatDateSafe(suatChieu.ngayChieu)}</p>
        </div>
      </div>

      <div class="dv-summary-row">
        <span class="label">Ghế đã chọn</span>
        <span class="value" id="summSeats">–</span>
      </div>
      <div class="dv-summary-row">
        <span class="label">Giá vé cơ bản</span>
        <span class="value" id="summBase">${formatCurrency(0)}</span>
      </div>
      <div class="dv-summary-row">
        <span class="label">Phụ thu ghế</span>
        <span class="value" id="summExtra">${formatCurrency(0)}</span>
      </div>
      <div class="dv-total-row">
        <span>Tổng cộng</span>
        <span id="summTotal">${formatCurrency(0)}</span>
      </div>

      <button class="dv-btn-book" id="btnBook" disabled onclick="confirmBooking()">
        <i class="fas fa-ticket-alt"></i> Đặt vé
      </button>
    </div>`;
}

/* ============================================================
   CHỌN GHẾ
   ============================================================ */
function toggleSeat(el) {
  const maGhe  = el.dataset.ma;
  const tenGhe = el.dataset.ten;
  const phuThu = parseFloat(el.dataset.phuThu) || 0;

  const idx = selectedSeats.findIndex(s => s.maGhe === maGhe);

  if (idx >= 0) {
    // Bỏ chọn
    selectedSeats.splice(idx, 1);
    el.classList.remove('selected');
  } else {
    // Giới hạn 8 ghế
    if (selectedSeats.length >= 8) {
      showToast('Tối đa 8 ghế mỗi lần đặt', 'error');
      return;
    }
    selectedSeats.push({ maGhe, tenGhe, phuThu });
    el.classList.add('selected');
  }

  updateSummary();
}

function updateSummary() {
  const baseTotal  = BASE_PRICE * selectedSeats.length;
  const extraTotal = selectedSeats.reduce((sum, s) => sum + s.phuThu, 0);
  const grandTotal = baseTotal + extraTotal;

  document.getElementById('summSeats').textContent =
    selectedSeats.length ? selectedSeats.map(s => s.tenGhe).join(', ') : '–';
  document.getElementById('summBase').textContent  = formatCurrency(baseTotal);
  document.getElementById('summExtra').textContent = formatCurrency(extraTotal);
  document.getElementById('summTotal').textContent = formatCurrency(grandTotal);
  document.getElementById('btnBook').disabled = selectedSeats.length === 0;
}

/* ============================================================
   XÁC NHẬN ĐẶT VÉ
   ============================================================ */
async function confirmBooking() {
  if (!selectedSeats.length) return;

  // Kiểm tra đăng nhập
  const user = getUser();
  if (!user) {
    showToast('Vui lòng đăng nhập để đặt vé', 'error');
    sessionStorage.setItem('utc_redirect', window.location.href);
    setTimeout(() => location.href = 'dang-nhap.html', 1500);
    return;
  }

  const names = selectedSeats.map(s => s.tenGhe).join(', ');
  const total = formatCurrency(
    BASE_PRICE * selectedSeats.length +
    selectedSeats.reduce((sum, s) => sum + s.phuThu, 0)
  );

  if (!confirm(`Xác nhận đặt ${selectedSeats.length} vé?\nGhế: ${names}\nTổng: ${total}`)) return;

  const btn = document.getElementById('btnBook');
  btn.disabled = true;
  btn.textContent = 'Đang xử lý...';

  try {
    // TODO: gọi API POST /api/hoa-don khi backend sẵn sàng
    // await apiFetch('/hoa-don', {
    //   method: 'POST',
    //   body: JSON.stringify({
    //     maSuat: suatChieu.maSuat,
    //     maKhachHang: user.username,
    //     dsGhe: selectedSeats.map(s => s.maGhe)
    //   })
    // });

    showToast('Đặt vé thành công! Cảm ơn quý khách.', 'success');
    setTimeout(() => location.href = 'index.html', 2000);
  } catch (e) {
    showToast('Đặt vé thất bại: ' + e.message, 'error');
    btn.disabled = false;
    btn.innerHTML = '<i class="fas fa-ticket-alt"></i> Đặt vé';
  }
}

/* ============================================================
   HELPERS
   ============================================================ */
function showError(msg, extra = '') {
  const el = document.getElementById('bookingContent');
  if (el) el.innerHTML = `
    <div class="empty-state">
      <i class="fas fa-exclamation-triangle"></i>
      <h3>${escapeHtml(msg)}</h3>
      ${extra}
    </div>`;
}

function formatTimeSafe(t) {
  if (!t) return '--:--';
  return String(t).slice(0, 5);
}

function formatDateSafe(d) {
  if (!d) return '';
  try {
    return new Date(d).toLocaleDateString('vi-VN', {
      day: '2-digit', month: '2-digit', year: 'numeric'
    });
  } catch { return String(d); }
}