document.addEventListener('DOMContentLoaded', () => {
  const toggle = document.querySelector('.nav-toggle');
  const nav = document.querySelector('.nav');
  if (toggle && nav) {
    toggle.addEventListener('click', () => nav.classList.toggle('open'));
  }

  fetch('/users/me')
    .then(res => res.ok ? res.json() : null)
    .then(data => {
      const loggedIn = data !== null;
      const isAdmin = data?.role === 'ADMIN';
      const loginBtn = document.getElementById('btn-login');
      const logoutBtn = document.getElementById('btn-logout');
      const adminEntry = document.querySelector('.admin-entry');

      if (loginBtn) loginBtn.style.display = loggedIn ? 'none' : '';
      if (logoutBtn) logoutBtn.style.display = loggedIn ? '' : 'none';
      if (adminEntry) {
        adminEntry.style.display = loggedIn ? '' : 'none';
        if (loggedIn && !isAdmin) {
          adminEntry.addEventListener('click', e => {
            e.preventDefault();
            showToast('관리자만 접근 가능합니다.');
          });
        }
      }
    });
});