**商城系统**

这是一个采用SpringCloud微服务架构的商城系统，分为商品微服务、购物车微服务、用户微服务、订单微服务、支付微服务、网关微服务和查询微服务。该项目使用MySQL数据库，使用的中间件包括Nacos、Seata、RabbitMQ、Elasticsearch和Kibana。除了这些微服务外，还有hm-common公共子模块和hm-api开放Feign公共调用模块。

**项目结构**

1. **项目文件结构**

```
perl复制代码Marketplace-system/
├── hmall-nginx/             # 前端文件夹
├── hmall/                   # 后端文件夹
├── sql/                     # 数据库文件夹
├── picture/                 # 图片文件夹
├── README.md                # 项目文档
```

1. **主要目录和文件**

- **hmall-nginx/**：包含电商系统的前端代码。
- **hmall/**：包含处理业务逻辑的后端代码。
- **sql/**：包含与数据库操作相关的SQL脚本。
- **picture/**：存储项目中使用的图像资源。
- **README.md**：项目文档文件，包含项目概述、设置说明和使用方法。

**环境**

- 项目使用Maven构建，JDK版本为17，Elasticsearch版本为7.1.2，MySQL版本为8或更高。