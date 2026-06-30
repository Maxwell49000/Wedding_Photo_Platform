import { defineStore } from 'pinia'

export const useGalleryStore = defineStore('galleryStore', {
  state: () => ({
    search: '',
    filter: 'all',
  }),
})
