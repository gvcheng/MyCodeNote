# Spring Boot

课程主要内容

```markdown
1. Spring Boot 基本应用
2. Spring Boot 原理深入及源码剖析
3. Spring Boot 数据访问
4. Spring Boot 视图技术
5. Spring Boot 实战演练
6. Spring Boot 项目部署
```



## 1. Spring Boot 基本应用

### 1.1 约定优于配置

官网描述：*Spring Boot is the starting point for building all Spring-based applications. Spring Boot is designed to get you up and running as quickly as possible, with minimal upfront configuration of Spring*。

含义是，Spring Boot 是所有基于 Spring 开发的项目的起点。Spring Boot 的设计是为了让你<font color="blue">尽可能快的跑起来 Spring 应用程序并且尽可能减少你的配置文件</font>。

**约定优于配置（Convention over Configuration）**，又称按约定编程，是一种软件设计范式。本质上是系统、类库或框架假定合理的默认值，而非要求提供不必要的配置。例如：

- 模型中有一个名为 `User` 的类，数据库中对应的表默认命名为 `user`
- 仅当偏离约定（如表名改为 `person`）时，才需要编写配置

**核心价值**：类似架构师制定开发规范，强制统一编码标准，提升开发效率、代码可读性与维护性，减少开发人员的决策成本。

约定优于配置简单来理解，就是<font color=blue>*遵循约定*</font>



### 1.2 Spring Boot 概念

#### 1.2.1 Spring 优缺点分析

**优点**：

- Java EE 的轻量级替代品，无需开发重量级 EJB
- 通过依赖注入（DI）和面向切面编程（AOP），用简单的 POJO 实现 EJB 功能
- 简化企业级 Java 开发

**缺点**：

- Spring的组件代码是轻量级的，但**配置是重量级**的。早期依赖大量 XML 配置，后续注解和 Java 配置仍需额外编写，开发中需在配置与业务逻辑间频繁切换，挤占业务开发时间。
- **依赖管理复杂**，需手动分析依赖坐标及版本兼容性，SSM 整合时依赖繁多易冲突，阻碍项目的开发进度。



#### 1.2.2 Spring Boot 解决上述问题

> SpringBoot对上述Spring的缺点进行的改善和优化，基于约定优于配置的思想，可以让开发人员不必在配置与逻辑业务之间进行思维的切换，全身心的投入到逻辑业务的代码编写中，从而大大提高了开发的效率，一定程度上缩短 了项目周期。

1. **起步依赖**：本质是 Maven POM（Project Object Model），将某功能所需依赖打包，提供默认配置（如 `spring-boot-starter-web` 包含 Web 开发所有基础依赖）	

2. **自动配置**：Springboot自动将配置类的 Bean 注册到 IOC 容器，开发人员只需引入依赖，无需关心配置细节，直接使用 Bean

SpringBoot简单、快速、方便地搭建项目、无配置集成主流框架、大幅提升开发与部署效率。



### 1.3 Spring Boot 入门案例

**需求**：请求 Controller 方法，并将返回值响应到页面

#### （1）依赖管理

```xml
<!--  所有的springBoot项目都会直接或者间接的继承spring-boot-starter-parent
        1.指定项目的编码格式为UTF-8
        2.指定JDK版本为1.8
        3.对项目依赖的版本进行管理,当前项目再引入其他常用的依赖时就需要再指定版本号,避免版本冲突的问题
        4.默认的资源过滤和插件管理
    -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
    </parent>

    <!--  引入Spring Web及Spring MVC相关的依赖  -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <!--  可以将project打包为一个可以执行的jar  -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

#### （2）启动类

```java
/*
    SpringBoot的启动类通常放在二级包中,比如:com.gvc.SpringBootDemoApplication
    因为SpringBoot项目在做包扫描,会扫描启动类所在的包及其子包下的所有内容｡
 */
@SpringBootApplication  //标识当前类为SpringBoot项目的启动类
public class SpringBootDemoApplication {
    public static void main(String[] args) {
        //样板代码
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }
}
```

#### （3）Controller

```java
@RestController
@RequestMapping("/hello")
public class HelloController {
    @RequestMapping("/helloBoot")
    public String helloBoot(){
        return "Hello SpirngBoot!";
    }
}
```



### 1.4 Spring Boot 快速构建（Spring Initializr）

**案例需求**：请求 Controller 中的方法，并将返回值响应到页面。

#### 构建步骤

Spring Initializr 本质是一个 Web 应用，能提供基础项目结构，帮助快速构建 Spring Boot 项目。

#### （1）创建Spring Boot项目

![0101](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0101.png)

![0102](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0102.png)

生成后的项目**目录结构**如下：

![0103](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0103.png)

#### （2）创建 Controller并修改默认端口（8080）

```java
@RestController
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/sayHello")
    public String sayHello(){
        return "Hello Spring Boot!";
    }
}
```

我们想要更改端口测试一下，在`application.properties`更改

![0104](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0104.png)

#### （3）运行项目测试

运行主程序启动类SpringbootDemoApplication，项目启动成功后，在浏览器上访问http://127.0.0.1:8888/hello/sayHello

![0105](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0105.png)

页面输出的内容是“hello Spring Boot!”，至此，构建Spring Boot项目就完成了



### 1.5 单元测试与热部署

#### 1.5.1 单元测试

开发中，完成功能接口或业务方法后，需通过单元测试验证正确性。Spring Boot 提供了良好的单元测试支持，核心是引入测试依赖并通过注解实现测试。

##### （1）添加依赖

在 `pom.xml` 中添加 `spring-boot-starter-test` 依赖（Spring Initializr 构建的项目会自动引入，无需手动添加）：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

##### （2）编写单元测试类和方法

使用Spring Initializr方式搭建的Spring Boot项目，会在src.test.java测试目录下自动创建与项目主程序启动类对应的单元测试类

```java
/**
 *  SpringJUnit4ClassRunner.class: Spring运行环境
 *  JUnit4.class: JUnit运行环境
 *  SpringRunner.class: Spring Boot运行环境
 */
@RunWith(SpringRunner.class) //@RunWith运行器
@SpringBootTest //标记当前类为springboot的测试类，加载项目名的ApplicationContext上下文环境
class SpringbootQuickstartDemoApplicationTests {
    
    @Autowired
    private HelloController helloController;

    @Test
    void contextLoads() {
        String hello = helloController.sayHello();
        System.out.println(hello);
    }
}
```

上述代码中，通过 @Autowired注入 HelloController 实例，在 contextLoads() 方法中调用其请求控制方法并打印结果。运行测试后，控制台输出 `hello spring Boot`，且显示 `Tests passed: 1 of 1 test - 339 ms`，表明测试通过。



#### 1.5.2 热部署

开发中频繁修改代码后重启服务，会因服务启动耗时加载很久降低效率。Spring Boot 提供热部署依赖，修改代码后无需手动重启项目即可生效。

热部署： 修改代码后，无需重启容器即可实现更新

##### 使用步骤

```markdown
1. 添加热部署依赖；
2. 开启 IDEA 自动编译；
3. 开启 IDEA 运行中自动编译功能。
```

##### （1）添加依赖

在 pom.xml 中引入 spring-boot-devtools热部署依赖启动器

```xml
<!-- 引入热部署依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

由于使用的是IDEA开发工具，添加热部署依赖后可能没有任何效果，接下来还需要针对IDEA开发工具进行热部署相关的功能设置。

##### （2）IDEA工具热部署设置

- <font color="blue">开启自动编译</font>：选择IDEA工具界面的【File】→【Settings】选项，打开Compiler面板设置页面，勾选“Build project automatically”选项将项目设置为自动编译，单击【Apply】→【OK】按钮保存设置。

![0106](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0106.png)

- <font color="blue">开启运行中自动编译</font>： 【File】→【Settings】 → 【Advanced Settings】

  勾选 `Allow auto-make to start even if developed application is currently running`

![0107](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0107.png)

##### （3）热部署效果测试

启动项目后访问 http://127.0.0.1:8888/hello/sayHello；
![0108](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0108.png)

将DemoController 类中的请求处理方法hello()的返回值修改为“你好，Spring Boot！！！”并保存，查看控制台信息会发现项目能够自动构建和编译，说明项目热部署生效。

![0109](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0109.png)



### 1.6 全局配置文件

全局配置文件用于修改 Spring Boot 默认配置，支持两种格式：`application.properties` 和 `application.yaml`（或 `application.yml`），文件需放在 `src/main/resource` 目录或类路径的 `/config` 目录下，优先选择 `resource` 目录。

#### 1.6.1 application.properties 配置文件

Spring Initializr 方式构建的Spring Boot项目会在 resource 目录下自动生成空的 application.properties，项目启动时会自动加载该文件。

文件中可定义系统属性、环境变量、命令参数及自定义配置。

```properties
#修改Tomcat端口号
server.port=8888
#定义数据库的连接信息 JdbcTemplate
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_db
spring.datasource.username=root
spring.datasource.password=0709
```

接下来，通过一个案例对Spring Boot项目中application.properties配置文件的具体使用进行讲解

演示：

预先准备了两个实体类文件，后续会演示将application.properties配置文件中的自定义配置属性注入到Person实体类的对应属性中

##### （1）创建实体类

在 com.gvc包下新建 pojo 包，创建 `Pet` 和 `Person` 类

```java
public class Pet {
    private String type;
    private String name;
    // 省略getter、setter方法
}
```

```java
@Component
@ConfigurationProperties(prefix = "person")
public class Person {
    private int id; //id
    private String name; //名称
    private List hobby; //爱好
    private String[] family; //家庭成员
    private Map map;
    private Pet pet; //宠物
    // 省略getter、setter方法
}
```

`@ConfigurationProperties(prefix = "person")` 用于将配置文件中以 `person` 开头的属性通过 `setXX()` 方法注入实体类；

`@Component` 用于将 `Person` 类对象作为 Bean 放入 Spring 容器，否则无法被 `@ConfigurationProperties` 赋值。

##### （2）编写自定义配置

在 application.properties 中添加：

```properties
#自定义配置属性注入到Person对象中
person.id=100
person.name=于谦
#List
person.hobby=抽烟,喝酒,烫头
#String[]
person.family=妻,妾
#Map
person.map.k1=v1
person.map.k2=v2
#Pet对象
person.pet.type=dog
person.pet.name=旺财
```

##### （3）测试配置

在测试类中注入 Person 并输出：

```java
@RunWith(SpringRunner.class) // 测试启动器,并加载Spring Boot测试注解
@SpringBootTest // 标记为Spring Boot单元测试类,并加载项目的ApplicationContext上下文环境
class SpringbootDemoApplicationTests {
    // 配置测试
    @Autowired
    private Person person;

    @Test
    void configurationTest() {
        System.out.println(person);
    }
}
```

运行测试后，控制台输出如下结果，表明配置注入成功

```bash
Person{id=1,name='tom',hobby=[吃饭,睡觉,打豆豆],family=[father, mother],map={k1=v1,k2=v2),pet=Pet{type='dag',name='旺财'}}
```

##### （4）中文乱码解决

调整文件编码格式：

![0110](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0110.png)

设置Tomcat及Http编码：

```properties
#解决中文乱码
server.tomcat.uri-encoding=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true
server.servlet.encoding.charset=UTF-8
# 针对 Tomcat 日志的编码设置
logging.charset.console=UTF-8
logging.charset.file=UTF-8
```



#### 1.6.2 application.yaml 配置文件

YAML 是Spring Boot支持的一种JSON文件格式，以数据为核心，相较于 `properties` 更简洁，工作原理相同，扩展名支持 `.yml` 或 `.yaml`。其语法规则为：`key:(空格)value`，通过缩进控制<font color="red">层级关系</font>。

Spring Boot 是支持三种配置文件共存的：

```xml
<includes>
    <include>**/application*.yml</include>
    <include>**/application*.yaml</include>
    <include>**/application*.properties</include>
</includes>
```

##### 不同数据类型的配置方式

1. **value值为普通数据类型（例如数字、字符串、布尔等）**

   直接配置，字符串无需加引号

   ```yaml
   server:
     port: 8080
     servlet:
       context-path: /hello
   ```

2. **数组和单列集合**

   支持缩进式和行内式，其中缩进式还有两种写法

   ```yaml
   person: 
     hobby: 
       - play
       - read
       - sleep
   ```

   ​	或

   ```yaml
   person: 
     hobby: 
       play
       read
       sleep
   ```

   行内式：

   ```yaml
   person: 
     hobby: [play,read,sleep]
   ```

   YAML配置文件的行内式写法更加简明、方便。另外，包含属性值的中括
   号 “[]” 还可以进一步省略，在进行属性赋值时，程序会自动匹配和校对

3. **Map 集合和对象**

   支持缩进式和行内式：

   ```yaml
   person: 
     map: 
       k1: v1
       k2: v2
   ```

   ```yaml
   person:
     map: {k1: v1,k2: v2}
   ```

   在YAML配置文件中，Map 集合和对象的行内式写法的属性值要用大括号 “{}” 包含。

##### 配置注入

接下来，在Properties配置文件演示案例基础上，通过配置application.yaml配置文件对Person对象进行赋值，具体使用如下

在 `resource` 目录下新建 `application.yaml`，配置 `Person` 类属性：

```yaml
#对实体类对象Person进行属性配置
person:
  id: 1
  name: 王二麻子
  family:
    - 妻
    - 妾
  hobby:
    - play
    - read
    - sleep
  map:
    k1: value1
    k2: value2
  pet:
    type: 狗
    name: 哈士奇
```

再次执行测试，输出结果

```bash
Person{id=1, name='王二麻子', hobby=[play, read, sleep], family=[妻, 妾], map={k1=value1, k2=value2}, pet=Pet{type='狗', name='哈士奇'}}
```

**注意**：若同时存在 `properties` 和 `yaml` 文件，需注释 `properties` 中的配置，避免被覆盖。



### 1.7 配置文件属性值的注入

#### 1.7.1 配置文件优先级

**默认优先顺序为**：

`properties` 文件 > `yaml` 文件 > `yml` 文件

```xml
<includes>
    <include>**/application*.yml</include>
    <include>**/application*.yaml</include>
    <include>**/application*.properties</include>
</includes>
```

后面的文件会覆盖前面，即：`application*.properties` 最先被读取，然后是 `application*.yaml`，最后是 `application*.yml`。这是因为properties 格式的解析器优先级高于 yaml/yml，而 yaml 和 yml 本质是同一种格式的不同扩展名，yaml 的优先级略高于 yml。

- 若配置 Spring Boot 已有属性（如 `server.port`），框架会自动扫描并覆盖默认值；
- 若配置自定义的Person实体类属性（如 `person.id`等），还必须在程序中注入这些配置属性方可生效。

#### 1.7.2 注入方式

Spring Boot 支持 `@ConfigurationProperties` 和 `@Value` 两种注入方式。

##### （1）@ConfigurationProperties 注入（批量注入）

用于快速批量将配置文件中的自定义属性注入 Bean 的多个属性，示例如下：

```java
/*
    将配置文件所有以person开头的配置信息注入当前类中
    前提：1.必须保证配置文件中person.xx与Person类中的属性名一致
         2.必须保证Person类中的属性都有set方法
*/
@Component
@ConfigurationProperties(prefix = "person")
public class Person {
    private int id; //id
    private String name; //名称
    private List hobby; //爱好
    private String[] family; //家庭成员
    private Map map;
    private Pet pet; //宠物
    // 省略getter、setter方法
}
```

该注解需配合 `@Component` 使用，将类注册为 Spring Bean，同时要求配置文件中属性前缀与类属性名一致，且类属性需提供 `set` 方法。

##### （2）@Value 注入（单个注入）

`@Value` 是 Spring 框架提供的注解，可逐个读取配置文件属性并注入 Bean 属性，Spring Boot 默认继承该注解。示例如下：

```java
@Component
public class Person {
    @Value("${person.id}")
    private int id;
    // 省略其他属性及getter、setter方法
}
```

相较于 `@ConfigurationProperties`，`@Value` 支持直接赋值（如 `@Value("123")`），但无需 `set` 方法，且需对每个属性单独配置。

##### （3）@Value 注入测试

1. pojo包下新创建一个实体类Student 类，使用@Value注解注入属性

```java
@Component
public class Student {
    @Value("${person.id}")
    private int id;
    @Value("${person.name}")
    private String name; //名称
    //省略toString方法
}
```

2. 打开测试类编写测试方法

```java
@Autowired
private Student student;

@Test
public void studentTest() {
    System.out.println(student);
}
```

运行测试后，控制台输出 `Student{id=1000,name='lucy'}`，表明注入成功。

注意：`@Value` 不支持 Map 集合、对象及 yaml 行内式配置的属性注入，强行注入会报错。



### 1.8 自定义配置

Spring Boot 无法自动识别自定义配置文件（非 `application` 开头），需手动加载。常用加载方式有两种：`@PropertySource` 和 `@Configuration`。

#### 1.8.1 @PropertySource 加载自定义配置文件

`@PropertySource` 用于指定自定义配置文件的位置和名称，配合 `@ConfigurationProperties` 或 `@Value` 实现属性注入。

**（1）创建自定义配置文件**

在 `resource` 目录下新建 `test.properties`：

```properties
#对实体类对象MyProperties进行属性配置
product.id=99
product.name=华为
```

**（2）创建配置类**

在 com.gvc.pojo 包下新建 MyProperties类

```java
@Component
@ConfigurationProperties(prefix = "product")
@PropertySource("classpath:my.properties") //指定加载外部自定义的配置文件
public class Product {
    private int id;
    private String name;
	//getter,setter,toString...
}
```

- @PropertySource("classpath:my.properties") 指定配置文件为类路径下的 my.properties；
- @ConfigurationProperties(prefix = "product") 将配置文件中以 product开头的属性注入类中。

**（3）测试**

```java
	@Autowired
    private Product product;
    @Test
    void getProduct(){
        System.out.println(product);
    }
```

控制台输出

```
Product{id=99, name='华为'}
```

#### 1.8.2 @Configuration 编写自定义配置类

在Spring Boot框架中，推荐使用配置类的方式向容器中添加和配置组件，通常使用`@Configuration` 定义配置类，替代传统 Spring 的 XML 配置文件；

定义一个配置类后，还需要在类中的方法上配合 `@Bean` 将方法返回对象注入 Spring 容器，组件名称默认为方法名，也可通过 `@Bean(name/value)` 自定义。

**（1）创建 MyService类**

在 com.gvc.config 包下新建 MyService，该类不需要编写任何代码

```java
public class MyService {
}
```

该类目前没有添加任何配置和注解，因此还无法正常被Spring Boot扫描和识别

**（2）创建配置类**

在项目的com.gvc.config包下，新建一个MyConfig类，并使用@Configuration注解将该类声明一个配置类

```java
@Configuration //当前类为配置类，SpringBoot会扫描该类，将标识@Bean注解的方法返回值注入容器中
public class MyConfig {
    @Bean  //id默认为myService
    public MyService myService(){
        return new MyService();
    }
    @Bean("myService__") //id默认为myService2,但此时已被自定义为myService__
    public MyService myService2(){
        return new MyService();
    }
}
```

`@Configuration` 标识MyConfig类为配置类（类似于声明了一个XML配置文件），该配置类会被Spring Boot自动扫描识；

使用@Bean注解的myService()方法，其返回值对象会作为组件添加到了Spring容器中，组件的id默认是方法名myService。

**（3）测试类**

通过@Autowired注解引入了Spring容器实例ApplicationContext，然后在测试方法
iocTest()中测试查看该容器中是否包括id为myService的组

```java
@Autowired
private ApplicationContext applicationContext;
@Test
void testMyConfig(){
    System.out.println(applicationContext.containsBean("myService"));
    System.out.println(applicationContext.containsBean("myService__"));
}
```

运行测试后，控制台输出两个 `true`，表明 `myService`,`myService__` 组件已注入 Spring 容器。





## 2. Spring Boot 原理深入及源码剖析

传统的Spring框架实现一个Web服务，需要导入各种依赖JAR包，编写对应的XML配置文件等，相较而言，Spring Boot显得更加方便、快捷和高效。

那么，Spring Boot究竟如何做到这些的呢？接下来从**依赖管理**和**自动配置**两方面剖析

### 2.1 依赖管理

**（1）问题1：为什么导入dependency时不需要指定版本？**

Spring Boot 项目的 pom.xml 有两个核心依赖：<font color="blue">**spring-boot-starter-parent** </font>和 <font color="blue">**spring-boot-starter-web**</font>。

1. <font color="blue">**spring-boot-starter-parent** </font>依赖

```xml
<!-- Spring Boot父项目依赖管理 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId><!-- 查看parent -->
    <version>3.5.6</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```

![0111](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0111.png)

将spring-boot-starter-parent依赖作为Spring Boot项目的统一父项目依赖管理，并将项
目版本号统一为3.5.6，该版本号根据实际开发需求是可以修改的；

使用“Ctrl+鼠标左键”进入并查看spring-boot-starter-parent底层源文件，发现spring-boot-
starter-parent的底层有一个父依赖spring-boot-dependencies：

```xml
<properties>
    ...
    <activemq.version>6.1.7</activemq.version>
    <commons-dbcp2.version>2.13.0</commons-dbcp2.version>
    <junit.version>4.13.2</junit.version>
    <mysql.version>9.4.0</mysql.version>
    <kafka.version>3.9.1</kafka.version>
    <spring-security.version>6.5.5</spring-security.version>
    <tomcat.version>10.1.46</tomcat.version>
    ...
</properties>
```

![0112](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0112.png)

因此，项目引入 spring-boot-starter-parent 管理的依赖时，无需指定版本号。

如果pom.xml引入的依赖文件不是 spring-boot-starter-parent管理的，那么在pom.xml引入依赖文件时，需要使用标签指定依赖文件的版本号。



**（2）问题2： spring-boot-starter-parent父依赖启动器的主要作用是进行版本统一管理，那么项目运行依赖的JAR包是从何而来的？**

以 spring-boot-starter-web 为例，其本质是一个「依赖组合包」，包含 Web 开发所需的所有底层依赖。查看 spring-boot-starter-web 依赖文件源码，核心代码具体如下

2. <font color="blue">**spring-boot-starter-web**</font>依赖

```xml
<dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
      <version>3.5.6</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-json</artifactId>
      <version>3.5.6</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-tomcat</artifactId>
      <version>3.5.6</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>6.2.11</version>
      <scope>compile</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>6.2.11</version>
      <scope>compile</scope>
    </dependency>
```

Spring-boot-starter-web依赖启动器的主要作用是提供Web开发场景所需的底层所有依赖。

- 在pom.xml中引入 `spring-boot-starter-web` 后，会自动导入 Tomcat、Spring MVC、JSON 解析等依赖；


- 这些依赖的版本由 `spring-boot-starter-parent` 统一管理，无需手动维护。

starter可视作一系列的依赖被封装在一起，其它starter:

 [Spring 官网](https://github.com/spring-projects/spring-boot/tree/3.5.x/spring-boot-project/spring-boot-starters) 或 [Maven 仓库](https://mvnrepository.com/search?q=starter) 

Spring Boot除了提供有上述介绍的Web依赖启动器外，还提供了其他许多开发场景的相关依赖，我们可以打开Spring Boot官方文档，搜索“Starters”关键字查询场景依赖启动器：

https://docs.spring.io/spring-boot/reference/using/build-systems.html#using.build-systems.starters

![0113](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0113.png)

对于官方未整合的框架（如 MyBatis、Druid），第三方团队会提供自定义 Starter（如 mybatis-spring-boot-starter、druid-spring-boot-starter），引入时需手动指定版本。



### 2.2 自动配置

**概念**：添加 JAR 包依赖后，Spring Boot 自动配置相关组件，开发者无需配置或只需少量配置即可运行项目。

**问题：Spring Boot到底是如何进行自动配置的，都把哪些组件进行了自动配置？**

 由`@SpringBootApplication` 标注在某个类上说明该类是 SpringBoot 的主配置类， SpringBoot 会运行这个类的 main() 方法启动 SpringBoot 应用。

#### 2.2.1 自动配置的入口：@SpringBootApplication

Spring Boot 应用的启动入口是 `@SpringBootApplication` 标注类的 main() 方法，该注解是组合注解，核心作用是标识主配置类并启动自动配置。

```java
@SpringBootApplication
public class SpringbootDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootDemoApplication.class, args);
    }
}
```

进入到@SpringBootApplication 内，观察其做了哪些工作：

```java
@Target({ElementType.TYPE}) //注解的适用范围,Type表示注解可以标识类､接口､注解或枚举类型等
@Retention(RetentionPolicy.RUNTIME) //表示注解的生命周期,Runtime运行时
@Documented //表示注解可以记录在javadoc中
@Inherited //表示可以被子类继承该注解
@SpringBootConfiguration // 标明该类是一个配置类
@EnableAutoConfiguration // 启动自动配置功能
@ComponentScan(          // 组件扫描
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
    // 根据class来排除特定的类,使其不能加入spring容器,传入参数value类型是class类型｡
    @AliasFor(annotation = EnableAutoConfiguration.class)
    Class<?>[] exclude() default {};
    // 根据classname 来排除特定的类,使其不能加入spring容器,传入参数value类型是class的全类名字符串数组｡
    @AliasFor(annotation = EnableAutoConfiguration.class)
    String[] excludeName() default {};
    // 指定扫描包,参数是包名的字符串数组｡
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};
    // 扫描特定的包,参数类似是Class类型数组｡
    @AliasFor(annotation = ComponentScan.class, attribute =
            "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};
}
```

@SpringBootApplication注解是一个**组合注解**，前面 4 个是注解的元数据
信息， 我们主要看后面 3 个注解：**<font color="blue">@SpringBootConfiguration、@EnableAutoConfiguration、
@ComponentScan</font>**三个核心注解。

##### （1） <font color="blue">@SpringBootConfiguration</font>注解

 标注在某个类上，表示这是一个 SpringBoot的配置类，源码如下

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed // 为字段或属性创建索引，常见于 ORM（对象关系映射）框架
@Configuration // 配置类的作用等同于配置文件,配置类也是容器中的一个对象
public @interface SpringBootConfiguration {
}
```

> @SpringBootConfiguration注解内部有一个核心注解@Configuration，该
> 注解是Spring框架提供的，表示当前类为一个配置类（XML配置文件的注解表现形式），并可以被组件扫描器扫描。
>
> 由此可见，**@SpringBootConfiguration注解的作用与@Configuration注解相同，都是标识一个可以被组件扫描器扫描的配置类，只不过@SpringBootConfiguration是被Spring Boot进行了重新封装命名而已**。



##### （2）<font color="blue">@EnableAutoConfiguration</font>注解

开启自动配置功能，是 Spring Boot 实现自动配置的关键，源码如下：

```java
// 自动配置包
@AutoConfigurationPackage
// Spring的底层注解@Import,给容器中导入一个组件;
// 导入的组件是AutoConfigurationPackages.Registrar.class 
@Import(AutoConfigurationImportSelector.class)
// 告诉SpringBoot开启自动配置功能,这样自动配置才能生效｡
public @interface EnableAutoConfiguration {
    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
    // 返回不会被导入到 Spring 容器中的类
    Class<?>[] exclude() default {};
    // 返回不会被导入到 Spring 容器中的类名
    String[] excludeName() default {};
}
```

> 可以发现它是一个组合注解，包含@AutoConfigurationPackage和@Import， Spring 中有很多以 Enable 开头的注解，其作用就是借助 @Import来收集并注册特定场景相关的 Bean ，并加载到 IOC 容器。@EnableAutoConfiguration就是借助@Import来收集所有符合自动配置条件的bean定义，并加载到IoC容器。	

- ###### 子注解 1：@AutoConfigurationPackage 

  功能：指定自动配置的包扫描范围（默认是启动类所在包及其子包）；

  逻辑：通过 `@Import` 导入 Registrar 类，Registrar 会扫描启动类所在包及其子包的组件，注册到 IOC 容器。

  ```java
  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Inherited
  @Import({Registrar.class}) // 导入Registrar中注册的组件
  public @interface AutoConfigurationPackage {
  }
  ```

  ![0114](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0114.png)

  启动类必须放在项目根目录（如 `com.gvc`），否则子包中的组件无法被扫描到。

  

- ###### 子注解 2：@Import (AutoConfigurationImportSelector.class)

  功能：导入 `AutoConfigurationImportSelector` 类，该类通过委托 `AutoConfigurationGroup` 实现自动配置类的加载与筛选，最终导入所有符合条件的自动配置类；

  逻辑：通过 `getImportGroup` 方法指定 `AutoConfigurationGroup`，由该 Group 借助升级后的加载机制，读取 classpath 下所有 JAR 包中的 `METAINF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件（替代旧版 `spring.factories`），加载并筛选自动配置类。

​	**方法源码**

 1. `getAutoConfigurationEntry` 方法（自动配置入口：筛选最终生效的配置类）：

    ```java
    @Override //Spring Boot 3.5.6
    protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
        // 1. 检查自动配置是否启用（通过@EnableAutoConfiguration注解控制，默认启用）
        // 若禁用，返回空的配置入口，不加载任何自动配置类
        if (!isEnabled(annotationMetadata)) {
            return EMPTY_ENTRY;
        }
        // 2. 提取@EnableAutoConfiguration注解的属性（如exclude、excludeName等配置）
        AnnotationAttributes attributes = getAttributes(annotationMetadata);
        // 3. 核心步骤：点击获取所有候选自动配置类（从AutoConfiguration.imports文件中读取，下文详细解析）
        List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
        // 4. 去重：避免多个JAR包中重复定义同一自动配置类
        configurations = removeDuplicates(configurations);
        // 5. 获取需要排除的配置类（根据注解的exclude属性或spring.autoconfigure.exclude配置）
        Set<String> exclusions = getExclusions(annotationMetadata, attributes);
        // 6. 校验排除类：确保要排除的类确实在候选配置列表中（避免排除不存在的类导致异常）
        checkExcludedClasses(configurations, exclusions);
        // 7. 执行排除：从候选列表中移除需要排除的配置类
        configurations.removeAll(exclusions);
        // 8. 环境筛选：通过配置类过滤器（如@Conditional注解）筛选符合当前环境的配置类
        // （例如：依赖不存在时，对应的AutoConfiguration会被过滤）
        configurations = getConfigurationClassFilter().filter(configurations);
        // 9. 触发事件：发布自动配置导入事件，允许外部监听（如日志记录、自定义处理）
        fireAutoConfigurationImportEvents(configurations, exclusions);
        // 10. 返回最终生效的配置入口（包含筛选后的配置类和排除类）
        return new AutoConfigurationEntry(configurations, exclusions);
    }
    ```

2. `getCandidateConfigurations` 方法（候选配置类加载：衔接配置文件与入口方法）

   ```java
   protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
       // 1. 核心工具：通过ImportCandidates加载配置类
       // - 参数1：autoConfigurationAnnotation = EnableAutoConfiguration.class（固定指向自动配置核心注解）
       // - 参数2：类加载器（用于扫描classpath下所有JAR包中的配置文件） 查看load方法
       ImportCandidates importCandidates = ImportCandidates.load(this.autoConfigurationAnnotation,
               getBeanClassLoader());
       // 2. 提取加载到的候选配置类列表（全类名字符串集合）
       List<String> configurations = importCandidates.getCandidates();
       // 3. 校验非空：若未找到任何配置类，抛出异常提示
       // 异常信息直接暴露配置文件路径格式，是定位文件的关键线索
       Assert.state(!CollectionUtils.isEmpty(configurations),
               "No auto configuration classes found in " + "META-INF/spring/"
                       + this.autoConfigurationAnnotation.getName() + ".imports. If you "
                       + "are using a custom packaging, make sure that file is correct.");
       // 4. 返回候选配置类列表（交给上一级方法筛选）
       return configurations;
   }
   ```
   
3. `load` 方法（配置文件读取：从 AutoConfiguration.imports 中解析配置类）

   ```java
   public static ImportCandidates load(Class<?> annotation, ClassLoader classLoader) {
       // 1. 非空校验：确保传入的注解类（EnableAutoConfiguration）不为null
       Assert.notNull(annotation, "'annotation' must not be null");
       // 2. 确定类加载器：优先使用传入的类加载器，若为null则用当前线程的上下文类加载器
       ClassLoader classLoaderToUse = decideClassloader(classLoader);
       // 3. 拼接配置文件路径（核心！生成AutoConfiguration.imports的完整路径）
       // - 固定前缀：META-INF/spring/
       // - 中间部分：注解全类名（如org.springframework.boot.autoconfigure.AutoConfiguration）
       // - 固定后缀：.imports
       // 最终路径：META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
       String location = String.format("META-INF/spring/%s.imports", annotation.getName());
       // 4. 扫描类路径：查找所有JAR包中符合上述路径的文件（支持多JAR包合并读取）
       // （例如：spring-boot-autoconfigure.jar中的该文件会被优先扫描）
       Enumeration<URL> urls = findUrlsInClasspath(classLoaderToUse, location);
       // 5. 存储解析后的配置类列表
       List<String> importCandidates = new ArrayList();
   
       // 6. 遍历所有找到的配置文件，解析内容
       while(urls.hasMoreElements()) {
           URL url = (URL)urls.nextElement(); // 获取单个配置文件的URL（如JAR包内的文件路径）
           // 7. 读取文件内容：解析每行的配置类全类名（忽略空行和#注释）
           importCandidates.addAll(readCandidateConfigurations(url));
       }
   
       // 8. 返回封装后的配置类集合（提供给上一级方法使用）
       return new ImportCandidates(importCandidates);
   }
   ```

**AutoConfiguration.imports 文件示例**（spring-boot-autoconfigure.jar 中）：

声明需要自动加载的自动配置类（AutoConfiguration 类），Spring Boot 2.7+ 引入的新文件，替代了旧版本中 `spring.factories` 文件，文件内容是一系列自动配置类的全限定名（每行一个类），例如：

![springboot0115](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0115.png)

`META-INF/spring-autoconfigure-metadata.properties`文件存储自动配置类的元数据，用于优化自动配置的条件判断效率，帮助快速判断 “这些类是否应该生效”。



##### （3）  <font color="blue">@ComponentScan</font>注解

功能：组件扫描，默认扫描启动类所在包及其子包下的所有组件（@Controller、@Service、@Component 等）；

扫描范围：由 `@AutoConfigurationPackage` 解析，即启动类所在包的路径

小结：@SpringBootApplication 的注解的功能简单来说就是 3 个注解的组合注解

```markdown
* |- @SpringBootConfiguration
		|- @Configuration 通过javaConfig的方式来添加组件到IOC容器中
* |- @EnableAutoConfiguration
		|- @AutoConfigurationPackage 自动配置包,与@ComponentScan扫描到的添加到IOC
        |- @Import(AutoConfigurationImportSelector.class) 将AutoConfiguration.imports中定义的bean添加到IOC容器中
* |- @ComponentScan //包扫描
```



#### 2.2.2 自动配置流程总结

1. 启动 Spring Boot 应用（执行 `main` 方法）；
2. `@SpringBootApplication` 注解生效，触发 `@EnableAutoConfiguration`；
3. `@AutoConfigurationPackage` 导入 `Registrar` 类，扫描启动类所在包及其子包的组件，注册到 IOC 容器；
4. `@Import(AutoConfigurationImportSelector.class)` 导入选择器类，通过 `ImportCandidates` 扫描类路径，查找所有JAR包中符合路径的文件，从`AutoConfiguration.imports `文件加载自动配置类；
5. 自动配置类根据当前环境（依赖是否存在、配置是否生效）筛选出最终需要的组件，注册到 IOC 容器；
6. 开发者可直接使用 IOC 容器中的组件（通过 `@Autowired` 注入）。





## 3. Spring Boot 数据访问

### 3.1 Spring Boot 整合 MyBatis

MyBatis 是优秀的持久层框架，Spring Boot 官方未提供整合支持，但 MyBatis 团队自行适配了 `mybatis-spring-boot-starter`，简化整合流程。

#### 3.1.1基础环境搭建

##### ① 数据准备（MySQL）

创建数据库、表并插入测试数据

```mysql
-- 创建数据库
CREATE DATABASE springbootdata;
USE springbootdata;

-- 创建文章表 t_article
DROP TABLE IF EXISTS t_article;
CREATE TABLE t_article (
    id int(20) NOT NULL AUTO_INCREMENT COMMENT '文章id',
    title varchar(200) DEFAULT NULL COMMENT '文章标题',
    content longtext COMMENT '文章内容',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 插入文章数据
INSERT INTO t_article VALUES ('1', 'Spring Boot基础入门', '从入门到精通讲解...');
INSERT INTO t_article VALUES ('2', 'Spring Cloud基础入门', '从入门到精通讲解...');

-- 创建评论表 t_comment（a_id 关联文章表 id）
DROP TABLE IF EXISTS t_comment;
CREATE TABLE t_comment (
    id int(20) NOT NULL AUTO_INCREMENT COMMENT '评论id',
    content longtext COMMENT '评论内容',
    author varchar(200) DEFAULT NULL COMMENT '评论作者',
    a_id int(20) DEFAULT NULL COMMENT '关联的文章id',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- 插入评论数据
INSERT INTO t_comment VALUES ('1', '很全、很详细', 'lucy', '1');
INSERT INTO t_comment VALUES ('2', '赞一个', 'tom', '1');
INSERT INTO t_comment VALUES ('3', '很详细', 'eric', '1');
INSERT INTO t_comment VALUES ('4', '很好,非常详细', '张三', '1');
INSERT INTO t_comment VALUES ('5', '很不错', '李四', '2');
```

##### ② 创建项目并引入依赖

通过 Spring Initializr 构建项目，勾选以下依赖：Spring Web（Web 支持），MyBatis Framework（MyBatis 核心），MySQL Driver（MySQL 驱动）。

![0116](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0116.png)

##### ③ 编写实体类（对应数据库表）

```java
// 评论实体类（对应 t_comment 表）
public class Comment {
    private Integer id;       // 评论id
    private String content;  // 评论内容
    private String author;   // 评论作者
    private Integer aId;     // 关联文章id（对应表中 a_id）
    // 省略 getter/setter/toString 方法
}
```

```java
// 文章实体类（对应 t_article 表）
public class Article {
    private Integer id;       // 文章id
    private String title;     // 文章标题
    private String content;   // 文章内容
    // 省略 getter/setter/toString 方法
}
```

##### ④ 配置数据库连接（application.yml）

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot_db?serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: 0709
    driver-class-name: com.mysql.cj.jdbc.Driver //driverclass可自动配置无需编写
```



#### 3.1.2 注解方式整合 MyBatis（无 XML）

**需求**：通过注解实现「根据 ID 查询评论」功能。

##### ① 创建 Mapper 接口

```java
// 评论 Mapper 接口（MyBatis 映射接口）
public interface CommentMapper {
    // 注解方式编写 SQL：根据 id 查询评论
    @Select("SELECT * FROM t_comment WHERE id = #{id}")
    Comment findById(Integer id);
}
```

##### ② 启动类添加 Mapper 扫描

```java
@SpringBootApplication
@MapperScan("com.gvc.mapper") //执行扫描 mapper包
public class SpringbootMybatisDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootMybatisDemoApplication.class, args);
    }
}
```

##### ③ 编写测试方法

```java
@RunWith(SpringRunner.class)
@SpringBootTest
class SpringbootPersistenceApplicationTests {
    @Autowired
    private CommentMapper commentMapper; // 注入 Mapper 接口（MyBatis 动态代理生成实现类）
    @Test
    void findCommentById() {
        Comment comment = commentMapper.findById(1);
        System.out.println(comment);
    }
}
```

##### ④ 测试结果与问题解决

- 第一次测试结果：

```
Comment{id=1, content='很全、很详细', author='lucy', aId=null}
```

**问题**：`aId` 为 `null`（表中 `a_id` 字段值未映射到实体类 `aId` 属性）。

**解决方案**：开启 MyBatis 驼峰命名匹配（表中 `a_id` → 实体类 `aId`），自动将数据库表中下划线命名的字段名映射为 Java 实体类中驼峰命名的属性名，在 `application.yml` 中添加配置：

```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true # 开启驼峰命名匹配
```

- 第二次测试结果：

```
Comment{id=1, content='很全、很详细', author='lucy', aId=1}
```



#### 3.1.3 配置文件方式整合 MyBatis（XML 映射）

**需求**：通过 XML 配置实现「根据 ID 查询文章」功能。

① ② 步骤可由**插件**【**free Mybatis plugin**】生成;

![0117](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0117.png)

![0118](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0118.png)



##### ① 创建 Mapper 接口

```java
@Mapper // 标识为 MyBatis Mapper 接口（无需在启动类添加 @MapperScan）
public interface ArticleMapper {
    // 方法声明（SQL 写在 XML 中）
    Article selectArticle(Integer id);
}
```

##### ② 创建 XML 映射文件

在 `resources` 目录下创建 `mapper` 文件夹，新建 `ArticleMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!-- namespace：对应 Mapper 接口全类名 -->
<mapper namespace="com.gvc.mapper.ArticleMapper">
    <!-- id：对应 Mapper 接口中的方法名；resultType：返回值类型（实体类全类名/别名） -->
    <select id="selectArticle" resultType="com.gvc.pojo.Article">
        SELECT * FROM t_article WHERE id = #{id}
    </select>
</mapper>
```

##### ③ 配置 XML 映射文件路径（application.yml）

Spring Boot 无法自动扫描自定义 XML 文件，需手动配置路径：

```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true  # 开启驼峰命名匹配
  mapper-locations: classpath:mapper/*.xml  # XML 映射文件路径（classpath:mapper/ 表示 resources/mapper/）
  type-aliases-package: com.gvc.pojo  # 实体类别名（配置后，XML 中 resultType 可直接写类名，无需全类名）
```

##### ④ 编写测试方法

```java
@Autowired
private ArticleMapper articleMapper;
@Test
void findArticleById() {
    Article article = articleMapper.selectByPrimaryKey(1);
    System.out.println(article);
}
```

##### ⑤ 测试结果

查询成功

```
Article{id=1, title='Spring Boot基础入门', content='从入门到精通讲解...'}
```



### 3.2 Spring Boot 整合 Redis

Redis 是高性能键值数据库，Spring Boot 提供 `spring-boot-starter-data-redis` 简化整合。

#### （1）添加 Redis 依赖（pom.xml）

```xml
<!-- Redis 依赖包（包含 Redis 客户端和 Spring 整合支持） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### （2）配置 Redis 连接（application.yml）

```properties
spring:
  data:
    redis:
	  # Redis 服务器地址（本地默认 localhost）
      host: 192.168.44.128
      # Redis 服务器端口（默认 6379）
      port: 6379
      # Redis 数据库索引（默认 0，Redis 支持 16 个数据库）
      database: 0
      # Jedis连接池
      jedis:
        pool:
      	  # 连接池最大连接数（负值表示无限制）
          max-active: 50      
     	  # 连接池中的最大空闲连接
          max-idle: 20     		
      	  # 连接池中的最小空闲连接
          min-idle: 0	     
     	  # 连接池最大阻塞等待时间（单位：毫秒）默认-1ms
          max-wait: 5000ms    
      # 连接超时时间（单位：毫秒）
      connect-timeout: 3000
     
```



#### （3）编写 Redis 操作工具类

封装 `RedisTemplate`（Spring 提供的 Redis 操作模板），简化 Redis 数据操作：

```java
@Component // 注册为 Spring Bean，可被自动注入
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate; // 注入 RedisTemplate 实例

    /**
     * 读取缓存（根据 key 获取值）
     * @param key 缓存键
     * @return 缓存值
     */
    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 写入缓存（设置 key-value，有效期 1 天）
     * @param key 缓存键
     * @param value 缓存值
     * @return 操作结果（true：成功，false：失败）
     */
    public boolean set(String key, Object value) {
        boolean result = false;
        try {
            // opsForValue()：操作字符串类型数据；set 方法参数：key、value、有效期、时间单位
            redisTemplate.opsForValue().set(key, value, 1, TimeUnit.DAYS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新缓存（先获取旧值，再设置新值）
     * @param key 缓存键
     * @param value 新缓存值
     * @return 操作结果
     */
    public boolean getAndSet(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().getAndSet(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除缓存（根据 key 删除）
     * @param key 缓存键
     * @return 操作结果
     */
    public boolean delete(final String key) {
        boolean result = false;
        try {
            redisTemplate.delete(key);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
```

#### （4）实体类标记序列化接口Serializable

```java
public class Article implements Serializable {
    // 序列化版本号（必须添加，避免类结构变化导致反序列化失败）
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String title;
    private String content;
	// 省略...
}
```

#### （5）测试 Redis 操作

```java
@Autowired
private RedisUtils redisUtils;

//redis写入，key:1 - value:mysql数据库中id为1的Article
@Test
void testRedisWrite() {
    redisUtils.set("1", articleMapper.selectByPrimaryKey(1));
    System.out.println("success");
}

// 测试读取redis key为1的值
@Test
void testRedisRead() {
    Article article = (Article) redisUtils.get("1");
    System.out.println(article);
}
```

#### （6）测试结果

> 运行 `setRedisData`：控制台输出 `success`，数据存入 Redis；
>
> 运行 `getRedisData`：控制台输出文章信息，数据读取成功





## 4. Spring Boot 视图技术

### 4.1 支持的视图技术

Spring Boot 推荐使用 **模板引擎** 实现页面渲染，支持 FreeMarker、Thymeleaf、Mustache 等，不推荐 JSP（存在兼容性问题）。

#### JSP 不推荐原因

1. 嵌入式容器限制：
   - Jetty/Tomcat 容器中，WAR 包部署支持 JSP，但 Spring Boot 默认以 JAR 包部署（不支持 JSP）；
   - Undertow 容器（红帽开发的高性能容器）不支持 JSP。
2. 错误处理器限制：Spring Boot 提供默认错误处理器（处理 `/error` 请求），使用 JSP 无法覆盖该处理器，只能在指定位置定制错误页面。



### 4.2 Thymeleaf（推荐）

Thymeleaf 是基于服务器端的 Java 模板引擎，支持 HTML 原型（静态页面可直接预览），标签丰富、语法简洁，与 Spring Boot 无缝整合。

#### 4.2.1 Thymeleaf 语法

##### （1）命名空间引入

在 HTML 页面头部引入 Thymeleaf 命名空间，才能使用其标签：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" media="all"
    href="../../css/gtvg.css" th:href="@{/css/gtvg.css}" />
    <title>Title</title>
</head>
<body>
	<p th:text="${hello}">欢迎进入Thymeleaf的学习</p>	
</body>
</html>
```

![0119](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0119.png)

##### （2）常用 th: 标签

| 标签       | 说明                                                         |
| ---------- | ------------------------------------------------------------ |
| th:insert  | 布局标签：将内容插入到引入的文件中                           |
| th:replace | 页面片段包含（类似 JSP 的 `include` 标签，替换当前标签为引入的片段） |
| th:each    | 元素遍历（类似 JSP 的 `c:forEach`，如 `th:each="user : ${users}"`） |
| th:if      | 条件判断（为真时显示标签，如 `th:if="${user.age > 18}"`）    |
| th:unless  | 条件判断（为假时显示标签，与 `th:if` 相反）                  |
| th:switch  | 条件判断（多分支，配合 `th:case` 使用）                      |
| th:case    | 条件判断（`th:switch` 的分支）                               |
| th:value   | 设置标签属性值（如 `th:value="${user.id}"`）                 |
| th:href    | 设置链接地址（如 `th:href="@{/user/detail(id=${user.id})}"`） |
| th:src     | 设置资源路径（如 `th:src="@{/img/logo.png}"`）               |
| th:text    | 设置标签文本内容（如 `th:text="${user.name}"`）              |

##### （3）标准表达式

Thymeleaf 提供 5 种核心表达式，用于动态获取数据或生成路径：

| 表达式类型      | 语法   | 说明                                                         |
| --------------- | ------ | ------------------------------------------------------------ |
| 变量表达式      | ${...} | 获取上下文变量（如 `${user.name}` 获取用户名称）             |
| 选择变量表达式  | *{...} | 从指定对象中获取属性（需配合 `th:object`，如 `${#locale.country}`） |
| 消息表达式      | #{...} | 国际化内容替换（需配合国际化配置文件，如 `#{welcome.msg}`）  |
| 链接 URL 表达式 | @{...} | 生成链接地址（支持绝对路径和相对路径，自动处理上下文路径）   |
| 片段表达式      | ~{...} | 引用页面片段（如 `~{common::header}` 引用 common.html 的 header 片段） |

###### 1. 变量表达式 ${...}

- 功能：获取 Spring MVC 传递到页面的变量（如 `Model` 中的数据）；

- 示例：

  ```html
  <!-- 静态文本：未启动时显示；动态文本：启动后替换为 ${title} 的值 -->
  <p th:text="${title}">这是标题</p>
  ```

  > Thymeleaf模板的变量表达式${...}用来动态获取P标签中的内容，如果当前程序没有启动或
  > 者当前上下文中不存在title变量，该片段会显示标签默认值“这是标题”；
  >
  > 如果当前上下文中存在title变量并且程序已经启动，当前P标签中的默认文本内容将会被title变量的值所替换，从而达到模板引擎页面数据动态替换的效果。

  内置对象：可通过 `#` 访问 Thymeleaf 内置对象，如：

  ```html
  <!-- 获取当前用户所在国家（默认显示 US） -->
  <span th:text="${#locale.country}">US</span>
  ```

  > th:text="${#locale.country}"动态获取当前用户所在国家信息，其中标签内默认内容为US（美国），程序启动后通过浏览器查看当前页面时，Thymeleaf会通过浏览器语言设置来识别当前用户所在国家信息，从而实现动态替换。

  **常用内置对象**：

  ```
  # ctx:      		上下文对象
  # vars:     		上下文变量
  # locale:   		上下文区域设置
  # request:  		(仅限Web Context)HttpServletRequest对象
  # response: 		(仅限Web Context)HttpServletResponse对象
  # session:  		(仅限Web Context)HttpSession对象
  # servletContext:	(仅限Web Context)ServletContext对象
  ```



###### 2. 选择变量表达式 *{...}

- 功能：从指定对象中获取属性，简化变量表达式；

- 示例：

  ```html
  <!-- 指定对象：${user} -->
  <div th:object="${user}">
      <!-- 等同于 ${user.name} -->
      <p th:text="*{name}">用ji</p>
      <!-- 等同于 ${user.age} -->
      <p th:text="*{age}">年龄</p>
  </div>
  ```



###### 3. 消息表达式 #{...}

消息表达式#{...}主要用于Thymeleaf模板页面国际化内容的动态替换和展示，使用消息表达式#{...}
进行国际化设置时，还需要提供一些国际化配置文件。



###### 4. 链接表达式 @{...}

- 功能：生成 URL 地址，自动拼接上下文路径（如项目上下文路径为 `/demo`，则 `@{/user}` 生成 `/demo/user`）；

- 示例：

  ```html
  <!-- 绝对路径 -->
  <a th:href="@{http://localhost:8080/user/detail(id=${user.id})}">查看详情</a>
  <!-- 相对路径（带参数） -->
  <a th:href="@{/user/detail(id=${user.id}, name=${user.name})}">查看详情</a>
  <!-- 无参数相对路径 -->
  <a th:href="@{/user/list}">用户列表</a>
  ```

  

###### 5. 片段表达式 ~{...}

片段表达式~{...}用来标记一个片段模板，并根据需要移动或传递给其他模板。其中，最常见的用法是使用th:insert或th:replace属性插入片段

```html
<div th:insert="~{thymeleafDemo::title}"></div>
```

> 上述代码中，使用th:insert属性将title片段模板引用到该标签中。thymeleafDemo为模板名称，Thymeleaf会自动查找“/resources/templates/”目录下的thymeleafDemo模板，title为片段名称



#### 4.2.2 Thymeleaf 基本使用

##### （1）依赖引入（pom.xml）

```xml
<!-- Thymeleaf 依赖（Spring Boot 整合包） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

##### （2）核心配置（application.properties）

在全局配置文件中配置Thymeleaf模板的一些参数，一般Web项目都会使用下列配置：

```properties
# Thymeleaf 模板缓存（开发时关闭，避免修改后不生效；上线时开启）
spring.thymeleaf.cache=false
# 模板编码（UTF-8）
spring.thymeleaf.encoding=UTF-8
# 模板模式（HTML5）
spring.thymeleaf.mode=HTML5
# 模板页面存放路径（默认 classpath:/templates/ 该文件夹下不对用户开放，只能请求转发）
spring.thymeleaf.prefix=classpath:/templates/
# 模板页面后缀（默认 .html）
spring.thymeleaf.suffix=.html
```

##### （3）静态资源访问

Spring Boot 默认扫描以下目录的静态资源（CSS、JS、图片等）：

- `classpath:/static/`（推荐，resources/static/）；
- `classpath:/public/`（resources/public/）；
- `classpath:/resources/`（resources/resources/）。

**访问方式**：通过 `@{/资源路径}` 访问，如：

```html
<!-- 访问 resources/static/css/bootstrap.min.css -->
<link th:href="@{/css/bootstrap.min.css}" rel="stylesheet">
<!-- 访问 resources/static/img/login.jpg -->
<img th:src="@{/img/login.jpg}" width="72" height="72">
```



#### 4.2.3 案例：实现动态登录页面

**需求**：创建登录页面，动态显示当前年份，引入静态资源（CSS、图片）。

##### （1）创建Spring Boot项目，引入Thymeleaf依赖

![0120](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0120.png)

##### （2）编写配置文件application.properties

```properties
# thymeleaf页面缓存设置（默认为true），开发中方便调试应设置为false，上线稳定后应保持默认true
spring.thymeleaf.cache=false
```

##### （3）创建 Controller

```java
@Controller
public class LoginController {

    /**
     * 跳转登录页，并传递当前年份
     */
    @RequestMapping("/toLoginPage")
    public String toLoginPage(Model model) {
        // 向页面传递当前年份（Calendar.getInstance().get(Calendar.YEAR)）
        model.addAttribute("currentYear", Calendar.getInstance().get(Calendar.YEAR));
        return "login"; // 跳转至 templates/login.html
    }
}
```

##### （4）创建模板页面（templates/login.html）

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>用户登录界面</title>
    <!-- 引入静态 CSS 文件 -->
    <link th:href="@{/login/css/bootstrap.min.css}" rel="stylesheet">
    <link th:href="@{/login/css/signin.css}" rel="stylesheet">
</head>
<body class="text-center">
    <!-- 用户登录表单 -->
    <form class="form-signin">
        <!-- 引入静态图片 -->
        <img class="mb-4" th:src="@{/login/img/login.jpg}" width="72" height="72">
        <h1 class="h3 mb-3 font-weight-normal">请登录</h1>
        <!-- 动态占位符 -->
        <input type="text" class="form-control" th:placeholder="用户名" required autofocus>
        <input type="password" class="form-control" th:placeholder="密码" required>
        <div class="checkbox mb-3">
            <label>
                <input type="checkbox" value="remember-me"> 记住我
            </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
        <!-- 动态显示年份（当前年份 - 下一年） -->
        <p class="mt-5 mb-3 text-muted">© <span th:text="${currentYear}">2019</span>-<span th:text="${currentYear}+1">2020</span></p>
    </form>
</body>
</html>
```

> 通过“xmlns:th="http://www.thymeleaf.org"”引入了Thymeleaf模板标签；
> 使用“th:href”和“th:src”分别引入了两个外联的样式文件和一个图片；
> 使用“th:text”引入了后台动态传递过来的当前年份currentYea

##### （5）测试效果

启动项目，访问 `http://localhost:8080/toLoginPage`

![0121](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0121.png)





## 5. Spring Boot 实战演练

### 5.1 实战技能补充：lombok

Lombok 是 Java 开发中的一个工具库，通过注解简化代码编写。

它能自动生成 Java 类中常用的模板代码，比如 getter/setter、构造函数、toString () 等方法，无需手动编写，减少冗余代码，让代码更简洁。

例如，在类上添加 `@Data` 注解，Lombok 会在编译时自动生成该类所有字段的 getter、setter、toString、equals 和 hashCode 方法。

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.12</version>
    <!--只在编译阶段生效-->
    <scope>provided</scope>
</dependency>
```



### 5.2 实战：实现用户 CRUD 功能

需求：实现用户CRUD功能	

#### （1）创建springboot工程	

通过 Spring Initializr 构建项目，勾选以下依赖：

Spring Web，MyBatis Framework，MySQL Driver，Lombok。

![0122](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0122.png)

#### （2）补充依赖（pom.xml）

```xml
<!-- 引入 Druid 数据源依赖 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.3</version>
</dependency>
```

#### （3）实体类（User.java）

```java
@Data // Lombok 注解：自动生成 getter/setter/toString/equals/hashCode 方法
public class User implements Serializable {
    private Integer id;         // 用户 ID
    private String username;    // 用户名
    private String password;    // 密码
    private String birthday;    // 生日（字符串格式，如 "2000-01-01"）
    private static final long serialVersionUID = 1L; // 序列化版本号
}
```

#### （4）Mapper 接口及 XML 映射文件

使用`MybatisCodeHelper-Pro`插件自动生成

Mapper 接口（UserMapper.java）：

```java
package com.gvc.mapper;
@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> getAllUsers();
}
```

XML 映射文件（UserMapper.xml）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.lagou.mapper.UserMapper">
    <!-- 结果集映射：数据库表字段 → 实体类属性 -->
    <resultMap id="BaseResultMap" type="com.lagou.pojo.User">
        <id column="id" jdbcType="INTEGER" property="id" />
        <result column="username" jdbcType="VARCHAR" property="username" />
        <result column="password" jdbcType="VARCHAR" property="password" />
        <result column="birthday" jdbcType="VARCHAR" property="birthday" />
    </resultMap>

    <!-- 通用字段列表（复用 SQL） -->
    <sql id="Base_Column_List">
        id, username, password, birthday
    </sql>

    <!-- 根据 ID 查询用户 -->
    <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        select 
        <include refid="Base_Column_List" /> <!-- 引入通用字段列表 -->
        from user
        where id = #{id,jdbcType=INTEGER}
    </select>

    <!-- 查询所有用户 -->
    <select id="queryAll" resultType="com.lagou.pojo.User">
        select <include refid="Base_Column_List" />
        from user
    </select>

    <!-- 新增用户（仅非空字段） -->   
    <!-- 更新用户（仅非空字段） -->
	<!-- 根据 ID 删除用户 -->
	...
</mapper>
```

#### （5）Service 层（接口与实现类）

Service 接口（UserService.java）

```java
package com.gvc.service;
public interface UserService {
    public List<User> getAllUsers();
    public User getUserByID(int id);
    public boolean addUser(User user);
    public boolean updateUser(User user);
    public boolean deleteUser(int id);
}

```

Service 实现类（UserServiceImpl.java）

```java
package com.gvc.service.impl;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    public User getUserByID(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean addUser(User user) {
//        int inserted = userMapper.insert(user);//插入任意值
        int inserted = userMapper.insertSelective(user); //属性非空才会拼入sql语句,高效
        return inserted > 0;
    }

    @Override
    public boolean updateUser(User user) {
        int updated = userMapper.updateByPrimaryKeySelective(user);
        return updated > 0;
    }

    @Override
    public boolean deleteUser(int id) {
        return userMapper.deleteByPrimaryKey(id) > 0;
    }
}

```

#### （6）Controller 层（RESTful 风格）

```java
@RestController // 组合注解 = @Controller + @ResponseBody（返回 JSON/字符串，不跳转页面）
@RequestMapping("/user") // 一级请求路径
public class UserController {

    @Autowired // 注入 UserService 实例
    private UserService userService;

    /**
     * RESTful 风格说明：
     * - 查询：GET 请求
     * - 新增：POST 请求
     * - 更新：PUT 请求
     * - 删除：DELETE 请求
     */

    /**
     * 查询所有用户
     * 请求路径：/user/getAllUsers（GET）
     * @return 用户列表
     */
    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * 根据 ID 查询用户
     * 请求路径：/user/query/{id}（GET）
     * @param id 用户 ID（路径参数）
     * @return 单个用户信息
     */
    @GetMapping("/getUser/{id}")
    public User getUserByID(@PathVariable Integer id) {
        return userService.getUserByID(id);
    }

    /**
     * 根据 ID 删除用户
     * 请求路径：/user/delete/{id}（DELETE）
     * @param id 用户 ID（路径参数）
     * @return 操作结果提示
     */
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "删除成功";
    }

    /**
     * 新增用户
     * 请求路径：/user/insert（POST）
     * @param user 用户对象（请求参数自动封装）
     * @return 操作结果提示
     */
    @PostMapping("/addUser")
    public String addUser(User user) {
        userService.addUser(user);
        return "新增成功";
    }

    /**
     * 更新用户
     * 请求路径：/user/update（PUT）
     * @param user 用户对象（需包含 ID，请求参数自动封装）
     * @return 操作结果提示
     */
    @PutMapping("/updateUser")
    public String updateUser(User user) {
        userService.updateUser(user);
        return "修改成功";
    }
}
```

#### （7）全局配置文件（application.yml）

```yaml
server:
  port: 8080
spring:
  datasource:
    name: druid
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://localhost:3306/springboot_db?serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: 0709
mybatis:
  mapper-locations: classpath:mapper/*Mapper.xml
  configuration:
    map-underscore-to-camel-case: true
```

#### （8）启动类

```java
@SpringBootApplication
@MapperScan("com.gvc.mapper") // 扫描 Mapper 接口所在包（com.lagou.mapper）
public class Springbootdemo5Application {
    public static void main(String[] args) {
        SpringApplication.run(Springbootdemo5Application.class, args);
    }
}
```

#### （9）测试（使用 Postman）

通过 Postman 发送 HTTP 请求，测试 CRUD 功能

![0123](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0123.png)

![0124](https://github.com/gvcheng/note_images/blob/main/springboot_img/springboot0124.png)



## 6. Spring Boot 项目部署

需要添加打包组件项目中的资源、配置、依赖包打到一个jar包中；可以使用maven的`package`命令；

Spring Boot 项目默认支持 JAR 包部署，无需额外安装 Tomcat，直接通过 `java -jar` 命令运行。

#### （1）配置打包插件（pom.xml）

确保项目中包含 Spring Boot 打包插件，否则打包后的 JAR 包无法运行：

```xml
<build>
    <plugins>
        <!-- Spring Boot 打包插件：将项目打包为可执行 JAR，包含所有依赖和清单文件 -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

#### （2）打包步骤

打开 IDEA 右侧「Maven」面板：展开项目 → Lifecycle → 双击 `package`；

等待打包完成，打包后的 JAR 包位于项目根目录的 `target` 文件夹下（如 `Springboot_userCRUD-0.0.1-SNAPSHOT.jar`）。

#### （3）运行 JAR 包

打开命令行，进入 `target` 目录，执行命令`java -jar 包名`（如 `java -jar Springboot_userCRUD-0.0.1-SNAPSHOT.jar`）；

项目启动成功后，访问 `http://localhost:8080/user/getAllUsers`，验证服务是否正常。

#### （4）后台运行（Linux 环境）

在 Linux 服务器部署时，若希望项目后台运行（关闭命令行不停止服务），可执行以下命令：

```bash
# 后台运行 JAR 包，输出日志到 springboot.log 文件
nohup java -jar springbootdemo5-0.0.1-SNAPSHOT.jar > springboot.log 2>&1 &
```

> - `ohup`：忽略挂断信号，确保服务后台运行；
> - `> springboot.log`：将标准输出重定向到 `springboot.log` 文件；
> - `2>&1`：将错误输出重定向到标准输出，统一写入日志文件；
> - 最后一个 `&`：将进程放入后台运行。

