

**SpringMVC_02：SpringMVC 进阶**

主要内容：

```markdown
* ajax异步交互
* RESTful
* 文件上传
* 异常处理
* 拦截器
```





## 一、AJAX 异步交互

AJAX（Asynchronous JavaScript and XML，异步 JavaScript 和 XML）是一种**在不重新加载整个网页的前提下，与服务器进行局部数据交互的技术**。 “异步”—— 浏览器发送请求后无需等待服务器响应，可继续执行其他操作，响应返回后再动态更新页面局部内容，从根本上提升了网页的交互体验。

AJAX 的核心是 “**异步通信 + 局部更新**”，它彻底改变了传统网页 “刷新 - 等待 - 刷新” 的交互模式，是现代前端开发（如单页应用 SPA）的基础技术之一。

| 对比维度     | 同步交互（传统方式）                          | 异步交互（AJAX）                                         |
| ------------ | --------------------------------------------- | -------------------------------------------------------- |
| **页面刷新** | 每次请求都会重新加载整个网页，出现 “白屏等待” | 仅更新页面需要变化的局部区域，无整体刷新                 |
| **用户体验** | 等待期间无法操作页面，体验卡顿                | 请求发送后可继续操作其他功能（如输入、点击），无感知等待 |
| **数据传输** | 传递整个页面的 HTML 代码，冗余数据多          | 仅传递需要的局部数据（如 JSON 格式），效率高             |
| **典型场景** | 早期表单提交（如登录后跳转新页面）            | 搜索框联想、购物车数量更新、点赞 / 收藏                  |



### 核心依赖

SpringMVC 默认用`MappingJackson2HttpMessageConverter`对 json 数据进行转换，需要加入 jackson 的包；同时使用`<mvc:annotation-driven />`。

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.8</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.9.8</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.9.0</version>
</dependency>
```



### 什么是JSON

JSON（JavaScript Object Notation，JavaScript 对象表示法）是一种**轻量级的文本数据交换格式**，核心作用是在不同系统（如前端与后端、不同服务间）之间高效传递数据，因语法简洁、易读易解析，成为当前主流的数据交换格式。

> #### 核心特点
>
> 1. 语法简单：基于键值对（`"key": value`）和数组（`[value1, value2]`），类似 JavaScript 对象，肉眼可直接看懂；
> 2. 跨语言兼容：不依赖特定编程语言，Java、Python、前端 JS 等几乎所有语言都有解析 JSON 的工具；
> 3. 轻量高效：相比 XML 等格式，无冗余标签，数据体积小，传输和解析速度更快。
>
> #### 基本语法规则
>
> - 键（key）必须用**双引号**包裹（单引号或无引号均不合法）；
> - 值（value）支持 5 种类型：字符串（双引号包裹）、数字、布尔值（`true`/`false`）、`null`、数组（`[]` 包裹）、对象（`{}` 包裹，嵌套键值对）；
> - 键值对之间用逗号分隔，最后一个键值对后无逗号（避免语法错误）。
>
> #### 常见格式示例
>
> 1. 基础对象格式（最常用，对应后端实体类）
>
> ```json
> {
>   "id": 101,
>   "username": "zhangsan",
>   "age": 25,
>   "isStudent": true,
>   "hobbies": ["reading", "coding"],  // 数组类型
>   "address": {                      // 嵌套对象
>     "city": "Beijing",
>     "street": "Main Road"
>   },
>   "email": null  // null 值
> }
> ```
>
> 2. 数组格式（对应后端集合 / 列表）
>
> ```json
> [
>   {"name": "苹果", "price": 5.9},
>   {"name": "香蕉", "price": 3.2},
>   {"name": "橙子", "price": 4.5}
> ]
> ```
>
> #### 核心用途
>
> - **前后端数据交互**：前端通过 AJAX 发送 JSON 数据（配 `@RequestBody` 接收），后端返回 JSON 数据（配 `@ResponseBody`）；
> - **服务间接口通信**：微服务架构中，不同服务调用时（如 Java 服务调用 Python 服务），常用 JSON 传递请求 / 响应数据；
> - **配置文件**：部分项目用 JSON 替代 XML/Properties 作为配置文件（如前端 `package.json`）



### 1.1 @RequestBody

`@RequestBody` 用于**将 HTTP 请求体中的 JSON/XML 等数据，自动转换为控制器方法的参数对象**（如实体类、Map 等），常用于接收前端提交的复杂数据（如 AJAX 发送的 JSON 数据）。

该注解用于 Controller 的方法的形参声明，当使用 ajax 提交并指定`contentType`为 json 形式时，通过`HttpMessageConverter`接口转换为对应的 POJO 对象。

**前端 ajax 代码**

```html
<button id="btn1">ajax异步提交</button>
<script>
    $("#btn1").click(function () {
        let url = '${pageContext.request.contextPath}/ajaxRequest';
        let data = '[{"id":1,"username":"张三"},{"id":2,"username":"李四"}]';
        $.ajax({
            type: 'POST',
            url: url,
            data: data,
            contentType: 'application/json;charset=utf-8',
            success: function (resp) {
                alert(JSON.stringify(resp))
            }
        })
    })
</script>
```

**后端 Controller代码**

```java
@RequestMapping(value = "/ajaxRequest")
public void ajaxRequest(@RequestBody List<User> list) {
    System.out.println(list);
}
```





### 1.2 @ResponseBody

**将Controller方法返回值直接转换为 JSON/XML 等格式的响应体，写入 HTTP 响应中，而非跳转页面**，通过`HttpMessageConverter`接口转换为指定格式的数据（如：json、xml 等），通过 Response 响应给客户端。

 **Controller 代码**

```java
/* @RequestMapping(produces = "application/json;charset=utf-8")：响应返回数据的 mime 类型和编码，默认为 json */
@RequestMapping(value = "/ajaxRequest", produces = "application/json;charset=utf-8")
@ResponseBody
public List<User> ajaxRequest(@RequestBody List<User> list) {
    System.out.println(list);
    return list;
}
```



## 二、RESTful

### 2.1 什么是 RESTful

Restful（Representational State Transfer，表述性状态转移） 是一种软件架构风格、设计风格，而不是标准，只是提供了一组设计原则和约束条件。主要用于客户端和服务器交互类的软件，基于这个风格设计的软件可以更简洁，更有层次，更易于实现缓存机制等。

Restful 风格的请求是使用 **“url + 请求方式”** 表示一次请求目的的，HTTP 协议里面四个表示操作方式的动词如下：

> - GET：读取（Read）
> - POST：新建（Create）
> - PUT：更新（Update）
> - DELETE：删除（Delete）
>

- **以 “资源” 为中心**

  把接口操作的对象（如用户、商品）视为 “资源”，用 URL 直接表示资源（而非操作），例如：

  ```markdown
  * 正确：`/users`（用户列表资源）、`/users/101`（ID=101 的单个用户资源）
  
  - 错误：`/getUser`（URL 包含 “操作”，不符合 RESTful）
  ```

- **用 HTTP 方法表示 “操作”**

| 客户端请求       | RESTful 风格 URL 地址 | 原来风格 URL 地址   |
| ---------------- | --------------------- | ------------------- |
| 查询所有用户     | GET /user             | /user/findAll       |
| 根据 ID 查询用户 | GET /user/{id}        | /user/findById?id=? |
| 新增用户         | POST /user            | /user/save          |
| 修改用户         | PUT /user             | /user/update        |
| 删除用户         | DELETE /user/{id}     | /user/delete?id=?   |



### 2.2 代码实现

**核心注解**

- `@PathVariable`：用来接收 RESTful 风格请求地址中占位符的值
- `@RestController`：RESTful 风格多用于前后端分离项目开发，前端通过 ajax 与服务器进行异步交互，处理器通常返回的是 json 数据，所以使用`@RestController`来替代`@Controller`和`@ResponseBody`两个注解

**后端 Controller 代码**

```java
// @Controller
@RestController
public class RestFulController {

    @GetMapping(value = "/user/{id}")
    // 相当于 @RequestMapping(value = "/user/{id}",method = RequestMethod.GET)
    // @ResponseBody
    public String get(@PathVariable Integer id) {
        return "get:" + id;
    }

    @PostMapping(value = "/user")
    // @ResponseBody
    public String post() {
        return "post";
    }

    @PutMapping(value = "/user")
    // @ResponseBody
    public String put() {
        return "put";
    }

    @DeleteMapping(value = "/user/{id}")
    // @ResponseBody
    public String delete(@PathVariable Integer id) {
        return "delete:" + id;
    }
}
```

![0204](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0204.png)





## 三、文件上传

### 3.1 文件上传三要素

- 表单项 `type="file"`
- 表单的提交方式 `method="POST"`
- 表单的`enctype`属性是多部分表单形式`enctype="multipart/form-data"`（支持上传文件或混合数据（文本 + 文件）的表单提交格式 ）

```html
<form action="${pageContext.request.contextPath}/fileUpload" method="post" enctype="multipart/form-data">
    名称:<input type="text" name="username"><br>
    文件:<input type="file" name="filePic"><br>
        <input type="submit" value="单文件上传">
</form>
```

![0201](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0201.png)

### 3.2 文件上传原理

- 当 form 表单修改为多部分表单时，`request.getParameter()`将失效。
- 当 form 表单的`enctype`取值为`application/x-www-form-urlencoded`时，form 表单的正文内容格式是：`name=value&name=value`
- 当 form 表单的`enctype`取值为`multipart/form-data`时，请求正文内容就变成多部分形式：

![0202](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0202.png)



### 3.3 单文件上传

#### 步骤分析

```markdown
1. 导入 fileupload 和 io 坐标
2. 配置文件上传解析器
3. 编写文件上传代码
```

#### (1) 导入 fileupload 和 io 坐标

```xml
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.3.3</version>
</dependency>
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.6</version>
</dependency>
```

#### (2) spring-mvc.xml中配置文件上传解析器

```xml
<!--文件上传解析器-->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    <!-- 设定文件上传的最大值为5MB，5*1024*1024 -->
    <property name="maxUploadSize" value="5242880"></property>
    <!-- 设定文件上传时写入内存的最大值，如果小于这个参数不会生成临时文件，默认为10240 -->
    <property name="maxInMemorySize" value="40960"></property>
</bean>
```

#### (3) 编写文件上传代码

**前端表单**

```html
<form action="${pageContext.request.contextPath}/fileUpload" method="post" enctype="multipart/form-data">
    名称:<input type="text" name="username"> <br>
    文件:<input type="file" name="filePic"> <br>
    <input type="submit" value="单文件上传">
</form>
```

**后端 Controller 代码**

```java
@RequestMapping("/fileUpload")
public String fileUpload(String username, MultipartFile filePic) throws IOException {
    System.out.println(username);
    // 获取文件名
    String originalFilename = filePic.getOriginalFilename();
    // 保存文件
    filePic.transferTo(new File("d:/upload/" + originalFilename));
    return "success";
}
```



### 3.4 多文件上传

**前端表单**

```html
<form action="${pageContext.request.contextPath}/filesUpload" method="post" enctype="multipart/form-data">
    名称:<input type="text" name="username"> <br>
    文件1:<input type="file" name="filePic"> <br>
    文件2:<input type="file" name="filePic"> <br>
    <input type="submit" value="多文件上传">
</form>
```

**后端 Controller 代码**

```java
@RequestMapping("/filesUpload")
public String filesUpload(String username, MultipartFile[] filePic) throws IOException {
    System.out.println(username);
    for (MultipartFile multipartFile : filePic) {
        // 获取文件名
        String originalFilename = multipartFile.getOriginalFilename();
        // 保存到服务器
        multipartFile.transferTo(new File("d:/upload/" + originalFilename));
    }
    return "success";
}
```





## 四、异常处理

### 4.1 异常处理的思路

在 Java 中，异常处理主要有**两种**方式：

1. 当前方法捕获处理（try-catch）：该方式会导致业务代码和异常处理代码耦合；
2. 抛给调用者处理（throws）：自身不处理，抛给调用者，调用者再抛给其上层调用者，即一直向上抛-。

​       基于第二种方式，衍生出 SpringMVC 的异常处理机制：

系统的 DAO、Service、Controller 层出现异常时，均通过`throws Exception`向上抛出，最终由 SpringMVC 前端控制器交由异常处理器进行统一处理。如下图:

![0203](https://github.com/gvcheng/note_images/blob/main/springMVC_img/springMVC0203.png)



### 4.2 自定义异常处理器

#### 步骤分析

```markdown
1. 创建异常处理器类实现`HandlerExceptionResolver`
2. 配置异常处理器
3. 编写异常页面
4. 测试异常跳转
```

#### (1) 创建异常处理器类实现`HandlerExceptionResolver`

```java
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
```

#### (2) 配置异常处理器

```java
// 方式一：使用@Component注解
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {}
```

```xml
<!-- 方式二：XML配置 -->
<bean id="globalExceptionResolver" class="com.lagou.exception.GlobalExceptionResolver"></bean>
```

#### (3) 编写异常页面

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>error</title>
</head>
<body>
    <h3>这是一个最终异常的显示页面</h3>
    <p>${error}</p>
</body>
</html>
```

#### (4) 测试异常跳转

```java
@RequestMapping("/testException")
public String testException() {
    int i = 1 / 0; // 制造算术异常
    return "success";
}
```



### 4.3 web 的处理异常机制

```xml
<!--处理500异常-->
<error-page>
    <error-code>500</error-code>
    <location>/500.jsp</location>
</error-page>

<!--处理404异常-->
<error-page>
    <error-code>404</error-code>
    <location>/404.jsp</location>
</error-page>
```



## 五、拦截器

### 5.1 拦截器（interceptor）的作用

Spring MVC 的拦截器类似于 Servlet 开发中的过滤器 Filter，用于对处理器进行预处理和后处理。

将拦截器按一定的顺序联结成一条链，这条链称为拦截器链（InterceptorChain）。在访问被拦截的方法或字段时，拦截器链中的拦截器就会按其之前定义的顺序被调用。拦截器也是 AOP 思想的具体实现。



### 5.2 拦截器和过滤器区别

| 区别     | 过滤器                                                      | 拦截器                                                       |
| -------- | ----------------------------------------------------------- | ------------------------------------------------------------ |
| 使用范围 | 是 Servlet 规范中的一部分，任何 Java Web 工程都可以使用     | 是 SpringMVC 框架自己的，只有使用了 SpringMVC 框架的工程才能用 |
| 拦截范围 | 在`url-pattern`中配置了`/*`之后，可以对所有要访问的资源拦截 | 只会拦截访问的控制器方法，如果访问的是 html、css、image 或者 js 是不会进行拦截的 |



### 5.3 快速入门

#### 步骤分析

```markdown
1. 创建拦截器类实现`HandlerInterceptor`接口
2. 配置拦截器
3. 测试拦截器的拦截效果
```

#### (1) 创建拦截器类实现`HandlerInterceptor`接口

```java
public class MyInterceptor1 implements HandlerInterceptor {

    // 在目标方法执行之前 拦截
    @Override
    public boolean preHandle(HttpServletRequest request, 
                             HttpServletResponse response, 
                             Object handler) {
        System.out.println("preHandle1");
        return true; // 返回true继续执行，返回false终止执行
    }

    // 在目标方法执行之后，视图对象返回之前 执行
    @Override
    public void postHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler, 
                           ModelAndView modelAndView) {
        System.out.println("postHandle1");
    }

    // 在流程都执行完毕后 执行
    @Override
    public void afterCompletion(HttpServletRequest request, 
                                HttpServletResponse response, 
                                Object handler, 
                                Exception ex) {
        System.out.println("afterCompletion1");
    }
}
```

#### (2) 配置拦截器

```xml
<!--配置拦截器-->
<mvc:interceptors>
    <mvc:interceptor>
        <!--对哪些资源执行拦截操作，/**表示所有资源-->
        <mvc:mapping path="/**"/>
        <!--配置自定义拦截器类-->
        <bean class="com.lagou.interceptor.MyInterceptor1"/>
    </mvc:interceptor>
</mvc:interceptors>
```

#### (3) 测试拦截器的拦截效果

##### 编写 Controller

```java
@Controller
public class TargetController {
    @RequestMapping("/target")
    public String targetMethod() {
        System.out.println("目标方法执行了...");
        return "success";
    }
}
```

##### 编写 success.jsp 页面

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>success</title>
</head>
<body>
    <h3>success...</h3>
    <% System.out.println("视图执行了....");%>
</body>
</html>
```



### 5.4 拦截器链

开发中拦截器可以单独使用，也可以同时使用多个拦截器形成一条拦截器链。开发步骤和单个拦截器是一样的，只不过注册的时候注册多个，注意这里注册的顺序就代表拦截器执行的顺序。

1. 同上，编写第二个拦截器`MyInterceptor2`（与`MyInterceptor1`代码结构一致，仅输出内容不同）
2. 配置拦截器链

```xml
<!--配置拦截器链-->
<mvc:interceptors>
    <!--第一个拦截器-->
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <bean class="com.lagou.interceptor.MyInterceptor1"></bean>
    </mvc:interceptor>
    <!--第二个拦截器-->
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <bean class="com.lagou.interceptor.MyInterceptor2"></bean>
    </mvc:interceptor>
</mvc:interceptors>
```

执行打印结果

![0205](D:\Program\MyNotes\notes_image\springMVC0205.png)



### 5.5 知识小结

拦截器中的方法说明如下：

| 方法名            | 说明                                                         |
| ----------------- | ------------------------------------------------------------ |
| preHandle()       | 方法将在请求处理之前进行调用，该方法的返回值是布尔值 Boolean 类型。当它返回为 false 时，表示请求结束，后续的 Interceptor 和 Controller 都不再执行；当返回值为 true 时，就会继续调用下一个 Interceptor 的 preHandle 方法。 |
| postHandle()      | 该方法是在当前请求进行处理之后被调用，前提是 preHandle 方法的返回值为 true 时才能被调用，且它会在 DispatcherServlet 进行视图返回渲染之前被调用，所以我们可以在这个方法中对 Controller 处理之后的 ModelAndView 对象进行操作。 |
| afterCompletion() | 该方法将在整个请求结束之后，也就是在 DispatcherServlet 渲染了对应的视图之后执行，前提是 preHandle 方法的返回值为 true 时才能被调用。 |

