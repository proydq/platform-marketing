<template>
  <el-dialog title="分配角色" v-model="visible" width="400px">
    <el-checkbox-group v-model="checkedRoles">
      <el-checkbox
        v-for="role in roleOptions"
        :key="role.id"
        :label="role.id"
      >
        {{ role.name }}
      </el-checkbox>
    </el-checkbox-group>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchRoles } from '../../api/role'
import { fetchUserRoles, assignUserRoles } from '../../api/user'

const visible = ref(false)
const roleOptions = ref([])
const checkedRoles = ref([])
const saving = ref(false)
const userId = ref(null)

const emit = defineEmits(['saved'])

function open(id) {
  userId.value = id
  visible.value = true
  loadRoles()
  loadUserRoles(id)
}

function loadRoles() {
  fetchRoles().then(res => {
    roleOptions.value = res.data || []
  })
}

function loadUserRoles(id) {
  fetchUserRoles(id).then(res => {
    checkedRoles.value = res.data || []
  })
}

function save() {
  if (!userId.value) return
  saving.value = true
  assignUserRoles(userId.value, checkedRoles.value)
    .then(() => {
      ElMessage.success('保存成功')
      visible.value = false
      emit('saved')
    })
    .finally(() => (saving.value = false))
}

defineExpose({ open })
</script>

<style scoped>
.el-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 300px;
  overflow: auto;
}
</style>

