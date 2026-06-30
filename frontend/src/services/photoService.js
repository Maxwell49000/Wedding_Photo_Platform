import { http } from './http'
import { buildPhotoDownloadUrl, buildPhotoViewUrl } from '../utils/photoHelpers'

export async function fetchPhotos({ includeHidden = false } = {}) {
  const { data } = await http.get('/photos', {
    params: { includeHidden },
  })
  return data
}

export async function fetchPhoto(id) {
  const { data } = await http.get(`/photos/${id}`)
  return data
}

export async function updatePhoto(id, payload) {
  const { data } = await http.put(`/photos/${id}`, payload)
  return data
}

export async function deletePhoto(id) {
  await http.delete(`/photos/${id}`)
}

export function getPhotoViewUrl(id) {
  return buildPhotoViewUrl(id)
}

export function getPhotoDownloadUrl(id) {
  return buildPhotoDownloadUrl(id)
}
