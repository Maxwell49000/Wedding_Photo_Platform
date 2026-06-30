<template>
  <div class="page-shell">
    <section class="page-header">
      <div>
        <p class="eyebrow">Galerie</p>
        <h1 class="section-title">Photos des invités</h1>
        <p class="section-copy">Parcourez les souvenirs partagés et ouvrez une photo pour la voir en grand.</p>
      </div>
      <v-text-field
        v-model="gallery.search"
        label="Rechercher"
        prepend-inner-icon="mdi-magnify"
        variant="outlined"
        hide-details
        density="comfortable"
        class="search"
      />
    </section>

    <v-alert
      v-if="photoStore.error"
      type="error"
      variant="tonal"
      class="state-alert"
    >
      Impossible de charger la galerie.
    </v-alert>

    <div v-else-if="photoStore.loading" class="loading-state">
      <v-progress-circular indeterminate color="primary" />
      <span>Chargement des photos...</span>
    </div>

    <section v-else-if="filteredPhotos.length" class="grid">
      <PhotoCard
        v-for="photo in filteredPhotos"
        :key="photo.id"
        :photo="photo"
        @select="openViewer(photo)"
      />
    </section>

    <v-empty-state
      v-else
      icon="mdi-image-search-outline"
      title="Aucune photo trouvée"
      text="Essayez une autre recherche ou ajoutez les premières photos depuis l'espace upload."
      class="empty-state"
    >
      <template #actions>
        <v-btn color="primary" prepend-icon="mdi-cloud-upload-outline" @click="$router.push('/upload')">
          Ajouter des photos
        </v-btn>
      </template>
    </v-empty-state>

    <PhotoViewerDialog
      :open="viewerOpen"
      :photo="selectedPhoto"
      @close="closeViewer"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useGalleryStore } from '../stores/galleryStore'
import { usePhotoStore } from '../stores/photoStore'
import PhotoCard from '../components/PhotoCard.vue'
import PhotoViewerDialog from '../components/PhotoViewerDialog.vue'
import { filterPhotos } from '../utils/photoHelpers'

const gallery = useGalleryStore()
const photoStore = usePhotoStore()

const viewerOpen = ref(false)
const selectedPhoto = ref(null)

onMounted(() => {
  photoStore.loadPhotos()
})

const filteredPhotos = computed(() => {
  return filterPhotos(photoStore.photos, gallery.search)
})

function openViewer(photo) {
  selectedPhoto.value = photo
  viewerOpen.value = true
}

function closeViewer() {
  viewerOpen.value = false
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

.search {
  width: min(100%, 340px);
}

.grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
}

.loading-state,
.empty-state {
  min-height: 320px;
  border: 1px solid var(--app-border);
  border-radius: 8px;
  background: var(--app-surface);
}

.loading-state {
  display: grid;
  gap: 0.8rem;
  place-items: center;
  align-content: center;
  color: var(--app-muted);
}

.state-alert {
  border-radius: 8px;
}

@media (max-width: 640px) {
  .page-header {
    align-items: stretch;
  }

  .search {
    width: 100%;
  }

  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
