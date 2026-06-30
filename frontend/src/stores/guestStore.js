import { defineStore } from 'pinia'
import { createGuest, deleteGuest, fetchGuests, updateGuest } from '../services/guestService'

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
    async createGuest(payload) {
      const guest = await createGuest(payload)
      this.guests = [...this.guests, guest]
      return guest
    },
    async updateGuest(id, payload) {
      const guest = await updateGuest(id, payload)
      this.guests = this.guests.map((item) => (item.id === id ? guest : item))
      return guest
    },
    async deleteGuest(id) {
      await deleteGuest(id)
      this.guests = this.guests.filter((guest) => guest.id !== id)
    },
  },
})
