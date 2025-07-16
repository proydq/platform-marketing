<template>
  <div class="page-wrapper">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="8">
        <div class="action-buttons" style="margin-bottom:10px;">
          <el-button type="primary" @click="openRoleForm(false)"><span class="icon">➕</span>添加角色</el-button>
        </div>
        <el-space direction="vertical" fill style="width:100%">
          <el-card
            v-for="role in roles"
            :key="role.id"
            class="role-card"
            :style="{ borderColor: role.id === activeRoleId ? '#409eff' : '#e4e7ed' }"
            @click="selectRole(role)"
          >
            <div class="role-header">
              <div>
                <div class="role-name">{{ role.name }}</div>
                <div class="role-description">{{ role.description }}</div>
              </div>
              <div>
                <el-button text size="small" @click.stop="editRole(role)">编辑</el-button>
                <el-button text size="small" style="color:#f56c6c" @click.stop="removeRole(role)">删除</el-button>
              </div>
            </div>
            <div class="permission-tags">
              <el-tag v-for="p in role.permissions" :key="p" size="small">{{ p }}</el-tag>
            </div>
            <div class="user-avatar-list">
              <el-avatar v-for="u in role.users" :key="u.id" :src="u.avatar" :size="24" />
              <span style="margin-left:8px;">{{ role.users.length }}人</span>
            </div>
          </el-card>
          <el-card v-if="roleFormVisible" class="role-card">
            <el-form :model="roleForm" label-width="70px" class="form-section">
              <el-form-item label="名称">
                <el-input v-model="roleForm.name" />
              </el-form-item>
              <el-form-item label="描述">
                <el-input v-model="roleForm.description" />
              </el-form-item>
              <div class="action-buttons">
                <el-button type="primary" size="small" @click="saveRole">保存</el-button>
                <el-button size="small" @click="roleFormVisible = false">取消</el-button>
              </div>
            </el-form>
          </el-card>
        </el-space>
      </el-col>
      <el-col :xs="24" :sm="24" :md="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="权限配置" name="perm">
              <div class="permission-tree">
                <el-tree
                  ref="treeRef"
                  :data="permissions"
                  node-key="id"
                  show-checkbox
                  check-strictly
                  v-model="checkedKeys"
                  default-expand-all
                />
              </div>
              <el-button type="primary" style="margin-top:10px;" @click="savePerms">保存权限</el-button>
            </el-tab-pane>
            <el-tab-pane label="用户分配" name="users">
              <div class="action-buttons">
                <el-button type="primary" @click="toggleAddUser"><span class="icon">➕</span>添加用户</el-button>
              </div>
              <el-form v-if="addUserVisible" inline class="form-section" style="margin-bottom:10px;">
                <el-form-item label="用户">
                  <el-select v-model="selectedUserId" filterable placeholder="选择用户" style="width:200px;">
                    <el-option v-for="u in availableUsers" :key="u.id" :label="u.name" :value="u.id" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" size="small" @click="addUser">确定</el-button>
                  <el-button size="small" @click="addUserVisible = false">取消</el-button>
                </el-form-item>
              </el-form>
              <el-table :data="assignedUsers" style="width:100%">
                <el-table-column prop="name" label="用户名" min-width="120" />
                <el-table-column prop="department" label="部门" width="120" />
                <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                    <span :class="['status-badge', row.status === '在线' ? 'status-success' : 'status-error']">{{ row.status }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row }">
                    <el-button type="text" style="color:#f56c6c" @click="removeUser(row)">移除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import rolesData from '../mock/roles.json'
import permsData from '../mock/permissions.json'
import usersData from '../mock/users.json'

const roles = ref([])
const permissions = ref([])
const users = ref([])

const activeRoleId = ref(null)
const activeTab = ref('perm')
const checkedKeys = ref([])

const roleFormVisible = ref(false)
const isEditRole = ref(false)
const roleForm = reactive({ id: null, name: '', description: '' })

const addUserVisible = ref(false)
const selectedUserId = ref(null)

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
})

const currentRole = computed(() => roles.value.find(r => r.id === activeRoleId.value) || null)

watch(activeRoleId, val => {
  if (val && currentRole.value) {
    checkedKeys.value = currentRole.value.permissions.map(c => codeToId[c]).filter(Boolean)
  } else {
    checkedKeys.value = []
  }
})

const assignedUsers = computed(() => currentRole.value ? currentRole.value.users : [])

const availableUsers = computed(() => {
  if (!currentRole.value) return []
  const ids = new Set(assignedUsers.value.map(u => u.id))
  return users.value.filter(u => !ids.has(u.id))
})

function selectRole(role) {
  activeRoleId.value = role.id
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
  addUserVisible.value = !addUserVisible.value
}

function addUser() {
  if (!selectedUserId.value || !currentRole.value) return
  const user = users.value.find(u => u.id === selectedUserId.value)
  currentRole.value.users.push(user)
  selectedUserId.value = null
  addUserVisible.value = false
  ElMessage.success('已添加用户')
}

function removeUser(u) {
  if (!currentRole.value) return
  currentRole.value.users = currentRole.value.users.filter(item => item.id !== u.id)
  ElMessage.success('已移除用户')
}

function editRole(role) {
  openRoleForm(true, role)
}
</script>

