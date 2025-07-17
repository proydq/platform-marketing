<template>
  <div>
    <el-card>
      <PermissionTree ref="treeRef" :data="permissionTree" />
      <div style="text-align:right;margin-top:10px;">
        <el-button type="primary" icon="Check" @click="save">保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PermissionTree from '../../components/PermissionTree.vue'
import { getPermissionTree } from '../../api/permissionApi'

const permissionTree = ref([])
const treeRef = ref()

onMounted(loadTree)

function loadTree(){
  getPermissionTree().then(res => {
    if(res.code === 0){
      permissionTree.value = res.data
    }
  })
}

function save(){
  const checked = treeRef.value?.getCheckedKeys() || []
  // normally submit selected permissions
  ElMessage.success('保存成功')
}
</script>

<style scoped>
</style>

