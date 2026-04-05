/* ============================================================
   UTC CINEMA – Support/FAQ Logic
   ============================================================ */

const faqs = [
  { cat:'mua-ve', q:'Làm thế nào để mua vé xem phim online?', a:'Bạn có thể mua vé trực tiếp trên website bằng cách chọn phim, rạp, suất chiếu và ghế ngồi. Sau đó tiến hành thanh toán để nhận vé điện tử.' },
  { cat:'mua-ve', q:'Tôi có thể đặt vé trước bao lâu?', a:'Bạn có thể đặt vé trước tối đa 7 ngày kể từ ngày chiếu. Lịch chiếu được cập nhật hàng ngày.' },
  { cat:'mua-ve', q:'Tôi có thể hủy/đổi vé đã mua không?', a:'Vé đã mua online có thể hủy hoặc đổi trước giờ chiếu 2 tiếng. Phí hủy vé là 10% giá trị vé. Vui lòng liên hệ hotline hoặc đến quầy rạp.' },
  { cat:'mua-ve', q:'Tối đa mua được bao nhiêu vé một lần?', a:'Mỗi lần giao dịch, quý khách có thể mua tối đa 8 vé cho cùng một suất chiếu.' },
  { cat:'thanh-toan', q:'Những hình thức thanh toán nào được chấp nhận?', a:'UTC Cinema hỗ trợ thanh toán qua: thẻ tín dụng/ghi nợ (Visa, Mastercard, JCB), ví điện tử (MoMo, ZaloPay, VNPay), chuyển khoản ngân hàng và thanh toán tại quầy.' },
  { cat:'thanh-toan', q:'Thanh toán online có an toàn không?', a:'Toàn bộ giao dịch được mã hóa SSL 256-bit và xử lý qua cổng thanh toán đã được chứng nhận PCI-DSS. Thông tin thẻ không được lưu trữ trên hệ hệ thống.' },
  { cat:'thanh-vien', q:'Làm sao để đăng ký thẻ thành viên?', a:'Bạn chỉ cần tạo tài khoản trên website hoặc tại quầy rạp. Thẻ thành viên được kích hoạt tự động và hoàn toàn miễn phí.' },
  { cat:'thanh-vien', q:'Điểm tích lũy được tính như thế nào?', a:'Mỗi 10.000đ chi tiêu, bạn nhận được 1 điểm (Thường), 1.5 điểm (Gold) hoặc 2 điểm (VIP). Điểm được cộng tự động sau mỗi giao dịch.' },
  { cat:'thanh-vien', q:'Điểm thưởng có thời hạn sử dụng không?', a:'Điểm thưởng có hiệu lực trong 12 tháng kể từ ngày tích lũy. Sau thời hạn, điểm chưa sử dụng sẽ tự động hết hiệu lực.' },
  { cat:'rap', q:'UTC Cinema có bao nhiêu rạp trên toàn quốc?', a:'Hiện UTC Cinema có hệ thống rạp tại các thành phố lớn trên cả nước. Xem danh sách chi tiết tại trang rạp chiếu phim.' },
  { cat:'rap', q:'Rạp có chỗ gửi xe không?', a:'Tất cả rạp UTC Cinema đều có bãi giữ xe máy và ô tô. Khách hàng có vé xem phim được miễn phí gửi xe trong thời gian xem phim.' },
  { cat:'khac', q:'Trẻ em dưới mấy tuổi được miễn vé?', a:'Trẻ em dưới 2 tuổi không chiếm ghế được miễn vé. Từ 2 tuổi trở lên cần mua vé đầy đủ. Trẻ dưới 13 tuổi cần có người lớn đi kèm.' },
  { cat:'khac', q:'Tôi có thể mang đồ ăn từ ngoài vào rạp không?', a:'Để đảm bảo chất lượng dịch vụ và vệ sinh rạp, UTC Cinema không cho phép mang đồ ăn thức uống từ bên ngoài vào phòng chiếu.' },
  { cat:'khac', q:'Rạp có phục vụ người khuyết tật không?', a:'Có. Tất cả rạp UTC Cinema đều có lối đi và chỗ ngồi dành riêng cho người khuyết tật.' }
];

let currentCat = 'all';

document.addEventListener('DOMContentLoaded', () => {
  if (typeof initPage === 'function') {
    initPage('ho-tro');
  }
  renderFAQ(faqs);
});

function filterFAQ(cat, btn) {
  currentCat = cat;
  document.querySelectorAll('.faq-cat-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  document.getElementById('faqSearch').value = '';
  const filtered = cat === 'all' ? faqs : faqs.filter(f => f.cat === cat);
  renderFAQ(filtered);
}

function searchFAQ(term) {
  const lower = term.toLowerCase();
  let filtered = faqs;
  if (currentCat !== 'all') filtered = filtered.filter(f => f.cat === currentCat);
  if (lower) filtered = filtered.filter(f => f.q.toLowerCase().includes(lower) || f.a.toLowerCase().includes(lower));
  renderFAQ(filtered);
}

function renderFAQ(items) {
  const el = document.getElementById('faqList');
  if (!items.length) {
    el.innerHTML = '<div class="empty-state" style="padding:40px; text-align:center;"><i class="fas fa-search" style="font-size:2rem; color:#ccc;"></i><h3>Không tìm thấy câu hỏi phù hợp</h3></div>';
    return;
  }
  el.innerHTML = items.map((f, i) => `
    <div class="faq-item">
      <button class="faq-question" onclick="toggleFAQ(this)">
        <span>${escapeHtml(f.q)}</span>
        <i class="fas fa-chevron-down"></i>
      </button>
      <div class="faq-answer">${f.a}</div>
    </div>
  `).join('');
}

function toggleFAQ(btn) {
  const answer = btn.nextElementSibling;
  const isOpen = btn.classList.contains('open');

  // Đóng tất cả các FAQ khác trước khi mở cái mới (tùy chọn)
  document.querySelectorAll('.faq-question').forEach(q => {
    q.classList.remove('open');
    q.nextElementSibling.classList.remove('open');
  });

  // Nếu cái đang bấm chưa mở thì mở nó ra
  if (!isOpen) {
    btn.classList.add('open');
    answer.classList.add('open');
  }
}

// Hàm bổ trợ để tránh lỗi XSS
function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}