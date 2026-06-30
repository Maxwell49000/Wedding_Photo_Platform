import { http } from './http'

export async function fetchGuests() {
  const { data } = await http.get('/guests')
  return data
}
