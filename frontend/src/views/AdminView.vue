<template>
  <div class="page">
    <AppHero
      eyebrow="Admin"
      title="Pilotage des invités et des photos."
      subtitle="Un espace sobre pour modérer, ajuster les métadonnées et garder la galerie sous contrôle."
    />

    <section class="admin-grid">
      <v-card rounded="xl" class="metric-card" elevation="0">
        <v-card-title>Invités</v-card-title>
        <v-card-text>{{ guestStore.guests.length }} invités chargés</v-card-text>
      </v-card>
      <v-card rounded="xl" class="metric-card" elevation="0">
        <v-card-title>Photos</v-card-title>
        <v-card-text>{{ photoStore.photos.length }} photos chargées</v-card-text>
      </v-card>
      <v-card rounded="xl" class="metric-card" elevation="0">
        <v-card-title>Tags</v-card-title>
        <v-card-text>Gestion des personnes présentes et des associations photo</v-card-text>
      </v-card>
      <v-card rounded="xl" class="metric-card" elevation="0">
        <v-card-title>Paramètres</v-card-title>
        <v-card-text>Code mariage, limites d’upload et préférences globales</v-card-text>
      </v-card>
    </section>

    <section class="admin-list">
      <v-card
        v-for="photo in photoStore.photos"
        :key="photo.id"
        rounded="xl"
        class="photo-row"
        elevation="0"
      >
        <v-card-text class="photo-row__content">
          <div>
            <strong>{{ photo.filename }}</strong>
            <p>{{ photo.description || 'Sans description' }}</p>
            <small>{{ photo.visible ? 'Visible' : 'Masquée' }}</small>
          </div>
          <div class="photo-row__actions">
            <v-btn variant="tonal" @click="openEditor(photo)">Éditer</v-btn>
            <v-btn color="error" variant="tonal" @click="deletePhotoById(photo.id)">Supprimer</v-btn>
          </div>
        </v-card-text>
      </v-card>
    </section>

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
import { onMounted, ref } from 'vue'
import AppHero from '../components/AppHero.vue'
import PhotoEditorDialog from '../components/PhotoEditorDialog.vue'
import { useGuestStore } from '../stores/guestStore'
import { usePhotoStore } from '../stores/photoStore'
import { deletePhoto as deletePhotoApi, updatePhoto } from '../services/photoService'

const guestStore = useGuestStore()
const photoStore = usePhotoStore()

const editorOpen = ref(false)
const selectedPhoto = ref(null)

async function refreshData() {
  await Promise.all([guestStore.loadGuests(), photoStore.loadPhotos()])
}

onMounted(refreshData)

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
.page {
  padding-bottom: 4rem;
}

.admin-grid {
  width: min(100%, 1240px);
  margin: 0 auto;
  padding: 0 1.5rem 2rem;
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.metric-card,
.photo-row {
  background: rgba(255, 253, 248, 0.92);
  border: 1px solid rgba(140, 90, 82, 0.08);
  box-shadow: 0 12px 28px rgba(46, 38, 34, 0.05);
}

.admin-list {
  width: min(100%, 1240px);
  margin: 0 auto;
  padding: 0 1.5rem 2rem;
  display: grid;
  gap: 1rem;
}

.photo-row__content {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
}

.photo-row__content p {
  margin: 0.35rem 0;
}

.photo-row__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

@media (max-width: 640px) {
  .photo-row__content {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
