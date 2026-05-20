const SESSION_HEADER = 'X-Session-Id';

const _originalFetch = window.fetch.bind(window);
window.fetch = function (url, options = {}) {
  const sessionId = localStorage.getItem(SESSION_HEADER);
  if (sessionId) {
    options.headers = Object.assign({}, options.headers || {}, { [SESSION_HEADER]: sessionId });
  }
  return _originalFetch(url, options);
};

window.saveSession = function (sessionId) {
  localStorage.setItem(SESSION_HEADER, sessionId);
};

window.logout = async function () {
  await fetch('/users/logout', { method: 'POST' });
  localStorage.removeItem(SESSION_HEADER);
  window.location.href = '/login';
};

window.showToast = function (message, type = 'error') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  const iconClass = type === 'error' ? 'fas fa-exclamation-circle' : 'fas fa-check-circle';
  toast.innerHTML = `<i class="${iconClass}"></i><span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.add('toast-out');
    toast.addEventListener('animationend', () => toast.remove(), {once: true});
  }, 3500);
};