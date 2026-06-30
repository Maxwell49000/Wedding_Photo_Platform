<template>
  <div class="page-shell">
    <section class="page-header">
      <div>
        <p class="eyebrow">Administration</p>
        <h1 class="section-title">Piloter la galerie</h1>
        <p class="section-copy">Modérez les photos, corrigez les informations et gardez une galerie propre pour les invités.</p>
      </div>
      <v-btn color="primary" variant="flat" prepend-icon="mdi-refresh" :loading="photoStore.loading || guestStore.loading" @click="refreshData">
        Actualiser
      </v-btn>
    </section>

    <section class="admin-grid">
      <v-card class="metric-card" elevation="0">
        <v-card-text>
          <v-icon color="primary">mdi-account-group-outline</v-icon>
          <span>Invités</span>
          <strong>{{ guestStore.guests.length }}</strong>
        </v-card-text>
      </v-card>
      <v-card class="metric-card" elevation="0">
        <v-card-text>
          <v-icon color="primary">mdi-image-multiple-outline</v-icon>
          <span>Photos</span>
          <strong>{{ photoStore.photos.length }}</strong>
        </v-card-text>
      </v-card>
      <v-card class="metric-card" elevation="0">
        <v-card-text>
          <v-icon color="primary">mdi-eye-outline</v-icon>
          <span>Visibles</span>
          <strong>{{ visiblePhotoCount }}</strong>
        </v-card-text>
      </v-card>
      <v-card class="metric-card" elevation="0">
        <v-card-text>
          <v-icon color="primary">mdi-eye-off-outline</v-icon>
          <span>Masquées</span>
          <strong>{{ hiddenPhotoCount }}</strong>
        </v-card-text>
      </v-card>
    </section>

    <v-card class="admin-list" elevation="0">
      <v-card-title class="list-title">
        <span>Photos publiées</span>
        <v-chip size="small" variant="tonal" color="primary">{{ photoStore.photos.length }} total</v-chip>
      </v-card-title>

      <v-divider />

      <div v-if="photoStore.loading" class="list-state">
        <v-progress-circular indeterminate color="primary" />
        <span>Chargement...</span>
      </div>

      <div v-else-if="!photoStore.photos.length" class="list-state">
        <v-icon color="primary" size="38">mdi-image-off-outline</v-icon>
        <span>Aucune photo pour le moment.</span>
      </div>

      <div v-else class="photo-list">
        <article v-for="photo in photoStore.photos" :key="photo.id" class="photo-row">
          <div class="photo-row__main">
            <v-avatar rounded="lg" size="58" class="photo-row__thumb">
              <v-img :src="photoUrl(photo.id)" :alt="photo.filename" cover />
            </v-avatar>
            <div>
              <strong>{{ photo.filename }}</strong>
              <p>{{ photo.description || 'Sans description' }}</p>
              <v-chip :color="photo.visible ? 'success' : 'secondary'" variant="tonal" size="small">
                {{ photo.visible ? 'Visible' : 'Masquée' }}
              </v-chip>
            </div>
          </div>
          <div class="photo-row__actions">
            <v-btn variant="tonal" color="primary" prepend-icon="mdi-pencil-outline" @click="openEditor(photo)">
              Éditer
            </v-btn>
            <v-btn color="error" variant="tonal" prepend-icon="mdi-delete-outline" @click="deletePhotoById(photo.id)">
              Supprimer
            </v-btn>
          </div>
        </article>
      </div>
    </v-card>

    <PhotoEditorDialog
      :open="editorOpen"
      :photo="selectedPhoto"
      :guests="guestStore.guests"
      @close="closeEditor"
      @save="savePhoto"
      @delete="removeSelectedPhoto"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import PhotoEditorDialog from '../components/PhotoEditorDialog.vue'
import { useGuestStore } from '../stores/guestStore'
import { usePhotoStore } from '../stores/photoStore'
import { deletePhoto as deletePhotoApi, getPhotoViewUrl, updatePhoto } from '../services/photoService'

const guestStore = useGuestStore()
const photoStore = usePhotoStore()

const editorOpen = ref(false)
const selectedPhoto = ref(null)

const visiblePhotoCount = computed(() => photoStore.photos.filter((photo) => photo.visible).length)
const hiddenPhotoCount = computed(() => photoStore.photos.length - visiblePhotoCount.value)

async function refreshData() {
  await Promise.all([guestStore.loadGuests(), photoStore.loadPhotos()])
}

onMounted(refreshData)

function photoUrl(id) {
  return getPhotoViewUrl(id)
}

function openEditor(photo) {
  selectedPhoto.value = photo
  editorOpen.value = true
}

function closeEditor() {
  editorOpen.value = false
}

async function savePhoto(draft) {
  if (!selectedPhoto.value) {
    return
  }

  await updatePhoto(selectedPhoto.value.id, draft)
  await photoStore.loadPhotos()
  closeEditor()
}

async function deletePhotoById(id) {
  await deletePhotoApi(id)
  await photoStore.loadPhotos()
  if (selectedPhoto.value?.id === id) {
    closeEditor()
  }
}

async function removeSelectedPhoto() {
  if (!selectedPhoto.value) {
    return
  }

  await deletePhotoById(selectedPhoto.value.id)
}
</script>

<style scoped>
.page-header {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.admin-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-bottom: 1rem;
}

.metric-card,
.admin-list {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  box-shadow: 0 12px 28px rgba(22, 33, 38, 0.06);
}

.metric-card :deep(.v-card-text) {
  display: grid;
  gap: 0.45rem;
}

.metric-card span {
  color: var(--app-muted);
  font-weight: 750;
}

.metric-card strong {
  font-size: 2rem;
  line-height: 1;
}

.list-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  font-weight: 850;
}

.photo-list {
  display: grid;
}

.photo-row {
  display: flex;
  gap: 1rem;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: 1px solid var(--app-border);
}

.photo-row:last-child {
  border-bottom: 0;
}

.photo-row__main {
  display: flex;
  gap: 0.85rem;
  align-items: center;
  min-width: 0;
}

.photo-row__main strong {
  display: block;
  overflow-wrap: anywhere;
}

.photo-row__main p {
  margin: 0.2rem 0 0.45rem;
  color: var(--app-muted);
}

.photo-row__thumb {
  flex: 0 0 auto;
  background: var(--app-surface-soft);
}

.photo-row__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  justify-content: flex-end;
}

.list-state {
  display: grid;
  min-height: 220px;
  gap: 0.75rem;
  place-items: center;
  align-content: center;
  color: var(--app-muted);
}

@media (max-width: 920px) {
  .admin-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .page-header {
    align-items: stretch;
  }

  .admin-grid {
    grid-template-columns: 1fr;
  }

  .photo-row {
    align-items: stretch;
    flex-direction: column;
  }

  .photo-row__actions {
    display: grid;
    grid-template-columns: 1fr;
  }
}
</style>
