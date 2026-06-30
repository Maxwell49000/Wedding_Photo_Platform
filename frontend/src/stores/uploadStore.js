import { defineStore } from 'pinia'

export const useUploadStore = defineStore('uploadStore', {
  state: () => ({
    uploaderGuestId: null,
    photographerGuestId: null,
    people: [],
    comment: '',
    files: [],
    progress: 0,
    submitting: false,
    error: null,
    successCount: 0,
  }),
})
