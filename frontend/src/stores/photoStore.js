import { defineStore } from 'pinia'
import { fetchPhotos } from '../services/photoService'

export const usePhotoStore = defineStore('photoStore', {
  state: () => ({
    photos: [],
    loading: false,
    error: null,
  }),
  actions: {
    async loadPhotos(options = {}) {
      this.loading = true
      this.error = null
      try {
        this.photos = await fetchPhotos(options)
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    },
  },
})
