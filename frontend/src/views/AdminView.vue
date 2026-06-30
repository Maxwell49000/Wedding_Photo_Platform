<template>
  <div class="page">
    <AppHero
      eyebrow="Admin"
      title="Pilotage admin des invités et des photos."
      subtitle="Espace prévu pour la modération, les tags, les paramètres et les contrôles métier."
    />

    <section class="admin-grid">
      <v-card rounded="xl" elevation="4">
        <v-card-title>Invités</v-card-title>
        <v-card-text>{{ guestStore.guests.length }} invités chargés</v-card-text>
      </v-card>
      <v-card rounded="xl" elevation="4">
        <v-card-title>Photos</v-card-title>
        <v-card-text>{{ photoStore.photos.length }} photos chargées</v-card-text>
      </v-card>
      <v-card rounded="xl" elevation="4">
        <v-card-title>Tags</v-card-title>
        <v-card-text>Gestion des personnes présentes et des associations photo</v-card-text>
      </v-card>
      <v-card rounded="xl" elevation="4">
        <v-card-title>Paramètres</v-card-title>
        <v-card-text>Code mariage, limites d’upload et préférences globales</v-card-text>
      </v-card>
    </section>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import AppHero from '../components/AppHero.vue'
import { useGuestStore } from '../stores/guestStore'
import { usePhotoStore } from '../stores/photoStore'

const guestStore = useGuestStore()
const photoStore = usePhotoStore()

onMounted(() => {
  guestStore.loadGuests()
  photoStore.loadPhotos()
})
</script>

<style scoped>
.page {
  padding-bottom: 4rem;
}

.admin-grid {
  width: min(100%, 980px);
  margin: 0 auto;
  padding: 0 1.5rem 2rem;
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}
</style>
