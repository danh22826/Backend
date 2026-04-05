/* ============================================================
   UTC CINEMA – Home Page Logic
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {
  if (typeof initPage === 'function') {
    initPage('home');
  }
  loadBannerSlider();
  loadMoviesHome();
  loadQuickBooking();

  const qbDate = document.getElementById('qbDate');
  if (qbDate && typeof formatISODate === 'function') {
    qbDate.value = formatISODate(new Date());
  }
});

/* ——— HERO SLIDER ——— */
let heroIdx = 0, heroTimer;

function loadBannerSlider() {
  const slides = document.querySelectorAll('.hero-slide');
  const dotsEl = document.getElementById('heroDots');
  if (!dotsEl) return;

  slides.forEach((_, i) => {
    const dot = document.createElement('span');
    dot.className = 'hero-dot' + (i === 0 ? ' active' : '');
    dot.onclick = () => goHero(i);
    dotsEl.appendChild(dot);
  });
  autoHero();
}

function goHero(i) {
  const slides = document.querySelectorAll('.hero-slide');
  if (!slides.length) return;

  heroIdx = ((i % slides.length) + slides.length) % slides.length;
  const slider = document.getElementById('heroSlider');
  if (slider) {
    slider.style.transform = `translateX(-${heroIdx * 100}%)`;
  }

  document.querySelectorAll('.hero-dot').forEach((d, j) => d.classList.toggle('active', j === heroIdx));
}

function heroSlide(dir) {
  clearInterval(heroTimer);
  goHero(heroIdx + dir);
  autoHero();
}

function autoHero() {
  heroTimer = setInterval(() => goHero(heroIdx + 1), 5000);
}

/* ——— MOVIES ——— */
async function loadMoviesHome() {
  try {
    const movies = await apiFetch('/phim');
    const grid = document.getElementById('movieGrid');
    if (!grid) return;

    if (!movies || !movies.length) {
      grid.innerHTML = '<div class="empty-state"><i class="fas fa-film"></i><h3>Chưa có phim nào</h3></div>';
      return;
    }

    const LIMIT_HOME = 10;
    grid.innerHTML = movies.slice(0, LIMIT_HOME).map(m => `
      <a href="chitietphim.html?id=${encodeURIComponent(m.maPhim)}" class="movie-card">
        <div class="movie-card-poster">
          <img
            src="${getPosterUrl(m.poster)}"
            alt="${escapeHtml(m.tenPhim)}"
            loading="lazy"
            onerror="this.onerror=null;this.src='http://localhost:8080/poster/default.jpg';"
          >
          <div class="movie-card-overlay">
            <span class="btn btn-primary">MUA VÉ</span>
          </div>
        </div>
        <div class="movie-card-body">
          <h4>${escapeHtml(m.tenPhim)}</h4>
          <div class="card-meta">
            ${getAgeBadge(m.doTuoiPhuHop)}
            <span>${m.thoiLuong || '?'} phút</span>
          </div>
        </div>
      </a>
    `).join('');

  } catch (e) {
    console.error("Home Movie Load Error:", e);
  }
}

/* ——— QUICK BOOKING BAR ——— */
async function loadQuickBooking() {
  try {
    const cities = await apiFetch('/thanh-pho');
    const sel = document.getElementById('qbCity');
    if (!sel) return;

    cities.forEach(c => {
      const o = document.createElement('option');
      o.value = c.maThanhPho;
      o.textContent = c.tenThanhPho;
      sel.appendChild(o);
    });
  } catch(e) { console.error("Quick Booking Load Cities Error:", e); }
}

async function qbLoadRaps() {
  const city = document.getElementById('qbCity').value;
  const sel = document.getElementById('qbRap');
  if (!sel) return;

  sel.innerHTML = '<option value="">-- Chọn rạp --</option>';
  if (!city) return;

  try {
    const raps = await apiFetch('/rap?maThanhPho=' + encodeURIComponent(city));
    raps.forEach(r => {
      const o = document.createElement('option');
      o.value = r.maRap;
      o.textContent = r.tenRap;
      sel.appendChild(o);
    });
  } catch(e) { console.error("Quick Booking Load Raps Error:", e); }
}

async function qbLoadMovies() {
  const rap = document.getElementById('qbRap').value;
  const date = document.getElementById('qbDate').value;
  const sel = document.getElementById('qbMovie');
  if (!sel) return;

  sel.innerHTML = '<option value="">-- Chọn phim --</option>';
  if (!rap || !date) return;

  try {
    const movies = await apiFetch(`/suat-chieu/tim-phim?maRap=${encodeURIComponent(rap)}&ngay=${date}`);
    movies.forEach(m => {
      const o = document.createElement('option');
      o.value = m.maPhim;
      o.textContent = m.tenPhim;
      sel.appendChild(o);
    });
  } catch(e) { console.error("Quick Booking Load Movies Error:", e); }
}

function qbGo() {
  const rap = document.getElementById('qbRap').value;
  const date = document.getElementById('qbDate').value;
  const movie = document.getElementById('qbMovie').value;

  if (!rap) {
    if (typeof showToast === 'function') showToast('Vui lòng chọn rạp','error');
    return;
  }

  let url = `DatVe.html?rap=${encodeURIComponent(rap)}&ngay=${date}`;
  if (movie) url += `&phim=${encodeURIComponent(movie)}`;
  window.location.href = url;
}