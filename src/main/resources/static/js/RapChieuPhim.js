/* ============================================================
   UTC CINEMA – Cinema List Page Logic
   ============================================================ */

let allCities = [], allRaps = [];

document.addEventListener('DOMContentLoaded', async () => {
  // Khởi tạo trang từ shared.js
  if (typeof initPage === 'function') {
    initPage('rap');
  }

  try {
    // 1. Lấy danh sách thành phố
    allCities = await apiFetch('/thanh-pho');

    // 2. Render bộ lọc thành phố
    renderCityFilter();

    // 3. Tải tất cả rạp của các thành phố
    await loadAllCinemas();

    // 4. Hiển thị danh sách rạp ban đầu
    renderRaps(allRaps);

  } catch (e) {
    console.error("Cinema Load Error:", e);
    const listEl = document.getElementById('rapList');
    if (listEl) {
      listEl.innerHTML = '<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Lỗi tải dữ liệu rạp</h3></div>';
    }
  }
});

/**
 * Hiển thị các nút lọc thành phố
 */
function renderCityFilter() {
  const filter = document.getElementById('cityFilter');
  if (!filter) return;

  // Giữ lại nút "Tất cả" và thêm các thành phố từ API
  allCities.forEach(c => {
    const btn = document.createElement('button');
    btn.className = 'city-btn';
    btn.textContent = c.tenThanhPho;
    btn.onclick = (e) => filterCity(c.maThanhPho, e.target);
    filter.appendChild(btn);
  });
}

/**
 * Tải danh sách rạp của tất cả thành phố cùng lúc
 */
async function loadAllCinemas() {
  const rapPromises = allCities.map(c =>
    apiFetch('/rap?maThanhPho=' + encodeURIComponent(c.maThanhPho))
      .then(raps => raps.map(r => ({ ...r, city: c })))
      .catch(() => []) // Nếu lỗi một thành phố thì trả về mảng rỗng
  );

  const results = await Promise.all(rapPromises);
  allRaps = results.flat();
}

/**
 * Lọc rạp theo thành phố được chọn
 */
function filterCity(maTP, btn) {
  // Cập nhật trạng thái nút active
  document.querySelectorAll('.city-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');

  // Lọc dữ liệu
  if (!maTP) {
    renderRaps(allRaps);
  } else {
    renderRaps(allRaps.filter(r => r.city?.maThanhPho === maTP));
  }
}

/**
 * Hiển thị danh sách rạp ra màn hình
 */
async function renderRaps(raps) {
  const el = document.getElementById('rapList');
  if (!el) return;

  if (!raps.length) {
    el.innerHTML = '<div class="empty-state"><i class="fas fa-building"></i><h3>Không có rạp nào tại khu vực này</h3></div>';
    return;
  }

  // Tạm thời hiển thị khung loading trong khi fetch phòng chiếu
  el.innerHTML = '<div class="loading-spinner"><div class="spinner"></div></div>';

  let html = '';
  // Duyệt qua từng rạp để lấy thêm thông tin phòng chiếu (nếu cần)
  const cards = await Promise.all(raps.map(async (r) => {
    let roomsHtml = '';
    try {
      const rooms = await apiFetch('/phong-chieu?maRap=' + encodeURIComponent(r.maRap));
      if (rooms && rooms.length) {
        roomsHtml = `<div class="rap-rooms">${rooms.map(rm => `<span class="room-chip">${escapeHtml(rm.tenPhong)}</span>`).join('')}</div>`;
      }
    } catch (e) {
      console.warn(`Could not load rooms for cinema ${r.maRap}`);
    }

    return `
      <div class="rap-card">
        <div class="rap-card-header">
          <i class="fas fa-building"></i>
          <h3>${escapeHtml(r.tenRap)}</h3>
        </div>
        <div class="rap-card-body">
          <div class="rap-detail-row">
            <i class="fas fa-map-marker-alt"></i>
            <span>${escapeHtml(r.diaChi || 'Chưa cập nhật')}</span>
          </div>
          <div class="rap-detail-row">
            <i class="fas fa-city"></i>
            <span>${escapeHtml(r.city?.tenThanhPho || '')}</span>
          </div>
          ${roomsHtml}
        </div>
        <div class="rap-card-actions">
          <a href="DatVe.html?rap=${encodeURIComponent(r.maRap)}" class="btn btn-primary">Xem lịch chiếu</a>
          <a href="DatVe.html?rap=${encodeURIComponent(r.maRap)}" class="btn btn-outline">Mua vé</a>
        </div>
      </div>`;
  }));

  el.innerHTML = cards.join('');
}

/**
 * Hàm hỗ trợ tránh lỗi XSS
 */
function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}