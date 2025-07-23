import { useUserStore } from '@/store'

export function hasPermission(permission) {
  const store = useUserStore()
  const perms = store.currentUser?.permissions || []
  return perms.includes('*') || perms.includes(permission)
}
