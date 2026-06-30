<template>
  <div class="page">
    <AppHero
      eyebrow="Galerie collaborative privée"
      title="Chaque souvenir du mariage, au même endroit."
      subtitle="Les invités accèdent à la galerie, retrouvent les photos et ajoutent leurs moments favoris sans créer de compte."
    />

    <section class="content-grid">
      <v-card class="info-card info-card--accent" elevation="0">
        <v-card-title>
          <v-icon color="primary">mdi-account-heart-outline</v-icon>
          Accès invité
        </v-card-title>
        <v-card-text>
          Entrez un code de mariage privé pour découvrir la galerie ou déposer de nouvelles photos.
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" variant="flat" prepend-icon="mdi-arrow-right" @click="goTo('/gallery')">
            Entrer dans la galerie
          </v-btn>
        </v-card-actions>
      </v-card>

      <v-card class="info-card" elevation="0">
        <v-card-title>
          <v-icon color="primary">mdi-shield-check-outline</v-icon>
          Modération simple
        </v-card-title>
        <v-card-text>
          Les photos sont visibles rapidement. L'administration peut ensuite éditer, masquer ou supprimer un contenu si besoin.
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
  width: min(100%, 1180px);
  margin: 0 auto;
  padding: 0 20px 2rem;
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.info-card {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  box-shadow: 0 12px 28px rgba(22, 33, 38, 0.06);
}

.info-card :deep(.v-card-title) {
  gap: 0.65rem;
  font-weight: 850;
}

.info-card--accent {
  background: linear-gradient(135deg, rgba(33, 90, 87, 0.08), rgba(255, 255, 255, 1));
}

@media (max-width: 600px) {
  .content-grid {
    padding-inline: 14px;
  }
}
</style>
