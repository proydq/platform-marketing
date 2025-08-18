import { useUserStore } from '../store'

export function hasPermission(permission) {
  // 如果没有权限要求，直接允许
  if (!permission || permission === '') {
    return true
  }
  
  const store = useUserStore()
  const user = store.currentUser
  
  // 如果没有用户或用户权限，拒绝访问
  if (!user || !user.permissions) {
    return false
  }
  
  const perms = user.permissions || []
  
  return perms.includes('*') || perms.includes(permission)
}
