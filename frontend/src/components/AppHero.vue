<template>
  <section class="hero">
    <div class="hero__content">
      <p class="eyebrow">{{ eyebrow }}</p>
      <h1 class="title">{{ title }}</h1>
      <p class="subtitle">{{ subtitle }}</p>
      <div class="actions">
        <v-btn color="primary" size="large" to="/gallery">Ouvrir la galerie</v-btn>
        <v-btn variant="tonal" size="large" to="/upload">Uploader des photos</v-btn>
      </div>
    </div>
  </section>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, default: 'Private wedding gallery' },
  title: { type: String, default: 'Partagez les souvenirs du jour J' },
  subtitle: {
    type: String,
    default: 'Un espace simple, rapide et privé pour téléverser, parcourir et retrouver toutes les photos du mariage.',
  },
})
</script>

<style scoped>
.hero {
  padding: 5rem 1.5rem 2rem;
  display: grid;
  place-items: center;
}

.hero__content {
  width: min(100%, 980px);
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.75), transparent 42%),
    linear-gradient(135deg, rgba(183, 109, 97, 0.95), rgba(47, 111, 109, 0.92));
  color: white;
  border-radius: 32px;
  padding: clamp(2rem, 5vw, 4.5rem);
  box-shadow: 0 24px 80px rgba(63, 39, 34, 0.22);
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  font-size: 0.75rem;
  opacity: 0.78;
}

.title {
  font-size: clamp(2.4rem, 6vw, 4.8rem);
  line-height: 0.96;
  margin: 0.5rem 0 1rem;
  max-width: 12ch;
}

.subtitle {
  max-width: 60ch;
  font-size: 1.05rem;
  line-height: 1.7;
  opacity: 0.95;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.85rem;
  margin-top: 1.75rem;
}
</style>
