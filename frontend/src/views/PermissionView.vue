<template>
  <div class="page-wrapper page-permission" style="background:#f6f9fc;padding:20px;">
    <el-row :gutter="20">
      <el-col :xs="24" :md="6" style="max-width:280px;">
        <el-card class="section-card">
          <template #header>
            <div class="section-title">
              角色列表
              <el-button type="primary" circle size="small" @click="openRoleForm(false)">
                <el-icon><CirclePlusFilled /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-for="role in roles" :key="role.id" :class="['role-card', {active: role.id === activeRoleId}]" @click="selectRole(role)" style="position:relative;">
            <div class="role-header">
              <span class="role-name">{{ role.name }}</span>
              <div>
                <el-tooltip content="编辑">
                  <el-button :icon="Edit" circle size="small" @click.stop="editRole(role)" />
                </el-tooltip>
                <el-tooltip content="删除">
                  <el-button :icon="Delete" circle type="danger" size="small" @click.stop="removeRole(role)" />
                </el-tooltip>
              </div>
            </div>
            <div class="role-description">{{ role.description }}</div>
            <el-badge :value="role.users.length" class="status-badge" style="position:absolute;top:-6px;right:-6px;" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="18">
        <el-card class="section-card">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="权限配置" name="perm">
              <el-row :gutter="20">
                <el-col v-for="m in permissions" :key="m.id" :xs="24" :md="12">
                  <el-card class="permission-card">
                    <div class="section-title">
                      {{ m.label }}
                      <el-tooltip content="权限说明">
                        <el-icon><InfoFilled/></el-icon>
                      </el-tooltip>
                    </div>
                    <el-checkbox-group v-model="checkedKeys">
                      <el-checkbox v-for="c in m.children" :key="c.id" :label="c.id">{{ c.label }}</el-checkbox>
                    </el-checkbox-group>
                  </el-card>
                </el-col>
              </el-row>
              <div class="action-buttons" style="justify-content:flex-end;">
                <el-button type="primary" @click="savePerms">保存权限</el-button>
              </div>
            </el-tab-pane>
            <el-tab-pane label="用户分配" name="users">
              <div class="action-buttons">
                <el-button type="primary" @click="toggleAddUser">
                  <el-icon><CirclePlusFilled/></el-icon>添加用户
                </el-button>
              </div>
              <el-table :data="assignedUsers" class="section-card" style="width:100%;" v-if="assignedUsers.length">
                <el-table-column prop="name" label="姓名" />
                <el-table-column prop="department" label="部门" />
                <el-table-column label="操作" width="80">
                  <template #default="scope">
                    <el-tooltip content="移除">
                      <el-button :icon="Delete" type="danger" circle size="small" @click="removeUser(scope.row)" />
                    </el-tooltip>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="无数据" />
              <el-drawer v-model="addUserVisible" direction="rtl" size="480px" title="添加用户">
                <el-form label-width="70px" class="form-section">
                  <el-form-item label="用户">
                    <el-select v-model="selectedUserId" filterable placeholder="选择用户" style="width:240px;">
                      <el-option v-for="u in availableUsers" :key="u.id" :label="u.name" :value="u.id" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="分配角色">
                    <el-select v-model="selectedRoleIds" multiple style="width:240px;">
                      <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
                    </el-select>
                  </el-form-item>
                </el-form>
                <template #footer>
                  <el-button @click="addUserVisible=false">取消</el-button>
                  <el-button type="primary" @click="addUser">确定</el-button>
                </template>
              </el-drawer>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <el-drawer v-model="roleFormVisible" direction="rtl" size="480px" :title="isEditRole ? '编辑角色' : '添加角色'">
      <el-form :model="roleForm" label-width="70px" class="form-section">
        <el-form-item label="名称">
          <el-input v-model="roleForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roleForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleFormVisible=false">取消</el-button>
        <el-button type="primary" @click="saveRole">保存</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, CirclePlusFilled, InfoFilled } from '@element-plus/icons-vue'
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

function editRole(role) {
  openRoleForm(true, role)
}
</script>
