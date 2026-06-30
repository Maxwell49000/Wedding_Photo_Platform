import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const weddingTheme = {
  dark: false,
  colors: {
    background: '#f6f7fb',
    surface: '#ffffff',
    primary: '#215a57',
    secondary: '#334155',
    accent: '#c47a4b',
    error: '#c2413a',
    info: '#2563eb',
    success: '#16805f',
    warning: '#b76b12',
  },
}

export const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
  theme: {
    defaultTheme: 'weddingTheme',
    themes: {
      weddingTheme,
    },
  },
})
