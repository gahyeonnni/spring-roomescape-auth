let currentStoreId = null;
let currentTab = 'themes';
let editingId = null;
let editingThemeId = null;
let datePicker = null;

document.addEventListener('DOMContentLoaded', () => {
  datePicker = flatpickr('#edit-date', {
    dateFormat: 'Y-m-d',
    minDate: 'today',
    locale: 'ko',
    onChange: (_, dateStr) => {
      if (dateStr && editingThemeId) loadAvailableTimes(dateStr, editingThemeId);
    }
  });

  document.getElementById('edit-confirm').addEventListener('click', submitEdit);
  document.getElementById('edit-cancel').addEventListener('click', closeModal);
  document.getElementById('modal-overlay').addEventListener('click', e => {
    if (e.target === document.getElementById('modal-overlay')) closeModal();
  });

  loadStores();
});

// ── 매장 탭 ─────────────────────────────────────────────────────────────────

function loadStores() {
  fetch('/admin/stores')
    .then(res => {
      if (res.status === 401) { window.location.href = '/login'; return null; }
      return res.json();
    })
    .then(stores => {
      if (!stores) return;
      const tabs = document.getElementById('store-tabs');
      tabs.innerHTML = '';

      if (stores.length === 0) {
        document.getElementById('no-stores').classList.remove('d-none');
        return;
      }

      stores.forEach((s, i) => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'store-tab-btn';
        btn.textContent = s.name;
        btn.dataset.storeId = s.id;
        btn.style.cssText = 'padding:8px 20px;border-radius:20px;border:2px solid var(--accent);background:transparent;color:var(--accent);font-size:0.95rem;cursor:pointer;transition:all .2s;';
        btn.addEventListener('click', () => selectStore(s.id, s.name, btn));
        tabs.appendChild(btn);
        if (i === 0) selectStore(s.id, s.name, btn);
      });
    });
}

function selectStore(storeId, storeName, btn) {
  currentStoreId = storeId;
  document.querySelectorAll('.store-tab-btn').forEach(b => {
    b.style.background = 'transparent';
    b.style.color = 'var(--accent)';
  });
  btn.style.background = 'var(--accent)';
  btn.style.color = '#fff';

  document.getElementById('section-nav').classList.remove('d-none');
  document.getElementById('section-nav').style.display = 'flex';

  if (currentTab === 'themes') loadThemes(storeId);
  else loadReservations(storeId);
}

// ── 섹션 탭 전환 ─────────────────────────────────────────────────────────────

function switchTab(tab) {
  currentTab = tab;
  document.getElementById('tab-themes').classList.toggle('active', tab === 'themes');
  document.getElementById('tab-reservations').classList.toggle('active', tab === 'reservations');
  document.getElementById('section-themes').classList.toggle('d-none', tab !== 'themes');
  document.getElementById('section-reservations').classList.toggle('d-none', tab !== 'reservations');

  if (!currentStoreId) return;
  if (tab === 'themes') loadThemes(currentStoreId);
  else loadReservations(currentStoreId);
}

// ── 테마 ──────────────────────────────────────────────────────────────────────

function loadThemes(storeId) {
  fetch(`/themes?storeId=${storeId}`)
    .then(res => res.json())
    .then(renderThemes)
    .catch(err => console.error('테마 조회 실패:', err));
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
  themes.forEach(t => {
    const card = document.createElement('div');
    card.className = 'theme-card';
    card.innerHTML = `
      <div class="thumb" style="${t.imageUrl ? `background-image:url('${t.imageUrl}')` : ''}"></div>
      <div class="body">
        <h3 class="name">${t.name}</h3>
        <p class="desc">${t.description || ''}</p>
      </div>
    `;
    grid.appendChild(card);
  });
}

// ── 예약 ──────────────────────────────────────────────────────────────────────

function loadReservations(storeId) {
  fetch(`/admin/reservations?storeId=${storeId}`)
    .then(res => {
      if (res.status === 403) { showToast('이 매장의 예약에 접근할 수 없습니다.'); return null; }
      if (res.status === 401) { window.location.href = '/login'; return null; }
      return res.json();
    })
    .then(data => { if (data) renderReservations(data); })
    .catch(err => console.error('예약 조회 실패:', err));
}

function renderReservations(data) {
  const tbody = document.getElementById('reservation-tbody');
  const empty = document.getElementById('reservation-empty');
  tbody.innerHTML = '';

  if (!data || data.length === 0) {
    empty.classList.remove('d-none');
    return;
  }
  empty.classList.add('d-none');

  data.forEach(r => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${r.date}</td>
      <td>${r.themeName}</td>
      <td>${formatTime(r.time.startAt)}</td>
      <td>${r.memberName}</td>
      <td style="text-align:right;">
        <button class="btn btn-secondary" style="font-size:0.82rem;padding:6px 12px;margin-right:4px;"
          onclick="openEditModal(${r.id}, '${r.date}', ${r.themeId})">변경</button>
        <button class="btn btn-danger" style="font-size:0.82rem;padding:6px 12px;"
          onclick="cancelReservation(${r.id}, this)">취소</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

// ── 예약 취소 ────────────────────────────────────────────────────────────────

function cancelReservation(id, btn) {
  if (!confirm('이 예약을 취소하시겠습니까?')) return;
  fetch(`/reservations/${id}`, {method: 'DELETE'})
    .then(res => {
      if (res.status === 204) {
        btn.closest('tr').remove();
        showToast('예약이 취소되었습니다.', 'success');
        return;
      }
      if (res.status === 403) throw new Error('이 매장의 예약만 취소할 수 있습니다.');
      return res.json().then(b => { throw new Error(b.message || '취소에 실패했습니다.'); });
    })
    .catch(err => showToast(err.message));
}

// ── 예약 변경 모달 ──────────────────────────────────────────────────────────

function openEditModal(id, currentDate, themeId) {
  editingId = id;
  editingThemeId = themeId;
  datePicker.setDate(currentDate);
  document.getElementById('modal-overlay').classList.remove('d-none');
  loadAvailableTimes(currentDate, themeId);
}

function closeModal() {
  document.getElementById('modal-overlay').classList.add('d-none');
  editingId = null;
  editingThemeId = null;
  datePicker.clear();
}

function loadAvailableTimes(date, themeId) {
  const select = document.getElementById('edit-time');
  select.innerHTML = '<option value="">불러오는 중...</option>';
  fetch(`/times/available?date=${date}&themeId=${themeId}`)
    .then(res => res.json())
    .then(times => {
      select.innerHTML = '';
      if (!times.length) {
        select.innerHTML = '<option value="">예약 가능한 시간이 없습니다.</option>';
        return;
      }
      times.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.id;
        opt.textContent = formatTime(t.startAt);
        select.appendChild(opt);
      });
    })
    .catch(() => { select.innerHTML = '<option value="">시간 조회 실패</option>'; });
}

function submitEdit() {
  const date = datePicker.input.value;
  const timeId = document.getElementById('edit-time').value;
  if (!date || !timeId) { showToast('날짜와 시간을 선택해주세요.'); return; }

  fetch(`/reservations/${editingId}`, {
    method: 'PATCH',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({date, timeId: parseInt(timeId)})
  })
    .then(res => {
      if (res.ok) return res.json();
      if (res.status === 403) throw new Error('이 매장의 예약만 변경할 수 있습니다.');
      return res.json().then(b => { throw new Error(b.message || '변경에 실패했습니다.'); });
    })
    .then(() => {
      showToast('예약이 변경되었습니다.', 'success');
      closeModal();
      loadReservations(currentStoreId);
    })
    .catch(err => showToast(err.message));
}

function formatTime(value) {
  if (!value) return '';
  const [h, m] = value.split(':');
  return `${h}:${m}`;
}