document.addEventListener('DOMContentLoaded', async () => {
    // 1. Khởi tạo Nav-bar từ shared.js
    if (typeof initPage === 'function') initPage('phim');

    // 2. Lấy ID phim từ URL
    const params = new URLSearchParams(window.location.search);
    const phimId = params.get('id');

    if (!phimId) {
        window.location.href = 'index.html';
        return;
    }

    try {
        // 3. Gọi API lấy chi tiết phim (Khớp với PhimController của bạn)
        const movie = await apiFetch(`/phim/${phimId}`);
        renderData(movie);
    } catch (error) {
        console.error("Lỗi tải phim:", error);
        showToast("Không tìm thấy thông tin phim!", "error");
    }
});

function renderData(m) {
    // Đổ dữ liệu cơ bản
    document.getElementById('movieTitle').textContent = m.tenPhim;
    document.getElementById('movieDesc').textContent = m.moTa || "Nội dung đang được cập nhật...";
    document.getElementById('movieDuration').innerHTML = `<i class="far fa-clock"></i> ${m.thoiLuong} phút`;
    document.getElementById('movieRelease').innerHTML = `<i class="far fa-calendar-alt"></i> ${formatDate(m.ngayKhoiChieu)}`;
    document.getElementById('movieAgeBadge').textContent = m.doTuoiPhuHop;
    document.getElementById('moviePoster').src = getPosterUrl(m.poster);
    document.getElementById('movieLang').textContent = m.ngonNgu || "Tiếng Việt";

    // Đổ thể loại (m.theLoais là List<String> từ DTO)
    const genreBox = document.getElementById('movieGenres');
    genreBox.innerHTML = m.theLoais.map(g => `<span>${g}</span>`).join('');

    // 4. Xử lý Trailer YouTube
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

// Hàm tách ID từ link YouTube (hỗ trợ cả link ngắn youtube.be và link dài)
function getYouTubeId(url) {
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    const match = url.match(regExp);
    return (match && match[2].length === 11) ? match[2] : null;
}

function scrollToBooking() {
    document.getElementById('booking-area').scrollIntoView({ behavior: 'smooth' });
}