// 공통 모달 닫기
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// (수정!) 1. "수정" 팝업 열기 (Thymeleaf 보안 에러 해결)
function openEditModal(buttonElement) { // 'this'(버튼 자신)를 받음
    const modal = document.getElementById('editReviewModal');
    const form = document.getElementById('reviewEditForm');

    // (수정) data-* 속성에서 안전하게 값을 읽어옴
    const reviewId = buttonElement.getAttribute('data-review-id');
    const rating = buttonElement.getAttribute('data-rating');
    const content = buttonElement.getAttribute('data-content');

    // (중요) 폼의 action URL을 동적으로 변경 (예: /reviews/123/edit)
    form.action = '/reviews/' + reviewId + '/edit';

    // 팝업 안의 폼에, 클릭한 리뷰의 기존 데이터를 채워넣기
    document.getElementById('edit-review-rating').value = rating;
    document.getElementById('edit-review-content').value = content;

    modal.style.display = 'flex';
}

// 2. "삭제" 팝업 열기
function openDeleteModal(reviewId) {
    const modal = document.getElementById('deleteReviewModal');
    const form = document.getElementById('reviewDeleteForm');

    // (중요) 폼의 action URL을 동적으로 변경 (예: /reviews/123/delete)
    form.action = '/reviews/' + reviewId + '/delete';

    modal.style.display = 'flex';
}

// 3. 모달 바깥쪽 어두운 영역 클릭 시 닫기
window.addEventListener("click", (event) => {
    if (event.target.classList.contains('modal-overlay')) {
        event.target.style.display = 'none';
    }
});