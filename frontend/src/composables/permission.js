import { useUserStore } from '../store'

export function hasPermission(permission) {
  const store = useUserStore()
  return store.currentUser?.permissions?.includes(permission)
}
