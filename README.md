# Marketplace-system

if you want to chinese-version, please [click here](https://github.com/jiangjjl/Marketplace-system/blob/main/README-zh.md)

This is a mall system that adopts the SpringCLoud microservice architecture, which is divided into commodity microservices, shopping cart microservices, user microservices, order microservices, payment microservices, gateway microservices, and query microservices。
The project uses the database MySQL, and the middleware used includes Nacos, Seata, RabbitMQ, ES, and Kibana
In addition to these microservices, hm-common is a public sub-module, and hm-api is an OpenFeign public calling module.

![image1](https://github.com/jiangjjl/Marketplace-system/blob/main/picture/image1.png)

# introduce
hmall - This is the project folder
sql - This is the database folder
hm-nginx - This is the front-end project folder

# Environment

The project is built with Maven, using JDK version 17, Elasticsearch version 7.1.2, and MySQL version 8 or above.

# structure

## 1. **Project File Structure**

```
Marketplace-system/
├── hmall-nginx/             # Frontend folder
├── hmall/                   # Backend folder
├── sql/                     # Database folder
├── picture/                 # Image folder
├── README.md                # Project documentation
```

## 2. **Key Directories and Files**

**hmall-nginx/**: Contains frontend code for the user interface of the e-commerce system.

**hmall/**: Contains backend code that handles business logic.

**sql/**: Contains SQL scripts related to database operations.

**picture/**: Stores image resources used in the project.

**README.md**: Documentation file with an overview of the project, setup instructions, and usage.
