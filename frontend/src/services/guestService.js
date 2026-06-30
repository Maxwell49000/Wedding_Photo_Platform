import { http } from './http'

export async function fetchGuests() {
  const { data } = await http.get('/guests')
  return data
}

export async function createGuest(payload) {
  const { data } = await http.post('/guests', payload)
  return data
}

export async function updateGuest(id, payload) {
  const { data } = await http.put(`/guests/${id}`, payload)
  return data
}

export async function deleteGuest(id) {
  await http.delete(`/guests/${id}`)
}
