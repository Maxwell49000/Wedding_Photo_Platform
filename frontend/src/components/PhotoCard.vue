<template>
  <v-card class="photo-card" rounded="xl" elevation="0" @click="$emit('select')">
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

defineEmits(['select'])

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
  background: rgba(255, 253, 248, 0.92);
  border: 1px solid rgba(140, 90, 82, 0.08);
  cursor: pointer;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    border-color 0.18s ease;
}

.photo-card:hover {
  transform: translateY(-3px);
  border-color: rgba(140, 90, 82, 0.16);
  box-shadow: 0 16px 32px rgba(46, 38, 34, 0.08);
}

.photo-card__preview {
  min-height: 210px;
  display: grid;
  place-items: center;
  background:
    linear-gradient(135deg, rgba(140, 90, 82, 0.08), rgba(49, 92, 96, 0.08)),
    radial-gradient(circle at top, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.45));
  color: #5f514c;
  font-weight: 700;
  padding: 1rem;
  text-align: center;
}

.photo-card__meta {
  display: grid;
  gap: 0.35rem;
}

.photo-card__meta span {
  color: rgba(48, 52, 56, 0.58);
  font-size: 0.92rem;
}
</style>
