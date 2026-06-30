import { defineStore } from 'pinia'
import { fetchGuests } from '../services/guestService'

export const useGuestStore = defineStore('guestStore', {
  state: () => ({
    guests: [],
    loading: false,
    error: null,
  }),
  actions: {
    async loadGuests() {
      this.loading = true
      this.error = null
      try {
        this.guests = await fetchGuests()
      } catch (error) {
        this.error = error
      } finally {
        this.loading = false
      }
    },
  },
})
