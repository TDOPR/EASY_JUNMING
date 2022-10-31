<template>
  <div class="setting-system">
    <el-tabs class="setting-card">
      <el-tab-pane label="基础字典设置">
        <div class="storage">
          <el-form :model="dictionary" label-width="300px" class="dataForm">
            <el-form-item label="登录日志保存天数">
              <el-input-number
                v-model="dictionary.loginLogSaveDay"
                :min="-1"
                :max="1000"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="操作日志保存天数">
              <el-input-number
                v-model="dictionary.operationLogSaveDay"
                :min="-1"
                :max="1000"
              ></el-input-number>
            </el-form-item>

            <el-form-item label="错误日志保存天数">
              <el-input-number
                v-model="dictionary.errorLogSaveDay"
                :min="-1"
                :max="1000"
              ></el-input-number>
            </el-form-item>

            <el-form-item label="启用单点登录">
              <el-switch
                v-model="dictionary.enableSso"
                active-value="true"
                inactive-value="false"
                active-text="开启"
                inactive-text="关闭"
              >
              </el-switch>
            </el-form-item>

            <el-form-item label="硬盘使用率超过x%发送邮件通知">
              <el-input-number
                v-model="dictionary.thresholdSize"
                :min="50"
                :max="90"
              ></el-input-number>
            </el-form-item>
          <div class="btn-footer">
            <el-button size="mini" type="primary" @click="submitForm(0)"
              >保存</el-button
            >
          </div>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { quillEditor } from "vue-quill-editor";
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
// 工具栏配置
const toolbarOptions = [
  ["bold", "italic", "underline", "strike"], // 加粗 斜体 下划线 删除线 -----['bold', 'italic', 'underline', 'strike']
  ["blockquote", "code-block"], // 引用  代码块-----['blockquote', 'code-block']
  [{ header: 1 }, { header: 2 }], // 1、2 级标题-----[{ header: 1 }, { header: 2 }]
  [{ list: "ordered" }, { list: "bullet" }], // 有序、无序列表-----[{ list: 'ordered' }, { list: 'bullet' }]
  [{ script: "sub" }, { script: "super" }], // 上标/下标-----[{ script: 'sub' }, { script: 'super' }]
  [{ indent: "-1" }, { indent: "+1" }], // 缩进-----[{ indent: '-1' }, { indent: '+1' }]
  [{ direction: "rtl" }], // 文本方向-----[{'direction': 'rtl'}]
  [{ size: ["small", false, "large", "huge"] }], // 字体大小-----[{ size: ['small', false, 'large', 'huge'] }]
  [{ header: [1, 2, 3, 4, 5, 6, false] }], // 标题-----[{ header: [1, 2, 3, 4, 5, 6, false] }]
  [{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色-----[{ color: [] }, { background: [] }]
  [{ font: [] }], // 字体种类-----[{ font: [] }]
  [{ align: [] }], // 对齐方式-----[{ align: [] }]
  ["clean"], // 清除文本格式-----['clean']
  ["image", "video"], // 链接、图片、视频-----['link', 'image', 'video']
];
export default {
  name: "",
  data() {
    return {
      rules: {
        smtpServer: {
          required: true,
          message: "SMTP服务器不能为空",
          trigger: "blur",
        },
        smtpPort: {
          required: true,
          message: "SMTP 端口不能为空",
          trigger: "blur",
        },
        emailName: {
          required: true,
          message: "邮箱帐号不能为空",
          trigger: "blur",
        },
        emailPassword: {
          required: true,
          message: "邮箱密码不能为空",
          trigger: "blur",
        },
      },
      content: ``, // 富文本编辑器默认内容
      editorOption: {
        //  富文本编辑器配置
        modules: {
          //工具栏定义的
          toolbar: toolbarOptions,
        },
        //主题
        theme: "snow",
        placeholder: "请输入正文",
      },
      dictionary: {},
      email: {},
      loading: false,
    };
  },
  mounted() {
    this.getSystem();
  },
  components: {
    quillEditor,
  },
  methods: {
    getSystem() {
      this.$ajax.get("/api/admin/setting").then((res) => {
        if (res.code == 200) {
          this.dictionary = res.data.dictionary;
          this.email = res.data.email;
          this.email.sendMessage =
            '<p style="white-space: normal;">亲爱的用户：</p><p style="white-space: normal;">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;您好！</p><p style="white-space: normal;">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;您的运维管理的合同比对服务部署机器的硬盘内存已使用${size},已超过设置的${rate}警戒线</p><p style="white-space: normal;">请及时增加硬盘存储或者清理硬盘存储,避免硬盘不足造成服务的不可以情况!</strong></p><p style="white-space: normal;">请查看服务运行状态,如有重大问题请联系ocr平台管理员.</p><p style="white-space: normal;"><br/></p><p style="white-space: normal;"><strong>合同比对监控服务！</strong></p><p><br/></p>\n';
        }
      });
    },
    submitForm(type) {
      var url = "",
        params = {};
      if (type == 0) {
        url = "/admin/setting";
        params = this.dictionary;
      } else {
        url = "/admin/modifyEmail";
        params = this.email;
        console.log(this.email);
      }
      if (this.loading) {
        return;
      }
      this.loading = true;
      this.$ajax
        .post("/api" + url, params)
        .then((res) => {
          if (res.code == 200) {
            this.$notify({
              title: "提示",
              message: "修改成功",
              type: "success",
              duration: 1500,
              customClass: "el-notification-error",
            });
            this.getSystem();
          } else {
            this.$notify({
              title: "提示",
              message: res.msg,
              type: "error",
              duration: 1500,
              customClass: "el-notification-error",
            });
          }
          this.loading = false;
        })
        .catch((err) => {
          this.loading = false;
          this.$notify({
            title: "提示",
            message: "服务器错误",
            type: "error",
            duration: 1500,
            customClass: "el-notification-error",
          });
        });
    },
    //失去焦点事件
    onEditorBlur(quill) {
      console.log("editor blur!", quill);
    },
    //获得焦点事件
    onEditorFocus(quill) {
      console.log("editor focus!", quill);
    },
    // 准备富文本编辑器
    onEditorReady(quill) {
      console.log("editor ready!", quill);
    },
    //内容改变事件
    onEditorChange({ quill, html, text }) {
      console.log("editor change!", quill, html, text);
      this.content = html;
    },
  },
};
</script>

<style scoped>
.setting-card .el-tab-pane {
  height: calc(100% - 0px);
  border-radius: 4px;
}
.setting-system {
  width: 100%;
  height: 100%;
  padding: 20px;
}
.storage {
  padding: 20px 0;
}
.setting-card >>> .el-tabs__nav {
  background: #eee;
}
.setting-card >>> .el-tabs__active-bar {
  display: none;
}
.setting-card >>> .el-tabs__item {
  width: 150px;
  padding: 0;
  font-size: 15px;
  text-align: center;
}
.setting-card >>> .el-tabs__item.is-active {
  color: #fff;
  background: rgb(51, 51, 51);
}
.setting-card /deep/.el-tabs__item.is-bottom:nth-child(2),
.setting-card /deep/.el-tabs__item.is-top:nth-child(2),
.setting-card /deep/.el-tabs__item.is-bottom:nth-child(2),
.setting-card /deep/.el-tabs__item.is-top:nth-child(2) {
  padding-left: 20px;
}
.setting-card /deep/.el-tabs__item.is-bottom:last-child,
.setting-card /deep/.el-tabs__item.is-top:last-child,
.setting-card /deep/.el-tabs__item.is-bottom:last-child,
.setting-card /deep/.el-tabs__item.is-top:last-child {
  padding-right: 20px;
}
.setting-card /deep/.el-tabs__active-bar {
  display: none;
}
.tab-title {
  font-weight: 500;
  font-size: 24px;
  padding-top: 30px;
  padding-bottom: 10px;
}
.storage .dataForm .el-form-item:last-child {
  margin-bottom: 5px;
}
.prompt {
  font-size: 13px;
  margin-left: 220px;
  text-align: left;
}
.btn-footer {
  margin-top: 20px;
  margin-right: 80%;
  text-align: right;
}
.sslRadio >>> .el-form-item__content {
  text-align: left;
}

.setting-card.el-tabs {
  height: 100%;
}
.setting-card >>> .el-tabs__content {
  height: calc(100% - 34px);
  overflow: auto;
}
</style>
<style lang="scss">
.ql-toolbar.ql-snow .ql-formats {
  height: 28px;
  line-height: 28px;
  margin-right: 5px;
}
.ql-snow.ql-toolbar button,
.ql-snow .ql-toolbar button {
  float: initial;
  width: 20px;
  height: 20px;
}
.ql-snow .ql-picker.ql-size,
.ql-snow .ql-picker.ql-header {
  width: 79px;
}
.editor {
  line-height: normal !important;
  height: 500px;
}
.ql-snow .ql-tooltip[data-mode="link"]::before {
  content: "请输入链接地址:";
}
.ql-snow .ql-tooltip.ql-editing a.ql-action::after {
  border-right: 0px;
  content: "保存";
  padding-right: 0px;
}
.ql-snow .ql-picker-label::before {
  position: absolute;
  left: 5px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 13px;
}
.ql-snow .ql-color-picker .ql-picker-label svg,
.ql-snow .ql-icon-picker .ql-picker-label svg {
  right: 4px;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}
.ql-toolbar.ql-snow {
  text-align: left;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
.ql-snow .ql-picker.ql-expanded .ql-picker-label {
  color: #000000;
}
.ql-snow .ql-tooltip[data-mode="video"]::before {
  content: "请输入视频地址:";
}
.ql-toolbar.ql-snow .ql-picker-label {
  border: 1px solid #000000;
}
.ql-snow .ql-picker.ql-size .ql-picker-label::before,
.ql-snow .ql-picker.ql-size .ql-picker-item::before {
  content: "14px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="small"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="small"]::before {
  content: "10px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="large"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="large"]::before {
  content: "18px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="huge"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="huge"]::before {
  content: "32px";
}

.ql-snow .ql-picker.ql-header .ql-picker-label::before,
.ql-snow .ql-picker.ql-header .ql-picker-item::before {
  content: "文本";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="1"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="1"]::before {
  content: "标题1";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="2"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="2"]::before {
  content: "标题2";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="3"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="3"]::before {
  content: "标题3";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="4"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="4"]::before {
  content: "标题4";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="5"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="5"]::before {
  content: "标题5";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="6"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="6"]::before {
  content: "标题6";
}

.ql-snow .ql-picker.ql-font .ql-picker-label::before,
.ql-snow .ql-picker.ql-font .ql-picker-item::before {
  content: "标准字体";
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value="serif"]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value="serif"]::before {
  content: "衬线字体";
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value="monospace"]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value="monospace"]::before {
  content: "等宽字体";
}
</style>
