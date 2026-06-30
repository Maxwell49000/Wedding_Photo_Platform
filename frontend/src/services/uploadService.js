import { http } from './http'
import { createUploadFormData } from '../utils/uploadHelpers'

export async function uploadPhotos({
  files,
  description,
  uploaderGuestId,
  photographerGuestId,
  onProgress,
}) {
  const formData = createUploadFormData({
    files,
    description,
    uploaderGuestId,
    photographerGuestId,
  })

  const { data } = await http.post('/photos/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    onUploadProgress(event) {
      if (!onProgress || !event.total) {
        return
      }

      onProgress(Math.round((event.loaded / event.total) * 100))
    },
  })

  return data
}
