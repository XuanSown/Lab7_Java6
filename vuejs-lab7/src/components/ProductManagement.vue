<template>
  <div>
    <!-- ===== TOAST NOTIFICATION ===== -->
    <div v-if="toast.show" :class="['toast', `toast-${toast.type}`]">
      {{ toast.message }}
    </div>

    <!-- ===== TOOLBAR ===== -->
    <div class="toolbar">
      <div class="toolbar-left">
        <input
            v-model="searchKeyword"
            class="form-control search-input"
            placeholder="Tìm theo tên sản phẩm..."
        />
        <!-- Filter theo loại hàng -->
        <select v-model="filterCategoryId" class="form-control" style="max-width:200px">
          <option value="">Tất cả loại hàng</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
      </div>
      <div class="toolbar-right">
        <button class="btn btn-primary" @click="openCreateModal">
          Thêm sản phẩm
        </button>
      </div>
    </div>

    <!-- ===== BẢNG DANH SÁCH ===== -->
    <div class="card">
      <div class="card-header">
        <h2>Danh sách sản phẩm</h2>
        <span class="badge badge-primary">{{ filteredProducts.length }} sản phẩm</span>
      </div>

      <div v-if="loading" class="loading-wrap">
        <div class="spinner"></div>
      </div>

      <div v-else-if="loadError" class="empty-state">
        <p style="color: var(--danger)">{{ loadError }}</p>
        <button class="btn btn-secondary" style="margin-top:12px" @click="loadAll">
          Thử lại
        </button>
      </div>

      <div v-else class="table-wrap">
        <table>
          <thead>
          <tr>
            <th style="width:50px">#</th>
            <th>Tên sản phẩm</th>
            <th style="width:140px">Giá</th>
            <th style="width:120px">Ngày nhập</th>
            <th style="width:140px">Loại hàng</th>
            <th style="width:140px; text-align:center">Thao tác</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="filteredProducts.length === 0">
            <td colspan="6">
              <div class="empty-state">
                <p>Chưa có sản phẩm nào</p>
              </div>
            </td>
          </tr>

          <tr v-for="(product, index) in filteredProducts" :key="product.id">
            <td style="color: var(--text-muted)">{{ index + 1 }}</td>
            <td style="font-weight: 500">{{ product.name }}</td>
            <td>
              <span class="price-tag">{{ formatPrice(product.price) }}</span>
            </td>
            <td style="color: var(--text-muted)">{{ formatDate(product.createDate) }}</td>
            <td>
              <span class="badge badge-success">{{ product.categoryName }}</span>
            </td>
            <td style="text-align:center">
              <div style="display:flex; gap:6px; justify-content:center">
                <button class="btn btn-secondary btn-sm" @click="openEditModal(product)">
                  Sửa
                </button>
                <button class="btn btn-danger btn-sm" @click="confirmDelete(product)">
                  Xóa
                </button>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ===== MODAL THÊM / SỬA ===== -->
    <div v-if="modal.show" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ modal.isEdit ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới' }}</h3>
          <button class="modal-close" @click="closeModal">✕</button>
        </div>

        <div class="modal-body">
          <!-- Tên sản phẩm -->
          <div class="form-group">
            <label class="form-label">Tên sản phẩm *</label>
            <input
                v-model="form.name"
                :class="['form-control', { error: formErrors.name }]"
                placeholder="VD: iPhone 15 Pro Max..."
            />
            <p v-if="formErrors.name" class="error-msg">{{ formErrors.name }}</p>
          </div>

          <!-- Giá sản phẩm -->
          <div class="form-group">
            <label class="form-label">Giá (VNĐ) *</label>
            <input
                v-model.number="form.price"
                type="number"
                :class="['form-control', { error: formErrors.price }]"
                placeholder="VD: 29990000"
                min="0"
            />
            <!-- Hiển thị preview giá đã format khi nhập -->
            <p v-if="form.price > 0" style="font-size:11px; color:var(--success); margin-top:4px">
              ≈ {{ formatPrice(form.price) }}
            </p>
            <p v-if="formErrors.price" class="error-msg">{{ formErrors.price }}</p>
          </div>

          <!-- Ngày nhập -->
          <div class="form-group">
            <label class="form-label">Ngày nhập</label>
            <input
                v-model="form.createDate"
                type="date"
                class="form-control"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Loại hàng *</label>
            <select
                v-model="form.categoryId"
                :class="['form-control', { error: formErrors.categoryId }]"
            >
              <option value="">-- Chọn loại hàng --</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
            <p v-if="formErrors.categoryId" class="error-msg">{{ formErrors.categoryId }}</p>
            <p v-if="categories.length === 0" style="font-size:11px; color:var(--warning); margin-top:4px">
              Chưa có loại hàng nào. Hãy thêm loại hàng trước.
            </p>
          </div>

          <!-- Lỗi từ server -->
          <div v-if="formErrors.server" class="error-server">
            {{ formErrors.server }}
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeModal">Hủy</button>
          <button
              class="btn btn-primary"
              @click="submitForm"
              :disabled="submitting || categories.length === 0"
          >
            {{ submitting ? 'Đang lưu...' : (modal.isEdit ? 'Cập nhật' : '➕ Thêm mới') }}
          </button>
        </div>
      </div>
    </div>

    <!-- ===== MODAL XÁC NHẬN XÓA ===== -->
    <div v-if="deleteConfirm.show" class="modal-overlay" @click.self="deleteConfirm.show = false">
      <div class="modal" style="width: 380px">
        <div class="modal-header">
          <h3>Xác nhận xóa</h3>
          <button class="modal-close" @click="deleteConfirm.show = false">✕</button>
        </div>
        <div class="modal-body">
          <p style="font-size:14px">
            Bạn có chắc muốn xóa sản phẩm
            <strong>{{ deleteConfirm.target?.name }}</strong>?
          </p>
          <p style="font-size:12px; color:var(--text-muted); margin-top:8px">
            Thao tác này không thể hoàn tác.
          </p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="deleteConfirm.show = false">Hủy</button>
          <button
              class="btn btn-danger"
              @click="executeDelete"
              :disabled="submitting"
          >
            {{ submitting ? 'Đang xóa...' : 'Xóa' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { productApi, categoryApi } from '../api.js'

export default {
  name: 'ProductManagement',
  emits: ['api-status'],

  data() {
    return {
      products: [],           // Danh sách sản phẩm
      categories: [],         // Danh sách loại hàng (dùng cho dropdown)
      searchKeyword: '',
      filterCategoryId: '',   // Filter theo loại hàng

      loading: false,
      loadError: null,
      submitting: false,

      modal: { show: false, isEdit: false },

      form: {
        name: '',
        price: '',
        createDate: '',
        categoryId: ''
      },

      formErrors: { name: '', price: '', categoryId: '', server: '' },

      deleteConfirm: { show: false, target: null },

      toast: { show: false, message: '', type: 'success' },
    }
  },

  computed: {
    filteredProducts() {
      let result = this.products

      // Lọc theo loại hàng nếu có chọn
      if (this.filterCategoryId) {
        result = result.filter(p => p.categoryId === this.filterCategoryId)
      }

      // Lọc theo từ khóa tìm kiếm
      if (this.searchKeyword.trim()) {
        const kw = this.searchKeyword.toLowerCase()
        result = result.filter(p => p.name.toLowerCase().includes(kw))
      }

      return result
    }
  },

  methods: {
    // ===== Tải dữ liệu =====
    async loadAll() {
      this.loading = true
      this.loadError = null

      try {
        // Promise.all: gọi đồng thời cả 2 API
        const [productRes, categoryRes] = await Promise.all([
          productApi.getAll(),
          categoryApi.getAll()
        ])

        this.products   = productRes.data  || []
        this.categories = categoryRes.data || []
        this.$emit('api-status', true)
      } catch (err) {
        this.loadError = err.message || 'Không thể tải dữ liệu'
        this.$emit('api-status', false)
      } finally {
        this.loading = false
      }
    },

    // ===== Modal =====

    openCreateModal() {
      this.resetForm()
      // Mặc định chọn ngày hôm nay
      this.form.createDate = new Date().toISOString().split('T')[0]
      this.modal = { show: true, isEdit: false }
    },

    openEditModal(product) {
      this.resetForm()
      // Điền thông tin sản phẩm cũ vào form
      this.form = {
        name:       product.name,
        price:      product.price,
        createDate: product.createDate || '',
        categoryId: product.categoryId
      }
      this.modal = { show: true, isEdit: true, editId: product.id }
    },

    closeModal() {
      this.modal.show = false
      this.resetForm()
    },

    resetForm() {
      this.form = { name: '', price: '', createDate: '', categoryId: '' }
      this.formErrors = { name: '', price: '', categoryId: '', server: '' }
    },

    // ===== Validate =====

    validateForm() {
      let valid = true
      this.formErrors = { name: '', price: '', categoryId: '', server: '' }

      if (!this.form.name.trim()) {
        this.formErrors.name = 'Tên sản phẩm không được để trống'
        valid = false
      }

      if (!this.form.price || this.form.price <= 0) {
        this.formErrors.price = 'Giá phải là số lớn hơn 0'
        valid = false
      }

      if (!this.form.categoryId) {
        this.formErrors.categoryId = 'Vui lòng chọn loại hàng'
        valid = false
      }

      return valid
    },

    // ===== Submit =====

    async submitForm() {
      if (!this.validateForm()) return

      this.submitting = true
      try {
        // Chuẩn bị payload gửi lên server
        const payload = {
          name:       this.form.name,
          price:      this.form.price,
          createDate: this.form.createDate || null,
          categoryId: this.form.categoryId
        }

        let response
        if (this.modal.isEdit) {
          response = await productApi.update(this.modal.editId, payload)
        } else {
          response = await productApi.create(payload)
        }

        if (response.success) {
          this.showToast(response.message, 'success')
          this.closeModal()
          await this.loadAll()
        } else {
          this.formErrors.server = response.message
        }
      } catch (err) {
        this.formErrors.server = err.message || 'Đã xảy ra lỗi, vui lòng thử lại'
      } finally {
        this.submitting = false
      }
    },

    // ===== Xóa =====

    confirmDelete(product) {
      this.deleteConfirm = { show: true, target: product }
    },

    async executeDelete() {
      this.submitting = true
      try {
        const response = await productApi.delete(this.deleteConfirm.target.id)

        if (response.success) {
          this.showToast(response.message, 'success')
          this.deleteConfirm.show = false
          await this.loadAll()
        } else {
          this.showToast(response.message, 'error')
          this.deleteConfirm.show = false
        }
      } catch (err) {
        this.showToast(err.message || 'Xóa thất bại', 'error')
      } finally {
        this.submitting = false
      }
    },

    // ===== Helpers hiển thị =====
    formatPrice(price) {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(price)
    },

    formatDate(dateStr) {
      if (!dateStr) return '—'
      const [y, m, d] = dateStr.split('-')
      return `${d}/${m}/${y}`
    },

    showToast(message, type = 'success') {
      this.toast = { show: true, message, type }
      setTimeout(() => { this.toast.show = false }, 3000)
    }
  },

  mounted() {
    this.loadAll()
  }
}
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 12px;
}
.toolbar-left { display: flex; gap: 10px; flex: 1; }
.search-input { max-width: 280px; }

.price-tag {
  font-weight: 600;
  color: var(--primary);
  font-size: 13px;
}

.error-server {
  background: #fee2e2;
  border: 1px solid #fecaca;
  color: #991b1b;
  padding: 10px 12px;
  border-radius: 7px;
  font-size: 13px;
}
</style>