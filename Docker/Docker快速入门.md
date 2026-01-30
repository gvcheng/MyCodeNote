# Docker 实战教程笔记

```markdown
从「环境搭建」到「企业级实战部署」，掌握 Docker 核心操作、多容器协同、项目容器化全流程，解决开发 / 运维中的环境一致性、跨平台部署问题。

### 适用人群
- Docker 入门 / 进阶学习者
- 需落地微服务、中间件容器化部署的开发 / 运维人员
- 想通过 Docker 优化开发 / 部署流程的技术人员
```



## 1. Docker 环境搭建

 Ubuntu系统一键安装命令（阿里云镜像加速）

```bash
curl -fsSL https://github.com/tech-shrimp/docker_installer/releases/download/latest/linux.sh| bash -s docker --mirror Aliyun
```

启动docker

```bash
service docker start
```



## 2. 镜像与容器实战

### 2.1 镜像核心操作（模板管理）

|         功能         |                           命令示例                           |                       关键说明                        |
| :------------------: | :----------------------------------------------------------: | :---------------------------------------------------: |
| 拉取镜像（指定版本） |       `docker pull mysql:8.0` `docker pull nginx:1.25`       |            避免用`latest`，防止版本不一致             |
|     查看本地镜像     |             `docker images` 或 `docker image ls`             | `grep 镜像名` 可筛选（如 `docker images grep mysql`） |
|       删除镜像       |               `docker rmi 镜像ID/镜像名:版本`                |      需先删除依赖该镜像的容器（加`-f`强制删除）       |
|   保存 / 加载镜像    | `docker save -o 镜像名.tar 镜像名:版本`  `docker load -i 镜像名.tar` |                 用于跨服务器传输镜像                  |



### 2.2 容器核心操作（运行实例）

#### 高频命令（含实战参数）

```bash
# 1. 创建并启动容器（最核心）
# 示例：启动MySQL容器（端口映射+数据持久化+环境变量）
docker run -d -p 3306:3306 \
  --name mysql8 \
  -v /data/mysql:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=123456 \
  mysql:8.0

# 参数说明：
# -d：后台运行；-p：宿主机端口:容器端口（外部访问）；--name：自定义容器名；
# -v：数据卷挂载（持久化数据，容器删除不丢失）；-e：设置环境变量

# 2. 容器启停/重启
docker start 容器ID/容器名  # 启动
docker stop 容器ID/容器名   # 停止
docker restart 容器ID/容器名# 重启

# 3. 查看容器状态
docker ps  # 运行中的容器
docker ps -a  # 所有容器（含运行中/已停止）
docker ps -a | grep mysql # 筛选容器（例：查找mysql相关容器）

# 4. 进入容器（调试用）
docker exec -it 容器ID/容器名 /bin/bash  # 如：docker exec -it mysql8 /bin/bash
# 若提示无bash，替换为 /bin/sh

# 5. 查看容器日志（排查故障）
docker logs -f 容器ID/容器名  # -f：实时查看
docker logs --tail 100 容器ID/容器名  # 查看最后100行

# 6. 删除容器
docker rm 容器ID/容器名  # 删除停止的容器
docker rm -f 容器ID/容器名  # 强制删除运行中的容器
```

#### 易错点排查

- 端口冲突：`netstat -tulpn | grep 端口号` 查看占用进程，关闭后重试；
- 数据丢失：数据库、日志类容器必须用`-v`挂载数据卷；
- 容器启动失败：用`docker logs 容器ID`查看报错日志（高频原因：端口占用、配置错误）。