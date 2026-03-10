# Docker：快速入门与基础操作

快速构建、运行、管理应用的工具

---

## 快速入门 01

### 部署MySQL

#### 传统方式安装MySQL（CentOS7）

传统安装步骤繁琐，需依次执行以下命令，且存在**命令多记不住、安装包难找、步骤复杂易出错**的问题：

1. 查看已安装的MySQL/MariaDB

    ```Bash
    
    [root@gvcheng ~]# rpm -qa | grep mysql
    [root@gvcheng ~]# rpm -qa | grep mariadb
    # 输出示例：mariadb-libs-5.5.68-1.el7.x86_64
    ```

2. 卸载自带的MySQL/MariaDB 

    ```Bash
    
    [root@gvcheng ~]# rpm -e --nodeps mariadb-libs-5.5.68-1.el7.x86_64
    ```

3. 创建目录并上传、解压MySQL安装包

    ```Bash
    
    [root@gvcheng ~]# mkdir /usr/local/mysql
    [root@gvcheng ~]# tar -zxvf mysql-5.7.25-1.el7.x86_64.rpm-bundle.tar.gz -C /usr/local/mysql
    ```

4. 依次安装MySQL相关RPM包

    ```Bash
    
    [root@gvcheng ~]# rpm -ivh mysql-community-common-5.7.25-1.el7.x86_64.rpm
    [root@gvcheng ~]# rpm -ivh mysql-community-libs-5.7.25-1.el7.x86_64.rpm
    [root@gvcheng ~]# rpm -ivh mysql-community-devel-5.7.25-1.el7.x86_64.rpm
    [root@gvcheng ~]# rpm -ivh mysql-community-libs-compat-5.7.25-1.el7.x86_64.rpm
    [root@gvcheng ~]# rpm -ivh mysql-community-client-5.7.25-1.el7.x86_64.rpm
    [root@gvcheng ~]# yum install net-tools
    [root@gvcheng ~]# yum install openssl-devel -y
    [root@gvcheng ~]# rpm -ivh mysql-community-server-5.7.25-1.el7.x86_64.rpm
    ```



#### Docker方式部署MySQL

**前提**：停掉虚拟机中的MySQL，虚拟机已安装Docker且网络开通

**执行命令**：

```Bash
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123 \
  mysql:8.0
```

**首次运行输出示例**：

```Bash

Unable to find image 'mysql:latest' locally
latest: Pulling from library/mysql
72a69066d2fe: Pull complete
93619dbc5b36: Pull complete
99da31dd6142: Pull complete
626033c43d70: Pull complete
Status: Downloaded newer image for mysql:latest
a6cec8ff4765ca0876d0453f3ccab205fca29b5dce74f8cfbad8d76571bf79be
```

### 镜像和容器核心概念

当利用Docker安装应用时，Docker会自动完成镜像拉取和容器创建，核心概念如下：

- **镜像（image）**：包含应用本身，还包含应用运行所需的环境、配置、系统函数库的文件包。

- **容器（container）**：运行镜像时创建的**隔离运行环境**，为镜像的应用进程提供独立运行空间。

- **镜像仓库**：存储和管理镜像的平台，Docker官方维护的公共仓库为**Docker Hub**。

**架构关系**：

计算机硬件 → 内核 → 系统应用 → Docker Server（docker daemon 守护进程）↔ 镜像仓库 → 本地images（镜像）→ `docker run` 命令 → Container（容器）

（注：Docker Client端通过命令操作Docker Server完成镜像和容器管理）

![01](D:\Program\MyNotes\notes_image\docker\docker01.png)

### Docker run命令解读

```shell
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123 \
  -v /root/mysql/data:/var/lib/mysql \
  -v /root/mysql/init:/docker-entrypoint-initdb.d \
  -v /root/mysql/conf:/etc/mysql/conf.d \
  mysql
```

部署MySQL的`docker run`命令是Docker创建并运行容器的核心命令，各参数含义如下：

- `docker run`：核心指令，用于**创建并运行一个容器**

- `-d`：让容器在**后台运行**

- `--name mysql`：为容器指定唯一名称（容器名不可重复）

- `-p 3306:3306`：设置**端口映射**，格式为`宿主机端口:容器内端口`

- `-e KEY=VALUE`：为容器设置**环境变量**（可多个）

- `mysql:8.0`：指定运行的镜像名称（未指定版本时默认拉取`latest`版）

**端口映射示例**：

宿主机IP为`192.168.44.128`，通过`-p 3306:3306`将宿主机3306端口映射到mysql容器3306端口，外部连接地址为：`jdbc:mysql://192.168.44.128:3306`

![02](D:\Program\MyNotes\notes_image\docker\docker02.png)

### 镜像命名规范

镜像名称采用**双部分结构**，格式为：`[repository]:[tag]`

- `repository`：镜像名，标识镜像的应用类型

- `tag`：镜像版本，标识镜像的具体版本号

- **默认规则**：未指定`tag`时，默认使用`latest`，代表该镜像的最新版本

**示例**：`mysql:8.0`

- Repository：`mysql`（镜像名）

- Tag：`8.0`（版本号）

### 快速入门总结

1. `docker run`命令常见核心参数：

    - `-d`：让容器后台运行

    - `--name`：给容器命名（唯一）

    - `-e`：设置容器环境变量

    - `-p`：宿主机端口映射到容器内端口

2. 镜像名称标准结构：`Repository:TAG`（镜像名:版本号）

---

## Docker基础 02

### 常见命令

Docker的核心命令为**镜像操作**和**容器操作**，涵盖镜像的拉取、构建、管理，以及容器的启停、查看、进入等，官方文档：[https://docs.docker.com/](https://docs.docker.com/)

![03](D:\Program\MyNotes\notes_image\docker\docker03.png)

#### 镜像与容器操作流转

- 镜像仓库 ↔ 本地镜像：`docker pull`（拉取）、`docker push`（推送）

- 本地镜像管理：`docker images`（查看）、`docker rmi`（删除）、`docker build`（构建）、`docker save`（保存）、`docker load`（加载）

- 容器生命周期管理：

    - 运行：`docker run`

    - 查看：`docker ps`

    - 启停：`docker stop`（停止）、`docker start`（启动）

    - 删除：`docker rm`

    - 日志：`docker logs`

    - 进入：`docker exec`

#### 实操案例

**案例1：拉取并操作Nginx镜像/容器**

需求：

1. 在DockerHub中搜索Nginx镜像，查看镜像名称

2. 拉取Nginx镜像

3. 查看本地镜像列表

4. 创建并运行Nginx容器

5. 查看运行中的容器

6. 停止Nginx容器

7. 再次启动Nginx容器

8. 进入Nginx容器内部

9. 删除Nginx容器

**案例2：利用Nginx容器部署静态资源**

需求：

1. 创建Nginx容器，修改容器内`html`目录下的`index.html`文件，查看页面变化

2. 将本地静态资源部署到Nginx容器的`html`目录

```shell
# 第1步，去DockerHub查看nginx镜像仓库及相关信息

# 第2步，拉取Nginx镜像
docker pull nginx

# 第3步，查看镜像
docker images
# 结果如下：
REPOSITORY   TAG       IMAGE ID       CREATED         SIZE
nginx        latest    605c77e624dd   16 months ago   141MB
mysql        latest    3218b38490ce   17 months ago   516MB

# 第4步，创建并允许Nginx容器
docker run -d --name nginx -p 80:80 nginx

# 第5步，查看运行中容器
docker ps
# 也可以加格式化方式访问，格式会更加清爽
docker ps --format "table {{.ID}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}\t{{.Names}}"

# 第6步，访问网页，地址：http://虚拟机地址

# 第7步，停止容器
docker stop nginx

# 第8步，查看所有容器
docker ps -a --format "table {{.ID}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}\t{{.Names}}"

# 第9步，再次启动nginx容器
docker start nginx

# 第10步，再次查看容器
docker ps --format "table {{.ID}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}\t{{.Names}}"

# 第11步，查看容器详细信息
docker inspect nginx

# 第12步，进入容器,查看容器内目录
docker exec -it nginx bash
# 或者，可以进入MySQL
docker exec -it mysql mysql -uroot -p

# 第13步，删除容器
docker rm nginx
# 发现无法删除，因为容器运行中，强制删除容器
docker rm -f nginx
```

> Linux小技巧：命令别名
>
> 编辑文件
>
> ```shell
> vi ~/.bashrc
> ```
>
> 插入以下内容
>
> ```shell
> alias dps='docker ps --format "table {{.ID}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}\t{{.Names}}"'
> ```
>
> 使命令别名生效
>
> ```
> source ~/.bashrc
> ```



### 数据卷（volume）

![04](D:\Program\MyNotes\notes_image\docker\docker04.png)

#### 核心概念

数据卷是一个**虚拟目录**，作为容器内目录与宿主机目录之间映射的**桥梁**，解决容器内文件操作繁琐、容器数据难以迁移的问题。

#### 数据卷映射原理

Nginx容器示例：

容器内核心目录（`/etc/nginx/conf`、`/usr/share/nginx/html`）通过数据卷映射到宿主机的`/var/lib/docker/volumes/[数据卷名]/_data`目录，操作宿主机该目录即可同步修改容器内文件。

#### 数据卷相关命令

|命令|说明|文档地址|
|---|---|---|
|docker volume create|创建数据卷|docker volume create|
|docker volume ls|查看所有数据卷|docker volume ls|
|docker volume rm|删除指定数据卷|docker volume rm|
|docker volume inspect|查看某个数据卷的详情|docker volume inspect|
|docker volume prune|清除未使用的闲置数据卷|docker volume prune|
#### 数据卷挂载

- **挂载命令**：创建容器时，通过 `-v 数据卷名:容器内目录` 完成挂载

- **自动创建**：若挂载的数卷据不存在，Docker会在创建容器时**自动创建该数据卷**

- **示例**：创建Nginx容器并挂载数据卷到容器内`/usr/share/nginx/html`

    ```Bash
    docker run -d --name nginx -p 80:80 -v nginx-html:/usr/share/nginx/html nginx
    ```

#### 本地目录挂载

除数据卷挂载外，Docker还支持**宿主机本地目录直接挂载**到容器内目录，适用于需要自定义宿主机映射路径的场景：

- **挂载命令**：`-v 本地目录:容器内目录`

- **关键注意**：本地目录必须以`/`或`./`开头，否则会被Docker识别为**数据卷**而非本地目录

    - 错误示例：`-v mysql:/var/lib/mysql` → 识别为**数据卷**`mysql`

    - 正确示例：`-v ./mysql:/var/lib/mysql` → 识别为**当前目录下**的`mysql`目录

#### 数据卷实操案例

**案例1：利用Nginx容器部署静态资源**

需求：

1. 创建Nginx容器，修改容器内`html`目录下的`index.html`文件内容

2. 将本地静态资源部署到Nginx容器的`html`目录

**提示**：使用`-v 数据卷:容器内目录`完成挂载，操作宿主机数据卷目录即可同步修改容器内文件。

**案例2：MySQL容器的本地目录挂载**

需求：

1. 查看MySQL容器默认的挂载情况

2. 基于宿主机目录实现MySQL**数据目录、配置文件、初始化脚本**的自定义挂载

**挂载要求**：

1. 挂载`/root/mysql/data` → 容器内`/var/lib/mysql`（数据目录）

2. 挂载`/root/mysql/init` → 容器内`/docker-entrypoint-initdb.d`（初始化脚本目录，放置SQL脚本）

3. 挂载`/root/mysql/conf` → 容器内`/etc/mysql/conf.d`（配置文件目录）

```bash
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123 \
  -v /root/mysql/data:/var/lib/mysql \
  -v /root/mysql/init:/docker-entrypoint-initdb.d \
  -v /root/mysql/conf:/etc/mysql/conf.d \
  mysql
```

#### 数据卷核心总结

1. 数据卷的作用：将宿主机目录与容器内目录映射，方便操作容器内文件、迁移容器数据。

2. 数据卷挂载方式：创建容器时通过`-v 数据卷名:容器内目录`，数据卷不存在时自动创建。

3. 核心命令：

    - `docker volume ls`：查看所有数据卷

    - `docker volume rm`：删除指定数据卷

    - `docker volume inspect`：查看数据卷详情

    - `docker volume prune`：清理未使用的数据卷

### 自定义镜像

#### 核心概念

镜像是包含**应用程序、运行依赖的系统函数库、配置文件**的文件包，**构建镜像**的过程就是将应用及所有运行依赖打包的过程。

#### 传统Java部署 vs Docker构建Java镜像对比

|传统部署Java应用步骤|Docker构建Java镜像步骤|
|---|---|
|1. 准备Linux服务器<br>2. 安装JRE并配置环境变量<br>3. 拷贝Jar包到服务器<br>4. 执行命令运行Jar包|1. 准备Linux基础运行环境<br>2. 安装JRE并配置环境变量<br>3. 拷贝Jar包到镜像中<br>4. 编写镜像的启动脚本|
#### 镜像的三层结构

镜像采用**分层打包**的结构，从底层到上层依次为：

1. **基础镜像（BaseImage）**：应用运行的基础环境，包含系统函数库、系统配置、基础文件等（如Ubuntu 16.04、CentOS 7）。
2. **层（Layer）**：在基础镜像上执行的每一步操作（如安装JRE、拷贝Jar包、配置环境变量）都会生成一个新的层，层可复用。
3. **入口（Entrypoint）**：镜像的运行入口，指定容器启动时执行的命令（如Java应用的`java -jar xx.jar`）。

![05](D:\Program\MyNotes\notes_image\docker\docker05.png)

### Dockerfile

#### 核心概念

Dockerfile是一个**纯文本文件**，包含一系列构建镜像的**指令（Instruction）**，用于描述镜像的分层结构和构建步骤，Docker可根据Dockerfile**自动构建自定义镜像**。

#### 常见Dockerfile指令

|指令|说明|示例|
|---|---|---|
|FROM|指定构建镜像的**基础镜像**（必选指令，首行）|FROM centos:6|
|ENV|设置环境变量，后续指令可直接引用|ENV JAVA_DIR=/usr/local|
|COPY|将**本地文件/目录**拷贝到镜像的指定目录|COPY ./jre11.tar.gz /tmp|
|RUN|执行Linux Shell命令，主要用于安装依赖/配置环境|RUN tar -zxvf /tmp/jre11.tar.gz && export PATH=/tmp/jre11:$PATH|
|EXPOSE|声明容器运行时监听的端口（仅说明，不做端口映射）|EXPOSE 8080|
|ENTRYPOINT|指定容器的**启动命令**，容器运行时自动执行|ENTRYPOINT ["java", "-jar", "app.jar"]|
#### Dockerfile实操示例

**示例1：基于Ubuntu基础镜像构建Java镜像**

```Dockerfile
# 指定基础镜像
FROM ubuntu:16.04
# 配置环境变量，指定JDK安装目录
ENV JAVA_DIR=/usr/local
# 拷贝本地JDK压缩包和Java项目Jar包到镜像中
COPY ./jdk8.tar.gz $JAVA_DIR/
COPY ./docker-demo.jar /tmp/app.jar
# 解压JDK并修改目录名
RUN cd $JAVA_DIR \ && tar -xf ./jdk8.tar.gz \ && mv ./jdk1.8.0_144 ./java8
# 配置Java环境变量
ENV JAVA_HOME=$JAVA_DIR/java8
ENV PATH=$PATH:$JAVA_HOME/bin
# 容器启动命令（运行Jar包）
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**示例2：基于JDK基础镜像构建Java镜像（简化版）**

直接使用官方已封装的JDK基础镜像，省略JDK安装步骤，简化Dockerfile：

```Dockerfile
# 基础镜像：官方OpenJDK 11的JRE环境
FROM openjdk:11.0-jre-buster
# 拷贝本地Jar包到镜像根目录
COPY docker-demo.jar /app.jar
# 容器启动命令
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### 构建自定义镜像的命令

编写完Dockerfile后，通过`docker build`命令构建镜像，核心格式：

```Bash
docker build -t 镜像名:版本号 Dockerfile所在目录 
```

**参数说明**：

- `-t`：为镜像指定名称和版本，格式`repository:tag`，未指定tag时默认`latest`。

- `.`：表示Dockerfile在**当前目录**，若在其他目录则填写绝对/相对路径。

**示例**：

```Bash
docker build -t myImage:1.0 .
```

#### 自定义镜像核心总结

1. 镜像结构：由基础镜像、多层操作层、入口指令分层打包而成，层可复用。

2. Dockerfile作用：通过标准化指令描述镜像的构建步骤，实现镜像的自动化、可重复构建。

3. 构建命令：`docker build -t 镜像名:版本号 Dockerfile目录`。

### 容器网络

#### 默认网络模式

Docker默认创建一个名为`docker0`的**虚拟网桥**（地址为`172.17.0.1/16`），所有容器默认以`bridge`模式连接到该网桥：

- 每个容器会分配独立的内网IP（如`172.17.0.2`、`172.17.0.3`）。

- 容器通过`veth`虚拟网卡与`docker0`网桥通信，实现容器间、容器与宿主机的网络互通。

#### 容器网络核心特性

**默认网络中容器无法通过容器名互相访问**，只有将容器加入**自定义网络**，容器之间才能通过**容器名/服务名**直接通信（核心特性，用于多容器协作）。

#### 容器网络相关命令

|命令|说明|文档地址|
|---|---|---|
|docker network create|创建一个自定义网络|docker network create|
|docker network ls|查看Docker中所有的网络|docker network ls|
|docker network rm|删除指定的自定义网络|docker network rm|
|docker network prune|清除未使用的闲置网络|docker network prune|
|docker network connect|将指定容器加入某一个网络|docker network connect|
|docker network disconnect|将指定容器从某一个网络移除|docker network disconnect|
|docker network inspect|查看某个网络的详细信息（含接入的容器）|docker network inspect|
---

## 项目部署 03

### 部署Java应用

#### 实操案例

需求：将课前资料提供的`hmall`项目打包为自定义Docker镜像，并完成容器部署，**镜像名指定为hmall**。

（核心步骤：编写Dockerfile → 执行`docker build`构建镜像 → 执行`docker run`创建容器，可结合数据卷、自定义网络优化部署）

### 部署前端

#### 实操案例

需求：创建一个新的Nginx容器，将课前资料提供的**Nginx配置文件（nginx.conf）** 和**前端静态资源目录（html）** 挂载到容器对应目录，实现前端项目的部署。

（核心步骤：使用`-v 本地目录:容器内目录`完成配置文件和静态资源的挂载，保证本地修改可同步到容器）

```bash
docker run -d \
 --name nginx \
 -p 18080:18080 \
 -p 18081:18081 \
 -v /root/nginx/html:/usr/share/nginx/html \
 -v /root/nginx/nginx.conf:/etc/nginx/nginx.conf \
 --network dockerNet \
 nginx
```



### DockerCompose

#### 核心概念

Docker Compose是Docker的官方编排工具，通过一个单独的**`docker-compose.yml`** 模板文件（YAML格式），可以**定义一组相互关联的应用容器**，实现多容器的**一键创建、启动、停止、删除**，解决多容器协作时命令繁琐、配置复杂的问题。

#### DockerCompose核心术语

- **项目（Project）**：由一组相互关联的容器组成的整体应用（如hmall项目包含mysql、hmall、nginx三个容器）。

- **服务（Service）**：项目中的单个容器应用，是`docker-compose.yml`的核心配置单元（如mysql服务、nginx服务）。

#### docker-compose.yml基础格式

```YAML

# 指定Compose的版本（需与Docker版本兼容）
version: "3.8"

# 定义所有服务（容器）
services: 
  # 服务名1：containerA
  containerA:
    image: 镜像名A  # 服务使用的镜像
    container_name: 容器名A  # 容器实际名称
    ports:  # 端口映射
      - "宿主机端口1:容器内端口1"
  # 服务名2：containerB
  containerB:
    image: 镜像名B
    container_name: 容器名B
    ports:
      - "宿主机端口2:容器内端口2"
```

#### 实操示例：hmall项目docker-compose.yml

整合`mysql`、`hmall`（Java应用）、`nginx`（前端）三个服务，实现多容器一键部署，配置如下：

```YAML
version: "3.8"

services:
  # MySQL服务
  mysql:
    image: mysql  # 使用的镜像
    container_name: mysql  # 容器名
    ports:
      - "3306:3306"  # 端口映射
    environment:  # 环境变量
      TZ: Asia/Shanghai
      MYSQL_ROOT_PASSWORD: 123
    volumes:  # 本地目录挂载
      - "./mysql/conf:/etc/mysql/conf.d"
      - "./mysql/data:/var/lib/mysql"
      - "./mysql/init:/docker-entrypoint-initdb.d"
    networks:  # 加入自定义网络
      - hm-net
  # hmallJava应用服务
  hmall:
    build:  # 基于本地Dockerfile构建镜像
       context: .  # Dockerfile所在目录
       dockerfile: Dockerfile  # Dockerfile文件名
    container_name: hmall
    ports:
      - "8080:8080"
    networks:
      - hm-net
    depends_on:  # 依赖mysql服务，启动时先启动mysql
      - mysql
  # Nginx前端服务
  nginx:
    image: nginx
    container_name: nginx
    ports:
      - "18080:18080"
      - "18081:18081"
    volumes:  # 挂载配置文件和前端静态资源
      - "./nginx/nginx.conf:/etc/nginx/nginx.conf"
      - "./nginx/html:/usr/share/nginx/html"
    depends_on:  # 依赖hmall服务，启动时先启动hmall
      - hmall
    networks:
      - hm-net
# 定义自定义网络
networks:
  hm-net:
    name: hmall  # 自定义网络名称
```

#### docker run 与 docker-compose.yml 对比

以MySQL服务为例，对比传统命令和Compose配置的差异，体现Compose的简洁性：

**传统docker run命令**

```Bash
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123 \
  -v ./mysql/data:/var/lib/mysql \
  -v ./mysql/conf:/etc/mysql/conf.d \
  -v ./mysql/init:/docker-entrypoint-initdb.d \
  --network hmall \
  mysql
```

**DockerCompose配置**

```YAML
mysql:
  image: mysql
  container_name: mysql
  ports:
    - "3306:3306"
  environment:
    TZ: Asia/Shanghai
    MYSQL_ROOT_PASSWORD: 123
  volumes:
    - "./mysql/conf:/etc/mysql/conf.d"
    - "./mysql/data:/var/lib/mysql"
    - "./mysql/init:/docker-entrypoint-initdb.d" 
  networks:
    - hmall
```

#### Docker Compose核心命令

**命令基础格式**：

```Bash
docker compose [OPTIONS] [COMMAND]
```

在`docker-compose.yml`所在目录执行命令，即可实现多容器的统一管理。

#### Docker Compose常用参数/命令

|类型|参数或指令|说明|
|---|---|---|
|Options|-f|指定`docker-compose.yml`文件的**路径和名称**（默认使用当前目录的该文件）|
||-p|指定**项目（Project）** 的名称（默认使用当前目录名）|
|Commands|up|创建并启动所有服务的容器（加`-d`表示后台运行）|
||down|停止并**移除**所有容器、自定义网络（数据卷/挂载目录不会删除）|
||ps|列出当前项目中所有启动的容器|
||logs|查看指定服务/所有服务的容器日志（加`-f`实时刷新）|
||stop|停止当前项目中所有运行的容器（不删除）|
||start|启动当前项目中所有已停止的容器|
||restart|重启当前项目中所有容器|
||top|查看当前项目中容器内运行的进程|
||exec|在指定的运行中容器内执行命令（如进入容器）|