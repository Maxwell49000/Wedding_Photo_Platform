<template>
  <div class="page-shell">
    <section v-if="!isAdminUnlocked" class="access-shell">
      <v-card class="access-card" elevation="0">
        <v-card-title class="access-title">
          <v-icon color="primary">mdi-lock-outline</v-icon>
          Acces administration
        </v-card-title>
        <v-card-text class="access-form">
          <v-text-field
            v-model="adminCodeInput"
            label="Code administrateur"
            type="password"
            variant="outlined"
            autofocus
            :error-messages="adminError"
            @keyup.enter="unlockAdmin"
          />
          <v-btn color="primary" variant="flat" prepend-icon="mdi-login" @click="unlockAdmin">
            Entrer
          </v-btn>
        </v-card-text>
      </v-card>
    </section>

    <template v-else>
      <section class="page-header">
        <div>
          <p class="eyebrow">Administration</p>
          <h1 class="section-title">Piloter la galerie</h1>
          <p class="section-copy">Gerez les invites, moderez les photos et gardez une galerie propre.</p>
        </div>
        <div class="header-actions">
          <v-btn variant="tonal" prepend-icon="mdi-logout" @click="lockAdmin">
            Verrouiller
          </v-btn>
          <v-btn
            color="primary"
            variant="flat"
            prepend-icon="mdi-refresh"
            :loading="photoStore.loading || guestStore.loading"
            @click="refreshData"
          >
            Actualiser
          </v-btn>
        </div>
      </section>

      <section class="admin-grid">
        <v-card class="metric-card" elevation="0">
          <v-card-text>
            <v-icon color="primary">mdi-account-group-outline</v-icon>
            <span>Invites</span>
            <strong>{{ guestStore.guests.length }}</strong>
          </v-card-text>
        </v-card>
        <v-card class="metric-card" elevation="0">
          <v-card-text>
            <v-icon color="primary">mdi-image-multiple-outline</v-icon>
            <span>Photos</span>
            <strong>{{ photoStore.photos.length }}</strong>
          </v-card-text>
        </v-card>
        <v-card class="metric-card" elevation="0">
          <v-card-text>
            <v-icon color="primary">mdi-eye-outline</v-icon>
            <span>Visibles</span>
            <strong>{{ visiblePhotoCount }}</strong>
          </v-card-text>
        </v-card>
        <v-card class="metric-card" elevation="0">
          <v-card-text>
            <v-icon color="primary">mdi-eye-off-outline</v-icon>
            <span>Masquees</span>
            <strong>{{ hiddenPhotoCount }}</strong>
          </v-card-text>
        </v-card>
      </section>

      <v-tabs v-model="activeTab" color="primary" class="admin-tabs">
        <v-tab value="photos" prepend-icon="mdi-image-multiple-outline">Photos</v-tab>
        <v-tab value="guests" prepend-icon="mdi-account-group-outline">Invites</v-tab>
      </v-tabs>

      <v-window v-model="activeTab">
        <v-window-item value="photos">
          <v-card class="admin-list" elevation="0">
            <v-card-title class="list-title">
              <span>Photos publiees</span>
              <v-chip size="small" variant="tonal" color="primary">{{ photoStore.photos.length }} total</v-chip>
            </v-card-title>

            <v-divider />

            <div v-if="photoStore.loading" class="list-state">
              <v-progress-circular indeterminate color="primary" />
              <span>Chargement...</span>
            </div>

            <div v-else-if="!photoStore.photos.length" class="list-state">
              <v-icon color="primary" size="38">mdi-image-off-outline</v-icon>
              <span>Aucune photo pour le moment.</span>
            </div>

            <div v-else class="photo-list">
              <article v-for="photo in photoStore.photos" :key="photo.id" class="photo-row">
                <div class="photo-row__main">
                  <v-avatar rounded="lg" size="58" class="photo-row__thumb">
                    <v-img :src="photoUrl(photo.id)" :alt="photo.filename" cover />
                  </v-avatar>
                  <div>
                    <strong>{{ photo.filename }}</strong>
                    <p>{{ photo.description || 'Sans description' }}</p>
                    <v-chip :color="photo.visible ? 'success' : 'secondary'" variant="tonal" size="small">
                      {{ photo.visible ? 'Visible' : 'Masquee' }}
                    </v-chip>
                  </div>
                </div>
                <div class="photo-row__actions">
                  <v-btn variant="tonal" color="primary" prepend-icon="mdi-pencil-outline" @click="openEditor(photo)">
                    Editer
                  </v-btn>
                  <v-btn color="error" variant="tonal" prepend-icon="mdi-delete-outline" @click="deletePhotoById(photo.id)">
                    Supprimer
                  </v-btn>
                </div>
              </article>
            </div>
          </v-card>
        </v-window-item>

        <v-window-item value="guests">
          <section class="guest-layout">
            <v-card class="admin-list" elevation="0">
              <v-card-title class="list-title">
                <span>Invites</span>
                <v-chip size="small" variant="tonal" color="primary">{{ guestStore.guests.length }} total</v-chip>
              </v-card-title>

              <v-divider />

              <div v-if="guestStore.loading" class="list-state">
                <v-progress-circular indeterminate color="primary" />
                <span>Chargement...</span>
              </div>

              <div v-else-if="!guestStore.guests.length" class="list-state">
                <v-icon color="primary" size="38">mdi-account-plus-outline</v-icon>
                <span>Aucun invite pour le moment.</span>
              </div>

              <div v-else class="guest-list">
                <article v-for="guest in guestStore.guests" :key="guest.id" class="guest-row">
                  <div>
                    <strong>{{ guest.displayName }}</strong>
                    <p>{{ guest.firstname }} {{ guest.lastname }} - {{ guest.category }}</p>
                    <v-chip :color="guest.active ? 'success' : 'secondary'" variant="tonal" size="small">
                      {{ guest.active ? 'Actif' : 'Inactif' }}
                    </v-chip>
                  </div>
                  <div class="guest-row__actions">
                    <v-btn icon="mdi-pencil-outline" variant="tonal" color="primary" aria-label="Editer" @click="editGuest(guest)" />
                    <v-btn icon="mdi-delete-outline" variant="tonal" color="error" aria-label="Supprimer" @click="removeGuest(guest.id)" />
                  </div>
                </article>
              </div>
            </v-card>

            <v-card class="admin-list" elevation="0">
              <v-card-title class="list-title">
                <span>{{ editingGuestId ? 'Modifier invite' : 'Ajouter invite' }}</span>
              </v-card-title>
              <v-divider />
              <v-card-text class="guest-form">
                <v-text-field v-model="guestDraft.firstname" label="Prenom" variant="outlined" />
                <v-text-field v-model="guestDraft.lastname" label="Nom" variant="outlined" />
                <v-text-field v-model="guestDraft.displayName" label="Nom affiche dans les selects" variant="outlined" />
                <v-text-field v-model="guestDraft.category" label="Categorie" variant="outlined" />
                <v-text-field v-model="guestDraft.tableNumber" label="Table" variant="outlined" />
                <v-switch v-model="guestDraft.active" color="primary" label="Actif" inset />

                <v-alert v-if="guestFormError" type="error" variant="tonal" density="comfortable">
                  {{ guestFormError }}
                </v-alert>
              </v-card-text>
              <v-card-actions class="form-actions">
                <v-btn v-if="editingGuestId" variant="text" @click="resetGuestDraft">Annuler</v-btn>
                <v-btn color="primary" prepend-icon="mdi-content-save-outline" @click="saveGuest">
                  {{ editingGuestId ? 'Enregistrer' : 'Ajouter' }}
                </v-btn>
              </v-card-actions>
            </v-card>
          </section>
        </v-window-item>
      </v-window>

      <PhotoEditorDialog
        :open="editorOpen"
        :photo="selectedPhoto"
        :guests="activeGuests"
        @close="closeEditor"
        @save="savePhoto"
        @delete="removeSelectedPhoto"
      />
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import PhotoEditorDialog from '../components/PhotoEditorDialog.vue'
import { useGuestStore } from '../stores/guestStore'
import { usePhotoStore } from '../stores/photoStore'
import { deletePhoto as deletePhotoApi, getPhotoViewUrl, updatePhoto } from '../services/photoService'

const ADMIN_SESSION_KEY = 'wedding-photo-admin-code'
const expectedAdminCode = import.meta.env.VITE_ADMIN_ACCESS_CODE ?? 'admin-demo'

const guestStore = useGuestStore()
const photoStore = usePhotoStore()

const isAdminUnlocked = ref(Boolean(sessionStorage.getItem(ADMIN_SESSION_KEY)))
const adminCodeInput = ref('')
const adminError = ref('')
const activeTab = ref('photos')
const editorOpen = ref(false)
const selectedPhoto = ref(null)
const editingGuestId = ref(null)
const guestFormError = ref('')

const guestDraft = reactive({
  firstname: '',
  lastname: '',
  displayName: '',
  category: 'Invite',
  tableNumber: '',
  active: true,
})

const visiblePhotoCount = computed(() => photoStore.photos.filter((photo) => photo.visible).length)
const hiddenPhotoCount = computed(() => photoStore.photos.length - visiblePhotoCount.value)
const activeGuests = computed(() => guestStore.guests.filter((guest) => guest.active))

watch(
  () => [guestDraft.firstname, guestDraft.lastname],
  ([firstname, lastname]) => {
    if (!editingGuestId.value && !guestDraft.displayName.trim()) {
      guestDraft.displayName = [firstname, lastname].filter(Boolean).join(' ')
    }
  },
)

onMounted(() => {
  if (isAdminUnlocked.value) {
    refreshData()
  }
})

async function refreshData() {
  await Promise.all([guestStore.loadGuests(), photoStore.loadPhotos({ includeHidden: true })])
}

function unlockAdmin() {
  if (adminCodeInput.value !== expectedAdminCode) {
    adminError.value = 'Code administrateur incorrect'
    return
  }

  adminError.value = ''
  adminCodeInput.value = ''
  isAdminUnlocked.value = true
  sessionStorage.setItem(ADMIN_SESSION_KEY, expectedAdminCode)
  refreshData()
}

function lockAdmin() {
  isAdminUnlocked.value = false
  sessionStorage.removeItem(ADMIN_SESSION_KEY)
}

function photoUrl(id) {
  return getPhotoViewUrl(id)
}

function openEditor(photo) {
  selectedPhoto.value = photo
  editorOpen.value = true
}

function closeEditor() {
  editorOpen.value = false
}

async function savePhoto(draft) {
  if (!selectedPhoto.value) {
    return
  }

  await updatePhoto(selectedPhoto.value.id, draft)
  await photoStore.loadPhotos({ includeHidden: true })
  closeEditor()
}

async function deletePhotoById(id) {
  await deletePhotoApi(id)
  await photoStore.loadPhotos({ includeHidden: true })
  if (selectedPhoto.value?.id === id) {
    closeEditor()
  }
}

async function removeSelectedPhoto() {
  if (!selectedPhoto.value) {
    return
  }

  await deletePhotoById(selectedPhoto.value.id)
}

function editGuest(guest) {
  editingGuestId.value = guest.id
  guestDraft.firstname = guest.firstname
  guestDraft.lastname = guest.lastname
  guestDraft.displayName = guest.displayName
  guestDraft.category = guest.category
  guestDraft.tableNumber = guest.tableNumber ?? ''
  guestDraft.active = guest.active
  guestFormError.value = ''
}

function resetGuestDraft() {
  editingGuestId.value = null
  guestDraft.firstname = ''
  guestDraft.lastname = ''
  guestDraft.displayName = ''
  guestDraft.category = 'Invite'
  guestDraft.tableNumber = ''
  guestDraft.active = true
  guestFormError.value = ''
}

function buildGuestPayload() {
  const firstname = guestDraft.firstname.trim()
  const lastname = guestDraft.lastname.trim()
  const displayName = guestDraft.displayName.trim() || [firstname, lastname].filter(Boolean).join(' ')
  const category = guestDraft.category.trim() || 'Invite'

  return {
    firstname,
    lastname,
    displayName,
    category,
    tableNumber: guestDraft.tableNumber.trim() || null,
    active: guestDraft.active,
  }
}

async function saveGuest() {
  const payload = buildGuestPayload()

  if (!payload.firstname || !payload.lastname || !payload.displayName || !payload.category) {
    guestFormError.value = 'Prenom, nom, nom affiche et categorie sont obligatoires.'
    return
  }

  guestFormError.value = ''

  if (editingGuestId.value) {
    await guestStore.updateGuest(editingGuestId.value, payload)
  } else {
    await guestStore.createGuest(payload)
  }

  resetGuestDraft()
}

async function removeGuest(id) {
  await guestStore.deleteGuest(id)
  if (editingGuestId.value === id) {
    resetGuestDraft()
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.header-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem;
  justify-content: flex-end;
}

.access-shell {
  display: grid;
  min-height: 62vh;
  place-items: center;
}

.access-card {
  width: min(100%, 440px);
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  box-shadow: 0 12px 28px rgba(22, 33, 38, 0.06);
}

.access-title {
  display: flex;
  gap: 0.65rem;
  align-items: center;
  font-weight: 850;
}

.access-form {
  display: grid;
  gap: 0.85rem;
}

.admin-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-bottom: 1rem;
}

.metric-card,
.admin-list {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  box-shadow: 0 12px 28px rgba(22, 33, 38, 0.06);
}

.metric-card :deep(.v-card-text) {
  display: grid;
  gap: 0.45rem;
}

.metric-card span {
  color: var(--app-muted);
  font-weight: 750;
}

.metric-card strong {
  font-size: 2rem;
  line-height: 1;
}

.admin-tabs {
  margin-bottom: 1rem;
  border-bottom: 1px solid var(--app-border);
}

.list-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  font-weight: 850;
}

.photo-list,
.guest-list {
  display: grid;
}

.photo-row,
.guest-row {
  display: flex;
  gap: 1rem;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: 1px solid var(--app-border);
}

.photo-row:last-child,
.guest-row:last-child {
  border-bottom: 0;
}

.photo-row__main {
  display: flex;
  gap: 0.85rem;
  align-items: center;
  min-width: 0;
}

.photo-row__main strong,
.guest-row strong {
  display: block;
  overflow-wrap: anywhere;
}

.photo-row__main p,
.guest-row p {
  margin: 0.2rem 0 0.45rem;
  color: var(--app-muted);
}

.photo-row__thumb {
  flex: 0 0 auto;
  background: var(--app-surface-soft);
}

.photo-row__actions,
.guest-row__actions,
.form-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  justify-content: flex-end;
}

.guest-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 420px);
  gap: 1rem;
  align-items: start;
}

.guest-form {
  display: grid;
  gap: 1rem;
}

.list-state {
  display: grid;
  min-height: 220px;
  gap: 0.75rem;
  place-items: center;
  align-content: center;
  color: var(--app-muted);
}

@media (max-width: 920px) {
  .admin-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .guest-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .page-header {
    align-items: stretch;
  }

  .header-actions,
  .admin-grid {
    display: grid;
    grid-template-columns: 1fr;
  }

  .photo-row,
  .guest-row {
    align-items: stretch;
    flex-direction: column;
  }

  .photo-row__actions,
  .guest-row__actions,
  .form-actions {
    display: grid;
    grid-template-columns: 1fr;
  }
}
</style>
