<script setup>
const props = defineProps(['items'])
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
</script>

<template>
  <el-table :data="props.items" style="width:100%">
    <el-table-column prop="name" :label="t('user.name')" width="150" show-overflow-tooltip>
      <template #default="scope">
        <div style="display:flex;align-items:center;gap:10px;">
          <el-avatar :size="30" :src="scope.row.avatar">{{ scope.row.name.charAt(0) }}</el-avatar>
          <span>{{ scope.row.name }}</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="email" :label="t('user.email')" min-width="200" show-overflow-tooltip />
    <el-table-column prop="role" :label="t('user.role')" width="120">
      <template #default="scope">
        <el-tag type="primary" size="small">{{ scope.row.role }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="department" :label="t('user.department')" width="120" />
    <el-table-column prop="status" :label="t('user.status')" width="100">
      <template #default="scope">
        <el-tag :type="scope.row.status === t('user.online') ? 'success' : 'info'" size="small">{{ scope.row.status }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="lastLogin" :label="t('user.lastLogin')" width="180" />
  </el-table>
</template>
