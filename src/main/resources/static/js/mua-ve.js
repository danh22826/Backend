/* ============================================================
   MUA VÉ – Page Logic
   Flow: Chọn ngày → Chọn khu vực → Chọn rạp → Chọn phim → Xem giờ chiếu
   ============================================================ */

/* ---------- State ---------- */
let cities        = [];
let selectedCity  = null;   // maThanhPho
let selectedRap   = null;   // { maRap, tenRap }
let selectedPhim  = null;   // { maPhim, tenPhim }
let selectedDate  = formatISODate(new Date());
let calStart      = new Date();
let rapType       = 'all';  // 'all' | 'special'
let phimTab       = 'popular';

/* ---------- Init ---------- */
document.addEventListener('DOMContentLoaded', async () => {
  initPage('mua-ve');

  const params = new URLSearchParams(location.search);
  if (params.get('ngay')) {
    selectedDate = params.get('ngay');
    calStart = new Date(selectedDate);
  }

  renderCalendar();
  updateStatusDate();
  await loadCities();

  if (params.get('rap')) {
    await autoSelectRap(params.get('rap'));
  }
});

/* ============================================================
   HELPERS
   ============================================================ */
async function apiGet(url) {
  try {
    const data = await apiFetch(url);
    return Array.isArray(data) ? data : (data ? [data] : []);
  } catch (e) {
    console.error('API Error:', url, e.message);
    return [];
  }
}

function getPosterSafe(poster) {
  if (!poster) return 'https://via.placeholder.com/60x90?text=No+Image';
  return getPosterUrl(poster);
}

function formatTimeSafe(t) {
  if (!t) return '--:--';
  return String(t).slice(0, 5);
}

function toJsParam(value) {
  return JSON.stringify(String(value ?? ''));
}

/* ============================================================
   CALENDAR
   ============================================================ */
function renderCalendar() {
  const container = document.getElementById('calDays');
  if (!container) return;
  container.innerHTML = '';

  const start = new Date(calStart);
  start.setDate(start.getDate() - start.getDay());

  const MONTHS = ['January','February','March','April','May','June',
                  'July','August','September','October','November','December'];

  for (let i = 0; i < 14; i++) {
    const d = new Date(start);
    d.setDate(start.getDate() + i);
    const iso = formatISODate(d);
    const dow = d.getDay();

    let cls = 'cal-day';
    if (iso === selectedDate) cls += ' active';
    if (dow === 0) cls += ' sunday';
    if (dow === 6) cls += ' saturday';

    if (i === 0 || d.getDate() === 1) {
      container.innerHTML += `
        <div class="cal-month">
          ${d.getMonth() + 1}
          <small>${d.getFullYear()} ${MONTHS[d.getMonth()]}</small>
        </div>`;
    }

    container.innerHTML += `
      <button class="${cls}" onclick="selectDate('${iso}')">
        <span class="day-name">${getDayName(d)}</span>
        <span class="day-num">${d.getDate()}</span>
      </button>`;
  }
}

function selectDate(iso) {
  selectedDate = iso;
  renderCalendar();
  updateStatusDate();
  // Nếu đã chọn rạp + phim thì tải lại giờ chiếu
  if (selectedRap && selectedPhim) loadShowtimes();
  // Nếu chỉ có rạp thì reload danh sách phim
  else if (selectedRap) loadPhimForRap();
}

function calShift(days) {
  calStart.setDate(calStart.getDate() + days);
  renderCalendar();
}

/* ============================================================
   CITIES / KUVỰC
   ============================================================ */
async function loadCities() {
  const el = document.getElementById('regionTabs');
  if (!el) return;
  el.innerHTML = '<div class="loading-spinner"><div class="spinner"></div></div>';

  cities = await apiGet('/thanh-pho');

  if (!cities.length) {
    el.innerHTML = '<p class="mv-text-danger" style="padding:16px;">Không tải được danh sách khu vực</p>';
    return;
  }
  renderRegionList();
}

function renderRegionList() {
  const el = document.getElementById('regionTabs');
  if (!el) return;

  el.innerHTML = cities.map(c => `
    <button
      class="mv-region-item${selectedCity === c.maThanhPho ? ' active' : ''}"
      onclick='selectCity(${toJsParam(c.maThanhPho)})'>
      ${escapeHtml(c.tenThanhPho)}
    </button>
  `).join('');
}

async function selectCity(maTP) {
  selectedCity = maTP;
  selectedRap  = null;
  selectedPhim = null;

  renderRegionList();
  clearPhimList();
  hideShowtimes();
  updateStatusRap(null);
  updateStatusPhim(null);

  const el = document.getElementById('cinemaList');
  if (el) el.innerHTML = '<div class="loading-spinner"><div class="spinner"></div></div>';

  const raps = await apiGet(`/rap?maThanhPho=${encodeURIComponent(maTP)}`);

  if (!el) return;
  if (!raps.length) {
    el.innerHTML = '<p class="mv-text-muted" style="padding:12px 16px;">Chưa có rạp nào tại khu vực này</p>';
    return;
  }

  el.innerHTML = raps.map(r => `
    <button
      class="mv-cinema-chip${selectedRap?.maRap === r.maRap ? ' active' : ''}"
      data-ma-rap="${escapeHtml(r.maRap)}"
      onclick='selectRap(${toJsParam(r.maRap)}, ${toJsParam(r.tenRap)})'>
      <i class="fas fa-check mv-chip-check"></i>
      ${escapeHtml(r.tenRap)}
    </button>
  `).join('');
}

async function autoSelectRap(maRap) {
  for (const c of cities) {
    const raps = await apiGet(`/rap?maThanhPho=${encodeURIComponent(c.maThanhPho)}`);
    const found = raps.find(r => r.maRap === maRap);
    if (found) {
      await selectCity(c.maThanhPho);
      await selectRap(maRap, found.tenRap);
      return;
    }
  }
}

/* ============================================================
   RAP
   ============================================================ */
async function selectRap(maRap, tenRap) {
  selectedRap  = { maRap, tenRap };
  selectedPhim = null;

  // Cập nhật chip active
  document.querySelectorAll('.mv-cinema-chip').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.maRap === maRap);
  });

  updateStatusRap(tenRap);
  updateStatusPhim(null);
  hideShowtimes();
  await loadPhimForRap();
}

function switchRapType(btn, type) {
  rapType = type;
  document.querySelectorAll('.mv-type-tab').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  if (selectedRap) loadPhimForRap();
}

function clearRap() {
  selectedRap  = null;
  selectedPhim = null;
  updateStatusRap(null);
  updateStatusPhim(null);
  clearPhimList();
  hideShowtimes();
  document.querySelectorAll('.mv-cinema-chip').forEach(b => b.classList.remove('active'));
}

/* ============================================================
   PHIM
   ============================================================ */
async function loadPhimForRap() {
  const el = document.getElementById('phimList');
  if (!el || !selectedRap) return;

  el.innerHTML = '<div class="loading-spinner"><div class="spinner"></div></div>';

  // Lấy phim có suất chiếu tại rạp + ngày này
  const movies = await apiGet(
    `/suat-chieu/tim-phim?maRap=${encodeURIComponent(selectedRap.maRap)}&ngay=${selectedDate}`
  );

  // Lấy tất cả phim để hiển thị phim mờ (không có suất)
  const allMovies = await apiGet('/phim');

  if (!allMovies.length) {
    el.innerHTML = '<div class="mv-phim-empty">Không tải được danh sách phim</div>';
    return;
  }

  const activeIds = new Set(movies.map(m => m.maPhim));

  // Phim có suất lên đầu, phim không có suất mờ phía sau
  const sorted = [
    ...allMovies.filter(m => activeIds.has(m.maPhim)),
    ...allMovies.filter(m => !activeIds.has(m.maPhim)),
  ];

  el.innerHTML = sorted.map(m => {
    const hasShowtime = activeIds.has(m.maPhim);
    const isSelected  = selectedPhim?.maPhim === m.maPhim;
    let cls = 'mv-phim-item';
    if (!hasShowtime) cls += ' dimmed';
    if (isSelected)   cls += ' active';

    return `
      <button class="${cls}"
        data-ma-phim="${escapeHtml(m.maPhim)}"
        ${hasShowtime ? `onclick='selectPhim(${toJsParam(m.maPhim)}, ${toJsParam(m.tenPhim)})'` : 'disabled'}
      >
        ${getAgeBadge(m.doTuoiPhuHop)}
        <span>${escapeHtml(m.tenPhim)}</span>
      </button>`;
  }).join('');
}

async function selectPhim(maPhim, tenPhim) {
  selectedPhim = { maPhim, tenPhim };

  // Cập nhật active trong danh sách
  document.querySelectorAll('.mv-phim-item').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.maPhim === maPhim);
  });

  updateStatusPhim(tenPhim);
  await loadShowtimes();
}

function switchPhimTab(btn, tab) {
  phimTab = tab;
  document.querySelectorAll('.mv-phim-tab').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  if (selectedRap) loadPhimForRap();
}

function clearPhimList() {
  const el = document.getElementById('phimList');
  if (el) el.innerHTML = '<div class="mv-phim-empty">Chọn rạp để xem phim đang chiếu</div>';
}

function clearPhim() {
  selectedPhim = null;
  updateStatusPhim(null);
  hideShowtimes();
  document.querySelectorAll('.mv-phim-item').forEach(b => b.classList.remove('active'));
}

/* ============================================================
   SHOWTIME
   ============================================================ */
async function loadShowtimes() {
  if (!selectedRap || !selectedPhim) return;

  const section = document.getElementById('showtimeSection');
  const results = document.getElementById('showtimeResults');
  if (!section || !results) return;

  section.style.display = 'block';
  section.scrollIntoView({ behavior: 'smooth', block: 'start' });
  results.innerHTML = '<div class="loading-spinner"><div class="spinner"></div></div>';

  const suats = await apiGet(
    `/suat-chieu/tim-suat?maPhim=${encodeURIComponent(selectedPhim.maPhim)}&maRap=${encodeURIComponent(selectedRap.maRap)}&ngay=${selectedDate}`
  );

  if (!suats.length) {
    results.innerHTML = `
      <div class="empty-state" style="padding:40px 24px;">
        <i class="fas fa-calendar-times"></i>
        <h3>Không có suất chiếu cho ngày này</h3>
      </div>`;
    return;
  }

  // Render header phim
  const phimInfo = suats[0]; // lấy thông tin phim từ suất đầu tiên nếu API trả về
  results.innerHTML = renderShowtimeBlock(selectedPhim, suats);
}

function renderShowtimeBlock(phim, suats) {
  // Nhóm suất theo phòng
  const groups = {};
  suats.forEach(s => {
    const key = s.maPhong || s.tenPhong || 'default';
    if (!groups[key]) groups[key] = { tenPhong: s.tenPhong || s.maPhong, suats: [] };
    groups[key].suats.push(s);
  });

  const roomsHTML = Object.values(groups).map(g => {
    const cols = g.suats.map(s => `<th>${escapeHtml(g.tenPhong || '')}</th>`).join('');
    const times = g.suats.map(s => `
      <td>
        <a class="mv-st-time-link"
           href="dat-ve.html?suat=${encodeURIComponent(s.maSuat)}&phong=${encodeURIComponent(s.maPhong || '')}">
          ${formatTimeSafe(s.gioChieu)}
        </a>
        <div class="mv-st-seats">
          ${s.soGheTrong != null ? `${s.soGheTrong} / ${s.tongGhe || '?'} Ghế ngồi` : ''}
        </div>
      </td>`).join('');

    return `
      <div class="mv-st-room-group">
        <table class="mv-st-table">
          <thead><tr>${cols}</tr></thead>
          <tbody><tr>${times}</tr></tbody>
        </table>
      </div>`;
  }).join('');

  return `
    <div class="mv-st-movie-block">
      <a class="mv-st-movie-title"
         href="chitietphim.html?id=${encodeURIComponent(phim.maPhim)}">
        ${getAgeBadge('')}
        ${escapeHtml(phim.tenPhim)}
        <i class="fas fa-chevron-right" style="font-size:.75rem;margin-left:4px;"></i>
      </a>

      <div class="mv-st-room-meta">
        <span>${escapeHtml(selectedRap?.tenRap || '')}</span>
      </div>

      ${roomsHTML}
    </div>`;
}

function hideShowtimes() {
  const section = document.getElementById('showtimeSection');
  if (section) section.style.display = 'none';
}

function filterShowtimes() {
  // Có thể mở rộng: lọc theo loại phòng
}

/* ============================================================
   STATUS BAR
   ============================================================ */
function updateStatusDate() {
  const el = document.getElementById('statusDate');
  if (!el) return;
  const d = new Date(selectedDate);
  const days = ['CN','Hai','Ba','Tư','Năm','Sáu','Bảy'];
  el.textContent = `${selectedDate} (${days[d.getDay()]})`;
}

function updateStatusRap(tenRap) {
  const chip  = document.getElementById('statusRapChip');
  const name  = document.getElementById('statusRapName');
  const empty = document.getElementById('statusRapEmpty');
  if (!chip) return;
  if (tenRap) {
    name.textContent = tenRap;
    chip.style.display  = 'inline-flex';
    empty.style.display = 'none';
  } else {
    chip.style.display  = 'none';
    empty.style.display = 'inline';
  }
}

function updateStatusPhim(tenPhim) {
  const chip  = document.getElementById('statusPhimChip');
  const name  = document.getElementById('statusPhimName');
  const empty = document.getElementById('statusPhimEmpty');
  if (!chip) return;
  if (tenPhim) {
    name.textContent = tenPhim;
    chip.style.display  = 'inline-flex';
    empty.style.display = 'none';
  } else {
    chip.style.display  = 'none';
    empty.style.display = 'inline';
  }
}
