[TOC]





## 一、Mybatis高级查询

### 1.1 ResutlMap属性

 建立对象关系映射

![img](https://github.com/gvc10233/note_images/blob/main/mybatis_img/mybatis0201.png)

**resultType** 如果实体的属性名表中字段名一致，将查询结果自动封装到实体类中

![img](https://github.com/gvc10233/note_images/blob/main/mybatis_img/mybatis0202.png)

**resutlMap** 如果实体的属性名与表中字段名不一致，可以使用ResutlMap实现手动封装到实体类中

**（1）编写UserMapper接口**

```java
public interface UserMapper {    
    //查询所有用户    
    public List<User> findAllResultMap(); 
}
```

**（2）编写UserMapper.xml**

```xml
<!-- id: 标签的唯一标识 ; type: 封装后的实体类型 -->
<resultMap id="userResultMap" type="com.gvc.domain.User">

    <!-- 手动配置映射关系 -->
    <!-- 子标签id: 用来配置主键 ；property对应属性，column对应字段-->
    <id property="idabc" column="id"></id>
    
    <!-- 子标签result: 表中普通字段的封装 -->
    <result property="usernameabc" column="username"></result>
    <result property="birthdayabc" column="birthday"></result>
    <result property="sexabc" column="sex"></result>
    <result property="addressabc" column="address"></result>
</resultMap>

<!-- 查询所有用户 -->
<!-- resultMap: 手动配置实体属性与表中字段的映射关系，完成手动封装 -->
<select id="findAllResultMap" resultMap="userResultMap">
    select * from user
</select>
```

**（3）编写测试方法**

```java
@Test
public void testFindAllResultMap() throws IOException {

    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();
    
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> allResultMap = mapper.findAllResultMap();
    for (User user : allResultMap) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



### 1.2 多条件查询（三种） 

**需求:**  根据id和username查询user表

**（1）方式一  #{arg0}-#{argn} 或 #{param1}-#{paramn}**

使用 #{arg0}-#{argn} 或者 #{param1}-#{paramn} 获取参数

**UserMapper接口**

```java
public interface UserMapper {
    //进行多条件查询方式一
    public List<User> findByIdAndUsername1(int id, String username);
}
```

**UserMapper.xml**

```xml
<!-- 多条件查询：方式一 -->
<select id="findByIdAndUsername1" resultMap="userResultMap">
    select * from user where id = #{arg0} and username = #{arg1}
</select>
```

**测试**

```java
@Test
//多条件查询方式一
public void testfindByIdAndUsername1() throws IOException {

    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = mapper.findByIdAndUsername1(1,"嵇围城");
    System.out.println(user);

    sqlSession.close();
}
```



**（2）方式二  使用注解，引入 @Param() 注解获取参数**

**UserMapper接口**

```java
public interface UserMapper {
    //进行多条件查询方式二
    public User findByIdAndUsername2(@Param("id") int id, @Param("username") String username);
}
```

**UserMapper.xml**

```xml
<!-- 多条件查询：方式二  #{}内为注解里的值 -->
<select id="findByIdAndUsername2" resultMap="userResultMap">
    select * from user where id = #{id} and username = #{username}
</select>
```

**测试**

```java
@Test
//多条件查询方式二
public void testfindByIdAndUsername2() throws IOException {

    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();
    
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = mapper.findByIdAndUsername2(1,"嵇围城");
    System.out.println(user);

    sqlSession.close();
}
```



**（3）方式三（推荐）使用pojo对象传递参数**

使用pojo对象传递参数

**UserMapper接口**

```java
public interface UserMapper {
    //进行多条件查询方式三
    public User findByIdAndUsername3(User user);
}
```

**UserMapper.xml**

```xml
<!-- 多条件查询：方式三  #{}内为 User实体内get方法后的首字母小写属性名 -->
<select id="findByIdAndUsername3" resultMap="userResultMap" parameterType="com.gvc.domain.User">
    select * from user where id = #{idabc} and username = #{usernameabc}
</select>
```

**测试**

```java
@Test
//多条件查询方式二
public void testfindByIdAndUsername3() throws IOException {

    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user1 = new User();
    user1.setIdabc(1);
    user1.setUsernameabc("嵇围城");

    User user = mapper.findByIdAndUsername3(user1);
    System.out.println(user);

    sqlSession.close();
}
```



### 1.3 模糊查询

**需求：**根据username模糊查询user表

**（1）方式一**

**UserMapper接口**

```java
public interface UserMapper {
    //模糊查询：方式一
    public List<User> findByUsername1(String username);
}
```

**UserMapper.xml**

```xml
<!-- 模糊查询：方式一 -->
<select id="findByUsername1" resultMap="userResultMap" parameterType="string">
    select * from user where username like #{username}
</select>
```

**测试**

```java
@Test
//模糊查询：方式一
public void testfindByUsername1() throws IOException {

    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> userList = mapper.findByUsername1("%嵇%");
    for (User user : userList) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



**（2）方式二**

**UserMapper接口**

```java
public interface UserMapper {
    //模糊查询：方式二
    public List<User> findByUsername2(String username);
}
```

**UserMapper.xml**

```xml
<!-- 模糊查询：方式二 -->
<select id="findByUsername2" resultMap="userResultMap" parameterType="string">
    <!-- parameterType是基本数据类型或 String时 ${}里面的值只能写value，${}是sql原样拼接，无单引号-->
    <!--不推荐使用，因为会出现sql注入问题-->
    select * from user where username like '${vlaue}'
</select>
```

**测试**

```java
@Test
//模糊查询：方式二
public void testfindByUsername2() throws IOException {

    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> userList = mapper.findByUsername2("%嵇%");
    for (User user : userList) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



**（3）#{} 与 ${}   区别 【笔试题】**

 **#{}** : 表示一个占位符号 

1. 通过 #{} 可以实现preparedStatement向占位符中设置值，自动进行java类型和jdbc类型转换，#{} 可以有效防止sql注入。
2. #{} 可以接收简单类型值或pojo属性值。
3. 如果parameterType传输单个简单类型值， #{} 括号中名称随便写。

**${}** : 表示拼接sql串 

1. 通过 ${} 可以将parameterType 传入的内容拼接在sql中且不进行jdbc类型转换，会出现 sql注入 问题。
2. ${} 可以接收简单类型值或pojo属性值。
3. 如果parameterType传输单个简单类型值， ${} 括号中只能是value。 

补充：TextSqlNode.java 源码可以证明



## 二 、Mybatis映射文件深入

### 2.1 返回主键 

**应用场景 ：**我们很多时候有这种需求，向数据库插入一条记录后，希望能立即拿到这条记录在数据库中的主键值。 

#### 2.1.1 useGeneratedKeys

**UserMapper接口**

 ```java
  public interface UserMapper {
     //添加用户，获取返回主键：方式一
     public void saveUser1(User user);
 }
 ```

**UserMapper.xml**

```xml
<!-- 添加用户，获取返回主键：方式一 -->
<!-- useGeneratedKeys: 声明返回主键；
     keyProperty: 返回主键的值封装到实体中的哪个属性上 -->
<insert id="saveUser1" parameterType="user" useGeneratedKeys="true" keyProperty="idabc">
    insert into user(username,birthday,sex,address) values (#{usernameabc},#{birthdayabc},#{sexabc},#{addressabc})
</insert>
```

**测试**

```java
@Test
//添加用户，返回主键：方式一
public void testSaveUser1() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();
    
    User user = new User();
    user.setUsernameabc("宫水三叶");
    user.setBirthdayabc(new Date());
    user.setSexabc("女");
    user.setAddressabc("东京");

    System.out.println(user);
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    mapper.saveUser1(user);
    System.out.println(user);

    sqlSession.commit();
    sqlSession.close();
}
```

**注意：**只适用于**主键自增**的数据库，**mysql和sqlserver支持**，**oracle不行**。



#### 2.1.2 标签<selectKey>

**UserMapper接口**

```java
public interface UserMapper {    
    //添加用户，获取返回主键：方式二
    public void saveUser2(User user);
}
```

**UserMapper.xml**

![img](https://github.com/gvc10233/note_images/blob/main/mybatis_img/mybatis0203.png)

```xml
<!-- 添加用户，获取返回主键：方式二 -->
<insert id="saveUser2" parameterType="user">
    <!-- selectKey标签：适用范围广，支持所有类型数据库
         order="AFTER": 表示这句话在Insert执行后再执行此语句
         keyColumn="id": 指定主键对应的列名
         keyProperty="idabc"：返回主键的值封装到实体中的哪个属性上
         resultType="int"：指定主键类型-->
    <selectKey order="AFTER" keyColumn="id" keyProperty="idabc" resultType="int">
        select LAST_INSERT_ID()
    </selectKey>
    insert into user(username,birthday,sex,address) values (#{usernameabc},#{birthdayabc},#{sexabc},#{addressabc})
</insert>
```

**测试**

```java
@Test
//添加用户，返回主键：方式二
public void testSaveUser2() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    User user = new User();
    user.setUsernameabc("汤唯");
    user.setBirthdayabc(new Date());
    user.setSexabc("女");
    user.setAddressabc("浙江杭州");

    System.out.println(user);
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    mapper.saveUser2(user);
    System.out.println(user);

    sqlSession.commit();
    sqlSession.close();
}
```



### 2.2 动态SQL 

**应用场景:** 当我们要根据不同的条件，来执行不同的sql语句的时候，需要用到动态sql。

![img](https://github.com/gvc10233/note_images/blob/main/mybatis_img/mybatis0204.png)

#### 2.2.1 动态 SQL 之 标签 if

**需求:** 根据id和username查询，但是不确定两个都有值。

**UserMapper接口** 

```java
//动态sql的 <if>标签：多条件查询
public List<User> findByIdAndUsernameIf(User user);
```

**UserMapper.xml**

```xml
<!-- 动态sql之<if>:多条件查询 -->
<select id="findByIdAndUsernameIf" parameterType="user" resultMap="userResultMap">
    select * from user
     <!-- <where>标签：相当于 where 1=1,但如果没有条件，不会拼接上where关键字-->
     <where>
         <!-- test里面写的就是表达式 -->
        <if test="idabc != null">
            AND id = #{idabc}
        </if>
        <if test="usernameabc != null">
            AND username = #{usernameabc}
        </if>
     </where>
</select>
```

**测试**

```java
@Test
//动态sql的 <if>标签：多条件查询
public void testfindByIdandUsernameIf() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = new User();
    user.setIdabc(10);
    user.setUsernameabc("汤唯");

    List<User> userList = mapper.findByIdAndUsernameIf(user);
    for (User user1 : userList) {
        System.out.println(user1);
    }

    sqlSession.close();
}
```



#### 2.2.2 动态 SQL 之 标签 set

**需求:** 动态更新user表数据，如果该属性有值就更新，没有值不做处理。

**UserMapper接口** 

```java
public interface UserMapper {
    //动态sql的 <set>标签：动态更新
    public void updateIf(User user);
}
```

**UserMapper.xml**

```xml
<!-- 动态sql之set: 动态更新 -->
<update id="updateIf" parameterType="user">
    update user
    <!-- <set>标签：在更新的时候，会自动添加set关键字，且去掉最后一个条件的逗号 -->
    <set>
        <if test="usernameabc != null">
            username = #{usernameabc},
        </if>
        <if test="birthdayabc != null">
            birthday = #{birthdayabc},
        </if>
        <if test="sexabc != null">
            sex = #{sexabc},
        </if>
        <if test="addressabc != null">
            address = #{addressabc},
        </if>

    </set>
        where id = #{idabc}
</update>
```

**测试**

```java
@Test
//动态sql的 <set>标签：动态更新
public void testUpdateIf() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = new User();
    user.setUsernameabc("Khalil");
    user.setAddressabc("夏威夷");
    user.setIdabc(11);
    mapper.updateIf(user);

    sqlSession.commit();
    sqlSession.close();
}
```



#### 2.2.3 动态 SQL 之 标签 foreach

**foreach主要是用来做数据的循环遍历** 

例如： select * from user where id in (1,2,3) 在这样的语句中，传入的参数部分必须依靠 foreach遍历才能实现。

**<foreach>** 标签用于遍历集合，它的属性：

> **collection**：代表要遍历的集合元素。
>
> **open**：代表语句的开始部分。
>
> **close**：代表结束部分。
>
> **item**：代表遍历集合的每个元素，生成的变量名。
>
> **separator**：代表分隔符。

**（1）集合**

**UserMapper接口** 

```java
public interface UserMapper {
    //动态sql的 <foreach>标签：多值查询
    public List<User> findByList(List<Integer> userIdList);
}
```

**UserMapper.xml**

```xml
<!-- 动态sql之<foreach>标签：根据多个id值查询用户 -->
<select id="findByList" parameterType="list" resultMap="userResultMap">
    select * from user
    <where>
        <!-- collection: 代表要遍历的集合元素，当传递数据类型为基本数据类型或String时，通常写collection或list
             open: 语句的开始部分
             close: 语句的结束部分
             item: 遍历集合中的每个元素，生成的变量名
             separator: 分隔符
         -->
        <foreach collection="collection" open="id in (" close=")" item="idabc" separator=",">
            #{idabc}
        </foreach>
    </where>
</select>
```

**测试**

```java
@Test
//动态sql的 <foreach>标签：多值查询
public void testFindByList() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<Integer> idList = new ArrayList<>();
    idList.add(1);
    idList.add(2);
    idList.add(7);
    idList.add(9);
    idList.add(11);
    List<User> userList = mapper.findByList(idList);
    for (User user : userList) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



**（2）数组**

**UserMapper接口** 

```java
public interface UserMapper {
    //动态sql的 <foreach>标签：多值查询 (数组)
    public List<User> findByArray(Integer[] idArray);
}
```

**UserMapper.xml**

```xml
<!-- 动态sql之<foreach>标签：根据多个id值查询用户 -->
<select id="findByArray" parameterType="int" resultMap="userResultMap">
    select * from user
    <where>
        <!-- collection: 代表要遍历的集合元素，当传递数据类型为基本数据类型或String时，通常写collection或list
             open: 语句的开始部分
             close: 语句的结束部分
             item: 遍历集合中的每个元素，生成的变量名
             separator: 分隔符
         -->
        <foreach collection="array" open="id in (" close=")" item="idabc" separator=",">
            #{idabc}
        </foreach>
    </where>
</select>
```

**测试**

```java
@Test
//动态sql的 <foreach>标签：多值查询 - 数组
public void testFindByAraay() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    Integer[] idArray = {5,7,9,10,11} ;

    List<User> userList = mapper.findByArray(idArray);
    for (User user : userList) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



### 2.3 SQL片段

**应用场景：** 映射文件中可将重复的 sql 提取出来，使用时用 include 引用即可，最终达到 sql 重用的目的。

```xml
<!--抽取的sql片段-->
<sql id="selectUser">
    select * from user
</sql>

<!-- 动态sql之<foreach>标签：根据多个id值查询用户 -->
<select id="findByList" parameterType="list" resultMap="userResultMap">
    <!--引用的sql片段-->
    <include refid="selectUser"/>
    <where>
        <!-- collection: 代表要遍历的集合元素，当传递数据类型为基本数据类型或String时，通常写collection或list
             open: 语句的开始部分
             close: 语句的结束部分
             item: 遍历集合中的每个元素，生成的变量名
             separator: 分隔符
         -->
        <foreach collection="collection" open="id in (" close=")" item="idabc" separator=",">
            #{idabc}
        </foreach>
    </where>
</select>

<!-- 动态sql之<foreach>标签：根据多个id值查询用户 数组 -->
<select id="findByArray" parameterType="int" resultMap="userResultMap">
    <!--引用的sql片段-->
    <include refid="selectUser"/>
    <where>
        <!-- collection: 代表要遍历的集合元素，当传递数据类型为基本数据类型或String时，通常写collection或list
             open: 语句的开始部分
             close: 语句的结束部分
             item: 遍历集合中的每个元素，生成的变量名
             separator: 分隔符
         -->
        <foreach collection="array" open="id in (" close=")" item="idabc" separator=",">
            #{idabc}
        </foreach>
    </where>
</select>
```



### 2.4 知识小结

MyBatis映射文件配置

select：查询

insert：插入

update：修改

delete：删除

selectKey：返回主键

where：where条件

if：if判断

foreach：for循环

set：set设置

sql：sql片段抽取

------





## 三、Mybatis核心配置文件深入

### 3.1 plugins 标签

MyBatis 可以使用第三方的插件来对功能进行扩展，分页助手 PageHelper 将分页的复杂操作进行封装，使用简单的方式即可获得分页的相关数据。

**开发步骤**：

① 导入通用 PageHelper 的坐标。

② 在 MyBatis 核心配置文件中配置 PageHelper 插件。

③ 测试分页数据获取。



**① 导入通用 PageHelper 坐标**

```xml
<!-- 分页助手 -->
<dependency>
  <groupId>com.github.pagehelper</groupId>
  <artifactId>pagehelper</artifactId>
  <version>3.7.5</version>
</dependency>
<dependency>
  <groupId>com.github.jsqlparser</groupId>
  <artifactId>jsqlparser</artifactId>
  <version>0.9.1</version>
</dependency>
```

**② 在 MyBatis 核心配置文件中配置 PageHelper 插件**

```xml
<plugins>
    <plugin interceptor="com.github.pagehelper.PageHelper">
        <!-- dialect: 指定方言 “limit” -->
        <property name="dialect" value="mysql"/>
    </plugin>
</plugins>
```

**③ 测试分页数据获取**

```java
@Test
//核心配置文件深入：<plugin>标签 ：pageHelper
public void testPageHelper() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);

    //设置分页参数：当前页，显示条数
    PageHelper.startPage(1, 5);

    List<User> userList = mapper.findAllResultMap();
    for (User user : userList) {
        System.out.println(user);
    }
```

获得分页相关的其他参数

```java
//获取分页相关的其它参数
    PageInfo<User> userPageInfo = new PageInfo<User>(userList);
    System.out.println("总条数：" + userPageInfo.getTotal());
    System.out.println("总页数：" + userPageInfo.getPages());
    System.out.println("当前页：" + userPageInfo.getPageNum());
    System.out.println("每页显示长度：" + userPageInfo.getPageSize());
    System.out.println("是否第一页：" + userPageInfo.isIsFirstPage());
    System.out.println("是否最后一页：" + userPageInfo.isIsLastPage());

    sqlSession.close();
}
```



### 3.2 知识小结                                      

MyBatis核心配置文件常用标签：

> 1. properties标签：该标签可以加载外部的properties文件
> 2. typeAliases标签：设置类型别名
> 3. environments标签：数据源环境配置标签
> 4. plugins标签：配置MyBatis的插件
>





## 四、MyBatis 多表查询

### 4.1 数据库表关系介绍

关系型数据库表关系分为：

- 一对一
- 一对多
- 多对多

**举例**：

**人和身份证号**就是一对一：

一个人只能有一个身份证号，一个身份证号只能属于一个人。

**用户和订单**就是一对多，**订单和用户**就是多对一：

一个用户可以下多个订单，多个订单属于同一个用户。

**学生和课程**就是多对多：

一个学生可以选择多门课程，一个课程可以接受多个学生选择。

**特例**：

一个订单只从属于一个用户，所以 MyBatis 将多对一看成了一对一。

**案例环境准备**

```sql
DROP TABLE IF EXISTS `orders`;
 CREATE TABLE `orders` (
 `id` INT(11) NOT NULL AUTO_INCREMENT,
 `ordertime` VARCHAR(255) DEFAULT NULL,
 `total` DOUBLE DEFAULT NULL,
 `uid` INT(11) DEFAULT NULL,
 PRIMARY KEY (`id`),
 KEY `uid` (`uid`),
 CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`id`)
 ) ENGINE=INNODB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;-- ------------------------------ Records of orders-- ----------------------------
 INSERT INTO `orders` VALUES ('1', '2020-12-12', '3000', '1');
 INSERT INTO `orders` VALUES ('2', '2020-12-12', '4000', '1');
 INSERT INTO `orders` VALUES ('3', '2020-12-12', '5000', '2');-- ------------------------------ Table structure for sys_role-- ----------------------------
 DROP TABLE IF EXISTS `sys_role`;
 CREATE TABLE `sys_role` (
 `id` INT(11) NOT NULL AUTO_INCREMENT,
 `rolename` VARCHAR(255) DEFAULT NULL,
 `roleDesc` VARCHAR(255) DEFAULT NULL,
 PRIMARY KEY (`id`)
 ) ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;-- ------------------------------ Records of sys_role-- ----------------------------
 INSERT INTO `sys_role` VALUES ('1', 'CTO', 'CTO');
 INSERT INTO `sys_role` VALUES ('2', 'CEO', 'CEO');-- ----------------------------
-- Table structure for sys_user_role-- ----------------------------
 DROP TABLE IF EXISTS `sys_user_role`;
 CREATE TABLE `sys_user_role` (
 `userid` INT(11) NOT NULL,
 `roleid` INT(11) NOT NULL,
 PRIMARY KEY (`userid`,`roleid`),
 KEY `roleid` (`roleid`),
 CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `sys_role` 
(`id`),
 CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`roleid`) REFERENCES `user` 
(`id`)
 ) ENGINE=INNODB DEFAULT CHARSET=utf8;-- ------------------------------ Records of sys_user_role-- ----------------------------
 INSERT INTO `sys_user_role` VALUES ('1', '1');
 INSERT INTO `sys_user_role` VALUES ('2', '1');
 INSERT INTO `sys_user_role` VALUES ('1', '2');
 INSERT INTO `sys_user_role` VALUES ('2', '2');
```



### 4.2 一对一（多对一）

#### 4.2.1 介绍

**一对一查询模型**

用户表和订单表的关系为，一个用户有多个订单，一个订单只从属于一个用户。

**一对一查询的需求**：查询所有订单，与此同时查询出每个订单所属的用户。

![img](https://github.com/gvc10233/note_images/blob/main/mybatis_img/mybatis0205.png)

**一对一查询语句**

-- 查询所有订单，与此同时查询出每个订单所属的用户 SELECT * FROM orders o LEFT JOIN user u ON o.uid = u.id

 

#### 4.2.2 代码实现

![img](https://github.com/gvc10233/note_images/blob/main/mybatis_img/mybatis0206.png)

**（1）Order实体**

```java
public class Orders {
    private Integer id;
    private String ordertime;
    private Double total;
    private Integer uid;

    //表示当前订单属于哪个用户
    private User user;
}
```

**（2）OrderMapper接口**

```java
public interface OrderMapper {
    //一对一关联查询： 查询所有订单，同时查出每个订单所属的用户信息
    public List<Orders> findAllWithUser();
}
```

**（3）OrderMapper.xml映射**

```xml
<mapper namespace="com.gvc.mapper.OrderMapper">

    <resultMap id="orderMap" type="com.gvc.domain.Orders">
        <id property="id" column="id"/>
        <result property="ordertime" column="ordertime"/>
        <result property="total" column="total"/>
        <result property="uid" column="uid"/>

        <!-- <association>标签：在进行一对一关联查询配置时，使用该标签进行关联
              property="user"：要封装实体的属性名
              javaType="com.gvc.domain.User"：要封装实体的属性类型
              -->
        <association property="user" javaType="com.gvc.domain.User">
            <id property="id" column="uid"/>
            <result property="username" column="username" />
            <result property="birthday" column="brithday" />
            <result property="sex" column="sex" />
            <result property="address" column="address" />
        </association>
    </resultMap>

    <!-- 一对一关联查询： 查询所有订单，同时查询每个订单所属的用户信息 -->
    <select id="findAllWithUser" resultMap="orderMap">
        select * from orders o LEFT JOIN user u ON o.uid = u.id
    </select>

</mapper>
```

**（4）测试代码**

```java
//一对一关联查询： 查询所有订单，同时查询每个订单所属的用户信息
@Test
public void testFindAllWithUser() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    OrderMapper mapper = sqlSession.getMapper(OrderMapper.class);
    List<Orders> orders = mapper.findAllWithUser();
    for (Orders order : orders) {
        System.out.println(order);
    }

    sqlSession.close();
}
```



### 4.3 一对多

#### 4.3.1 介绍

**一对多查询模型**

用户表和订单表的关系为，一个用户有多个订单，一个订单只从属于一个用户。

**一对多查询的需求**：查询所有用户，与此同时查询出该用户具有的订单。

![img](https://github.com/gvc10233/note_images/blob/main/mybatis_img/mybatis0207.png)

**一对多查询语句**

```sql
-- 一对多查询的需求：查询所有用户，与此同时查询出该用户具有的订单
SELECT *,o.id oid FROM USER u LEFT JOIN orders o ON u.id = o.uid
```



#### 4.3.2 代码实现 

**（1）User实体**

```java
public class User {
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;

    // 表示user有多个Orders,使用集合.代表当前用户的订单列表
    private List<Orders> ordersList;
}
```

**（2）UserMapper接口**

```java
public interface UserMapper {
    // 一对多查询：查询所有用户，与此同时查询出该用户具有的订单
    public List<User> findAllWithOrders();
}
```

**（3）UserMapper.xml映射**

```xml
<mapper namespace="com.gvc.mapper.UserMapper">

    <resultMap id="userMap" type="com.gvc.domain.User">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="birthday" column="birthday"/>
        <result property="sex" column="sex"/>
        <result property="address" column="address"/>

        <!-- 一对多使用<collection>标签进行关联 -->
        <collection property="ordersList" ofType="Orders">
            <id property="id" column="oid"/>
            <result property="ordertime" column="ordertime"/>
            <result property="total" column="total"/>
            <result property="uid" column="uid"/>
        </collection>
        
    </resultMap>

    <!-- 一对一关联查询： 查询所有订单，同时查出每个订单所属的用户信息-->
    <select id="findAllWithOrders" resultMap="userMap">
        SELECT *,o.id oid FROM USER u LEFT JOIN orders o ON u.id = o.uid
    </select>
</mapper>
```

**（4）测试代码**

```java
//一对多关联查询： 查询所有用户，同时查询每个用户关联的订单信息
@Test
public void testFindAllWithOrders() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> userList = mapper.findAllWithOrders();
    for (User user : userList) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



### 4.4 多对多

#### 4.4.1 介绍

**多对多查询的模型**

用户表和角色表的关系为，一个用户有多个角色，一个角色被多个用户使用。

**多对多查询的需求**： 

![img](https://github.com/gvc10233/note_images/blob/main/mybatis_img/mybatis0208.png)

**多对多查询语句**

```sql
SELECT u.*, r.id rid,r.rolename,r.roleDesc FROM `user` u
LEFT JOIN sys_user_role ur ON u.id = ur.userid -- 左外连接中间表
LEFT JOIN sys_role r ON ur.roleid = r.id
```



#### 4.4.2 代码实现 

**（1）User和Role实体**

```java
public class User {
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;

    // 表示user有多个Orders,使用集合.代表当前用户的订单列表
    private List<Orders> ordersList;
    //代表当前用户的角色列表
    private List<Role> roleList;
}

public class Role {
    private Integer id;
    private String rolename;
    private String roleDesc;
}
```

**（2）UserMapper接口**

```java
public interface UserMapper {
    // 多对多查询：查询所有用户，同时查询出该用户的所有角色
    public List<User> findAllWithRoles();
}
```

**（3）UserMapper.xml映射**

```xml
<!-- 多对多查询：查询所有用户，同时查询出该用户的所有角色 -->
<resultMap id="userRoleMap" type="com.gvc.domain.User">
    <id property="id" column="id"/>
    <result property="username" column="username"/>
    <result property="birthday" column="birthday"/>
    <result property="sex" column="sex"/>
    <result property="address" column="address"/>

    <collection property="roleList" ofType="com.gvc.domain.Role">
        <id property="id" column="rid"/>
        <result property="rolename" column="rolename"/>
        <result property="roleDesc" column="roleDesc"/>
    </collection>

</resultMap>

<select id="findAllWithRoles" resultMap="userRoleMap">
    SELECT u.*, r.id rid,r.rolename,r.roleDesc FROM `user` u
        LEFT JOIN sys_user_role ur ON u.id = ur.userid -- 左外连接中间表
        LEFT JOIN sys_role r ON ur.roleid = r.id
</select>
```

**（4）测试代码**

```java
//多对多关联查询：查询所有用户，同时查询出用户关联的的角色信息
@Test
public void testFindAllWithRole() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> userList = mapper.findAllWithRoles();
    for (User user : userList) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



### 4.5 小结

**MyBatis多表配置方式**

- 多对一（一对一）配置：使用 <resultMap> + <association> 做配置。
- 一对多配置：使用 <resultMap> + <collection> 做配置。
- 多对多配置：使用 <resultMap> + <collection> 做配置。

多对多的配置跟一对多很相似，难度在于 SQL 语句的编写。





## 五、MyBatis 嵌套查询

### 5.1 什么是嵌套查询

嵌套查询就是将原来多表查询中的联合查询语句拆成单个表的查询，再使用 MyBatis 的语法嵌套在一起。

**举个栗子**

**需求**：查询一个订单，与此同时查询出该订单所属的用户。

**1. 联合查询**

```sql
SELECT * FROM orders o LEFT JOIN user u ON o.uid = u.id 
```

**2. 嵌套查询**

2.1 先查询订单

```sql
SELECT * FROM orders
```

2.2 再根据订单 uid 外键，查询用户

```sql
SELECT * FROM user WHERE id = #{根据订单查询的uid} 
```

2.3 最后使用 MyBatis，将以上两步嵌套起来。

```xml
...
```



### 5.2 一对一嵌套查询

#### 5.2.1 介绍

**需求**：查询一个订单，与此同时查询出该订单所属的用户。

**一对一查询语句**

```sql
-- 先查询订单
select * from orders
-- 再根据订单 uid 外键，查询用户
select * from user where id = #{id}
```



#### 5.2.2 代码实现

**（1）OrderMapper 接口**

````java
public interface OrderMapper {
    //一对一嵌套查询： 查询所有订单，同时查出每个订单所属的用户信息
    public List<Orders> findAllWithUser2();
}
````

**（2）OrderMapper.xml映射**

```xml
<resultMap id="orderMap2" type="com.gvc.domain.Orders">
    <id property="id" column="id"/>
    <result property="ordertime" column="ordertime"/>
    <result property="total" column="total"/>
    <result property="uid" column="uid"/>

    <!-- 问题：1.怎么去执行第二条sql；2. 执行第二条sql后，怎么传递 uid参数 -->
    <association property="user" javaType="com.gvc.domain.User" select="com.gvc.mapper.UserMapper.findById" column="uid"/>

</resultMap>

<!-- 一对一嵌套查询： 查询所有订单，同时查询每个订单所属的用户信息 -->
<select id="findAllWithUser2" resultMap="orderMap2">
    select * from orders
</select>
```

**（3）UserMapper接口**

```java
public interface UserMapper {
    //根据id查询用户
    public User findById(Integer id);
}
```

**（4）UserMapper.xml映射**

```xml
<!-- 根据id查询用户 -->
<select id="findById" resultType="com.gvc.domain.User" parameterType="int">
    select * from user where id = #{id}
</select>
```

**（5）测试代码**

```java
//一对一嵌套查询： 查询所有订单，同时查出每个订单所属的用户信息
@Test
public void testFindAllWithUser2() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    OrderMapper mapper = sqlSession.getMapper(OrderMapper.class);
    List<Orders> ordersList2 = mapper.findAllWithUser2();

    for (Orders orders : ordersList2) {
        System.out.println(orders);
    }

    sqlSession.close();
}
```

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\ad686b4a02754dcaacdecea7da359d4a\2timesselect.png)



### 5.3 一对多嵌套查询

#### 5.3.1 介绍

**需求**：查询所有用户，与此同时查询出该用户具有的订单

**一对多查询语句**

```sql
-- 先查询用户
SELECT * FROM user 
-- 再根据用户id主键，查询订单列表
SELECT * FROM orders WHERE uid = #{uid}
```

**（1）UserMapper 接口**

```java
public interface UserMapper {
    //一对多嵌套查询：查询所有用户，与此同时查询出该用户具有的订单
    public List<User> findAllWithOrders2();
}
```

**（2）UserMapper.xml映射**

```xml
<resultMap id="userOrderMap" type="com.gvc.domain.User">
    <id property="id" column="id"/>
    <result property="username" column="username"/>
    <result property="birthday" column="birthday"/>
    <result property="sex" column="sex"/>
    <result property="address" column="address"/>

    <collection property="ordersList" ofType="com.gvc.domain.Orders" select="com.gvc.mapper.OrderMapper.findByUid" column="id"/>

</resultMap>

<!-- 一对多嵌套查询：查询所有用户，与此同时查询出该用户具有的订单 -->
<select id="findAllWithOrders2" resultMap="userOrderMap">
    select * from user
</select>
```

**（3）OrderMapper接口**

```java
public interface OrderMapper {
    // 根据uid查询对应订单
    public List<Orders> findByUid(Integer uid);
}
```

**（4）OrderMapper.xml映射**

```xml
<!-- 根据uid查询对应订单 -->
<select id="findByUid" parameterType="int" resultType="com.gvc.domain.Orders">
    select * from orders where uid = #{uid}
</select>
```

**（5）测试代码**

```java
//一对多嵌套查询：查询所有用户，与此同时查询出该用户具有的订单
@Test
public void testFindAllWithOrders2() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> userList2 = mapper.findAllWithOrders2();
    for (User user : userList2) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



### 5.4 多对多嵌套查询

#### 5.4.1 介绍

**需求**：查询用户, 同时查询出该用户的所有角色

**多对多查询语句**

```sql
-- 先查询用户
SELECT * FROM user
-- 再根据用户id主键，查询角色列表
SELECT * FROM sys_role r INNER JOIN sys_user_role ur ON ur.roleid = r.id
    WHERE ur.userid = #{id}
```



#### 5.4.2 代码实现

**（1）UserMapper 接口**

```java
public interface UserMapper {
    //多对多嵌套查询：查询用户, 同时查询出该用户的所有角色
    public List<User> findAllWithRoles2();
}
```

**（2）UserMapper.xml映射**

```xml
<resultMap id="userRoleMap2" type="com.gvc.domain.User">
    <id property="id" column="id"/>
    <result property="username" column="username"/>
    <result property="birthday" column="birthday"/>
    <result property="sex" column="sex"/>
    <result property="address" column="address"/>

    <collection property="roleList" ofType="com.gvc.domain.Role" select="com.gvc.mapper.RoleMapper.findByUid" column="id"/>
</resultMap>

<!-- 多对多嵌套查询：查询用户, 同时查询出该用户的所有角色 -->
<select id="findAllWithRoles2" resultMap="userRoleMap2">
    select * from user
</select>
```

**（3）RoleMapper接口**

```java
public interface RoleMapper {
    //根据用户id查询对应角色
    public List<Role> findByUid(Integer uid);
}
```

**（4）RoleMapper.xml映射**

```xml
<mapper namespace="com.gvc.mapper.RoleMapper">
    <select id="findByUid" resultType="com.gvc.domain.Role" parameterType="int">
        select * from sys_role r INNER JOIN sys_user_role ur ON r.id = ur.roleid
            where ur.userid = #{uid}
    </select>
</mapper>
```

**（5）测试代码**

```java
//多对多嵌套查询：查询用户, 同时查询出该用户的所有角色
@Test
public void testFindAllWithRoles2() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    SqlSession sqlSession = sqlSessionFactory.openSession();

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> rolesList2 = mapper.findAllWithRoles2();
    for (User user : rolesList2) {
        System.out.println(user);
    }

    sqlSession.close();
}
```



### 5.5 小结

> **一对一配置**：使用 <resultMap> + <association> 做配置，通过 column 条件，执行 select 查询。
>
> **一对多配置**：使用 <resultMap> + <collection> 做配置，通过 column 条件，执行 select 查询。
>
> **多对多配置**：使用 <resultMap> + <collection> 做配置，通过 column 条件，执行 select 查询。
>
> **优点**：简化多表查询操作。
>
> **缺点**：执行多次 SQL 语句，浪费数据库性能。
