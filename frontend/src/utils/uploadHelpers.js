export function createUploadFormData({ files, description, uploaderGuestId, photographerGuestId }) {
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

  return formData
}
