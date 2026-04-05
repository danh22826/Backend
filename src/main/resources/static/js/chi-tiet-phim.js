let currentMovieId = null;

document.addEventListener('DOMContentLoaded', async () => {
  if (typeof initPage === 'function') {
    initPage('phim');
  }

  currentMovieId = new URLSearchParams(window.location.search).get('id');

  if (!currentMovieId) {
    renderPageError('Không tìm thấy mã phim trên URL.');
    return;
  }

  try {
    const [movieData, allShowtimes] = await Promise.all([
      apiFetch(`/phim/${currentMovieId}`),
      apiFetch('/suat-chieu')
    ]);

    renderMovieDetails(movieData);
    await renderShowtimeBoard(allShowtimes, movieData);
  } catch (error) {
    console.error(error);
    renderPageError('Không thể tải dữ liệu phim.');
  }
});

function renderMovieDetails(movie) {
  document.title = `UTC Cinema - ${movie.tenPhim || 'Chi tiết phim'}`;

  const posterUrl = getPosterUrlSafe(movie.poster);
  const genresText = Array.isArray(movie.theLoais) && movie.theLoais.length
    ? movie.theLoais.join(', ')
    : 'Đang cập nhật';
  const durationText = movie.thoiLuong ? `${movie.thoiLuong} phút` : 'Đang cập nhật';
  const releaseText = movie.ngayKhoiChieu ? formatDate(movie.ngayKhoiChieu) : 'Đang cập nhật';
  const ageText = movie.doTuoiPhuHop ? `[Trong nước] (${movie.doTuoiPhuHop})` : 'Đang cập nhật';
  const extraText = [
    movie.ngonNgu ? `Ngôn ngữ: ${movie.ngonNgu}` : null,
    Array.isArray(movie.theLoais) && movie.theLoais.length ? `Thể loại: ${movie.theLoais.join(' | ')}` : null
  ].filter(Boolean).join(' | ') || 'Đang cập nhật';

  document.getElementById('movieTitle').textContent = movie.tenPhim || 'Đang cập nhật';
  document.getElementById('movieAge').textContent = ageText;
  document.getElementById('movieRelease').textContent = releaseText;
  document.getElementById('movieBasicInfo').textContent = `${genresText} (${durationText})`;
  document.getElementById('movieExtraInfo').textContent = extraText;
  document.getElementById('movieSummary').textContent = movie.moTa || 'Chưa có mô tả phim.';

  const moviePoster = document.getElementById('moviePoster');
  const trailerThumb = document.getElementById('trailerThumb');

  if (moviePoster) {
    moviePoster.onerror = () => {
      moviePoster.onerror = null;
      moviePoster.src = getPosterUrlSafe(null);
    };
    moviePoster.src = posterUrl;
  }

  if (trailerThumb) {
    trailerThumb.onerror = () => {
      trailerThumb.onerror = null;
      trailerThumb.src = getPosterUrlSafe(null);
    };
    trailerThumb.src = posterUrl;
  }

  const bookBtn = document.getElementById('bookTicketBtn');
  if (bookBtn) {
    bookBtn.href = '#showtimeSection';
  }

  renderTrailer(movie.trailerUrl || movie.trailer, posterUrl, movie.tenPhim || 'Trailer');
}

function renderTrailer(trailerUrl, posterUrl, movieTitle) {
  const trailerStage = document.getElementById('trailerStage');
  const trailerStrip = document.querySelector('.trailer-strip');

  if (trailerStrip) {
    trailerStrip.querySelector('.trailer-link')?.remove();
  }

  if (!trailerUrl) {
    trailerStage.innerHTML = `
      <div class="trailer-placeholder">
        <i class="fab fa-youtube"></i>
        <p>Phim này hiện chưa có trailer</p>
      </div>
    `;
    return;
  }

  const embedUrl = buildYoutubeEmbedUrl(trailerUrl);

  if (embedUrl) {
    trailerStage.innerHTML = `
      <iframe
        src="${embedUrl}"
        title="Trailer ${escapeHtml(movieTitle)}"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        referrerpolicy="strict-origin-when-cross-origin"
        loading="lazy"
        allowfullscreen>
      </iframe>
    `;
    return;
  }

  trailerStage.innerHTML = `
    <div class="trailer-placeholder">
      <img src="${posterUrl}" alt="${escapeHtml(movieTitle)}" style="max-height: 220px; object-fit: contain;">
    </div>
  `;
}

function buildYoutubeEmbedUrl(url) {
  const rawUrl = String(url || '').trim();

  if (!rawUrl) {
    return null;
  }

  if (rawUrl.includes('youtube-nocookie.com/embed/')) {
    return appendYoutubeParams(rawUrl);
  }

  if (rawUrl.includes('youtube.com/embed/')) {
    return appendYoutubeParams(rawUrl.replace('youtube.com/embed/', 'youtube-nocookie.com/embed/'));
  }

  const videoId = getYoutubeId(rawUrl);
  return videoId ? `https://www.youtube-nocookie.com/embed/${videoId}?rel=0&modestbranding=1` : null;
}

function getYoutubeId(url) {
  const rawUrl = String(url || '').trim();

  if (!rawUrl) {
    return null;
  }

  try {
    const parsed = new URL(rawUrl);
    const host = parsed.hostname.replace(/^www\./, '');

    if (host === 'youtu.be') {
      const shortId = parsed.pathname.replaceAll('/', '');
      return shortId.length === 11 ? shortId : null;
    }

    if (host === 'youtube.com' || host === 'm.youtube.com' || host === 'youtube-nocookie.com') {
      const watchId = parsed.searchParams.get('v');
      if (watchId && watchId.length === 11) {
        return watchId;
      }

      const pathParts = parsed.pathname.split('/').filter(Boolean);
      const embedIndex = pathParts.findIndex((part) => part === 'embed' || part === 'v');
      if (embedIndex >= 0 && pathParts[embedIndex + 1] && pathParts[embedIndex + 1].length === 11) {
        return pathParts[embedIndex + 1];
      }
    }
  } catch (error) {
    const regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
    const match = rawUrl.match(regExp);
    return match && match[2].length === 11 ? match[2] : null;
  }

  return null;
}

function appendYoutubeParams(url) {
  const separator = url.includes('?') ? '&' : '?';
  return `${url}${separator}rel=0&modestbranding=1`;
}

function getPosterUrlSafe(poster) {
  if (typeof getPosterUrl === 'function') {
    return getPosterUrl(poster);
  }

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

function renderPageError(message) {
  document.getElementById('movieTitle').textContent = 'Không tải được phim';
  document.getElementById('movieSummary').textContent = message;
  document.getElementById('trailerStage').innerHTML = `
    <div class="trailer-placeholder">
      <i class="fas fa-circle-exclamation"></i>
      <p>${escapeHtml(message)}</p>
    </div>
  `;

  const container = document.getElementById('danhSachSuatChieu');
  if (container) {
    container.innerHTML = `
      <div class="empty-state">
        <i class="fas fa-triangle-exclamation"></i>
        <h3>${escapeHtml(message)}</h3>
      </div>
    `;
  }
}

async function renderShowtimeBoard(allShowtimes, movie) {
  const container = document.getElementById('danhSachSuatChieu');

  if (!container) {
    return;
  }

  const movieShowtimes = Array.isArray(allShowtimes)
    ? allShowtimes
        .filter((showtime) => showtime.maPhim === currentMovieId && !isExpiredShowtime(showtime))
        .sort(compareShowtimes)
    : [];

  if (!movieShowtimes.length) {
    container.innerHTML = `
      <div class="empty-state">
        <i class="fas fa-calendar-times"></i>
        <h3>Hiện chưa có suất chiếu khả dụng cho phim này</h3>
      </div>
    `;
    return;
  }

  const groupedShowtimes = groupShowtimes(movieShowtimes);
  const sections = await Promise.all(
    groupedShowtimes.map(async (group) => {
      const details = await fetchShowtimeDetails(group);
      return renderShowtimeGroup(group, details, movie);
    })
  );

  container.innerHTML = sections.join('');
}

function groupShowtimes(showtimes) {
  const groups = new Map();

  showtimes.forEach((showtime) => {
    const key = `${showtime.ngayChieu}|${showtime.maRap}|${showtime.tenRap}`;

    if (!groups.has(key)) {
      groups.set(key, {
        ngayChieu: showtime.ngayChieu,
        maRap: showtime.maRap,
        tenRap: showtime.tenRap,
        items: []
      });
    }

    groups.get(key).items.push(showtime);
  });

  return [...groups.values()].sort((left, right) => compareShowtimes(left.items[0], right.items[0]));
}

async function fetchShowtimeDetails(group) {
  try {
    const result = await apiFetch(
      `/suat-chieu/tim-suat?maPhim=${encodeURIComponent(currentMovieId)}&maRap=${encodeURIComponent(group.maRap)}&ngay=${encodeURIComponent(group.ngayChieu)}`
    );

    if (Array.isArray(result)) {
      return result;
    }

    return result ? [result] : [];
  } catch (error) {
    console.error(error);
    return group.items.map((item) => ({
      maSuat: item.maSuat,
      gioChieu: item.gioChieu,
      tenPhong: item.tenPhong,
      tongGhe: null,
      soGheTrong: null,
      tenLoaiPhong: null,
      gia: item.gia
    }));
  }
}

function renderShowtimeGroup(group, details, movie) {
  const genres = Array.isArray(movie.theLoais) && movie.theLoais.length
    ? movie.theLoais.join(' | ')
    : 'Đang cập nhật';

  const formats = [...new Set(
    details
      .map((detail) => detail.tenLoaiPhong)
      .filter(Boolean)
  )];

  const metaParts = [
    formats.length ? formats.join(' | ') : '2D',
    movie.ngonNgu || 'Đang cập nhật',
    genres
  ];

  const cards = [...details]
    .sort((left, right) => String(left.gioChieu || '').localeCompare(String(right.gioChieu || '')))
    .map((detail) => renderShowtimeCard(detail))
    .join('');

  return `
    <section class="showtime-cinema-group">
      <div class="showtime-group-header">
        <div class="showtime-group-title">
          <h4>${escapeHtml(group.tenRap || 'Rạp chiếu')}</h4>
          <div class="showtime-group-date">${escapeHtml(formatDate(group.ngayChieu))}</div>
        </div>
        <div class="showtime-group-meta">${escapeHtml(metaParts.join(' | '))}</div>
      </div>
      <div class="showtime-slot-grid">
        ${cards}
      </div>
    </section>
  `;
}

function renderShowtimeCard(detail) {
  const seatsText = detail.tongGhe != null && detail.soGheTrong != null
    ? `${detail.soGheTrong} / ${detail.tongGhe} gh\u1EBF ng\u1ED3i`
    : 'C\u00F2n v\u00E9';

  return `
    <a href="dat-ve.html?suat=${encodeURIComponent(detail.maSuat)}" class="showtime-slot-card">
      <div class="showtime-slot-room">${escapeHtml(detail.tenPhong || 'Ph\u00F2ng chi\u1EBFu')}</div>
      <div class="showtime-slot-time">${escapeHtml(formatTime(detail.gioChieu))}</div>
      <div class="showtime-slot-seats">${escapeHtml(seatsText)}</div>
    </a>
  `;
}

function compareShowtimes(left, right) {
  return buildShowtimeDate(left) - buildShowtimeDate(right);
}

function isExpiredShowtime(showtime) {
  return buildShowtimeDate(showtime) < new Date();
}

function buildShowtimeDate(showtime) {
  return new Date(`${showtime.ngayChieu}T${normalizeTimeValue(showtime.gioChieu)}`);
}

function normalizeTimeValue(value) {
  const raw = String(value || '').trim();

  if (/^\d{2}:\d{2}:\d{2}$/.test(raw)) {
    return raw;
  }

  if (/^\d{2}:\d{2}$/.test(raw)) {
    return `${raw}:00`;
  }

  return '00:00:00';
}

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;');
}
