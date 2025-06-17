[TOC]

## 一、MyBatis 加载策略

### 1.1 什么是延迟加载？

**问题**

通过前面的学习，我们已经掌握了 MyBatis 中一对一、一对多、多对多关系的配置及实现，可以实现对象的关联查询。实际开发过程中，很多时候我们并不需要总是在加载用户信息时就一定要加载他的订单信息。此时就是我们所说的延迟加载。

**举个栗子**

> 在一对多中，当我们有一个用户，它有 100 个订单：
>
> 在查询用户的时候，要不要把关联的订单查出来？
>
> 在查询订单的时候，要不要把关联的用户查出来？

**回答：**

> 在查询用户时，用户下的订单应该是，什么时候用，什么时候查询。
>
> 在查询订单时，订单所属的用户信息应该是随着订单一起查询出来。

**延迟加载**

就是在需要用到数据时才进行加载，不需要用到数据时就不加载数据。延迟加载也称懒加载。

> **优点**：
>
> 先从单表查询，需要时再从关联表去关联查询，大大提高数据库性能，因为查询单表要比关联查询多张表速度更快。
>
> **缺点**：
>
> 因为只有当需要用到数据时，才会进行数据库查询，这样在大批量数据查询时，因为查询工作也要消耗时间，所以可能造成用户等待时间变长，造成用户体验下降。

> **在多表中**：
>
> 一对多、多对多：通常情况下采用延迟加载。
>
> 一对一（多对一）：通常情况下采用立即加载。
>
> **注意**：
>
> 延迟加载是基于嵌套查询来实现的。



### 1.2 实现

#### 1.2.1 局部延迟加载

在 ```<association>```和 ```<collection>``` 标签中都有一个 fetchType 属性，通过修改它的值，可以修改局部的加载策略。

```xml
<!-- fetchType="lazy"：延迟加载
     fetchType="eager"：立即加载
 -->
<collection property="ordersList" ofType="com.gvc.domain.Orders" select="com.gvc.mapper.OrderMapper.findByUid" column="id"
            fetchType="lazy"/>
```



#### 1.2.2 设置触发延迟加载的方法

在配置了延迟加载策略后，即使没有调用关联对象的任何方法，调用当前对象的 equals、clone、hashCode、toString 方法时也会触发关联对象的查询。

可以在配置文件中使用 lazyLoadTriggerMethods 配置项覆盖上面四个方法：

```XML
<settings>
    <!--所有方法都会延迟加载-->
    <setting name="lazyLoadTriggerMethods" value="toString()"/>
</settings>
```



#### 1.2.3 全局延迟加载

在 MyBatis 的核心配置文件中可以使用 setting 标签修改全局的加载策略：

```XML
<settings>
    <!--开启全局延迟加载功能-->
    <setting name="lazyLoadingEnabled" value="true"/>
</settings>
```

**注意:** 局部的加载策略优先级高于全局的加载策略。

````XML
<!-- 关闭一对一延迟加载 -->
<association property="user" javaType="com.gvc.domain.User" select="com.gvc.mapper.UserMapper.findById" column="uid" fetchType="eager"/>
````





## 二、MyBatis缓存

### 2.1 为什么使用缓存？

当用户频繁查询某些固定的数据时：

1. 第一次从数据库查询数据并保存在缓存中
2. 后续查询直接从缓存获取
3. 减少数据库查询和网络连接损耗，提高查询效率
4. 缓解高并发访问带来的系统性能问题

**核心价值**：对不经常变化的频繁查询数据使用缓存提高效率。



### 2.2 一级缓存

#### 2.2.1 介绍

- **级别**：SqlSession级别
- **状态**：默认开启
- **特性**：

- - 相同SqlSession+相同参数+相同SQL → 只执行一次SQL
  - 首次查询后结果存入缓存
  - 后续查询优先从缓存获取
  - 除非显式刷新或缓存超时

**工作流程**：

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\1ae2de3a961044a385c5149c6c0320eb\clipboard.png)



#### 2.2.2 验证

```java
//验证mybatis中的一级缓存
    @Test
    public void testOneCache() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        //根据id查询用户信息
        //第一次查询，查询的是数据库
        User user1 = mapper.findById(1);
        System.out.println(user1);

        //第二次查询，查询到是一级缓存
        User user2 = mapper.findById(1);
        System.out.println(user2);

        sqlSession.close();
    }
}
```

我们可以发现，虽然在上面的代码中我们查询了两次，但最后只执行了一次数据库操作，这就是Mybatis提供给我们的一级缓存在起作用了。因为一级缓存的存在，导致第二次查询id为1的记录时，并没有发出sql语句从数据库中查询数据，而是从一级缓存中查询。

 

#### 2.2.3 分析

一级缓存是SqlSession范围的缓存，执行SqlSession的 C（增加）U（更新）D（删除）操作，或者调用clearCache()、commit()、close()方法，都会清空缓存。

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\beeb39272a2c4d04ba6e2f8e9d618e23\clipboard.png)

1. 第一次发起查询用户id为41的用户信息，先去找缓存中是否有id为41的用户信息，如果没有，从数据库查询用户信息。
2. 得到用户信息，将用户信息存储到一级缓存中。

3.如果sqlSession去执行commit操作（执行插入、更新、删除），清空sqlSession中的一级缓存，这样做的目的是为了让缓存中存储的是最新的信息，避免脏读。

4.第二次发起查询用户id为41的用户信息，先去找缓存中是否有id为41的用户信息，缓存中有，直接从缓存中获取用户信息。

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\b3e4ac79d1034f23b5530b9155585609\clipboard.png)

#### 2.2.4 清除

```java
//手动清空缓存
sqlSession.close();
```

或

```xml
<!-- 每次查询时都会清除缓存 -->
<select flushCache="true"></select>
```



### 2.3 二级缓存

#### 2.3.1 介绍

**二级缓存是namespace级别（跨sqlSession）的缓存，是默认不开启的。**

二级缓存的开启需要进行配置，实现二级缓存的时候，MyBatis要求返回的POJO必须是可序列化的。也就是要求实现Serializable接口，配置方法很简单，只需要在映射XML文件配置 <cache/> 就可以开启二级缓存了。

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\2176009f93a84dde99e731d57c4111db\clipboard.png)



#### 2.3.2 验证

**（1）配置核心配置文件SqlMapConfig.xml**

```xml
<settings>
    <!-- 开启二级缓存 -->
    <setting name="cacheEnabled" value="true"/>  
</settings>
```

**（2）配置UserMapper.xml映射**

```xml
<!-- 当前映射开启二级缓存 -->
<cache/>

<!-- useCache="true"：当前statement使用二级缓存 -->
<select id="findById" resultType="com.gvc.domain.User" parameterType="int" useCache="true">
    select * from user where id = #{id}
</select>
```

**（3）修改User实体**

```java
public class User implements Serializable{
    //...
}
```

**（4）测试结果**

```java
//验证mybatis中的二级缓存
@Test
public void testTwoCache() throws IOException {
    InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

    SqlSession sqlSession1 = sqlSessionFactory.openSession();
    UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
    //第一次查询
    User user1 = mapper1.findById(1);
    System.out.println(user1);
    //只有执行sqlSession.commit()或sqlSession.close(),一级缓存中的内容才会刷新到二级缓存
    sqlSession1.close();

    SqlSession sqlSession2 = sqlSessionFactory.openSession();
    UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
    User user2 = mapper2.findById(1);
    System.out.println(user2);
    sqlSession2.close();
}
```



#### 2.3.3 分析

二级缓存是mapper映射级别的缓存，多个SqlSession去操作同一个Mapper映射的sql语句，多个 SqlSession可以共用二级缓存，二级缓存是跨SqlSession的。

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\56783960022047be8a4328d57dc11498\clipboard.png)

1. 映射语句文件中的所有select语句将会被缓存。
2. 映射语句文件中的所有insert、update和delete语句会刷新缓存。



#### 2.3.4 注意问题（脏读）

mybatis的二级缓存因为是namespace级别，所以在进行多表查询时会产生脏读问题。

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\81ba2ceedeb9410291fdcf43085d876f\3-2.png)



### 2.4 小结

1. mybatis的缓存，都不需要我们手动存储和获取数据。mybatis自动维护的。
2. mybatis开启了二级缓存后，那么查询顺序：二级缓存--》一级缓存--》数据库。 
3. 注意：mybatis的二级缓存会存在脏读问题，需要使用第三方的缓存技术解决问题。





## 三、MyBatis注解

### 3.1 MyBatis常用注解

这几年来注解开发越来越流行，Mybatis也可以使用注解开发方式，这样我们就可以减少编写Mapper映射文件了。我们先围绕一些基本的CRUD来学习，再学习复杂映射多表操作。

@Insert: 实现新增，代替了```<insert></insert>```

@Delete: 实现删除，代替了```<delete></delete>```

@Update: 实现更新，代替了```<update></update>```

@Select: 实现查询，代替了```<select></select>```

@Result: 实现结果集封装，代替了```<result></result>```

@Results: 可以与@Result 一起使用，封装多个结果集，代替了```<resultMap></resultMap>```

@One: 实现一对一结果集封装，代替了```<association></association>```

@Many: 实现一对多结果集封装，代替了```<collection></collection>```



### 3.2 MyBatis注解的增删改查【重点】

#### 3.2.1 创建UserMapper接口

```java
public interface UserMapper {
    //查询用户
    @Select("select * from user")
    public List<User> findAll();

    //添加用户
    @Insert("insert into user(username,birthday,sex,address) values (#{username},#{birthday},#{sex},#{address})")
    public void saveUser(User user);

    //更新用户
    @Update("update user set username = #{username},birthday = #{birthday} where id = #{id}")
    public void update(User user);

    //删除用户
    @Delete("delete from user where id = #{id}")
    public void delete(Integer id);
}
```



#### 3.2.2 编写核心配置文件

```xml
<!--我们使用了注解替代的映射文件，所以我们只需要加载使用了注解的Mapper接口即可-->
<mappers>
    <!--扫描使用注解的Mapper类-->
    <mapper class="com.lagou.mapper.UserMapper"></mapper>
</mappers>
```

```xml
<!--或者指定扫描包含映射关系的接口所在的包也可以-->
<mappers>
    <!--扫描使用注解的Mapper类所在的包-->
    <package name="com.lagou.mapper"></package>
</mappers>
```



#### 3.2.3 测试代码

```java
public class MybatisTest    {

    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;

    //在 @Test 标注的方法执行之前来执行
    @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        sqlSession = sqlSessionFactory.openSession();
    }

    @After
    public void after(){
        sqlSession.commit();
        sqlSession.close();
    }

    //测试查询方法
    @Test
    public void testSelect() throws IOException {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = userMapper.findAll();
        for (User user : userList) {
            System.out.println(user);
        }
    }

    //测试添加方法
    @Test
    public void testInsert() throws IOException {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        User user = new User();
        user.setUsername("陶喆");
        user.setSex("男");
        user.setBirthday(new Date());
        user.setAddress("香港");

        userMapper.saveUser(user);
    }

    //测试更新方法
    @Test
    public void testUpdate() throws IOException {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        User user = new User();
        user.setId(11);
        user.setUsername("方大同");
        user.setBirthday(new Date());

        userMapper.update(user);
    }


    //测试更新方法
    @Test
    public void testDelete() throws IOException {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        userMapper.delete(10);
    }
}
```



### 3.3 使用注解实现复杂映射开发

之前我们在映射文件中通过配置 ```<resultMap>、<association>、<collection>``` 来实现复杂关系映射。

使用注解开发后，我们可以使```@Results、@Result、@One、@Many ```注解组合完成复杂关系的配置。

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\28246a8b003744039db145880a286275\clipboard.png)



### 3.4 一对一查询

#### 3.4.1 介绍

**需求**：查询一个订单，同时查询出该订单所属的用户。

**SQL 语句示例**：

```sql
SELECT * FROM orders
```



#### 3.4.2 代码实现

**（1） OrderMapper 接口**

```java
public interface OrderMapper {
    //查询所有订单，同时查询订单所属的用户信息
    @Select("SELECT * FROM orders")
    @Results({//代替<resultMap>标签
        @Result(property = "id",column = "id",id = true),  //id = true表示为主键字段
        @Result(property = "ordertime",column = "ordertime"),
        @Result(property = "total",column = "total"),
        @Result(property = "uid",column = "uid"),
        @Result(property = "user", javaType = User.class,column = "uid",
                one = @One(select = "com.gvc.mapper.UserMapper.findById",fetchType = FetchType.EAGER)) //namespace.id, EAGER立即加载
    })
    public List<Orders> findAllWithUser();
}
```



**（2）UserMapper接口**

```java
public interface UserMapper {
    //根据id查询用户
    @Select("select * from user where id = #{uid}")
    public User findById(Integer uid);
}
```



**（3）测试代码**

```java
//一对一查询：注解方式
@Test
public void testOneToOne() throws IOException {
    OrderMapper mapper = sqlSession.getMapper(OrderMapper.class);
    List<Orders> orders = mapper.findAllWithUser();
    for (Orders order : orders) {
        System.out.println(order);
    }
}
```



### 3.5 一对多查询

#### 3.5.1 介绍

**需求**：查询一个用户，与此同时查询出该用户具有的订单

**SQL 语句示例**：

```sql
SELECT * FROM user
SELECT * FROM orders WHERE uid = #{用户id}
```



#### 3.5.2 代码实现

**（1） UserMapper 接口**

```java
public interface UserMapper {
    //查询所有用户及关联的订单信息
    @Select("SELECT * FROM user")
    @Results({
            @Result(property = "id",column = "id",id = true),
            @Result(property = "username",column = "username"),
            @Result(property = "birthday",column = "birthday"),
            @Result(property = "sex",column = "sex"),
            @Result(property = "address",column = "address"),
            @Result(property = "ordersList",javaType = List.class,column = "id",
                    many = @Many(select = "com.gvc.mapper.OrderMapper.findOrdersByUid")),
    })
    public List<User> findAllWithOrder();

}
```

**（2）OrderMapper接口**

```java
public interface OrderMapper {
    //根据用户id查询订单
    @Select("select * from orders where uid = #{uid}")
    public List<Orders> findOrdersByUid(Integer uid);
}
```

**（3）测试代码**

```java
//一对多查询：注解方式
@Test
public void testOneToMany() throws IOException {
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> userList = mapper.findAllWithOrder();
    for (User user : userList) {
        System.out.println(user);
        System.out.println(user.getOrdersList());
    }
}
```

![img](C:\D_Data\SysTools\Youdao Note\Youdao Files\ggvxcc@163.com\e9920669eb974688b5c9a5812b61b143\clipboard.png)



### 3.6 多对多查询

#### 3.6.1 介绍

**需求**：查询所有用户，同时查询出该用户的所有角色

**SQL 语句示例**：

```sql
SELECT * FROM user;
SELECT * FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.roleid
    WHERE ur.userid = #{uid}
```



#### 3.6.2 代码实现

**（1） UserMapper 接口**

```java
public interface UserMapper {
    //查询所有用户及关联角色
    @Select("SELECT * FROM user")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "birthday", column = "birthday"),
            @Result(property = "sex", column = "sex"),
            @Result(property = "address", column = "address"),
           @Result(property = "roleList",javaType = List.class, column = "id",many = @Many(select = "com.gvc.mapper.RoleMapper.findRoleById")),
    })
    public List<User> findAllWithRole();
}
```

**（2）RoleMapper接口**

```java
public interface RoleMapper {
    //根据用户id查询角色
    @Select("SELECT * FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.roleid WHERE ur.userid = #{uid}")
    public List<Role> findRoleById();
}
```

**（3）测试代码**

```java
//多对多查询：注解方式
@Test
public void testManyToMany() throws IOException {
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> userList = mapper.findAllWithRole();
    for (User user : userList) {
        System.out.println(user);
        System.out.println(user.getRoleList());
    }
}
```



### 3.7 基于注解的二级缓存

#### 3.7.1 配置 SqlMapConfig.xml 文件开启二级缓存支持

```xml
<settings>
    <!-- cacheEnabled 默认值为 true，可省略配置 -->
    <setting name="cacheEnabled" value="true"/>
</settings>
```



#### 3.7.2 在 Mapper 接口中使用注解配置二级缓存

```java
@CacheNamespace  // 启用二级缓存
public interface UserMapper {
    // 接口方法...
}
```



### 3.8 注解延迟加载

在注解配置中，通过 fetchType 属性控制加载策略：

> - **FetchType.LAZY**：懒加载（延迟加载）
> - **FetchType.EAGER**：立即加载
> - **FetchType.DEFAULT**：使用全局配置



### 3.9 小结

|              | **注解开发**           | **XML 配置**           |
| ------------ | ---------------------- | ---------------------- |
| **开发效率** | 编写简单，效率更高     | 配置繁琐，效率较低     |
| **可维护性** | 需修改源码，维护成本高 | 修改配置文件，维护性强 |

**适用场景**：

- 注解开发：快速开发、简单 SQL 场景。(如单表操作)
- XML 配置：复杂 SQL、动态 SQL 或需要高维护性的项目。（如多表操作）