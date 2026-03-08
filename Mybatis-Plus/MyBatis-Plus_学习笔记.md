# MyBatis-Plus 学习笔记

## 一、MyBatis-Plus 简介

MyBatis-Plus（简称MP）是MyBatis的增强工具，**只做增强不做改变**，引入后不会对现有工程产生影响，能通过简单配置快速实现单表CRUD操作，大幅节省开发时间，是MyBatis的最佳搭档。

核心特点：

- 无侵入：对原有MyBatis代码无侵入性

- 效率至上：极简配置，快速实现单表CRUD

- 功能丰富：提供条件构造器、代码生成、分页插件等多种实用功能

## 二、快速入门

### 2.1 学习目标

1. 掌握MP的基本用法

2. 体会MP的无侵入和便捷性特点

### 2.2 入门案例需求

基于课前项目实现用户的基础操作：

```markdOwn
1. 新增用户功能

2. 根据id查询用户

3. 根据id批量查询用户

4. 根据id更新用户

5. 根据id删除用户
```

### 2.3 实现步骤

#### 步骤1：引入MybatisPlus起步依赖

替换原有MyBatis依赖，MP的starter已集成MyBatis并实现自动装配：

```XML

<!--MybatisPlus-->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.1</version>
</dependency>
```

#### 步骤2：定义Mapper并继承BaseMapper

自定义Mapper接口继承MP提供的`BaseMapper`，即可获得内置的CRUD方法：

```Java

public interface UserMapper extends BaseMapper<User> {
}
```

#### BaseMapper 内置核心方法

|操作类型|核心方法|
|---|---|
|新增|insert(T)|
|修改|updateById(T)、update(T, Wrapper<T>)|
|查询|selectById(Serializable)、selectBatchIds(Collection)、selectList(Wrapper<T>)等|
|删除|deleteById(Serializable)、delete(Wrapper<T>)等|
### 2.4 快速入门总结

使用MP的基本步骤：

1. 引入MybatisPlus起步依赖，替代Mybatis依赖

2. 定义Mapper接口并继承`BaseMapper<T>`



## 三、核心基础：常见注解与配置

### 3.1 MP 表与实体的默认映射规则

MP通过扫描实体类，基于反射自动获取数据库表信息，默认规则：

```markdown
- 类名**驼峰转下划线**作为表名

- 名为`id`的字段作为主键

- 变量名**驼峰转下划线**作为表的字段名
```

### 3.2 常见注解

MP提供注解用于自定义表与实体的映射关系，解决默认规则不适用的场景，核心注解如下：

|注解|作用|核心使用场景|
|---|---|---|
|`@TableName`|指定实体类对应的数据库表名|实体类名与表名不一致时|
|`@TableId`|指定表中的主键字段信息|主键名非id、主键生成策略自定义时|
|`@TableField`|指定表中的普通字段信息|字段名不一致、is开头布尔值、关键字冲突、非数据库字段|
#### 3.2.1 @TableId 主键生成策略（IdType枚举）

```markdown
- `AUTO`：数据库自增长（需数据库表设置自增）

- `INPUT`：通过set方法自行输入主键值

- `ASSIGN_ID`：雪花算法生成主键（默认），由`IdentifierGenerator`接口的`nextId`方法实现
```

#### 3.2.2 @TableField 常见使用场景示例

```Java

@TableName("tb_user") // 指定表名为tb_user
public class User {
    @TableId(value = "id", type = IdType.AUTO) // 主键自增
    private Long id;
    
    @TableField("username") // 字段名映射
    private String name;
    
    @TableField("is_married") // is开头布尔值映射
    private Boolean isMarried;
    
    @TableField("`order`") // 解决关键字冲突，反引号包裹
    private Integer order;
    
    @TableField(exist = false) // 非数据库字段，MP忽略
    private String address;
}
```

### 3.3 常见配置

MP的配置继承MyBatis原生配置，并新增特有配置，配置文件以`yml`为例：

```YAML

mybatis-plus:
  type-aliases-package: com.gvc.mp.domain.po # 实体类别名扫描包
  mapper-locations: "classpath*:/mapper/**/*.xml" # Mapper.xml文件路径（默认值）
  configuration:
    map-underscore-to-camel-case: true # 开启下划线与驼峰自动映射（默认开启）
    cache-enabled: false # 关闭二级缓存
  global-config:
    db-config:
      id-type: assign_id # 全局主键生成策略：雪花算法
      update-strategy: not_null # 全局更新策略：只更新非空字段
```

### 3.4 基础内容总结

1. MP通过**默认映射规则**获取表信息，也可通过注解自定义

2. 核心注解：`@TableName`、`@TableId`、`@TableField`

3. MP使用基本流程：引入依赖 → 定义Mapper继承BaseMapper → 实体类加注解声明表信息 → 配置文件自定义配置



## 四、核心功能

### 4.1 条件构造器

MP提供强大的条件构造器，支持各种复杂的`where`条件，满足日常开发的所有查询、更新、删除需求，核心封装类为`QueryWrapper`和`UpdateWrapper`。

```markdown
'wrap'意为包装，'wrapper'意为封装器
```

#### 4.1.1 核心构造器分类

![01](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp01.png)

|构造器|适用场景|推荐使用|
|---|---|---|
|`QueryWrapper`|构建查询、删除、更新的where条件|推荐使用**LambdaQueryWrapper**（避免硬编码）|
|`UpdateWrapper`|构建复杂的update语句（如set字段为表达式）|推荐使用**LambdaUpdateWrapper**（避免硬编码）|
|`LambdaQueryWrapper`|基于Lambda的QueryWrapper，字段名由编译器检查|首选|
|`LambdaUpdateWrapper`|基于Lambda的UpdateWrapper，字段名由编译器检查|首选|
#### 4.1.2 案例实操

##### 案例1：基于QueryWrapper的查询

需求：查询名字中带o、存款≥1000元的用户，返回id、username、info、balance字段

```Java

// 构建查询条件
QueryWrapper<User> wrapper = new QueryWrapper<User>()
        .select("id", "username", "info", "balance")
        .like("username", "o")
        .ge("balance", 1000);
// 执行查询
List<User> userList = userMapper.selectList(wrapper);
```

对应的SQL：

```SQL

SELECT id,username,info,balance FROM user WHERE username LIKE ? AND balance >= ?
```

##### 案例2：基于UpdateWrapper的更新

需求：更新id为1、2、4的用户余额，扣减200

```Java

List<Long> ids = List.of(1L, 2L, 4L);
UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
        .setSql("balance = balance - 200") // 自定义set语句
        .in("id", ids);
// 执行更新
userMapper.update(null, wrapper);
```

对应的SQL：

```SQL

UPDATE user SET balance = balance - 200 WHERE id in (1, 2, 4)
```

#### 4.1.3 条件构造器总结

```markdown
- `QueryWrapper/LambdaQueryWrapper`：主要构建where条件，用于select、delete、普通update

- `UpdateWrapper/LambdaUpdateWrapper`：适用于set语句为**表达式/复杂逻辑**的更新操作

- 优先使用**Lambda版构造器**，避免字段名硬编码导致的运行时错误
```



### 4.2 自定义SQL

MP支持结合`Wrapper`构建复杂where条件，同时自定义SQL的其他部分，兼顾灵活性和便捷性。

#### 4.2.1 自定义SQL核心步骤

```markdown
1. 基于`Wrapper`构建where条件

2. Mapper方法参数中用`@Param("ew")`声明wrapper变量（变量名必须为`ew`）

3. 自定义XML/注解SQL，通过`${ew.customSqlSegment}`引入Wrapper的条件
```

```markdown
- `ew` 为 `Entity Wrapper` 的缩写
```

#### 4.2.2 案例实操

需求：将id在指定范围的用户余额扣减指定值

![02](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp02.png)

只用mp在业务代码里编写sql语句，不符合规范。
mp善于处理where条件编写，无论多复杂都便于快速定义；但sql语句前半部分如`set balance = balance-200`只能硬编码在业务逻辑里；

于是两相结合，就是自定义SQL的意义。

##### 步骤1：Mapper接口定义方法

```Java

void updateBalanceByIds(@Param("ew") LambdaQueryWrapper<User> wrapper, @Param("amount") int amount);
```

##### 步骤2：Mapper.xml自定义SQL

```XML

<update id="updateBalanceByIds">
    UPDATE tb_user SET balance = balance - #{amount} ${ew.customSqlSegment}
</update>
```

##### 步骤3：调用方法

```Java

List<Long> ids = List.of(1L, 2L, 4L);
int amount = 200;
// 构建条件
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().in(User::getId, ids);
// 调用自定义SQL
userMapper.updateBalanceByIds(wrapper, amount);
```

#### 4.2.3 多表关联查询

MP的`Wrapper`主要用于单表，多表关联查询可直接自定义SQL，示例：

需求：查询收货地址在北京且用户id在指定范围的用户

```XML

<select id="queryUserByIdAndAddr" resultType="com.gvc.mp.domain.po.User">
    SELECT *
    FROM user u
    INNER JOIN address a ON u.id = a.user_id
    WHERE u.id
    <foreach collection="ids" separator="," item="id" open="IN (" close=")">
        #{id}
    </foreach>
    AND a.city = #{city}
</select>
```

### 4.3 Service接口

MP提供了通用的`IService`接口和`ServiceImpl`实现类，封装了更上层的CRUD方法（包含批量操作、分页操作等），弥补了`BaseMapper`在业务层的功能不足。

![03](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp03.png)

#### 4.3.1 使用流程

1. **自定义Service接口**：继承MP的`IService<T>`

2. **自定义Service实现类**：实现自定义接口，并继承`ServiceImpl<Mapper<T>, T>`

#### 4.3.2 代码示例

```Java

// 1. 自定义Service接口
public interface IUserService extends IService<User> {
    // 自定义业务方法
    List<User> listByName(String name);
}

// 2. 自定义Service实现类
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    // 实现自定义业务方法
    @Override
    public List<User> listByName(String name) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().like(User::getUsername, name);
        return this.list(wrapper);
    }
}
```

#### 4.3.3 IService 核心内置方法

IService封装了批量操作、分页、链式查询等功能，核心方法分类：

- 新增：`save(T)`、`saveBatch(Collection<T>)`（批量新增）

- 修改：`updateById(T)`、`updateBatchById(Collection<T>)`（批量更新）

- 删除：`removeById(Serializable)`、`removeBatchByIds(Collection<?>)`（批量删除）

- 查询：`getById(Serializable)`、`list(Wrapper<T>)`、`page(IPage<T>, Wrapper<T>)`（分页查询）

- 计数：`count()`、`count(Wrapper<T>)`

- 链式查询：`lambdaQuery()`、`lambdaUpdate()`

#### 4.3.4 Service层实操案例

##### 案例1：Restful风格接口实现

基于IService实现用户的Restful接口，包含新增、删除、查询、批量查询、扣减余额等功能：

|编号|接口|请求方式|请求路径|请求参数|返回值|
|---|---|---|---|---|---|
|1|新增用户|POST|/users|用户表单实体|无|
|2|删除用户|DELETE|/users/{id}|用户id|无|
|3|根据id查询用户|GET|/users/{id}|用户id|用户VO|
|4|根据id批量查询|GET|/users|用户id集合|用户VO集合|
|5|根据id扣减余额|PUT|/users/{id}/deduction/{money}|用户id、扣减金额|无|


项目依赖

```xml
<!--swagger-->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
    <version>4.1.0</version>
</dependency>
<!--web-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

然后需要配置swagger信息：

```yml
knife4j:
  enable: true
  openapi:
    title: 用户管理接口文档
    description: "用户管理接口文档"
    email: 2059511585@qq.com
    concat: gvcheng
    url: https://www.itcast.cn
    version: v1.0.0
    group:
      default:
        group-name: default
        api-rule: package
        api-rule-resources:
          - com.gvc.mp.controller     
```



##### 案例2：Lambda复杂条件查询

需求：实现多条件模糊查询

```markdown
- `name` 用户名关键字，条件可为空
- `status` 用户状态，条件可为空
- `minBalance` 最小余额，条件可为空
- `maxBalance` 最大余额，条件可为空
```

原生mybatis写法：

```xml
<select id="queryUsers" resultType="com.gvc.mp.domain.po.User">
    SELECT *
    FROM tb_user
    <where>
        <if test="name != null">
            AND username LIKE #{name}
        </if>
        <if test="status != null">
            AND `status` = #{status}
        </if>
        <if test="minBalance != null and maxBalance != null">
            AND balance BETWEEN #{minBalance} AND #{maxBalance}
        </if>
    </where>
</select>
```

```Java

public List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance) {
        return lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();
    }
```

可以发现lambdaQuery方法中除了可以构建条件，还需要在链式编程的最后添加一个`list()`，这是在告诉MP我们的调用结果需要是一个list集合。这里不仅可以用`list()`，可选的方法有：

- `.one()`：最多1个结果
- `.list()`：返回集合结果
- `.count()`：返回计数结果

**lambdaUpdate复杂更新**

与lambdaQuery方法类似，IService中的lambdaUpdate方法可以非常方便的实现**复杂更新**业务。

例如下面的需求：

> 需求：改造根据id修改用户余额的接口，要求如下
>
> - 如果扣减后余额为0，则将用户status修改为冻结状态（2）

也就是说我们在扣减用户余额时，需要对用户剩余余额做出判断，如果发现剩余余额为0，则应该将status修改为2，这就是说update语句的set部分是动态的。

```java
	@Override
    @Transactional
    public void deductBalance(Long id, Integer money) {
        //1.查询用户
        User user = this.getById(id);
        //2.校验用户状态
        if(user==null || user.getStatus()==2){
            throw new RuntimeException("用户状态异常！");
        }
        //3.校验余额是否充足
        if(user.getBalance() < money){
            throw new RuntimeException("用户余额不足！");
        }
        //4.扣减余额 update tb_user set balance = balance - ?
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance,remainBalance)
                .set(remainBalance == 0,User::getStatus,2)
                .eq(User::getId,id)
                .eq(User::getBalance,user.getBalance()) //乐观锁
                .update();
    }
```



##### 案例3：批量新增性能对比

批量插入10万条用户数据的三种方案对比：

1. 普通for循环逐条插入：速度极差，**不推荐**
2. MP的`saveBatch`：基于预编译的批处理，性能良好
3. 开启`rewriteBatchedStatements=true`（JDBC参数）+ `saveBatch`：性能最优，**推荐**

###### 逐条插入

```java
@Test
void testSaveOneByOne() {
    long b = System.currentTimeMillis();
    for (int i = 1; i <= 100000; i++) {
        userService.save(buildUser(i));
    }
    long e = System.currentTimeMillis();
    System.out.println("耗时：" + (e - b));
}

private User buildUser(int i) {
    User user = new User();
    user.setUsername("user_" + i);
    user.setPassword("123");
    user.setPhone("" + (18688190000L + i));
    user.setBalance(2000);
    user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
    user.setCreateTime(LocalDateTime.now());
    user.setUpdateTime(user.getCreateTime());
    return user;
}
```

执行结果如下，约3min46s

![04](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp04.png)

###### MybatisPlus批处理

```java
@Test
void testSaveBatch() {
    // 准备10万条数据
    List<User> list = new ArrayList<>(1000);
    long b = System.currentTimeMillis();
    for (int i = 1; i <= 100000; i++) {
        list.add(buildUser(i));
        // 每1000条批量插入一次
        if (i % 1000 == 0) {
            userService.saveBatch(list);
            list.clear();
        }
    }
    long e = System.currentTimeMillis();
    System.out.println("耗时：" + (e - b));
}
```

执行最终耗时如下，约19s

![](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp05.png)

可以看到使用了批处理以后，比逐条新增效率提高了10倍左右，性能还是不错的。

不过，我们简单查看一下`MybatisPlus`源码：

```java
@Transactional(rollbackFor = Exception.class)
@Override
public boolean saveBatch(Collection<T> entityList, int batchSize) {
    String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
    return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
}
// ...SqlHelper
public static <E> boolean executeBatch(Class<?> entityClass, Log log, Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
    Assert.isFalse(batchSize < 1, "batchSize must not be less than one");
    return !CollectionUtils.isEmpty(list) && executeBatch(entityClass, log, sqlSession -> {
        int size = list.size();
        int idxLimit = Math.min(batchSize, size);
        int i = 1;
        for (E element : list) {
            consumer.accept(sqlSession, element);
            if (i == idxLimit) {
                sqlSession.flushStatements();
                idxLimit = Math.min(idxLimit + batchSize, size);
            }
            i++;
        }
    });
}
```

可以发现其实`MybatisPlus`的批处理是基于`PrepareStatement`的预编译模式，然后批量提交，最终在数据库执行时还是会有多条insert语句，逐条插入数据。SQL类似这样：

```sql
Preparing: INSERT INTO user ( username, password, phone, info, balance, create_time, update_time ) VALUES ( ?, ?, ?, ?, ?, ?, ? )
Parameters: user_1, 123, 18688190001, "", 2000, 2023-07-01, 2023-07-01
Parameters: user_2, 123, 18688190002, "", 2000, 2023-07-01, 2023-07-01
Parameters: user_3, 123, 18688190003, "", 2000, 2023-07-01, 2023-07-01
```

而如果想要得到最佳性能，最好是将多条SQL合并为一条，像这样：

```sql
INSERT INTO user ( username, password, phone, info, balance, create_time, update_time )
VALUES 
(user_1, 123, 18688190001, "", 2000, 2023-07-01, 2023-07-01),
(user_2, 123, 18688190002, "", 2000, 2023-07-01, 2023-07-01),
(user_3, 123, 18688190003, "", 2000, 2023-07-01, 2023-07-01),
(user_4, 123, 18688190004, "", 2000, 2023-07-01, 2023-07-01);
```

该怎么做呢？

MySQL的客户端连接参数中有这样的一个参数：`rewriteBatchedStatements`。顾名思义，就是重写批处理的`statement`语句。这个参数的默认值是false，我们需要修改连接参数，将其配置为true.

修改项目中的application.yml文件，在jdbc的url后面添加参数`&rewriteBatchedStatements=true`

```yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/mp?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: MySQL123
```

再次测试插入10万条数据，可以发现速度有非常明显的提升：约6s

![06](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp06.png)



## 五、扩展功能

### 5.1 代码生成

MP提供代码生成器（可通过IDEA插件快速使用），能根据数据库表自动生成**实体类、Mapper、Service、Controller**等代码，大幅减少重复开发工作。

#### 5.1.1 IDEA插件使用步骤

1. 安装MyBatisPlus相关代码生成插件（如`MybatisPlus Code Generator`）

2. 配置数据库连接（数据库地址、用户名、密码）

3. 选择需要生成代码的数据库表

4. 配置生成参数：父级包路径、作者、主键策略、生成的模块（Entity/Mapper/Service/Controller）

5. 执行生成，自动创建对应代码文件

#### 5.1.2 生成代码示例

插件安装：

![07](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp07.png)

安装成功后 `Tools`多出两个选项，具体分别如下

![08](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp08.png)

![09](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp09.png)

![10](https://github.com/gvcheng/note_images/blob/main/MybatisPlus_img/mp10.png)

自动生成的代码包含完整的基础CRUD，示例：

```Java

// 自动生成的实体类
@TableName("user")
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private Boolean isMarried;
    private Integer order;
}

// 自动生成的Mapper
public interface UserMapper extends BaseMapper<User> {
}

// 自动生成的Service
public interface IUserService extends IService<User> {
}

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
}
```

### 5.2 静态工具 Db

MP提供静态工具类`Db`，无需注入Mapper/Service，直接调用CRUD方法，适用于**非业务层的简单操作**（如工具类、测试类）。需要多告知一个对象字节码参数`Class<T>`获取泛型

#### Db 核心方法

`Db`的方法与IService基本一致，核心包括：

- 新增：`Db.save(T)`、`Db.saveBatch(Collection<T>)`

- 修改：`Db.updateById(T)`、`Db.update(T, Wrapper<T>)`

- 查询：`Db.getById(Serializable, Class<T>)`、`Db.list(Wrapper<T>, Class<T>)`

- 删除：`Db.removeById(Serializable, Class<T>)`

#### 案例：静态工具查询

需求：

①改造根据id查询用户的接口，查询用户的同时，查询出用户对应的所有地址

②改造根据id批量查询用户的接口，查询用户的同时，查询出用户对应的所有地址

③实现根据用户id查询收货地址功能，需要验证用户状态，冻结用户抛出异常（练习）

```Java

// 根据id查询用户
User user = Db.getById(1L, User.class);
// 查询用户的所有地址
LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<Address>().eq(Address::getUserId, user.getId());
List<Address> addressList = Db.list(wrapper, Address.class);
// 封装结果
user.setAddressList(addressList);
```

### 5.3 逻辑删除

逻辑删除是**基于代码模拟删除效果**，并非真正删除数据库数据，通过添加**标记字段**区分数据是否被删除。

#### 5.3.1 实现思路

1. 数据库表添加逻辑删除字段（如`deleted`，0=未删除，1=已删除）

2. 删除操作：将标记字段置为1

3. 查询操作：自动过滤标记字段为1的数据

```sql
-- 对应SQL 
UPDATE tb_user SET deleted = 1 WHERE id = ? deleted = 0 
```

#### 5.3.2 MP配置逻辑删除

只需在配置文件中声明逻辑删除的字段和值，MP会自动修改CRUD语句，无需改变方法调用：

```YAML

mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted # 全局逻辑删除字段名
      logic-delete-value: 1 # 逻辑已删除值
      logic-not-delete-value: 0 # 逻辑未删除值
```

#### 5.3.3 注意事项

MP的逻辑删除存在一定问题，**不推荐优先使用**：

- 会导致数据库表垃圾数据增多，影响查询效率

- 所有SQL都会自动添加逻辑删除字段的判断，增加SQL复杂度

- 替代方案：若数据不能物理删除，可将数据迁移到历史表

### 5.4 枚举处理器

MP支持将实体类中的**枚举类型字段**与数据库中的**数值/字符串字段**自动映射，无需手动转换。

#### 5.4.1 实现步骤

1. **枚举类添加@EnumValue注解**：标记与数据库字段对应的属性

2. **配置全局枚举处理器**：让MP识别枚举注解并完成类型转换

#### 5.4.2 代码示例

##### 步骤1：定义枚举类并添加@EnumValue

```Java

@Getter
public enum UserStatus {
    NORMAL(1, "正常"),
    FREEZE(2, "冻结");

    @EnumValue // 标记与数据库对应的数值
    private final int value;
    @JsonValue // 前端返回时展示的描述（可选）
    private final String desc;

    UserStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
```

##### 步骤2：修改实体类字段为枚举类型

```Java

@Data
@TableName("tb_user")
public class User {
    private Long id;
    private String username;
    private UserStatus status; // 枚举类型，对应数据库int字段
}
```

##### 步骤3：配置全局枚举处理器

```YAML

mybatis-plus:
  configuration:
    default-enum-type-handler: com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler
```

### 5.5 JSON处理器

MP支持将数据库中的**JSON类型字段**与实体类中的**自定义对象类型**自动映射，无需手动序列化/反序列化。

#### 5.5.1 实现步骤

1. 定义与JSON字段对应的自定义实体类

2. 实体类中JSON字段添加`@TableField`，指定**JSON类型处理器**

3. 实体类添加`@TableName(autoResultMap = true)`开启自动结果映射

#### 5.5.2 代码示例

##### 步骤1：定义JSON对应的自定义实体类

```Java
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class UserInfo {
    private Integer age;
    private String intro;;
    private String gender;
}
```

##### 步骤2：修改主实体类，配置JSON处理器

```Java

@Data
@TableName(value = "tb_user", autoResultMap = true) // 开启自动结果映射
public class User {
    private Long id;
    private String username;
    // JSON字段，指定Jackson处理器（也可使用Gson/Fastjson处理器）
    @TableField(typeHandler = JacksonTypeHandler.class)
    private UserInfo info; // 自定义对象，对应数据库JSON字段
}
```

### 5.6 配置加密

MP从3.3.2版本开始提供**基于AES算法**的加密工具，用于对配置文件中的**敏感信息**（如数据库用户名、密码）进行加密，避免明文泄露。

#### 5.6.1 实现步骤

1. 生成AES随机密钥

2. 利用密钥加密敏感信息（用户名、密码）

3. 配置文件中使用加密后的密文

4. 项目启动时传入密钥，让MP自动解密

#### 5.6.2 代码示例

##### 步骤1：生成AES密钥和加密后的密文

```Java

@Test
void generateEncryptData() {
    // 1.生成16位随机AES密钥
    String randomKey = AES.generateRandomKey();
    System.out.println("AES密钥：" + randomKey); // 如：d1104d7c3b616f0b

    // 2.加密数据库用户名和密码
    String encryptUsername = AES.encrypt("root", randomKey);
    String encryptPassword = AES.encrypt("MySQL123", randomKey);
    System.out.println("加密后的用户名：" + encryptUsername);
    System.out.println("加密后的密码：" + encryptPassword);
}
```

##### 步骤2：配置文件使用密文（前缀`mpw:`）

```YAML

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/mp
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: mpw:QWWVnk1Oal3258x5rVhaeQ== # 加密后的用户名
    password: mpw:EUFmeH3cNAzdRGdOQcabWg== # 加密后的密码
```

##### 步骤3：项目启动时传入AES密钥

- **Jar包启动**：`java -jar xxx.jar --mpw.key=d1104d7c3b616f0b`

- **IDEA启动**：Program arguments中配置`--mpw.key=d1104d7c3b616f0b`

- **单元测试**：通过`@SpringBootTest(args = "--mpw.key=d1104d7c3b616f0b")`指定

## 六、插件功能

MP基于MyBatis的`Interceptor`实现了核心拦截器`MybatisPlusInterceptor`，并提供多种内置拦截器，实现分页、多租户、乐观锁等功能，插件可按需配置，灵活扩展。

### 6.1 MP 核心拦截器架构

`MybatisPlusInterceptor`是MP的核心拦截器，内部维护了内置拦截器的集合，执行时会依次调用所有内置拦截器，核心拦截点包括：

- `StatementHandler`的`prepare`、`getBoundSql`方法

- `Executor`的`update`、`query`方法

### 6.2 MP 内置拦截器列表

|序号|拦截器|功能描述|
|---|---|---|
|1|`TenantLineInnerInterceptor`|多租户插件，自动添加租户ID条件|
|2|`DynamicTableNameInnerInterceptor`|动态表名插件，实现表名的动态替换|
|3|`PaginationInnerInterceptor`|分页插件，实现自动分页（最常用）|
|4|`OptimisticLockerInnerInterceptor`|乐观锁插件，解决并发更新问题|
|5|`IllegalSQLInnerInterceptor`|SQL性能规范插件，检测并拦截垃圾SQL|
|6|`BlockAttackInnerInterceptor`|防止全表更新/删除插件，避免误操作|
### 6.3 分页插件（PaginationInnerInterceptor）

分页插件是MP最常用的插件，能自动将普通查询转换为分页查询，无需手动编写`limit`语句。

#### 6.3.1 配置分页插件

在配置类中注册`MybatisPlusInterceptor`，并添加分页插件：

```Java

@Configuration
public class MybatisConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 配置分页插件，指定数据库类型（MYSQL/ORACLE等）
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        pageInterceptor.setMaxLimit(1000L); // 设置分页上限，避免超大分页
        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }
}
```

#### 6.3.2 使用分页插件

MP提供`IPage`接口和`Page`实现类，结合`IService`/`BaseMapper`的分页方法即可实现分页。

##### 代码示例：基础分页查询

```Java

@Test
void testPageQuery() {
    // 1.设置分页参数：第1页，每页5条
    int pageNo = 1, pageSize = 5;
    Page<User> page = Page.of(pageNo, pageSize);
    // 2.设置排序：按balance降序
    page.addOrder(new OrderItem("balance", false));
    // 3.执行分页查询（Service层）
    Page<User> resultPage = userService.page(page);
    
    // 4.获取分页结果
    long total = resultPage.getTotal(); // 总条数
    long pages = resultPage.getPages(); // 总页数
    List<User> records = resultPage.getRecords(); // 当前页数据
}
```

#### 6.3.3 实战案例：分页查询接口

需求：实现用户分页查询接口，支持条件查询、自定义排序，接口规范：

- 请求方式：GET

- 请求路径：/users/page

- 请求参数：`pageNo`、`pageSize`、`sortBy`、`isAsc`、`name`、`status`

- 排序规则：sortBy为空时默认按updateTime排序，否则按指定字段排序

##### 步骤1：定义分页查询参数实体

```Java
@Data
@ApiModel(description = "分页查询实体")
public class PageQuery {
    @ApiModelProperty("页码")
    private Integer pageNo;
    @ApiModelProperty("页面大小")
    private Integer pageSize;
    @ApiModelProperty("排序字段")
    private String sortBy;
    @ApiModelProperty("是否升序")
    private boolean isAsc;
}
```

##### 步骤2：定义分页结果返回实体

```Java
@Data
@ApiModel(description = "分页结果")
public class PageDTO<T> {
    @ApiModelProperty("总条数")
    private Integer total;
    @ApiModelProperty("总页数")
    private Integer pages;
    @ApiModelProperty("集合")
    private List<?> list;
}
```

##### 步骤3：Controller实现接口

```Java

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/page")
    public PageDTO<UserVO> page(UserPageQuery query) {
        // 1.构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(StringUtils.isNotBlank(query.getName()), User::getUsername, query.getName())
                .eq(query.getStatus() != null, User::getStatus, query.getStatus());
        // 2.执行分页查询
        Page<User> page = userService.page(query.toPage(), wrapper);
        // 3.转换为VO并返回
        return PageDTO.of(page.convert(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            // 枚举字段转换为描述
            vo.setStatusDesc(user.getStatus().getDesc());
            return vo;
        }));
    }
}
```

##### 接口返回结果示例

```JSON

{
    "total": 1005,
    "pages": 201,
    "list": [
        {
            "id": 1,
            "username": "Jack",
            "info": {
                "age": 21,
                "gender": "male",
                "intro": "佛系青年"
            },
            "statusDesc": "正常",
            "balance": 2000
        },
        {
            "id": 2,
            "username": "Rose",
            "info": {
                "age": 20,
                "gender": "female",
                "intro": "文艺青年"
            },
            "statusDesc": "冻结",
            "balance": 1000
        }
    ]
}
```

#### 6.3.4 分页封装，简化开发

• 在PageQuery中定义方法，将PageQuery对象转为MyBatisPlus中的Page对象

• 在PageDTO中定义方法，将MyBatisPlus中的Page结果转为PageDTO结果

在PageQuery中增添下列方法：

```java
@Data
@ApiModel(description = "分页查询实体")
public class PageQuery {
    @ApiModelProperty("页码")
    private Integer pageNo = 1;
    @ApiModelProperty("页面大小")
    private Integer pageSize = 5;
    @ApiModelProperty("排序字段")
    private String sortBy;
    @ApiModelProperty("是否升序")
    private boolean isAsc = true;

    public <T> Page<T> toPage(OrderItem...items) {
        //1 构建分页条件
        //1.1 分页条件
        Page<T> page = Page.of(pageNo, pageSize);
        //1.2 排序条件
        if (StrUtil.isNotBlank(sortBy)) {//非空
            page.addOrder(new OrderItem(sortBy, isAsc));
        }else if (items!=null && items.length>0){
            //为空，默认按照更新时间排序
            page.addOrder(items);
        }
        return page;
    }

    public <T> Page<T> toPage(String defaultSortBy, boolean defaultIsAsc) {
        return toPage(new OrderItem(defaultSortBy, defaultIsAsc));
    }

    public <T> Page<T> toPageSortByCreateTime() {
        return toPage(new OrderItem("create_time", false));
    }

    public <T> Page<T> toPageSortByUpdateTime() {
        return toPage(new OrderItem("update_time", false));
    }
}
```

在PageDTO中增添下列方法：

```java
@Data
@ApiModel(description = "分页结果")
public class PageDTO<T> {
    @ApiModelProperty("总条数")
    private long total;
    @ApiModelProperty("总页数")
    private long pages;
    @ApiModelProperty("集合")
    private List<?> list;

    public static<PO, VO> PageDTO<VO> of(Page<PO> p, Class clazz) {
        //3.1 总条数，总页数，当前页数据
        PageDTO<VO> dto = new PageDTO<>();
        dto.setTotal(p.getTotal());
        dto.setPages(p.getPages());
        List<PO> records = p.getRecords();
        if(CollUtil.isEmpty(records)){
            dto.setList(Collections.emptyList());
            return dto;
        }
        //3.2 转vo
        List<VO> voList = BeanUtil.copyToList(records, clazz);
        dto.setList(voList);
        return dto;
    }

    public static<PO, VO> PageDTO<VO> of(Page<PO> p, Function<PO, VO> converter) {
        //3.1 总条数，总页数，当前页数据
        PageDTO<VO> dto = new PageDTO<>();
        dto.setTotal(p.getTotal());
        dto.setPages(p.getPages());
        List<PO> records = p.getRecords();
        if(CollUtil.isEmpty(records)){
            dto.setList(Collections.emptyList());
            return dto;
        }
        //3.2 转vo
        List<VO> voList = records.stream().map(converter).collect(Collectors.toList());//手动转换
        dto.setList(voList);
        return dto;
    }
```

此时UserServiceImpl中对应的业务方法：

```java
    @Override
    public PageDTO<UserVO> queryUsersPage(UserQuery query) {
        String name = query.getName();
        Integer status = query.getStatus();
        //1 构建分页条件
        Page<User> page = query.toPageSortByUpdateTime();
        //2.分页查询
        Page<User> p = lambdaQuery()
                        .like(name != null, User::getUsername, name)
                        .eq(status != null, User::getStatus, status)
                        .page(page);
        //3.封装返回
		//return PageDTO.of(p, UserVO.class);
        return PageDTO.of(p,user -> { //自定义转换器
            //1.拷贝基础属性
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            //2.处理特殊逻辑
            vo.setUsername(vo.getUsername().substring(0,vo.getUsername().length()-2) + "**"); //隐藏用户名后两位

            return vo;
        });
    }
```



## 七、MyBatis-Plus 核心总结

1. MP是MyBatis的增强工具，**无侵入、高效率**，核心是简化单表CRUD开发

2. 基础使用：引入依赖 → Mapper继承BaseMapper → 实体类注解映射 → 配置文件自定义

3. 核心功能：条件构造器（Lambda版首选）、自定义SQL、Service层封装（IService）

4. 扩展功能：代码生成、静态工具Db、枚举/JSON处理器、配置加密、逻辑删除

5. 插件功能：基于MybatisPlusInterceptor实现，最常用**分页插件**，按需配置其他插件

6. 最佳实践：优先使用Lambda版构造器避免硬编码、Service层使用IService封装、分页使用分页插件、敏感信息加密存储
> （注：文档部分内容可能由 AI 生成）
