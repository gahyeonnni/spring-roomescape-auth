let selectedStoreId = null;

document.addEventListener('DOMContentLoaded', () => {
  loadStores();
  loadThemes(null);
});

function loadStores() {
  fetch('/stores')
    .then(res => res.json())
    .then(stores => {
      const tabs = document.getElementById('store-tabs');
      tabs.innerHTML = '';

      const allBtn = buildTab('전체', null);
      allBtn.classList.add('active');
      tabs.appendChild(allBtn);

      stores.forEach(s => tabs.appendChild(buildTab(s.name, s.id)));
    })
    .catch(err => console.error('매장 조회 실패:', err));
}

function buildTab(label, storeId) {
  const btn = document.createElement('button');
  btn.type = 'button';
  btn.className = 'store-tab-btn';
  btn.textContent = label;
  btn.style.cssText = 'padding:8px 20px;border-radius:20px;border:2px solid var(--accent);background:transparent;color:var(--accent);font-size:0.95rem;cursor:pointer;transition:all .2s;';
  btn.addEventListener('click', () => {
    document.querySelectorAll('.store-tab-btn').forEach(b => {
      b.style.background = 'transparent';
      b.style.color = 'var(--accent)';
      b.classList.remove('active');
    });
    btn.style.background = 'var(--accent)';
    btn.style.color = '#fff';
    btn.classList.add('active');
    selectedStoreId = storeId;
    loadThemes(storeId);
  });
  return btn;
}

function loadThemes(storeId) {
  const url = storeId ? `/themes?storeId=${storeId}` : '/themes';
  fetch(url)
    .then(res => res.json())
    .then(themes => {
      const title = document.getElementById('store-section-title');
      const activeBtn = document.querySelector('.store-tab-btn.active');
      title.textContent = activeBtn ? activeBtn.textContent + ' 테마' : '전체 테마';
      renderThemes(themes);
    })
    .catch(err => console.error('테마 조회 실패:', err));
}

function renderThemes(themes) {
  const grid = document.getElementById('store-theme-grid');
  const empty = document.getElementById('store-theme-empty');
  grid.innerHTML = '';

  if (!themes || themes.length === 0) {
    empty.classList.remove('d-none');
    return;
  }
  empty.classList.add('d-none');
  themes.forEach(theme => grid.appendChild(buildThemeCard(theme)));
}

function buildThemeCard(theme) {
  const card = document.createElement('a');
  card.className = 'theme-card';
  card.href = '/reservation';

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
  card.appendChild(body);

  return card;
}