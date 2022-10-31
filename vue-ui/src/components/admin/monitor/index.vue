<template>
  <div id="monitor_chats"  v-loading="firstLoading">
          <!-- <el-backtop target=".el-tabs__content"></el-backtop> -->
      <div class="flexBox">
        <div class="midParent serverInfo">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span><strong>服务器资源监控</strong></span>
            </div>
                <div class="serverDiv">
                            <el-card class="box-card serverDetail">
                      <div slot="header" class="clearfix">
                        <span><strong>内存</strong></span>
                      </div>
                        <div
                          id="myChart5"
                          :style="{ width: '100%', height: '100%' }"
                        ></div>
                    </el-card>
                            <el-card class="box-card serverDetail">
                      <div slot="header" class="clearfix">
                        <span><strong>硬盘</strong></span>
                      </div>
                        <div
                          id="myChart6"
                          :style="{ width: '100%', height: '100%' }"
                        ></div>
                    </el-card>
            </div>
            <div class="serverDiv">
                            <el-card class="box-card serverDetail">
                      <div slot="header" class="clearfix">
                        <span><strong>CPU</strong></span>
                      </div>
                        <div
                          id="myChart7"
                          :style="{ width: '100%', height: '100%' }"
                        ></div>
                    </el-card>
                            <el-card class="box-card serverDetail">
                      <div slot="header" class="clearfix">
                        <span><strong>GPU</strong></span>
                      </div>
                        <div
                          id="myChart8"
                          :style="{ width: '100%', height: '100%' }"
                        ></div>
                    </el-card>
            </div>
          </el-card>
        </div>
    
    </div>
  </div>
  
</template>
<script>
// 引入基本模板
// let echarts = require('echarts/lib/echarts')
// // 引入柱状图组件
// require('echarts/lib/chart/bar')
// // 引入提示框和title组件
// require('echarts/lib/component/tooltip')
// require('echarts/lib/component/title')
export default {
  name: "home",
  data() {
    return {
      timer: "",
      cpu:{},
      gpu:{},
      disk:{},
      memory:{},
      firstLoading: true,
      ajaxLoading: false
    };
  },
  mounted() {
    this.initData();
    this.timer = setInterval(this.initData, 20000);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    initData() {
      let that = this;
      if(this.ajaxLoading){
        return
      }
      this.ajaxLoading=true;
      this.$ajax.get("/api/admin/getMonitorInfo").then((res) => {
        if (res.code == 200) {
          this.firstLoading=false
          that.cpu=res.data.cpu;
          that.gpu=res.data.gpu;
          that.disk=res.data.disk;
          that.memory=res.data.memory;
          that.drawLine();
          this.ajaxLoading=false
        }
      });
    },
    func(x) {
      x /= 10;
      return Math.sin(x) * Math.cos(x * 2 + 1) * Math.sin(x * 3 + 2) * 50;
    },
    generateData() {
      let data = [];
      for (let i = -200; i <= 200; i += 0.1) {
        data.push([i, this.func(i)]);
      }
      return data;
    },
    drawLine() {
      //渲染饼图
      let that = this;
      //渲染折线图
      let myChart5 = this.$echarts.init(document.getElementById("myChart5"));
      let myChart6 = this.$echarts.init(document.getElementById("myChart6"));
      let myChart7 = this.$echarts.init(document.getElementById("myChart7"));
       let myChart8 = this.$echarts.init(document.getElementById("myChart8"));


      let option5 = {
        tooltip: {
          formatter: "{a} <br/>{b}  {c}%",
        },
        toolbox: {
          feature: {
            restore: {},
            saveAsImage: {},
          },
        },
        series: [
          {
            name: "内存使用率",
            type: "gauge",
            detail: { 
            formatter: this.memory.name ,
            textStyle:{
                fontSize:11
              }
              },
            data: [{ value: this.memory.value, name: "" }],
          },
        ],
      };

            let option6 = {
        tooltip: {
          formatter: "{a} <br/>{b}  {c}%",
        },
        toolbox: {
          feature: {
            restore: {},
            saveAsImage: {},
          },
        },
        series: [
          {
            name: "硬盘使用率",
            type: "gauge",
            detail: { 
            formatter: this.disk.name ,
            textStyle:{
                fontSize:11
              }
              },
            data: [{ value: this.disk.value, name: "" }],
          },
        ],
      };

            let option7 = {
        tooltip: {
          formatter: "{a} <br/>{b}  {c}%",
        },
        toolbox: {
          feature: {
            restore: {},
            saveAsImage: {},
          },
        },color:[
            '#ff6666','#3cb371','#b8860b','#30e0e0'
        ],
        series: [
          {
            name: "CPU使用率",
            type: "gauge",
            detail: { 
            formatter: this.cpu.name ,
            textStyle:{
                fontSize:11
              }
              },
            data: [{ value: this.cpu.value, name: "" }],
          },
        ],
      };


            let option8 = {
        tooltip: {
          formatter: "{a} <br/>{b}  {c}%",
        },
        toolbox: {
          feature: {
            restore: {},
            saveAsImage: {},
          },
        },
        series: [
          {
            name: "GPU使用率",
            type: "gauge",
            detail: { 
            formatter: this.gpu.name ,
            textStyle:{
                fontSize:11
              }
              },
            data: [{ value: this.gpu.value, name: "" }],
          },
        ],
      };

      myChart5.setOption(option5);
      myChart6.setOption(option6);
      myChart7.setOption(option7);
      myChart8.setOption(option8);
    },
  },
};
</script>
<style scoped>
#monitor_chats {
  height: 100%;
}
#monitor_chats .el-card {
  height: 100%;
}
/* .el-tabs{
  height: auto !important;
} */
.f1,.f2{
  height: 40%;
}
.flexBox {
  display: flex;
}
.flexBox .parent {
  width: 48%;
  margin: 1%;
}

.serverDiv{
display: flex;width:100%;height:400px;margin-bottom: 1%;
}

.serverDetail{
  width: 48%;
  height: 100%;
  margin: 1%;
}
.flexBox .midParent {
  width: 98%;
  margin: 1%;
}

</style>
<style>
.box-card .el-card__header {
  padding: 10px 20px !important;
}
.box-card .el-card__body {
  height: calc(100% - 42px) !important;
}
</style>
