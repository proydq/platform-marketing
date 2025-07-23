import request from "@/utils/request";

const marketingApi = {
  list(params) {
    return request({
      url: "/marketing-campaign",
      method: "get",
      params,
    });
  },
  create(data) {
    return request({
      url: "/marketing-campaign",
      method: "post",
      data,
    });
  },
  update(data) {
    return request({
      url: `/marketing-campaign/${data.id}`,
      method: "put",
      data,
    });
  },
  delete(id) {
    return request({
      url: `/marketing-campaign/${id}`,
      method: "delete",
    });
  },
  detail(id) {
    return request({
      url: `/marketing-campaign/${id}`,
      method: "get",
    });
  },
  updateStatus(id, status) {
    return request({
      url: "/marketing-campaign/status",
      method: "patch",
      data: { id, status },
    });
  },
};

export default marketingApi;
