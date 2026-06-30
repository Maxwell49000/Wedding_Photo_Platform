export function buildPhotoViewUrl(id) {
  return `/api/photos/${id}/view`
}

export function buildPhotoDownloadUrl(id) {
  return `/api/photos/${id}/download`
}

export function filterPhotos(photos, search) {
  const query = search.trim().toLowerCase()
  if (!query) {
    return photos
  }

  return photos.filter((photo) => {
    return [photo.filename, photo.description]
      .filter(Boolean)
      .some((value) => value.toLowerCase().includes(query))
  })
}
