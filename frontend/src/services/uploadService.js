import { http } from './http'

export async function uploadPhotos({
  files,
  description,
  uploaderGuestId,
  photographerGuestId,
  onProgress,
}) {
  const formData = new FormData()

  for (const file of files) {
    formData.append('files', file)
  }

  if (description) {
    formData.append('description', description)
  }

  if (uploaderGuestId) {
    formData.append('uploaderGuestId', uploaderGuestId)
  }

  if (photographerGuestId) {
    formData.append('photographerGuestId', photographerGuestId)
  }

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
