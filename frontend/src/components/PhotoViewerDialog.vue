<template>
  <v-dialog :model-value="open" max-width="980" @update:model-value="$emit('close')">
    <v-card v-if="photo" class="viewer-card">
      <div class="viewer-media">
        <v-img :src="viewUrl" :alt="photo.filename" class="viewer-image" cover />
      </div>

      <div class="viewer-content">
        <v-card-title class="viewer-title">
          <span>{{ photo.filename }}</span>
          <v-chip :color="photo.visible ? 'success' : 'secondary'" variant="flat" size="small">
            {{ photo.visible ? 'Visible' : 'Masquée' }}
          </v-chip>
        </v-card-title>

        <v-card-text class="viewer-meta">
          <p>{{ photo.description || 'Sans description' }}</p>
          <span>Upload : {{ formattedDate }}</span>
        </v-card-text>

        <v-card-actions class="viewer-actions">
          <v-btn variant="tonal" color="primary" prepend-icon="mdi-download" :href="downloadUrl" target="_blank">
            Télécharger
          </v-btn>
          <v-btn color="primary" variant="flat" prepend-icon="mdi-close" @click="$emit('close')">
            Fermer
          </v-btn>
        </v-card-actions>
      </div>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { getPhotoDownloadUrl, getPhotoViewUrl } from '../services/photoService'

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  photo: {
    type: Object,
    default: null,
  },
})

defineEmits(['close'])

const viewUrl = computed(() => (props.photo ? getPhotoViewUrl(props.photo.id) : ''))
const downloadUrl = computed(() => (props.photo ? getPhotoDownloadUrl(props.photo.id) : ''))

const formattedDate = computed(() => {
  if (!props.photo?.uploadDate) {
    return 'Date inconnue'
  }

  return new Intl.DateTimeFormat('fr-FR', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(props.photo.uploadDate))
})
</script>

<style scoped>
.viewer-card {
  overflow: hidden;
  background: var(--app-surface);
}

.viewer-media {
  background: #0f171a;
}

.viewer-image {
  height: min(64vh, 560px);
}

.viewer-content {
  padding-bottom: 0.4rem;
}

.viewer-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  font-weight: 850;
}

.viewer-title span {
  min-width: 0;
  overflow-wrap: anywhere;
}

.viewer-meta {
  display: grid;
  gap: 0.4rem;
}

.viewer-meta p {
  margin: 0;
}

.viewer-meta span {
  color: var(--app-muted);
}

.viewer-actions {
  justify-content: end;
  padding: 0 1rem 1rem;
}

@media (max-width: 640px) {
  .viewer-image {
    height: 48vh;
  }

  .viewer-actions {
    display: grid;
    grid-template-columns: 1fr;
  }
}
</style>
