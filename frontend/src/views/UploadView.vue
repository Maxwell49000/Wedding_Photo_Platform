<template>
  <div class="page-shell upload-page">
    <section class="page-header">
      <div>
        <p class="eyebrow">Upload</p>
        <h1 class="section-title">Ajouter des photos</h1>
        <p class="section-copy">Sélectionnez vos images, renseignez le contexte, puis envoyez le tout en une seule fois.</p>
      </div>
    </section>

    <section class="upload-layout">
      <v-card class="panel panel--form" elevation="0">
        <v-card-title class="panel-title">
          <v-icon color="primary">mdi-form-select</v-icon>
          Informations
        </v-card-title>

        <v-card-text class="form-grid">
          <v-autocomplete
            v-model="upload.uploaderGuestId"
            :items="guestItems"
            item-title="title"
            item-value="value"
            label="Uploader"
            variant="outlined"
            :loading="guestStore.loading"
          />
          <v-autocomplete
            v-model="upload.photographerGuestId"
            :items="guestItems"
            item-title="title"
            item-value="value"
            label="Photographe"
            variant="outlined"
            :loading="guestStore.loading"
          />
          <v-combobox
            v-model="upload.people"
            label="Personnes présentes"
            multiple
            chips
            variant="outlined"
          />
          <v-textarea
            v-model="upload.comment"
            label="Commentaire"
            variant="outlined"
            rows="4"
            auto-grow
          />
        </v-card-text>

        <v-divider />

        <v-card-text class="dropzone-block">
          <div
            class="dropzone"
            :class="{ 'dropzone--active': isDragging }"
            @dragenter.prevent="handleDragEnter"
            @dragover.prevent="handleDragOver"
            @dragleave.prevent="handleDragLeave"
            @drop.prevent="handleDrop"
          >
            <input
              ref="fileInput"
              class="dropzone__input"
              type="file"
              multiple
              accept="image/*"
              @change="handleFileChange"
            >

            <v-icon size="42" color="primary">mdi-cloud-upload-outline</v-icon>
            <div class="dropzone__copy">
              <strong>Glissez vos images ici</strong>
              <span>ou choisissez des fichiers depuis votre appareil</span>
            </div>
            <v-btn variant="flat" color="primary" prepend-icon="mdi-folder-image" @click.stop="openFilePicker">
              Choisir des fichiers
            </v-btn>
          </div>

          <v-alert
            v-if="upload.files.length"
            type="info"
            variant="tonal"
            density="comfortable"
            class="status-alert"
          >
            {{ upload.files.length }} image(s) sélectionnée(s)
            <template #append>
              <v-btn variant="text" size="small" @click="clearFiles">Vider</v-btn>
            </template>
          </v-alert>

          <div v-if="upload.files.length" class="file-list">
            <v-chip
              v-for="file in upload.files"
              :key="fileKey(file)"
              size="small"
              variant="tonal"
              color="primary"
              closable
              @click:close="removeFile(file)"
            >
              {{ file.name }}
            </v-chip>
          </div>
        </v-card-text>

        <v-card-text class="upload-footer">
          <div class="progress-line">
            <div class="progress-line__header">
              <span>Progression</span>
              <strong>{{ upload.progress }}%</strong>
            </div>
            <v-progress-linear
              :model-value="upload.progress"
              color="primary"
              height="10"
              rounded
            />
          </div>

          <v-alert
            v-if="upload.error"
            type="error"
            variant="tonal"
            density="comfortable"
            class="status-alert"
          >
            {{ upload.error }}
          </v-alert>

          <v-alert
            v-if="upload.successCount"
            type="success"
            variant="tonal"
            density="comfortable"
            class="status-alert"
          >
            {{ upload.successCount }} photo(s) envoyée(s) avec succès.
          </v-alert>
        </v-card-text>

        <v-card-actions class="actions">
          <v-btn
            color="primary"
            size="large"
            prepend-icon="mdi-send"
            :loading="upload.submitting"
            :disabled="!canSubmit"
            @click="submitUpload"
          >
            Envoyer les photos
          </v-btn>
        </v-card-actions>
      </v-card>

      <v-card class="panel panel--preview" elevation="0">
        <v-card-title class="panel-title">
          <v-icon color="primary">mdi-image-frame</v-icon>
          Aperçu
        </v-card-title>

        <v-card-text class="preview-content">
          <div v-if="coverPreview" class="preview-hero">
            <v-img
              :src="coverPreview.url"
              :alt="coverPreview.file.name"
              class="preview-hero__image"
              cover
            />
            <div class="preview-hero__overlay">
              <span>Aperçu principal</span>
              <strong>{{ coverPreview.file.name }}</strong>
              <small>{{ coverPreview.formattedSize }}</small>
            </div>
          </div>

          <div v-else class="preview-empty">
            <v-icon size="52" color="primary">mdi-image-outline</v-icon>
            <strong>Aucun fichier sélectionné</strong>
            <span>Ajoutez une ou plusieurs photos pour voir un aperçu avant validation.</span>
          </div>

          <div v-if="previewItems.length" class="thumbnail-strip">
            <button
              v-for="item in previewItems"
              :key="item.key"
              type="button"
              class="thumbnail"
              :class="{ 'thumbnail--active': item.key === coverPreview?.key }"
              @click="selectedPreviewKey = item.key"
            >
              <v-img :src="item.url" :alt="item.file.name" cover class="thumbnail__image" />
            </button>
          </div>

          <div class="preview-meta">
            <v-chip variant="tonal" color="secondary">{{ previewItems.length }} fichier(s)</v-chip>
            <v-chip variant="tonal" color="primary">{{ totalSizeLabel }}</v-chip>
          </div>
        </v-card-text>
      </v-card>
    </section>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useGuestStore } from '../stores/guestStore'
import { useUploadStore } from '../stores/uploadStore'
import { uploadPhotos } from '../services/uploadService'

const upload = useUploadStore()
const guestStore = useGuestStore()

const fileInput = ref(null)
const isDragging = ref(false)
const previewMap = ref(new Map())
const selectedPreviewKey = ref(null)

onMounted(async () => {
  await guestStore.loadGuests()
})

const guestItems = computed(() => {
  return guestStore.guests.map((guest) => ({
    title: guest.displayName,
    value: guest.id,
  }))
})

const canSubmit = computed(() => upload.files.length > 0 && !upload.submitting)

function fileKey(file) {
  return `${file.name}-${file.size}-${file.lastModified}`
}

function formatBytes(bytes) {
  if (!bytes) {
    return '0 B'
  }

  const units = ['B', 'KB', 'MB', 'GB']
  const index = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), units.length - 1)
  const value = bytes / (1024 ** index)

  return `${value.toFixed(value >= 10 || index === 0 ? 0 : 1)} ${units[index]}`
}

function syncPreviewUrls(files) {
  const nextMap = new Map()

  for (const file of files) {
    const key = fileKey(file)
    const existing = previewMap.value.get(key)
    nextMap.set(key, existing ?? URL.createObjectURL(file))
  }

  for (const [key, url] of previewMap.value.entries()) {
    if (!nextMap.has(key)) {
      URL.revokeObjectURL(url)
    }
  }

  previewMap.value = nextMap

  if (!selectedPreviewKey.value || !nextMap.has(selectedPreviewKey.value)) {
    selectedPreviewKey.value = files[0] ? fileKey(files[0]) : null
  }
}

watch(
  () => upload.files,
  (files) => syncPreviewUrls(files),
  { immediate: true },
)

onBeforeUnmount(() => {
  for (const url of previewMap.value.values()) {
    URL.revokeObjectURL(url)
  }
})

const previewItems = computed(() => {
  return upload.files.map((file) => {
    const key = fileKey(file)
    return {
      key,
      file,
      url: previewMap.value.get(key),
      formattedSize: formatBytes(file.size),
    }
  })
})

const coverPreview = computed(() => {
  if (!previewItems.value.length) {
    return null
  }

  return (
    previewItems.value.find((item) => item.key === selectedPreviewKey.value) ??
    previewItems.value[0]
  )
})

const totalSizeLabel = computed(() => {
  const total = upload.files.reduce((sum, file) => sum + file.size, 0)
  return `${formatBytes(total)} au total`
})

function addFiles(incomingFiles) {
  const byKey = new Map(upload.files.map((file) => [fileKey(file), file]))

  for (const file of incomingFiles) {
    if (file.type.startsWith('image/')) {
      byKey.set(fileKey(file), file)
    }
  }

  upload.files = Array.from(byKey.values())
}

function openFilePicker() {
  fileInput.value?.click()
}

function handleFileChange(event) {
  addFiles(Array.from(event.target.files ?? []))
  event.target.value = ''
  isDragging.value = false
}

function handleDragEnter() {
  isDragging.value = true
}

function handleDragOver() {
  isDragging.value = true
}

function handleDragLeave(event) {
  if (!event.currentTarget?.contains(event.relatedTarget)) {
    isDragging.value = false
  }
}

function handleDrop(event) {
  isDragging.value = false
  addFiles(Array.from(event.dataTransfer?.files ?? []))
}

function removeFile(fileToRemove) {
  upload.files = upload.files.filter((file) => fileKey(file) !== fileKey(fileToRemove))
}

function clearFiles() {
  upload.files = []
}

async function submitUpload() {
  upload.submitting = true
  upload.error = null
  upload.successCount = 0
  upload.progress = 0

  try {
    const result = await uploadPhotos({
      files: upload.files,
      description: upload.comment,
      uploaderGuestId: upload.uploaderGuestId,
      photographerGuestId: upload.photographerGuestId,
      onProgress(value) {
        upload.progress = value
      },
    })

    upload.successCount = Array.isArray(result) ? result.length : 0
    upload.progress = 100
    upload.files = []
  } catch (error) {
    upload.error = error?.response?.data?.message ?? 'Upload impossible'
  } finally {
    upload.submitting = false
  }
}
</script>

<style scoped>
.page-header {
  margin-bottom: 1.5rem;
}

.upload-layout {
  display: grid;
  gap: 1rem;
  grid-template-columns: minmax(0, 1.08fr) minmax(320px, 0.92fr);
  align-items: start;
}

.panel {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  box-shadow: 0 12px 28px rgba(22, 33, 38, 0.06);
}

.panel-title {
  display: flex;
  gap: 0.65rem;
  align-items: center;
  padding: 1.15rem 1.15rem 0.4rem;
  font-weight: 850;
}

.form-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.dropzone-block,
.upload-footer,
.preview-content {
  display: grid;
  gap: 1rem;
}

.dropzone {
  position: relative;
  display: grid;
  gap: 0.85rem;
  justify-items: center;
  min-height: 220px;
  padding: 1.4rem;
  border: 2px dashed rgba(33, 90, 87, 0.32);
  border-radius: 8px;
  background: var(--app-surface-soft);
  text-align: center;
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    box-shadow 0.18s ease;
}

.dropzone:hover,
.dropzone--active {
  border-color: var(--app-primary);
  background: rgba(33, 90, 87, 0.1);
  box-shadow: 0 16px 34px rgba(33, 90, 87, 0.12);
}

.dropzone__input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.dropzone__copy {
  display: grid;
  gap: 0.25rem;
}

.dropzone__copy span,
.progress-line__header {
  color: var(--app-muted);
}

.dropzone .v-icon,
.dropzone__copy,
.dropzone .v-btn {
  position: relative;
  z-index: 1;
}

.status-alert {
  border-radius: 8px;
}

.file-list,
.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.progress-line {
  display: grid;
  gap: 0.45rem;
}

.progress-line__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.actions {
  justify-content: end;
  padding: 0 1.15rem 1.15rem;
}

.panel--preview {
  position: sticky;
  top: 84px;
}

.preview-hero,
.preview-empty {
  min-height: 390px;
  overflow: hidden;
  border-radius: 8px;
}

.preview-hero {
  position: relative;
  background: var(--app-surface-soft);
}

.preview-hero__image {
  height: 390px;
}

.preview-hero__overlay {
  position: absolute;
  inset: auto 0 0 0;
  display: grid;
  gap: 0.15rem;
  padding: 1rem;
  background: linear-gradient(180deg, transparent, rgba(10, 18, 20, 0.86));
  color: #fff;
}

.preview-hero__overlay span {
  color: rgba(255, 255, 255, 0.76);
  font-size: 0.76rem;
  font-weight: 800;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.preview-hero__overlay strong {
  overflow-wrap: anywhere;
}

.preview-empty {
  display: grid;
  gap: 0.5rem;
  place-items: center;
  align-content: center;
  padding: 2rem;
  border: 1px dashed var(--app-border);
  background: var(--app-surface-soft);
  text-align: center;
}

.preview-empty span {
  max-width: 30ch;
  color: var(--app-muted);
}

.thumbnail-strip {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: 78px;
  gap: 0.65rem;
  overflow-x: auto;
  padding-bottom: 0.25rem;
}

.thumbnail {
  overflow: hidden;
  padding: 0;
  border: 2px solid transparent;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
}

.thumbnail--active {
  border-color: var(--app-primary);
}

.thumbnail__image {
  width: 100%;
  aspect-ratio: 1 / 1;
}

@media (max-width: 960px) {
  .upload-layout {
    grid-template-columns: 1fr;
  }

  .panel--preview {
    position: static;
  }
}

@media (max-width: 600px) {
  .actions {
    display: grid;
    padding-inline: 1rem;
  }

  .preview-hero,
  .preview-empty {
    min-height: 290px;
  }

  .preview-hero__image {
    height: 290px;
  }
}
</style>
