let currentMovie = null;

function checkAuthStatus() {
    const isLoggedIn = sessionStorage.getItem('loggedInUser') !== null;
    const logoutView = document.getElementById('logoutView');
    const loginView = document.getElementById('loginView');

    if (logoutView && loginView) {
        if (isLoggedIn) {
            logoutView.style.display = 'none';
            loginView.style.display = 'flex';
        } else {
            logoutView.style.display = 'flex';
            loginView.style.display = 'none';
        }
    }
}

function handleLogout(e) {
    e.preventDefault();

    if (confirm('Bạn chắc chắn muốn đăng xuất?')) {
        sessionStorage.removeItem('loggedInUser');
        localStorage.removeItem('userData');
        localStorage.removeItem('isLoggedIn');
        checkAuthStatus();
        alert('Đã đăng xuất thành công');
    }
}

function scrollToBooking() {
    document.querySelector('#citySelect')?.scrollIntoView({ behavior: 'smooth' });
}

function renderMovieDetail() {
    const m = currentMovie;
    if (!m) return;

    document.title = `UTC Cinema – ${m.tenPhim}`;

    const bc = document.getElementById('breadcrumbMovie');
    if (bc) bc.textContent = m.tenPhim;

    let genres = 'N/A';
    if (Array.isArray(m.theLoais)) {
        if (typeof m.theLoais[0] === 'string') {
            genres = m.theLoais.join(', ');
        } else {
            genres = m.theLoais.map(t => t.tenTheLoai).join(', ');
        }
    }

    document.getElementById('movieHero').innerHTML = `
        <div class="container">
            <div class="movie-detail-inner">
                <div class="movie-detail-poster">
                    <img src="${getPosterUrl(m.poster)}" alt="${escapeHtml(m.tenPhim)}">
                </div>
                <div class="movie-detail-info">
                    <h1>${escapeHtml(m.tenPhim)}</h1>

                    <div class="detail-row">
                        <span class="detail-label">Thể loại:</span>
                        <span class="detail-value">${escapeHtml(genres)}</span>
                    </div>

                    <div class="detail-row">
                        <span class="detail-label">Thời lượng:</span>
                        <span class="detail-value">${m.thoiLuong || 'N/A'} phút</span>
                    </div>

                    <div class="detail-row">
                        <span class="detail-label">Khởi chiếu:</span>
                        <span class="detail-value">${formatDate(m.ngayKhoiChieu)}</span>
                    </div>

                    <div class="detail-row">
                        <span class="detail-label">Độ tuổi:</span>
                        <span class="detail-value">${getAgeBadge(m.doTuoiPhuHop)} ${escapeHtml(m.doTuoiPhuHop || '')}</span>
                    </div>

                    <div class="detail-row">
                        <span class="detail-label">Ngôn ngữ:</span>
                        <span class="detail-value">${escapeHtml(m.ngonNgu || 'N/A')}</span>
                    </div>

                    ${m.moTa ? `<div class="movie-desc">${escapeHtml(m.moTa)}</div>` : ''}

                    <div class="movie-detail-actions">
                        <a href="#" id="btnBuyTicket" class="btn btn-primary">
                            <i class="fas fa-ticket-alt"></i> MUA VÉ
                        </a>
                        <a href="phim.html" class="btn btn-outline">
                            <i class="fas fa-arrow-left"></i> QUAY LẠI
                        </a>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.getElementById('btnBuyTicket')?.addEventListener('click', function (e) {
        e.preventDefault();
        scrollToBooking();
    });
}

async function loadCities() {
    try {
        const cities = await apiFetch('/thanh-pho');
        const sel = document.getElementById('citySelect');
        if (!sel) return;

        cities.forEach(c => {
            const o = document.createElement('option');
            o.value = c.maThanhPho;
            o.textContent = c.tenThanhPho;
            sel.appendChild(o);
        });
    } catch (e) {
        console.error('Lỗi tải thành phố:', e);
    }
}

async function loadRapsForDetail() {
    const city = document.getElementById('citySelect')?.value;
    const sel = document.getElementById('rapSelect');
    const showtimeList = document.getElementById('showtimeList');

    if (!sel || !showtimeList) return;

    sel.innerHTML = '<option value="">-- Chọn rạp --</option>';
    showtimeList.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-calendar-alt"></i>
            <h3>Vui lòng chọn rạp để xem lịch chiếu</h3>
        </div>
    `;

    if (!city) return;

    try {
        const raps = await apiFetch('/rap?maThanhPho=' + encodeURIComponent(city));

        raps.forEach(r => {
            const o = document.createElement('option');
            o.value = r.maRap;
            o.textContent = r.tenRap;
            sel.appendChild(o);
        });
    } catch (e) {
        console.error('Lỗi tải rạp:', e);
    }
}

async function loadShowtimes() {
    const rap = document.getElementById('rapSelect')?.value;
    const date = document.getElementById('dateSelect')?.value;
    const el = document.getElementById('showtimeList');

    if (!el) return;

    if (!rap || !date || !currentMovie) {
        el.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-calendar-alt"></i>
                <h3>Chọn rạp và ngày</h3>
            </div>
        `;
        return;
    }

    el.innerHTML = `
        <div class="loading-spinner">
            <div class="spinner"></div>
        </div>
    `;

    try {
        const suats = await apiFetch(
            `/suat-chieu/tim-suat?maPhim=${encodeURIComponent(currentMovie.maPhim)}&maRap=${encodeURIComponent(rap)}&ngay=${date}`
        );

        if (!suats.length) {
            el.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-calendar-times"></i>
                    <h3>Không có suất chiếu nào cho ngày này</h3>
                </div>
            `;
            return;
        }

        el.innerHTML = `
            <div class="showtime-group">
                <div class="showtime-movie-header">
                    <img class="showtime-poster" src="${getPosterUrl(currentMovie.poster)}" alt="${escapeHtml(currentMovie.tenPhim)}">
                    <div class="showtime-movie-info">
                        <h3>${escapeHtml(currentMovie.tenPhim)}</h3>
                        <div class="movie-meta">
                            ${getAgeBadge(currentMovie.doTuoiPhuHop)}
                            <span>${currentMovie.thoiLuong || '?'} phút</span>
                        </div>
                    </div>
                </div>

                <div class="showtime-times">
                    ${suats.map(s => `
                        <a href="dat-ve.html?suat=${encodeURIComponent(s.maSuat)}" class="showtime-btn">
                            ${formatTime(s.gioChieu)}
                            <span class="seats-left">
                                ${s.soGheTrong != null ? s.soGheTrong + ' ghế trống' : ''}
                            </span>
                        </a>
                    `).join('')}
                </div>
            </div>
        `;
    } catch (e) {
        console.error('Lỗi tải lịch chiếu:', e);
        el.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Lỗi tải lịch chiếu</h3>
            </div>
        `;
    }
}

async function initMovieDetailPage() {
    checkAuthStatus();

    if (typeof initPage === 'function') {
        initPage('phim');
    }

    const citySelect = document.getElementById('citySelect');
    const rapSelect = document.getElementById('rapSelect');
    const dateSelect = document.getElementById('dateSelect');

    citySelect?.addEventListener('change', loadRapsForDetail);
    rapSelect?.addEventListener('change', loadShowtimes);
    dateSelect?.addEventListener('change', loadShowtimes);

    if (dateSelect && typeof formatISODate === 'function') {
        dateSelect.value = formatISODate(new Date());
    }

    const params = new URLSearchParams(location.search);
    const id = params.get('id');

    if (!id) {
        window.location.href = 'phim.html';
        return;
    }

    try {
        currentMovie = await apiFetch('/phim/' + encodeURIComponent(id));
        renderMovieDetail();
        await loadCities();
    } catch (e) {
        console.error('Lỗi tải chi tiết phim:', e);
        document.getElementById('movieHero').innerHTML = `
            <div class="container">
                <div class="empty-state movie-not-found">
                    <i class="fas fa-exclamation-triangle"></i>
                    <h3>Không tìm thấy phim</h3>
                </div>
            </div>
        `;
    }
}

document.addEventListener('DOMContentLoaded', initMovieDetailPage);