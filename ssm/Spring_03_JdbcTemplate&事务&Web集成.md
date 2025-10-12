**Spring_03:  JdbcTemplate&事务&Web集成**

**课程任务主要内容**

```markdown
- Spring 的 JdbcTemplate
* Spring 的事务
- Spring 集成 web 环境
```

## 一、Spring 的 JdbcTemplate

### 1.1 JdbcTemplate 是什么？

JdbcTemplate 是 Spring 框架中提供的一个模板对象，是对原始繁琐的 Jdbc API 对象的简单封装。

#### 核心对象

```java
JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSource dataSource);
```

#### 核心方法

```java
int update()：执行增、删、改语句
List<T> query()：查询多个结果
T queryForObject()：查询一个结果
new BeanPropertyRowMapper<>()：实现 ORM 映射封装
```

#### 举个栗子

查询数据库所有账户信息到 Account 实体中：

```java
public class JdbcTemplateTest {
    @Test
    public void testFindAll() throws Exception {
        // 创建核心对象
        JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());
        // 编写sql
        String sql = "select * from account";
        // 执行sql
        List<Account> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Account.class));
    }
}
```



### 1.2 Spring 整合 JdbcTemplate

#### 需求

基于 Spring 的 XML 配置实现账户的 CRUD 案例。

#### 步骤分析

```markdown
1. 创建 Java 项目，导入坐标
2. 编写 Account 实体类
3. 编写 AccountDao 接口和实现类
4. 编写 AccountService 接口和实现类
5. 编写 Spring 核心配置文件
6. 编写测试代码
```

#### （1）创建 Java 项目，导入坐标

```xml
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.47</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>1.1.15</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.1.2</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.8.13</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>6.1.2</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-tx</artifactId>
        <version>6.1.2</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>6.1.2</version>
    </dependency>
</dependencies>
```

#### （2）编写 Account 实体类

```java
public class Account {
    private Integer id;
    private String name;
    private Double money;

	//getter，setter,toString()...
}
```

#### （3）编写 AccountDao 接口和实现类

```java
public interface AccountDao {
    //查询所有账户
    public List<Account> findAll();
    //根据ID查询账户
    public Account findById(int id);
    //添加账户
    public void save(Account account);
    //更新账户
    public void update(Account account);
    //根据id删除账户
    public void deleteById(int id);
}
```

```java
@Repository
public class AccountDaoImpl implements AccountDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    //查询所有账户
    public List<Account> findAll() {
        String sql = "select * from account";
        List<Account> accountList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Account>(Account.class));

        return accountList;
    }

    @Override
    //根据ID查询账户
    public Account findById(int id) {
        String sql = "select * from account where id = ?";
        Account account = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Account>(Account.class), id);

        return account;
    }

    @Override
    //添加账户
    public void save(Account account) {
        String sql = "insert into account (name, money) values (?, ?)";
        jdbcTemplate.update(sql, account.getName(), account.getMoney());
    }

    @Override
    //修改账户
    public void update(Account account) {
        String sql = "update account set name = ?, money = ? where id = ?";
        jdbcTemplate.update(sql, account.getName(), account.getMoney(), account.getId());
    }

    @Override
    //根据ID删除账户
    public void deleteById(int id) {
        String sql = "delete from account where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
```

#### （4）编写 AccountService 接口和实现类

```java
public interface AccountService {
    //查询所有账户
    public List<Account> findAll();
    //根据ID查询账户
    public Account findById(int id);
    //添加账户
    public void save(Account account);
    //更新账户
    public void update(Account account);
    //根据id删除账户
    public void deleteById(int id);
}
```

```java
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    @Override
    public List<Account> findAll() {
        List<Account> accountList = accountDao.findAll();
        return accountList;
    }

    @Override
    public Account findById(int id) {
        Account account = accountDao.findById(id);
        return account;
    }

    @Override
    public void save(Account account) {
        accountDao.save(account);
    }

    @Override
    public void update(Account account) {
        accountDao.update(account);
    }

    @Override
    public void deleteById(int id) {
        accountDao.deleteById(id);
    }
}
```

#### （5）编写 Spring 核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

    <!--  注解扫描  -->
    <context:component-scan base-package="com.gvc"/>

    <!--  引入properties文件  -->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--  数据源dataSource  -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <!--  JdbcTemplate  -->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <constructor-arg name="dataSource" ref="dataSource"/>
    </bean>

</beans>
```

#### （6）编写测试代码

```java
@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test//测试添加
    public void testSaveAccount() {
        Account account = new Account();
        account.setName("Lucy");
        account.setMoney(1000d);
        accountService.save(account);
    }

    @Test//测试查询全部
    public void testFindAll() {
        List<Account> accountList = accountService.findAll();
        for (Account account : accountList) {
            System.out.println(account);
        }
    }

    @Test//测试根据ID查询
    public void testFindById() {
       Account account = accountService.findById(1);
       System.out.println(account);
    }

    @Test//测试更新
    public void testUpdate() {
        Account account = new Account();
        account.setId(1);
        account.setName("Tom");
        account.setMoney(1000d);
        accountService.update(account);
    }

    @Test//测试根据ID删除
    public void testDeleteById() {
        accountService.deleteById(3);
    }

}
```



### 1.3 实现转账案例

#### 步骤分析

```markdown
1. 创建 Java 项目，导入坐标
2. 编写 Account 实体类
3. 编写 AccountDao 接口和实现类
4. 编写 AccountService 接口和实现类
5. 编写 Spring 核心配置文件
6. 编写测试代码
```

####  （1）创建 Java 项目，导入坐标

```xml
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.47</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>1.1.15</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.1.2</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.8.13</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>6.1.2</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-tx</artifactId>
        <version>6.1.2</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>6.1.2</version>
    </dependency>
</dependencies>
```

####  （2）编写 Account 实体类

```java
public class Account {
    private Integer id;
    private String name;
    private Double money;
 
    //getter,setter,toString()...
}
```

####  （3）编写 AccountDao 接口和实现类

```java
public interface AccountDao {
    //转出
    public void out(String outUser,Double money);
    //转入
    public void in(String inUser,Double money);
}
```

```java
@Repository
public class AccountDaoImpl implements AccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void out(String outUser, Double money) {
        String sql = "update account set money = money-? where name = ?";
        jdbcTemplate.update(sql,money,outUser);
    }

    @Override
    public void in(String inUser, Double money) {
        String sql = "update account set money = money+? where name = ?";
        jdbcTemplate.update(sql,money,inUser);
    }
}
```

####  （4）编写 AccountService 接口和实现类

```java
public interface AccountService {
    //转账方法
    public void transfer(String inUser,String outUser,Double money);
}
```

```java
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;
    @Override
    public void transfer(String inUser, String outUser, Double money) {
        accountDao.out(outUser,money);
        int i = 1/0;
        accountDao.in(inUser,money);
    }
}
```

#### （5）编写 Spring 核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

    <!--  注解扫描  -->
    <context:component-scan base-package="com.gvc"/>
    <!--  引入properties  -->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--  dataSource  -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <!--  JdbcTemplate  -->
    <bean id="JdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <constructor-arg name="dataSource" ref="dataSource"/>
    </bean>

</beans>
```

####  （6）编写测试代码

```java
@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void testTransfer(){
        accountService.transfer("Jerry","Tom",100d);
    }
}
```



## 二、Spring 的事务

### 2.1 Spring 中的事务控制方式

Spring 的事务控制可分为**编程式事务控制**和**声明式事务控制**。

| 类型   | 说明                                                         |
| ------ | ------------------------------------------------------------ |
| 编程式 | 开发者直接将事务代码与业务代码耦合，实际开发中很少使用       |
| 声明式 | 开发者通过配置实现事务控制，业务代码与事务代码解耦，基于 AOP 思想实现，实际开发中常用 |



### 2.2 编程式事务控制相关对象 (了解)

#### 2.2.1 PlatformTransactionManager

PlatformTransactionManager 接口是 Spring 的事务管理器，提供常用的事务操作方法。

| 方法                                                         | 说明               |
| ------------------------------------------------------------ | ------------------ |
| TransactionStatus getTransaction(TransactionDefinition definition); | 获取事务的状态信息 |
| void commit(TransactionStatus status);                       | 提交事务           |
| void rollback(TransactionStatus status);                     | 回滚事务           |

**注意**：

```markdown
PlatformTransactionManager 是接口类型，不同 Dao 层技术对应不同实现类：

- Dao 层技术为 JdbcTemplate 或 MyBatis 时：`DataSourceTransactionManager`

- Dao 层技术为 Hibernate 时：`HibernateTransactionManager`

- Dao 层技术为 JPA 时：`JpaTransactionManager`
```



#### 2.2.2 TransactionDefinition

TransactionDefinition 接口提供事务的定义信息（事务隔离级别、事务传播行为等）。

| 方法                         | 说明               |
| ---------------------------- | ------------------ |
| int getIsolationLevel()      | 获得事务的隔离级别 |
| int getPropogationBehavior() | 获得事务的传播行为 |
| int getTimeout()             | 获得超时时间       |
| boolean isReadOnly()         | 是否只读           |

##### (1) 事务隔离级别

设置隔离级别可解决事务并发产生的脏读、不可重复读和虚读（幻读）问题

```markdown
- ISOLATION_DEFAULT               使用数据库默认级别

- ISOLATION_READ_UNCOMMITTED      读未提交

- ISOLATION_READ_COMMITTED        读已提交

- ISOLATION_REPEATABLE_READ       可重复读

- ISOLATION_SERIALIZABLE          串行化
```

如果不进行事务隔离，或者隔离级别设置得很低，就会发生以下三种经典的并发问题：

> - - ```markdown
>     ## 1. 脏读
>     
>     - * 定义：一个事务读到了另一个未提交事务修改的数据。
>     - * 危害：如果那个未提交的事务后来被回滚了，那么第一个事务读到的数据就是从未正式存在过的“脏”数据。
>     - * 例子：
>       - 事务A将账户余额从100元修改为200元（但尚未提交）。
>       - 此时事务B读取账户余额，读到了200元。
>       - 事务A因为某种原因（如后续操作失败）被回滚，余额恢复为100元。
>       - 那么事务B使用的200元就是无效的“脏数据”，基于这个数据所做的任何操作都是错误的。
>     ```
>
> - - ```markdown
>     ## 2. 不可重复读
>     
>     - * 定义：在同一个事务中，两次读取同一条数据，得到的结果不同。这是因为在两次读取之间，另一个已提交的事务修改了这条数据。
>     - * 危害：破坏了事务的隔离性，导致事务内的多次查询结果不一致，影响业务逻辑判断。
>     - * 例子：
>       - 事务A第一次读取账户余额为100元。
>       - 此时事务B将余额更新为150元并提交。
>       - 事务A再次读取余额，发现变成了150元。
>       - 事务A很困惑：“为什么同一个事务里，我两次读到的钱数不一样？”
>     ```
>
> - - ```markdown
>     ## 3. 幻读
>     
>     - * 定义：在同一个事务中，两次执行相同的查询，返回的结果集记录数不同。这是因为在两次查询之间，另一个已提交的事务插入或删除了符合查询条件的数据。
>     - * 危害：就像出现了幻觉一样，突然多了一些行或少了一些行。
>     - * 例子：
>       - 事务A查询年龄小于30岁的员工，得到10条记录。
>       - 此时事务B插入了一名25岁的新员工记录并提交。
>       - 事务A再次查询年龄小于30岁的员工，得到了11条记录。
>       - 对于事务A来说，这多出来的第11条记录就像“幻觉”一样。
>     ```

> | 隔离级别     | 脏读       | 不可重复读 | 幻读       | 并发性能 |
> | :----------- | :--------- | :--------- | :--------- | :------- |
> | **读未提交** | ❌ 可能发生 | ❌ 可能发生 | ❌ 可能发生 | 最高     |
> | **读已提交** | ✅ 避免     | ❌ 可能发生 | ❌ 可能发生 | 较高     |
> | **可重复读** | ✅ 避免     | ✅ 避免     | ❌ 可能发生 | 较低     |
> | **串行化**   | ✅ 避免     | ✅ 避免     | ✅ 避免     | 最低     |

> 1. **读未提交**：基本不隔离，性能最高，但什么并发问题都可能发生。除非对数据一致性完全没要求，否则极少使用。
> 2. **读已提交**：**最常用的默认级别**（如Oracle、PostgreSQL）。它只读取已提交的数据，解决了脏读问题，但无法避免不可重复读和幻读。
> 3. **可重复读**：**MySQL的默认级别**。它确保在同一个事务中多次读取同一数据的结果是一致的，解决了脏读和不可重复读。但理论上仍可能存在幻读（不过MySQL的InnoDB引擎通过“间隙锁”在很大程度上解决了幻读）。
> 4. **串行化**：最高级别的隔离。它通过强制事务串行执行（如同单线程）来避免所有问题。这是最安全但性能最差的级别，通常只在极端要求数据一致性且并发量不高的场景下使用。

------



##### (2) 事务传播行为

事务传播行为指一个业务方法被另一个业务方法调用时的事务控制方式。

| 参数          | 说明                                                         |
| ------------- | ------------------------------------------------------------ |
| **REQUIRED**  | 如果当前没有事务，新建事务；如果已存在事务，加入该事务（默认值，常用） |
| **SUPPORTS**  | 支持当前事务；如果当前没有事务，以非事务方式执行             |
| MANDATORY     | 使用当前事务；如果当前没有事务，抛出异常                     |
| REQUIRES_NEW  | 新建事务；如果当前在事务中，挂起当前事务                     |
| NOT_SUPPORTED | 以非事务方式执行；如果当前存在事务，挂起当前事务             |
| NEVER         | 以非事务方式运行；如果当前存在事务，抛出异常                 |
| NESTED        | 如果当前存在事务，在嵌套事务内执行；如果当前没有事务，执行与 REQUIRED 类似操作 |

![spring0301](https://github.com/gvc10233/note_images/blob/main/spring_img/spring0301.png)

```markdown
* read-only（是否只读）：建议查询时设置为只读，提高性能。

* timeout（超时时间）：默认值为 - 1，无超时限制；若设置，单位为秒。
```



#### 2.2.3 TransactionStatus

TransactionStatus 接口提供事务具体的运行状态。

| 方法                       | 说明         |
| -------------------------- | ------------ |
| boolean isNewTransaction() | 是否是新事务 |
| boolean hasSavepoint()     | 是否是回滚点 |
| boolean isRollbackOnly()   | 事务是否回滚 |
| boolean isCompleted()      | 事务是否完成 |

**三者关系**：**事务管理器**通过读取**事务定义参数**进行事务管理，生成**事务状态**。



#### 2.2.4 实现代码

##### (1) 配置文件

```xml
<!--事务管理器交给IOC-->
<bean id="transactionManager"
      class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>
```

##### (2) 业务层代码

```java
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public void transfer(String outUser, String inUser, Double money) {
        // 创建事务定义对象
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 设置是否只读，false支持事务
        def.setReadOnly(false);
        // 设置事务隔离级别，可重复读（MySQL默认级别）
        def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        // 设置事务传播行为，必须有事务
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 配置事务管理器
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            // 转账
            accountDao.out(outUser, money);
            accountDao.in(inUser, money);
            // 提交事务
            transactionManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            transactionManager.rollback(status);
        }
    }
} 
```



#### 2.2.5 知识小结

Spring 三个事务控制核心 API：

```markdown
* `PlatformTransactionManager`：负责事务管理（接口，子类实现具体逻辑）

* `TransactionDefinition`：定义事务参数（隔离级别、传播行为等）

* `TransactionStatus`：代表事务运行实时状态
```



### 2.3 基于 XML 的声明式事务控制【重点】

在 Spring 配置文件中声明式处理事务，替代代码式事务，底层基于 AOP 思想。

#### 声明式事务控制明确事项

- 核心业务代码（目标对象）：切入点
- 事务增强代码（Spring 提供事务管理器）：通知
- 切面配置：关联切入点与通知



#### 2.3.1 快速入门

**需求**

使用 Spring 声明式事务控制转账业务。

##### 步骤分析

```markdown
1. 引入 tx 命名空间

2. 事务管理器通知配置

3. 事务管理器 AOP 配置

4. 测试事务控制转账业务代码
```



##### (1) 引入 tx 命名空间

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">
</beans>
```

##### (2) 事务管理器通知配置

```xml
<!--  事务管理器对象  -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>
<!--  通知增强  -->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <!--  定义事务的属性  *表示任意方法都采用默认配置 -->
        <tx:attributes>
            <tx:method name="*"/>
        </tx:attributes>
    </tx:advice>
```

##### (3) 事务管理器 AOP 配置

```xml
<!--  AOP配置  -->
    <aop:config>
        <aop:advisor advice-ref="txAdvice" pointcut="execution(* com.gvc.service.impl.AccountServiceImpl.*(..))"/>
    </aop:config>
```

##### (4) 测试事务控制转账业务代码

```java
@Override
public void transfer(String outUser, String inUser, Double money) {
    accountDao.out(outUser, money);
    // 制造异常
    int i = 1 / 0;
    accountDao.in(inUser, money);
}
```



#### 2.3.2 事务参数的配置详解

```xml
<tx:method name="transfer" isolation="REPEATABLE_READ" propagation="REQUIRED"
           timeout="-1" read-only="false"/>

* name:           切点方法名称（支持通配符，如`save*`、`find*`）

* isolation:      事务的隔离级别

* propogation:    事务的传播行为

* timeout:        超时时间（单位：秒，-1 表示无限制）

* read-only:      是否只读（查询方法建议设为`true`，增删改设为`false`）
```



##### CRUD 常用配置

```xml
<!-- CRUD常用配置 -->
    <tx:method name="save*" propagation="REQUIRED"/>
    <tx:method name="delete*" propagation="REQUIRED"/>
    <tx:method name="update*" propagation="REQUIRED"/>
    <tx:method name="find*" read-only="true"/>
    <tx:method name="*"/>
```



#### 2.3.3 知识小结

```markdown
* 平台事务管理器配置

* 事务通知的配置

* 事务 AOP 织入的配置
```



### 2.4 基于注解的声明式事务控制【重点】

#### 2.4.1 常用注解

##### 步骤分析

```markdown
1. 修改 Service 层，增加事务注解
2. 修改 Spring 核心配置文件，开启事务注解支持
```

##### （1）修改 Service 层，增加事务注解

```java
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, readOnly = false, timeout = -1)
    public void transfer(String inUser, String outUser, Double money) {
        accountDao.out(outUser,money);
        int i = 1/0;
        accountDao.in(inUser,money);
    }
}
```

##### （2）修改 Spring 核心配置文件，开启事务注解支持

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--  注解扫描  -->
    <context:component-scan base-package="com.gvc"/>
    <!--  引入properties  -->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--  dataSource  -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <!--  JdbcTemplate  -->
    <bean id="JdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <constructor-arg name="dataSource" ref="dataSource"/>
    </bean>

    <!--  事务管理器对象  -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--  事务的注解支持  -->
    <tx:annotation-driven/>
</beans>
```



#### 2.4.2 纯注解

##### 核心配置类

```java
@Configuration //声明为核心配置类
@ComponentScan("com.gvc") //包扫描
@Import(DataSourceConfig.class)//导入其他配置类
@EnableTransactionManagement //事务注解驱动
public class SpringConfig {

    @Bean
    public JdbcTemplate getJdbcTemplate(@Autowired DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    @Bean
    public PlatformTransactionManager getTransactionManager(@Autowired DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }
}
```

##### 数据源配置类

```java
@PropertySource("classpath:jdbc.properties")
public class DataSourceConfig {

    @Value("${jdbc.driverClassName}")
    private String driverClassName;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    @Bean //将当前方法的返回值对象放进IOC容器中
    public DataSource getDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);

        return druidDataSource;
    }
}
```



#### 2.4.3 知识小结

```markdown
* 平台事务管理器配置（XML、注解方式）

* 事务通知的配置（`@Transactional`注解配置）

* 事务注解驱动的配置：`<tx:annotation-driven/>`、`@EnableTransactionManagement`
```





## 三、Spring 集成 web 环境

### 3.1 ApplicationContext 应用上下文获取方式

传统方式通过`new ClasspathXmlApplicationContext(spring配置文件)`获取应用上下文对象，但每次获取 Bean 时都需重复创建，会导致配置文件多次加载、应用上下文对象多次创建，效率低下。

#### 解决思路分析

```markdown
在 Web 项目中，可通过`ServletContextListener`监听 Web 应用启动：

   1. Web 应用启动时，加载 Spring 配置文件，创建`ApplicationContext`对象
   2. 将`ApplicationContext`对象存储到`ServletContext`域（最大域）
   3. 任意位置可从`ServletContext`域中获取`ApplicationContext`对象
```



### 3.2 Spring 提供获取应用上下文的工具

上面的分析不用手动实现，Spring提供了一个监听器***ContextLoaderListener***就是对上述功能的封
装，该监听器内部加载Spring配置文件，创建应用上下文对象，并存储到***ServletContext***域中，提供
了一个客户端工具***WebApplicationContextUtils***供使用者获得应用上下文对象。

**所以我们需要做的只有两件事:**

1. 在web.xml中配置*ContextLoaderListener*监听器(导入spring-web坐标)
2. 使用*WebApplicationContextUtils*获得应用上下文对象ApplicationContext



### 3.3 实现

#### （1) 导入 Spring 集成 Web 的坐标

```xml
	  <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-context</artifactId>
          <version>5.1.5.RELEASE</version>
      </dependency>
     
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-web</artifactId>
          <version>5.1.5.RELEASE</version>
      </dependency>
```

#### （2) 配置ContextLoaderListener监听器

```xml
<!--web.xml-->	
<!--  配置全局参数： 指定applicationContext.xml的路径  -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:applicationContext.xml</param-value>
    </context-param>

    <!-- Spring的监听器 contextLoaderListener-->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
```

#### （3) 通过工具获得应用上下文对象

```java
@WebServlet(name = "AccountServlet", urlPatterns = "/AccountServlet")
public class AccountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        Account account = webApplicationContext.getBean("account", Account.class);
        System.out.println(account);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
```


