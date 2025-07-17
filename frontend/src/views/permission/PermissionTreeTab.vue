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
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import PermissionTree from '../../components/PermissionTree.vue'

const permissionTree = ref([
  {
    id: 1,
    label: '用户管理',
    children: [
      { id: 11, label: '新增用户' },
      { id: 12, label: '删除用户' }
    ]
  },
  {
    id: 2,
    label: '内容管理',
    children: [
      { id: 21, label: '编辑内容' },
      { id: 22, label: '发布内容' }
    ]
  },
  {
    id: 3,
    label: '统计报表'
  }
])
const treeRef = ref()


function save(){
  const checked = treeRef.value?.getCheckedKeys() || []
  // normally submit selected permissions
  ElMessage.success('保存成功')
}
</script>

<style scoped>
</style>

