import request from "@/utils/request";

const API_BASE = "/v1";

// 产品相关API
export const getProducts = (params) => {
  return request.get(`${API_BASE}/products`, { params });
};

export const getProductList = (params) => {
  return request.get(`${API_BASE}/products`, { params });
};

export const getProduct = (id) => {
  return request.get(`${API_BASE}/products/${id}`);
};

export const createProduct = (data) => {
  return request.post(`${API_BASE}/products`, data);
};

export const updateProduct = (id, data) => {
  return request.put(`${API_BASE}/products/${id}`, data);
};

export const deleteProduct = (id) => {
  return request.delete(`${API_BASE}/products/${id}`);
};

// 产品分类相关API
export const getCategories = () => {
  return request.get(`${API_BASE}/product-categories`);
};

export const createCategory = (data) => {
  return request.post(`${API_BASE}/product-categories`, data);
};

export const updateCategory = (id, data) => {
  return request.put(`${API_BASE}/product-categories/${id}`, data);
};

export const deleteCategory = (id) => {
  return request.delete(`${API_BASE}/product-categories/${id}`);
};

export const duplicateProduct = (id) => {
  return request.post(`${API_BASE}/products/${id}/duplicate`);
};

// 文件上传相关
export const uploadProductFile = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return request.post(`${API_BASE}/products/upload`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};