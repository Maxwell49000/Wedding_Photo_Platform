<template>
  <v-card class="photo-card" rounded="xl" elevation="8">
    <div class="photo-card__preview">
      <span>{{ photo.filename }}</span>
    </div>
    <v-card-text>
      <div class="photo-card__meta">
        <strong>{{ photo.description || 'Sans description' }}</strong>
        <span>{{ formattedDate }}</span>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  photo: {
    type: Object,
    required: true,
  },
})

const formattedDate = computed(() => {
  if (!props.photo.uploadDate) {
    return 'Date inconnue'
  }

  return new Intl.DateTimeFormat('fr-FR', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(props.photo.uploadDate))
})
</script>

<style scoped>
.photo-card {
  overflow: hidden;
  background: rgba(255, 250, 244, 0.88);
  border: 1px solid rgba(183, 109, 97, 0.08);
}

.photo-card__preview {
  min-height: 220px;
  display: grid;
  place-items: center;
  background:
    linear-gradient(135deg, rgba(183, 109, 97, 0.1), rgba(47, 111, 109, 0.1)),
    radial-gradient(circle at top, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.3));
  color: #7b5a52;
  font-weight: 700;
  padding: 1rem;
  text-align: center;
}

.photo-card__meta {
  display: grid;
  gap: 0.35rem;
}

.photo-card__meta span {
  color: rgba(48, 52, 56, 0.68);
  font-size: 0.92rem;
}
</style>
