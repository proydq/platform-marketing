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
    <RoleCard v-for="role in roleList" :key="role.id" :role="role" />
    <UserTable :items="userList" />
    <PermissionTree :roles="roleList" />
  </div>
</template>
