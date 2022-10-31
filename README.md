# 1.项目模块描述

模块 | 描述 | 使用端口 
--- | --- | --- 
test | 测试代码和样例 | 9999 
business | 业务代码  |  
common | 工具代码 |   
monitor  | 监控服务  | 9091
platform | 应用入口 | 9090

# 2.启动后台项目
-  把platform模块下resources/init.sql中数据插入到数据库中
 - 修改platform模块下resource/applicaition.yml中的数据库连接信息
```$xslt
    url: jdbc:mysql://localhost:3306/haoliang
    username: root
    password: 123456
```

# 3.启动前端项目
```$xslt
cd vue
npm i 
npm run dev
#访问 http://127.0.0.1:8080/  登录系统
```
账号: admin 
密码: 123456



# 4.生成本地接口文档到resource/static目录下
在父工程命令行执行下面命令
mvn smart-doc:markdown -Dfile.encoding=UTF-8  -pl :system -am
访问地址: http://localhost:4612/api.html

