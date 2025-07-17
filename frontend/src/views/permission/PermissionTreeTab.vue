<template>
  <div>
    <el-card :loading="loading">
      <PermissionTree ref="treeRef" :data="permissionTree" />
      <div style="text-align:right;margin-top:10px;">
        <el-button type="primary" icon="Check" :loading="saving" @click="save">保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PermissionTree from '../../components/PermissionTree.vue'
import { getPermissionTree } from '../../api/permission'

const permissionTree = ref([])
const loading = ref(false)
const saving = ref(false)
const treeRef = ref()

onMounted(fetchTree)

function fetchTree() {
  loading.value = true
  getPermissionTree()
    .then(res => {
      permissionTree.value = res.data.tree || res.data
      treeRef.value?.setCheckedKeys(res.data.checked || [])
    })
    .finally(() => {
      loading.value = false
    })
}


function save(){
  const checked = treeRef.value?.getCheckedKeys() || []
  saving.value = true
  // normally submit selected permissions via API
  Promise.resolve()
    .then(() => {
      ElMessage.success('保存成功')
    })
    .finally(() => {
      saving.value = false
    })
}
</script>

<style scoped>
</style>

