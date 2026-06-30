<template>
  <div class="page">
    <AppHero
      eyebrow="Upload"
      title="Déposer plusieurs photos en quelques secondes."
      subtitle="Sélection du téléverseur, du photographe, des personnes présentes et d’un commentaire avant envoi."
    />

    <v-card rounded="xl" class="form-card" elevation="6">
      <v-card-text class="form-grid">
        <v-autocomplete
          v-model="upload.uploaderGuestId"
          :items="guestItems"
          item-title="title"
          item-value="value"
          label="Uploader"
          variant="outlined"
          :loading="guestStore.loading"
        />
        <v-autocomplete
          v-model="upload.photographerGuestId"
          :items="guestItems"
          item-title="title"
          item-value="value"
          label="Photographe"
          variant="outlined"
          :loading="guestStore.loading"
        />
        <v-combobox
          v-model="upload.people"
          label="Personnes présentes"
          multiple
          chips
          variant="outlined"
        />
        <v-textarea v-model="upload.comment" label="Commentaire" variant="outlined" rows="4" />
      </v-card-text>

      <v-divider />

      <v-card-text>
        <div class="dropzone">
          <p>Drag & Drop ici ou sélectionne tes fichiers</p>
          <v-file-input
            v-model="upload.files"
            multiple
            show-size
            label="Photos"
            variant="outlined"
            accept="image/*"
          />
        </div>

        <div class="progress-line">
          <v-progress-linear :model-value="upload.progress" color="primary" height="10" rounded />
          <span>{{ upload.progress }}%</span>
        </div>

        <p v-if="upload.error" class="error-text">
          {{ upload.error }}
        </p>

        <p v-if="upload.successCount" class="success-text">
          {{ upload.successCount }} photo(s) envoyée(s) avec succès.
        </p>
      </v-card-text>

      <v-card-actions class="actions">
        <v-btn
          color="primary"
          size="large"
          :loading="upload.submitting"
          :disabled="!canSubmit"
          @click="submitUpload"
        >
          Upload photos
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import AppHero from '../components/AppHero.vue'
import { useGuestStore } from '../stores/guestStore'
import { useUploadStore } from '../stores/uploadStore'
import { uploadPhotos } from '../services/uploadService'

const upload = useUploadStore()
const guestStore = useGuestStore()

onMounted(async () => {
  await guestStore.loadGuests()
})

const guestItems = computed(() => {
  return guestStore.guests.map((guest) => ({
    title: guest.displayName,
    value: guest.id,
  }))
})

const canSubmit = computed(() => upload.files.length > 0 && !upload.submitting)

async function submitUpload() {
  upload.submitting = true
  upload.error = null
  upload.successCount = 0
  upload.progress = 0

  try {
    const result = await uploadPhotos({
      files: upload.files,
      description: upload.comment,
      uploaderGuestId: upload.uploaderGuestId,
      photographerGuestId: upload.photographerGuestId,
      onProgress(value) {
        upload.progress = value
      },
    })

    upload.successCount = Array.isArray(result) ? result.length : 0
    upload.progress = 100
    upload.files = []
  } catch (error) {
    upload.error = error?.response?.data?.message ?? 'Upload impossible'
  } finally {
    upload.submitting = false
  }
}
</script>

<style scoped>
.page {
  padding-bottom: 4rem;
}

.form-card {
  width: min(100%, 980px);
  margin: 0 auto 2rem;
  background: rgba(255, 250, 244, 0.92);
}

.form-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.dropzone {
  border: 1.5px dashed rgba(183, 109, 97, 0.4);
  border-radius: 24px;
  background: rgba(183, 109, 97, 0.05);
  padding: 1.25rem;
  margin-bottom: 1rem;
}

.progress-line {
  display: grid;
  gap: 0.5rem;
}

.actions {
  justify-content: end;
  padding: 1rem 1.25rem 1.25rem;
}

.error-text {
  color: #b3261e;
  margin-top: 1rem;
}

.success-text {
  color: #2e7d32;
  margin-top: 1rem;
}
</style>
