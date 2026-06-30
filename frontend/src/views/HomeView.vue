<template>
  <div class="page">
    <AppHero
      eyebrow="Private collaborative gallery"
      title="Chaque souvenir du mariage, au même endroit."
      subtitle="Les invités peuvent accéder à la galerie, filtrer les photos et uploader leurs moments favoris sans créer de compte."
    />

    <section class="content-grid">
      <v-card rounded="xl" class="info-card" elevation="4">
        <v-card-title>Accès invité</v-card-title>
        <v-card-text>
          Entrez un code de mariage privé pour découvrir la galerie ou déposer de nouvelles photos.
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" variant="flat" to="/gallery">Entrer le code</v-btn>
        </v-card-actions>
      </v-card>

      <v-card rounded="xl" class="info-card" elevation="4">
        <v-card-title>Publication automatique</v-card-title>
        <v-card-text>
          Les photos sont visibles dès l’upload. L’administration peut ensuite masquer ou supprimer un contenu si besoin.
        </v-card-text>
      </v-card>
    </section>
  </div>
</template>

<script setup>
import AppHero from '../components/AppHero.vue'
</script>

<style scoped>
.page {
  min-height: 100%;
  padding-bottom: 4rem;
}

.content-grid {
  width: min(100%, 980px);
  margin: 0 auto;
  padding: 0 1.5rem 2rem;
  display: grid;
  gap: 1.25rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.info-card {
  background: rgba(255, 250, 244, 0.9);
}
</style>
