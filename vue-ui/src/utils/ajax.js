import axios from "axios";
import $q from "q";
import $utils from "./utils";

// 默认请求设置
axios.defaults.baseURL = "/";
axios.defaults.headers = {
  "X-Requested-With": "XMLHttpRequest",
};
axios.defaults.timeout = 1000 * 60 * 5;

function requestHandle(params) {
  let defer = $q.defer();
  axios(params)
    .then((res) => {
      if (res.data) {
        if (res.data.code == 401) {
          $utils.removeSessionStorage("token");
          alert("登录超时或账号在别处登录!")
            document.location.replace("/login");
        } else {
          defer.resolve(res.data);
        }
      } else {
        defer.reject("没有返回数据");
      }
    })
    .catch((err) => {
      defer.reject(err);
    });

  return defer.promise;
}

function getHeaders(options) {
  let headers = Object.assign(
    {
      "Content-Type": "application/json",
    },
    options
  );
  let token = $utils.getSessionStorage("token");
  if (token) {
    headers.token = token;
  }
  return headers;
}

export default {
  post: function(url, params, op,responseType) {
    return requestHandle({
      method: "post",
      url: url,
      data: params,
      headers: getHeaders(op),
      timeout: 300000, //上传超时时间
      responseType:responseType,
    });
  },
  get: function(url, params, op) {
    return requestHandle({
      method: "get",
      url: $utils.queryString(url, params),
      headers: getHeaders(op),
    });
  },
  put: function(url, params, op,responseType) {
    return requestHandle({
      method: "put",
      url: url,
      data: params,
      headers: getHeaders(op),
      responseType:responseType,
    });
  },
  delete: function(url, params, op) {
    return requestHandle({
      method: "delete",
      url: $utils.queryString(url, params),
      headers: getHeaders(op),
    });
  },
  download: function(url,param,fileName){
    this.post(url, param,{},'blob').then(res => {
      if (window.navigator && window.navigator.msSaveOrOpenBlob) {
          window.navigator.msSaveOrOpenBlob(res, fileName);
      }else {
          let URL = window.URL || window.webkitURL;
          let objectUrl = URL.createObjectURL(res);
          console.log(objectUrl)
          let a = document.createElement('a');
          a.download=fileName;
          a.href = objectUrl; // 文件流生成的url
          document.body.appendChild(a);
          a.click();
          a.remove();
      }
});
}
};
