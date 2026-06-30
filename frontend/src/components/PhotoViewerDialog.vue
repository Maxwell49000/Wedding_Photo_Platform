<template>
  <v-dialog :model-value="open" max-width="960" @update:model-value="$emit('close')">
    <v-card v-if="photo" rounded="xl">
      <v-img :src="viewUrl" height="520" cover />

      <v-card-title class="viewer-title">
        <span>{{ photo.filename }}</span>
        <v-chip :color="photo.visible ? 'success' : 'grey'" variant="flat" size="small">
          {{ photo.visible ? 'Visible' : 'Masquée' }}
        </v-chip>
      </v-card-title>

      <v-card-text class="viewer-meta">
        <p>{{ photo.description || 'Sans description' }}</p>
        <p>Upload: {{ formattedDate }}</p>
      </v-card-text>

      <v-card-actions class="viewer-actions">
        <v-btn variant="tonal" :href="downloadUrl" target="_blank">Télécharger</v-btn>
        <v-btn color="primary" variant="flat" @click="$emit('close')">Fermer</v-btn>
      </v-card-actions>
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
.viewer-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.viewer-meta {
  display: grid;
  gap: 0.4rem;
}

.viewer-actions {
  justify-content: end;
  padding: 0 1rem 1rem;
}
</style>
