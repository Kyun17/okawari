const form = document.getElementById('loginForm');
const btn = document.getElementById('submitBtn');
const err = document.getElementById('err');
const statusBox = document.getElementById('loginStatus');

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    err.style.display = 'none';
    statusBox.style.display = 'none';
    btn.disabled = true;

    const payload = {
        email: document.getElementById('email').value.trim(),
        password: document.getElementById('password').value.trim()
    };

    try {
        // 1) 로그인
        const res = await fetch('/login', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(payload),
            credentials: 'include'
        });

        if (!res.ok) {
            if (res.status === 401) {
                err.textContent = '이메일 또는 비밀번호가 올바르지 않습니다.';
            } else {
                err.textContent = '잠시 후 다시 시도해주세요. (' + res.status + ')';
            }
            err.style.display = 'block';
            return;
        }

        // 2) 로그인 성공 → 곧바로 /me 조회해서 화면에 표시
        const meRes = await fetch('/me', { credentials: 'include' });
        if (meRes.ok) {
            const me = await meRes.json();
            form.style.display = 'none'; // 폼 숨김
            statusBox.innerHTML = `
        <div style="padding:12px; border-radius:10px; background:#ecfdf5; color:#065f46;">
          로그인 성공 🎉<br>
          <b>${me.nickname}</b> (${me.email})
        </div>
        <button id="goLogout" style="margin-top:12px; padding:10px 14px; border-radius:10px; border:0; background:#0b3f8a; color:#fff; cursor:pointer;">
          로그아웃
        </button>
      `;
            statusBox.style.display = 'block';

            // 3) 로그아웃 버튼 동작
            document.getElementById('goLogout').addEventListener('click', async () => {
                await fetch('/logout', { method: 'POST', credentials: 'include' });
                location.reload();
            });
        } else {
            err.textContent = '세션 확인 실패 (' + meRes.status + ')';
            err.style.display = 'block';
        }
    } catch (ex) {
        err.textContent = '네트워크 오류가 발생했습니다.';
        err.style.display = 'block';
    } finally {
        btn.disabled = false;
    }
});
