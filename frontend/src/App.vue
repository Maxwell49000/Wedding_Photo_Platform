<template>
  <v-app>
    <v-app-bar flat color="surface" class="app-bar">
      <div class="app-bar__shell">
        <button type="button" class="brand" @click="goTo('/')">
          <span class="brand__mark">WP</span>
          <span class="brand__text">Wedding Photos</span>
        </button>

        <v-spacer />

        <v-btn
          v-for="item in navItems"
          :key="item.to"
          variant="text"
          :prepend-icon="item.icon"
          class="nav-link"
          :class="{ 'nav-link--active': route.path === item.to }"
          @click="goTo(item.to)"
        >
          {{ item.label }}
        </v-btn>
      </div>
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>

    <v-bottom-navigation
      v-model="activePath"
      class="mobile-nav"
      color="primary"
      grow
      mandatory
      elevation="12"
      @update:model-value="goTo"
    >
      <v-btn v-for="item in navItems" :key="item.to" :value="item.to">
        <v-icon>{{ item.icon }}</v-icon>
        <span>{{ item.label }}</span>
      </v-btn>
    </v-bottom-navigation>
  </v-app>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const navItems = [
  { label: 'Accueil', to: '/', icon: 'mdi-home-outline' },
  { label: 'Galerie', to: '/gallery', icon: 'mdi-image-multiple-outline' },
  { label: 'Upload', to: '/upload', icon: 'mdi-cloud-upload-outline' },
  { label: 'Admin', to: '/admin', icon: 'mdi-tune-variant' },
]

const activePath = computed(() => route.path)

const goTo = (path) => {
  if (path !== route.path) {
    router.push(path)
  }
}
</script>

<style scoped>
.app-bar {
  border-bottom: 1px solid var(--app-border);
  backdrop-filter: blur(16px);
}

.app-bar__shell {
  width: min(100%, 1180px);
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0 20px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 0.7rem;
  min-width: 0;
  border: 0;
  background: transparent;
  color: var(--app-text);
  cursor: pointer;
  padding: 0;
}

.brand__mark {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: 10px;
  background: var(--app-primary);
  color: white;
  font-size: 0.82rem;
  font-weight: 900;
}

.brand__text {
  font-weight: 850;
  white-space: nowrap;
}

.nav-link {
  cursor: pointer;
  border-radius: 10px;
  color: var(--app-muted);
  min-height: 40px;
}

.nav-link:hover {
  background: var(--app-surface-soft);
  color: var(--app-primary);
}

.nav-link--active {
  background: var(--app-primary);
  color: white;
}

.mobile-nav {
  display: none;
}

@media (max-width: 760px) {
  .app-bar__shell {
    padding: 0 14px;
  }

  .nav-link {
    display: none;
  }

  .mobile-nav {
    display: flex;
  }

  .brand__text {
    font-size: 0.98rem;
  }
}
</style>
