import request from "@/utils/request";

export function getProductList(params) {
  return request({
    url: "/api/product/list",
    method: "get",
    params,
  });
}

export function createProduct(data) {
  return request({
    url: "/api/product",
    method: "post",
    data,
  });
}

export function updateProduct(id, data) {
  return request({
    url: `/api/product/${id}`,
    method: "put",
    data,
  });
}

export function deleteProduct(id) {
  return request({
    url: `/api/product/${id}`,
    method: "delete",
  });
}

export function uploadProductFile(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: "/api/product/upload",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}
