<template>
  <div class="page">
    <section class="page-header">
      <div>
        <p class="eyebrow">Gallery</p>
        <h1>Galerie des invités</h1>
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

    <section class="grid">
      <PhotoCard
        v-for="photo in filteredPhotos"
        :key="photo.id"
        :photo="photo"
      />
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useGalleryStore } from '../stores/galleryStore'
import { usePhotoStore } from '../stores/photoStore'
import PhotoCard from '../components/PhotoCard.vue'

const gallery = useGalleryStore()
const photoStore = usePhotoStore()

onMounted(() => {
  photoStore.loadPhotos()
})

const filteredPhotos = computed(() => {
  const search = gallery.search.trim().toLowerCase()
  if (!search) {
    return photoStore.photos
  }

  return photoStore.photos.filter((photo) => {
    return [photo.filename, photo.description]
      .filter(Boolean)
      .some((value) => value.toLowerCase().includes(search))
  })
})
</script>

<style scoped>
.page {
  width: min(100%, 1240px);
  margin: 0 auto;
  padding: 2rem 1.5rem 4rem;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  color: rgba(60, 47, 43, 0.58);
  font-size: 0.72rem;
  margin-bottom: 0.4rem;
}

h1 {
  font-size: clamp(2rem, 4vw, 3.4rem);
  margin: 0;
}

.search {
  width: min(100%, 320px);
}

.grid {
  display: grid;
  gap: 1.25rem;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
}
</style>
