<template>
  <div>
    <div class="toolbar mb-3">
      <el-button type="primary" icon="Plus" @click="openDialog">新建角色</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="role in roles" :key="role.id">
        <RoleCard :role="role" />
        <div style="text-align:right; margin-top:6px;">
          <el-button type="primary" size="small" icon="Edit" @click="openDialog(role)">编辑</el-button>
          <el-button type="danger" size="small" icon="Delete" @click="remove(role)">删除</el-button>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '创建角色'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <el-tree
        ref="treeRef"
        class="permission-tree"
        :data="treeData"
        node-key="id"
        show-checkbox
        :default-expanded-keys="expandedKeys"
      />
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" icon="Check" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import RoleCard from '../../components/RoleCard.vue'

const roles = ref([
  {
    id: 1,
    name: '管理员',
    description: '拥有全部权限',
    permissions: ['add_user', 'delete_user', 'view_reports'],
    users: [
      { id: 1, name: '张三', avatar: '' },
      { id: 2, name: '李四', avatar: '' }
    ]
  },
  {
    id: 2,
    name: '编辑',
    description: '内容管理',
    permissions: ['edit_content', 'publish_content'],
    users: [{ id: 3, name: '王五', avatar: '' }]
  }
])
const treeData = ref([
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
    label: '统计报表',
    children: []
  }
])
const expandedKeys = ref(treeData.value.slice(0, 4).map(n => n.id))
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: '', name: '', description: '' })
const treeRef = ref()

function openDialog(role) {
  if (role) {
    isEdit.value = true
    Object.assign(form, role)
  } else {
    isEdit.value = false
    Object.assign(form, { id: '', name: '', description: '' })
  }
  dialogVisible.value = true
}

function save() {
  const permissions = treeRef.value?.getCheckedKeys() || []
  if (isEdit.value) {
    const index = roles.value.findIndex(r => r.id === form.id)
    if (index !== -1) {
      roles.value[index] = { ...roles.value[index], ...form, permissions }
    }
    ElMessage.success('更新成功')
  } else {
    roles.value.push({
      id: Date.now(),
      name: form.name,
      description: form.description,
      permissions,
      users: []
    })
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
}

function remove(role) {
  roles.value = roles.value.filter(r => r.id !== role.id)
  ElMessage.success('已删除')
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}
.mb-3 { margin-bottom: 15px; }
</style>

