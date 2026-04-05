let invoiceDetail = null;
let countdownTimer = null;

document.addEventListener('DOMContentLoaded', async () => {
  initPage('mua-ve');

  const params = new URLSearchParams(location.search);
  const maDon = params.get('maDon');

  if (!maDon) {
    renderInvoiceError(
      'Khong tim thay ma hoa don.',
      '<a href="DatVe.html" class="btn-primary" style="text-decoration:none;display:inline-block;margin-top:16px;">Quay lai dat ve</a>'
    );
    return;
  }

  await loadInvoice(maDon);
});

async function loadInvoice(maDon) {
  try {
    invoiceDetail = await apiFetch('/hoa-don/' + encodeURIComponent(maDon));
    renderInvoice();
  } catch (error) {
    console.error('load invoice error:', error);
    renderInvoiceError('Khong tai duoc hoa don: ' + escapeHtml(error.message || 'Loi khong xac dinh'));
  }
}

function renderInvoice() {
  const container = document.getElementById('invoiceContent');
  if (!container || !invoiceDetail) return;

  clearCountdown();

  const posterSrc = invoiceDetail.poster
    ? getPosterUrl(invoiceDetail.poster)
    : `${BACKEND}/poster/default.jpg`;

  container.innerHTML = `
    <div class="hd-layout">
      <div class="hd-card">
        <div class="hd-header">
          <div>
            <h1 class="hd-title">Hoa don ${escapeHtml(invoiceDetail.maDon || '')}</h1>
            <div class="hd-subtitle">Thong tin chi tiet don dat ve va thanh toan</div>
          </div>
          <div class="hd-status ${getStatusClass(invoiceDetail.trangThai)}">
            <i class="fas ${getStatusIcon(invoiceDetail.trangThai)}"></i>
            <span>${escapeHtml(getStatusLabel(invoiceDetail.trangThai))}</span>
          </div>
        </div>

        <div class="hd-grid">
          <div class="hd-card" style="padding:18px;">
            <h3 class="hd-panel-title">Thong tin hoa don</h3>
            <div class="hd-meta">
              <div class="hd-meta-row"><span class="label">Ma khach hang</span><strong>${escapeHtml(invoiceDetail.maKhachHang || '-')}</strong></div>
              <div class="hd-meta-row"><span class="label">Thoi gian tao</span><strong>${escapeHtml(formatDateTime(invoiceDetail.thoiGianDat) || '-')}</strong></div>
              <div class="hd-meta-row"><span class="label">Han thanh toan</span><strong>${escapeHtml(formatDateTime(invoiceDetail.hanThanhToan) || '-')}</strong></div>
              <div class="hd-meta-row"><span class="label">Thanh toan luc</span><strong>${escapeHtml(formatDateTime(invoiceDetail.thoiGianThanhToan) || '-')}</strong></div>
              <div class="hd-meta-row"><span class="label">Phuong thuc</span><strong>${escapeHtml(invoiceDetail.phuongThucThanhToan || '-')}</strong></div>
            </div>
          </div>

          <div class="hd-card" style="padding:18px;">
            <h3 class="hd-panel-title">Thong tin suat chieu</h3>
            <div class="hd-movie">
              <img class="hd-poster"
                   src="${posterSrc}"
                   alt="${escapeHtml(invoiceDetail.tenPhim || '')}"
                   onerror="this.src='https://via.placeholder.com/110x154?text=No+Image'">
              <div class="hd-movie-info">
                <h3>${escapeHtml(invoiceDetail.tenPhim || '-')}</h3>
                <p><i class="fas fa-map-marker-alt"></i> ${escapeHtml(invoiceDetail.tenRap || '-')}</p>
                <p><i class="fas fa-door-open"></i> ${escapeHtml(invoiceDetail.tenPhong || '-')}</p>
                <p><i class="fas fa-clock"></i> ${escapeHtml(formatTime(invoiceDetail.gioChieu) || '--:--')} · ${escapeHtml(formatDate(invoiceDetail.ngayChieu) || '-')}</p>
                <p><i class="fas fa-ticket-alt"></i> ${invoiceDetail.soVe || 0} ghe / ${invoiceDetail.tongGhe || 0} ghe</p>
              </div>
            </div>
          </div>
        </div>

        <div class="hd-card" style="padding:18px; margin-top:20px;">
          <h3 class="hd-panel-title">Danh sach ve</h3>
          ${renderTicketTable()}
        </div>
      </div>

      <div class="hd-sidebar">
        <div class="hd-card">
          <h3 class="hd-panel-title">Tong ket thanh toan</h3>
          <div class="hd-meta">
            <div class="hd-meta-row"><span class="label">Gia ve co ban</span><strong>${formatCurrency(invoiceDetail.giaVeCoBan || 0)}</strong></div>
            <div class="hd-meta-row"><span class="label">So luong ghe</span><strong>${invoiceDetail.soVe || 0}</strong></div>
            <div class="hd-meta-row"><span class="label">Tong thanh toan</span><strong>${formatCurrency(invoiceDetail.tongTien || 0)}</strong></div>
          </div>
          <div class="hd-total">
            <span>Tong cong</span>
            <span>${formatCurrency(invoiceDetail.tongTien || 0)}</span>
          </div>
        </div>

        <div class="hd-card">
          <h3 class="hd-panel-title">Thao tac</h3>
          ${renderActionPanel()}
        </div>
      </div>
    </div>
  `;

  startCountdown();
}

function renderTicketTable() {
  if (!invoiceDetail.dsVe || !invoiceDetail.dsVe.length) {
    return '<div class="hd-empty">Hoa don hien khong con ve nao.</div>';
  }

  const rows = invoiceDetail.dsVe.map(ticket => `
    <tr>
      <td>${escapeHtml(ticket.tenGhe || '-')}</td>
      <td>${escapeHtml(ticket.tenLoaiGhe || '-')}</td>
      <td>${formatCurrency(ticket.giaVeCoBan || 0)}</td>
      <td>${formatCurrency(ticket.phuThu || 0)}</td>
      <td>${formatCurrency(ticket.thanhTien || 0)}</td>
    </tr>
  `).join('');

  return `
    <table class="hd-table">
      <thead>
        <tr>
          <th>Ghe</th>
          <th>Loai ghe</th>
          <th>Gia co ban</th>
          <th>Phu thu</th>
          <th>Thanh tien</th>
        </tr>
      </thead>
      <tbody>${rows}</tbody>
    </table>
  `;
}

function renderActionPanel() {
  if (invoiceDetail.trangThai === 'DA_THANH_TOAN') {
    return `
      <p>Hoa don da duoc thanh toan thanh cong.</p>
      <div class="hd-actions">
        <button class="hd-btn secondary" onclick="location.href='menu.html'">Ve trang chu</button>
      </div>
    `;
  }

  if (invoiceDetail.trangThai === 'DA_HUY' || !invoiceDetail.coTheThanhToan) {
    return `
      <p>Hoa don nay khong con hieu luc thanh toan.</p>
      <div class="hd-actions">
        <button class="hd-btn secondary" onclick="location.href='DatVe.html'">Dat ve moi</button>
      </div>
    `;
  }

  return `
    <label for="paymentMethod">Phuong thuc thanh toan</label>
    <select id="paymentMethod" class="hd-payment-select">
      <option value="MOMO">MOMO</option>
      <option value="ZALOPAY">ZaloPay</option>
      <option value="TIEN_MAT">Tien mat</option>
    </select>
    <div id="countdown" class="hd-countdown"></div>
    <div class="hd-actions">
      <button class="hd-btn primary" id="payBtn" onclick="payInvoice()">Thanh toan ngay</button>
      <button class="hd-btn secondary" id="cancelBtn" onclick="cancelInvoice()">Huy hoa don</button>
    </div>
  `;
}

async function payInvoice() {
  const methodEl = document.getElementById('paymentMethod');
  const payBtn = document.getElementById('payBtn');
  const cancelBtn = document.getElementById('cancelBtn');
  const phuongThucThanhToan = methodEl ? methodEl.value : 'MOMO';

  if (payBtn) payBtn.disabled = true;
  if (cancelBtn) cancelBtn.disabled = true;

  try {
    invoiceDetail = await apiFetch(`/hoa-don/${encodeURIComponent(invoiceDetail.maDon)}/thanh-toan`, {
      method: 'POST',
      body: JSON.stringify({ phuongThucThanhToan })
    });
    showToast('Thanh toan thanh cong', 'success');
    renderInvoice();
  } catch (error) {
    showToast('Thanh toan that bai: ' + error.message, 'error');
    if (payBtn) payBtn.disabled = false;
    if (cancelBtn) cancelBtn.disabled = false;
  }
}

async function cancelInvoice() {
  if (!confirm('Ban co chac muon huy hoa don nay?')) {
    return;
  }

  const payBtn = document.getElementById('payBtn');
  const cancelBtn = document.getElementById('cancelBtn');
  if (payBtn) payBtn.disabled = true;
  if (cancelBtn) cancelBtn.disabled = true;

  try {
    invoiceDetail = await apiFetch(`/hoa-don/${encodeURIComponent(invoiceDetail.maDon)}/huy`, {
      method: 'POST'
    });
    showToast('Da huy hoa don', 'success');
    renderInvoice();
  } catch (error) {
    showToast('Khong the huy hoa don: ' + error.message, 'error');
    if (payBtn) payBtn.disabled = false;
    if (cancelBtn) cancelBtn.disabled = false;
  }
}

function startCountdown() {
  if (!invoiceDetail?.coTheThanhToan || !invoiceDetail.hanThanhToan) {
    return;
  }

  const updateCountdown = async () => {
    const countdownEl = document.getElementById('countdown');
    if (!countdownEl) return;

    const diff = new Date(invoiceDetail.hanThanhToan).getTime() - Date.now();
    if (diff <= 0) {
      clearCountdown();
      countdownEl.textContent = 'Hoa don da het han, dang cap nhat...';
      await loadInvoice(invoiceDetail.maDon);
      return;
    }

    const totalSeconds = Math.floor(diff / 1000);
    const minutes = String(Math.floor(totalSeconds / 60)).padStart(2, '0');
    const seconds = String(totalSeconds % 60).padStart(2, '0');
    countdownEl.textContent = `Giu ghe toi: ${minutes}:${seconds}`;
  };

  updateCountdown();
  countdownTimer = setInterval(updateCountdown, 1000);
}

function clearCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
}

function getStatusLabel(status) {
  switch (status) {
    case 'DA_THANH_TOAN':
      return 'Da thanh toan';
    case 'DA_HUY':
      return 'Da huy';
    default:
      return 'Cho thanh toan';
  }
}

function getStatusClass(status) {
  switch (status) {
    case 'DA_THANH_TOAN':
      return 'paid';
    case 'DA_HUY':
      return 'cancelled';
    default:
      return 'pending';
  }
}

function getStatusIcon(status) {
  switch (status) {
    case 'DA_THANH_TOAN':
      return 'fa-circle-check';
    case 'DA_HUY':
      return 'fa-circle-xmark';
    default:
      return 'fa-clock';
  }
}

function renderInvoiceError(message, extra = '') {
  const container = document.getElementById('invoiceContent');
  if (!container) return;

  clearCountdown();
  container.innerHTML = `
    <div class="hd-card">
      <div class="empty-state">
        <i class="fas fa-exclamation-triangle"></i>
        <h3>${message}</h3>
        ${extra}
      </div>
    </div>
  `;
}
