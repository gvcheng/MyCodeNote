**01_SpringMVC 基本应用**

**课程任务主要内容**

```markdown
* SpringMVC 简介
* SpringMVC 组件概述
* SpringMVC 请求
* SpringMVC 响应
* 静态资源开启
```



## 一、SpringMVC 简介

### 1.1 MVC 模式

MVC 是软件工程中的一种软件架构模式，核心是分离业务逻辑与显示界面，包含三个核心角色：

- **M（Model，模型）**：处理业务逻辑，封装实体数据（对应项目中的 Service 层、Dao 层）。
- **V（View，视图）**：展示内容（如 JSP、HTML 页面）。
- **C（Controller，控制器）**：负责调度分发，核心职责包括：1. 接收客户端请求；2. 调用模型处理业务；3. 转发到视图展示结果。

![0101](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0101.png)

### 1.2 SpringMVC 概述

SpringMVC 是基于 Java 的轻量级 Web 框架，实现了 MVC 设计模式，属于 Spring Framework 的后续产品，已融合到 Spring Web Flow 中。

#### 核心特点

- 主流框架：目前最优秀的 MVC 框架之一，自 Spring 3.0 发布后全面超越 Struts2。
- 简化开发：通过注解让普通 Java 类成为请求控制器，无需实现任何接口。
- 支持 RESTful：原生支持 RESTful 编程风格的请求

![0102](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0102.png)

#### 总结

SpringMVC 框架封装了传统 Servlet 的共有行为（如参数封装、视图转发、请求分发等），开发者只需关注核心业务逻辑，无需重复编写通用 Servlet 代码。



### 1.3 SpringMVC 快速入门

#### 需求

客户端发起请求，服务器接收请求后执行业务逻辑，并完成视图跳转。

#### 步骤分析

```markdown
1. 创建 Web 项目，导入 SpringMVC 相关坐标

2. 配置 SpringMVC 前端控制器 DispatcherServlet

3. 编写 Controller 类和视图页面

4. 用注解配置 Controller 业务方法的映射地址

5. 配置 SpringMVC 核心文件`spring-mvc.xml`
```

##### (1) 创建 Web 项目，导入 SpringMVC 相关坐标

```xml
<dependencies>
      <!-- Spring MVC 核心依赖 -->
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-webmvc</artifactId>
          <version>5.3.20</version> <!-- 版本可根据需求调整，5.x 较稳定 -->
      </dependency>
      <!-- Servlet API（需与 Tomcat 版本兼容，如 Tomcat 9 对应 servlet-api 4.0） -->
      <dependency>
          <groupId>javax.servlet</groupId>
          <artifactId>javax.servlet-api</artifactId>
          <version>4.0.1</version>
          <scope>provided</scope> <!-- 避免与容器自带的Servlet API冲突 -->
      </dependency>
      <dependency>
      <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>3.8.1</version>
        <scope>test</scope>
    </dependency>
  </dependencies>
```

##### (2) 配置 SpringMVC 前端控制器 DispatcherServlet

在`web.xml`中配置前端控制器，作为请求入口：

```xml
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    <display-name>Archetype Created Web Application</display-name>
    <!-- springMVC的前端控制器：DispatcherServlet -->
    <servlet>
        <servlet-name>dispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring-mvc.xml</param-value>
        </init-param>

        <!--   在应用启动时就完成servlet的实例化和初始化操作   -->
        <load-on-startup>2</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>dispatcherServlet</servlet-name>
        <!--   '/' 表示会匹配到所有的访问路径，但不会匹配 ‘*.jsp’带有后缀名类似的方法url
                /add /login /uodate √      /a.jsp × -->
        <url-pattern>/</url-pattern>
    </servlet-mapping>

</web-app>

```

##### (3) 编写 Controller 类和视图页面

**Controller 类**：处理请求的核心类

```java
public class UserController {
    // 业务方法：处理请求并返回视图路径
    public String quick() {
        System.out.println("quick running.....");
        // 返回JSP页面路径（物理路径）
        return "/WEB-INF/pages/success.jsp";
    }
}
```

**视图页面**：`/WEB-INF/pages/success.jsp`（WEB-INF 目录下的资源无法直接访问，需通过 Controller 转发）

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>success</title>
</head>
<body>
    <h3>请求成功!</h3>
</body>
</html>
```

##### (4) 用注解配置 Controller 业务方法的映射地址

通过`@Controller`将类注入 Spring 容器，`@RequestMapping`指定请求 URL 与方法的映射：

```java
@Controller // 标记为SpringMVC控制器，注入Spring容器
public class UserController {
    // 映射URL：http://localhost:8080/项目名/quick
    @RequestMapping("/quick")
    public String quick() {
        System.out.println("quick running.....");
        return "/WEB-INF/pages/success.jsp";
    }
}
```

##### (5) 配置 SpringMVC 核心文件`spring-mvc.xml`

主要配置注解扫描（识别`@Controller`等注解）：

```xml
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

    <!-- 配置注解扫描：扫描Controller所在包 -->
    <context:component-scan base-package="com.lagou.controller"/>
</beans>
```

![0105](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0105.png)



### 1.4 Web 工程执行流程

![0103](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0103.png)



### 1.5 知识小结

```markdown
* SpringMVC 是 MVC 设计模式的实现，属于轻量级 Web 框架。

* SpringMVC 开发核心步骤：
  1. 创建 Web 项目，导入 SpringMVC、Servlet、JSP 坐标。
  2. 配置前端控制器`DispatcherServlet`，指定核心配置文件路径。
  3. 编写`Controller`类（处理请求）和视图页面（展示结果）。
  4. 用`@Controller`和`@RequestMapping`注解配置映射关系。
  5. 配置`spring-mvc.xml`，开启注解扫描。
```





## 二、SpringMVC 组件概述 

### 2.1 SpringMVC 的执行流程

![0104](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0104.png)

```markdown
1. 用户发送请求至前端控制器DispatcherServlet。 

2. DispatcherServlet收到请求调用HandlerMapping处理器映射器。

3. 处理器映射器找到具体的处理器(可以根据xml配置、注解进行查找)，生成处理器对象及处理器拦截器(如果有则生成)一并返回给DispatcherServlet。 

4. DispatcherServlet调用HandlerAdapter处理器适配器。

5. HandlerAdapter经过适配调用具体的处理器(Controller,也叫后端控制器)｡

6. Controller执行完成返回ModelAndView｡

7. HandlerAdapter将controller执行结果ModelAndView返回给DispatcherServlet｡

8. DispatcherServlet将ModelAndView传给ViewReslover视图解析器｡

9. ViewReslover解析后返回具体View｡

10. DispatcherServlet根据View进行渲染视图(即将模型数据填充至视图中)｡

11. DispatcherServlet将渲染后的视图响应响应用户｡
```



### 2.2 SpringMVC 组件解析

```markdown
## 1. 前端控制器：DispatcherServlet

- 作用：整个流程的控制中心，接收所有请求，调用其他组件处理请求，降低组件耦合度。
- 特点：无需开发者编写，SpringMVC 内置实现。

## 2. 处理器映射器：HandlerMapping

- 作用：根据请求 URL 查找对应的`Handler`（控制器方法），支持 XML 配置、注解（如`@RequestMapping`）等映射方式。
- 特点：无需开发者编写，通过`mvc:annotation-driven`自动注册。

## 3. 处理器适配器：HandlerAdapter

- 作用：适配不同类型的`Handler`，调用`Handler`执行业务逻辑（适配器模式）。
- 特点：无需开发者编写，通过`mvc:annotation-driven`自动注册。

## 4. 处理器：Handler（开发者编写）

- 作用：即`Controller`类，处理具体业务逻辑，接收请求参数并返回`ModelAndView`。
- 开发方式：用`@Controller`注解标记普通 Java 类，`@RequestMapping`映射请求。

## 5. 视图解析器：ViewResolver

- 作用：将`ModelAndView`中的逻辑视图名（如`success`）解析为物理视图路径（如`/WEB-INF/pages/success.jsp`），生成`View`对象。

## 6. 视图：View（开发者编写）
- 作用：展示数据，支持 JSP、Freemarker、PDF 等类型，最常用 JSP。
- 开发方式：编写 JSP 页面，通过 EL 表达式或 JSTL 获取模型数据

# 笔试题：SpringMVC 的三大组件是什么？
   处理器映射器（HandlerMapping）、处理器适配器（HandlerAdapter）、视图解析器（ViewResolver）。
```

```xml
```







### 2.3 SpringMVC 注解解析

#### 1. @Controller

- **作用**：标记 Java 类为 SpringMVC 控制器，将类注入 Spring 容器。
- **依赖配置**：需配合注解扫描，否则 Spring 无法识别该注解：

```xml
<context:component-scan base-package="com.gvc.controller"/>
```



#### 2. @RequestMapping

- **作用**：建立请求 URL 与Controller方法的映射关系

```markdown
* 位置：
	1.类上：请求URL的第一级访问目录。此处不写的话，就相当于应用的根目录。写的话需要以/开头。
		它出现的目的是为了使我们的URL可以按照模块化管理:
			用户模块
                /user/add
                /user/update
                /user/delete
                ...
			账户模块
                /account/add
                /account/update
                /account/delete
* 2.方法上：请求URL的第二级访问目录，和一级目录组成一个完整的 URL 路径。

* 属性：
    1.value：用于指定请求的URL。它和path属性的作用是一样的
    2.method：用来限定请求的方式
    3.params：用来限定请求参数的条件
        例如：params={"accountName"} 表示请求参数中必须有accountName
             pramss={"money!100"} 表示请求参数中money不能是100
```



### 2.4 知识小结

```markdown
* SpringMVC的三大组件（框架提供）
	处理器映射器:   HandlerMapping
	处理器适配器:   HandlerAdapter
	视图解析器:   ViewResolver
	
* 开发者编写
	处理器:   Handler（Controller 类）
	视图:   View（JSP 页面）
	
* 核心注解 
    @Controller（标记控制器）
    @RequestMapping（映射请求）。
```





## 三、SpringMVC 的请求

### 3.1 请求参数类型介绍

客户端请求参数格式为`name=value&name=value`，SpringMVC 支持自动类型转换和数据封装，可接收以下类型参数：

- 基本类型参数（int、String、Double 等）
- 对象类型参数（POJO，如 User、Account）
- 数组类型参数（如 Integer []、String []）
- 集合类型参数（如 List<User>、Map<String, User>）



### 3.2 获取基本类型参数

**规则**：Controller 方法参数名与请求参数`name`一致，SpringMVC 自动映射并完成类型转换（如 String→int）。

**示例**：

1. 前端请求（超链接）：

   ```html
   <a href="${pageContext.request.contextPath}/user/simpleParam?id=1&username=杰克">
       基本类型参数
   </a>
   ```

2. Controller 方法：

   ```java
   @RequestMapping("/simpleParam")
   public String simpleParam(Integer id, String username) {
       System.out.println("id：" + id); // 输出：1
       System.out.println("username：" + username); // 输出：杰克
       return "success"; // 逻辑视图名，视图解析器拼接为/WEB-INF/pages/success.jsp
   }
   ```

   ![0106](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0106.png)



### 3.3 获取对象类型参数

**规则**：Controller 方法参数为 POJO 对象，POJO 的属性名需与请求参数`name`一致，SpringMVC 自动封装数据

**示例**：

1. 前端表单（POST 请求）：

   ```html
   <form action="${pageContext.request.contextPath}/user/pojoParam" method="post">
       编号：<input type="text" name="id"><br>
       用户名：<input type="text" name="username"><br>
       <input type="submit" value="提交对象参数">
   </form>
   ```

2. POJO 类（User）：

   ```java
   public class User {
       private Integer id;
       private String username;
       // 必须提供setter/getter方法（SpringMVC通过反射赋值）
       public Integer getId() { return id; }
       public void setId(Integer id) { this.id = id; }
       public String getUsername() { return username; }
       public void setUsername(String username) { this.username = username; }
   }
   ```

3. Controller 方法：

   ```java
   @RequestMapping("/pojoParam")
   public String pojoParam(User user) {
       System.out.println("User：" + user); // 输出：User{id=1, username='张三'}
       return "success";
   }
   ```



> POJO 的属性名需与请求参数`name`一致![0107](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0107.png)



### 3.4 中文乱码过滤器

**问题**：POST 请求时，客户端提交的中文参数会出现乱码。

**解决方案**：在`web.xml`中配置 SpringMVC 内置的字符编码过滤器：

```xml
<!-- 全局中文乱码过滤器 -->
<filter>
    <filter-name>CharacterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <!-- 设置编码格式为UTF-8 -->
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
<!-- 过滤所有请求 -->
<filter-mapping>
    <filter-name>CharacterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```



### 3.5 获取数组类型参数

**规则**：Controller 方法参数为数组，数组名与请求参数`name`一致，SpringMVC 自动封装所有参数值。

**示例**：

1. 前端表单（复选框）：

   ```html
   <form action="${pageContext.request.contextPath}/user/arrayParam">
       选择编号：<br>
       <input type="checkbox" name="ids" value="1">1<br>
       <input type="checkbox" name="ids" value="2">2<br>
       <input type="checkbox" name="ids" value="3">3<br>
       <input type="submit" value="提交数组参数">
   </form>
   ```

2. Controller 方法：

   ```java
   @RequestMapping("/arrayParam")
   public String arrayParam(Integer[] ids) {
       System.out.println("选中的ids：" + Arrays.toString(ids)); // 输出：[1,2,3]
       return "success";
   }
   ```

> 图示：
>
> ![0108](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0108.png)



### 3.6 获取集合（复杂）类型参数

**规则**：集合参数（如 List、Map）不能直接作为方法参数，需封装到 POJO 中（称为 “包装类”）。

**示例**：

1. 包装类（QueryVo）：

   ```java
   public class QueryVo {
       private String keyword; // 普通参数
       private User user; // 对象参数
       private List<User> userList; // List集合参数
       private Map<String, User> userMap; // Map集合参数
       // setter/getter方法。。。
   }
   ```

2. 前端表单：

   ```html
   <form action="${pageContext.request.contextPath}/user/queryParam" method="post">
       搜索关键字：<input type="text" name="keyword"><br>
       
       用户信息（对象）：<br>
       编号：<input type="text" name="user.id" placeholder="编号"><br>
       用户名：<input type="text" name="user.username" placeholder="姓名"><br>
       
       List集合（第一个用户）：<br>
       编号：<input type="text" name="userList[0].id" placeholder="编号"><br>
       用户名：<input type="text" name="userList[0].username" placeholder="姓名"><br>
       List集合（第二个用户）：<br>
       编号：<input type="text" name="userList[1].id" placeholder="编号"><br>
       用户名：<input type="text" name="userList[1].username" placeholder="姓名"><br>
       
       Map集合（第一个用户，key为u1）：<br>
       编号：<input type="text" name="userMap['u1'].id" placeholder="编号"><br>
       用户名：<input type="text" name="userMap['u1'].username" placeholder="姓名"><br>
       Map集合（第一个用户，key为u2）：<br>
       编号：<input type="text" name="userMap['u2'].id" placeholder="编号"><br>
       用户名：<input type="text" name="userMap['u2'].username" placeholder="姓名"><br>
       
       <input type="submit" value="提交复杂参数">
   </form>
   ```

3. Controller 方法：

   ```java
   //获取复杂类型请求参数
   @RequestMapping("/queryParam")
   public String queryParam(QueryVo queryVo){
       System.out.println(queryVo);
       return "success";
   }
   ```



### 3.7 自定义类型转换器

**问题**：SpringMVC 默认支持常用类型转换（如 String→int），但对特殊格式（如日期`yyyy-MM-dd`）无法自动转换，需自定义转换器。

**实现步骤**：

1. 自定义转换器类：实现`Converter<S, T>`接口（S 为源类型，T 为目标类型）。

   ```java
   // 字符串转日期转换器
   public class DateConverter implements Converter<String, Date> {
       @Override
      //source就是表单传递过来的请求参数： "2025-10-11"
       public Date convert(String source) {
           //将字符串转换成日期对象
           Date date = null;
           try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(source);
           } catch (ParseException e) {
               e.printStackTrace();
           }
           return date;
       }
   }
   ```

2. 配置转换器：在`spring-mvc.xml`中注册转换器，并关联到`mvc:annotation-driven`。

   ```xml
   <!-- 1. 配置转换器工厂 -->
   <bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
       <property name="converters">
           <set>
               <!-- 注册自定义转换器 -->
               <bean class="com.lagou.converter.DateConverter"></bean>
           </set>
       </property>
   </bean>
   
   <!-- 2. 处理器映射器/适配器增强：关联转换器 -->
   <mvc:annotation-driven conversion-service="conversionService"></mvc:annotation-driven>
   ```

3. 前端请求与 Controller 方法：

   ```html
   <!-- 前端表单 -->
   <form action="${pageContext.request.contextPath}/user/converterParam" method="post">
       生日（yyyy-MM-dd）：<input type="text" name="birthday">
       <input type="submit" value="提交日期">
   </form>
   ```

   ```java
   // Controller方法
   @RequestMapping("/converterParam")
   public String converterParam(Date birthday) {
       System.out.println("生日：" + birthday); // 输出：Fri Oct 13 00:00:00 CST 2023
       return "success";
   }
   ```

   

### 3.8 相关注解

#### 1. @RequestParam

**作用**：当请求参数`name`与 Controller 方法参数名不一致时，通过使用@RequestParam显式绑定参数。

**示例 **:

```html
<!-- 前端请求：参数名为pageNo，方法参数名为pageNum -->
<a href="${pageContext.request.contextPath}/user/findByPage?pageNo=2">分页查询</a>
```

```java
@RequestMapping("/findByPage")
public String findByPage(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
    System.out.println("当前页：" + pageNum); 
    System.out.println("每页条数：" + pageSize); 
    return "success";
}
```

> **图示：**
>
> ![0109](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0109.png)



#### 2. @RequestHeader

**作用**：获取请求头中的数据（如 Cookie、User-Agent）。

**示例**：

```java
// 获取请求头中的Cookie信息
@RequestMapping("/requestHead")
public String requestHead(@RequestHeader("cookie") String cookie) {
    System.out.println("Cookie：" + cookie);
    return "success";
}
```



#### 3. @CookieValue

**作用**：直接获取 Cookie 中的指定属性值（如 JSESSIONID）。

**示例**：

```java
// 获取Cookie中JSESSIONID的值
@RequestMapping("/cookieValue")
public String cookieValue(@CookieValue("JSESSIONID") String jsessionId) {
    System.out.println("JSESSIONID：" + jsessionId);
    return "success";
}
```



### 3.9 获取 Servlet 相关 API

SpringMVC 支持将原始 Servlet API 对象（如 HttpServletRequest、HttpServletResponse）作为 Controller 方法参数进行注入。

**示例**：

```java
@RequestMapping("/servletAPI")
public String servletAPI( HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    System.out.println("Request：" + request);
    System.out.println("Response：" + response);
    System.out.println("Session：" + session);
    // 可通过request设置域属性
    request.setAttribute("msg", "通过Servlet API设置的消息");
    return "success";
}
```





## 四、SpringMVC 的响应

### 4.1 SpringMVC 响应方式介绍

**页面跳转**

1. 返回字符串逻辑视图
2. void 原始 Servlet API
3. ModelAndView

**返回数据**

1. 直接返回字符串数据
2. 将对象 / 集合转为 JSON 返回（任务二演示）



### 4.2 返回字符串逻辑视图

**直接返回字符串**: 此种方式会将返回的字符串与视图解析器的前后缀拼接后跳转到指定页面。

```java
@RequestMapping("/returnString")
public String returnString() {
	return "success";
}
```

![0110](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0110.png)



### 4.3 void 原始 ServletAPI

通过`request`、`response`对象实现响应，可直接响应数据、转发或重定向页面。

```java
@RequestMapping("/returnVoid")
public void returnVoid(HttpServletRequest request, HttpServletResponse response) throws Exception {
    // 1. 通过response直接响应数据
    response.setContentType("text/html;charset=utf-8");
    response.getWriter().write("J1vyC");
    
    request.setAttribute("username", "GVCHENG");
    // 2. 通过request实现转发
    request.getRequestDispatcher("/WEB-INF/pages/success.jsp").forward(request, response);
    
    // 3. 通过response实现重定向
    response.sendRedirect(request.getContextPath() + "/index.jsp");
}
```



### 4.4 转发和重定向

#### forward 转发

企业开发中常用返回字符串逻辑视图实现页面跳转，本质就是请求转发。

使用`forward:`时，路径需写实际视图 URL，不能写逻辑视图，相当于：

```java
request.getRequestDispatcher("url").forward(request,response)
```

使用请求转发,既可以转发到jsp,也可以转发到其他的控制器方法｡

```java
	//forward:关键字请求转发
    @RequestMapping("/forward")
    public String forward(Model model){
        //模型中设置值
        model.addAttribute("username","J1vyC");
        //使用请求转发，既可以转发jsp,也可以转发到其他的Controller方法
        return "forward:/WEB-INF/pages/success.jsp";
//        return "forward:/product/findAll";
    }
```



#### Redirect 重定向

我们可以不写虚拟目录,springMVC框架会自动拼接,并且将Model中的数据拼接到url地址上

```java
    //redirect:关键字重定向，当使用redirect或forward关键字时不会调用视图解析器拼接前后缀
    @RequestMapping("/redirect")
    public String redirect(Model model){
        //底层使用的仍是 request.setAttribute(),request域范围为一次请求,而重定向为两次请求大于request域范围
        model.addAttribute("username","redirect:_GVCheng");
        return "redirect:/index.jsp";
    }
```



### 4.5 ModelAndView

#### 方式一：手动创建 ModelAndView 对象

在 Controller 方法中创建并返回 ModelAndView 对象，设置视图名称和模型数据。

```java
	//ModelAndView 页面跳转方式一：手动创建 ModelAndView 对象
    @RequestMapping("/returnModelAndView")
    public ModelAndView returnModelAndView(){
        //model模型：封装存放数据
        //view视图：展示数据
        ModelAndView modelAndView = new ModelAndView();
        //设置数据模型
        modelAndView.addObject("username","模型视图 方式一");
        //设置视图名称：视图解析器解析modelAndView 拼接前缀和后缀
        modelAndView.setViewName("success");
        return modelAndView;
    }
```



#### 方式二：形参声明 ModelAndView

在 Controller 方法形参上直接声明 ModelAndView，无需手动创建，在方法中直接使用该对象设置视图和模型数据。

```java
    //ModelAndView 页面跳转方式二：形参声明 ModelAndView
    @RequestMapping("/returnModelAndView2")
    public ModelAndView returnModelAndView2(ModelAndView modelAndView){
        modelAndView.addObject("username","模型视图 方式二");
        modelAndView.setViewName("success");
        return modelAndView;
    }
```



### 4.6 @SessionAttributes

若需在多个请求之间共用数据，可在控制器类上标注`@SessionAttributes`，配置需存放在 session 中的数据范围，SpringMVC 会将 model 中对应数据暂存到 HttpSession 中。

**注意**：`@SessionAttributes`只能定义在类上。

```java
@Controller
@SessionAttributes("username") // 向request域存入key为username的数据时，同步到session域中
public class UserController {
    @RequestMapping("/forward")
    public String forward(Model model) {
        model.addAttribute("username", "子慕");
        return "forward:/WEB-INF/pages/success.jsp";
    }
    @RequestMapping("/returnString")
    public String returnString() {
        return "success";
    }
}
```



### 4.7 知识小结

```markdown
* 页面跳转常用返回字符串逻辑视图：
  1. `forward`转发：可通过 Model 向 request 域中设置数据。
  2. `redirect`重定向：直接写资源路径，虚拟目录由 SpringMVC 框架自动拼接。
  
* 向 request 域中存储数据：通过`Model`对象的`addAttribute`方法
		`model.addAttribute("username", "张三")`。
```





## 五、静态资源访问的开启

当加载静态资源（如 jquery 文件）时，若 SpringMVC 前端控制器 DispatcherServlet 的`url-pattern`配置为`/`（缺省），会对所有静态资源进行处理，导致 Tomcat 内置的 DefaultServlet 无法处理静态资源，可通过以下两种方式放行静态资源。

![0111](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0111.png)

### 方式一：指定放行资源

在 spring-mvc.xml 配置文件中，通过`mvc:resources`标签**指定**需放行的静态资源路径：

```xml
<!--在springmvc配置文件中指定放行资源-->
<mvc:resources mapping="/js/**" location="/js/"/>
<mvc:resources mapping="/css/**" location="/css/"/>
<mvc:resources mapping="/img/**" location="/img/"/>
```

### 方式二：开启 DefaultServlet 处理静态资源

在 spring-mvc.xml 配置文件中，通过`mvc:default-servlet-handler`标签开启 DefaultServlet 放行**全部**静态资源：

```xml
<!--在springmvc配置文件中开启DefaultServlet处理静态资源-->
<mvc:default-servlet-handler/>
```

该方式会让 SpringMVC 将无法处理的静态资源请求交给 Tomcat 的 DefaultServlet 处理。

