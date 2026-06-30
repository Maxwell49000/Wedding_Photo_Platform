import test from 'node:test'
import assert from 'node:assert/strict'
import { createUploadFormData } from '../src/utils/uploadHelpers.js'

test('creates upload form data with files and metadata', () => {
  const file = new Blob(['hello'], { type: 'image/jpeg' })
  const formData = createUploadFormData({
    files: [file],
    description: 'Wedding moment',
    uploaderGuestId: 'guest-1',
    photographerGuestId: 'guest-2',
  })

  assert.equal(formData.getAll('files').length, 1)
  assert.equal(formData.get('description'), 'Wedding moment')
  assert.equal(formData.get('uploaderGuestId'), 'guest-1')
  assert.equal(formData.get('photographerGuestId'), 'guest-2')
})
