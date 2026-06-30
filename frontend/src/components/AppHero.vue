<template>
  <section class="hero">
    <div class="hero__content">
      <div>
        <p class="eyebrow">{{ eyebrow }}</p>
        <h1 class="title">{{ title }}</h1>
        <p class="subtitle">{{ subtitle }}</p>
      </div>

      <div class="hero__panel" aria-label="Actions rapides">
        <div class="hero__stat">
          <v-icon color="primary" size="30">mdi-lock-outline</v-icon>
          <div>
            <strong>Galerie privée</strong>
            <span>Accès simple pour les invités</span>
          </div>
        </div>
        <div class="actions">
          <v-btn
            class="hero-action"
            color="primary"
            size="large"
            prepend-icon="mdi-image-multiple-outline"
            @click="goTo('/gallery')"
          >
            Ouvrir la galerie
          </v-btn>
          <v-btn
            class="hero-action"
            variant="outlined"
            color="primary"
            size="large"
            prepend-icon="mdi-cloud-upload-outline"
            @click="goTo('/upload')"
          >
            Ajouter des photos
          </v-btn>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'

defineProps({
  eyebrow: { type: String, default: 'Galerie privée' },
  title: { type: String, default: 'Partagez les souvenirs du jour J' },
  subtitle: {
    type: String,
    default: 'Un espace simple, rapide et privé pour téléverser, parcourir et retrouver toutes les photos du mariage.',
  },
})

const router = useRouter()

const goTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.hero {
  width: min(100%, 1180px);
  margin: 0 auto;
  padding: 28px 20px 18px;
}

.hero__content {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(280px, 360px);
  gap: clamp(1rem, 4vw, 3rem);
  align-items: center;
  min-height: 300px;
  padding: clamp(1.5rem, 4vw, 3.25rem);
  border: 1px solid var(--app-border);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(33, 90, 87, 0.1), rgba(196, 122, 75, 0.08)),
    var(--app-surface);
  box-shadow: var(--app-shadow);
}

.hero__content > div,
.hero__panel {
  min-width: 0;
}

.title {
  max-width: 13ch;
  margin: 0.6rem 0 1rem;
  font-size: clamp(2.1rem, 5vw, 4.45rem);
  line-height: 0.98;
  letter-spacing: 0;
}

.subtitle {
  max-width: 62ch;
  margin: 0;
  color: var(--app-muted);
  font-size: 1.02rem;
  line-height: 1.7;
}

.hero__panel {
  display: grid;
  gap: 1rem;
  padding: 18px;
  border: 1px solid var(--app-border);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.78);
}

.hero__stat {
  display: flex;
  gap: 0.85rem;
  align-items: center;
  min-width: 0;
}

.hero__stat strong,
.hero__stat span {
  display: block;
}

.hero__stat span {
  color: var(--app-muted);
  font-size: 0.92rem;
}

.actions {
  display: grid;
  gap: 0.8rem;
}

.hero-action {
  min-height: 48px;
  border-radius: 8px;
}

@media (max-width: 780px) {
  .hero__content {
    grid-template-columns: 1fr;
    min-height: auto;
  }
}

@media (max-width: 1040px) {
  .hero__content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .hero {
    padding: 16px 14px 12px;
  }

  .hero-action {
    width: 100%;
  }
}
</style>
