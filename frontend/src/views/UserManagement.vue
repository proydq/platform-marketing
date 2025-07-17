<template>
  <div class="page-permission">
    <div class="toolbar mb-3">
      <el-input v-model="search.keyword" :placeholder="t('common.search')" style="width:200px" clearable />
      <el-select v-model="search.role" placeholder="角色" clearable style="width:140px">
        <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.name" />
      </el-select>
      <el-select v-model="search.status" placeholder="状态" clearable style="width:120px">
        <el-option :label="t('user.online')" value="online" />
        <el-option :label="t('user.offline')" value="offline" />
      </el-select>
      <el-button type="primary" icon="Search" @click="fetchList">{{ t('campaign.search') }}</el-button>
      <el-button @click="reset">{{ t('common.cancel') }}</el-button>
      <div style="flex:1"></div>
      <el-upload :auto-upload="false" :show-file-list="false" @change="importUser">
        <el-button icon="UploadFilled">导入</el-button>
      </el-upload>
      <el-button type="primary" icon="Plus" @click="openDialog">{{ t('common.create') }}</el-button>
    </div>

    <user-table :items="list" />

    <div style="text-align:right; margin-top:10px;">
      <el-pagination background layout="prev, pager, next" :current-page="page" :total="total" :page-size="size" @current-change="val=>{page=val;fetchList()}" />
    </div>

    <user-dialog ref="dialog" @saved="fetchList" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import UserTable from '../components/UserTable.vue'
import UserDialog from '../components/UserDialog.vue'
import { getUserList, importUsers } from '../api/user'
import { getRoleList } from '../api/role'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 10
const roles = ref([])

const search = reactive({ keyword:'', role:'', status:'' })

const dialog = ref()

onMounted(() => {
  loadRoles()
  fetchList()
})

function loadRoles(){
  getRoleList().then(res=>{ if(res.code===0) roles.value=res.data })
}

function fetchList() {
  getUserList({ page: page.value - 1, size, ...search }).then(res => {
    if(res.code===0){
      list.value=res.data.list
      total.value=res.data.total
    }
  })
}

function reset(){
  search.keyword=''
  search.role=''
  search.status=''
  fetchList()
}

function openDialog(){
  dialog.value.open()
}

function importUser(file){
  const formData=new FormData()
  formData.append('file', file.raw)
  importUsers(formData).then(res=>{
    if(res.code===0){
      ElMessage.success(t('common.saveSuccess'))
      fetchList()
    }
  })
}
</script>
