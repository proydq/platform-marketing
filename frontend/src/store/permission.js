import { defineStore } from 'pinia'
import { listPermissions } from '../api/permissionApi'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    list: [],
    total: 0,
    loading: false,
    page: 1,
    size: 10,
    search: { keyword: '', type: '', status: '' }
  }),
  actions: {
    fetch() {
      this.loading = true
      listPermissions({
        page: this.page - 1,
        size: this.size,
        keyword: this.search.keyword,
        type: this.search.type,
        status: this.search.status
      }).then(res => {
        this.loading = false
        if (res.code === 0) {
          this.list = res.data.list
          this.total = res.data.total
        }
      }).catch(() => {
        this.loading = false
      })
    }
  }
})
