<template>
  <div id="app">
    <!-- ===== SIDEBAR NAVIGATION ===== -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <span class="logo-text">QuanLyKho</span>
        </div>
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          {{ sidebarCollapsed ? '→' : '←' }}
        </button>
      </div>

      <nav class="sidebar-nav">
        <a
            class="nav-item"
            :class="{ active: currentPage === 'category' }"
            @click="currentPage = 'category'"
        >
          <span class="nav-label">Loại hàng</span>
        </a>
        <a
            class="nav-item"
            :class="{ active: currentPage === 'product' }"
            @click="currentPage = 'product'"
        >
          <span class="nav-label">Sản phẩm</span>
        </a>
      </nav>

      <div class="sidebar-footer">
        <span class="api-status" :class="apiOnline ? 'online' : 'offline'">
          <span class="status-dot"></span>
          {{ apiOnline ? 'API Online' : 'API Offline' }}
        </span>
      </div>
    </aside>

    <!-- ===== MAIN CONTENT ===== -->
    <main class="main-content">
      <!-- Header -->
      <header class="topbar">
        <h1 class="page-title">
          {{ currentPage === 'category' ? 'Quản lý Loại hàng' : 'Quản lý Sản phẩm' }}
        </h1>
        <div class="topbar-right">
          <span class="time-badge">{{ currentTime }}</span>
        </div>
      </header>

      <!-- Page Content -->
      <div class="page-body">
        <CategoryManagement v-if="currentPage === 'category'" @api-status="setApiStatus" />
        <ProductManagement  v-if="currentPage === 'product'"  @api-status="setApiStatus" />
      </div>
    </main>
  </div>
</template>

<script>
import CategoryManagement from './components/CategoryManagement.vue'
import ProductManagement  from './components/ProductManagement.vue'

export default {
  name: 'App',
  components: { CategoryManagement, ProductManagement },

  data() {
    return {
      currentPage: 'category',   // Trang hiện tại: 'category' hoặc 'product'
      sidebarCollapsed: false,   // Trạng thái sidebar thu gọn
      apiOnline: true,           // Trạng thái kết nối API
      currentTime: '',           // Giờ hiện tại hiển thị trên header
    }
  },

  methods: {
    // Nhận trạng thái API từ component con qua emit
    setApiStatus(status) {
      this.apiOnline = status
    },

    // Cập nhật giờ hiện tại mỗi giây
    updateTime() {
      const now = new Date()
      this.currentTime = now.toLocaleTimeString('vi-VN')
    }
  },

  mounted() {
    this.updateTime()
    setInterval(this.updateTime, 1000)
  }
}
</script>

<style>
/* ===== RESET & GLOBAL ===== */
* { margin: 0; padding: 0; box-sizing: border-box; }

:root {
  --primary:    #2563eb;
  --primary-dk: #1d4ed8;
  --success:    #16a34a;
  --danger:     #dc2626;
  --warning:    #d97706;
  --bg:         #f1f5f9;
  --surface:    #ffffff;
  --border:     #e2e8f0;
  --text:       #1e293b;
  --text-muted: #64748b;
  --sidebar-w:  220px;
  --sidebar-collapsed: 64px;
  --topbar-h:   60px;
  --radius:     10px;
  --shadow:     0 1px 3px rgba(0,0,0,.08), 0 4px 16px rgba(0,0,0,.06);
}

body { font-family: 'Segoe UI', sans-serif; background: var(--bg); color: var(--text); }

/* ===== LAYOUT ===== */
#app { display: flex; min-height: 100vh; }

/* ===== SIDEBAR ===== */
.sidebar {
  width: var(--sidebar-w);
  background: var(--text);
  color: #fff;
  display: flex;
  flex-direction: column;
  transition: width .25s ease;
  position: fixed;
  top: 0; left: 0; bottom: 0;
  z-index: 100;
}
.sidebar.collapsed { width: var(--sidebar-collapsed); }
.sidebar.collapsed .logo-text,
.sidebar.collapsed .nav-label,
.sidebar.collapsed .api-status { display: none; }

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 16px;
  border-bottom: 1px solid rgba(255,255,255,.1);
}
.logo { display: flex; align-items: center; gap: 10px; font-weight: 700; font-size: 16px; }
.logo-icon { font-size: 22px; }
.collapse-btn {
  background: rgba(255,255,255,.1);
  border: none; color: #fff;
  width: 28px; height: 28px;
  border-radius: 6px; cursor: pointer;
  font-size: 14px;
}
.collapse-btn:hover { background: rgba(255,255,255,.2); }

.sidebar-nav { flex: 1; padding: 12px 8px; display: flex; flex-direction: column; gap: 4px; }
.nav-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 12px; border-radius: 8px;
  cursor: pointer; color: rgba(255,255,255,.7);
  transition: all .15s; text-decoration: none;
  white-space: nowrap; overflow: hidden;
}
.nav-item:hover { background: rgba(255,255,255,.1); color: #fff; }
.nav-item.active { background: var(--primary); color: #fff; }
.nav-icon { font-size: 18px; min-width: 18px; }

.sidebar-footer { padding: 16px; border-top: 1px solid rgba(255,255,255,.1); }
.api-status {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; color: rgba(255,255,255,.6);
}
.status-dot {
  width: 8px; height: 8px; border-radius: 50%;
}
.online .status-dot { background: #4ade80; box-shadow: 0 0 6px #4ade80; }
.offline .status-dot { background: #f87171; }

/* ===== MAIN CONTENT ===== */
.main-content {
  margin-left: var(--sidebar-w);
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: margin-left .25s ease;
  min-height: 100vh;
}

/* ===== TOPBAR ===== */
.topbar {
  height: var(--topbar-h);
  background: var(--surface);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky; top: 0; z-index: 50;
  box-shadow: 0 1px 0 var(--border);
}
.page-title { font-size: 18px; font-weight: 600; }
.time-badge {
  background: var(--bg); border: 1px solid var(--border);
  padding: 4px 12px; border-radius: 20px;
  font-size: 13px; color: var(--text-muted);
  font-variant-numeric: tabular-nums;
}

.page-body { padding: 24px; flex: 1; }

/* ===== SHARED CARD ===== */
.card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}
.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
  display: flex; align-items: center; justify-content: space-between;
}
.card-header h2 { font-size: 15px; font-weight: 600; }
.card-body { padding: 20px; }

/* ===== BUTTON ===== */
.btn {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 8px 16px; border-radius: 7px;
  font-size: 13px; font-weight: 500; cursor: pointer;
  border: none; transition: all .15s;
  white-space: nowrap;
}
.btn-primary   { background: var(--primary); color: #fff; }
.btn-primary:hover   { background: var(--primary-dk); }
.btn-success   { background: var(--success); color: #fff; }
.btn-success:hover   { background: #15803d; }
.btn-danger    { background: var(--danger); color: #fff; }
.btn-danger:hover    { background: #b91c1c; }
.btn-secondary { background: var(--bg); color: var(--text); border: 1px solid var(--border); }
.btn-secondary:hover { background: var(--border); }
.btn-sm { padding: 5px 10px; font-size: 12px; }
.btn:disabled { opacity: .5; cursor: not-allowed; }

/* ===== TABLE ===== */
.table-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; font-size: 13px; }
thead th {
  background: var(--bg); padding: 10px 14px;
  text-align: left; font-weight: 600; font-size: 12px;
  text-transform: uppercase; letter-spacing: .5px;
  color: var(--text-muted); border-bottom: 1px solid var(--border);
}
tbody td { padding: 12px 14px; border-bottom: 1px solid var(--border); vertical-align: middle; }
tbody tr:last-child td { border-bottom: none; }
tbody tr:hover { background: #f8fafc; }

/* ===== FORM ===== */
.form-group { margin-bottom: 14px; }
.form-label { display: block; font-size: 12px; font-weight: 600; margin-bottom: 5px; color: var(--text-muted); }
.form-control {
  width: 100%; padding: 8px 12px;
  border: 1px solid var(--border); border-radius: 7px;
  font-size: 13px; color: var(--text);
  transition: border-color .15s, box-shadow .15s;
  background: var(--surface);
}
.form-control:focus {
  outline: none; border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(37,99,235,.1);
}
.form-control.error { border-color: var(--danger); }
.error-msg { color: var(--danger); font-size: 11px; margin-top: 4px; }

/* ===== MODAL ===== */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,.4);
  display: flex; align-items: center; justify-content: center;
  z-index: 200;
  animation: fadeIn .15s ease;
}
.modal {
  background: var(--surface);
  border-radius: 12px;
  width: 480px; max-width: 95vw;
  box-shadow: 0 20px 60px rgba(0,0,0,.2);
  animation: slideUp .2s ease;
}
.modal-header {
  padding: 18px 20px;
  border-bottom: 1px solid var(--border);
  display: flex; align-items: center; justify-content: space-between;
}
.modal-header h3 { font-size: 15px; font-weight: 600; }
.modal-close {
  background: none; border: none; cursor: pointer;
  font-size: 18px; color: var(--text-muted); line-height: 1;
}
.modal-close:hover { color: var(--text); }
.modal-body { padding: 20px; }
.modal-footer {
  padding: 14px 20px;
  border-top: 1px solid var(--border);
  display: flex; justify-content: flex-end; gap: 8px;
}

/* ===== ALERT / TOAST ===== */
.toast {
  position: fixed; top: 20px; right: 20px;
  padding: 12px 18px; border-radius: 8px;
  font-size: 13px; font-weight: 500;
  z-index: 999; max-width: 360px;
  animation: slideInRight .2s ease;
  box-shadow: 0 4px 20px rgba(0,0,0,.15);
}
.toast-success { background: #dcfce7; color: #166534; border: 1px solid #bbf7d0; }
.toast-error   { background: #fee2e2; color: #991b1b; border: 1px solid #fecaca; }

/* ===== BADGE ===== */
.badge {
  display: inline-block; padding: 3px 8px;
  border-radius: 20px; font-size: 11px; font-weight: 600;
}
.badge-primary { background: #dbeafe; color: #1d4ed8; }
.badge-success { background: #dcfce7; color: #166534; }

/* ===== LOADING ===== */
.loading-wrap { display: flex; align-items: center; justify-content: center; padding: 40px; }
.spinner {
  width: 28px; height: 28px;
  border: 3px solid var(--border);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin .7s linear infinite;
}

/* ===== EMPTY STATE ===== */
.empty-state { text-align: center; padding: 48px 20px; color: var(--text-muted); }
.empty-state .empty-icon { font-size: 40px; margin-bottom: 12px; }
.empty-state p { font-size: 14px; }

/* ===== ANIMATIONS ===== */
@keyframes fadeIn   { from { opacity: 0 } to { opacity: 1 } }
@keyframes slideUp  { from { opacity: 0; transform: translateY(12px) } to { opacity: 1; transform: none } }
@keyframes slideInRight { from { opacity: 0; transform: translateX(20px) } to { opacity: 1; transform: none } }
@keyframes spin     { to { transform: rotate(360deg) } }
</style>