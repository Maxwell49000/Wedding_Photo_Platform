import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const adminCode = sessionStorage.getItem('wedding-photo-admin-code')
  if (adminCode) {
    config.headers['X-Admin-Code'] = adminCode
  }
  return config
})
