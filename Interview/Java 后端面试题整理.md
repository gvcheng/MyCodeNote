# Java 后端面试题整理及项目询问

# Java 八股

## 1. String、StringBuilder、StringBuffer 区别

- **String**：不可变（final 修饰），每次修改都会生成新对象，效率低，适合少量拼接。

- **StringBuilder**：可变，非线程安全，效率高，单线程环境下首选。

- **StringBuffer**：可变，线程安全（加了锁），效率低，适合多线程环境。

---

## 2. 异常分类

Java 异常继承自 **Throwable**，分为两大类：

1. **Error（错误）**：JVM 系统错误，如 OutOfMemoryError，程序无法处理，只能退出。

2. **Exception（异常）**：程序可以处理。

    - 运行时异常（RuntimeException）：如空指针、数组越界，不必须捕获。

    - 受检异常（Checked Exception）：如 IOException、SQLException，必须捕获或抛出。

---

## 3. ArrayList vs LinkedList

- 底层结构：ArrayList 是数组；LinkedList 是双向链表。

- 性能：

    - 查询：ArrayList 快（随机访问）。

    - 增删：LinkedList 快（只需改变节点引用，ArrayList 需移动元素）。

- 场景：频繁查询用 ArrayList；频繁增删用 LinkedList。

---

## 4. HashMap 怎么用？底层结构？

- 用法：`Map<String, Object> map = new HashMap<>();` 存键值对，key 唯一。

- 底层（JDK1.8+）：**数组 + 链表 + 红黑树**。

    - 链表长度超过 8 且数组长度大于 64 时，链表会转成红黑树（提升查询效率）。
    
- 使用场景：

    1. 快速查找 / 映射：通过一个 “唯一标识” 快速找到对应数据

       接口返回的高频数据（如用户信息、字典表），用 HashMap 做本地缓存

    2. 去重 / 统计频次：

       统计字符串中字符出现次数

       ```java
       String str = "hello world";
       Map<Character, Integer> countMap = new HashMap<>();
       for (char c : str.toCharArray()) {
           // 存在则+1，不存在则设为1
           countMap.put(c, countMap.getOrDefault(c, 0) + 1);
       }
       System.out.println(countMap); // 输出：{h=1, e=1, l=3, o=2,  =1, w=1, r=1, d=1}
       ```

    3. 临时存储非有序的键值对数据

---

## 5. == 和 equals 的区别

- **==**：比较地址。基本类型比较值；引用类型比较内存地址。

- **equals**：比较内容。Object 类默认用 == 比较，String、Integer 等重写后比较内容。

---

## 6. 多线程基础：创建线程的方式

两种常见方式，本质一致：

1. 继承 Thread 类：重写 run() 方法，调用 start() 启动。

2. 实现 Runnable 接口：实现 run() 方法，传给 Thread 对象启动。

- 推荐：实现 Runnable 更好，Java 单继承，接口更灵活。

---

## 7. sleep() vs wait()

- 锁释放：sleep() 不释放锁；wait() 释放锁。

- 使用位置：sleep() 可在任何地方使用；wait() 只能在同步代码块（synchronized）中使用。

- 唤醒方式：sleep() 时间到自动唤醒；wait() 需要 notify()/notifyAll() 唤醒。

---

## 8. 什么是索引？有什么用？

索引是加速数据库查询的数据结构。

- 作用：大幅加快 SELECT 查询速度，降低 INSERT/UPDATE/DELETE 速度（维护索引开销）。

- 建议：经常查询的字段加索引，频繁增删改的字段少加。

---

## 9. 什么是事务？ACID 特性？

事务是一组 SQL 操作，要么全部执行成功，要么全部失败回滚。

**ACID 特性**：

1. **A（Atomicity）原子性**：要么全做，要么全不做。

2. **C（Consistency）一致性**：数据完整性不变。

3. **I（Isolation）隔离性**：多事务并发互不干扰。

4. **D（Durability）持久性**：提交后数据永久保存。

---

## 10. 必写 SQL：分页查询

```SQL
SELECT * FROM user LIMIT 0, 10;
SELECT * FROM user LIMIT 10, 10;
```

---

## 11. 必写 SQL：联表查询（左连接）

```SQL
SELECT o.*, p.title 
FROM `order` o 
LEFT JOIN product p ON o.product_id = p.id;
```

---

## 12. 必写 SQL：给表加索引

```SQL

CREATE INDEX idx_user_phone ON user(phone);
```

---

## 13. 什么是主键？外键？

- **主键（Primary Key）**：唯一标识表中每一行，非空且唯一，一张表只有一个主键。

- **外键（Foreign Key）**：关联两张表，保证数据引用完整性。

---

## 14. group by 的用法

```SQL

SELECT dept_id, COUNT(*) FROM employee GROUP BY dept_id;
```

---

## 15. 什么是乐观锁、悲观锁？

- **悲观锁**：认为别人一定会修改数据，直接上锁（synchronized），效率低，适合写多读少。

    ```SQL
    SELECT * FROM product WHERE id =#{productId} FOR UPDATE;
    ```
    
- **乐观锁**：认为别人不会修改，不上锁，提交时判断数据是否变化。

    ```SQL
    UPDATE product SET stock = stock – 1 
    WHERE id = #{id} AND stock = stock;
    ```

---

## 16. Spring 核心是什么？

核心是 **IOC（控制反转）** 和 **AOP（面向切面编程）**。

- IOC：对象创建与依赖交给 Spring 容器管理，不用自己 new。

- DI（依赖注入）：IOC 具体实现，用 @Autowired 注入对象。

---

## 17. SpringBoot 优点

1. 简化配置：无繁琐 XML，全注解驱动。

2. 内嵌容器：自带 Tomcat，main 方法直接启动。

3. 起步依赖：Maven 依赖自动管理。

4. 自动化配置：根据依赖自动配置 Bean。

---

## 18. SpringBoot 启动流程

1. 执行主类 main 方法，创建 SpringApplication 实例。

2. 加载配置文件（application.yml/properties）。

3. 初始化 ApplicationContext，创建 Bean 工厂。

4. 刷新容器，完成 Bean 创建与初始化。

5. 启动内置 Tomcat，运行项目。

---

## 19. MyBatis #{} 和 ${} 区别

- **#{}**：预编译，防 SQL 注入，推荐使用，底层 PreparedStatement。

- **${}**：字符串直接替换，有注入风险，用于表名/字段名动态传入。

---

## 20. MyBatis resultType vs resultMap

- **resultType**：返回简单类型/实体类，字段名与属性名一致（下划线自动转驼峰）。

- **resultMap**：复杂映射，字段名不匹配、一对多/多一关联查询使用。

---

## 21. 如何写一个接口返回 JSON？

类上加 **@RestController** 注解（等同于 @Controller + @ResponseBody）。

---

## 22. @Transactional 注解作用？

事务管理，方法内 SQL 在同一事务中。

- rollbackFor：默认回滚运行时异常；`rollbackFor = Exception.class` 表示任何异常都回滚。

---

## 23. @Autowired 注解作用？

自动装配（依赖注入），Spring 自动从容器中找对应对象赋值，不用手动 new。

---

## 24. @RequestMapping 注解作用？

请求映射，将 HTTP 请求路径映射到控制器方法。

- 可使用 @GetMapping/@PostMapping/@PutMapping/@DeleteMapping 替代。

---

## 25. BIO、NIO、AIO？

**BIO**：同步阻塞，一个连接一个线程

**NIO**：同步非阻塞，一个线程处理多个连接（多路复用）

**AIO**：异步非阻塞，操作系统完成后通知你



# 基础算法题

### 1. 冒泡排序

**思路**

- 相邻元素两两比较，大的往后移，每轮把最大的数 “冒泡” 到末尾。
- 优化：加标志位，若某轮无交换则提前结束。

![bubbleSort](D:\Images\gif\bubbleSort.gif)

```java
public static void bubbleSort(int[] arr) {
    if (arr == null || arr.length < 2) return;
    boolean swapFlag; // 优化：标记是否交换
    for (int i = arr.length - 1; i > 0; i--) {
        swapFlag = false;
        for (int j = 0; j < i; j++) {
            if (arr[j] > arr[j + 1]) {
                // 交换
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
                swapFlag = true;
            }
        }
        if (!swapFlag) break; // 无交换，说明已排好
    }
}
```

### 2. 快速排序（性能最优）

**核心思路**

- 分治思想：选一个基准值，把小于基准的放左边，大于的放右边，递归处理左右子数组。

![quicksort](D:\Images\gif\quickSort.gif)

```java
public static void quickSort(int[] arr) {
    if (arr == null || arr.length < 2) return;
    quickSort(arr, 0, arr.length - 1);
}

private static void quickSort(int[] arr, int left, int right) {
    if (left >= right) return;
    int pivot = partition(arr, left, right); // 找基准位置
    quickSort(arr, left, pivot - 1); // 左子数组
    quickSort(arr, pivot + 1, right); // 右子数组
}

private static int partition(int[] arr, int left, int right) {
    int pivot = arr[right]; // 选最右元素当基准
    int i = left - 1; // 小于基准的区域边界
    for (int j = left; j < right; j++) {
        if (arr[j] <= pivot) {
            i++;
            // 交换
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
    // 把基准放到正确位置
    int temp = arr[i + 1];
    arr[i + 1] = arr[right];
    arr[right] = temp;
    return i + 1;
}
```

### 3. 二分查找(有序数组，高频手写)

**核心思路**

- 有序数组中，每次取中间值对比，缩小查找范围（左 / 右半区）。

![binarySearch](D:\Images\gif\binarySearch.gif)

```java
public static int binarySearch(int[] arr, int target) {
    if (arr == null || arr.length == 0) return -1;
    int left = 0, right = arr.length - 1;
    while (left <= right) {
        int mid = left + (right - left) / 2; // 避免溢出，等价于 (left+right)/2
        if (arr[mid] == target) {
            return mid; // 找到，返回下标
        } else if (arr[mid] < target) {
            left = mid + 1; // 目标在右半区
        } else {
            right = mid - 1; // 目标在左半区
        }
    }
    return -1; // 没找到
}
```

### 4. 两数之和（LeetCode 第一题，必背）

**题目**

给定整数数组和目标值，找出两个数的下标，使它们的和等于目标值。

**核心思路**

- 用 HashMap 存 “数值 - 下标”，遍历数组时查目标值 - 当前值是否在 Map 中。



```java
public static int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }
        map.put(nums[i], i);
    }
    throw new IllegalArgumentException("无符合条件的数");
}
```



------

# 项目询问

- 项目是什么、给谁用、核心目标是什么？

​	例：校园二手交易平台，面向在校学生，解决交易麻烦、信息不透明问题。

- 不做这个项目会有什么问题，为什么要做？

​	例：之前只能发朋友圈/群，混乱、不安全、无交易流程。

- 用了什么技术、为什么这么选、核心架构？

​	例：前后端分离，SpringBoot + Vue + MySQL + Redis，登录认证、接口安全、分页、缓存。

- 核心功能有哪些？

​	例：用户登录、商品发布、搜索筛选、订单生成。

- 你负责什么？

​	例：后端接口、数据库设计、JWT 登录、商品模块。

