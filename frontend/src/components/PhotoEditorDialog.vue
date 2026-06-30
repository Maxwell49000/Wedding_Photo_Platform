<template>
  <v-dialog :model-value="open" max-width="720" @update:model-value="$emit('close')">
    <v-card v-if="draft" rounded="xl">
      <v-card-title>Éditer la photo</v-card-title>
      <v-card-text class="editor-grid">
        <v-text-field v-model="draft.description" label="Description" variant="outlined" />
        <v-switch v-model="draft.visible" label="Visible" inset />
        <v-autocomplete
          v-model="draft.uploaderGuestId"
          :items="guestItems"
          item-title="title"
          item-value="value"
          label="Uploader"
          variant="outlined"
        />
        <v-autocomplete
          v-model="draft.photographerGuestId"
          :items="guestItems"
          item-title="title"
          item-value="value"
          label="Photographe"
          variant="outlined"
        />
      </v-card-text>

      <v-card-actions class="editor-actions">
        <v-btn color="error" variant="tonal" @click="$emit('delete')">Supprimer</v-btn>
        <v-spacer />
        <v-btn variant="text" @click="$emit('close')">Annuler</v-btn>
        <v-btn color="primary" @click="$emit('save', draft)">Enregistrer</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { computed, reactive, watch } from 'vue'

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  photo: {
    type: Object,
    default: null,
  },
  guests: {
    type: Array,
    default: () => [],
  },
})

defineEmits(['close', 'save', 'delete'])

const draft = reactive({
  description: '',
  visible: true,
  uploaderGuestId: null,
  photographerGuestId: null,
})

const guestItems = computed(() => {
  return props.guests.map((guest) => ({
    title: guest.displayName,
    value: guest.id,
  }))
})

watch(
  () => props.photo,
  (photo) => {
    draft.description = photo?.description ?? ''
    draft.visible = photo?.visible ?? true
    draft.uploaderGuestId = photo?.uploaderGuestId ?? null
    draft.photographerGuestId = photo?.photographerGuestId ?? null
  },
  { immediate: true },
)
</script>

<style scoped>
.editor-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.editor-actions {
  padding: 0 1rem 1rem;
}
</style>
