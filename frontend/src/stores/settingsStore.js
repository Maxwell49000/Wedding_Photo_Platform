import { defineStore } from 'pinia'

export const useSettingsStore = defineStore('settingsStore', {
  state: () => ({
    weddingCode: '',
    apiBaseUrl: import.meta.env.VITE_API_BASE_URL ?? '/api',
  }),
})
