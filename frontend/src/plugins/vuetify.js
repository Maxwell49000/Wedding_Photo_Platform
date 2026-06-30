import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import { aliases, mdi } from 'vuetify/iconsets/mdi'

const weddingTheme = {
  dark: false,
  colors: {
    background: '#f6efe7',
    surface: '#fffaf4',
    primary: '#b76d61',
    secondary: '#2f6f6d',
    accent: '#e8c07d',
    error: '#c84c4c',
    info: '#4f7bd8',
    success: '#4f8f68',
    warning: '#d08a2f',
  },
}

export const vuetify = createVuetify({
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
