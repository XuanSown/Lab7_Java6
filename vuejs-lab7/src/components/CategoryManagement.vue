<template>
  <div>
    <!-- ===== TOAST NOTIFICATION ===== -->
    <div v-if="toast.show" :class="['toast', `toast-${toast.type}`]">
      {{ toast.message }}
    </div>

    <!-- ===== TOOLBAR ===== -->
    <div class="toolbar">
      <div class="toolbar-left">
        <!-- Ô tìm kiếm -->
        <input
            v-model="searchKeyword"
            class="form-control search-input"
            placeholder="Tìm theo tên loại hàng..."
        />
      </div>
      <div class="toolbar-right">
        <button class="btn btn-primary" @click="openCreateModal">
          Thêm loại hàng
        </button>
      </div>
    </div>

    <!-- ===== BẢNG DANH SÁCH ===== -->
    <div class="card">
      <div class="card-header">
        <h2>Danh sách loại hàng</h2>
        <span class="badge badge-primary">{{ filteredCategories.length }} loại</span>
      </div>

      <!-- Đang tải -->
      <div v-if="loading" class="loading-wrap">
        <div class="spinner"></div>
      </div>

      <!-- Lỗi kết nối -->
      <div v-else-if="loadError" class="empty-state">
        <p style="color: var(--danger)">{{ loadError }}</p>
        <button class="btn btn-secondary" style="margin-top:12px" @click="loadCategories">
          Thử lại
        </button>
      </div>

      <!-- Bảng dữ liệu -->
      <div v-else class="table-wrap">
        <table>
          <thead>
          <tr>
            <th style="width:60px">#</th>
            <th>Mã loại hàng</th>
            <th>Tên loại hàng</th>
            <th style="width:140px; text-align:center">Thao tác</th>
          </tr>
          </thead>
          <tbody>
          <!-- Không có dữ liệu -->
          <tr v-if="filteredCategories.length === 0">
            <td colspan="4">
              <div class="empty-state">
                <p>Chưa có loại hàng nào</p>
              </div>
            </td>
          </tr>

          <!-- Dữ liệu thật -->
          <tr v-for="(cat, index) in filteredCategories" :key="cat.id">
            <td style="color: var(--text-muted)">{{ index + 1 }}</td>
            <td>
              <span class="badge badge-primary">{{ cat.id }}</span>
            </td>
            <td style="font-weight: 500">{{ cat.name }}</td>
            <td style="text-align: center">
              <div style="display: flex; gap: 6px; justify-content: center">
                <!-- Nút sửa -->
                <button
                    class="btn btn-secondary btn-sm"
                    @click="openEditModal(cat)"
                    title="Chỉnh sửa"
                >Sửa</button>

                <!-- Nút xóa -->
                <button
                    class="btn btn-danger btn-sm"
                    @click="confirmDelete(cat)"
                    title="Xóa"
                >Xóa</button>
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
          <h3>{{ modal.isEdit ? 'Cập nhật loại hàng' : 'Thêm loại hàng mới' }}</h3>
          <button class="modal-close" @click="closeModal">✕</button>
        </div>

        <div class="modal-body">
          <!-- Field: Mã loại hàng -->
          <div class="form-group">
            <label class="form-label">Mã loại hàng *</label>
            <input
                v-model="form.id"
                :class="['form-control', { error: formErrors.id }]"
                placeholder="VD: DIEN-TU, THOI-TRANG..."
                :disabled="modal.isEdit"
            />
            <!--
              Khi sửa: disable mã vì là khóa chính, không được đổi
              Khi thêm: cho phép nhập tự do
            -->
            <p v-if="formErrors.id" class="error-msg">{{ formErrors.id }}</p>
            <p v-if="modal.isEdit" style="font-size:11px; color:var(--text-muted); margin-top:4px">
              Mã loại hàng không thể thay đổi sau khi tạo
            </p>
          </div>

          <!-- Field: Tên loại hàng -->
          <div class="form-group">
            <label class="form-label">Tên loại hàng *</label>
            <input
                v-model="form.name"
                :class="['form-control', { error: formErrors.name }]"
                placeholder="VD: Điện tử, Thời trang..."
                @keyup.enter="submitForm"
            />
            <p v-if="formErrors.name" class="error-msg">{{ formErrors.name }}</p>
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
              :disabled="submitting"
          >
            {{ submitting ? 'Đang lưu...' : (modal.isEdit ? 'Cập nhật' : 'Thêm mới') }}
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
            Bạn có chắc muốn xóa loại hàng
            <strong>{{ deleteConfirm.target?.name }}</strong>?
          </p>
          <p style="font-size:12px; color:var(--danger); margin-top:8px">
            Không thể xóa nếu còn sản phẩm thuộc loại này.
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
import { categoryApi } from '../api.js'

export default {
  name: 'CategoryManagement',

  // emit 'api-status' lên App.vue để cập nhật badge online/offline
  emits: ['api-status'],

  data() {
    return {
      // ===== Dữ liệu =====
      categories: [],       // Danh sách loại hàng từ API
      searchKeyword: '',    // Từ khóa tìm kiếm

      // ===== Trạng thái UI =====
      loading: false,       // Đang tải danh sách
      loadError: null,      // Lỗi khi tải danh sách
      submitting: false,    // Đang gửi form (submit)

      // ===== Modal thêm/sửa =====
      modal: {
        show: false,
        isEdit: false,      // true = đang sửa, false = đang thêm mới
      },

      // ===== Form data =====
      form: { id: '', name: '' },

      // ===== Lỗi validation form =====
      formErrors: { id: '', name: '', server: '' },

      // ===== Modal xác nhận xóa =====
      deleteConfirm: {
        show: false,
        target: null,
      },

      // ===== Toast thông báo =====
      toast: { show: false, message: '', type: 'success' },
    }
  },

  computed: {
    filteredCategories() {
      if (!this.searchKeyword.trim()) return this.categories
      const kw = this.searchKeyword.toLowerCase()
      return this.categories.filter(c =>
          c.name.toLowerCase().includes(kw) ||
          c.id.toLowerCase().includes(kw)
      )
    }
  },

  methods: {
    // ===== Tải danh sách =====

    async loadCategories() {
      this.loading = true
      this.loadError = null

      try {
        const response = await categoryApi.getAll()
        // response = { success, message, data: [...] }
        this.categories = response.data || []
        this.$emit('api-status', true)   // API online
      } catch (err) {
        this.loadError = err.message || 'Không thể tải danh sách loại hàng'
        this.$emit('api-status', false)  // API offline
      } finally {
        this.loading = false
      }
    },

    // ===== Mở modal =====

    openCreateModal() {
      this.resetForm()
      this.modal = { show: true, isEdit: false }
    },

    openEditModal(category) {
      this.resetForm()
      // Điền dữ liệu cũ vào form để chỉnh sửa
      this.form = { id: category.id, name: category.name }
      this.modal = { show: true, isEdit: true }
    },

    closeModal() {
      this.modal.show = false
      this.resetForm()
    },

    resetForm() {
      this.form = { id: '', name: '' }
      this.formErrors = { id: '', name: '', server: '' }
    },

    // ===== Validate form phía client =====

    validateForm() {
      let valid = true
      this.formErrors = { id: '', name: '', server: '' }

      if (!this.form.id.trim()) {
        this.formErrors.id = 'Mã loại hàng không được để trống'
        valid = false
      } else if (this.form.id.length > 50) {
        this.formErrors.id = 'Mã loại hàng không được vượt quá 50 ký tự'
        valid = false
      }

      if (!this.form.name.trim()) {
        this.formErrors.name = 'Tên loại hàng không được để trống'
        valid = false
      } else if (this.form.name.length > 100) {
        this.formErrors.name = 'Tên loại hàng không được vượt quá 100 ký tự'
        valid = false
      }

      return valid
    },

    // ===== Submit form =====

    async submitForm() {
      // Validate client-side trước, nếu sai thì dừng
      if (!this.validateForm()) return

      this.submitting = true
      try {
        let response

        if (this.modal.isEdit) {
          // Gọi API cập nhật: PUT /api/categories/{id}
          response = await categoryApi.update(this.form.id, this.form)
        } else {
          // Gọi API tạo mới: POST /api/categories
          response = await categoryApi.create(this.form)
        }

        if (response.success) {
          this.showToast(response.message, 'success')
          this.closeModal()
          await this.loadCategories() // Tải lại danh sách sau khi lưu thành công
        } else {
          // Lỗi nghiệp vụ từ server (vd: mã đã tồn tại)
          this.formErrors.server = response.message
        }
      } catch (err) {
        this.formErrors.server = err.message || 'Đã xảy ra lỗi, vui lòng thử lại'
      } finally {
        this.submitting = false
      }
    },

    // ===== Xóa =====

    confirmDelete(category) {
      this.deleteConfirm = { show: true, target: category }
    },

    async executeDelete() {
      this.submitting = true
      try {
        const response = await categoryApi.delete(this.deleteConfirm.target.id)

        if (response.success) {
          this.showToast(response.message, 'success')
          this.deleteConfirm.show = false
          await this.loadCategories()
        } else {
          // Không xóa được (vd: còn sản phẩm đang dùng)
          this.showToast(response.message, 'error')
          this.deleteConfirm.show = false
        }
      } catch (err) {
        this.showToast(err.message || 'Xóa thất bại', 'error')
      } finally {
        this.submitting = false
      }
    },

    // ===== Toast =====
    showToast(message, type = 'success') {
      this.toast = { show: true, message, type }
      setTimeout(() => { this.toast.show = false }, 3000)
    }
  },

  mounted() {
    this.loadCategories()
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
.search-input { max-width: 300px; }
.error-server {
  background: #fee2e2; border: 1px solid #fecaca;
  color: #991b1b; padding: 10px 12px;
  border-radius: 7px; font-size: 13px;
}
</style>