src/main/java/com/photo/
├── PhotoApplication.java
├── common/
│   ├── constant/CommonConstant.java      ← 系统常量
│   ├── entity/BaseEntity.java            ← 公共字段基类
│   ├── entity/PageResult.java            ← 分页封装
│   ├── exception/BusinessException.java  ← 业务异常
│   ├── exception/GlobalExceptionHandler.java ← 全局异常处理
│   └── result/R.java / ResultCode.java   ← 统一响应
├── config/
│   ├── MinioConfig.java / MinioProperties.java
│   ├── MybatisPlusConfig.java            ← 分页+自动填充
│   └── SaTokenConfig.java                ← 认证+角色权限
├── utils/MinioUtils.java                 ← 文件操作工具
└── module/
├── user/     → 注册、登录、个人信息、改密码
├── album/    → 相册增删改查
├── photo/    → 上传、删除、搜索、下载、批量操作
├── tag/      → 标签管理、图片打标签
├── share/    → 创建分享链接、访问分享
└── admin/    → 用户管理、禁用、重置密码、配额调整

接口文档：启动后访问 http://localhost:8080/doc.html 查看所有接口。

模块	接口
用户	POST /api/user/register /login /logout，GET/PUT /api/user/info，PUT /api/user/password
相册	CRUD /api/album
图片	POST /api/photo/upload，GET /api/photo/page，DELETE /api/photo，PUT /api/photo/move
标签	GET/PUT/DELETE /api/tag
分享	POST /api/share，GET /s/{shareCode}
管理员	GET /api/admin/user/page，PUT 禁用/重置密码/配额