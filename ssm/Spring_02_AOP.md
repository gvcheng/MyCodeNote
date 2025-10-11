**Spring_02：AOP**

```markdown
课程任务主要内容

- 转账案例
- Proxy 优化转账案例
- 初识 AOP
- 基于 XML 的 AOP 开发
- 基于注解的 AOP 开发
- AOP 优化转账案例
```



## 一、转账案例

**需求**

使用 Spring 框架整合 DBUtils 技术，实现用户转账功能。

### 1.1 基础功能

**步骤分析**

```markdown
1. 创建 Java 项目，导入坐标
2. 编写 Account 实体类
3. 编写 AccountDao 接口和实现类
4. 编写 AccountService 接口和实现类
5. 编写 Spring 核心配置文件
6. 编写测试代码
```



#### （1） 创建 Java 项目，导入坐标

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
            <groupId>commons-dbutils</groupId>
            <artifactId>commons-dbutils</artifactId>
            <version>1.6</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>6.1.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>6.1.2</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
```

#### （2）编写 Account 实体类

```java
public class Account {
    private Integer id;
    private String name;
    private Double money;
    // setter、getter方法
}
```

#### （3）编写 AccountDao 接口和实现类

```java
public interface AccountDao {
    // 转出操作
    public void out(String outUser, Double money);
    // 转入操作
    public void in(String inUser, Double money);
}

@Repository
public class AccountDaoImpl implements AccountDao {
    @Autowired
    private QueryRunner queryRunner;

    @Override
    public void out(String outUser, Double money) {
        try {
            queryRunner.update("update account set money=money-? where name=?", money, outUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void in(String inUser, Double money) {
        try {
            queryRunner.update("update account set money=money+? where name=?", money, inUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

#### （4）编写 AccountService 接口和实现类

```java
public interface AccountService {
    public void transfer(String outUser, String inUser, Double money);
}

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;

    @Override
    public void transfer(String outUser, String inUser, Double money) {
        accountDao.out(outUser, money);
        accountDao.in(inUser, money);
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
    <!--开启组件扫描-->
    <context:component-scan base-package="com.gvc"/>
    <!--加载jdbc配置文件-->
    <context:property-placeholder location="classpath:jdbc.properties"/>
    <!--把数据库连接池交给IOC容器-->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driver}"></property>
        <property name="url" value="${jdbc.url}"></property>
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value="${jdbc.password}"></property>
    </bean>
    <!--把QueryRunner交给IOC容器-->
    <bean id="queryRunner" class="org.apache.commons.dbutils.QueryRunner">
        <constructor-arg name="ds" ref="dataSource"></constructor-arg>
    </bean>
</beans>
```

#### （6）编写 测试代码

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void testTransfer() throws Exception {
        accountService.transfer("tom", "jerry", 100d);
    }
}
```

#### （7）问题分析

```markdown
上述代码中，事务在 DAO 层，转出、转入操作各自是一个独立的事务。但在实际开发中，应将业务逻辑控制在一个事务中，所以需将事务挪到 Service 层。
```



### 1.2 传统事务

**步骤分析**

```markdown
1. 编写线程绑定工具类
2. 编写事务管理器
3. 修改 Service 层代码
4. 修改 DAO 层代码
```

#### （1）编写线程绑定工具类

```java
/**
 * 连接工具类，从数据源中获取一个连接，并将其与线程绑定
 */
@Component
public class ConnectionUtils {
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    @Autowired
    private DataSource dataSource;

    /**
     * 获取当前线程上的连接
     * @return Connection
     */
    public Connection getThreadConnection() {
        // 1.先从ThreadLocal上获取
        Connection connection = threadLocal.get();
        // 2.判断当前线程是否有连接
        if (connection == null) {
            try {
                // 3.从数据源中获取一个连接，并存入到ThreadLocal中
                connection = dataSource.getConnection();
                threadLocal.set(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * 解除当前线程的连接绑定
     */
    public void removeThreadConnection() {
        threadLocal.remove();
    }
}
```

![spring0201](D:\Program\MyNotes\notes_image\spring0201.png)

#### （2）编写事务管理器

```java
/**
 * 事务管理器工具类，包含：开启事务、提交事务、回滚事务、释放资源
 */
@Component
public class TransactionManager {
    @Autowired
    private ConnectionUtils connectionUtils;

    public void beginTransaction() {
        try {
            connectionUtils.getThreadConnection().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            connectionUtils.getThreadConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            connectionUtils.getThreadConnection().rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            connectionUtils.getThreadConnection().setAutoCommit(true); // 改回自动提交事务
            connectionUtils.getThreadConnection().close();// 归还到连接池
            connectionUtils.removeThreadConnection();// 解除线程绑定
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

#### （3）修改service层代码

```java
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransactionManager transactionManager;

    @Override
    public void transfer(String outUser, String inUser, Double money) {
        try {
            // 1.开启事务
            transactionManager.beginTransaction();
            // 2.业务操作
            accountDao.out(outUser, money);
            int i = 1 / 0;
            accountDao.in(inUser, money);
            // 3.提交事务
            transactionManager.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 4.回滚事务
            transactionManager.rollback();
        } finally {
            // 5.释放资源
            transactionManager.release();
        }
    }
}
```

![spring0202](D:\Program\MyNotes\notes_image\spring0202.png)

#### （4）修改DAO层代码

```java
@Repository
public class AccountDaoImpl implements AccountDao {
    @Autowired
    private QueryRunner queryRunner;
    @Autowired
    private ConnectionUtils connectionUtils;

    @Override
    public void out(String outUser, Double money) {
        try {
            queryRunner.update(connectionUtils.getThreadConnection(), "update account set money=money-? where name=?", money, outUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void in(String inUser, Double money) {
        try {
            queryRunner.update(connectionUtils.getThreadConnection(), "update account set money=money+? where name=?", money, inUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

#### （5）问题分析

```markdown
上述代码通过对业务层改造，虽实现了事务控制，但也带来新问题：业务层方法变得臃肿，充斥大量重复代码，且业务层方法与事务控制方法耦合，违背面向对象开发思想。
```



## 二、Proxy 优化转账案例

可将业务代码和事务代码拆分，通过动态代理方式对业务方法进行事务增强，避免影响业务层，解决耦合问题。

### 常用的动态代理技术

- **JDK 代理**：基于接口的动态代理技术。利用拦截器（必须实现 InvocationHandler）加上反射机制生成一个代理接口的匿名类，在调用具体方法前调用 InvokeHandler 处理，实现方法增强。
- **Cglib 代理**：基于父类的动态代理技术。动态生成要代理的子类，子类重写要代理类的所有非 final 方法，在子类中采用方法拦截技术拦截所有父类方法的调用，织入横切逻辑，实现方法增强。

![spring0203](D:\Program\MyNotes\notes_image\Spring0203.png)

### 2.1 JDK 动态代理方式

#### (1) JDK 工厂类

```java
//JDK动态代理工厂类
@Component
public class JDKProxyFactory {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionManager transactionManager;
    /*
        采用jdk动态代理技术生成目标类的代理对象
        ClassLoader loader, :类加载器--借助被代理对象获取
        Class<?>[] interfaces, ：被代理类所需实现的接口
        InvocationHandler h ：代理对象调用接口任意方法，都会执行InvocationHandler内的invoke()方法
    */
    public AccountService createAccountServiceJDKProxy() {
        AccountService accountServiceProxy = (AccountService) Proxy.newProxyInstance(accountService.getClass().getClassLoader(), accountService.getClass().getInterfaces(), new InvocationHandler() {
            @Override           // proxy: 当前代理对象， method: 被调用的目标方法，args: 被调用目标方法用到的的参数
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                try{
                    //手动开启事务
                    transactionManager.beginTransaction();
                    //让被代理对象的原方法执行
                    result = method.invoke(accountService, args);
                    //手动提交事务
                    transactionManager.commitTransaction();
                }catch (Exception e){
                    e.printStackTrace();
                    //手动回滚事务
                    transactionManager.rollbackTransaction();
                }finally {
                    //手动释放资源
                    transactionManager.releaseTransaction();
                }
                return result;
            }
        });
        return accountServiceProxy;
    }

}
```

![](D:\Program\MyNotes\notes_image\spring0204.png)

#### 测试代码

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class AccountTest {
    @Autowired
    private JdkProxyFactory jdkProxyFactory;

    @Test
    public void testTransfer() throws Exception {
        AccountService accountServiceJdkProxy = jdkProxyFactory.createAccountServiceJdkProxy();
        accountServiceJdkProxy.transfer("tom", "jerry", 100d);
    }
}
```



#### (2) Cglib工厂类

```java
/*
* 采用Cglib动态代理对目标类（AccountServiceImpl）方法（transfer）进行动态增强（添加事务）
* */
@Component
public class CglibProxyFactory {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionManager transactionManager;

    public AccountService createAccountServiceCglibProxy(){
        /*
        编写Cglib对应的API返回proxy
        参数1：目标类的字节码对象
        参数2；动作类，当代理对象调用目标对象的原方法时，执行MethodInterceptor中的intercept()方法
        */
        AccountService accountServiceProxy = (AccountService) Enhancer.create(accountService.getClass(), new MethodInterceptor() {
            @Override           //obj: 生成的代理对象，method: 调用的目标方法，args:调用方法的入参，proxy: 代理方法
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                try {
                    transactionManager.beginTransaction();
                    method.invoke(accountService, args);
                    transactionManager.commitTransaction();
                }catch (Exception e){
                    e.printStackTrace();
                    transactionManager.rollbackTransaction();
                }finally {
                    transactionManager.releaseTransaction();
                }
                return null;
            }
        });

        return accountServiceProxy;
    }
}
```

#### 测试代码

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class AccountServiceTest {
    @Autowired
    private CglibProxyFactory cglibProxyFactory;

    @Test
    public void testTransfer() throws Exception {
        AccountService accountServiceCglibProxy = cglibProxyFactory.createAccountServiceCglibProxy();
        accountServiceCglibProxy.transfer("tom", "jerry", 100d);
    }
}
```





## 三、初识 AOP

### 3.1 什么是 AOP

AOP 为 Aspect Oriented Programming 的缩写，即**面向切面编程**。

AOP 是 OOP（面向对象编程）的延续，是软件开发的热点，也是 Spring 框架的重要内容。利用 AOP 可对业务逻辑各部分进行隔离，降低业务逻辑间的耦合度，提高程序可重用性与开发效率。

**好处**：

```markdown
1. 程序运行期间，无需修改源码即可对方法进行功能增强。
2. 逻辑清晰，开发核心业务时无需关注增强业务代码。
3. 减少重复代码，提高开发效率，便于后期维护。
```



### 3.2 AOP 底层实现

AOP 底层通过 Spring 提供的**动态代理技术**实现。运行期间，Spring 通过动态代理技术动态生成代理对象，代理对象方法执行时介入增强功能，再调用目标对象方法，完成功能增强。



### 3.3 AOP 相关术语

Spring 的 AOP 实现底层封装了动态代理代码，开发者只需编写关注部分代码，通过配置完成指定目标的方法增强。AOP 常用术语如下：

```markdown
- * Target（目标对象）：代理的目标对象。

- * Proxy（代理）：一个类被 AOP 织入增强后，产生的结果代理类。

- * Joinpoint（连接点）：可被拦截的点，Spring 中仅支持方法类型的连接点。

- * Pointcut（切入点）：定义要拦截的 Joinpoint。

- * Advice（通知 / 增强）：拦截到 Joinpoint 后执行的操作，分为前置通知、后置通知、异常通知、最终通知、环绕通知。

- * Aspect（切面）：切入点和通知（引介）的结合。

- * Weaving（织入）：将增强应用到目标对象创建新代理对象的过程，Spring 采用动态代理织入，AspectJ 采用编译期织入和类装载期织入。
```

![spring0205](D:\Program\MyNotes\notes_image\spring0205.png)

### 3.4 AOP 开发明确事项

#### 3.4.1 开发阶段（开发者操作）

1. 编写核心业务代码（目标类的目标方法）—— 切入点。
2. 抽取公用代码，制作成通知（增强功能方法）—— 通知。
3. 在配置文件中声明切入点与通知的关系，即切面。

#### 3.4.2 运行阶段（Spring 框架操作）

Spring 框架监控切入点方法执行，一旦监控到切入点方法运行，采用代理机制动态创建目标对象的代理对象，根据通知类别在代理对象对应位置织入通知功能，完成完整代码逻辑运行。

#### 3.4.3 底层代理实现

Spring 中，框架根据目标类是否实现接口选择动态代理方式：

- 当 Bean 实现接口时，采用 JDK 代理模式。
- 当 Bean 未实现接口时，采用 CGLIB 实现（可在 Spring 配置中加入`<aop:aspectj-autoproxy proxy-target-class="true"/>`强制使用 CGLIB）。



### 3.5 知识小结

```markdown
- AOP：面向切面编程。
- AOP 底层实现：基于 JDK 的动态代理和基于 CGLIB 的动态代理。
- AOP 重点概念：
 	 Pointcut（切入点）：真正被增强的方法。
 	 Advice（通知 / 增强）：封装增强业务逻辑的方法。
 	 Aspect（切面）：切点 + 通知。
 	 Weaving（织入）：将切点与通知结合，产生代理对象的过程。
```



## 四、基于 XML 的 AOP 开发

### 4.1 快速入门

#### 步骤分析

```markdown
1. 创建 Java 项目，导入 AOP 相关坐标。
2. 创建目标接口和目标实现类（定义切入点）。
3. 创建通知类及方法（定义通知）。
4. 将目标类和通知类对象创建权交给 Spring。
5. 在核心配置文件中配置织入关系及切面。
6. 编写测试代码。
```



#### 4.1.1 创建 Java 项目，导入 AOP 相关坐标

```xml
	<dependencies>
        <!--导入Spring的context坐标，context依赖AOP-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>6.1.2</version>
        </dependency>
        <!--AspectJ的织入（切点表达式需要用到该JAR包）-->
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.13</version>
        </dependency>
        <!--Spring整合JUnit-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>6.1.2</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
```

#### 4.1.2 创建目标接口和目标实现类

```java
public interface AccountService {
    public void transfer();
}

public class AccountServiceImpl implements AccountService {
    @Override
    public void transfer() {
        System.out.println("转账业务...");
    }
}
```

#### 4.1.3 创建通知类

```java
public class MyAdvice {
    public void before() {
        System.out.println("前置通知...");
    }
}
```

#### 4.1.4 将目标类和通知类对象创建权交给 Spring

```java
<!--目标类交给IOC容器-->
<bean id="accountService" class="com.gvc.service.impl.AccountServiceImpl"></bean>
<!--通知类交给IOC容器-->
<bean id="myAdvice" class="com.gvc.advice.MyAdvice"></bean>
```

#### 4.1.5 在核心配置文件中配置织入关系及切面

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd">
    <!--目标类交给IOC容器-->
    <bean id="accountService" class="com.gvc.service.impl.AccountServiceImpl"></bean>
    <!--通知类交给IOC容器-->
    <bean id="myAdvice" class="com.gvc.advice.MyAdvice"></bean>

    <aop:config>
        <!--引入通知类-->
        <aop:aspect ref="myAdvice">
            <!--配置目标类的transfer方法执行时，使用通知类的before方法进行前置增强-->
            <aop:before method="before"
                        pointcut="execution(public void com.gvc.service.impl.AccountServiceImpl.transfer())"></aop:before>
        </aop:aspect>
    </aop:config>
</beans>
```

#### 4.1.6 编写测试代码

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void testTransfer() throws Exception {
        accountService.transfer();
    }
}
```



### 4.2 XML 配置 AOP 详解

#### 4.2.1 切点表达式

**表达式语法**：

```java
execution([修饰符] 返回值类型 包名.类名.方法名(参数))
```

> - 访问修饰符可省略。
> - 返回值类型、包名、类名、方法名可用星号`*`代替，表示任意。
> - 包名与类名间一个点`.`代表当前包下的类，两个点`..`表示当前包及其子包下的类。
> - 参数列表可用两个点`..`表示任意个数、任意类型的参数列表。

```java
- execution(public void com.gvc.service.impl.AccountServiceImpl.transfer())
- execution(void com.gvc.service.impl.AccountServiceImpl.*(..))
- execution(* com.gvc.service.impl.*.*(..))
- execution(* com.gvc.service..*.*(..))
```

**切点表达式抽取**：当多个增强的切点表达式相同时，可抽取切点表达式，在增强中用`pointcut-ref`属性代替`pointcut`属性引用抽取后的切点表达式。

```xml
<aop:config>
    <!--抽取的切点表达式-->
    <aop:pointcut id="myPointcut" expression="execution(* com.gvc.service..*.*(..))"></aop:pointcut>
    <aop:aspect ref="myAdvice">
        <aop:before method="before" pointcut-ref="myPointcut"></aop:before>
    </aop:aspect>
</aop:config>
```



#### 4.2.2 通知类型

通知配置语法：

```xml
<aop:通知类型 method="通知类中方法名" pointcut="切点表达式"></aop:通知类型>
```

| 名称     | 标签                                     | 说明                                                 |
| -------- | ---------------------------------------- | ---------------------------------------------------- |
| 前置通知 | [aop:before](aop:before)                 | 配置前置通知，指定增强方法在切入点方法之前执行       |
| 后置通知 | [aop:afterReturning](aop:afterReturning) | 配置后置通知，指定增强方法在切入点方法之后执行       |
| 异常通知 | [aop:afterThrowing](aop:afterThrowing)   | 配置异常通知，指定增强方法在切入点方法出现异常后执行 |
| 最终通知 | [aop:after](aop:after)                   | 配置最终通知，无论切入点方法执行是否有异常，都会执行 |
| 环绕通知 | [aop:around](aop:around)                 | 配置环绕通知，开发者可手动控制增强代码执行时机       |

**注意**：通常情况下，环绕通知独立使用。



### 4.3 知识小结

```markdown
AOP 织入配置：
	<aop:config>
    	<aop:aspect ref="通知类">
        <aop:before method="通知方法名称" pointcut="切点表达式"></aop:before>
  	  	</aop:aspect>
	</aop:config>

 通知类型：前置通知、后置通知、异常通知、最终通知、环绕通知。
 切点表达式：`execution([修饰符] 返回值类型 包名.类名.方法名(参数))`
```





## 五、基于注解的 AOP 开发

### 5.1 快速入门

#### 步骤分析

```markdown
1. 创建 Java 项目，导入 AOP 相关坐标。
2. 创建目标接口和目标实现类（定义切入点）。
3. 创建通知类（定义通知）。
4. 将目标类和通知类对象创建权交给 Spring。
5. 在通知类中用注解配置织入关系，升级为切面类。
6. 在配置文件中开启组件扫描和 AOP 的自动代理。
7. 编写测试代码。
```



#### 5.1.1 创建 Java 项目，导入 AOP 相关坐标

```xml
<dependencies>
    <!--导入Spring的context坐标，context依赖AOP-->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.1.2</version>
    </dependency>
    <!--AspectJ的织入-->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.8.13</version>
    </dependency>
    <!--Spring整合JUnit-->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>6.1.2</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>
</dependencies>
```

#### 5.1.2 创建目标接口和目标实现类

```java
public interface AccountService {
    public void transfer();
}

public class AccountServiceImpl implements AccountService {
    @Override
    public void transfer() {
        System.out.println("转账业务...");
    }
}
```

#### 5.1.3 创建通知类

```java
public class MyAdvice {
    public void before() {
        System.out.println("前置通知...");
    }
}
```

#### 5.1.4 将目标类和通知类对象创建权交给Spring

```java
@Service
public class AccountServiceImpl implements AccountService {}

@Component
public class MyAdvice {}
```

#### 5.1.5 在通知中用注解配置织入关系，升级为切面类

```java
@Component
@Aspect
public class MyAdvice {
    @Before("execution(* com.gvc..*.*(..))")
    public void before() {
        System.out.println("前置通知...");
    }
}
```

#### 5.1.6 在配置文件中开启组件扫描和AOP的自动代理

```xml
<!--组件扫描-->
<context:component-scan base-package="com.gvc"/>
<!--AOP的自动代理-->
<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
```

#### 5.1.7 编写测试代码

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void testTransfer() throws Exception {
        accountService.transfer();
    }
}
```



### 5.2 注解配置 AOP 详解

#### 5.2.1 切点表达式

**切点表达式的抽取**：

```java
@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(* com.gvc..*.*(..))")
    public void myPoint() {}

    @Before("MyAdvice.myPoint()")
    public void before() {
        System.out.println("前置通知...");
    }
}
```



#### 5.2.2 通知类型

通知配置语法：

```java
`@通知注解("切点表达式")`
```

| 名称     | 标签            | 说明                                                 |
| -------- | --------------- | ---------------------------------------------------- |
| 前置通知 | @Before         | 配置前置通知，指定增强方法在切入点方法之前执行       |
| 后置通知 | @AfterReturning | 配置后置通知，指定增强方法在切入点方法之后执行       |
| 异常通知 | @AfterThrowing  | 配置异常通知，指定增强方法在切入点方法出现异常后执行 |
| 最终通知 | @After          | 配置最终通知，无论切入点方法执行是否有异常，都会执行 |
| 环绕通知 | @Around         | 配置环绕通知，开发者可手动控制增强代码执行时机       |



#### 5.2.3 纯注解配置

```java
@Configuration
@ComponentScan("com.gvc")
@EnableAspectJAutoProxy // 替代 <aop:aspectj-autoproxy />
public class SpringConfig {
}
```



### 5.3 知识小结

- ```markdown
  - 用`@Aspect`注解标注切面类。
  - 用`@Before`等注解标注通知方法。
  - 用`@Pointcut`注解抽取切点表达式。
  - 配置 AOP 自动代理：`<aop:aspectj-autoproxy/>`或`@EnableAspectJAutoProxy`。
  ```



## 六、AOP 优化转账案例

沿用前面的转账案例，删除两个代理工厂对象，采用 Spring 的 AOP 思想实现。

### 6.1 XML 配置实现

#### （1）配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">
    <!--开启组件扫描-->
    <context:component-scan base-package="com.gvc"/>
    <!--加载jdbc配置文件-->
    <context:property-placeholder location="classpath:jdbc.properties"/>
    <!--把数据库连接池交给IOC容器-->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driver}"></property>
        <property name="url" value="${jdbc.url}"></property>
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value="${jdbc.password}"></property>
    </bean>
    <!--把QueryRunner交给IOC容器-->
    <bean id="queryRunner" class="org.apache.commons.dbutils.QueryRunner">
        <constructor-arg name="ds" ref="dataSource"></constructor-arg>
    </bean>

    <!--AOP配置-->
    <aop:config>
        <!--切点表达式-->
        <aop:pointcut id="myPointcut" expression="execution(* com.gvc.service..*.*(..))"/>
        <!--切面配置-->
        <aop:aspect ref="transactionManager">
            <aop:before method="beginTransaction" pointcut-ref="myPointcut"/>
            <aop:after-returning method="commit" pointcut-ref="myPointcut"/>
            <aop:after-throwing method="rollback" pointcut-ref="myPointcut"/>
            <aop:after method="release" pointcut-ref="myPointcut"/>
        </aop:aspect>
    </aop:config>
</beans>
```

#### （2）事务管理器（通知）

```java
// 事务管理器工具类，包括：开启事务、提交事务、回滚事务、释放资源
@Component
public class TransactionManager {
    @Autowired
    ConnectionUtils connectionUtils;

    public void beginTransaction() {
        try {
            connectionUtils.getThreadConnection().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            connectionUtils.getThreadConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            connectionUtils.getThreadConnection().rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            connectionUtils.getThreadConnection().setAutoCommit(true);
            connectionUtils.getThreadConnection().close();
            connectionUtils.removeThreadConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```



### 6.2 注解配置实现

#### （1）配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">
    <!--开启组件扫描-->
    <context:component-scan base-package="com.gvc"/>
    <!--开启AOP注解支持-->
    <aop:aspectj-autoproxy/>
    <!--加载jdbc配置文件-->
    <context:property-placeholder location="classpath:jdbc.properties"/>
    <!--把数据库连接池交给IOC容器-->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driver}"></property>
        <property name="url" value="${jdbc.url}"></property>
        <property name="username" value="${jdbc.username}"></property>
        <property name="password" value="${jdbc.password}"></property>
    </bean>
    <!--把QueryRunner交给IOC容器-->
    <bean id="queryRunner" class="org.apache.commons.dbutils.QueryRunner">
        <constructor-arg name="ds" ref="dataSource"></constructor-arg>
    </bean>
</beans>
```



#### （2） 事务管理器（通知）

```java
@Component
@Aspect
public class TransactionManager {
    @Autowired
    ConnectionUtils connectionUtils;

    @Around("execution(* com.gvc.service..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) {
        Object object = null;
        try {
            // 开启事务
            connectionUtils.getThreadConnection().setAutoCommit(false);
            // 业务逻辑
            pjp.proceed();
            // 提交事务
            connectionUtils.getThreadConnection().commit();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            // 回滚事务
            try {
                connectionUtils.getThreadConnection().rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                connectionUtils.getThreadConnection().setAutoCommit(true);
                connectionUtils.getThreadConnection().close();
                connectionUtils.removeThreadConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return object;
    }
}
```



























