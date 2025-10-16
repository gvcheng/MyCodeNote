**springMVC_03：SSM 框架整合**

任务目标

```markdown
* 实现 SSM 框架整合
```



# SSM 框架整合



## 1.1 需求和步骤分析

### 需求

使用 SSM 框架完成对`account`表的增删改查操作。

### 步骤分析

```markdown
1. 准备数据库和表记录

2. 创建 Web 项目

3. 编写 MyBatis 在 SSM 环境中可以单独使用

4. 编写 Spring 在 SSM 环境中可以单独使用

5. Spring 整合 MyBatis

6. 编写 SpringMVC 在 SSM 环境中可以单独使用

7. Spring 整合 SpringMVC
```



## 1.2 环境搭建

### (1) 准备数据库和表记录

```sql
CREATE TABLE `account` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(32) DEFAULT NULL,
`money` double DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

insert into `account`(`id`,`name`,`money`) values (1,'tom',1000), (2,'jerry',1000);
```

### (2) 创建 Web 项目

> ![0301](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0301.png)



## 1.3 编写 MyBatis 在 SSM 环境中可以单独使用

### 需求

基于 MyBatis 实现对`account`表的查询

### (1) 相关坐标

```xml
<!--MyBatis相关坐标-->
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
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.1</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```

### (2) Account 实体

```java
public class Account {
    private Integer id;
    private String name;
    private Double money;
	//getter,setter,toString()
}
```

### (3) AccountDao 接口

```java
public interface AccountDao {
    public List<Account> findAll();
}
```

### (4) AccountDao.xml 映射

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.lagou.dao.AccountDao">
    <select id="findAll" resultType="Account">
        select * from account
    </select>
</mapper>
```

### (5) MyBatis 核心配置文件

#### jdbc.properties

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!--加载properties配置文件-->
    <properties resource="jdbc.properties"/>
    
    <!--类型别名配置-->
    <typeAliases>
        <package name="com.lagou.domain"/>
    </typeAliases>
    
    <!--环境配置-->
    <environments default="mysql">
        <!--使用MySQL环境-->
        <environment id="mysql">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>
    
    <!--加载映射文件-->
    <mappers>
        <package name="com.lagou.dao"/>
    </mappers>
</configuration>
```

#### SqlMapConfig.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!--加载properties配置文件-->
    <properties resource="jdbc.properties"/>
    
    <!--类型别名配置-->
    <typeAliases>
        <package name="com.lagou.domain"/>
    </typeAliases>
    
    <!--环境配置-->
    <environments default="mysql">
        <!--使用MySQL环境-->
        <environment id="mysql">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>
    
    <!--加载映射文件-->
    <mappers>
        <package name="com.lagou.dao"/>
    </mappers>
</configuration>
```

### (6) 测试代码

```java
public class MyBatisTest {
    @Test
    public void testMybatis() throws Exception {
        // 加载核心配置文件
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 获得SqlSession工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
        // 获得SqlSession会话对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 获得Mapper代理对象
        AccountDao accountDao = sqlSession.getMapper(AccountDao.class);
        // 执行查询操作
        List<Account> list = accountDao.findAll();
        for (Account account : list) {
            System.out.println(account);
        }
        // 释放资源
        sqlSession.close();
    }
}
```



## 1.4 编写 Spring 在 SSM 环境中可以单独使用

### (1) 相关坐标

```xml
<!--Spring相关坐标-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.8.13</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>
```

### (2) AccountService 接口

```java
public interface AccountService {
    public List<Account> findAll();
}
```

### (3) AccountServiceImpl 实现

```java
@Service
public class AccountServiceImpl implements AccountService {
    @Override
    public List<Account> findAll() {
        System.out.println("findAll执行了....");
        return null;
    }
}
```

### (4) Spring 核心配置文件（applicationContext.xml）

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
    
    <!--注解组件扫描（只扫描Service层）-->
    <context:component-scan base-package="com.lagou.service"/>
</beans>
```

### (5) 测试代码

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringTest {
    @Autowired
    private AccountService accountService;
    
    @Test
    public void testSpring() throws Exception {
        List<Account> list = accountService.findAll();
        System.out.println(list);
    }
}
```



## 1.5 Spring 整合 MyBatis

### (1) 整合思想

将 MyBatis 接口代理对象的创建权交给 Spring 管理，可将 DAO 的代理对象注入到 Service 中，完成 Spring 与 MyBatis 的整合。

### (2) 导入整合包

```xml
<!--MyBatis整合Spring坐标-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>1.3.1</version>
</dependency>
```

### (3) Spring 配置文件管理 MyBatis（修改 applicationContext.xml）

> 注意：此时可删除 MyBatis 主配置文件（SqlMapConfig.xml）

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
    
    <!--注解组件扫描（扫描Service层）-->
    <context:component-scan base-package="com.lagou.service"/>
    
    <!--Spring整合MyBatis相关配置-->
    <!--加载jdbc.properties配置文件-->
    <context:property-placeholder location="classpath:jdbc.properties"/>
    
    <!--配置数据源（Druid连接池）-->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driver}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
    
    <!--将SqlSessionFactory的创建交给Spring的IOC容器-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!--注入数据源-->
        <property name="dataSource" ref="dataSource"/>
        <!--配置类型别名-->
        <property name="typeAliasesPackage" value="com.lagou.domain"/>
        <!--若需引入MyBatis主配置文件，可添加如下配置（可选）-->
        <!--<property name="configLocation" value="classpath:SqlMapConfig.xml"/>-->
    </bean>
    
    <!--映射接口扫描配置：由Spring创建DAO代理对象，并存入IOC容器-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com.lagou.dao"/>
    </bean>
</beans>
```

### (4) 修改 AccountServiceImpl（注入 DAO）

```java
@Service
public class AccountServiceImpl implements AccountService {
    // 注入AccountDao代理对象（由Spring创建）
    @Autowired
    private AccountDao accountDao;
    
    @Override
    public List<Account> findAll() {
        return accountDao.findAll();
    }
}
```



## 1.6 编写 SpringMVC 在 SSM 环境中可以单独使用

### 需求

访问 Controller 中的方法查询所有账户，并跳转到`list.jsp`页面进行列表展示

### (1) 相关坐标

```xml
<!--SpringMVC相关坐标-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>javax.servlet.jsp</groupId>
    <artifactId>jsp-api</artifactId>
    <version>2.2</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>jstl</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
```

### (2) 导入页面资源

> ![0302](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0302.png)

### (3) 前端控制器（DispatcherServlet）

在`webapp/WEB-INF/web.xml`中配置：

```xml
`<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    
    <!--1. 配置前端控制器DispatcherServlet-->
    <servlet>
        <servlet-name>DispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!--指定SpringMVC配置文件路径-->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring-mvc.xml</param-value>
        </init-param>
        <!--设置启动顺序（数字越小越先启动）-->
        <load-on-startup>2</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>DispatcherServlet</servlet-name>
        <!--拦截所有请求（除.jsp外）-->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    
    <!--2. 配置Post请求中文乱码过滤器-->
    <filter>
        <filter-name>CharacterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>CharacterEncodingFilter</filter-name>
        <!--拦截所有请求-->
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
```

### (4) AccountController 和 list.jsp

AccountController

```java
@Controller
@RequestMapping("/account")
public class AccountController {
    @RequestMapping("/findAll")
    public String findAll(Model model) {
        // 模拟数据（后续整合后从Service获取）
        List<Account> list = new ArrayList<>();
        list.add(new Account(1, "张三", 1000d));
        list.add(new Account(2, "李四", 1000d));
        // 将数据存入Model，传递到页面
        model.addAttribute("list", list);
        // 跳转到list.jsp页面
        return "list";
    }
}
```

list.jsp（核心列表展示）

```jsp
<c:forEach items="${list}" var="account">
    <tr>
        <td>
            <input type="checkbox" name="ids">
        </td>
        <td>${account.id}</td>
        <td>${account.name}</td>
        <td>${account.money}</td>
        <td>
            <a class="btn btn-default btn-sm" href="update.jsp">修改</a>&nbsp;
            <a class="btn btn-default btn-sm" href="">删除</a>
        </td>
    </tr>
</c:forEach>
```

### (5) SpringMVC 核心配置文件（spring-mvc.xml）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
    
    <!--1. 组件扫描（只扫描Controller层）-->
    <context:component-scan base-package="com.lagou.controller"/>
    
    <!--2. 开启MVC注解增强（支持@RequestMapping等注解）-->
    <mvc:annotation-driven/>
    
    <!--3. 配置视图解析器（InternalResourceViewResolver）-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <!--页面前缀-->
        <property name="prefix" value="/"/>
        <!--页面后缀-->
        <property name="suffix" value=".jsp"/>
    </bean>
    
    <!--4. 实现静态资源映射（解决静态资源被DispatcherServlet拦截问题）-->
    <mvc:default-servlet-handler/>
</beans>
```



## 1.7 Spring 整合 SpringMVC

### (1) 整合思想

Spring 和 SpringMVC 同属 Spring 生态，无需复杂整合，但需实现**Spring 与 Web 容器的整合**：让 Web 容器启动时自动加载 Spring 配置文件，Web 容器销毁时 Spring 的 IOC 容器也随之

### (2) Spring 和 Web 容器整合（配置监听器）

在`web.xml`中添加`ContextLoaderListener`监听器：

```xml
<!--Spring与Web容器整合：通过监听器加载Spring配置文件-->
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
<!--指定Spring配置文件路径-->
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
</context-param>
```

### (3) 修改 AccountController（注入 Service）

```java
@Controller
@RequestMapping("/account")
public class AccountController {
    // 注入AccountService（由Spring IOC容器管理）
    @Autowired
    private AccountService accountService;
    
    @RequestMapping("/findAll")
    public String findAll(Model model) {
        // 从Service获取真实数据（而非模拟数据）
        List<Account> list = accountService.findAll();
        model.addAttribute("list", list);
        return "list";
    }
}
```



## 1.8 Spring 配置声明式事务

### (1) Spring 配置文件加入声明式事务

修改 applicationContext.xml

```xml
<!--1. 配置事务管理器（DataSourceTransactionManager）-->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <!--注入数据源-->
    <property name="dataSource" ref="dataSource"></property>
</bean>

<!--2. 开启事务注解支持（支持@Transactional注解）-->
<tx:annotation-driven/>
```

修改 AccountServiceImpl（添加事务注解）

```java
@Service
@Transactional // 类级别的事务注解：该类所有方法均受事务管理
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;
    
    @Override
    public List<Account> findAll() {
        return accountDao.findAll();
    }
}
```

### (2) 新增账户：add.jsp

```jsp
<form action="${pageContext.request.contextPath}/account/save" method="post">
    <div class="form-group">
        <label for="name">姓名:</label>
        <input type="text" class="form-control" id="name" name="name" placeholder="请输入姓名">
    </div>
    <div class="form-group">
        <label for="age">余额:</label>
        <input type="text" class="form-control" id="age" name="money" placeholder="请输入余额">
    </div>
    <div class="form-group" style="text-align: center">
        <input class="btn btn-primary" type="submit" value="提交" />
        <input class="btn btn-default" type="reset" value="重置" />
        <input class="btn btn-default" type="button" onclick="history.go(-1)" value="返回" />
    </div>
</form>
```

### (3) 新增账户：AccountController

```java
@RequestMapping("/save")
public String save(Account account) {
    accountService.save(account);
    // 重定向到查询所有方法，避免表单重复提交
    return "redirect:/account/findAll";
}
```

### (4) 新增账户：AccountService 接口和实现类

AccountService 接口

```java
public void save(Account account);
```

AccountServiceImpl 实现类

```java
@Override
public void save(Account account) {
    accountDao.save(account);
}
```

### (5) 新增账户：AccountDao 接口和映射文件

AccountDao 接口

```java
void save(Account account);
```

AccountDao.xml 映射

```xml
<insert id="save" parameterType="Account">
    insert into account (name, money) values (#{name}, #{money})
</insert>
```



## 1.9 修改操作

### 1.9.1 数据回显（查询待修改账户）

#### (1) AccountController

```java
@RequestMapping("/findById")
public String findById(Integer id, Model model) {
    Account account = accountService.findById(id);
    // 将查询到的账户数据存入Model，用于页面回显
    model.addAttribute("account", account);
    // 跳转到update.jsp页面
    return "update";
}
```

#### (2) AccountService 接口和实现类

AccountService 接口

```java
Account findById(Integer id);
```

AccountServiceImpl 实现类

```java
@Override
public Account findById(Integer id) {
    return accountDao.findById(id);
}
```

#### (3)AccountDao 接口和映射文件

AccountDao 接口

```java
Account findById(Integer id);
```

AccountDao.xml 映射

```xml
<select id="findById" parameterType="int" resultType="Account">
    select * from account where id = #{id}
</select>
```

#### (4) update.jsp（数据回显页面）

```jsp
<form action="${pageContext.request.contextPath}/account/update" method="post">
    <!--隐藏域：传递账户ID（不可修改）-->
    <input type="hidden" name="id" value="${account.id}">
    
    <div class="form-group">
        <label for="name">姓名:</label>
        <input type="text" class="form-control" id="name" name="name" value="${account.name}" placeholder="请输入姓名">
    </div>
    <div class="form-group">
        <label for="money">余额:</label>
        <input type="text" class="form-control" id="money" name="money" value="${account.money}" placeholder="请输入余额">
    </div>
    <div class="form-group" style="text-align: center">
        <input class="btn btn-primary" type="submit" value="提交" />
        <input class="btn btn-default" type="reset" value="重置" />
        <input class="btn btn-default" type="button" onclick="history.go(-1)" value="返回" />
    </div>
</form>
```



### 1.9.2 账户更新（执行修改操作）

#### (1) AccountController

```java
@RequestMapping("/update")
public String update(Account account) {
    accountService.update(account);
    // 重定向到查询所有方法
    return "redirect:/account/findAll";
}
```

#### (2) AccountService 接口和实现类

```java
void update(Account account);
```

```java
@Override
public void update(Account account) {
    accountDao.update(account);
}
```

#### (3) AccountDao 接口和映射文件

```java
void update(Account account);
```

```xml
<update id="update" parameterType="Account">
    update account set name = #{name}, money = #{money} where id = #{id}
</update>
```



## 1.10 批量删除

### (1) list.jsp

```jsp
<!--批量删除按钮和表单-->
<button id="deleteBatchBtn" class="btn btn-danger">批量删除</button>
<form id="deleteBatchForm" action="${pageContext.request.contextPath}/account/deleteBatch" method="post">
    <!--用于存储选中的账户ID数组（由JS动态赋值）-->
</form>

<!--JS代码：实现全选/全不选和批量删除提交-->
<script>
    //实现全选全不选效果
    //现代事件绑定方式
    $('#checkAll').on('click', function() {
        // 选择所有name为"ids"的复选框，并将它们的选中状态设置为 和"全选"复选框的选中状态一致
        $('input[name="ids"]').prop('checked', $(this).prop('checked'));
    });
    
    // 批量删除按钮点击事件
    $('#deleteBatchBtn').click(function () {
        if (confirm('您确定要删除吗?')) {
            // 判断是否选中至少一个账户
            if ($('input[name="ids"]:checked').length > 0) {
                // 将选中的ID值添加到表单中
                $('input[name="ids"]:checked').each(function () {
                    $('#deleteBatchForm').append('<input type="hidden" name="ids" 						value="' + $(this).val() + '">');
                });
                // 提交表单
                $('#deleteBatchForm').submit();
            } else {
                alert('请至少选择一个账户！');
            }
        }
    });
</script>
```

### (2) AccountController

```java
@RequestMapping("/deleteBatch")
public String deleteBatch(Integer[] ids) {
    accountService.deleteBatch(ids);
    // 重定向到查询所有方法
    return "redirect:/account/findAll";
}
```

### (3) AccountService 接口和实现类

```java
void deleteBatch(Integer[] ids);
```

```java
@Override
public void deleteBatch(Integer[] ids) {
    accountDao.deleteBatch(ids);
}
```

### (4) AccountDao 接口和映射文件

```java
void deleteBatch(Integer[] ids);
```

```xml
<delete id="deleteBatch" parameterType="int">
    delete from account
    <where>
        <!--foreach标签：遍历ids数组，生成id in (?, ?, ...)语法-->
        <foreach collection="array" open="id in(" close=")" separator="," item="id">
            #{id}
        </foreach>
    </where>
</delete>
```




