<script setup>
import { ref, onMounted } from 'vue'
import rolesJson from '../mock/roles.json'
import usersJson from '../mock/users.json'
import treeJson from '../mock/permissionTree.json'

const roles = ref([])
const users = ref([])
const tree = ref([])
const dialogVisible = ref(false)
const roleForm = ref({ name: '', description: '' })

onMounted(() => {
  roles.value = rolesJson
  users.value = usersJson
  tree.value = treeJson
})

function openDialog() {
  roleForm.value = { name: '', description: '' }
  dialogVisible.value = true
}
</script>

<template>
  <div class="card-container">
    <el-row class="action-buttons" justify="space-between" align="middle">
      <el-space>
        <el-button type="primary" @click="openDialog"><span class="icon">➕</span>新建角色</el-button>
      </el-space>
    </el-row>
    <el-card class="chart-container">
      <RoleCard v-for="r in roles" :key="r.id" :role="r" />
    </el-card>

    <el-card class="chart-container">
      <UserTable :items="users" />
    </el-card>

    <el-card class="chart-container">
      <PermissionTree :data="tree" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="创建角色" width="500px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="roleForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roleForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
