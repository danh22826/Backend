/* ============================================================
   DAT VE - Page Logic
   ============================================================ */

let suatChieu = null;
let allSeats = [];
let selectedSeats = [];

document.addEventListener('DOMContentLoaded', async () => {
  initPage('mua-ve');

  const params = new URLSearchParams(location.search);
  const maSuat = params.get('suat');

  if (!maSuat) {
    showError(
      'Khong tim thay ma suat chieu.',
      '<a href="DatVe.html" class="btn-primary" style="text-decoration:none;display:inline-block;margin-top:16px;">Chon lich chieu</a>'
    );
    return;
  }

  try {
    suatChieu = await apiFetch('/suat-chieu/' + encodeURIComponent(maSuat));
    allSeats = await apiFetch('/ghe/theo-suat/' + encodeURIComponent(maSuat));
    renderPage();
    updateSummary();
  } catch (error) {
    console.error('dat-ve init error:', error);
    showError('Loi tai du lieu suat chieu. Vui long thu lai.');
  }
});

function renderPage() {
  const el = document.getElementById('bookingContent');
  if (!el) return;

  el.innerHTML = `
    <div class="dv-wrapper">
      ${renderSeatSection()}
      ${renderSummarySection()}
    </div>`;
}

function renderSeatSection() {
  return `
    <div class="dv-seat-section">
      <h2 class="dv-movie-title">${escapeHtml(suatChieu.tenPhim || 'Phim')}</h2>
      <div class="dv-session-info">
        <span><i class="fas fa-map-marker-alt"></i> ${escapeHtml(suatChieu.tenRap || '')}</span>
        <span><i class="fas fa-door-open"></i> ${escapeHtml(suatChieu.tenPhong || 'Chua xep phong')}</span>
        <span><i class="fas fa-clock"></i> ${formatTimeSafe(suatChieu.gioChieu)}</span>
        <span><i class="fas fa-calendar"></i> ${formatDateSafe(suatChieu.ngayChieu)}</span>
      </div>

      <div class="dv-screen">
        <div class="dv-screen-line"></div>
        <span class="dv-screen-label">Man hinh</span>
      </div>

      <div class="dv-seat-map" id="seatMap">
        ${allSeats.length ? renderSeatMap() : '<p style="color:var(--text-muted);text-align:center;padding:40px 0;">Chua co so do ghe cho suat chieu nay</p>'}
      </div>

      <div class="dv-legend">
        <div class="dv-legend-item"><div class="dv-legend-box"></div>Thuong</div>
        <div class="dv-legend-item"><div class="dv-legend-box vip"></div>VIP</div>
        <div class="dv-legend-item"><div class="dv-legend-box sweetbox"></div>Sweetbox</div>
        <div class="dv-legend-item"><div class="dv-legend-box sel"></div>Dang chon</div>
        <div class="dv-legend-item"><div class="dv-legend-box booked"></div>Da dat</div>
      </div>
    </div>`;
}

function renderSeatMap() {
  const rows = {};
  allSeats.forEach(seat => {
    const rowKey = (seat.tenGhe || '?').replace(/[0-9]/g, '').trim() || '?';
    if (!rows[rowKey]) rows[rowKey] = [];
    rows[rowKey].push(seat);
  });

  Object.values(rows).forEach(seats => {
    seats.sort((a, b) => {
      const left = parseInt((a.tenGhe || '').replace(/\D/g, ''), 10) || 0;
      const right = parseInt((b.tenGhe || '').replace(/\D/g, ''), 10) || 0;
      return left - right;
    });
  });

  return Object.keys(rows)
    .sort()
    .map(rowKey => `
      <div class="dv-row">
        <span class="dv-row-label">${escapeHtml(rowKey)}</span>
        ${rows[rowKey].map(renderSeat).join('')}
        <span class="dv-row-label">${escapeHtml(rowKey)}</span>
      </div>
    `)
    .join('');
}

function renderSeat(seat) {
  const loai = (seat.tenLoaiGhe || '').toLowerCase();
  const phuThu = Number(seat.giaPhuThu || 0);
  const isBooked = seat.coTheDat === false || seat.trangThai === 'BOOKED';
  const isSelected = selectedSeats.some(item => item.maGhe === seat.maGhe);

  let cls = 'dv-seat';
  if (loai.includes('vip')) cls += ' vip';
  if (loai.includes('sweet') || loai.includes('doi')) cls += ' sweetbox';
  if (isBooked) cls += ' booked';
  if (isSelected) cls += ' selected';

  const displayNum = (seat.tenGhe || '').replace(/[A-Za-z]/g, '');

  return `<button
    class="${cls}"
    data-ma="${escapeHtml(seat.maGhe)}"
    data-ten="${escapeHtml(seat.tenGhe || '')}"
    data-phu-thu="${phuThu}"
    title="${escapeHtml(seat.tenGhe || '')} - ${escapeHtml(seat.tenLoaiGhe || 'Thuong')}"
    ${isBooked ? 'disabled' : 'onclick="toggleSeat(this)"'}
  >${escapeHtml(displayNum)}</button>`;
}

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
          <p><i class="fas fa-door-open"></i> ${escapeHtml(suatChieu.tenPhong || 'Chua xep phong')}</p>
          <p><i class="fas fa-clock"></i> ${formatTimeSafe(suatChieu.gioChieu)} · ${formatDateSafe(suatChieu.ngayChieu)}</p>
        </div>
      </div>

      <div class="dv-summary-row">
        <span class="label">Ghe da chon</span>
        <span class="value" id="summSeats">-</span>
      </div>
      <div class="dv-summary-row">
        <span class="label">Gia ve co ban</span>
        <span class="value" id="summBase">${formatCurrency(0)}</span>
      </div>
      <div class="dv-summary-row">
        <span class="label">Phu thu ghe</span>
        <span class="value" id="summExtra">${formatCurrency(0)}</span>
      </div>
      <div class="dv-total-row">
        <span>Tong cong</span>
        <span id="summTotal">${formatCurrency(0)}</span>
      </div>

      <button class="dv-btn-book" id="btnBook" disabled onclick="confirmBooking()">
        <i class="fas fa-file-invoice-dollar"></i> Tao hoa don
      </button>
    </div>`;
}

function toggleSeat(el) {
  const maGhe = el.dataset.ma;
  const tenGhe = el.dataset.ten;
  const phuThu = Number(el.dataset.phuThu || 0);

  const idx = selectedSeats.findIndex(seat => seat.maGhe === maGhe);
  if (idx >= 0) {
    selectedSeats.splice(idx, 1);
    el.classList.remove('selected');
  } else {
    if (selectedSeats.length >= 8) {
      showToast('Toi da 8 ghe moi lan dat', 'error');
      return;
    }
    selectedSeats.push({ maGhe, tenGhe, phuThu });
    el.classList.add('selected');
  }

  updateSummary();
}

function updateSummary() {
  const basePrice = Number(suatChieu?.gia || 0);
  const baseTotal = basePrice * selectedSeats.length;
  const extraTotal = selectedSeats.reduce((sum, seat) => sum + seat.phuThu, 0);
  const grandTotal = baseTotal + extraTotal;

  const seatsEl = document.getElementById('summSeats');
  const baseEl = document.getElementById('summBase');
  const extraEl = document.getElementById('summExtra');
  const totalEl = document.getElementById('summTotal');
  const buttonEl = document.getElementById('btnBook');

  if (seatsEl) seatsEl.textContent = selectedSeats.length ? selectedSeats.map(seat => seat.tenGhe).join(', ') : '-';
  if (baseEl) baseEl.textContent = formatCurrency(baseTotal);
  if (extraEl) extraEl.textContent = formatCurrency(extraTotal);
  if (totalEl) totalEl.textContent = formatCurrency(grandTotal);
  if (buttonEl) buttonEl.disabled = selectedSeats.length === 0;
}

async function confirmBooking() {
  if (!selectedSeats.length) return;

  const user = getUser();
  if (!user) {
    showToast('Vui long dang nhap de dat ve', 'error');
    sessionStorage.setItem('utc_redirect', window.location.href);
    setTimeout(() => location.href = '../lor/dang-nhap.html', 1200);
    return;
  }

  const maKhachHang = getCustomerCode(user);
  if (!maKhachHang) {
    showToast('Tai khoan chua gan ma khach hang', 'error');
    return;
  }

  const names = selectedSeats.map(seat => seat.tenGhe).join(', ');
  const total = formatCurrency(
    Number(suatChieu?.gia || 0) * selectedSeats.length +
    selectedSeats.reduce((sum, seat) => sum + seat.phuThu, 0)
  );

  if (!confirm(`Xac nhan tao hoa don?\nGhe: ${names}\nTong: ${total}`)) {
    return;
  }

  const btn = document.getElementById('btnBook');
  if (btn) {
    btn.disabled = true;
    btn.textContent = 'Dang xu ly...';
  }

  try {
    const invoice = await apiFetch('/hoa-don', {
      method: 'POST',
      body: JSON.stringify({
        maSuat: suatChieu.maSuat,
        maKhachHang,
        dsGhe: selectedSeats.map(seat => seat.maGhe)
      })
    });

    showToast('Da tao hoa don, chuyen sang trang thanh toan', 'success');
    setTimeout(() => {
      location.href = `hoa-don.html?maDon=${encodeURIComponent(invoice.maDon)}`;
    }, 500);
  } catch (error) {
    showToast('Dat ve that bai: ' + error.message, 'error');
    selectedSeats = [];
    await reloadSeatState();
  } finally {
    if (btn) {
      btn.disabled = selectedSeats.length === 0;
      btn.innerHTML = '<i class="fas fa-file-invoice-dollar"></i> Tao hoa don';
    }
  }
}

async function reloadSeatState() {
  if (!suatChieu?.maSuat) return;

  try {
    allSeats = await apiFetch('/ghe/theo-suat/' + encodeURIComponent(suatChieu.maSuat));
    renderPage();
    updateSummary();
  } catch (error) {
    console.error('reload seat state error:', error);
  }
}

function showError(msg, extra = '') {
  const el = document.getElementById('bookingContent');
  if (!el) return;

  el.innerHTML = `
    <div class="empty-state">
      <i class="fas fa-exclamation-triangle"></i>
      <h3>${escapeHtml(msg)}</h3>
      ${extra}
    </div>`;
}

function formatTimeSafe(time) {
  if (!time) return '--:--';
  return String(time).slice(0, 5);
}

function formatDateSafe(date) {
  if (!date) return '';
  try {
    return new Date(date).toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  } catch {
    return String(date);
  }
}
