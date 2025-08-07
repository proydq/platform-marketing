<template>
  <div>
    <el-form :inline="true" :model="filters" class="filter-form">
      <el-form-item :label="$t('product.name')">
        <el-input v-model="filters.name" placeholder="" />
      </el-form-item>
      <el-form-item :label="$t('product.category')">
        <el-input v-model="filters.category" placeholder="" />
      </el-form-item>
      <el-form-item :label="$t('product.tags')">
        <el-input v-model="filters.tag" placeholder="" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">Search</el-button>
        <el-button @click="openDialog()">Add</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="products">
      <el-table-column prop="name" :label="$t('product.name')" />
      <el-table-column prop="category" :label="$t('product.category')" />
      <el-table-column :label="$t('product.priceUSD')" prop="priceUSD" />
      <el-table-column :label="$t('product.tags')">
        <template #default="{ row }">
          <el-tag v-for="t in row.tags" :key="t" style="margin-right:4px">{{ t }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Spec" >
        <template #default="{ row }">
          <span v-if="row.specPdf">Uploaded</span>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="Operations">
        <template #default="{ row }">
          <el-button type="text" size="small" @click="openDialog(row)">Edit</el-button>
          <el-button type="text" size="small" @click="handleDelete(row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="form" label-width="120px">
        <el-form-item :label="$t('product.name')"><el-input v-model="form.name" /></el-form-item>
        <el-form-item :label="$t('product.category')"><el-input v-model="form.category" /></el-form-item>
        <el-form-item :label="$t('product.priceUSD')"><el-input v-model="form.priceUSD" /></el-form-item>
        <el-form-item :label="$t('product.priceCNY')"><el-input v-model="form.priceCNY" /></el-form-item>
        <el-form-item :label="$t('product.priceEUR')"><el-input v-model="form.priceEUR" /></el-form-item>
        <el-form-item :label="$t('product.tags')">
          <el-select v-model="form.tags" multiple filterable>
            <el-option v-for="t in allTags" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('product.imageList')">
          <el-upload
            action="/api/product/upload"
            list-type="picture-card"
            :on-success="handleImageSuccess"
            :file-list="imageList">
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
        <el-form-item :label="$t('product.specPdf')">
          <el-upload
            action="/api/product/upload"
            :on-success="handlePdfSuccess"
            :file-list="pdfList"
            accept="application/pdf">
            <el-button type="primary">Upload</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item :label="$t('product.videoUrl')"><el-input v-model="form.videoUrl" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">Cancel</el-button>
        <el-button type="primary" @click="submitForm">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { getProductList, createProduct, updateProduct, deleteProduct } from '@/api/product';

const filters = reactive({ name:'', category:'', tag:'' });
const products = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const form = reactive({ id:null, name:'', category:'', priceUSD:'', priceCNY:'', priceEUR:'', tags:[], imageList:[], specPdf:'', videoUrl:'' });
const imageList = ref([]);
const pdfList = ref([]);
const allTags = ref(['New','Hot','Sale']);

function loadData(){
  getProductList({ ...filters }).then(res=>{ products.value=res.data.rows || []; });
}
function openDialog(row){
  dialogTitle.value = row && row.id ? 'Edit Product' : 'New Product';
  if(row){ Object.assign(form,row); imageList.value=(row.imageList||[]).map(url=>({url})); pdfList.value=row.specPdf?[{url:row.specPdf,name:'spec.pdf'}]:[]; }
  else{ Object.assign(form,{id:null,name:'',category:'',priceUSD:'',priceCNY:'',priceEUR:'',tags:[],imageList:[],specPdf:'',videoUrl:''}); imageList.value=[]; pdfList.value=[]; }
  dialogVisible.value=true;
}
function handleImageSuccess(resp, file){
  form.imageList.push(resp.data.url || resp.url);
}
function handlePdfSuccess(resp, file){
  form.specPdf = resp.data.url || resp.url;
  pdfList.value=[{url:form.specPdf,name:file.name}];
}
function submitForm(){
  const api = form.id ? updateProduct(form.id, form) : createProduct(form);
  api.then(()=>{ dialogVisible.value=false; loadData(); });
}
function handleDelete(row){
  deleteProduct(row.id).then(()=>loadData());
}

loadData();
</script>

