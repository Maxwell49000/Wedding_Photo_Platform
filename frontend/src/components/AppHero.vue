<template>
  <section class="hero">
    <div class="hero__frame">
      <div class="hero__content">
        <p class="eyebrow">{{ eyebrow }}</p>
        <h1 class="title">{{ title }}</h1>
        <p class="subtitle">{{ subtitle }}</p>
        <div class="actions">
          <v-btn class="hero-action" color="primary" size="large" rounded="pill" @click="goTo('/gallery')">
            Ouvrir la galerie
          </v-btn>
          <v-btn
            class="hero-action hero-action--secondary"
            variant="outlined"
            size="large"
            rounded="pill"
            @click="goTo('/upload')"
          >
            Uploader des photos
          </v-btn>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'

defineProps({
  eyebrow: { type: String, default: 'Private wedding gallery' },
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
  padding: 2rem 1.5rem 1.25rem;
  display: grid;
  place-items: center;
}

.hero__frame {
  width: min(100%, 1240px);
  padding: 1px;
  border-radius: 30px;
  background: linear-gradient(135deg, rgba(140, 90, 82, 0.14), rgba(49, 92, 96, 0.12));
  box-shadow: 0 18px 48px rgba(46, 38, 34, 0.08);
}

.hero__content {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.9), transparent 34%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(250, 244, 238, 0.96));
  border-radius: 29px;
  color: #2d2a28;
  padding: clamp(1.75rem, 4vw, 3.25rem);
}

.eyebrow {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.18em;
  font-size: 0.72rem;
  color: rgba(45, 42, 40, 0.55);
}

.title {
  font-size: clamp(2rem, 5vw, 4rem);
  line-height: 1;
  margin: 0.6rem 0 1rem;
  max-width: 12ch;
  letter-spacing: -0.04em;
}

.subtitle {
  max-width: 62ch;
  font-size: 1.02rem;
  line-height: 1.7;
  margin: 0;
  color: rgba(45, 42, 40, 0.74);
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem;
  margin-top: 1.5rem;
}

.hero-action {
  min-width: 180px;
  letter-spacing: 0.01em;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    background-color 0.18s ease,
    border-color 0.18s ease;
}

.hero-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 26px rgba(46, 38, 34, 0.08);
}

.hero-action--secondary {
  background: rgba(255, 255, 255, 0.72);
}

@media (max-width: 600px) {
  .hero {
    padding-inline: 1rem;
  }

  .actions .v-btn {
    width: 100%;
  }
}
</style>
