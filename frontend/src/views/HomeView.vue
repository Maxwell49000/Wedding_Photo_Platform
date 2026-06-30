<template>
  <div class="page">
    <AppHero
      eyebrow="Private collaborative gallery"
      title="Chaque souvenir du mariage, au même endroit."
      subtitle="Les invités peuvent accéder à la galerie, filtrer les photos et uploader leurs moments favoris sans créer de compte."
    />

    <section class="content-grid">
      <v-card rounded="xl" class="info-card info-card--accent" elevation="0">
        <v-card-title>Accès invité</v-card-title>
        <v-card-text>
          Entrez un code de mariage privé pour découvrir la galerie ou déposer de nouvelles photos.
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" variant="flat" @click="goTo('/gallery')">Entrer le code</v-btn>
        </v-card-actions>
      </v-card>

      <v-card rounded="xl" class="info-card" elevation="0">
        <v-card-title>Publication automatique</v-card-title>
        <v-card-text>
          Les photos sont visibles dès l’upload. L’administration peut ensuite masquer ou supprimer un contenu si besoin.
        </v-card-text>
      </v-card>
    </section>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AppHero from '../components/AppHero.vue'

const router = useRouter()

const goTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.page {
  min-height: 100%;
  padding-bottom: 4rem;
}

.content-grid {
  width: min(100%, 1240px);
  margin: 0 auto;
  padding: 0 1.5rem 2rem;
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.info-card {
  background: rgba(255, 253, 248, 0.9);
  border: 1px solid rgba(140, 90, 82, 0.08);
  box-shadow: 0 12px 28px rgba(46, 38, 34, 0.05);
}

.info-card--accent {
  background:
    linear-gradient(135deg, rgba(140, 90, 82, 0.08), rgba(49, 92, 96, 0.04)),
    rgba(255, 253, 248, 0.92);
}
</style>
