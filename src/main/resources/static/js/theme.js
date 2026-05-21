const THEME_API = '/admin/themes';
const STORE_API = '/admin/stores';

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('add-theme').addEventListener('click', openForm);
  document.getElementById('save-theme').addEventListener('click', saveTheme);
  document.getElementById('cancel-theme').addEventListener('click', closeForm);
  fetchThemes();
  loadStores();
});

function fetchThemes() {
  fetch(THEME_API)
    .then(res => {
      if (res.status === 401) { window.location.href = '/login'; return null; }
      if (res.status === 403) { showToast('접근 권한이 없습니다.'); return null; }
      return res.json();
    })
    .then(themes => { if (themes) renderThemes(themes); })
    .catch(err => console.error('테마 조회 실패:', err));
}

function loadStores() {
  fetch(STORE_API)
    .then(res => {
      if (!res.ok) return [];
      return res.json();
    })
    .then(stores => {
      const select = document.getElementById('new-store');
      select.innerHTML = '';
      if (!stores || stores.length === 0) {
        select.innerHTML = '<option value="">관리 중인 매장 없음</option>';
        return;
      }
      stores.forEach(s => {
        const opt = document.createElement('option');
        opt.value = s.id;
        opt.textContent = s.name;
        select.appendChild(opt);
      });
    })
    .catch(() => {
      document.getElementById('new-store').innerHTML = '<option value="">매장 조회 실패</option>';
    });
}

function renderThemes(themes) {
  const grid = document.getElementById('theme-grid');
  const empty = document.getElementById('theme-empty');
  grid.innerHTML = '';

  if (!themes || themes.length === 0) {
    empty.classList.remove('d-none');
    return;
  }
  empty.classList.add('d-none');
  themes.forEach(theme => grid.appendChild(buildAdminCard(theme)));
}

function buildAdminCard(theme) {
  const card = document.createElement('div');
  card.className = 'theme-card';
  card.dataset.id = theme.id;

  const thumb = document.createElement('div');
  thumb.className = 'thumb';
  if (theme.imageUrl) {
    thumb.style.backgroundImage = `url('${theme.imageUrl}')`;
  }
  card.appendChild(thumb);

  const body = document.createElement('div');
  body.className = 'body';
  const name = document.createElement('h3');
  name.className = 'name';
  name.textContent = theme.name;
  body.appendChild(name);
  const desc = document.createElement('p');
  desc.className = 'desc';
  desc.textContent = theme.description || '';
  body.appendChild(desc);

  const actions = document.createElement('div');
  actions.className = 'card-actions';
  const delBtn = document.createElement('button');
  delBtn.className = 'btn btn-danger';
  delBtn.innerHTML = '<i class="fas fa-trash"></i> 삭제';
  delBtn.addEventListener('click', () => deleteTheme(theme.id, card));
  actions.appendChild(delBtn);
  body.appendChild(actions);

  card.appendChild(body);
  return card;
}

function openForm() {
  document.getElementById('theme-form').classList.remove('d-none');
  document.getElementById('new-name').focus();
}

function closeForm() {
  document.getElementById('theme-form').classList.add('d-none');
  document.getElementById('new-name').value = '';
  document.getElementById('new-desc').value = '';
  document.getElementById('new-image').value = '';
}

function saveTheme() {
  const storeId = parseInt(document.getElementById('new-store').value);
  const body = {
    name: document.getElementById('new-name').value.trim(),
    description: document.getElementById('new-desc').value.trim(),
    imageUrl: document.getElementById('new-image').value.trim(),
    storeId: storeId || null
  };
  if (!body.name || !body.description) {
    showToast('이름과 설명을 입력해주세요.');
    return;
  }
  if (!body.storeId) {
    showToast('매장을 선택해주세요.');
    return;
  }
  fetch(THEME_API, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(body)
  })
    .then(res => {
      if (res.status === 401) { window.location.href = '/login'; return null; }
      if (res.status === 403) throw new Error('해당 매장에 대한 접근 권한이 없습니다.');
      if (res.status === 201) return res.json();
      return res.json().then(b => { throw new Error(b.message || '테마 추가에 실패했습니다.'); });
    })
    .then(theme => {
      if (!theme) return;
      const grid = document.getElementById('theme-grid');
      document.getElementById('theme-empty').classList.add('d-none');
      grid.appendChild(buildAdminCard(theme));
      closeForm();
      showToast('테마가 추가되었습니다.', 'success');
    })
    .catch(err => showToast(err.message));
}

function deleteTheme(id, cardEl) {
  if (!confirm('이 테마를 삭제하시겠습니까?')) return;
  fetch(`${THEME_API}/${id}`, {method: 'DELETE'})
    .then(res => {
      if (res.status === 204) {
        cardEl.remove();
        const grid = document.getElementById('theme-grid');
        if (!grid.children.length) document.getElementById('theme-empty').classList.remove('d-none');
        showToast('테마가 삭제되었습니다.', 'success');
        return;
      }
      if (res.status === 403) throw new Error('해당 매장에 대한 접근 권한이 없습니다.');
      return res.json().then(b => { throw new Error(b.message || '삭제에 실패했습니다.'); });
    })
    .catch(err => showToast(err.message));
}