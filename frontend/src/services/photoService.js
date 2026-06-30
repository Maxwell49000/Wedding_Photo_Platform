import { http } from './http'

export async function fetchPhotos() {
  const { data } = await http.get('/photos')
  return data
}
