<template>
  <v-app>
    <v-app-bar flat color="transparent" class="app-bar">
      <v-app-bar-title class="brand">
        Wedding Photo Platform
      </v-app-bar-title>

      <v-spacer />

      <v-btn
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        variant="text"
        class="nav-link"
      >
        {{ item.label }}
      </v-btn>
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
const navItems = [
  { label: 'Accueil', to: '/' },
  { label: 'Galerie', to: '/gallery' },
  { label: 'Upload', to: '/upload' },
  { label: 'Admin', to: '/admin' },
]
</script>

<style scoped>
.app-bar {
  backdrop-filter: blur(18px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.brand {
  letter-spacing: 0.08em;
  font-weight: 700;
}

.nav-link {
  text-transform: none;
  letter-spacing: 0.02em;
}
</style>
