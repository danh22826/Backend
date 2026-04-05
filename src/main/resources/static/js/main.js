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

    const genreSelect = document.getElementById('genreFilter');
    allGenres.forEach((genre) => {
      const option = document.createElement('option');
      option.value = genre.tenTheLoai;
      option.dataset.genreCode = genre.maTheLoai;
      option.textContent = genre.tenTheLoai;
      genreSelect.appendChild(option);
    });

    renderMovies();
  } catch (error) {
    console.error(error);
    document.getElementById('movieGrid').innerHTML =
      '<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Không thể tải dữ liệu phim</h3></div>';
  }
});

function filterMovies() {
  currentPage = 1;
  renderMovies();
}

function getFiltered() {
  const keyword = normalizeText(document.getElementById('searchInput').value);
  const genre = normalizeText(document.getElementById('genreFilter').value);

  return allMovies.filter((movie) => {
    const movieTitle = normalizeText(movie.tenPhim);
    const movieGenres = Array.isArray(movie.theLoais)
      ? movie.theLoais.map((item) => normalizeText(item))
      : [];

    const matchKeyword = !keyword || movieTitle.includes(keyword);
    const matchGenre = !genre || movieGenres.includes(genre);

    return matchKeyword && matchGenre;
  });
}

function normalizeText(value) {
  return String(value ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/\u0111/g, 'd')
    .replace(/\u0110/g, 'd')
    .toLowerCase()
    .replace(/\s+/g, ' ')
    .trim();
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

  const totalPages = Math.ceil(filtered.length / PER_PAGE);
  const start = (currentPage - 1) * PER_PAGE;
  const moviesOnPage = filtered.slice(start, start + PER_PAGE);
  const buildDetailUrl = (movieId) => `chitietphim.html?id=${encodeURIComponent(movieId)}`;

  grid.innerHTML = moviesOnPage.map((movie) => `
    <div class="movie-card">
      <div class="movie-card-poster">
        <img
          src="${getPosterUrl(movie.poster)}"
          alt="${escapeHtml(movie.tenPhim)}"
          loading="lazy"
          onerror="this.onerror=null;this.src='http://localhost:8080/poster/default.jpg';"
        >
        <div class="movie-card-overlay">
          <a href="${buildDetailUrl(movie.maPhim)}" class="btn btn-primary">XEM CHI TIẾT</a>
          <a href="${buildDetailUrl(movie.maPhim)}#showtimeSection" class="btn btn-primary">MUA VÉ</a>
        </div>
      </div>
      <div class="movie-card-body">
        <a href="${buildDetailUrl(movie.maPhim)}" class="movie-card-title-link">
          <h4>${escapeHtml(movie.tenPhim)}</h4>
        </a>
        <div class="card-meta">
          ${getAgeBadge(movie.doTuoiPhuHop)}
          <span>${movie.thoiLuong || '?'} phút</span>
          ${movie.ngayKhoiChieu ? `<span>· ${formatDate(movie.ngayKhoiChieu)}</span>` : ''}
        </div>
      </div>
    </div>
  `).join('');

  renderPagination(totalPages);
}

function renderPagination(totalPages) {
  const paginationElement = document.getElementById('pagination');

  if (totalPages <= 1) {
    paginationElement.innerHTML = '';
    return;
  }

  let html = '';
  for (let page = 1; page <= totalPages; page += 1) {
    html += `
      <button
        class="btn ${page === currentPage ? 'btn-primary' : 'btn-outline'} movie-page-btn"
        onclick="goPage(${page})"
      >
        ${page}
      </button>
    `;
  }

  paginationElement.innerHTML = html;
}

function goPage(page) {
  currentPage = page;
  renderMovies();
  window.scrollTo({ top: 200, behavior: 'smooth' });
}
