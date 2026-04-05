let currentMovieId = null;

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Dựng Navbar
    if (typeof renderHeader === 'function') renderHeader('phim');

    // 2. Lấy ID phim
    const params = new URLSearchParams(window.location.search);
    currentMovieId = params.get('id');

    // ĐÃ BỎ CƠ CHẾ ĐÁ VĂNG (REDIRECT) - Thay bằng chế độ xem trước
    if (!currentMovieId) {
        if (typeof showToast === 'function') showToast("Chế độ xem trước giao diện (Không có dữ liệu phim)", "info");

        const titleEl = document.getElementById('movieTitle');
        const descEl = document.getElementById('movieDesc');

        if(titleEl) titleEl.textContent = "Giao Diện Mẫu (Test)";
        if(descEl) descEl.textContent = "Bạn đang mở file trực tiếp nên hệ thống không biết bạn muốn xem phim nào. Giao diện vẫn giữ nguyên để bạn dễ dàng CSS/Thiết kế.";

        return; // Dừng code tại đây, không gọi API để tránh lỗi
    }

    // Set ngày hiện tại cho ô input Date
    const todayInput = document.getElementById('chonNgay');
    if (todayInput && typeof formatISODate === 'function') {
        todayInput.value = formatISODate(new Date());
    }

    try {
        // Tải Phim và Thành phố cùng lúc
        const [movieData, cities] = await Promise.all([
            apiFetch(`/phim/${currentMovieId}`),
            apiFetch('/thanh-pho')
        ]);

        renderMovieDetails(movieData);
        renderCities(cities);
    } catch (error) {
        console.error(error);
        if (typeof showToast === 'function') showToast("Lỗi tải dữ liệu từ Server!", "error");
    }

    // Lắng nghe sự kiện đổi bộ lọc
    const citySel = document.getElementById('chonThanhPho');
    const rapSel = document.getElementById('chonRap');
    const dateSel = document.getElementById('chonNgay');

    if (citySel) citySel.addEventListener('change', handleCityChange);
    if (rapSel) rapSel.addEventListener('change', fetchShowtimes);
    if (dateSel) dateSel.addEventListener('change', fetchShowtimes);
});

// --- RENDER DỮ LIỆU PHIM ---
function renderMovieDetails(m) {
    document.title = `UTC Cinema - ${m.tenPhim || 'Chi tiết phim'}`;

    document.getElementById('movieTitle').textContent = m.tenPhim || 'Đang cập nhật';
    document.getElementById('movieDesc').textContent = m.moTa || "Chưa có mô tả.";
    document.getElementById('movieDuration').innerHTML = `<i class="far fa-clock"></i> ${m.thoiLuong || '--'} phút`;
    document.getElementById('movieAgeBadge').textContent = m.doTuoiPhuHop || 'P';
    document.getElementById('movieLang').textContent = m.ngonNgu || "Tiếng Việt";

    if (m.ngayKhoiChieu) {
        document.getElementById('movieRelease').innerHTML = `<i class="far fa-calendar-alt"></i> ${formatDate(m.ngayKhoiChieu)}`;
    }

    const posterUrl = getPosterUrl(m.poster);
    document.getElementById('moviePoster').src = posterUrl;
    document.getElementById('movieBackdrop').style.backgroundImage = `url('${posterUrl}')`;

    if (m.theLoais && m.theLoais.length > 0) {
        document.getElementById('movieGenres').innerHTML = m.theLoais.map(g => `<span>${g}</span>`).join('');
    }

    // Render Trailer
    if (m.trailer) {
        const videoId = getYouTubeId(m.trailer);
        if (videoId) {
            document.getElementById('trailerContainer').innerHTML = `
                <iframe src="https://www.youtube.com/embed/${videoId}?autoplay=0"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowfullscreen></iframe>`;
        }
    }
}

function getYouTubeId(url) {
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    const match = url.match(regExp);
    return (match && match[2].length === 11) ? match[2] : null;
}

// --- RENDER LỊCH CHIẾU ---
function renderCities(cities) {
    const select = document.getElementById('chonThanhPho');
    if (!select) return;

    cities.forEach(c => {
        const option = document.createElement('option');
        option.value = c.maThanhPho;
        option.textContent = c.tenThanhPho;
        select.appendChild(option);
    });
}

async function handleCityChange(e) {
    const cityId = e.target.value;
    const rapSelect = document.getElementById('chonRap');
    const container = document.getElementById('danhSachSuatChieu');

    if (rapSelect) rapSelect.innerHTML = '<option value="">-- Chọn Rạp chiếu --</option>';
    if (container) container.innerHTML = '<p class="text-center text-muted mt-3">Vui lòng chọn Rạp để xem suất chiếu.</p>';

    if (!cityId) {
        if (rapSelect) rapSelect.disabled = true;
        return;
    }

    try {
        const raps = await apiFetch(`/rap?maThanhPho=${cityId}`);
        if (rapSelect) {
            rapSelect.disabled = false;
            raps.forEach(r => {
                const option = document.createElement('option');
                option.value = r.maRap;
                option.textContent = r.tenRap;
                rapSelect.appendChild(option);
            });
        }
    } catch (error) {
        console.error("Lỗi:", error);
    }
}

async function fetchShowtimes() {
    const maRap = document.getElementById('chonRap')?.value;
    const ngay = document.getElementById('chonNgay')?.value;
    const container = document.getElementById('danhSachSuatChieu');

    if (!maRap || !ngay || !container) return;

    container.innerHTML = '<div class="text-center my-4"><div class="spinner-border text-danger"></div><p class="mt-2">Đang tải lịch chiếu...</p></div>';

    try {
        const suatChieus = await apiFetch(`/suat-chieu/tim-suat?maPhim=${currentMovieId}&maRap=${maRap}&ngay=${ngay}`);

        if (!suatChieus || suatChieus.length === 0) {
            container.innerHTML = `
                <div class="text-center text-danger py-4">
                    <i class="fas fa-box-open fa-2x mb-2"></i>
                    <h5>Không có suất chiếu nào vào ngày này.</h5>
                </div>`;
            return;
        }

        let html = '<h5 class="fw-bold mb-3 text-dark">2D Phụ Đề</h5><div class="d-flex flex-wrap gap-2">';
        suatChieus.forEach(sc => {
            const timeStr = sc.gioChieu.substring(0, 5); // Cắt 19:00:00 -> 19:00
            html += `
                <a href="dat-ve.html?suat=${sc.maSuat}" class="time-btn">
                    <div class="time">${timeStr}</div>
                    <div class="seats">${sc.soGheTrong} ghế trống</div>
                </a>
            `;
        });
        html += '</div>';

        container.innerHTML = html;

    } catch (error) {
        console.error(error);
        container.innerHTML = '<p class="text-danger text-center mt-3">Lỗi tải lịch chiếu! Vui lòng thử lại.</p>';
    }
}