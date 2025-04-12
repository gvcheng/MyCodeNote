[TOC]



## 一、框架简介

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\49e7ec84e112410092f49a3263c9f371\框架图.png)

### 1.1 三层架构

软件开发常用的架构是三层架构，之所以流行是因为有着清晰的任务划分。一般包括以下三层：

**持久层：**

主要完成与数据库相关的操作，即对数据库的增加或改善。

因为数据库访问的对象一般称为 Data Access Object（简称 DAO），所以有人把持久层叫做 DAO 层。

**业务层：**

主要根据功能需求完成业务逻辑的定义和实现。

因为它主要是为上层提供服务的，所以有人把业务层叫做 Service 层 或 Business 层。

**表现层**：

主要完成与最终软件使用用户的交互，需要有交互界面（UI）。

因此，有人把表现层称之为 Web 层 或 View 层。

**调用关系**

- 表现层调用业务层，业务层调用持久层。

**数据交互**

- 各层之间使用 Java 实体对象传递数据。

  

### 1.2 框架

#### 1.2.1 什么是框架？

框架是一套规范，使用框架需遵守其约束。

可理解为半成品软件，开发者在其基础上进行开发。



#### 1.2.2 为什么使用框架？

***功能封装：***

封装冗余且重用率低的代码，通过反射与动态代理实现代码通用性。

开发者可专注于核心业务逻辑。

***简化开发：***

例如：Servlet 开发中需手动获取表单参数，框架通过反射和拦截器自动完成。

传统 CRUD 需编写 SQL，框架提供直接调用方法简化操作。

规范约束：需按框架规范进行配置。



#### 1.2.3 常见的框架

Java 框架为解决特定问题而存在，按层级分类：

***持久层框架：***

解决数据持久化问题。

常用：MyBatis、Hibernate、Spring JDBC 等。

***表现层框架：***

解决用户交互问题。

常见：Struts2、Spring MVC 等。

***全栈框架：***

提供各层解决方案。

代表：Spring（涵盖依赖注入、事务管理、Web 开发等）。

框架选择建议：

- 企业常用组合：Spring + Spring MVC + MyBatis（SSM）。



## 二、Mybatis介绍

### 2.1 原始JDBC操作

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\0f09db8393904bc78783b0bc350f32f2\clipboard.png)



### 2.2 原始 JDBC 操作的分析

***原始 JDBC 开发存在的问题：***

> **数据库连接管理问题：**
>
> 频繁创建和释放数据库连接，导致系统资源浪费，影响性能。
>
> **SQL 硬编码问题：**
>
> SQL 语句直接嵌入 Java 代码中，代码维护困难。若 SQL 需修改，必须改动 Java 代码。
>
> **结果集手动封装问题：**
>
> 查询操作需手动将结果集数据封装到实体对象中，过程繁琐且易出错。
>

***解决方案：***

> **数据库连接池：**
>
> 通过连接池（如 C3P0、Druid）初始化并管理连接资源，减少频繁创建和释放的开销。
>
> **SQL 与代码解耦：**
>
> 将 SQL 语句抽取到 XML 配置文件（如 MyBatis 的 Mapper 文件）中，实现 SQL 与 Java 代码分离。
>
> **自动化映射技术：**
>
> 利用 反射 和 内省（Introspection）技术，自动将数据库字段与实体类属性进行映射（如 ORM 框架的实体映射功能）。



### 2.3 MyBatis 介绍

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\684243ca43834e679a27e8b7531f1b9c\clipboard.png)

MyBatis 是一个基于 ORM（Object Relational Mapping，对象关系映射）的半自动轻量级持久层框架。它封装了 JDBC 操作数据库的复杂流程，开发者仅需关注 SQL 编写，无需手动处理注册驱动、创建 Connection 连接、创建 Statement、手动设置参数、结果集检索等繁琐的JDBC繁杂的过程代码。

**半自动**：MyBatis

**全自动**：Hibernate

**本质区别**：<u>*是否需要手动编写SQL*</u>

**轻量级**：<u>*程序在启动期间所需要消耗的资源多少*</u>

####  MyBatis 历史

起源：Apache 开源项目 iBatis。

2010 年 6 月：项目迁移至 Google Code，并更名为 MyBatis。

2013 年 11 月：代码迁移至 GitHub，开源地址：https://github.com/mybatis/mybatis-3

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\a89e3ae7c5e54a5b948ef083b0e1db06\屏幕截图 2025-03-18 151135.jpg)

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\5b7a285ddec848c49626b26e22091a23\屏幕截图 2025-03-18 152827.jpg)



### 2.4 ORM思想

**ORM（对象关系映射）**

- **O（对象模型）**：程序中的实体对象（如 Java 类）。
- **R（关系模型）**：数据库表结构（如表、字段）。
- **M（映射）**：建立对象属性与表字段的对应关系。

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\54400793e4344005a99326508ec79091\mapping.png)

**如何建立映射关系？**

**定义实体类**：类的属性对应表的字段（如 User 类对应 user 表）。

**配置映射规则**：

1.通过注解（如 @Column(name = "user_name")）。

2.通过 XML 文件（如 MyBatis 的映射文件）。

**框架实现**：ORM 框架自动或半自动完成以下操作：

对象属性 → 表字段的赋值（插入/更新）。

查询结果集 → 对象属性的转换（查询）。

**ORM作为一种思想**

帮助我们跟踪实体的变化，并将实体的变化翻译成SQL脚本执行到数据库中去，也就是将实体的变化映射到了表的变化。

MyBatis采用ORM思想解决了实体和数据库映射的问题。对jdbc 进行了封装，屏蔽了jdbc api 底层访问细节，使我们不用与jdbc api 打交道，就可以完成对数据库的持久化操作。





## 三、MyBatis快速入门

### 3.1 Mybatis开发步骤

MyBatis官网地址：http://www.mybatis.org/mybatis-3/

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\c065d7e14202404b870e101a66a8f748\mybatisweb.png)

**案例需求**：通过mybatis查询数据库user表的所有记录，封装到User对象中，打印到控制台上

**步骤分析：**

> 1. 创建数据库及user表
> 2. 创建maven工程，导入依赖（MySQL驱动、mybatis、junit）
> 3. 编写User实体类
> 4. 编写UserMapper.xml映射配置文件（ORM思想）
> 5. 编写SqlMapConfig.xml核心配置文件
>
> ​	数据库环境配置
>
> ​	映射关系配置的引入（引入映射配置文件的路径）
>
> 6. 编写测试代码
>
>    // 1.加载核心配置文件
>
>    // 2.获取SqlSessionFactory工厂对象
>
>    // 3.获取SqlSession会话对象
>
>    // 4.执行sql
>
>    // 5.打印结果
>
>    // 6.释放资源	
>
>

### 3.2 代码实现

**（1）创建 user 数据表**

```sql 
CREATE DATABASE `mybatis_db`;  
USE `mybatis_db`;  

CREATE TABLE `user` (  
    `id` int(11) NOT NULL auto_increment,  
    `username` varchar(32) NOT NULL COMMENT '用户名称',  
    `birthday` datetime default NULL COMMENT '生日',  
    `sex` char(1) default NULL COMMENT '性别',  
    `address` varchar(256) default NULL COMMENT '地址',  
    PRIMARY KEY (`id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;  

-- 插入数据  
INSERT INTO `user` (`id`, `username`, `birthday`, `sex`, `address`)  
VALUES (1, '嵇围城', '2020-11-11 00:00:00', '男', '江西抚州'),  
       (2, '嵇牛', '2020-12-12 00:00:00', '男', '江西抚州');  
```

**（2）导入 MyBatis 的坐标和其他相关坐标**

```xml
<!--指定编码及版本-->
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
  <java.version>21</java.version>
  <maven.compiler.source>21</maven.compiler.source>
  <maven.compiler.target>21</maven.compiler.target>
</properties>

<!--引入相关依赖-->
<dependencies>
<!--引入mybatis依赖-->
  <dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.4</version>
  </dependency>

<!--引入mysql驱动-->
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.47</version>
  </dependency>

<!--引入junit单元测试-->
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
  </dependency>
```



**（3）编写** **User** **实体类**

```java
public class User {
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;

    // getter/setter 方法  
}  
```

**（4）编写** **UserMapper.xml** **映射文件**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">  

<!--命名空间,与id共同构成唯一标识  namespace.id (user.findAll)-->
<mapper namespace="user">
    <!--编写sql,勿加分号-->
    <!--resultType返回结果类型(自动映射封装),表示封装在哪个实体内,实体属性名需跟字段名一致-->
    <select id="findAll" resultType="com.gvc.domain.User">
        select * from user
    </select>
</mapper>
```

**（5）编写 MyBatis 核心配置文件** **SqlMapConfig.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>
    <!--environments: 运行环境, default表示默认运行哪个环境-->
    <environments default="development">
        <!--数据库环境配置-->
        <environment id="development">
            <!--事务管理器,当前事务交由JDBC管理-->
            <transactionManager type="JDBC"></transactionManager>
            <!--数据源信息, POOLED表示使用类型为Mybatis连接池-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql:///mybatis_db?useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="0709"/>
            </dataSource>
        </environment>
    </environments>

    <!--引入映射配置文件-->
    <mappers>
        <mapper resource="mapper/UserMapper.xml"></mapper>
    </mappers>

</configuration>
```



**（6）编写 测试类**

```java
@Test
//快速入门测试方法
public void mybatisTestQuickStart() throws IOException {

    //1.加载核心配置文件
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");

    //2.获取sqlSessionFactory工厂对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

    //3.获取sqlSession会话对象
    SqlSession sqlSession = sqlSessionFactory.openSession();

    //4.执行sql,参数:statementId: namespace.id
    List<User> userList = sqlSession.selectList("user.findAll");

    //5.遍历打印结果
    for (User user : userList) {
        System.out.println(user);
    }

    //6.关闭资源
    sqlSession.close();
}
```



### 3.3 知识小结

1. *创建 mybatis_db数据库和 user表*
2. *创建项目，导入依赖*
3. *创建 User实体类*
4. *编写映射文件UserMapper.xml*
5. *编写核心文件SqlMapConfig.xml*
6. *编写测试类*





## 四、Mybatis映射文件描述

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\7d60d4e0a1a84ea7b852580886e5dd0a\usermapper.png)





## 五 、MyBatis 增删改查

### 5.1 新增

**（1）编写映射文件** **UserMapper.xml**

```xml
<!--新增用户-->
<!-- #{} 为 MyBatis中的占位符，等同于JDBC中的 ？ -->
<!-- parameterType 指定接收到的参数类型-->
<insert id="saveUser" parameterType="com.gvc.domain.User">
    insert into user(username,birthday,sex,address) values(#{username},#{birthday},#{sex},#{address})
</insert>
```

**（2）编写测试类**

```java
@Test
//测试新增用户
public void testSave() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    User user = new User();
    user.setUsername("小方");
    user.setBirthday(new Date());
    user.setSex("男");
    user.setAddress("美国夏威夷");

    sqlSession.insert("userMapper.saveUser",user);

    //手动提交事务
    sqlSession.commit();

    sqlSession.close();
}
```

**（3）新增注意事项**

> 1. *插入语句使用 insert 标签。*
> 2. *在映射文件中使用 parameterType 属性指定要插入的数据类型。*
> 3. *SQL 语句中使用 #{实体属性名} 方式引用实体中的属性值。*
> 4. *插入操作使用的 API 是 SqlSession.insert("命名空间.id", 实体对象)。*
> 5. *插入操作涉及数据库数据变化，所以要使用 sqlSession 对象显式提交事务即 sqlSession.commit()。*
>



### 5.2 修改

**（1）编写映射文件**

```xml
<!--更新用户-->
<update id="updateUser" parameterType="com.gvc.domain.User">
    update user set username = #{username},birthday = #{birthday},sex = #{sex}, address = #{address} where id = #{id}
</update>
```

**（2）编写测试类**

```java
@Test
//测试更新用户
public void testUpdate() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    User user = new User();
    user.setId(4);
    user.setUsername("马小芳");
    user.setSex("女");
    user.setBirthday(new Date());
    user.setAddress("今州");
    sqlSession.update("userMapper.updateUser",user);

    //手动提交事务
    sqlSession.commit();

    sqlSession.close();
}
```

**（3）修改注意事项**

> 1. *修改语句使用 update 标签。*
> 2. *修改操作使用的 API 是 sqlSession.update("命名空间.id", 实体对象)。*
>



### 5.3 删除

**（1）编写映射配置文件UserMapper.xml**

```xml
<!--删除用户-->
<delete id="deleteUser" parameterType="java.lang.Integer">
    delete from user where id = #{abc}
</delete>
```

**（2）编写测试类**

```java
@Test
//测试删除用户
public void testDelete() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    sqlSession.delete("userMapper.deleteUser",4);

    //手动提交事务
    sqlSession.commit();

    sqlSession.close();
}
```

**（3）删除注意事项**

> 1. *删除语句使用 delete 标签。*
> 2. *SQL 语句中使用 #{任意字符串} 方式引用传递的单个参数。*
> 3. *删除操作使用的 API 是 sqlSession.delete("命名空间.id", object)。*
>



### 5.4 知识小结

**查询**

*代码*：

```java
List<User> userList = sqlSession.selectList("userMapper.findAll");
```

*映射文件：*

```xml
<select id="findAll" resultType="com.gvc.domain.User">
    select * from user
</select>
```



**新增**

*代码*：

```java
sqlSession.insert("userMapper.saveUser",user); 
```

*映射文件：*

```xml
<insert id="saveUser" parameterType="com.gvc.domain.User">
    insert into user(username,birthday,sex,address) values(#{username},#{birthday},#{sex},#{address})
</insert>
```



**修改**

代码：

```xml
sqlSession.update("userMapper.updateUser",user); 
```

映射文件：

```xml
<update id="updateUser" parameterType="com.gvc.domain.User">
    update user set username = #{username},birthday = #{birthday},sex = #{sex}, address = #{address} where id = #{id}
</update>
```



**删除**

代码：

```java
sqlSession.delete("userMapper.deleteUser",4); 
```

映射文件：

```xml
<delete id="deleteUser" parameterType="java.lang.Integer">
    delete from user where id = #{abc}
</delete> 
```





## 六、MyBatis 核心文件概述

### **6.1 MyBatis 核心配置文件层级关系**

MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息。

配置文档的顶层结构如下：

- configuration（配置）

- - [properties（属性）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#properties)
  - [settings（设置）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#settings)
  - [typeAliases（类型别名）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#typeAliases)
  - [typeHandlers（类型处理器）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#typeHandlers)
  - [objectFactory（对象工厂）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#objectFactory)
  - [plugins（插件）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#plugins)
  - [environments（环境配置）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#environments)

- - ​	environment（环境变量）

- - ​		transactionManager（事务管理器）
    
    ​		dataSource（数据源）
  
- - [databaseIdProvider（数据库厂商标识）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#databaseIdProvider)
  - [mappers（映射器）](https://mybatis.org/mybatis-3/zh_CN/configuration.html#mappers)
  
  

### **6.2 MyBatis常用配置解析**

#### 6.2.1 environments标签

数据库环境的配置，支持多环境配置

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\b8e5229314424e8f83e2da9e0cdac183\environments_.png)

**1.事务管理器（transactionManager）类型有两种：**

**JDBC**：

这个配置直接使用了 JDBC 的提交和回滚设置，它依赖于从数据源得到的连接来管理事务作用域。

**MANAGED**：

这个配置几乎没做什么。它从不提交或回滚连接，而是让容器来管理事务的整个生命周期。

例如：MyBatis 与 Spring 整合后，事务交给 Spring 容器管理。

**2.数据源（dataSource）常用类型有三种：**

**UNPOOLED**：

> 这个数据源的实现只是每次被请求时打开和关闭连接。
>

**POOLED**：

> 这种数据源的实现利用“池”的概念将 JDBC 连接对象组织起来。

**JNDI**：

> 这个数据源实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的数据源引用。
>



#### 6.2.2 properties 标签

实际开发中，习惯将数据源的配置信息单独抽取成一个 properties 文件，该标签可以加载额外配置的 properties 文件。

```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql:///mybatis_db?useSSL=false
jdbc.username=root
jdbc.password=0709
```

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\80561507f5814772a977410807be9625\properties.jpg)



#### 6.2.3 typeAliases 标签

类型别名是为 Java 类型设置一个短的名字。

为了简化映射文件 Java 类型设置，MyBatis 框架为我们设置好了一些常用的类型的别名。

| 别名Aliases | 数据类型 |
| ----------- | -------- |
| string      | String   |
| long        | Long     |
| int         | Integer  |
| double      | Double   |
| boolean     | Boolean  |
| .......     | ......   |

原来的类型名称配置如下

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\8e7798fcc11c415381cc00001c6f231e\typealiases.png)

配置typeAliases，为com.gvc.domain.User定义别名为user

<!--设置别名--> <typeAliases>    <!--方式一：给单个实体起别名-->    <typeAlias type="com.gvc.domain.User" alias="user"></typeAlias>    <!--方式二：批量起别名, 别名就是类名，且不区分大小写-->    <package name="com.gvc.domain"/> </typeAliases>

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\033e8cfd9a054b55a5a2718a2a16a119\typealiases.png)



#### 6.2.4 mappers 标签

该标签的作用是加载映射的，加载方式有如下几种：

1. 使用相对于类路径的资源引用，例如：

```xml
<mapper resource="org/mybatis/builder/userMapper.xml"/>  
```

2. 使用完全限定资源定位符（URL），例如：

```xml
<mapper url="file:///var/mappers/userMapper.xml"/>  
```

***（以下两种在 mapper 代理开发中使用）***

3. 使用映射器接口实现类的完全限定类名，例如：

```xml
<!--使用该方法，接口和映射文件需要同包同名--> 
<mapper class="com.gvc.mapper.UserMapper"></mapper>  
```

4. 将包内的映射器接口实现全部注册为映射器，例如：

```xml
<!--批量加载映射--> 
<package name="com.gvc.mapper"/>
```



### 6.3 知识小结

核心配置文件常用配置：

1. ** properties 标签**：该标签可以加载外部的 properties 文件。

```xml
<properties resource="jdbc.properties"></properties>  
```

2. **typeAliases 标签**：设置类型别名。

```xml
<typeAlias type="com.gvc.domain.User" alias="user"></typeAlias>  
```

3. **mapper 标签**：加载映射配置。

```xml
<mapper resource="com/lagou/mapper/userMapping.xml"></mapper>  
```

4. **environment 标签**：数据源环境配置。

```xml  
<environments default="development">  
    <environment id="development">  
        <transactionManager type="JDBC"/>  
        <dataSource type="POOLED">  
            <property name="driver" value="${jdbc.driver}"/>  
            <property name="url" value="${jdbc.url}"/>  
            <property name="username" value="${jdbc.username}"/>  
            <property name="password" value="${jdbc.password}"/>  
        </dataSource>  
    </environment>  
</environments>  
```



## 七、MyBatis 的 API 概述

### 7.1 API 介绍

#### 7.1.1 SqlSession 工厂构建器 SqlSessionFactoryBuilder

**常用 API**：<u>SqlSessionFactory build(InputStream inputStream)</u>

通过加载 MyBatis 核心文件的输入流的形式构建一个 SqlSessionFactory 对象。

**示例代码**：

```java
String resource = "org/mybatis/builder/mybatis-config.xml";  
InputStream inputStream = Resources.getResourceAsStream(resource);  
SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();  
SqlSessionFactory factory = builder.build(inputStream);  
```

其中，Resources 工具类位于 org.apache.ibatis.io 包中。Resources 类帮助从类路径下、文件系统或一个 web URL 中加载资源文件。



#### 7.1.2 SqlSession 工厂对象 SqlSessionFactory

SqlSessionFactory 有多个方法创建 SqlSession 实例，常用的有如下两个：

| 方法                                | 解释                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| **openSession()**                   | 会默认开启一个事务，但事务不会自动提交，需要手动提交事务，更新操作数据才会持久化到数据库中。 |
| **openSession(boolean autoCommit)** | 参数为是否自动提交，如果设置为 true，则不需要手动提交事务。  |



#### 7.1.3 SqlSession 会话对象

SqlSession 实例在 MyBatis 中是非常强大的一个类。在这里你会看到所有执行语句、提交或回滚事务和获取映射器实例的方法。

**执行语句的方法主要有**：

```java
<T> T selectOne(String statement, Object parameter)
<E> List<E> selectList(String statement, Object parameter)
int insert(String statement, Object parameter)
int update(String statement, Object parameter)
int delete(String statement, Object parameter)
```

**操作事务的方法主要有**：

```java
void commit()
void rollback()
```



#### 7.2 MyBatis 基本原理介绍

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\80b99d5b7e5846c5b7fc14af708401aa\mybatis.png)



## 八 、MyBatis 的 Dao 层开发使用

### 8.1 传统开发方式

**（1）编写 UserMapper 接口**

```java
public interface UserMapper {  
    public List<User> findAll() throws Exception;  
}  
```

**（2）编写 UserMapper 实现类**

```java
public class UserMapperImpl implements UserMapper {  
    @Override  
    public List<User> findAll() throws Exception {  
        // 加载配置文件  
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");  
        // 获取 SqlSessionFactory 工厂对象  
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);  
        // 获取 SqlSession 会话  
        SqlSession sqlSession = sqlSessionFactory.openSession();  
        // 执行 SQL  
        List<User> list = sqlSession.selectList("UserMapper.findAll");  
        // 释放资源  
        sqlSession.close();  
        return list;  
    }  
}  
```

**（3）编写 UserMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>  
<!DOCTYPE mapper  
    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"  
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">  
<mapper namespace="UserMapper">  
    <!-- 查询所有 -->  
    <select id="findAll" resultType="user">  
        SELECT * FROM user  
    </select>  
</mapper>  
```

**（4）测试**

```java
@Test  
public void testFindAll() throws Exception {  
    // 创建 UserMapper 实现类  
    UserMapper userMapper = new UserMapperImpl();  
    // 执行查询  
    List<User> list = userMapper.findAll();  
    for (User user : list) {  
        System.out.println(user);  
    }  
}  
```

**（5）知识小结**

传统开发方式

1. *编写 UserMapper 接口*
2. *编写 UserMapper 实现类*
3. *编写 UserMapper.xml*

**传统方式问题思考**：

1. 实现类中，存在 MyBatis 模板代码重复。（builder——>factory——>session）
2. 实现类调用方法时，XML 中的 SQL statement 硬编码到 Java 代码中。

**思考**：能否**只写接口，不写实现类**？只编写接口和 Mapper.xml 即可？

> 因为在 DAO（Mapper）的实现类中对 SqlSession 的使用方式很类似，因此 MyBatis 提供了接口的**动态代理**。



### 8.2 代理开发方式

#### 8.2.1 介绍

采用 MyBatis 的基于接口代理方式实现持久层的开发，这种方式是我们后面进入企业的主流。

基于接口代理方式的开发只需要程序员编写 Mapper 接口，MyBatis 框架会为我们动态生成实现类的对象。

这种开发方式要求我们遵循一定的规范：

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\eaca8becca1a4318a1114b8d48986451\clipboard.png)

**规范要求**：

> **1. Mapper.xml** 映射文件中的 **namespace** 与 **Mapper 接口的全限定名（相对路径）**相同。
>
> **2.** **Mapper 接口方法名称**和 Mapper.xml 映射文件中定义的每个 **statement** **的** **id** 相同。
>
> **3.** **Mapper 接口方法的输入参数类型**和 Mapper.xml 映射文件中定义的每个 SQL 的 **parameterType** **的类型**相同。
>
> **4. Mapper** **接口方法的****输出参数类型**和 Mapper.xml 映射文件中定义的每个 SQL 的 **resultType** **的类型**相同。

> Mapper 接口开发方法只需要程序员编写 Mapper 接口（相当于 Dao 接口），由 MyBatis 框架根据接口定义创建接口的动态代理对象，代理对象的方法体同上边 Dao 接口实现类方法。
>



#### 8.2.2 代码实现

**（1）编写UserMapper接口**

```java
public interface UserMapper {
    //根据id查询用户
    public User findUserById(int id);
}
```

**（2）编写UserMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.gvc.mapper.UserMapper">
    <!--根据id查询用户-->
    <select id="findUserById" parameterType="int" resultType="user">
        select * from user where id = #{id}
    </select>
</mapper>
```

**（3）测试**

```java
@Test
//Mapper代理方式测试方法
public void mybatisTestQuickStart() throws IOException {

    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    //当前返回的是基于UserMapper产生的代理对象，底层为JDK动态代理
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = mapper.findUserById(1);
    System.out.println(user);
    
    sqlSession.close();
}
```

#### **8.2.3 Mybatis基于接口代理方式的内部执行原理**

我们的持久层现在只有一个接口，而接口是不实际干活的，那么是谁在做查询的实际工作呢？ 

**下面通过追踪源码看一下：** 

1、通过追踪源码我们会发现，我们使用的mapper实际上是一个代理对象,是由**MapperProxy**代理产生的。

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\26a357bfb1704e109577925b92ed1816\clipboard.png)

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\78425a2df71343d1a3f97e362ba313a9\clipboard.png)

2、追踪MapperProxy的invoke方法会发现，其最终调用了mapperMethod.execute(sqlSession, args)

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\acaf107415ea4431a03ee91d7eb1a204\clipboard.png)

3、进入execute方法会发现，最终工作的还是sqlSession

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\431f32777a714f29ba8779888d8a8057\clipboard.png)