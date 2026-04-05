/* ============================================================
   UTC CINEMA – News & Promotions Logic
   ============================================================ */

const promotions = [
  {
    id: 1, type: 'khuyen-mai', badge: 'HOT',
    img: 'https://images.unsplash.com/photo-1585647347483-22b66260dfff?w=600&q=80',
    title: 'Happy Day – Đồng giá vé 55K',
    date: '2026-01-01', endDate: '2026-12-31',
    excerpt: 'Áp dụng vào thứ 4 hàng tuần, tất cả khách hàng UTC Cinema được mua vé xem phim với giá chỉ 55.000đ cho phim 2D.'
  },
  {
    id: 2, type: 'khuyen-mai', badge: 'MỚI',
    img: 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=600&q=80',
    title: 'Combo Bắp Nước siêu tiết kiệm',
    date: '2026-02-01', endDate: '2026-12-31',
    excerpt: 'Mua combo 1 bắp lớn + 2 nước ngọt lớn chỉ với 89.000đ. Áp dụng tại quầy khi mua vé xem phim.'
  },
  {
    id: 3, type: 'su-kien',
    img: 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=600&q=80',
    title: 'Đêm phim kinh dị – Horror Night',
    date: '2026-03-15', endDate: '2026-03-31',
    excerpt: 'Sự kiện chiếu phim kinh dị marathon xuyên đêm tại tất cả rạp UTC Cinema toàn quốc. Giới hạn 100 vé/rạp.'
  },
  {
    id: 4, type: 'tin-tuc',
    img: 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=600&q=80',
    title: 'Top 5 phim bom tấn tháng 4/2026',
    date: '2026-03-25',
    excerpt: 'Danh sách những bộ phim bom tấn đáng mong đợi nhất sẽ ra rạp trong tháng 4/2026 tại UTC Cinema.'
  },
  {
    id: 5, type: 'khuyen-mai', badge: 'ĐẶC BIỆT',
    img: 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&q=80',
    title: 'Sinh nhật UTC Cinema – Giảm 50% vé',
    date: '2026-04-01', endDate: '2026-04-07',
    excerpt: 'Nhân dịp kỷ niệm sinh nhật, UTC Cinema tri ân khách hàng với ưu đãi giảm 50% giá vé cho tất cả thành viên.'
  },
  {
    id: 6, type: 'tin-tuc',
    img: 'https://images.unsplash.com/photo-1595769816263-9b910be24d5f?w=600&q=80',
    title: 'UTC Cinema khai trương rạp mới tại Đà Nẵng',
    date: '2026-03-20',
    excerpt: 'Rạp UTC Cinema Đà Nẵng chính thức khai trương với 8 phòng chiếu hiện đại, sức chứa trên 1200 ghế.'
  },
  {
    id: 7, type: 'su-kien',
    img: 'https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=600&q=80',
    title: 'Cuộc thi làm phim ngắn UTC Film Festival',
    date: '2026-05-01', endDate: '2026-06-30',
    excerpt: 'Cuộc thi sáng tạo phim ngắn dành cho các bạn trẻ yêu thích điện ảnh. Giải thưởng lên đến 50 triệu đồng.'
  },
  {
    id: 8, type: 'khuyen-mai',
    img: 'https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?w=600&q=80',
    title: 'Ưu đãi học sinh – sinh viên',
    date: '2026-01-01', endDate: '2026-12-31',
    excerpt: 'Học sinh, sinh viên xuất trình thẻ được giảm 20% giá vé xem phim tất cả các ngày trong tuần.'
  }
];

let filteredPromos = [...promotions];

document.addEventListener('DOMContentLoaded', () => {
  // Khởi tạo Header/Footer từ shared.js
  if (typeof initPage === 'function') {
    initPage('uu-dai');
  }
  renderPromos();
});

/**
 * Lọc khuyến mãi theo danh mục
 */
function filterPromo(type, btn) {
  // Update UI tabs
  document.querySelectorAll('.promo-tab').forEach(t => t.classList.remove('active'));
  btn.classList.add('active');

  // Filter data
  filteredPromos = type === 'all' ? [...promotions] : promotions.filter(p => p.type === type);
  renderPromos();
}

/**
 * Hiển thị danh sách khuyến mãi lên grid
 */
function renderPromos() {
  const el = document.getElementById('promoGrid');
  if (!el) return;

  if (!filteredPromos.length) {
    el.innerHTML = '<div class="empty-state"><i class="fas fa-gift"></i><h3>Không có tin nào phù hợp</h3></div>';
    return;
  }

  el.innerHTML = filteredPromos.map(p => `
    <div class="promo-card">
      <div class="promo-card-img">
        <img src="${p.img}" alt="${escapeHtml(p.title)}" loading="lazy">
        ${p.badge ? `<span class="promo-badge">${escapeHtml(p.badge)}</span>` : ''}
      </div>
      <div class="promo-card-body">
        <h3>${escapeHtml(p.title)}</h3>
        <div class="promo-date">
          <i class="far fa-calendar-alt"></i>
          ${formatDate(p.date)}${p.endDate ? ' – ' + formatDate(p.endDate) : ''}
        </div>
        <p class="promo-excerpt">${escapeHtml(p.excerpt)}</p>
        <a href="DatVe.html" class="promo-buy-btn">
            Mua vé ngay <i class="fas fa-arrow-right" style="font-size:.75rem"></i>
        </a>
      </div>
    </div>
  `).join('');
}

// Hàm bổ trợ Escape HTML (nếu shared.js chưa có)
function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}