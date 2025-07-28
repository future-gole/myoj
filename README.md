# MyOJ - 在线判题系统

## 项目介绍

MyOJ 是一个基于 Spring Cloud 微服务架构的在线判题系统（Online Judge），支持代码在线编写、提交、编译和执行。系统采用现代化的微服务架构设计，具备高可用性、可扩展性和高性能的特点。

## 技术栈

### 后端技术栈
- **Spring Cloud Alibaba **
- **Spring Cloud Gateway**
- **Nacos**
- **MyBatis Plus**
- **Redis**
- **RabbitMQ** 
- **MySQL** 
- **Docker** 
- **XXL-Job**
- **Elasticsearch**

## 项目架构

### 核心模块说明

#### 1. API 网关模块 (oj-gateway)
- 统一入口，路由转发
- 身份认证和权限控制

#### 2. 业务模块 (oj-modules)

**系统管理模块 (oj-system)**
- 题目管理：增删改查题目信息 
- 竞赛管理：创建和管理编程竞赛 
- 用户管理：系统用户管理

**用户端模块 (oj-friend)**
- 代码提交：用户提交代码进行判题 
- 结果查询：查看代码执行结果
- 异步提交：支持消息队列异步处理

**判题模块 (oj-judge)**
- 代码编译和执行
- 测试用例验证
- 结果评估和反馈
- Docker 容器隔离执行

**定时任务模块 (oj-job)**
- 系统维护任务
- 数据统计

#### 3. 公共模块 (oj-common)
- **核心模块 (oj-common-core)**：通用工具类和常量 
- **安全模块 (oj-common-security)**：认证和授权
- **数据库模块 (oj-common-mybatis)**：数据库配置 
- **缓存模块 (oj-common-redis)**：Redis 配置 
- **消息队列模块 (oj-common-rabbitmq)**：RabbitMQ 配置 
- **文件模块 (oj-common-file)**：文件上传下载 
- **API文档模块 (oj-common-swagger)**：Swagger 配置 

## 功能特性

### 🎯 核心功能
- **在线编程**：支持多种编程语言（当前支持 Java）
- **实时判题**：代码提交后实时编译执行和结果反馈
- **异步处理**：基于 RabbitMQ 的异步判题机制，提高系统并发能力
- **安全隔离**：Docker 容器化代码执行环境，确保系统安全
- **题目管理**：完整的题目CRUD操作，支持测试用例管理
- **考试系统**：支持创建编程竞赛和考试

### 🔧 技术特性
- **微服务架构**：模块化设计，便于扩展和维护
- **服务注册发现**：基于 Nacos 的服务治理
- **配置中心**：统一配置管理
- **API 网关**：统一入口和路由转发
- **分布式缓存**：Redis 提升系统性能
- **消息队列**：RabbitMQ 实现异步处理
- **容器化部署**：Docker 支持

### 📊 系统特性
- **高可用性**：微服务架构支持服务的独立部署和扩展
- **高性能**：Redis 缓存和异步处理机制
- **可扩展性**：模块化设计，易于添加新功能
- **安全性**：JWT 认证和 Docker 隔离

## 快速开始

### 环境要求
- 请根据pom文件中的版本来进行配置

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/future-gole/myoj.git
cd myoj
```

2. **配置数据库**
    - 创建 MySQL 数据库
    - 导入初始化脚本[deploy/sql/init.sql]()

3**编译项目**
```bash
mvn clean install
```

4**启动服务**
按照以下顺序启动服务：
    - 启动 oj-gateway
    - 启动 oj-system
    - 启动 oj-friend  
    - 启动 oj-judge
    - 启动 oj-job

## 项目结构

```
myoj/
├── oj-api/                    # 公共API接口
├── oj-common/                 # 公共模块
│   ├── oj-common-core/        # 核心工具类
│   ├── oj-common-security/    # 安全模块
│   ├── oj-common-redis/       # Redis配置
│   ├── oj-common-mybatis/     # MyBatis配置
│   ├── oj-common-swagger/     # Swagger配置
│   ├── oj-common-message/     # 消息处理
│   ├── oj-common-file/        # 文件处理
│   └── oj-common-rabbitmq/    # RabbitMQ配置
├── oj-gateway/                # API网关
├── oj-modules/                # 业务模块
│   ├── oj-system/             # 系统管理模块
│   ├── oj-friend/             # 用户端模块
│   ├── oj-judge/              # 判题模块
│   └── oj-job/                # 定时任务模块
└── pom.xml                    # 主项目配置
```

## 许可证

本项目采用 MIT 许可证。详细信息请查看 [LICENSE](LICENSE) 文件。


**注意**：本项目仍在开发中，部分功能可能还不完善。欢迎贡献代码和提出建议！

