<script setup>
import { ref, onMounted } from 'vue'
import RoleCard from '../components/RoleCard.vue'
import UserTable from '../components/UserTable.vue'
import PermissionTree from '../components/PermissionTree.vue'
import users from '../mock/users.json'
import roles from '../mock/roles.json'

const roleList = ref([])
const userList = ref([])

onMounted(() => {
  roleList.value = roles
  userList.value = users
})
</script>

<template>
  <div>
    <div class="chart-container">
      <RoleCard v-for="role in roleList" :key="role.id" :role="role" />
    </div>
    <div class="chart-container">
      <UserTable :items="userList" />
    </div>
    <div class="chart-container">
      <PermissionTree :roles="roleList" />
    </div>
  </div>
</template>
