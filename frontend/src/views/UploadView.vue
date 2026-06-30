<template>
  <div class="page">
    <AppHero
      eyebrow="Upload"
      title="Déposez, vérifiez, puis envoyez."
      subtitle="Sélectionnez vos images, ajoutez un contexte et visualisez le rendu avant validation."
    />

    <section class="upload-layout">
      <v-card rounded="xl" class="panel panel--form" elevation="0">
        <v-card-title class="panel-title">
          <div>
            <span class="panel-kicker">Étape 1</span>
            <h2>Métadonnées</h2>
          </div>
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

            <v-icon size="44" color="primary" class="dropzone__icon">mdi-cloud-upload</v-icon>
            <div class="dropzone__copy">
              <p class="dropzone__title">Glissez vos images ici</p>
              <p class="dropzone__subtitle">ou cliquez pour ouvrir le sélecteur de fichiers</p>
            </div>
            <v-btn variant="tonal" color="primary" class="dropzone__button" @click.stop="openFilePicker">
              Choisir des fichiers
            </v-btn>
          </div>

          <v-alert
            v-if="upload.files.length"
            type="info"
            variant="tonal"
            density="comfortable"
            class="upload-summary"
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
            :loading="upload.submitting"
            :disabled="!canSubmit"
            @click="submitUpload"
          >
            Envoyer les photos
          </v-btn>
        </v-card-actions>
      </v-card>

      <v-card rounded="xl" class="panel panel--preview" elevation="0">
        <v-card-title class="panel-title">
          <div>
            <span class="panel-kicker">Étape 2</span>
            <h2>Aperçu</h2>
          </div>
        </v-card-title>

        <v-card-text>
          <div v-if="coverPreview" class="preview-hero">
            <v-img
              :src="coverPreview.url"
              :alt="coverPreview.file.name"
              class="preview-hero__image"
              cover
            />
            <div class="preview-hero__overlay">
              <p class="preview-hero__label">Aperçu principal</p>
              <h3>{{ coverPreview.file.name }}</h3>
              <p>{{ coverPreview.formattedSize }}</p>
            </div>
          </div>

          <div v-else class="preview-empty">
            <v-icon size="52" color="primary">mdi-image-outline</v-icon>
            <h3>Aucun fichier sélectionné</h3>
            <p>Ajoutez une ou plusieurs photos pour voir un aperçu avant validation.</p>
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
import AppHero from '../components/AppHero.vue'
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
.page {
  width: min(100%, 1240px);
  margin: 0 auto;
  padding: 0 1.5rem 4rem;
}

.upload-layout {
  display: grid;
  gap: 1.25rem;
  grid-template-columns: minmax(0, 1.12fr) minmax(320px, 0.88fr);
  align-items: start;
}

.panel {
  background: rgba(255, 253, 248, 0.94);
  border: 1px solid rgba(140, 90, 82, 0.08);
  box-shadow: 0 16px 34px rgba(46, 38, 34, 0.06);
}

.panel-title {
  padding: 1.25rem 1.25rem 0;
}

.panel-kicker {
  display: block;
  margin-bottom: 0.2rem;
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.16em;
  color: rgba(60, 47, 43, 0.58);
}

.panel-title h2 {
  margin: 0;
  font-size: 1.2rem;
}

.form-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.dropzone-block {
  display: grid;
  gap: 1rem;
}

.dropzone {
  display: grid;
  gap: 0.85rem;
  justify-items: center;
  text-align: center;
  border: 1.5px dashed rgba(140, 90, 82, 0.38);
  border-radius: 24px;
  background:
    radial-gradient(circle at top, rgba(255, 255, 255, 0.9), transparent 48%),
    rgba(140, 90, 82, 0.04);
  padding: 1.4rem;
  cursor: pointer;
  transition:
    border-color 0.18s ease,
    background-color 0.18s ease,
    transform 0.18s ease,
    box-shadow 0.18s ease;
  position: relative;
  overflow: hidden;
}

.dropzone:hover {
  border-color: rgba(140, 90, 82, 0.68);
  background: rgba(140, 90, 82, 0.06);
  transform: translateY(-1px);
}

.dropzone--active {
  border-color: #8c5a52;
  background: rgba(140, 90, 82, 0.1);
  box-shadow: 0 16px 34px rgba(140, 90, 82, 0.12);
}

.dropzone__input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.dropzone__icon,
.dropzone__copy,
.dropzone__button {
  position: relative;
  z-index: 1;
}

.dropzone__copy {
  display: grid;
  gap: 0.25rem;
}

.dropzone__title {
  font-weight: 700;
  font-size: 1.05rem;
  margin: 0;
}

.dropzone__subtitle {
  margin: 0;
  color: rgba(48, 52, 56, 0.7);
}

.upload-summary {
  border-radius: 18px;
}

.file-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.upload-footer {
  display: grid;
  gap: 1rem;
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
  color: rgba(48, 52, 56, 0.72);
}

.status-alert {
  border-radius: 18px;
}

.actions {
  justify-content: end;
  padding: 0 1.25rem 1.25rem;
}

.panel--preview {
  position: sticky;
  top: 1rem;
}

.preview-hero {
  position: relative;
  min-height: 420px;
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(140, 90, 82, 0.06), rgba(49, 92, 96, 0.08));
}

.preview-hero__image {
  height: 420px;
}

.preview-hero__overlay {
  position: absolute;
  inset: auto 0 0 0;
  padding: 1.2rem;
  background: linear-gradient(180deg, transparent, rgba(34, 27, 24, 0.84));
  color: #fff;
}

.preview-hero__label {
  margin: 0 0 0.2rem;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  font-size: 0.72rem;
  opacity: 0.72;
}

.preview-hero__overlay h3 {
  margin: 0 0 0.2rem;
  font-size: 1rem;
  word-break: break-word;
}

.preview-hero__overlay p {
  margin: 0;
  opacity: 0.85;
}

.preview-empty {
  min-height: 420px;
  display: grid;
  place-items: center;
  text-align: center;
  padding: 2rem;
  border: 1px dashed rgba(140, 90, 82, 0.16);
  border-radius: 28px;
  background: rgba(140, 90, 82, 0.03);
}

.preview-empty h3 {
  margin: 0.85rem 0 0.25rem;
}

.preview-empty p {
  margin: 0;
  max-width: 28ch;
  color: rgba(48, 52, 56, 0.7);
}

.thumbnail-strip {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: minmax(72px, 96px);
  gap: 0.65rem;
  overflow-x: auto;
  padding-bottom: 0.25rem;
}

.thumbnail {
  border: 2px solid transparent;
  border-radius: 18px;
  padding: 0;
  background: transparent;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;
}

.thumbnail:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(63, 39, 34, 0.12);
}

.thumbnail--active {
  border-color: #8c5a52;
}

.thumbnail__image {
  width: 100%;
  aspect-ratio: 1 / 1;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
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
  .page {
    padding-inline: 1rem;
  }

  .panel-title {
    padding-inline: 1rem;
  }

  .actions {
    justify-content: stretch;
  }

  .actions .v-btn {
    width: 100%;
  }

  .preview-hero,
  .preview-empty {
    min-height: 300px;
  }

  .preview-hero__image {
    height: 300px;
  }
}
</style>
