import test from 'node:test'
import assert from 'node:assert/strict'
import { buildPhotoDownloadUrl, buildPhotoViewUrl, filterPhotos } from '../src/utils/photoHelpers.js'

test('builds viewer and download urls', () => {
  assert.equal(buildPhotoViewUrl('123'), '/api/photos/123/view')
  assert.equal(buildPhotoDownloadUrl('123'), '/api/photos/123/download')
})

test('filters photos by filename or description', () => {
  const photos = [
    { filename: 'first.jpg', description: 'Sunset dance' },
    { filename: 'second.jpg', description: 'Ceremony' },
  ]

  const result = filterPhotos(photos, 'sun')

  assert.equal(result.length, 1)
  assert.equal(result[0].filename, 'first.jpg')
})
