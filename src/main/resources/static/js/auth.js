/* ============================================================
   auth.js  – shared API helpers and toast utility
   ============================================================ */

const API_BASE = '';

function getToken() { return localStorage.getItem('token'); }

function authHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + getToken()
  };
}

async function apiPost(url, body) {
  const res = await fetch(API_BASE + url, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(json.message || json.error || 'Request failed');
  return json;
}

async function apiGet(url) {
  const res = await fetch(API_BASE + url, { headers: authHeaders() });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(json.message || json.error || 'Request failed');
  return json;
}

async function apiPut(url, body) {
  const res = await fetch(API_BASE + url, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(body)
  });
  const json = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(json.message || json.error || 'Request failed');
  return json;
}

async function apiDelete(url) {
  const res = await fetch(API_BASE + url, { method: 'DELETE', headers: authHeaders() });
  if (!res.ok) throw new Error('Delete failed');
}

function showToast(msg, type = 'success') {
  const t = document.getElementById('toast');
  const icon = document.getElementById('toastIcon');
  const m = document.getElementById('toastMsg');
  if (!t) return;
  t.className = 'show ' + type;
  icon.textContent = type === 'success' ? '✅' : '❌';
  m.textContent = msg;
  setTimeout(() => t.className = '', 3200);
}

function requireAuth(expectedRole) {
  const token = getToken();
  const role = localStorage.getItem('role');
  if (!token || !role) { window.location.href = 'login.html'; return false; }
  if (expectedRole && role !== expectedRole) { window.location.href = 'login.html'; return false; }
  return true;
}

function logout() {
  localStorage.clear();
  window.location.href = 'login.html';
}

function starsHtml(avg) {
  const n = Math.round(avg || 0);
  return '★'.repeat(n) + '☆'.repeat(5 - n);
}

function statusBadge(status) {
  const map = {
    OPEN: 'badge-open', IN_PROGRESS: 'badge-progress', COMPLETED: 'badge-done',
    PENDING: 'badge-pending', ACCEPTED: 'badge-accepted', REJECTED: 'badge-rejected'
  };
  return `<span class="ll-badge ${map[status] || ''}">${status}</span>`;
}
