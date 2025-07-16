<template>
  <div class="page-permission">
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="role-menu">
          <div class="toolbar" style="margin-bottom:10px; justify-content:space-between;">
            <el-button type="primary" icon="Plus" @click="openDialog">{{ t('common.create') }}</el-button>
            <el-button type="danger" icon="Delete" :disabled="!selectedRole" @click="removeRole">{{ t('common.delete') }}</el-button>
          </div>
          <el-scrollbar height="600px">
            <el-card v-for="item in roles" :key="item.id" class="role-card" @click="selectRole(item)" :class="{ active: item.id===selectedRole?.id }">
              <div class="role-header">
                <div>
                  <div class="role-name">{{ item.name }}</div>
                  <div class="role-description">{{ item.description }}</div>
                </div>
                <el-tag size="small">{{ item.permissionCount }}</el-tag>
              </div>
              <div class="text-xs text-gray-500">{{ formatDate(item.updatedAt) }}</div>
            </el-card>
          </el-scrollbar>
        </div>
      </el-col>
      <el-col :span="16">
        <el-card class="module-card">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span>{{ selectedRole ? selectedRole.name : t('common.all') }}</span>
              <el-button type="primary" icon="Check" :disabled="!selectedRole" @click="savePermissions">{{ t('common.save') }}</el-button>
            </div>
          </template>
          <permission-tree :data="treeData" v-if="treeData.length" />
          <div v-else class="text-center py-10">{{ t('common.noData') }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="roleForm.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="roleForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="saveRole">{{ t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { getRoleList, createRole, updateRole, deleteRole, bindRolePermissions } from '../api/roleApi'
import { getPermissionTree } from '../api/permissionApi'
import PermissionTree from '../components/PermissionTree.vue'
import { ElMessage } from 'element-plus'

const { t } = useI18n()

const roles = ref([])
const treeData = ref([])
const selectedRole = ref(null)
const dialogVisible = ref(false)
const roleForm = reactive({ id:'', name:'', description:'' })
const isEdit = ref(false)

onMounted(loadRoles)

function loadRoles() {
  getRoleList().then(res => {
    if(res.code===0) roles.value = res.data
  })
}

function selectRole(role) {
  selectedRole.value = role
  loadTree(role.id)
}

function loadTree(roleId) {
  getPermissionTree({ roleId }).then(res => {
    if(res.code===0) treeData.value = res.data
  })
}

function openDialog(role) {
  if(role) {
    isEdit.value = true
    Object.assign(roleForm, role)
  } else {
    isEdit.value = false
    Object.assign(roleForm,{ id:'', name:'', description:'' })
  }
  dialogVisible.value = true
}

function saveRole() {
  const api = isEdit.value ? updateRole(roleForm.id, roleForm) : createRole(roleForm)
  api.then(res => {
    if(res.code===0){
      ElMessage.success(t('common.saveSuccess'))
      dialogVisible.value=false
      loadRoles()
    }
  })
}

function removeRole() {
  if(!selectedRole.value) return
  deleteRole(selectedRole.value.id).then(res=>{
    if(res.code===0){
      ElMessage.success(t('common.deleted'))
      selectedRole.value=null
      treeData.value=[]
      loadRoles()
    }
  })
}

function savePermissions() {
  if(!selectedRole.value) return
  const codes = collectChecked(treeData.value)
  bindRolePermissions(selectedRole.value.id, codes).then(res=>{
    if(res.code===0) ElMessage.success(t('common.saveSuccess'))
  })
}

function collectChecked(nodes) {
  let arr=[]
  nodes.forEach(n=>{
    if(n.checked) arr.push(n.code)
    if(n.children) arr=arr.concat(collectChecked(n.children))
  })
  return arr
}

function formatDate(val) {
  if(!val) return ''
  return new Date(val).toLocaleDateString()
}

const dialogTitle = computed(() => isEdit.value ? t('common.edit') : t('common.create'))
</script>

<style scoped>
.active {
  border-color: #409eff;
}
</style>
