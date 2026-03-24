src/main/java/com/photo/
├── PhotoApplication.java
│
├── common/
│   ├── result/
│   │   ├── R.java              ← 统一响应体 R<T>
│   │   └── ResultCode.java     ← 响应状态码枚举
│   ├── exception/
│   │   ├── BusinessException.java      ← 业务异常
│   │   └── GlobalExceptionHandler.java ← 全局异常处理器
│   ├── constant/
│   │   └── CommonConstant.java ← 系统常量
│   └── entity/
│       ├── BaseEntity.java     ← 公共字段基类（id/时间/逻辑删除）
│       └── PageResult.java     ← 分页结果封装
│
├── config/
│   ├── MinioProperties.java    ← MinIO 配置属性
│   ├── MinioConfig.java        ← MinIO 客户端 Bean
│   └── MybatisPlusConfig.java  ← 分页插件 + 自动填充
│
└── utils/
└── MinioUtils.java         ← MinIO 上传/删除/获取URL


R<T>	所有接口统一返回格式	return R.ok(data)
ResultCode	错误码枚举，避免魔法数字	R.fail(ResultCode.USER_NOT_EXIST)
BusinessException	业务流程中断时抛出	throw new BusinessException(ResultCode.PHOTO_NOT_EXIST)
BaseEntity	业务实体继承它，自动有 id/时间/删除字段	class User extends BaseEntity
PageResult	分页接口的返回数据	return R.ok(PageResult.of(...))
CommonConstant	角色、状态等常量	CommonConstant.ROLE_ADMIN