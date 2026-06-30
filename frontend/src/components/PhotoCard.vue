<template>
  <v-card class="photo-card" elevation="0" @click="$emit('select')">
    <div class="photo-card__preview">
      <v-img :src="viewUrl" :alt="photo.filename" cover class="photo-card__image">
        <template #placeholder>
          <div class="photo-card__placeholder">
            <v-progress-circular indeterminate color="primary" size="26" />
          </div>
        </template>
        <template #error>
          <div class="photo-card__fallback">
            <v-icon size="38" color="primary">mdi-image-outline</v-icon>
            <span>{{ photo.filename }}</span>
          </div>
        </template>
      </v-img>
      <v-chip class="photo-card__status" :color="photo.visible ? 'success' : 'secondary'" size="small" variant="flat">
        {{ photo.visible ? 'Visible' : 'Masquée' }}
      </v-chip>
    </div>
    <v-card-text class="photo-card__body">
      <strong>{{ photo.description || photo.filename || 'Sans description' }}</strong>
      <span>{{ formattedDate }}</span>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { computed } from 'vue'
import { getPhotoViewUrl } from '../services/photoService'

defineEmits(['select'])

const props = defineProps({
  photo: {
    type: Object,
    required: true,
  },
})

const viewUrl = computed(() => getPhotoViewUrl(props.photo.id))

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
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  cursor: pointer;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    border-color 0.18s ease;
}

.photo-card:hover {
  transform: translateY(-3px);
  border-color: rgba(33, 90, 87, 0.35);
  box-shadow: var(--app-shadow);
}

.photo-card__preview {
  position: relative;
  background: var(--app-surface-soft);
}

.photo-card__image {
  width: 100%;
  aspect-ratio: 4 / 3;
}

.photo-card__placeholder,
.photo-card__fallback {
  display: grid;
  width: 100%;
  height: 100%;
  place-items: center;
  padding: 1rem;
  text-align: center;
}

.photo-card__fallback {
  gap: 0.5rem;
  color: var(--app-muted);
  font-weight: 750;
}

.photo-card__status {
  position: absolute;
  top: 10px;
  right: 10px;
}

.photo-card__body {
  display: grid;
  gap: 0.35rem;
}

.photo-card__body strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-card__body span {
  color: var(--app-muted);
  font-size: 0.92rem;
}
</style>
