import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: false,
    currentUser: null,
  }),
  actions: {
    login(user) {
      this.isLoggedIn = true
      this.currentUser = user
    },
    logout() {
      this.isLoggedIn = false
      this.currentUser = null
    },
  },
})
