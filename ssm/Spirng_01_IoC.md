[TOC]

## 一 Spring概述

### 1.1 Spring是什么

Spring是一个分层的Java SE/EE**全栈式轻量级**开源框架，
**full-stack（全栈式）**：对各种主流技术和框架都进行了整合，同时对三层架构都提供解决方案。
**轻量级**：轻量级和重量级的划分主要依据就是看它使用了多少服务，启动时需要加载的资源以及耦合度等等。

***Spring涵盖：***
**表现层**：SpringMVC
**持久层**：Spring JDBC Template
**业务层**：事务管理等企业级技术
<u>Spring能整合众多第三方框架（如Hibernate、MyBatis），已成为Java EE企业应用中最广泛使用的开源框架。</u>
**两大核心机制：**
***IOC***（Inverse of Control，控制反转）：把对象的创建权交给Spring
***AOP***（Aspect Oriented Programming，面向切面编程）：在不修改源码的情况下对方法进行增强。



### 1.2 Spring发展历程

****

> *** EJB**
>
> 1997 年，IBM提出了EJB 的思想
>
> 1998 年，SUN制定开发标准规范 EJB1.0
>
> 1999 年，EJB1.1 发布
>
> 2001 年，EJB2.0 发布
>
> 2003 年，EJB2.1 发布
>
> 2006 年，EJB3.0 发布
>
> *** Spring**
>
> **Rod Johnson（Spring 之父）**-- 改变Java世界的大师级人物
>
> 2002年编著《Expert one on one J2EE design and development》
>
> 指出Java EE和EJB组件框架中的主要缺陷，提出基于普通Java类依赖注入的简化解决方案。
>
> 2004年编著《Expert one-on-one J2EE Development without EJB》
>
> 阐述不使用EJB的Java EE开发方式（Spring雏形），同年4月，Spring 1.0正式发布
>
> 2006年10月，发布 Spring 2.0
>
> 2009年12月，发布 Spring 3.0
>
> 2013年12月，发布 Spring 4.0
>
> 2017年9月，发布 Spring 5.0 通用版（GA）



### 1.3 Spring优势

**（1）方便解耦，简化开发**

Spring作为容器，统一管理对象的创建和依赖关系。

**耦合度**指对象间的关联程度，高耦合会导致修改一个对象需同步修改其他对象，增加维护成本。Spring通过IoC降低耦合。

![spring0101](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0101.png)

**（2）AOP编程支持**

提供面向切面编程能力，方便实现程序进行权限拦截，运行监控等功能。

**（3）声明式事务支持**

通过配置完成事务的管理，无需手动编程。

**（4）方便测试，降低JavaEE API的使用**

集成JUnit4支持注解测试，封装JavaEE API，降低使用复杂度。

**（5）优秀框架集成**

兼容主流开源框架（如MyBatis、Hibernate），并提供直接支持。



### 1.4 Spring体系结构

![spring0102](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0102.png)



## 二 初识IOC

### 2.1 概述

**控制反转（Inverse Of Control）**不是技术，而是一种设计思想，旨在设计松耦合的程序。

**控制**：指Java中对象的控制权（创建、销毁）。

**反转**：对象控制权从 **开发者手动控制** 转为 **Spring容器控制**。

> 举个栗子
>
> *** 传统方式**：
>
> 需要UserDao实例时，开发者需手动创建：new UserDao();
>
> *** IOC方式**：
>
> 需要UserDao实例时，直接从Spring IOC容器获取，对象创建权交给Spring。

![spring0103](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0103.png)



### 2.2 自定义IOC容器

![spring0103](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0104.png)

#### 2.2.1 介绍

**需求**：实现Service层与Dao层代码解耦。

**步骤分析**：

1. 创建Java项目，导入自定义IOC相关依赖。
2. 编写Dao接口及其实现类。
3. 编写Service接口及其实现类。
4. 编写测试代码。

#### 2.2.2 实现 

**（1）创建java项目，导入自定义IOC相关坐标**

```xml
<dependencies>
 <dependency>
    <groupId>dom4j</groupId>
    <artifactId>dom4j</artifactId>
    <version>1.6.1</version>
  </dependency>
  <dependency>
    <groupId>jaxen</groupId>
    <artifactId>jaxen</artifactId>
    <version>1.1.6</version>
  </dependency>
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
  </dependency>
</dependencies>
```

**（2）编写Dao接口和实现类**

```java
public interface IUserDAO {
    public void save();
}
```

```java
public class UserDAOImpl implements IUserDAO {
    @Override
    public void save() {
        System.out.println("dao被调用了......保存成功");
    }
}
```

**（3）编写Service接口和实现类**

```java
public interface IUserService {
    public void save();
}
```

```java
public class UserServiceImpl implements IUserService {
    @Override
    public void save() throws ClassNotFoundException,InstantiationException,IllegalAccessException{ 
        //传统方法：对象直接new出来，存在编译期依赖--高耦合
        IUserDAO userDAO = new UserDAOImpl();
        userDAO.save();
    }
}
```

**（4）编写测试代码**

```java
public class SpringTest {

    @Test
    public void testMyIoC() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        IUserService userService = new UserServiceImpl();
        userService.save();
    }

}
```

**（5）问题**

*当前service对象和dao对象耦合度太高，而且每次new的都是一个新的对象，导致服务器压力过大。*

**解耦合的原则是编译期不依赖，而运行期依赖就行了。**

**（6） 编写beans.xml**

把所有需要创建对象的信息定义在配置文件中

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans>
    <!-- id: 标识； class: 要生成实例的类的全路径 -->
    <bean id="userDao" class="com.gvc.dao.impl.UserDAOImpl"></bean>
</beans>
```

**（7）编写BeanFactory工具类**

```java
public class BeanFactory {
    private static Map<String,Object> iocMap = new HashMap<String,Object>();

    //类加载时，初始化对象实例
    static {
        //1。读取配置文件
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");

        //2.使用dom4j解析xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
        //3.编写xpath表达式
            String xpath = "//bean";
        //4.获取所有的bean标签
            List<Element> list = document.selectNodes(xpath);
        //5.遍历并使用反射创建对象实例，存到Map集合（IoC容器）中
            for (Element element : list) {
                String id = element.attributeValue("id");
                //className: com.gvc.dao.impl.UserDAOImpl
                String className = element.attributeValue("class");
                //反射生成实例对象
                Object o = Class.forName(className).newInstance();
                //存至Map中 key: id   value：o
                iocMap.put(id,o);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getBean(String beanId) {
        return iocMap.get(beanId);
    }

}
```

**（8）修改UserServiceImpl实现类**

```java
public class UserServiceImpl implements IUserService {
    @Override
    public void save() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        IUserDAO userDao = (IUserDAO) BeanFactory.getBean("userDao");
        userDao.save();
    }
}
```

#### 2.2.3 知识小结

> 升级后的BeanFactory已具备简单Spring IOC容器的核心功能。
>
> **传统方式**：需要User实例时，开发者需手动创建：new User();
>
> **IOC方式**：需要User实例时，直接从Spring IOC容器获取，对象创建权交给Spring控制。
>
> **最终目标**：实现代码解耦。



## 三 Spring快速入门

### 3.1 介绍

**需求：** 借助Spring的IOC实现Service服务的应用代码解耦合

**步骤分析**

1. 创建Java项目，导入Spring开发基本坐标
2. 编写Dao接口和实现类
3. 创建Spring的核心配置文件
4. 在Spring配置文件中配置UserDaoImpl
5. 使用Spring相关API获得Bean实例

### 3.2 实现

**（1）创建Java项目，导入Spring开发基本坐标**

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.1.5.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>
</dependencies>
```

**（2）编写Dao接口和实现类**

```java
public interface IUserDao {
    public void save();
}
```

```java
public class UserDaoImpl implements IUserDao {
    @Override
    public void save() {
        System.out.println("dao被调用，保存成功...");
    }
}
```

**（3）创建Spring核心配置文件**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

</beans>
```

**（4）在Spring配置文件中配置UserDaoImpl**

```xml
<beans>    
    <!-- 在spring配置文件中配置UserDaoImpl
          id: 唯一标识
          class: 全路径 -->
    <bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl"></bean>
</beans>
```

**（5）使用Spring相关API获得Bean实例**

```java
public class SpringTest {
    @Test
    public void testQuickStart() {
        //获取到Spring上下文对象，借助上下文对象可获取IoC容器中的bean对象
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //使用上下文对象从容器中获取Bean对象
        IUserDao userDao = (IUserDao) context.getBean("userDao");
        userDao.save();
    }
}
```

![spring0105](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0105.png)

### 3.3 知识小结

**Spring的开发步骤：**

1. *导入依赖*
2. *创建Bean*
3. *创建applicationContext.xml*
4. *在配置文件中配置目标Bean*
5. *创建ApplicationContext对象，执行getBean方法获取实例*





## 四 Spring相关API

### 4.1 API继承体系介绍

Spring的API体系庞大，我们主要关注两个核心接口：**BeanFactory**、**ApplicationContext**

![spring0106](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0106.png)

### 4.2 BeanFactory

BeanFactory是IOC容器的核心接口，定义了IOC的基本功能。

**特点**：在第一次调用getBean()方法时创建对象实例。

```java
//核心接口，不会创建bean对象存到容器中
BeanFactory xmlBeanFactory = new XmlBeanFactory(new ClassPathResource("applicationContext.xml"));
//调用getBean（）时才真正创建Bean对象
IUserDao userDao = (IUserDao) xmlBeanFactory.getBean("userDao");
userDao.save();
```



### 4.3 ApplicationContext

代表应用上下文对象，用于获取Spring容器管理的Bean实例。

**特点**：Spring容器启动时加载并创建所有配置的Bean实例。



**常用实现类**

**1. ClassPathXmlApplicationContext**

> 从类路径加载XML配置文件。

```java
ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml"));
```

**2. FileSystemXmlApplicationContext**

> 从磁盘上文件系统路径加载XML配置文件。

**3. AnnotationConfigApplicationContext**

> 当使用注解配置容器对象时，需要使用此类来创建 spring 容器。它用来读取注解。



**常用方法**

**1. Object getBean(String name)**

> 根据Bean的id从容器中获得Bean实例，返回是Object，需要强转。

**2. <T> T getBean(Class<T> requiredType)**

> 根据类型从容器中匹配Bean实例，当容器中相同类型的Bean有多个时，则此方法会报错。

**3. <T> T getBean(String name, Class<T> requiredType)**

> 根据Bean的id和类型获得Bean实例，解决容器中相同类型Bean有多个情况。



### 4.4 知识小结

```java
ApplicationContext context = new ClassPathXmlApplicationContext("xml文件");
    context.getBean("beanId");
    context.getBean(BeanClass.class);
```



## 五 Spring配置文件

### 5.1 Bean标签基本配置

```xml
<bean id="" class=""></bean>
```

- 用于配置对象交由Spring容器管理
- **基本属性**：

- - **id**: Bean实例在容器中的唯一标识
  - **class**: Bean的全限定类名

- **注意**：默认调用无参构造函数，若无则创建失败



### 5.2 Bean标签范围配置

```xml
<bean id="" class="" scope=""></bean>
```

- **scope**属性取值及说明：

| 取值范围      | 说明                         |
| ------------- | ---------------------------- |
| **singleton** | 默认值，单例模式             |
| **prototype** | 多例模式                     |
| request       | WEB项目中，Bean存入request域 |
| session       | WEB项目中，Bean存入session域 |
| globalSession | Portlet环境中的全局session   |

> **1. scope="singleton"**
>
> 实例数量：1个
>
> 实例化时机：加载Spring配置文件时
>
> 生命周期：
>
> 创建：容器加载时创建
>
> 存活：容器存在期间一直存活
>
> 销毁：容器关闭时销毁

> **2. scope="prototype"**
>
> 实例数量：多个
>
> 实例化时机：调用getBean()方法时
>
> 生命周期：
>
> 创建：每次getBean()时创建
>
> 存活：只要在使用中就一直存活
>
> 销毁：由垃圾回收机制回收



### 5.3 Bean生命周期配置

```xml
<bean id="" class="" init-method="" destroy-method=""></bean>
```

- init-method: 指定初始化方法名
- destroy-method: 指定销毁方法名



### 5.4 Bean实例化三种方式

1. 无参**构造**方法实例化（默认），要求类必须有无参构造方法
2. 工厂**静态**方法实例化
3. 工厂**普通**方法实例化



#### 5.4.1 无参构造方法实例化

它会根据默认无参构造方法来创建类对象，如果bean中没有默认无参构造函数，将会创建失败

```xml
<bean id="userDao" class="com.gvc.dao.impl.UserDaoImpl"/>
```

#### 5.4.2 工厂静态方法实例化

**应用场景**

依赖的jar包中有个A类，A类中有个静态方法m1，m1方法的返回值是一个B对象。如果我们频繁使用B对象，此时我们可以将B对象的创建权交给spring的IOC容器，以后我们在使用B对象时，无需调用A类中的m1方法，直接从IOC容器获得。

```java
public class StaticFactoryBean {
    public static IUserDao createUserDao() {
        return new UserDaoImpl();
    }
}
```

```xml
<!--方式二：工厂静态方法-->
<bean id="userDao" class="com.gvc.factory.StaticFactoryBean" factory-method="createUserDao"/>
```

#### 5.4.3 工厂普通方法实例化

**应用场景**

依赖的jar包中有个A类，A类中有个普通方法m1，m1方法的返回值是一个B对象。如果我们频繁使用B对象，此时我们可以将B对象的创建权交给spring的IOC容器，以后我们在使用B对象时，无需调用A类中的m1方法，直接从IOC容器获得。

```java
public class DynamicFactoryBean {
    public IUserDao  createUserDao() {
        return new UserDaoImpl();
    }
}
```

```xml
<!--方式三：工厂普通方法-->
<bean id="dynamicFactoryBean" class="com.gvc.factory.DynamicFactoryBean"/>
<bean id="userDao" factory-bean="dynamicFactoryBean" factory-method="createUserDao"/>
```



### 5.5 Bean依赖注入概述

依赖注入**DI**（Dependency Injection）：它是Spring框架核心IOC的具体实现。

> 在编写程序时，通过控制反转，把对象的创建交给了Spring。但是代码中不可能出现没有依赖的情况，IOC解耦只是管理他们的依赖关系，但不会消除。例如：业务层仍会调用持久层的方法。

前述的业务层和持久层的依赖关系，在使用Spring之后，就让Spring来维护了。简单的说，就是**通过框架把持久层对象传入业务层**，而不用我们自己去获取。



### 5.6 Bean依赖注入方式

#### 5.6.1 构造方法

在UserServiceImpl中创建有参构造

```java
public class UserServiceImpl implements IUserService {
    //用于接收Spring注入进来的IUserDao对象
    private IUserDao userDao;

    public UserServiceImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void save() {
        //调用dao层
        userDao.save();
    }
}
```

配置Spring容器调用有参构造器进行注入

```xml
    <bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton" init-method="init" destroy-method="destroy"></bean>
    <!-- 配置UserService -->
    <bean id = "userService" class="com.gvc.service.impl.UserServiceImpl">
        <!-- 采用有参构造 index="0"：给构造方法第一个参数赋值
             name="userDao" 为 构造方法中的参数值 ；ref 为上句的 bean id-->
<!--        <constructor-arg index="0" type="com.gvc.dao.IUserDao" ref="userDao"></constructor-arg>-->
        <constructor-arg name="userDao" ref="userDao"></constructor-arg>
    </bean>
```

![spring0107](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0107.png)



#### 5.6.2 set方法

在UserServiceImpl中创建set方法

```java
public class UserServiceImpl implements IUserService {
    //用于接收Spring注入进来的IUserDao对象
    private IUserDao userDao;

    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void save() {
        //调用dao层
        userDao.save();
    }
}
```

配置Spring容器调用set方法进行注入

```xml
<!-- 方式一：无参构造 -->
    <bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton" init-method="init" destroy-method="destroy"></bean>
    <!-- 配置UserService -->
    <bean id = "userService" class="com.gvc.service.impl.UserServiceImpl">
        <!-- set方法完成依赖注入
             name="userDao" ：set方法名后的首字母小写 -->
        <property name="userDao" ref="userDao"/>
    </bean>
```



#### 5.6.3 P命名空间注入

P命名空间注入**本质也是set方法注入**，但配置更简洁：

引入P命名空间：

```xml
xmlns:p="http://www.springframework.org/schema/p"
```

修改注入方式：

```xml
    <!-- 方式一：无参构造 -->
    <bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton" init-method="init" destroy-method="destroy"></bean>
    <!-- 配置UserService -->
    <bean id = "userService" class="com.gvc.service.impl.UserServiceImpl"
          p:userDao-ref="userDao">
    
    </bean>
```



### 5.7 Bean依赖注入的数据类型

上面操作，都是注入Bean对象，除了对象的引用可以注入，普通数据类型和集合都可以在容器中进行注入。

**注入数据的三种数据类型**

1. **普通数据类型**
2. **引用数据类型**
3. **集合数据类型**

其中引用数据类型，此处就不再赘述了，之前的操作都是以UserDao对象的引用进行注入的。下面将以**set方法**注入为例，演示普通数据类型和集合数据类型的注入。

#### 5.7.1 注入普通数据类型

```java
public class UserDaoImpl implements IUserDao {
    private String username;
    private Integer age;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public void save() {
        System.out.println(username + " " + age);
        System.out.println("dao被调用，保存成功...");
    }
}
```

```xml
<bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton">
    <!-- ref: 引用数据类型注入
         value: 普通数据类型注入   -->
    <property name="username" value="嵇围城"/>
    <property name="age" value="23"/>
</bean>
```



#### 5.7.2 注入集合数据类型

**（1）List集合注入**

```java
public class UserDaoImpl implements IUserDao {
    //注入集合数据类型
    private List<Object> list;

    public void setList(List<Object> list) {
        this.list = list;
    }

    @Override
    public void save() {
        System.out.println(list);
        System.out.println("dao被调用，保存成功...");
    }
}
```

```xml
<!-- 配置user对象 -->
<bean id="user" class="com.gvc.domain.User">
    <property name="username" value="ggvxcc"></property>
    <property name="age" value="22"></property>
</bean>
<!-- 方式一：无参构造 -->
<bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton">
    <!-- 集合数据类型注入 -->
    <property name="list">
        <list>
            <value>AAA批发</value>
            <ref bean="user"></ref>
        </list>
    </property>
```



**（2）Set集合注入**

```java
public class UserDaoImpl implements IUserDao {
    //注入集合数据类型
    private List<Object> list;
    private Set<Object> set;

    public void setList(List<Object> list) {
        this.list = list;
    }

    public void setSet(Set<Object> set) {
        this.set = set;
    }
    
    @Override
    public void save() {
        System.out.println("list集合："+list);
        System.out.println("set集合："+set);
        System.out.println("dao被调用，保存成功...");
    }
}
```

```xml
<!-- 配置user对象 -->
<bean id="user" class="com.gvc.domain.User">
    <property name="username" value="ggvxcc"></property>
    <property name="age" value="22"></property>
</bean>

<bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton">
    <!-- list集合数据类型注入 -->
    <property name="list">
        <list>
            <value>AAA批发</value>
            <ref bean="user"></ref>
        </list>
    </property>

    <!-- set集合数据类型注入 -->
    <property name="set">
       <set>
           <value>BBB你好</value>
           <ref bean="user"></ref>
       </set>
    </property>

</bean>
```

**（3）Array数组注入**

```java
public class UserDaoImpl implements IUserDao {
    //注入集合数据类型
    private List<Object> list;
    private Set<Object> set;
    private Object[] array;

    public void setList(List<Object> list) {
        this.list = list;
    }

    public void setSet(Set<Object> set) {
        this.set = set;
    }

    public void setArray(Object[] array) {
        this.array = array;
    }

    @Override
    public void save() {
        System.out.println("list集合："+list);
        System.out.println("set集合："+set);
        System.out.println("Array数组："+ Arrays.toString(array));
        System.out.println("dao被调用，保存成功...");
    }
}
```

```xml
<!-- 配置user对象 -->
<bean id="user" class="com.gvc.domain.User">
    <property name="username" value="ggvxcc"></property>
    <property name="age" value="22"></property>
</bean>

<bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton">
    <!-- ref: 引用数据类型注入
         value: 普通数据类型注入   -->
    <property name="username" value="嵇围城"/>
    <property name="age" value="23"/>

    <!-- list集合数据类型注入 -->
    <property name="list">
        <list>
            <value>AAA批发</value>
            <ref bean="user"></ref>
        </list>
    </property>

    <!-- set集合数据类型注入 -->
    <property name="set">
       <set>
           <value>BBB你好</value>
           <ref bean="user"></ref>
       </set>
    </property>

    <!-- Array数组数据类型注入 -->
    <property name="array">
        <array>
            <value>CCC鼓掌</value>
            <ref bean="user"></ref>
        </array>
    </property>

</bean>
```

**（4）Map集合注入**

```java
public class UserDaoImpl implements IUserDao {
    //注入集合数据类型
    private List<Object> list;
    private Set<Object> set;
    private Object[] array;
    private Map<String, Object> map;

    public void setList(List<Object> list) {
        this.list = list;
    }

    public void setSet(Set<Object> set) {
        this.set = set;
    }

    public void setArray(Object[] array) {
        this.array = array;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public void save() {
        System.out.println("list集合："+list);
        System.out.println("set集合："+set);
        System.out.println("Array数组："+ Arrays.toString(array));
        System.out.println("Map集合："+map);
        System.out.println("dao被调用，保存成功...");
    }
}
```

```xml
<!-- 配置user对象 -->
<bean id="user" class="com.gvc.domain.User">
    <property name="username" value="ggvxcc"></property>
    <property name="age" value="22"></property>
</bean>

<bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton">
    <!-- ref: 引用数据类型注入
         value: 普通数据类型注入   -->
    <property name="username" value="嵇围城"/>
    <property name="age" value="23"/>

    <!-- list集合数据类型注入 -->
    <property name="list">
        <list>
            <value>AAA批发</value>
            <ref bean="user"></ref>
        </list>
    </property>

    <!-- set集合数据类型注入 -->
    <property name="set">
       <set>
           <value>BBB你好</value>
           <ref bean="user"></ref>
       </set>
    </property>

    <!-- Array数组数据类型注入 -->
    <property name="array">
        <array>
            <value>CCC鼓掌</value>
            <ref bean="user"></ref>
        </array>
    </property>

    <!-- Map集合数据类型注入 -->
    <property name="map">
       <map>
           <entry key="key1" value="DDD动态"></entry>
           <entry key="key2" value-ref="user"></entry>
       </map>
    </property>

</bean>
```

**（5）Properties集合注入**

```java
public class UserDaoImpl implements IUserDao {
    //注入集合数据类型
    private List<Object> list;
    private Set<Object> set;
    private Object[] array;
    private Map<String, Object> map;
    private Properties properties;

    public void setList(List<Object> list) {
        this.list = list;
    }

    public void setSet(Set<Object> set) {
        this.set = set;
    }

    public void setArray(Object[] array) {
        this.array = array;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void save() {
        System.out.println("list集合："+list);
        System.out.println("set集合："+set);
        System.out.println("Array数组："+ Arrays.toString(array));
        System.out.println("Map集合："+map);
        System.out.println("Properties："+properties);
        System.out.println("dao被调用，保存成功...");
    }
}
```

```xml
<!-- 配置user对象 -->
<bean id="user" class="com.gvc.domain.User">
    <property name="username" value="ggvxcc"></property>
    <property name="age" value="22"></property>
</bean>

<bean id ="userDao" class="com.gvc.dao.impl.UserDaoImpl" scope="singleton">
    <!-- ref: 引用数据类型注入
         value: 普通数据类型注入   -->
    <property name="username" value="嵇围城"/>
    <property name="age" value="23"/>

    <!-- list集合数据类型注入 -->
    <property name="list">
        <list>
            <value>AAA批发</value>
            <ref bean="user"></ref>
        </list>
    </property>

    <!-- set集合数据类型注入 -->
    <property name="set">
       <set>
           <value>BBB你好</value>
           <ref bean="user"></ref>
       </set>
    </property>

    <!-- Array数组数据类型注入 -->
    <property name="array">
        <array>
            <value>CCC鼓掌</value>
            <ref bean="user"></ref>
        </array>
    </property>

    <!-- Map集合数据类型注入 -->
    <property name="map">
       <map>
           <entry key="key1" value="DDD动态"></entry>
           <entry key="key2" value-ref="user"></entry>
       </map>
    </property>

    <!-- Properties数据类型注入 -->
    <property name="properties">
        <props>
            <prop key="key1">I am value1</prop>
            <prop key="key2">I am value2</prop>
            <prop key="key3">I am value3</prop>
        </props>
    </property>

</bean>
```



### 5.8 配置文件模块化

实际开发中，Spring的配置内容非常多，这就导致Spring配置很繁杂且体积很大，所以，可以将部分配置拆解到其他配置文件中，也就是所谓的配置文件模块化。

**（1）并列的多个配置文件**

```java
ClassPathXmlApplicationContext context =  new ClassPathXmlApplicationContext("applicationContext.xml","applicationContext-service.xml","applicationContext-dao.xml");
```

**（2）主从配置文件**

```xml
<import resource="classpath:applicationContext-user.xml"></import>
```

**注意**：

- 同一个xml中不能出现相同名称的bean，如果出现会报错
- 多个xml如果出现相同名称的bean，不会报错，但是后加载的会覆盖前加载的bean



### 5.9 知识小结

**Spring的重点配置**

<bean>标签：创建对象并放到spring的IOC容器
    id属性: 在容器中Bean实例的唯一标识，不允许重复
    class属性: 要实例化的Bean的全限定名
    scope属性: Bean的作用范围，常用是singleton(默认)和prototype

<constructor-arg>标签：属性注入
    name属性：属性名称
    value属性：注入的普通属性值
    ref属性：注入的对象引用值

<property>标签：属性注入
    name属性：属性名称
    value属性：注入的普通属性值
    ref属性：注入的对象引用值
    <list>
    <set>
    <array>

    <map>
    <props>

<import>标签: 导入其他的Spring的分文件

------



## 六  DbUtils（IOC实战）

### 6.1 DbUtils是什么？

DbUtils是Apache的一款用于简化Dao代码的工具类，它底层封装了JDBC技术。

***核心对象***

```java
QueryRunner queryRunner = new QueryRunner(DataSource dataSource);
```

***核心方法***

> - **int update();** 执行增删改语句
> - **T query();** 执行查询语句
> - **ResultSetHandler<T>** 接口：将结果集封装到实体对象
>

***举个栗子***

​	查询数据库所有账户信息到Account实体中

```java
public class DBUtilsTest{
    @Test
    public void findAllTest() throws Exception{
        //创建DBUtils工具类，传入连接池
        QueryRunner queryRunner = new QueryRunner(JdbcUtils.getDataSource());
        //编写sql
        String sql = "select * from account";
        //执行sql
        List<Account> list = queryRunner.query(sql,new BeanListHandler<Account>(Account.class));
        //打印结果
        for(Account account : list){
			system.out.println(account);
        }
    }
}
```



### 6.2 Spring的xml整合DbUtils

#### 6.2.1 介绍

**需求**：基于Spring的xml配置实现账户CRUD案例
*步骤分析*

> 1. 准备数据环境
> 2. 创建Java项目，导入坐标
> 3. 创建Account实体类
> 4. 编写AccountDao接口和实现类
> 5. 编写AccountService接口和实现类
> 6. 编写Spring配置文件
> 7. 编写测试代码



#### 6.2.2 实现

##### （1）准备数据环境

```sql
CREATE DATABASE `spring_db`;
 USE `spring_db`;
 CREATE TABLE `account` (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `name` varchar(32) DEFAULT NULL,
     `money` double DEFAULT NULL,
     PRIMARY KEY (`id`)
 ) ;
 insert  into `account`(`id`,`name`,`money`) values (1,'tom',1000),
 (2,'jerry',1000);
```

##### （2）创建java项目，导入坐标

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
        <version>1.1.9</version>
    </dependency>
    <dependency>
        <groupId>commons-dbutils</groupId>
        <artifactId>commons-dbutils</artifactId>
        <version>1.6</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.1.5.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>
</dependencies>
```

##### （3）编写Account实体类

```java
public class Account {
    private Integer id;
    private String name;
    private double money;

	//get,set,toString()...
}   
```

##### （4）编写AccountDao接口和实现类

```java
public interface AccountDao {
    public List<Account> findAll();
    public Account findById(Integer id);
    public void save(Account account);
    public void update(Account account);
    public void delete(Account account);
}
```

```java
public class AccountDaoImpl implements AccountDao {

    private QueryRunner queryRunner;

    public void setQueryRunner(QueryRunner queryRunner) {
        this.queryRunner = queryRunner;
    }

    @Override
    public List<Account> findAll() {
        //编写sql
        String sql = "select * from account";
        List<Account> queryList = null;
        try {
            queryList = queryRunner.query(sql, new BeanListHandler<Account>(Account.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return queryList;
    }

    @Override
    public Account findById(Integer id) {
        
        String sql = "select * from account where id = ?";
        Account account = null;
        try {
           account = queryRunner.query(sql, new BeanHandler<Account>(Account.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    @Override
    public void save(Account account) {

        String sql = "insert into account values (null,?,?)";
        try {
            queryRunner.update(sql,account.getName(),account.getMoney());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Account account) {
        String sql = "update account set name=?,money=? where id=?";
        try {
            queryRunner.update(sql,account.getName(),account.getMoney(),account.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Account account) {
        String sql = "delete from account where id = ?";
        try {
            queryRunner.update(sql,account.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
```



##### （5）编写AccountService接口和实现类

```java
public interface AccountService {
    public List<Account> findAll();
    public Account findById(Integer id);
    public void save(Account account);
    public void update(Account account);
    public void delete(Account account);
}
```

```java
public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public List<Account> findAll() {
        return accountDao.findAll();
    }

    @Override
    public Account findById(Integer id) {
        return accountDao.findById(id);
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
    public void delete(Account account) {
        accountDao.delete(account);
    }
}
```

##### （6）编写spring核心配置文件  applicationContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- dataSource 有参构造注入 queryRunner-->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql:///spring_db"/>
        <property name="username" value="root"/>
        <property name="password" value="0709"/>
    </bean>
    <!-- queryRunner先声明才可完成注入 AccountDao-->
    <bean id="queryRunner" class="org.apache.commons.dbutils.QueryRunner">
        <constructor-arg name="ds" ref="dataSource"/>
    </bean>
    <!-- AccountDao 注入给 AccountService-->
    <bean id="accountDao" class="com.gvc.dao.impl.AccountDaoImpl">
        <property name="queryRunner" ref="queryRunner"/>
    </bean>

    <!-- AccountService -->
    <bean id="accountService" class="com.gvc.service.impl.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
    </bean>

</beans>
```

##### （7）编写测试代码

```java
public class AccountServiceTest {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    AccountService accountService = (AccountService) context.getBean("accountService");

    @Test
    //测试添加
    public void testSave() {
        Account account = new Account();
        account.setName("Lucy");
        account.setMoney(888d);

        accountService.save(account);
    }

    @Test
    //测试根据Id查询
    public void testfindById() {
        Account account = accountService.findById(3);
        System.out.println(account);
    }

    @Test
    //测试查询所有
    public void testfindAll() {
        List<Account> accountList = accountService.findAll();
        for (Account account : accountList) {
            System.out.println(account);
        }
    }

    @Test
    //测试修改
    public void testUpdate() {
        Account account = new Account();
        account.setId(4);
        account.setName("David");
        account.setMoney(999d);

        accountService.update(account);
    }

    @Test
    //测试删除
    public void testDelete() {
        accountService.delete(4);
    }
}

```

##### （8）抽取jdbc配置文件

applicationContext.xml加载jdbc.properties配置文件获得连接信息。 首先，需要引入context命名空间和约束路径：

```xml
<!-- 命名空间 -->
xmlns:context="http://www.springframework.org/schema/context"
<!-- 约束路径 -->
 http://www.springframework.org/schema/context
 http://www.springframework.org/schema/context/spring-context.xsd
```

```xml
 <!-- 引入命名空间 -->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!-- dataSource 有参构造注入 queryRunner-->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
```



### 6.3 知识小结

```xml
* DataSource的创建权交由Spring容器去完成

* QueryRunner的创建权交由Spring容器去完成，使用构造方法传递DataSource

* Spring容器加载properties文件
    <context:property-placeholder location="xx.properties"/>
    <property name="" value="{{key}}"/>
```



## 七 Spring注解开发

Spring是轻代码而重配置的框架，配置比较繁重，影响开发效率，所以注解开发是一种趋势，注解代
码和配置文件可以简化配置，提高开发效率。

### 7.1 Spring常用注解

#### 7.1.1 介绍

Spring常用注解主要是替代 <bean> 的配置

| 注解           | 说明                                          |
| -------------- | :-------------------------------------------- |
| @Component     | 使用在类上用于实例化Bean                      |
| @Controller    | 使用在web层类上用于实例化Bean                 |
| @Service       | 使用在service层类上用于实例化Bean             |
| @Repository    | 使用在dao层类上用于实例化Bean                 |
| @Autowired     | 使用在字段上用于根据类型依赖注入              |
| @Qualifier     | 结合@Autowired一起使用,根据名称进行依赖注入   |
| @Resource      | 相当于@Autowired+@Qualifier，按照名称进行注入 |
| @Value         | 注入普通属性                                  |
| @Scope         | 标注Bean的作用范围                            |
| @PostConstruct | 使用在方法上标注该方法是Bean的初始化方法      |
| @PreDestroy    | 使用在方法上标注该方法是Bean的销毁方法        |

![spring0108](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0108.png)

**说明：**

JDK11以后完全移除了javax扩展导致不能使用@resource注解

需要maven引入依赖

```xml
<dependency>
    <groupId> javax.annotation</groupId>
    <artifactId> javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```

**注意**

使用注解进行开发时，需要在applicationContext.xml中配置组件扫描，作用是指源命令以及其子包
下的Bean需要进行扫描以便识别使用注解配置的类、字段和方法。

```xml
<!--注解创建并扫描-->
<context:component-scan base-package="com.gvc"/>
```



#### 7.1.2 实现

##### （1） Bean实例化 (IOC)

```xml
<bean id="accountDao" class="com.gvc.dao.impl.AccountDaoImpl"></bean>
```

使用@Component或@Repository标识AccountDaoImpl需要Spring进行实例化。

```java
// @Component(value = "accountDao")
@Repository // 如果没有写value属性值，Bean的id为：类名首字母小写
public class AccountDaoImpl implements AccountDao {
    //...
}
```



##### （2）属性依赖注入（DI）

```xml
	<!-- AccountService -->
    <bean id="accountService" class="com.gvc.service.impl.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
    </bean>
```

使用@Autowired或者@Autowired+@Qualifer或者@Resource进行AccountDao的注入

```java
@Service("accountService") //相当于配置了bean标签  id属性  class属性（已知）
public class AccountServiceImpl implements AccountService {
    @Qualifier("accountDao")
    @Autowired
    private AccountDao aDao;
 	//...   
}
```



##### （3）@Value

使用@Value进行字符串的注入，结合SPEL表达式获得配置参数

```java
@Service("accountService") //相当于配置了bean标签  id属性  class属性（已知）
public class AccountServiceImpl implements AccountService {
    @Value("注入普通属性")
    private String str;
    @Value("${jdbc.driverClassName}")
    private String driver;
    //...
}
```



##### （4）@Scope

```xml
<bean scope="" />
```

使用@Scope标注Bean的范围

```java
@Service("accountService") //相当于配置了bean标签  id属性  class属性（已知）
@Scope("prototype")
public class AccountServiceImpl implements AccountService {
	//...
}
```



##### （5）Bean生命周期

```xml
<bean init-method="init" destroy-method="destory" />
```

使用@PostConstruct标注初始化方法，使用@PreDestroy标注销毁方法

```java
    @PostConstruct
    public void init(){
        System.out.println("初始化方法...");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁方法...");
    }
```





### 7.2 Spring常用注解整合DbUtils

#### 步骤分析
> 1. 将xml配置项目，改为注解配置项目  
> 2. 修改AccountDaoImpl实现类  
> 3. 修改AccountServiceImpl实现类  
> 4. 修改Spring核心配置文件  
> 5. 编写测试代码  
>

---

#### （1）拷贝xml配置项目，改为常用注解配置项目  
​		过程描述略...  

#### （2）修改AccountDaoImpl实现类  
```java
@Repository //相当于配置了bean标签
public class AccountDaoImpl implements AccountDao {
    @Autowired
    private QueryRunner queryRunner;
    //...
}
```

#### （3）修改AccountServiceImpl实现类

java

复制

```java
@Service  
public class AccountServiceImpl implements AccountService {  
    @Autowired  
    private AccountDao accountDao;  
    ...  
}  
```

------

#### （4）修改Spring核心配置文件



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

<!-- 注解的组件扫描 -->  
<context:component-scan base-package="com.lagou"></context:component-scan>  

<!-- 加载jdbc配置文件 -->
<context:property-placeholder location="classpath:jdbc.properties"/>

<!-- 配置数据库连接池 -->
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">  
    <property name="driverClassName" value="${jdbc.driver}"/>  
    <property name="url" value="${jdbc.url}"/>  
    <property name="username" value="${jdbc.username}"/>  
    <property name="password" value="${jdbc.password}"/>  
</bean>  

<!-- 配置QueryRunner -->
<bean id="queryRunner" class="org.apache.commons.dbutils.QueryRunner">  
    <constructor-arg name="ds" ref="dataSource"/>  
</bean>  
</beans>  
```



#### （5）编写测试代码

```java
	@Test
    //测试查询所有
    public void testfindAll() {
        List<Account> accountList = accountService.findAll();
        for (Account account : accountList) {
            System.out.println(account);
        }
    }
```



### 7.3 Spring新注解

使用上面的注释还不能全部替代xml配置文件，还需要使用注释替代的配置如下：
```xml
- 非自定义的Bean的配置 `<bean>`  
    
- 加载properties文件的配置 `<context:property-placeholder>`  
    
- 组件扫描的配置 `<context:component-scan>`  
    
- 引入其他文件 `<import>` 
```

 

| 注解              | 说明                                                         |
| ----------------- | ------------------------------------------------------------ |
| `@Configuration`  | 用于指定当前类是一个Spring配置类，当创建容器时会从该类上加载注解 |
| `@Bean`           | 使用在方法上，标注将该方法的返回值存储到Spring容器中         |
| `@PropertySource` | 用于加载properties文件中的配置                               |
| `@ComponentScan`  | 用于指定Spring在初始化容器时要扫描的包                       |
| `@Import`         | 用于导入其他配置类                                           |

---

![spring0109](C:\D_Data\CommonFiles\Images\学习相关\ssm\spring0109.png)

### 7.4 Spring纯注解整合DbUtils

#### 步骤分析  

1. 编写Spring核心配置类  
2. 编写数据库配置信息类  
3. 编写测试代码  

---

#### （1）编写Spring核心配置类  
```java
@Configuration
@ComponentScan("com.gvc")
@Import(DataSourceConfig.class)
public class SpringConfig {
    
    @Bean("queryRunner")
    public QueryRunner getQueryRunner(@Autowired DataSource dataSource){
        return new QueryRunner(dataSource);
    }

}
```



#### （2）编写数据库配置信息类

```java
@PropertySource("classpath:jdbc.properties")
public class DataSourceConfig {

    @Value("${jdbc.driverClassName}")
    private String driver;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    @Bean("dataSource")
    public DataSource getDateSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);

        return druidDataSource;
    }
}
```



#### （3）编写测试代码

```java
public class AccountServiceTest {
    
    //当前为纯注解形式
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
    AccountService accountService = (AccountService) context.getBean("accountService");
    
    @Test
    //测试查询所有
    public void testfindAll() {
        List<Account> accountList = accountService.findAll();
        for (Account account : accountList) {
            System.out.println(account);
        }
    }
}
```





## 八、Spring整合Junit

### 8.1 普通junit测试问题

在普通的测试中，需要开发并手动加载配置文件并创建Spring容器。然后通过Spring相关API获得Bean实例；如果不这么做，那么无法从容器中获取对象。

```java
ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
AccountService accountService = applicationContext.getBean(AccountService.class);
```

我们可以让SpringJunit负责创建Spring容器来简化这个操作，开发者可以直接在测试类注入Bean实例；但是需要将配置文件的名称告诉它。



### 8.2 Spring整合junit

#### 步骤分析

1. 导入Spring集成Junit的坐标
2. 使用@Runwith注册替换原来的运行器
3. 使用@ContextConfiguration指定配置文件或配置类
4. 创建@Autowired注入需要测试的对象

------

#### （1） 导入spring集成junit的坐标

```xml
 <!--此处需要注意的是，spring5 及以上版本要求 junit 的版本必须是 4.12 及以上-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test</artifactId>
      <version>5.1.5.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
    </dependency>
```



#### （2）使用@Runwith注册替换原来的运行器

```java
@RunWith(SpringJUnit4ClassRunner.class)//@RunWith指定JUnit的运行环境，SpringJUnit4ClassRunner是Spring提供的作为JUnit运行环境的一个类
public class AccountServiceTest {
	//...
}
```



#### （3）使用@Context Configuration指定配置文件或配置类

```java
@RunWith(SpringJUnit4ClassRunner.class) //@RunWith指定JUnit的运行环境，SpringJUnit4ClassRunner是Spring提供的作为JUnit运行环境的一个类
@ContextConfiguration(classes = SpringConfig.class) //加载Spring核心配置类
public class AccountServiceTest {
    //...
}
```



#### （4）使用@Autowired注入需要测试的对象

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringConfig.class}) 
public class SpringJunitTest {
    @Autowired 
    private AccountService accountService;
}
```



















​        







