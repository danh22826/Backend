let allMovies = [];
let allGenres = [];
const PER_PAGE = 15;
let currentPage = 1;

document.addEventListener('DOMContentLoaded', async () => {
  initPage('phim');

  try {
    [allMovies, allGenres] = await Promise.all([
      apiFetch('/phim'),
      apiFetch('/the-loai'),
    ]);

    const gSel = document.getElementById('genreFilter');
    allGenres.forEach(g => {
      const o = document.createElement('option');
      o.value = g.maTheLoai;
      o.textContent = g.tenTheLoai;
      gSel.appendChild(o);
    });

    renderMovies();
  } catch (e) {
    console.error(e);
    document.getElementById('movieGrid').innerHTML =
      '<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Không thể tải dữ liệu phim</h3></div>';
  }
});

function filterMovies() {
  currentPage = 1;
  renderMovies();
}

function getFiltered() {
  const q = document.getElementById('searchInput').value.trim().toLowerCase();
  const g = document.getElementById('genreFilter').value;

  return allMovies.filter(m => {
    const matchQ = !q || (m.tenPhim && m.tenPhim.toLowerCase().includes(q));
    const matchG = !g || (
      m.theLoais &&
      m.theLoais.some(t => String(t).toLowerCase() === g.toLowerCase())
    );
    return matchQ && matchG;
  });
}

function getPosterUrl(poster) {
  if (!poster || String(poster).trim() === '') {
    return 'http://localhost:8080/poster/default.jpg';
  }

  const cleanPoster = String(poster).trim();

  if (cleanPoster.startsWith('http://') || cleanPoster.startsWith('https://')) {
    return cleanPoster;
  }

  if (cleanPoster.startsWith('/')) {
    return `http://localhost:8080${cleanPoster}`;
  }

  return `http://localhost:8080/poster/${cleanPoster}`;
}

function renderMovies() {
  const filtered = getFiltered();
  const grid = document.getElementById('movieGrid');

  if (!filtered.length) {
    grid.innerHTML = '<div class="empty-state"><i class="fas fa-search"></i><h3>Không tìm thấy phim nào</h3></div>';
    document.getElementById('pagination').innerHTML = '';
    return;
  }

  const total = Math.ceil(filtered.length / PER_PAGE);
  const start = (currentPage - 1) * PER_PAGE;
  const page = filtered.slice(start, start + PER_PAGE);

  grid.innerHTML = page.map(m => `
    <div class="movie-card">
      <div class="movie-card-poster">
        <img
          src="${getPosterUrl(m.poster)}"
          alt="${escapeHtml(m.tenPhim)}"
          loading="lazy"
          onerror="this.onerror=null;this.src='http://localhost:8080/poster/default.jpg';"
        >
        <div class="movie-card-overlay">
          <a href="chi-tiet-phim.html?id=${encodeURIComponent(m.maPhim)}" class="btn btn-primary">XEM CHI TIẾT</a>
          <a href="chi-tiet-phim.html?id=${encodeURIComponent(m.maPhim)}#suat-chieu" class="btn btn-primary">MUA VÉ</a>
        </div>
      </div>
      <div class="movie-card-body">
        <a href="chi-tiet-phim.html?id=${encodeURIComponent(m.maPhim)}" class="movie-card-title-link">
          <h4>${escapeHtml(m.tenPhim)}</h4>
        </a>
        <div class="card-meta">
          ${getAgeBadge(m.doTuoiPhuHop)}
          <span>${m.thoiLuong || '?'} phút</span>
          ${m.ngayKhoiChieu ? `<span>· ${formatDate(m.ngayKhoiChieu)}</span>` : ''}
        </div>
      </div>
    </div>
  `).join('');

  renderPagination(total);
}

function renderPagination(total) {
  const pagEl = document.getElementById('pagination');

  if (total <= 1) {
    pagEl.innerHTML = '';
    return;
  }

  let html = '';
  for (let i = 1; i <= total; i++) {
    html += `
      <button
        class="btn ${i === currentPage ? 'btn-primary' : 'btn-outline'} movie-page-btn"
        onclick="goPage(${i})"
      >
        ${i}
      </button>
    `;
  }

  pagEl.innerHTML = html;
}

function goPage(p) {
  currentPage = p;
  renderMovies();
  window.scrollTo({ top: 200, behavior: 'smooth' });
}