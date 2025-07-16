<template>
  <div class="page-wrapper page-permission">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="6">
        <div class="action-buttons" style="margin-bottom:10px;">
          <el-button type="primary" @click="openRoleForm(false)">
            <el-icon><CirclePlusFilled /></el-icon>添加角色
          </el-button>
        </div>
        <el-menu class="role-menu" :default-active="activeRoleId + ''" @select="handleRoleSelect">
          <el-menu-item v-for="role in roles" :key="role.id" :index="role.id + ''">
            <el-icon><UserFilled /></el-icon>
            <span>{{ role.name }}</span>
            <el-badge :value="role.users.length" style="margin-left:auto;margin-right:8px;" />
            <el-tooltip content="编辑">
              <el-icon size="14" @click.stop="editRole(role)"><Edit /></el-icon>
            </el-tooltip>
            <el-tooltip content="删除">
              <el-icon size="14" @click.stop="removeRole(role)" style="margin-left:5px;"><Delete /></el-icon>
            </el-tooltip>
          </el-menu-item>
        </el-menu>
        <el-drawer
          v-model="roleFormVisible"
          :title="isEditRole ? '编辑角色' : '添加角色'"
          size="480px"
          direction="rtl"
        >
          <el-form :model="roleForm" label-width="70px" class="form-section">
            <el-form-item label="名称">
              <el-input v-model="roleForm.name" />
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="roleForm.description" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="roleFormVisible = false">取消</el-button>
            <el-button type="primary" @click="saveRole">保存</el-button>
          </template>
        </el-drawer>
      </el-col>
      <el-col :xs="24" :sm="24" :md="18">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="权限配置" name="perm">
              <el-row :gutter="20">
                <el-col v-for="(m, i) in permissions" :key="m.id" :xs="24" :sm="12">
                  <el-card class="module-card">
                    <template #header>
                      <span>{{ m.label }}</span>
                      <el-tooltip content="权限说明" placement="top">
                        <el-icon><InfoFilled /></el-icon>
                      </el-tooltip>
                    </template>
                    <el-tree
                      :ref="el => registerTree(el, i)"
                      :data="m.children"
                      node-key="id"
                      show-checkbox
                      check-strictly
                      default-expand-all
                      @check="handleModuleCheck"
                    />
                  </el-card>
                </el-col>
              </el-row>
              <div class="action-buttons">
                <el-button type="primary" @click="savePerms">保存权限</el-button>
              </div>
            </el-tab-pane>
            <el-tab-pane label="用户分配" name="users">
              <div class="action-buttons">
                <el-button type="primary" @click="toggleAddUser">
                  <el-icon><CirclePlusFilled /></el-icon>添加用户
                </el-button>
              </div>
              <el-drawer
                v-model="addUserVisible"
                title="添加用户"
                size="480px"
                direction="rtl"
              >
                <el-form label-width="70px" class="form-section">
                  <el-form-item label="用户">
                    <el-select v-model="selectedUserId" filterable placeholder="选择用户" style="width:240px;">
                      <el-option v-for="u in availableUsers" :key="u.id" :label="u.name" :value="u.id">
                        <el-avatar size="20" :src="u.avatar" style="margin-right:5px" />{{ u.name }}
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="分配角色">
                    <el-select v-model="selectedRoleIds" multiple style="width:240px;">
                      <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
                    </el-select>
                  </el-form-item>
                </el-form>
                <template #footer>
                  <el-button @click="addUserVisible = false">取消</el-button>
                  <el-button type="primary" @click="addUser">确定</el-button>
                </template>
              </el-drawer>
              <el-row :gutter="20">
                <el-col v-for="user in assignedUsers" :key="user.id" :xs="24" :sm="12" :md="8">
                  <el-card class="user-card" shadow="hover">
                    <el-avatar :src="user.avatar" :size="40" />
                    <div class="info">
                      <div class="role-name">{{ user.name }}</div>
                      <div class="role-description">{{ user.department }}</div>
                    </div>
                    <el-tooltip content="移除">
                      <el-icon @click="removeUser(user)" style="cursor:pointer;color:#f56c6c;"><Delete /></el-icon>
                    </el-tooltip>
                  </el-card>
                </el-col>
              </el-row>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, UserFilled, CirclePlusFilled, InfoFilled } from '@element-plus/icons-vue'
import rolesData from '../mock/roles.json'
import permsData from '../mock/permissions.json'
import usersData from '../mock/users.json'

const roles = ref([])
const permissions = ref([])
const users = ref([])

const activeRoleId = ref(null)
const activeTab = ref('perm')
const checkedKeys = ref([])
const treeRefs = ref([])
const roleFormVisible = ref(false)
const isEditRole = ref(false)
const roleForm = reactive({ id: null, name: '', description: '' })

const addUserVisible = ref(false)
const selectedUserId = ref(null)
const selectedRoleIds = ref([])
const codeToId = {}
const idToCode = {}

function buildMaps(list) {
  list.forEach(n => {
    if (n.code) {
      codeToId[n.code] = n.id
      idToCode[n.id] = n.code
    }
    if (n.children) buildMaps(n.children)
  })
}

onMounted(() => {
  roles.value = rolesData
  permissions.value = permsData
  users.value = usersData
  buildMaps(permsData)
  if (roles.value.length) {
    activeRoleId.value = roles.value[0].id
    checkedKeys.value = roles.value[0].permissions.map(c => codeToId[c]).filter(Boolean)
  }
  syncTrees()
})

const currentRole = computed(() => roles.value.find(r => r.id === activeRoleId.value) || null)

watch(activeRoleId, val => {
  if (val && currentRole.value) {
    checkedKeys.value = currentRole.value.permissions.map(c => codeToId[c]).filter(Boolean)
  } else {
    checkedKeys.value = []
  }
  syncTrees()
})

const assignedUsers = computed(() => currentRole.value ? currentRole.value.users : [])

const availableUsers = computed(() => {
  if (!currentRole.value) return []
  const ids = new Set(assignedUsers.value.map(u => u.id))
  return users.value.filter(u => !ids.has(u.id))
})
function handleRoleSelect(key) {
  const role = roles.value.find(r => r.id === Number(key))
  if (role) selectRole(role)
}

function selectRole(role) {
  activeRoleId.value = role.id
}

function registerTree(el, index) {
  if (el) treeRefs.value[index] = el
}

function syncTrees() {
  nextTick(() => {
    treeRefs.value.forEach(tr => tr && tr.setCheckedKeys(checkedKeys.value))
  })
}

function openRoleForm(edit, role) {
  isEditRole.value = edit
  if (edit && role) {
    roleForm.id = role.id
    roleForm.name = role.name
    roleForm.description = role.description
  } else {
    roleForm.id = null
    roleForm.name = ''
    roleForm.description = ''
  }
  roleFormVisible.value = true
}

function saveRole() {
  if (!roleForm.name) {
    ElMessage.error('名称不能为空')
    return
  }
  if (isEditRole.value) {
    const idx = roles.value.findIndex(r => r.id === roleForm.id)
    roles.value.splice(idx, 1, { ...roles.value[idx], name: roleForm.name, description: roleForm.description })
  } else {
    const newId = roles.value.length ? Math.max(...roles.value.map(r => r.id)) + 1 : 1
    roles.value.push({ id: newId, name: roleForm.name, description: roleForm.description, permissions: [], users: [] })
  }
  roleFormVisible.value = false
  ElMessage.success('保存成功')
}
function removeRole(role) {
  ElMessageBox.confirm('确定删除该角色吗?', '提示', { type: 'warning' })
    .then(() => {
      roles.value = roles.value.filter(r => r.id !== role.id)
      if (activeRoleId.value === role.id) {
        activeRoleId.value = roles.value.length ? roles.value[0].id : null
      }
      ElMessage.success('已删除')
    })
    .catch(() => {})
}

function savePerms() {
  if (!currentRole.value) return
  currentRole.value.permissions = checkedKeys.value.map(id => idToCode[id]).filter(Boolean)
  ElMessage.success('权限已更新')
}

function toggleAddUser() {
  selectedUserId.value = null
  selectedRoleIds.value = [activeRoleId.value]
  addUserVisible.value = true
}

function addUser() {
  if (!selectedUserId.value || !selectedRoleIds.value.length) return
  const user = users.value.find(u => u.id === selectedUserId.value)
  selectedRoleIds.value.forEach(rid => {
    const role = roles.value.find(r => r.id === rid)
    if (role && !role.users.find(u => u.id === user.id)) {
      role.users.push(user)
    }
  })
  addUserVisible.value = false
  selectedUserId.value = null
  selectedRoleIds.value = []
  ElMessage.success('已添加用户')
}

function removeUser(u) {
  if (!currentRole.value) return
  currentRole.value.users = currentRole.value.users.filter(item => item.id !== u.id)
  ElMessage.success('已移除用户')
}

function handleModuleCheck() {
  const set = new Set()
  treeRefs.value.forEach(tr => {
    if (tr) tr.getCheckedKeys().forEach(k => set.add(k))
  })
  checkedKeys.value = Array.from(set)
}
function editRole(role) {
  openRoleForm(true, role)
}
</script>

