import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 1000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// Interceptor xử lí response chung
api.interceptors.response.use(
    //trường hợp thanh công 200
    response => response.data,
    //lỗi 4xx,5xx or network error
    error => {
        if (error.code === 'ECONNABORTED') {
            return Promise.reject({messsage: 'Kết nối quá chậm!'})
        }

        if(!error.response) {
            //không nhận được response
            return Promise.reject({message: 'Không thể kết nối với server'})
        }

        //lôĩ server - trả về data lỗi từ ApiResponse
        return Promise.reject(error.response.data)
    }
)

//API cho Category
export const categoryApi = {
    getAll: () => api.get('/categories'),
    getById: id => api.get(`/categories/${id}`),
    create: category => api.post('/categories', data),
    update: (id, category) => api.put(`/categories/${id}`, data),
    delete: id => api.delete(`/categories/${id}`)
}

//API cho Product
export const productApi = {
    getAll: (categoryId = null) =>{
        const params = categoryId ? {categoryId} : {}
        return api.get('/products', {params})
    }
}

export default api