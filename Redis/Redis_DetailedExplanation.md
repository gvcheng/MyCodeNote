# Redis 详解

课程目标

```markdown
1. Redis 概述
2. 下载与安装
3. 使用 Redis
```



## 一、概述

### 1.1 互联网架构的演变历程

#### 第 1 阶段

数据访问量不大，简单架构即可满足需求。

![0101](D:\Program\MyNotes\notes_image\Redis\Redis0101.png)

#### 第 2 阶段

- 数据访问量增大，引入缓存技术缓解数据库压力。
- 不同业务对应不同数据库。

![Redis0102](D:\Program\MyNotes\notes_image\Redis\Redis0102.png)

#### 第 3 阶段

- 主从读写分离，解决单数据库读写压力集中问题。
- 之前的缓存确实能够缓解数据库的压力,但是写和读都集中在一个数据库上,压力又来了｡
-  一个数据库负责写,一个数据库负责读｡分工合作｡愉快!
- master（主数据库）处理事务性操作（增删改），slave（从数据库）处理非事务性操作（查询），通过主从复制把master上的事务性操作同步到slave数据库中。
- slave数据库中 mysql的master/slave就是网站的标配!

![Redis0103](D:\Program\MyNotes\notes_image\Redis\Redis0103.png)

#### 第 4 阶段

- mysql的主从复制,读写分离的基础上,mysql的主库开始出现瓶颈 
- 由于MyISAM使用表锁,所以并发性能特别差 
- 分库分表开始流行,mysql也提出了表分区,虽然不稳定,但我们看到了希望 
- 开始吧,mysql集群

![Redis0104](D:\Program\MyNotes\notes_image\Redis\Redis0104.png)







### 1.2 Redis 入门介绍

#### 互联网需求的 3 高特性

```markdown
# 高并发、高可扩、高性能。
```



#### Redis 定义

Redis 是一种运行速度很快、并发性能很强，并且运行在内存上的 NoSql（not only sql）数据库。

#### NoSQL 数据库和传统数据库相比的优势

- NoSQL 数据库无需事先为要存储的数据建立字段，随时可以存储自定义的数据格式。
- 而在关系数据库里，增删字段是一件非常麻烦的事情。如果是非常大数据量的表，增加字段简直就是一个噩梦。



#### Redis 常用使用场景

1. <span style="color: #000FFF">**缓存**</span>：毫无疑问这是 Redis 当今最为人熟知的使用场景，在提升服务器性能方面非常有效；一些频繁被访问的数据，若放在关系型数据库，每次查询开销大，而 Redis 运行在内存中，能高效访问。
2. <span style="color: #000FFF">**排行榜**</span>：使用传统关系型数据库（mysql、oracle 等）实现较为复杂，利用 Redis 的 SortSet（有序集合）数据结构可简单实现。
3. <span style="color: #000FFF">**计算器 / 限速器**</span>：利用 Redis 中原子性的自增操作，可统计用户点赞数、用户访问数等（此类操作若用 MySQL，频繁读写会带来较大压力）；限速器可限制用户访问某个 API 的频率，如抢购时防止用户疯狂点击造成压力。
4. <span style="color: #000FFF">**好友关系**</span>：利用集合的交集、并集、差集等命令，可方便实现共同好友、共同爱好等功能。
5. <span style="color: #000FFF">**简单消息队列**</span>：除 Redis 自身的发布 / 订阅模式，还可利用 List 实现队列机制，如到货通知、邮件发送等需求（无需高可靠，但能缓解 DB 压力）。
6. <span style="color: #000FFF">**Session 共享**</span>：以 jsp 为例，默认 Session 保存在服务器文件中，集群服务下用户可能落在不同机器导致频繁登录；Redis 保存 Session 后，用户无论落在哪台机器都能获取对应 Session 信息



### 1.3 Redis/Memcache/MongoDB 对比

三者都是 nosql 数据库的著名代表

#### 1.3.1 Redis 和 Memcache

- 共性：都是内存数据库；memcache 还可用于缓存其他东西，例如图片、视频等等。
- 差异：
  1. 数据结构：memcache 数据结构单一（kv），redis 更丰富（提供 list、set、hash 等数据结构），能有效减少网络 IO 的次数。
  2. 虚拟内存：Redis 当物理内存用完时，可以将一些很久没用到的 value 交换到磁盘。
  3. <span style="color: #000FFF">**存储数据安全**</span>：memcache 挂掉后，数据没了（没有持久化机制）；redis 可以定期保存到磁盘（持久化）。
  4. 灾难恢复：memcache 挂掉后，数据不可恢复；redis 数据丢失后可以通过 RBD 或 AOF 恢复。

#### 1.3.2 Redis 和 MongoDB

- 关系定位：redis 和 mongodb 并不是竞争关系，更多的是一种<span style="color: #000FFF">**协作共存**</span>的关系。
- 核心差异与协作：
  1. mongodb 本质上还是硬盘数据库，在复杂查询时仍然会有大量的资源消耗，而且在处理复杂逻辑时仍然要不可避免地进行多次查询。
  2. 此时需要 redis 或 Memcache 这样的内存数据库来作为中间层进行缓存和加速，例如在某些复杂页面的场景中，整个页面的内容如果都从 mongodb 中查询，可能要几十个查询语句，耗时很长；如果需求允许，则可以把整个页面的对象缓存至 redis 中，定期更新，这样 mongodb 和 redis 就能很好地协作起来。



### 1.4 分布式数据库 CAP 原理

#### 1.4.1 CAP 简介

- 传统的关系型数据库事务具备 ACID：
  - A（Atomicity）：原子性
  - C（Consistency）：一致性
  - I（Isolation）：独立性
  - D（Durability）：持久性
- 分布式数据库的 CAP：
  1. **C（Consistency，强一致性）**：“all nodes see the same data at the same time”，即更新操作成功并返回客户端后，<span style="color: #000FFF">所有节点在同一时间的数据完全一致</span>，这就是分布式的一致性。一致性的问题在并发系统中不可避免，对于客户端来说，一致性指的是并发访问时更新过的数据如何获取的问题；从服务端来看，则是更新如何复制分布到整个系统，以保证数据最终一致。
  2. **A（Availability，高可用性）**：可用性指 “Reads and writes always succeed”，即<span style="color: #000FFF">服务一直可用</span>，而且要是正常的响应时间。好的可用性主要是指系统能够很好的为用户服务，不出现用户操作失败或者访问超时等用户体验不好的情况。
  3. **P（Partition tolerance，分区容错性）**：即分布式系统在遇到<span style="color: #000FFF">某节点或网络分区故障时，仍然能够对外提供满足一致性或可用性的服务</span>。分区容错性要求能够使应用虽然是一个分布式系统，而看上去却好像是在一个可以运转正常的整体，比如现在的分布式系统中有某一个或者几个机器宕掉了，其他剩下的机器还能够正常运转满足系统需求，对于用户而言并没有什么体验上的影响。

#### 1.4.2 CAP 理论

- 核心前提：CAP 理论提出就是针对分布式数据库环境的，所以 P（分区容错性）这个属性必须容忍它的存在，而且是必须具备的。
- 权衡选择：因为 P 是必须的，那么需要选择的就是 A（可用性）和 C（一致性）：
  1. 选择可用性 A：此时，那个失去联系的节点依然可以向系统提供服务，不过它的数据就不能保证是同步的了（失去了 C 属性）。
  2. 选择一致性 C：为了保证数据库的一致性，我们必须等待失去联系的节点恢复过来，在这个过程中，那个节点是不允许对外提供服务的，这时候系统处于不可用状态（失去了 A 属性）。
- 典型示例：读写分离场景中，某个节点负责写入数据，然后将数据同步到其它节点，其它节点提供读取的服务；当两个节点出现通信问题时，面临选择：A（继续提供服务，但是数据不保证准确）、C（用户处于等待状态，一直等到数据同步完成）。

#### 1.4.3 CAP 总结

- 核心结论：
  1. 分区是常态，不可避免，三者不可共存。
  2. <span style="color: #FF0000">可用性和一致性是一对冤家</span>（一致性高则可用性低，一致性低则可用性高）。
- NoSQL 数据库分类（基于 CAP 原理）：
  1. CA：单点集群，满足一致性、可用性的系统，通常在可扩展性上不太强大。
  2. CP：满足一致性、分区容忍性的系统，通常性能不是特别高。
  3. AP：满足可用性、分区容忍性的系统，通常可能对一致性要求低一些。



## 二、下载与安装

### 2.1 下载

- Redis 官方地址：http://www.redis.net.cn/

![0105](D:\Program\MyNotes\notes_image\Redis\Redis0105.png)

- 图形工具地址：https://redisdesktop.com/download

![0106](D:\Program\MyNotes\notes_image\Redis\Redis0106.png)



### 2.2 安装

注意：虽然可以在 Windows 操作系统安装，但是官方不推荐，建议安装在 Linux 系统。

#### 安装步骤

1. 上传 tar.gz 包，并解压：

   ```shell
   tar -zxvf redis-6.2.14.tar.gz
   ```

2. 安装 gcc（必须有网络）

   ```shell
   sudo apt install gcc
   ```

   忘记是否安装过，可使用 `gcc -v` 命令查看 gcc 版本，未安装会提示命令不存在。

3. 进入 redis 目录，进行编译：在`cd 解压后的redis目录` 

   ```shell
   make
   ```

4. 编译之后，开始安装：

   ```shell
   make install
   ```

   

### 2.3 安装后的操作

#### 2.3.1 后台运行方式

Redis 默认不会使用后台运行，如需开启，修改配置文件将 `daemonize` 设置为 `yes`（后台服务启动时会生成进程文件）。

```shell
vim /root/tools/redis-6.2.14/redis.conf
```

```
daemonize yes
```

以配置文件的方式启动

```shell
cd /usr/local/bin
redis-server /root/tools/redis-6.2.14/redis.conf
```



#### 2.3.2 关闭数据库

**单实例关闭**

```shell
redis-cli shutdown
```

**多实例关闭**（指定端口）

```shell
redis-cli -p 6379 shutdown
```



#### 2.3.3 常用操作

**检测 6379 端口是否在监听**

```shell
netstat -lntp | grep 6379
```

> 端口为什么是6379？
>
> 6379 是手机按键上 MERZ 对应的号码，
>
> 而MERZ 取自意大利歌女 Alessia Merz 的名字，
>
> MERZ长期被 antirez（redis 作者）及其朋友当作愚蠢的代名词

**检测后台进程是否存在**

```shell
ps -ef|grep redis
```



#### 2.3.4 连接 redis 并测试

```shell
redis-cli
ping
```

返回`PONG`表示连接成功



#### 2.3.5 HelloWorld

```bash
set k1 HelloWorld  # 保存数据
get k1  # 获取数据
```



#### 2.3.6 测试性能

1. 先按 `ctrl+c` 退出 redis 客户端。

2. 执行性能测试命令（命令不会自动停止，需手动按`ctrl+c`停止测试）。

   ```bash
   redis-benchmark
   ```

3. 测试结果示例

   ```
   [root@localhost bin]# redis-benchmark 
   ====== PING_INLINE ======
   100000 requests completed in 1.80 seconds # 1.8 秒处理10万个请求，性能因设备配置而异
   50 parallel clients
   3 bytes payload
   keep alive: 1
   87.69% <= 1 milliseconds
   99.15% <= 2 milliseconds
   99.65% <= 3 milliseconds
   99.86% <= 4 milliseconds
   99.92% <= 5 milliseconds
   99.94% <= 6 milliseconds
   99.97% <= 7 milliseconds
   100.00% <= 7 milliseconds
   55524.71 requests per second # 每秒处理的请求数量
   ```

   

#### 2.3.7 默认 16 个数据库

配置文件路径：

```bash
vim /root/tools/redis-6.2.14/redis.conf
```

切换与查询示例

```bash
127.0.0.1:6379> get k1     		  # 查询k1
"HelloWorld"
127.0.0.1:6379> select 16   	  # 切换16号数据库
(error) ERR DB index is out of range  # 数据库下标超出范围
127.0.0.1:6379> select 15   	  # 切换15号数据库
OK
127.0.0.1:6379[15]> get k1  	  # 查询k1
(nil)							  # 空指针					
127.0.0.1:6379[15]> SELECT 0      # 切换0号数据库
OK
127.0.0.1:6379> get k1            # 查询k1
"HelloWorld"
```



#### 2.3.8 数据库键的数量

redis命令：`dbsize`（统计当前数据库的键数量）

提示：redis 在 linux 支持命令补全（按`tab`键）



#### 2.3.9 清空数据库

1. 清空当前库：`flushdb`
2. 清空所有（16 个）库（慎用）：`flushall`



#### 2.3.10 模糊查询（key）

- 模糊查询`keys`命令支持三个通配符：
  1. `*`：通配任意多个字符
     - 查询所有的键：`keys *`
     - 模糊查询 k 开头，后面任意字符：`keys k*`
     - 模糊查询 e 为最后一位，前面任意字符：`keys *e`
     - 匹配包含 k 的键：`keys *k*`
  2. `?`：通配单个字符
     - 模糊查询 k 字头，且匹配 1 个字符：`keys k?`
     - 仅记得第一个字母是 k，长度为 3：`keys k??`
  3. `[]`：通配括号内的某一个字符
     - 记得其他字母，仅第二个字母可能是 a 或 e：`keys r[ae]dis`



#### 2.3.11 键（key）的核心操作

1. **exists key**：判断某个 key 是否存在

```bash
127.0.0.1:6379> exists k1
(integer) 1 # 存在
127.0.0.1:6379> exists y1
(integer) 0 # 不存在
```

2. **move key db**：移动（剪切 - 粘贴）键到指定数据库

```shell
127.0.0.1:6379> move x1 8 # 将x1移动到8号库
(integer) 1 # 移动成功
127.0.0.1:6379> exists x1 # 查看当前库中是否存在x1
(integer) 0 # 不存在（已移走）
127.0.0.1:6379> select 8 # 切换8号库
OK
127.0.0.1:6379[8]> keys * # 查看8号库中的所有键
1) "x1"
```

3. **ttl key**：查看键还有多久过期（-1 永不过期，-2 已过期），即 “time to live”（剩余存活时间）

```shell
127.0.0.1:6379[8]> ttl x1 
(integer) -1 # 永不过期
```

4. **expire key 秒**：为键设置过期时间（生命倒计时

```shell
127.0.0.1:6379[8]> set k1 v1 # 保存k1
OK
127.0.0.1:6379[8]> ttl k1 # 查看k1的过期时间
(integer) -1 # 永不过期
127.0.0.1:6379[8]> expire k1 10 # 设置k1的过期时间为10秒（10秒后自动销毁）
(integer) 1 # 设置成功
127.0.0.1:6379[8]> get k1 # 获取k1
"v1"
127.0.0.1:6379[8]> ttl k1 # 查看k1的过期时间
(integer) 6 # 还有6秒过期
127.0.0.1:6379[8]> ttl k1 # 查看k1的过期时间
(integer) 2 # 还有2秒过期
127.0.0.1:6379[8]> ttl k1 # 查看k1的过期时间
(integer) -2 # 已过期
127.0.0.1:6379[8]> get k1 
(nil) # 过期后无法获取
127.0.0.1:6379[8]> keys * # 键从内存中销毁
(empty array)
```

5. **type key**：查看键的数据类型

```shell
127.0.0.1:6379[8]> type k1
string # k1 的数据类型是string（字符串）
```



## 三、使用 Redis

### 3.1 五大数据类型

- 操作文档参考：https://redis.com.cn

#### 3.1.1 字符串 String

1. **set/get/del/append/strlen**（保存 / 获取 / 删除 / 追加 / 统计长度）

```bash
127.0.0.1:6379> set k1 v1 # 保存数据
OK
127.0.0.1:6379> set k2 v2 # 保存数据
OK
127.0.0.1:6379> keys *
1) "k1"
2) "k2"
127.0.0.1:6379> del k2 # 删除数据k2
(integer) 1
127.0.0.1:6379> keys *
1) "k1"
127.0.0.1:6379> get k1 # 获取数据k1
"v1"
127.0.0.1:6379> append k1 abc # 往k1的值追加数据abc
(integer) 5 # 返回值的长度（字符数量）
127.0.0.1:6379> get k1
"v1abc"
127.0.0.1:6379> strlen k1 # 返回k1值的长度（字符数量）
(integer) 5
```

2. **incr/decr/incrby/decrby**（加减操作，仅支持数字类型）

   incr = 自增 1，decr = 自减 1，incrby = 指定步长自增，decrby = 指定步长自减

```bash
127.0.0.1:6379> set k1 1 # 初始化k1的值为1
OK
127.0.0.1:6379> incr k1 # k1 自增1（相当于++）
(integer) 2
127.0.0.1:6379> incr k1
(integer) 3
127.0.0.1:6379> get k1
"3"
127.0.0.1:6379> decr k1 # k1 自减1（相当于--）
(integer) 2
127.0.0.1:6379> decr k1
(integer) 1
127.0.0.1:6379> get k1
"1"
127.0.0.1:6379> incrby k1 3 # k1 自增3（相当于+=3）
(integer) 4
127.0.0.1:6379> get k1
"4"
127.0.0.1:6379> decrby k1 2 # k1 自减2（相当于-=2）
(integer) 2
127.0.0.1:6379> get k1
"2"
```

3. **getrange/setrange**（范围截取 / 替换，类似`between...and...`）

```bash
127.0.0.1:6379> set k1 abcdef # 初始化k1的值为abcdef
OK
127.0.0.1:6379> get k1
"abcdef"
127.0.0.1:6379> getrange k1 0 -1 # 查询k1全部的值
"abcdef"
127.0.0.1:6379> getrange k1 0 3 # 查询k1的值，范围是下标0~下标3（包含0和3，共返回4个字符）
"abcd"
127.0.0.1:6379> setrange k1 1 xxx # 替换k1的值，从下标1开始替换为xxx
(integer) 6
127.0.0.1:6379> get k1
"axxxef"
```

4. **setex/setnx**

   <font color="red">setex</font> = <font color="red">set</font> with <font color="red">ex</font>pire：添加数据时设置生命周期；

   <font color="red">setnx</font> = <font color="red">set</font> if <font color="red">n</font>ot e<font color="red">x</font>ist：添加时判断是否存在，防止覆盖

```bash
# setex示例
127.0.0.1:6379> setex k1 5 v1 # 添加k1=v1数据，同时设置5秒生命周期
OK
127.0.0.1:6379> get k1
"v1"
127.0.0.1:6379> get k1 # 5秒后过期，值自动销毁
(nil)

# setnx示例
127.0.0.1:6379> setnx k1 sun # 添加失败，因为k1已经存在
(integer) 0 
127.0.0.1:6379> get k1
"laosun"
127.0.0.1:6379> setnx k2 sun # k2 不存在，添加成功
(integer) 1 
```

5. **mset/mget/msetnx**

   `m` = more：批量添加 / 获取；

   `msetnx`批量添加时，若有已存在的键则整体失败

```bash
127.0.0.1:6379> set k1 v1 k2 v2 # set 不支持一次添加多条数据（报错）
(error) ERR syntax error
127.0.0.1:6379> mset k1 v1 k2 v2 k3 v3 # mset 可以一次添加多条数据
OK
127.0.0.1:6379> keys *
1) "k1"
2) "k2"
3) "k3"
127.0.0.1:6379> mget k2 k3 # 一次获取多条数据
1) "v2"
2) "v3"
127.0.0.1:6379> msetnx k3 v3 k4 v4 # 一次添加多条数据时，若有已存在的键则失败
(integer) 0
127.0.0.1:6379> msetnx k4 v4 k5 v5 # 一次添加多条数据时，若均不存在则成功
(integer) 1
```

6. **getset**（先 get 后 set：先获取键的旧值，再设置新值）

```bash
127.0.0.1:6379> getset k6 v6 # 无k6，get为null，然后添加k6=v6
(nil)
127.0.0.1:6379> keys *
1) "k4"
2) "k1"
3) "k2"
4) "k3"
5) "k5"
6) "k6"
127.0.0.1:6379> get k6
"v6"
127.0.0.1:6379> getset k6 vv6 # 先获取k6的旧值"v6"，再修改为"vv6"
"v6"
127.0.0.1:6379> get k6
"vv6"
```



#### 3.1.2 列表 List

- 类比：push（压子弹，添加元素）、pop（射子弹，移除元素）

1. **lpush/rpush/lrange**

   `l`=left：自左向右 / 从上往下添加；

   `r`=right：自右向左 / 从下往上添加；

   `lrange`：查询列表范围

```bash
# lpush示例（从上往下添加）
127.0.0.1:6379> lpush list01 1 2 3 4 5 
(integer) 5
127.0.0.1:6379> keys *
1) "list01"
127.0.0.1:6379> lrange list01 0 -1 # 查询list01中的全部数据（0开始，-1结尾）
1) "5"
2) "4"
3) "3"
4) "2"
5) "1"

# rpush示例（从下往上添加）
127.0.0.1:6379> rpush list02 1 2 3 4 5 
(integer) 5
127.0.0.1:6379> lrange list02 0 -1
1) "1"
2) "2"
3) "3"
4) "4"
5) "5"
```

2. **lpop/rpop**：移除元素；`lpop`从左 / 上边移除第一个元素，`rpop`从右 / 下边移除第一个元素

```bash
127.0.0.1:6379> lpop list02 # 从左（上）边移除第一个元素
"1"
127.0.0.1:6379> lpop list02 2 # 从左（上）边开始连续移除两个元素
"2"
"3"
127.0.0.1:6379> rpop list02 # 从右（下）边移除第一个元素
"5"
```

3. **lindex**根据下标查询元素（从左向右 / 自上而下）

```bash
127.0.0.1:6379> lrange list01 0 -1
1) "5"
2) "4"
3) "3"
4) "2"
5) "1"
127.0.0.1:6379> lindex list01 2 # 从上到下数，下标为2的值
"3"
127.0.0.1:6379> lindex list01 1 # 从上到下数，下标为1的值
"4"
```

4. **llen**（返回列表长度）

```bash
127.0.0.1:6379> llen list01
(integer) 5
```

5. **lrem **: 删除指定数量的指定值（格式：`lrem key 数量 value`）

```bash
127.0.0.1:6379> lpush list01 1 2 2 3 3 3 4 4 4 4
(integer) 10
127.0.0.1:6379> lrem list01 2 3 # 从list01中移除2个3
(integer) 2
127.0.0.1:6379> lrange list01 0 -1
1) "4"
2) "4"
3) "4"
4) "4"
5) "3"
6) "2"
7) "2"
8) "1"
```

6. **ltrim**：截取指定范围的值，其余值删除，（格式：`ltrim key 开始下标 结束下标`）

```bash
127.0.0.1:6379> lpush list01 1 2 3 4 5 6 7 8 9
(integer) 9
127.0.0.1:6379> lrange list01 0 -1
1) "9" # 下标0
2) "8" # 下标1
3) "7" # 下标2
4) "6" # 下标3
5) "5" # 下标4
6) "4" # 下标5
7) "3" # 下标6
8) "2" # 下标7
9) "1" # 下标8
127.0.0.1:6379> ltrim list01 3 6 # 截取下标3~6的值，其余删除
OK
127.0.0.1:6379> lrange list01 0 -1
1) "6"
2) "5"
3) "4"
4) "3"
```

7. **rpoplpush**：从一个集合转移元素到另一个集合（右出一个，左进一个）

```bash
127.0.0.1:6379> rpush list01 1 2 3 4 5
(integer) 5
127.0.0.1:6379> lrange list01 0 -1
1) "1"
2) "2"
3) "3"
4) "4"
5) "5"
127.0.0.1:6379> rpush list02 1 2 3 4 5
(integer) 5
127.0.0.1:6379> lrange list02 0 -1
1) "1"
2) "2"
3) "3"
4) "4"
5) "5"
127.0.0.1:6379> rpoplpush list01 list02 # list01 右边出一个（5），从左进入list02的第一个位置
"5"
127.0.0.1:6379> lrange list01 0 -1
1) "1"
2) "2"
3) "3"
4) "4"
127.0.0.1:6379> lrange list02 0 -1
1) "5"
2) "1"
3) "2"
4) "3"
5) "4"
6) "5"
```

8. **lset**：修改指定下标的值（格式：`lset key index value`）

```bash
127.0.0.1:6379> lrange list02 0 -1
1) "5"
2) "1"
3) "2"
4) "3"
5) "4"
6) "5"
127.0.0.1:6379> lset list02 0 x # 将list02中下标为0的元素修改成x
OK
127.0.0.1:6379> lrange list02 0 -1
1) "x"
2) "1"
3) "2"
4) "3"
5) "4"
6) "5"
```

9. **linsert**：插入元素，指定元素之前 / 之后（插入格式：`linsert key before/after oldvalue newvalue`）

```bash
127.0.0.1:6379> lrange list02 0 -1
1) "x"
2) "1"
3) "2"
4) "3"
5) "4"
6) "5"
127.0.0.1:6379> linsert list02 before 2 java # 在list02中的2元素之前插入java
(integer) 7
127.0.0.1:6379> lrange list02 0 -1
1) "x"
2) "1"
3) "java"
4) "2"
5) "3"
6) "4"
7) "5"
127.0.0.1:6379> linsert list02 after 2 redis # 在list02中的2元素之后插入redis
(integer) 8
127.0.0.1:6379> lrange list02 0 -1
1) "x"
2) "1"
3) "java"
4) "2"
5) "redis"
6) "3"
7) "4"
8) "5"
```

**性能总结**：类似添加火车皮，头尾操作效率高，中间操作效率低。



#### 3.1.3 集合 Set

- 特点：和 Java 中的 Set 类似，不允许重复元素。

(1)  **sadd/smembers/sismember**（添加 / 查看所有元素 / 判断元素是否存在）

```bash
127.0.0.1:6379> sadd set01 1 2 2 3 3 3 # 添加元素（自动排除重复元素）
(integer) 3
127.0.0.1:6379> smembers set01 # 查询set01集合
1) "1"
2) "2"
3) "3"
127.0.0.1:6379> sismember set01 2
(integer) 1 # 存在
127.0.0.1:6379> sismember set01 5
(integer) 0 # 不存在
```

注意：返回的 1 和 0 是布尔值，1=true（存在），0=false（不存在）

(2)  **scard**（获得集合中的元素个数）

```bash
127.0.0.1:6379> scard set01
(integer) 3 # 集合中有3个元素
```

(3)  **srem**（删除集合中的指定元素，格式：`srem key value`）

```bash
127.0.0.1:6379> srem set01 2 # 移除set01中的元素2
(integer) 1 # 1 表示移除成功
```

(4)  **srandmember**（从集合中随机获取指定个数的元素，格式：`srandmember key 个数`）

```bash
127.0.0.1:6379> sadd set01 1 2 3 4 5 6 7 8 9
(integer) 9
127.0.0.1:6379> smembers set01
1) "1"
2) "2"
3) "3"
4) "4"
5) "5"
6) "6"
7) "7"
8) "8"
9) "9"
127.0.0.1:6379> srandmember set01 3 # 从set01中随机获取3个元素
1) "8"
2) "2"
3) "3"
127.0.0.1:6379> srandmember set01 5 # 从set01中随机获取5个元素
1) "5"
2) "8"
3) "7"
4) "4"
5) "6"
```

(5)  **spop**（随机出栈 / 移除一个元素）

```bash
127.0.0.1:6379> smembers set01
1) "1"
2) "2"
3) "3"
4) "4"
5) "5"
6) "6"
7) "7"
8) "8"
9) "9"
127.0.0.1:6379> spop set01 # 随机移除一个元素
"8"
127.0.0.1:6379> spop set01 # 随机移除一个元素
"7"
```

(6)  **smove**（移动元素：将一个集合的指定元素移动到另一个集合，格式：`smove 源集合 目标集合 value`）

```bash
127.0.0.1:6379> sadd set01 1 2 3 4 5
(integer) 5
127.0.0.1:6379> sadd set02 x y z
(integer) 3
127.0.0.1:6379> smove set01 set02 3 # 将set01中的元素3移动到set02中
(integer) 1 # 移动成功
```

(7)  数学集合类命令

- 交集（共同元素）：`sinter`
- 并集（所有元素，去重）：`sunion`
- 差集（前者有、后者无的元素）：`sdiff`

```bash
127.0.0.1:6379> sadd set01 1 2 3 4 5
(integer) 5
127.0.0.1:6379> sadd set02 2 a 1 b 3
(integer) 5
127.0.0.1:6379> sinter set01 set02 # set01 和set02共同存在的元素
1) "1"
2) "2"
3) "3"
127.0.0.1:6379> sunion set01 set02 # 将set01和set02中所有元素合并（去重）
1) "5"
2) "4"
3) "3"
4) "2"
5) "b"
6) "a"
7) "1"
127.0.0.1:6379> sdiff set01 set02 # 在set01中存在、在set02中不存在的元素
1) "4"
2) "5"
127.0.0.1:6379> sdiff set02 set01 # 在set02中存在、在set01中不存在的元素
1) "b"
2) "a"
```



#### 3.1.4 哈希 Hash

- 特点：类似 Java 中的`Map<String, Object>`，KV 模式不变，但 V 是键值对。

(1)  **hset/hget/hmset/hmget/hgetall/hdel**（添加单个属性 / 获取单个属性 / 添加多个属性 / 获取多个属性 / 获取所有属性 / 删除属性）

```bash
127.0.0.1:6379> hset user id 1001 # 添加user，属性id=1001
(integer) 1
127.0.0.1:6379> hget user # 报错，查询需指明具体字段
(error) ERR wrong number of arguments for 'hget' command
127.0.0.1:6379> hget user id # 查询user的id属性
"1001"
127.0.0.1:6379> hmset student id 101 name tom age 22 # 添加student，多个属性
OK
127.0.0.1:6379> hget student name # 获取student的name属性
"tom"
127.0.0.1:6379> hmget student name age # 获取student的name和age属性
1) "tom"
2) "22"
127.0.0.1:6379> hgetall student # 获取student的所有属性
1) "id"
2) "101"
3) "name"
4) "tom"
5) "age"
6) "22"
127.0.0.1:6379> hdel student age # 删除student的age属性
(integer) 1 # 删除成功
127.0.0.1:6379> hgetall student
1) "id"
2) "101"
3) "name"
4) "tom"
```

(2)  **hlen**（返回元素的属性个数）

```bash
127.0.0.1:6379> hgetall student
1) "id"
2) "101"
3) "name"
4) "tom"
127.0.0.1:6379> hlen student
(integer) 2 # student有2个属性（id和name）
```

(3)  **hexists**（判断元素是否存在指定属性）

```bash
127.0.0.1:6379> hexists student name # student是否存在name属性
(integer) 1 # 存在
127.0.0.1:6379> hexists student age # student是否存在age属性
(integer) 0 # 不存在
```

(4)  **hkeys/hvals**（获得所有属性名 / 获得所有属性值）

```bash
127.0.0.1:6379> hkeys student # 获取student的所有属性名
1) "id"
2) "name"
127.0.0.1:6379> hvals student # 获取student的所有属性值
1) "101"
2) "tom"
```

(5)  **hincrby/hincrbyfloat**（属性值自增：`hincrby`自增整数，`hincrbyfloat`自增小数）

```bash
127.0.0.1:6379> hmset student id 101 name tom age 22
OK
127.0.0.1:6379> hincrby student age 2 # age属性自增整数2
(integer) 24
127.0.0.1:6379> hget student age
"24"
127.0.0.1:6379> hmset user id 1001 money 1000
OK
127.0.0.1:6379> hincrbyfloat user money 5.5 # money属性自增小数5.5
"1005.5"
127.0.0.1:6379> hget user money
"1005.5"
```

(6)  **hsetnx**（添加属性时先判断是否存在，存在则添加失败）

```bash
127.0.0.1:6379> hsetnx student age 18 # 添加age属性，已存在则失败
(integer) 0 # 添加失败
127.0.0.1:6379> hsetnx student sex 男 # 添加sex属性，不存在则成功
(integer) 1 # 添加成功
127.0.0.1:6379> hgetall student
1) "id"
2) "101"
3) "name"
4) "tom"
5) "age"
6) "24"
7) "sex"
8) "\xe7\x94\xb7" # 中文显示乱码（后期解决）
```



#### 3.1.5 有序集合 Zset

- 真实需求：充 10 元享 vip1，充 20 元享 vip2，充 30 元享 vip3，以此类推（通过分数排序）

 (1)  **zadd/zrange (withscores)**（添加元素 / 查询元素，`withscores`表示携带分数）

```bash
127.0.0.1:6379> zadd zset01 10 vip1 20 vip2 30 vip3 40 vip4 50 vip5
(integer) 5
127.0.0.1:6379> zrange zset01 0 -1 # 查询数据（仅元素）
1) "vip1"
2) "vip2"
3) "vip3"
4) "vip4"
5) "vip5"
127.0.0.1:6379> zrange zset01 0 -1 withscores # 携带分数查询数据
1) "vip1"
2) "10"
3) "vip2"
4) "20"
5) "vip3"
6) "30"
7) "vip4"
8) "40"
9) "vip5"
10) "50"
```

(2)  **zrangebyscore**（按分数范围模糊查询，`(`表示不包含，`limit`表示跳过 / 截取）

```bash
127.0.0.1:6379> zrangebyscore zset01 20 40 # 20 <= score <= 40
1) "vip2"
2) "vip3"
3) "vip4"
127.0.0.1:6379> zrangebyscore zset01 20 (40 # 20 <= score < 40
1) "vip2"
2) "vip3"
127.0.0.1:6379> zrangebyscore zset01 (20 (40 # 20 < score < 40
1) "vip3"
127.0.0.1:6379> zrangebyscore zset01 10 40 limit 2 2 # 10 <= score <=40，跳过前2个，取2个
1) "vip3"
2) "vip4"
127.0.0.1:6379> zrangebyscore zset01 10 40 limit 2 1 # 10 <= score <=40，跳过前2个，取1个
1) "vip3"
```

(3)  **zrem**（删除指定元素）

```bash
127.0.0.1:6379> zrem zset01 vip5 # 移除vip5
(integer) 1
```

(4)  **zcard/zcount/zrank/zscore**（集合长度 / 分数范围内元素个数 / 元素下标 / 元素对应的分数）

```bash
127.0.0.1:6379> zcard zset01 # 集合中元素的个数
(integer) 4
127.0.0.1:6379> zcount zset01 20 30 # 分数在20~30之间的元素个数
(integer) 2
127.0.0.1:6379> zrank zset01 vip3 # vip3 在集合中的下标（从上向下）
(integer) 2 
127.0.0.1:6379> zscore zset01 vip2 # 通过元素获取对应的分数
"20"
```

(5)  **zrevrank**（逆序找下标，从下向上计数）

```bash
127.0.0.1:6379> zrevrank zset01 vip3
(integer) 1
```

(6)  **zrevrange**（逆序查询元素）

```bash
127.0.0.1:6379> zrange zset01 0 -1 # 顺序查询
1) "vip1"
2) "vip2"
3) "vip3"
4) "vip4"
127.0.0.1:6379> zrevrange zset01 0 -1 # 逆序查询
1) "vip4"
2) "vip3"
3) "vip2"
4) "vip1"
```

(7)  **zrevrangebyscore**（逆序范围查找，需先写大分数，再写小分数）

```bash
127.0.0.1:6379> zreangebyscore zset01 30 20 # 逆序查询分数在30~20之间的元素
1) "vip3"
2) "vip2"
127.0.0.1:6379> zrevrangebyscore zset01 20 30 # 小分数在前，结果为空
(empty array)
```



### 3.2 持久化

#### 3.2.1 RDB（<font color="red">R</font>edis <font color="red">D</font>ata<font color="red">B</font>ase）

- 原理：在指定的时间间隔内，将内存中的数据集快照写入磁盘，默认保存路径为`/usr/local/bin`，文件名为`dump.rdb`。

##### 3.2.1.1 自动备份

Redis 是内存数据库，默认情况下，关机时会自动将数据备份到`/usr/local/bin/dump.rdb`，因此重启后数据仍存在。

（1）修改自动备份策略（默认策略不利于测试，需修改`redis.conf`)

```bash
vim /root/tools/redis-6.2.14/redis.conf
/SNAP # 搜索备份配置
save 900 1 # 900 秒内，至少变更1次，触发自动备份
save 120 10 # 120 秒内，至少变更10次，触发自动备份
save 60 10000 # 60 秒内，至少变更10000次，触发自动备份
```

- 若仅用 Redis 缓存功能，无需持久化，可注释所有`save`行，或添加`save ""`停用备份。

（2）使用`shutdown`模拟关机，观察`dump.rdb`文件更新时间（当我们使用shutdown命令，redis会自动将数据库备份，所以，dump.rdb文件创建时间更新了）。

（3）开机启动redis，我们要在120秒内保存10条数据，再查看`dump.rdb`文件的更新时间（开两个终端窗口,方便查看）。

（4）120秒内保存10条数据这一动作触发了备份指令，目前，dump.rdb文件中保存了10条数据，将
dump.rdb拷贝一份dump10.rdb，此时两个文件中都保存10条数据。

（5）既然数据已备份好，执行`flushall`删除所有数据，再`shutdown`关机（此时会备份空数据库，覆盖原`dump.rdb`）

（6）再次启动redis，发现数据真的消失了，并没有按照我们所想的 将dump.rdb文件中的内容恢复到
redis中。为什么？

> 因为，当我们保存10条以上的数据时，数据备份起来了；
>
> 然后删除数据库，备份文件中的数据，也没问题；
>
> 但是，问题出在shutdown上,这个命令一旦执行，就会立刻备份，将删除之后的空数据库生成备份文件，将之前装10条数据的备份文件覆盖掉了。所以，就出现了上图的结果。自动恢复失败。
>
> 怎么解决这个问题呢？要将备份文件再备份。

（7）删除`dump.rdb`，将`dump10.rdb`重命名为`dump.rdb` 

（8）启动redis服务，登录redis，数据10条，全部恢复！



##### 3.2.1.2 手动备份

- 之前自动备份，必须更改好多数据，例如上边，我们改变了十多条数据，才会自动备份；

  现在，我只保存一条数据，就想立刻备份，应该怎么做？

- 每次操作完成，执行命令 `save` 就会立刻备份。



##### 3.2.1.3 与 RDB 相关的配置

1. `stop-writes-on-bgsave-error`：后台备份出错时是否停止前台写入
   - `yes`：后台备份出错，前台停止写入（默认）
   - `no`：无论备份是否出错，前台继续写入
2. `rdbcompression`：是否启用 LZF 压缩算法（快照存储到磁盘时）
   - `yes`：启用（默认，性能影响小）
   - `no`：不启用（节省 CPU，文件体积大）
3. `rdbchecksum`：是否启用 CRC64 算法校验快照数据
   - 启用：增加约 10% CPU 消耗，数据更可靠
   - 禁用：性能更高，无校验
4. `dbfilename`：快照备份文件名（默认`dump.rdb`）
5. `dir`：快照备份文件保存目录（默认当前目录）

、

##### 3.2.1.4 优势与劣势

- 优势：适合大规模数据恢复，对数据完整性和一致性要求不高的场景。
- 劣势：按间隔备份，意外宕机时会丢失最后一次快照后的所有修改。

、

#### 3.2.2 AOF（<font color="red">A</font>ppend <font color="red">O</font>nly <font color="red">F</font>ile）

- 以日志形式记录所有写操作；	
- 将redis执行过的写指令全部记录下来（读操作不记录）；
- 仅追加文件不可改写；
- Redis 启动时会读取 AOF 文件，从头到尾执行命令重建数据；

##### 3.2.2.1 开启 AOF

（1）备份`redis.conf`配置文件，修改以下内容：

```bash
appendonly yes # 开启AOF
appendfilename appendonly.aof # AOF文件名
```

（2）重新启动 Redis（指定新配置文件）

```
redis-server /root/tools/redis-6.2.14/redis.conf
```

（3）连接redis，加数据，删库，退出

（4）查看当前文件夹多一个aof文件，看看文件中的内容，保存的都是<font color="red">写</font>操作

- 删除最后一条`flushall`命令（否则恢复后数据为空）
- 编辑这个文件，最后要 `:wq!` 强制执行

（5）只需要重新连接，数据恢复成功



##### 3.2.2.2 AOF 与 RDB 共存优先级

```markdown
1. 动手试试，编辑appendonly.aof，胡搞乱码，保存退出

2. 启动redis 失败，所以是AOF优先载入来恢复原始数据！因为AOF比RDB数据保存的完整性更高！

3. 修复AOF文件，杀光不符合redis语法规范的代码
```

```bash
redis-check-aof --fix appendonly.aof
```

**总结：**

- 两者可同时开启，Redis 启动时优先载入 AOF 文件恢复数据（AOF 数据完整性更高）。
- 若 AOF 文件损坏（如乱码），启动 Redis 会失败；需修复 AOF 文件：`redis-check-aof --fix appendonly.aof`，修复后重启。



##### 3.2.2.3 与 AOF 相关的配置

1. `appendonly`：是否开启 AOF（`yes`开启，`no`关闭，默认`no`）
2. `appendfilename`：AOF 文件名（默认`appendonly.aof`，建议不修改）
3. `appendfsync`：AOF 追写策略
   - `always`：每次数据变更**立即写入**磁盘，性能差，数据完整性最好
   - `everysec`：默认，**每秒写入一次**，折中方案（1 秒内宕机可能丢失数据）
   - `no`：不主动追写，由操作系统决定写入时机，性能最好，数据安全性最低
4. `no-appendfsync-on-rewrite`：AOF 重写时是否暂停`appendfsync`
   - 默认`no`，保证数据安全性（重写时仍执行追写）
5. `auto-aof-rewrite-percentage`：AOF 文件大小超过原大小 100%（即翻倍）时触发重写
6. `auto-aof-rewrite-min-size`：AOF 文件超过 64MB 时触发重写（避免小文件频繁重写）



##### 3.2.2.4 AOF 重写机制

- 背景：AOF 文件追加写操作，体积会越来越大，重写机制可压缩文件，仅保留重建数据的最小指令集合。
- 触发条件：满足`auto-aof-rewrite-percentage`和`auto-aof-rewrite-min-size`两个条件时自动重写。



#### 3.2.3 持久化方案选择

- **RDB**：仅用作后备，建议 15 分钟备份一次。

- **AOF**：
  - 数据完整性高（最恶劣情况丢失不超过 2 秒数据），但持续 IO 开销大；
  - 对硬盘的大小要求也高，默认64mb太小了，企业级`auto-aof-rewrite-min-size`最少都是5G以上；
  - 企业推荐：结合主从复制（master/slave）使用（类似新浪微博架构）。



### 3.3 事务

- 一次执行多个命令，命令序列化排队（顺序执行、排他性），不会被其他命令插队。

#### 事务三特性

1. **隔离性**：所有命令按顺序执行，事务执行过程中不会被其他客户端命令打断。
2. **无隔离级别**：队列中的命令未提交前不会实际执行，不存在 “事务内查询可见、事务外不可见” 问题。
3. **不保证原子性**：冤有头债有主，若一个命令失败，其他命令可能执行成功（无回滚机制）。

#### 事务执行步骤（三步走）

1. 开启事务：`multi`
2. 命令入队：输入的命令返回`QUEUED`，暂不执行
3. 执行事务：`exec`；放弃事务：`discard`

> 与关系型数据库事务相比，
> multi：可以理解成关系型事务中的 begin
> exec ：可以理解成关系型事务中的 commit
> discard ：可以理解成关系型事务中的 rollback



#### 3.3.1  一起生

开启事务，加入队列，一起执行，并成功

```bash
127.0.0.1:6379> MULTI       # 开启事务
OK
127.0.0.1:6379(TX)> set k1 v1
QUEUED  # 加入队列
127.0.0.1:6379(TX)> set k2 v2
QUEUED  # 加入队列
127.0.0.1:6379(TX)> get k2
QUEUED  # 加入队列
127.0.0.1:6379(TX)> set k3 v3
QUEUED  # 加入队列
127.0.0.1:6379(TX)> EXEC   # 执行，所有命令成功
1) OK
2) OK
3) "v2"
4) OK
```



#### 3.3.2  一起死

开启事务→命令入队→放弃事务，恢复原始数据

```bash
127.0.0.1:6379> multi # 开启事务
OK
127.0.0.1:6379(TX)> set k1 v1111
QUEUED
127.0.0.1:6379(TX)> set k2 v2222
QUEUED
127.0.0.1:6379(TX)> discard # 放弃操作
OK
127.0.0.1:6379> get k1
"v1" # 仍为原始值
```



#### 3.3.3  一粒老鼠屎坏一锅汤

一句报错，全部取消，恢复到原来的值

```bash
127.0.0.1:6379> multi
OK
127.0.0.1:6379(TX)> set k4 v4
QUEUED
127.0.0.1:6379(TX)> setlalala # 语法错误
(error) ERR unknown command `setlalala`, with args beginning with: 
127.0.0.1:6379(TX)> set k5 v5
QUEUED
127.0.0.1:6379(TX)> exec # 队列中命令全部取消
(error) EXECABORT Transaction discarded because of previous errors.
127.0.0.1:6379> keys * # 数据仍为原始值
1) "k2"
2) "k3"
3) "k1"
```



#### 3.3.4  冤有头债有主

追究责任，谁的错，找谁去

```bash
127.0.0.1:6379> multi
OK
127.0.0.1:6379> incr k1 # 虽然v1不能++，但是加入队列并没有报错，类似java中的通过编译
QUEUED
127.0.0.1:6379> set k4 v4
QUEUED
127.0.0.1:6379> set k5 v5
QUEUED
127.0.0.1:6379> exec
1) (error) ERR value is not an integer or out of range # 真正执行的时候，报错
2) OK # 成功
3) OK # 成功
127.0.0.1:6379> keys *
1) "k5"
2) "k1"
3) "k3"
4) "k2"
5) "k4"
```



#### 3.3.5  watch监控

监控指定键，若执行`exec`前键被其他客户端修改，事务会失效。

示例：模拟收入与支出

- **正常情况**（无并发修改）：

```bash
127.0.0.1:6379> set in 100 # 收入100元
OK
127.0.0.1:6379> set out 0 # 支出0元
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby in 20 # 收入-20
QUEUED
127.0.0.1:6379> incrby out 20 # 支出+20
QUEUED
127.0.0.1:6379> exec
1) (integer) 80
2) (integer) 20 # 执行成功
```

- **特殊情况**（并发修改监控键）：

```bash
127.0.0.1:6379> watch in # 监控收入in
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby in 20
QUEUED
127.0.0.1:6379> incrby out 20
QUEUED
# 执行exec前，开启另一个窗口修改in的值（如set in 5）
127.0.0.1:6379> exec
(nil) # 事务失效（返回nil）
```

- 取消监控：`unwatch`（执行`exec`或`discard`后，监控自动失效）。



### 3.4 Redis 的发布订阅

进程间的一种消息通信模式：发送者(pub)发送消息，订阅者(sub)接收消息。例如：微信订阅号

订阅一个或多个频道

```bash
127.0.0.1:6379> subscribe cctv1 cctv5 cctv6 # 订阅三个频道
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "cctv1"
3) (integer) 1
1) "subscribe"
2) "cctv5"
3) (integer) 2
1) "subscribe"
2) "cctv6"
3) (integer) 3
# 接收消息时会显示：
1) "message"
2) "cctv5"
3) "NBA"
1) "message"
2) "cctv1"
3) "News"
```

```bash
127.0.0.1:6379> publish cctv5 NBA # 向cctv5频道发送消息"NBA"
(integer) 1 # 表示1个订阅者接收成功
127.0.0.1:6379> PUBLISH cctv1 News
(integer) 1
127.0.0.1:6379> PUBLISH cctv2 News
(integer) 0 # 发布失败，因为没有这个频道
```



### 3.5 主从复制（Redis 集群策略）

**原则**

- 配从（库）不配主（库），从库主动选择主库，主库无法选择从库；
- 实现读写分离（主机写，从机读）。

#### 3.5.1 一主二仆（1 主 2 从架构）

（1）准备三台服务器，均修改`redis.conf`

```bash
bind 0.0.0.0   #（允许所有 IP 访问）
```

（2）启动三台 Redis，默认均为`master`角色

```
info replication
```

（3）测试开始

1. 首先，将三个机器全都清空，第一台添加值

   ```
   mset k1 v1 k2 v2	
   ```

2. 其余两台机器（从库），复制（找大哥）

   ```
   slaveof 192.168.44.128 6379
   ```

3. 第一台（主库）再添加值，从库可通过`get k3`获取（同步成功）

   ```
   set k3 v3
   ```

   

- <font color="red">**思考 1**</font>：**从库关联主库前，主库已有的 k1、k2 能否获取？**
  -  <font color="blue">可以，关联后立即同步历史数据。</font>
- <font color="red">**思考 2**</font>：**从库关联后，主库新增的 k3 能否获取？**
  - <font color="blue">可以，实时同步新数据。</font>
- <font color="red">**思考 3**</font>：**主库和从库同时添加 k4，结果如何？**
  -  <font color="blue">主库添加成功，从库添加失败（从库只读，无权写入数据，这就是“读写分离）。</font>
- <font color="red">**思考 4**</font>：**主库执行`shutdown`宕机，从库状态如何？**
  -  <font color="blue">从库仍为`slave`，显示`master`已离线。</font>
- <font color="red">**思考 5**</font>：**主库重启后，从库状态如何？**
  - <font color="blue"> 从库自动重新关联主库，恢复同步。</font>
- <font color="red">**思考 6**</font>：**从机死了，主机如何？从机归来身份是否变化？**
  -  <font color="blue">主机没有变化,只是显示少了一个slave。</font>
  - <font color="blue">主机和从机没有变化，而重启归来的从机自立门户成为了master，不和原来的集群在一
    起了。</font>



#### 3.5.2 血脉相传（级联复制）

一个主机理论上可以多个从机，但是这样的话，这个主机会很累

我们可以使用java面向对象<font color="red">**继承中的传递性**</font>来解决这个问题，减轻主机的负担

形成祖孙三代:

```shell
127.0.0.1:6379> slaveof 192.168.44.128 6379  # 129跟随128
OK
127.0.0.1:6379> slaveof 192.168.44.129 6379  # 130跟随129
OK
```



#### 3.5.3 谋权篡位（手动故障转移）

1 主 2 从架构中，主库宕机后，需手动从从库中选择新主库。

模拟测试：1为master，2和3为slave，当1挂掉后，2篡权为master，3跟2

```bash
slaveof no one                             # 2晋升为新主库
```

```bash
slaveof 192.168.44.129 6379               # 3关联新主库
```

<font color="red">**思考**</font>：**当1再次回归，会怎样？**

-  原主库1成为独立`master`，与新集群无关。

​	

#### 3.5.4 复制原理

![0107](D:\Program\MyNotes\notes_image\Redis\Redis0107.png)

完成上面几个步骤后就完成了从服务器数据初始化的所有操作，从服务器此时可以接收来自用户的读请
求

1. <font color="blue">**全量复制**</font>（Slave 初始化阶段）：Slave初始化阶段，这时Slave需要将Master上的所有数据都复制一份slave接收到数据文件后，存盘，并加载到内存中；（步骤1234）
2. **<font color="blue">增量复制</font>**（Slave 初始化后）：Slave初始化后，开始正常工作时主服务器发生的写操作同步到从服务器的过程；（步骤56）
3. **同步策略**：
   - 主从刚连接时，执行全量复制；全量复制结束后，执行增量复制。
   - 从库重新连接主库时，自动执行全量复制。
   - Redis策略：无论如何，优先尝试增量复制，失败则触发从机全量复制。



#### 3.5.5 哨兵模式（自动故障转移）

自动版的谋权篡位！有个哨兵一直在巡逻，突然发现！！！老大挂了，小弟们会自动投票，从众小弟中选出新的老大。

Sentinel 是 Redis 的高可用性解决方案：

- 由一个或多个 Sentinel 实例组成的 Sentinel 系统可以监视任意多个主服务器，以及所有从服务器，并在被监视的主服务器进入下线状态时，自动将下线主服务器属下的某个从服务器升级为新的主服务器，然后由新的主服务器代替已下线的主服务器继续处理命令请求

模拟测试

1. 1 主，2 和 3 从。

2. 每一台服务器中创建一个配置文件 sentinel.conf，名字绝不能错，并编辑 sentinel.conf

   ```bash
   # sentinel monitor 被监控主机名(自定义) ip port 票数
   sentinel monitor redis128 192.168.44.128 6379 1
   ```

3. 启动服务的顺序：主 Redis → 从 Redis → Sentinel1/2/3

   ```bash
   redis-sentinel sentinel.conf
   ```

4. 将 1 号老大挂掉，后台自动发起激烈的投票，选出新的老大

   ```basj
   127.0.0.1:6379> shutdown
   not connected> exit
   ```

5. 查看最后权利的分配

   - 3 成为了新的老大（主机），2 还是小弟（从机）

6.  如果之前的老大再次归来呢？

   - 1 号再次归来，自己成为了 master，和 3 平起平坐。

   - 过了几秒之后，被哨兵检测到了 1 号机的归来，1 号你别自己玩了，进入集体吧，但是新的老大已经产生了，你只能作为小弟再次进入集体！



#### 3.5.6 缺点

- 由于所有的写操作都是在 master 上完成然后再同步到 slave 上，所以两台机器之间通信会有延迟；

- 当系统很繁忙的时候，延迟问题会加重；
- slave 机器数量增加，问题也会加重。



### 3.6 总配置 redis.conf 详解

```bash
# Redis 配置文件示例
# 注意单位: 当需要配置内存大小时, 可能需要指定像1k,5GB,4M等常见格式
# 1k => 1000 bytes # 1kb => 1024 bytes 
# 1m => 1000000 bytes # 1mb => 1024*1024 bytes 
# 1g => 1000000000 bytes # 1gb => 1024*1024*1024 bytes
# 单位是对大小写不敏感的 1GB 1Gb 1gB 是相同的。

################################## INCLUDES 包含文件 ##################################
# 可以在这里包含一个或多个其他的配置文件。如果你有一个适用于所有Redis服务器的标准配置模板 
# 但也需要一些每个服务器自定义的设置，这个功能将很有用。被包含的配置文件也可以包含其他配置文件， 
# 所以需要谨慎的使用这个功能。
# 注意“inclue”选项不能被admin或Redis哨兵的"CONFIG REWRITE"命令重写。 
# 因为Redis总是使用最后解析的配置行最为配置指令的值, 你最好在这个文件的开头配置includes来 
# 避免它在运行时重写配置。
# 如果相反你想用includes的配置覆盖原来的配置，你最好在该文件的最后使用include
# include /path/to/local.conf # include /path/to/other.conf

################################ GENERAL 综合配置 #####################################
# 默认Redis不会作为守护进程运行。如果需要的话配置成'yes' 
# 注意配置成守护进程后Redis会将进程号写入文件/var/run/redis.pid 
daemonize no

# 当以守护进程方式运行时，默认Redis会把进程ID写到 /var/run/redis.pid。你可以在这里修改路径。 
pidfile /var/run/redis.pid

# 接受连接的特定端口，默认是6379 
# 如果端口设置为0，Redis就不会监听TCP套接字。 
port 6379 

# TCP listen() backlog. 
# server在与客户端建立tcp连接的过程中，SYN队列的大小 
# 在高并发环境下你需要一个高backlog值来避免慢客户端连接问题。注意Linux内核默默地将这个值减小 
# 到/proc/sys/net/core/somaxconn的值，所以需要确认增大somaxconn和tcp_max_syn_backlog 
# 两个值来达到想要的效果。
tcp-backlog 511

# 默认Redis监听服务器上所有可用网络接口的连接。可以用"bind"配置指令跟一个或多个ip地址来实现 监听一个或多个网络接口
# 示例: 
# bind 192.168.1.100 10.0.0.1 
# bind 127.0.0.1

# 指定用来监听Unix套套接字的路径。没有默认值, 所以在没有指定的情况下Redis不会监听Unix套接字
# unixsocket /tmp/redis.sock 
# unixsocketperm 755

# 一个客户端空闲多少秒后关闭连接。(0代表禁用，永不关闭) 
timeout 0

# TCP keepalive.
# 如果非零，则设置SO_KEEPALIVE选项来向空闲连接的客户端发送ACK，由于以下两个原因这是很有用 的:
# 1)能够检测无响应的对端
# 2)让该连接中间的网络设备知道这个连接还存活
# 在Linux上，这个指定的值(单位:秒)就是发送ACK的时间间隔。 
# 注意:要关闭这个连接需要两倍的这个时间值。 
# 在其他内核上这个时间间隔由内核配置决定
# 这个选项的一个合理值是60秒 
tcp-keepalive 0

# 指定服务器调试等级 
# 可能值: 
# debug (大量信息，对开发/测试有用) 
# verbose (很多精简的有用信息，但是不像debug等级那么多) 
# notice (适量的信息，基本上是你生产环境中需要的) 
# warning (只有很重要/严重的信息会记录下来) 
loglevel notice

# 指明日志文件名。也可以使用"stdout"来强制让Redis把日志信息写到标准输出上。 
# 注意:如果Redis以守护进程方式运行，而设置日志显示到标准输出的话，日志会发送到/dev/null 
logfile ""

# 要使用系统日志记录器，只要设置 "syslog-enabled" 为 "yes" 就可以了。 
# 然后根据需要设置其他一些syslog参数就可以了。 
# syslog-enabled no

# 指明syslog身份 
# syslog-ident redis

# 指明syslog的设备。必须是user或LOCAL0 ~ LOCAL7之一。 
# syslog-facility local0

# 设置数据库个数。默认数据库是 DB 0,
# 可以通过select <dbid> (0 <= dbid <= 'databases' - 1 )来为每个连接使用不同的数据 库。 
databases 16

################################ SNAPSHOTTING 快照，持久化操作配置 ################################
# 把数据库存到磁盘上:
# save <seconds> <changes>
# 会在指定秒数和数据变化次数之后把数据库写到磁盘上。
# 下面的例子将会进行把数据写入磁盘的操作:
# 900秒(15分钟)之后，且至少1次变更 
# 300秒(5分钟)之后，且至少10次变更 
# 60秒之后，且至少10000次变更
# 注意:你要想不写磁盘的话就把所有 "save" 设置注释掉就行了。
# 
# 通过添加一条带空字符串参数的save指令也能移除之前所有配置的save指令 像下面的例子:
# save ""
save 900 1 
save 300 10 
save 60 10000

# 默认如果开启RDB快照(至少一条save指令)并且最新的后台保存失败，Redis将会停止接受写操作 
# 这将使用户知道数据没有正确的持久化到硬盘，否则可能没人注意到并且造成一些灾难。
# 如果后台保存进程能重新开始工作，Redis将自动允许写操作 
#
# 然而如果你已经部署了适当的Redis服务器和持久化的监控，你可能想关掉这个功能以便于即使是 
# 硬盘，权限等出问题了Redis也能够像平时一样正常工作, 
stop-writes-on-bgsave-error yes

# 当导出到 .rdb 数据库时是否用LZF压缩字符串对象? 
# 默认设置为 "yes"，因为几乎在任何情况下它都是不错的。 
# 如果你想节省CPU的话你可以把这个设置为 "no"，但是如果你有可压缩的key和value的话， 
# 那数据文件就会更大了。 
rdbcompression yes

# 因为版本5的RDB有一个CRC64算法的校验和放在了文件的最后。这将使文件格式更加可靠但在 
# 生产和加载RDB文件时，这有一个性能消耗(大约10%)，所以你可以关掉它来获取最好的性能。
# 生成的关闭校验的RDB文件有一个0的校验和，它将告诉加载代码跳过检查 
rdbchecksum yes

# 持久化数据库的文件名 
dbfilename dump.rdb

# 工作目录
# 数据库会写到这个目录下，文件名就是上面的 "dbfilename" 的值。
# 累加文件也放这里。
# 注意你这里指定的必须是目录，不是文件名。 
dir ./

################################# REPLICATION 主从复制的配置 #################################
# 主从同步。通过 slaveof 指令来实现Redis实例的备份。 
# 注意，这里是本地从远端复制数据。也就是说，本地可以有不同的数据库文件、绑定不同的IP、监听 
# 不同的端口。
# slaveof <masterip> <masterport>

# 如果master设置了密码保护(通过 "requirepass" 选项来配置)，那么slave在开始同步之前必须 
# 进行身份验证，否则它的同步请求会被拒绝。
# masterauth <master-password>

# 当一个slave失去和master的连接，或者同步正在进行中，slave的行为有两种可能:
# 1) 如果 slave-serve-stale-data 设置为 "yes" (默认值)，slave会继续响应客户端请求， 
# 可能是正常数据，也可能是还没获得值的空数据。 
# 2) 如果 slave-serve-stale-data 设置为 "no"，slave会回复"正在从master同步 
# (SYNC with master in progress)"来处理各种请求，除了 INFO 和 SLAVEOF 命令。 
slave-serve-stale-data yes

# 你可以配置salve实例是否接受写操作。可写的slave实例可能对存储临时数据比较有用(因为写入 salve 
# 的数据在同master同步之后将很容被删除)，但是如果客户端由于配置错误在写入时也可能产生一些问
# 从Redis2.6默认所有的slave为只读
# 注意:只读的slave不是为了暴露给互联网上不可信的客户端而设计的。它只是一个防止实例误用的保护 层。 
# 一个只读的slave支持所有的管理命令比如config，debug等。为了限制你可以用'renamecommand'来 
# 隐藏所有的管理和危险命令来增强只读slave的安全性 
slave-read-only yes

# slave根据指定的时间间隔向master发送ping请求。 
# 时间间隔可以通过 repl_ping_slave_period 来设置。 
# 默认10秒。
# repl-ping-slave-period 10

# 以下选项设置同步的超时时间
# 1)slave在与master SYNC期间有大量数据传输，造成超时 
# 2)在slave角度，master超时，包括数据、ping等 
# 3)在master角度，slave超时，当master发送REPLCONF ACK pings
# 确保这个值大于指定的repl-ping-slave-period，否则在主从间流量不高时每次都会检测到超时
# repl-timeout 60

# 是否在slave套接字发送SYNC之后禁用 TCP_NODELAY ?
# 如果你选择“yes”Redis将使用更少的TCP包和带宽来向slaves发送数据。但是这将使数据传输到 slave 
# 上有延迟，Linux内核的默认配置会达到40毫秒
# 如果你选择了 "no" 数据传输到salve的延迟将会减少但要使用更多的带宽
# 默认我们会为低延迟做优化，但高流量情况或主从之间的跳数过多时，把这个选项设置为“yes” 
# 是个不错的选择。 
repl-disable-tcp-nodelay no

# 设置数据备份的backlog大小。backlog是一个slave在一段时间内断开连接时记录salve数据的缓冲，
# 所以一个slave在重新连接时，不必要全量的同步，而是一个增量同步就足够了，将在断开连接的这段时间内slave丢失的部分数据传送给它。
# 同步的backlog越大，slave能够进行增量同步并且允许断开连接的时间就越长。
# backlog只分配一次并且至少需要一个slave连接
# repl-backlog-size 1mb

# 当master在一段时间内不再与任何slave连接，backlog将会释放。以下选项配置了从最后一个 
# slave断开开始计时多少秒后，backlog缓冲将会释放。
# 0表示永不释放backlog
# repl-backlog-ttl 3600

# slave的优先级是一个整数展示在Redis的Info输出中。如果master不再正常工作了，哨兵将用它来 
# 选择一个slave提升为master。
# 优先级数字小的salve会优先考虑提升为master，所以例如有三个slave优先级分别为10，100，25，哨兵将挑选优先级最小数字为10的slave。
# 0作为一个特殊的优先级，标识这个slave不能作为master，所以一个优先级为0的slave永远不会被哨兵挑选提升为master
# 默认优先级为100 
slave-priority 100

# 如果master少于N个延时小于等于M秒的已连接slave，就可以停止接收写操作。
# N个slave需要是“oneline”状态
# 延时是以秒为单位，并且必须小于等于指定值，是从最后一个从slave接收到的ping(通常每秒发送) 
# 开始计数。
# This option does not GUARANTEES that N replicas will accept the write, but 
# will limit the window of exposure for lost writes in case not enough slaves 
# are available, to the specified number of seconds.
# 例如至少需要3个延时小于等于10秒的slave用下面的指令:
# min-slaves-to-write 3 
# min-slaves-max-lag 10
# 两者之一设置为0将禁用这个功能。
# 默认 min-slaves-to-write 值是0(该功能禁用)并且 min-slaves-max-lag 值是10。

################################## SECURITY 安全相关配置 ###################################
# 要求客户端在处理任何命令时都要验证身份和密码。 
# 这个功能在有你不信任的其它客户端能够访问redis服务器的环境里非常有用。 
#
# 为了向后兼容的话这段应该注释掉。而且大多数人不需要身份验证(例如:它们运行在自己的服务器上)
# 警告:因为Redis太快了，所以外面的人可以尝试每秒150k的密码来试图破解密码。这意味着你需要 
# 一个高强度的密码，否则破解太容易了。
# requirepass foobared

# 命令重命名
# 在共享环境下，可以为危险命令改变名字。比如，你可以为 CONFIG 改个其他不太容易猜到的名字， 
# 这样内部的工具仍然可以使用，而普通的客户端将不行。
# 例如:
# rename-command CONFIG b840fc02d524045429941cc15f59e41cb7be6c52
# 也可以通过改名为空字符串来完全禁用一个命令
# rename-command CONFIG ""
# 请注意:改变命令名字被记录到AOF文件或被传送到从服务器可能产生问题。

################################### LIMITS 范围配置 ####################################
# 设置最多同时连接的客户端数量。默认这个限制是10000个客户端，然而如果Redis服务器不能配置 处理文件的限制数来满足指定的值，那么最大的客户端连接数就被设置成当前文件限制数减32(因 为Redis服务器保留了一些文件描述符作为内部使用)
# 一旦达到这个限制，Redis会关闭所有新连接并发送错误'max number of clients reached'
# maxclients 10000

# 不要用比设置的上限更多的内存。一旦内存使用达到上限，Redis会根据选定的回收策略(参见: 
# maxmemmory-policy)删除key
# 如果因为删除策略Redis无法删除key，或者策略设置为 "noeviction"，Redis会回复需要更 
# 多内存的错误信息给命令。例如，SET，LPUSH等等，但是会继续响应像Get这样的只读命令。
# 在使用Redis作为LRU缓存，或者为实例设置了硬性内存限制的时候(使用 "noeviction" 策略) 
# 的时候，这个选项通常事很有用的。
# 警告:当有多个slave连上达到内存上限的实例时，master为同步slave的输出缓冲区所需 
# 内存不计算在使用内存中。这样当驱逐key时，就不会因网络问题 / 重新同步事件触发驱逐key 
# 的循环，反过来slaves的输出缓冲区充满了key被驱逐的DEL命令，这将触发删除更多的key, 直到这个数据库完全被清空为止
# 总之...如果你需要附加多个slave，建议你设置一个稍小maxmemory限制，这样系统就会有空闲 
# 的内存作为slave的输出缓存区(但是如果最大内存策略设置为"noeviction"的话就没必要了)
# maxmemory <bytes>

# 最大内存策略:如果达到内存限制了，Redis如何选择删除key。你可以在下面五个行为里选:
# volatile-lru -> 根据LRU算法生成的过期时间来删除。 
# allkeys-lru -> 根据LRU算法删除任何key。 
# volatile-random -> 根据过期设置来随机删除key。 
# allkeys-random -> 无差别随机删。 
# volatile-ttl -> 根据最近过期时间来删除(辅以TTL) 
# noeviction -> 谁也不删，直接在写操作时返回错误。
# 注意:对所有策略来说，如果Redis找不到合适的可以删除的key都会在写操作时返回一个错误。 
#
# 目前为止涉及的命令:set setnx setex append 
# incr decr rpush lpush rpushx lpushx linsert lset rpoplpush sadd 
# sinter sinterstore sunion sunionstore sdiff sdiffstore zadd zincrby 
# zunionstore zinterstore hset hsetnx hmset hincrby incrby decrby 
# getset mset msetnx exec sort 
#
# 默认值如下:
# maxmemory-policy volatile-lru

# LRU和最小TTL算法的实现都不是很精确，但是很接近(为了省内存)，所以你可以用样本量做检测。 
# 例如:默认Redis会检查3个key然后取最旧的那个，你可以通过下面的配置指令来设置样本的个数。
# maxmemory-samples 3

############################## APPEND ONLY MODE AOF模式配置 ###############################
# 默认情况下，Redis是异步的把数据导出到磁盘上。这种模式在很多应用里已经足够好，但Redis进程 出问题或断电时可能造成一段时间的写操作丢失(这取决于配置的save指令)。
# AOF是一种提供了更可靠的替代持久化模式，例如使用默认的数据写入文件策略(参见后面的配置) 
# 在遇到像服务器断电或单写情况下Redis自身进程出问题但操作系统仍正常运行等突发事件时，Redis 能只丢失1秒的写操作。
# AOF和RDB持久化能同时启动并且不会有问题。 
# 如果AOF开启，那么在启动时Redis将加载AOF文件，它更能保证数据的可靠性。
# 请查看 http://redis.io/topics/persistence 来获取更多信息.
appendonly no

# 纯累加文件名字(默认:"appendonly.aof")
appendfilename "appendonly.aof"

# fsync() 系统调用告诉操作系统把数据写到磁盘上，而不是等更多的数据进入输出缓冲区。 有些操作系统会真的把数据马上刷到磁盘上;有些则会尽快去尝试这么做。
# Redis支持三种不同的模式:
# no:不要立刻刷，只有在操作系统需要刷的时候再刷。比较快。
# always:每次写操作都立刻写入到aof文件。慢，但是最安全。 
# everysec:每秒写一次。折中方案。
# 默认的 "everysec" 通常来说能在速度和数据安全性之间取得比较好的平衡。根据你的理解来 
# 决定，如果你能放宽该配置为"no" 来获取更好的性能(但如果你能忍受一些数据丢失，可以考虑使用 
# 默认的快照持久化模式)，或者相反，用“always”会比较慢但比everysec要更安全。 
# 请查看下面的文章来获取更多的细节 
# http://antirez.com/post/redis-persistence-demystified.html
# 如果不能确定，就用 "everysec"
# appendfsync always 
appendfsync everysec 
# appendfsync no

# 如果AOF的同步策略设置成 "always" 或者 "everysec"，并且后台的存储进程(后台存储或写入AOF 日志)会产生很多磁盘I/O开销。某些Linux的配置下会使Redis因为 fsync()系统调用而阻塞很久。 
# 注意，目前对这个情况还没有完美修正，甚至不同线程的 fsync() 会阻塞我们同步的write(2)调用。 
# 为了缓解这个问题，可以用下面这个选项。它可以在 BGSAVE 或 BGREWRITEAOF 处理时阻止 fsync()。
# 这就意味着如果有子进程在进行保存操作，那么Redis就处于"不可同步"的状态。 
# 这实际上是说，在最差的情况下可能会丢掉30秒钟的日志数据。(默认Linux设定)
# 如果把这个设置成"yes"带来了延迟问题，就保持"no"，这是保存持久数据的最安全的方式。
no-appendfsync-on-rewrite no

# 自动重写AOF文件 
# 如果AOF日志文件增大到指定百分比，Redis能够通过 BGREWRITEAOF 自动重写AOF日志文件。
# 工作原理:Redis记住上次重写时AOF文件的大小(如果重启后还没有写操作，就直接用启动时的AOF大小)
# 这个基准大小和当前大小做比较。如果当前大小超过指定比例，就会触发重写操作。你还需要指定被重写 
# 日志的最小尺寸，这样避免了达到指定百分比但尺寸仍然很小的情况还要重写。 
# 指定百分比为0会禁用AOF自动重写特性。
auto-aof-rewrite-percentage 100 
auto-aof-rewrite-min-size 64mb

################################ LUA SCRIPTING ###############################
# 设置lua脚本的最大运行时间，单位为毫秒，redis会记个log，然后返回error。
# 当一个脚本超过了最大时限.只有SCRIPT KILL和SHUTDOWN NOSAVE可以用。
# 第一个可以杀没有调write命令的东西。要是已经调用了write，只能用第二个命令杀。 
lua-time-limit 5000

################################## SLOW LOG ###################################
# 是redis用于记录慢查询执行时间的日志系统。由于slowlog只保存在内存中，因此slowlog的效率很高，完全不用担心影响到redis的性能。 
# 只有query执行时间大于slowlog-log-slower-than的才会定义成慢查询，才会被slowlog进行记录。 
# 单位是微秒
slowlog-log-slower-than 10000

# slowlog-max-len表示慢查询最大的条数 
slowlog-max-len 128

############################ EVENT NOTIFICATION ##############################
# 这个功能可以让客户端通过订阅给定的频道或者模式，来获知数据库中键的变化，以及数据库中命令的执行情况，所以在默认配置下，该功能处于关闭状态。 
# notify-keyspace-events 的参数可以是以下字符的任意组合，它指定了服务器该发送哪些类型的通知: 
# K 键空间通知，所有通知以 __keyspace@__ 为前缀 
# E 键事件通知，所有通知以 __keyevent@__ 为前缀 
# g DEL 、 EXPIRE 、 RENAME 等类型无关的通用命令的通知 
# $ 字符串命令的通知 
# l 列表命令的通知 
# s 集合命令的通知 
# h 哈希命令的通知 
# z 有序集合命令的通知 
# x 过期事件:每当有过期键被删除时发送 
# e 驱逐(evict)事件:每当有键因为 maxmemory 政策而被删除时发送 
# A 参数 g$lshzxe 的别名 
# 输入的参数中至少要有一个 K 或者 E，否则的话，不管其余的参数是什么，都不会有任何通知被分发。详细使用可以参考http://redis.io/topics/notifications
notify-keyspace-events ""

############################### ADVANCED CONFIG ###############################
# 单位字节:数据量小于等于hash-max-ziplist-entries的用ziplist，大于hash-max-ziplistentries用hash 
hash-max-ziplist-entries 512 
# value大小小于等于hash-max-ziplist-value的用ziplist，大于hash-max-ziplist-value用 hash。 
hash-max-ziplist-value 64

# 数据量小于等于list-max-ziplist-entries用ziplist(压缩列表)，大于list-max-ziplistentries用list。 
list-max-ziplist-entries 512 
# value大小小于等于list-max-ziplist-value的用ziplist，大于list-max-ziplist-value用 list。 
list-max-ziplist-value 64

# 数据量小于等于set-max-intset-entries用iniset，大于set-max-intset-entries用set。 
set-max-intset-entries 512

# 数据量小于等于zset-max-ziplist-entries用ziplist，大于zset-max-ziplist-entries用 zset。 
zset-max-ziplist-entries 128 
# value大小小于等于zset-max-ziplist-value用ziplist，大于zset-max-ziplist-value用 zset。 
zset-max-ziplist-value 64

# 基数统计的算法 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基数 
# 设置HyperLogLog的字节数限制，这个值通常在0~15000之间，默认为3000，基本不超过16000。 
# value大小小于等于hll-sparse-max-bytes使用稀疏数据结构(sparse)，大于hll-sparse-maxbytes使用稠密的数据结构(dense)。一个比16000大的value是几乎没用的，建议的value大概为 3000。如果对CPU要求不高，对空间要求较高的，建议设置到10000左右。 
hll-sparse-max-bytes 3000

# 重置hash。 Redis将在每100毫秒时使用1毫秒的CPU时间来对redis的hash表进行重新hash，可以降低内存的使用。当你的使用场景中，有非常严格的实时性需要，不能够接受Redis时不时的对请求有2毫秒的延迟的话，把这项配置为no。如果没有这么严格的实时性要求，可以设置为yes，以便能够尽可能快的释放内存。
activerehashing yes

# 对于Redis服务器的输出(也就是命令的返回值)来说，其大小通常是不可控制的。有可能一个简单的命 令，能够产生体积庞大的返回数据。另外也有可能因为执行了太多命令，导致产生返回数据的速率超过了往 客户端发送的速率，这是也会导致服务器堆积大量消息，从而导致输出缓冲区越来越大，占用过多内存，甚 至导致系统崩溃。
# 用于强制断开出于某些原因而无法以足够快的速度从服务器读取数据的客户端的连接。
# 对于normal client，包括monitor。第一个0表示取消hard limit，第二个0和第三个0表示取消 soft limit，normal client默认取消限制，因为如果没有寻问，他们是不会接收数据的。
client-output-buffer-limit normal 0 0 0 
# 对于slave client和MONITER client，如果client-output-buffer一旦超过256mb，又或者超过 64mb持续60秒，那么服务器就会立即断开客户端连接。 
client-output-buffer-limit slave 256mb 64mb 60 
# 对于pubsub client，如果client-output-buffer一旦超过32mb，又或者超过8mb持续60秒，那么 服务器就会立即断开客户端连接。 
client-output-buffer-limit pubsub 32mb 8mb 60

# redis执行任务的频率 
hz 10

# aof rewrite过程中，是否采取增量"文件同步"策略，默认为"yes"，而且必须为yes. 
# rewrite过程中，每32M数据进行一次文件同步，这样可以减少"aof大文件"写入对磁盘的操作次数. 
aof-rewrite-incremental-fsync yes

通常情况下，默认的配置足够你解决问题！
没有极特殊的要求，不要乱改配置！
```



### 3.7 Jedis

- java 和 redis 打交道的 API 客户端

#### 依赖引入

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.1.0</version>
</dependency>
```

#### 3.7.1 连接 redis

```java
public static void main(String[] args) {
    Jedis jedis = new Jedis("192.168.44.128",6379);
    String pong = jedis.ping();
    System.out.println("pong = " + pong);
}
// 运行前:
// 1.关闭Linux防火墙 sudo ufw disable
// 2.修改redis.conf [ bind 0.0.0.0 ] 允许任何ip访问，以这个redis.conf启动redis服务(重启redis)
// redis-server /root/tools/redis-6.2.14/redis.conf
```

#### 3.7.2 常用 API

```java
//常用API
public class TestAPI {

    Jedis jedis = new Jedis("192.168.44.128",6379);

    @Test
    public void testTypeString(){
        jedis.set("k1","v1");
        jedis.set("k2","v2");
        jedis.set("k3","v3");
        //查看所有键值
        Set<String> set = jedis.keys("*");
        Iterator<String> iterator = set.iterator();
        for (set.iterator(); iterator.hasNext();){
            String key = iterator.next();
            System.out.println("key: " + key + "; value: " + jedis.get(key));
        }
        //查看k2是否存在
        Boolean k2Exist = jedis.exists("k2");
        System.out.println("k2是否存在：" + k2Exist);
        //查看k1过期时间
        System.out.println(jedis.ttl("k1"));
        //String
        jedis.mset("k4","v4","k5","v5");
        System.out.println(jedis.mget("k1","k2","k3","k4","k5"));
        System.out.println("--------------------------------------");
    }

    @Test
    //list
    public void testTypeList() {
        jedis.lpush("list01","l1","l2","l3","l4","l5");
        System.out.println(jedis.lrange("list01",0,-1));
//        jedis.lpop("list01",9);
    }

    @Test
    //set
    public void testTypeSet() {
        jedis.sadd("order","jd001","jd002","jd003");
        Set<String> order = jedis.smembers("order");
        for (String s : order){
            System.out.println(s);
        }
        jedis.srem("order","jd002");
        System.out.println(jedis.smembers("order").size());
    }

    @Test
    //hash
    public void testTypeHash() {
        jedis.hset("user1","username","Leblanc");
        System.out.println(jedis.hget("user1","username"));

        HashMap<String, String> userMap = new HashMap<String, String>();
        userMap.put("username","Tom");
        userMap.put("gender","male");
        userMap.put("address","Beijing");
        userMap.put("phone","123-8888-8888");
        jedis.hmset("user2",userMap);
        List<String> List = jedis.hmget("user2", "username", "gender", "phone");
        for (String s : List){
            System.out.println(s);
        }
    }

    @Test
    //zset
    public void testTypeZSet(){
        jedis.zadd("zset01",60d,"zs1");
        jedis.zadd("zset01",70d,"zs2");
        jedis.zadd("zset01",80d,"zs3");
        jedis.zadd("zset01",90d,"zs4");
        List<String> zset01 = jedis.zrange("zset01", 0, -1);
        for (String s : zset01){
            System.out.println(s);
        }
    }
}

```

#### 3.7.3 事务

**初始化数据**

```bash
set balance 100 
set expense 0
```

**事务代码实现**

```java
public class TestTX {
    Jedis jedis = new Jedis("192.168.44.128",6379);
    @Test
    public void test1() throws InterruptedException {
        // 获取当前余额
        int balance = Integer.parseInt(jedis.get("balance"));
        // 本次支出金额
        int expense = 10;
        // 监控余额键，防止并发修改
        jedis.watch("balance");
        //模拟网络延迟5秒
        Thread.sleep(5000);

        if (balance < expense) {
            // 余额不足，解除监控
            jedis.unwatch();
            System.out.println("余额不足");
        }else {
            // 开启事务
            Transaction tx = jedis.multi();
            tx.decrBy("balance",expense);
            tx.incrBy("expense",expense);
            tx.exec();
        System.out.println("目前余额： " + jedis.get("balance"));
        System.out.println("累计支出： " + jedis.get("expense"));
        }
    }
}
```

模拟网络延迟：10 秒内，进入 linux 修改余额为 5，这样余额 < 支出，就会进入`if`分支，事务不执行。

#### 3.7.4 JedisPool

**依赖引入**

```xml
<dependency>
    <groupId>commons-pool</groupId>
    <artifactId>commons-pool</artifactId>
    <version>1.6</version>
</dependency>
```

**使用单例模式优化连接池**

```java
/*
  单例模式优化jedis连接池
 */
public class JedisPoolUtil {
    // 私有构造方法，防止外部实例化
    private JedisPoolUtil(){}
    
    // volatile关键字保证多线程下可见性和禁止指令重排
    private volatile static JedisPool jedisPool = null;
    private volatile static Jedis jedis = null;
    
    // 返回一个连接池（双层检测锁单例）
    private static JedisPool getInstance(){
        // 第一层检测：避免不必要的同步
        if(jedisPool == null){ 
            // 同步代码块，保证线程安全
            synchronized (JedisPoolUtil.class){ 
                // 第二层检测：防止多线程同时进入同步块后重复创建实例
                if(jedisPool == null) { 
                    // 配置连接池参数
                    JedisPoolConfig config = new JedisPoolConfig();
                    config.setMaxTotal(1000); // 资源池中的最大连接数
                    config.setMaxIdle(30); // 资源池允许的最大空闲连接数
                    // 当资源池连接用尽后，调用者的最大等待时间（单位为毫秒）
                    config.setMaxWaitMillis(60*1000); 
                    // 向资源池借用连接时是否做连接有效性检测（业务量大时建议设为false，减少ping开销）
                    config.setTestOnBorrow(true); 
                    // 创建连接池实例
                    jedisPool = new JedisPool( config, "192.168.204.141",6379 );
                }
            }
        }
        return jedisPool;
    }
    
    // 返回jedis对象（从连接池获取）
    public static Jedis getJedis(){
        if(jedis == null){
            jedis = getInstance().getResource();
        }
        return jedis;
    }
}
```

**测试类**

```java
/* 测试jedis连接池
 */
public class Test_JedisPool {
    public static void main(String[] args) {
        // 从连接池获取两个jedis对象
        Jedis jedis1 = JedisPoolUtil.getJedis();
        Jedis jedis2 = JedisPoolUtil.getJedis();
        // 判断两个jedis对象是否为同一个（验证连接池复用机制）
        System.out.println(jedis1==jedis2);
    }
}
```



### 3.8 高并发下的分布式锁

经典案例：秒杀、抢购优惠券等

#### 3.8.1 搭建工程并测试单线程

**工程依赖配置（pom.xml）**

```xml
<packaging>war</packaging>
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>5.2.7.RELEASE</version>
    </dependency>
    <!--实现分布式锁的工具类-->
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson</artifactId>
        <version>3.6.1</version>
    </dependency>
    <!--spring操作redis的工具类-->
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis</artifactId>
        <version>2.3.2.RELEASE</version>
    </dependency>
    <!--redis客户端-->
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>3.1.0</version>
    </dependency>
    <!--json解析工具-->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.9.8</version>
    </dependency>
</dependencies>
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.tomcat.maven</groupId>
            <artifactId>tomcat7-maven-plugin</artifactId>
            <configuration>
                <port>8080</port>
                <path>/</path>
            </configuration>
            <executions>
                <execution>
                    <!-- 打包完成后，运行服务 -->
                    <phase>package</phase>
                    <goals>
                        <goal>run</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**web.xml 配置**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         id="WebApp_ID" version="3.1">
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring/spring.xml</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

**spring.xml 配置**

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
    
    <!-- 扫描控制器包 -->
    <context:component-scan base-package="com.gvc.controller"/>
    
    <!-- 配置StringRedisTemplate（spring操作redis的模板类） -->
    <bean id="stringRedisTemplate"
          class="org.springframework.data.redis.core.StringRedisTemplate">
        <property name="connectionFactory" ref="connectionFactory"></property>
    </bean>
    
    <!-- 配置Redis连接工厂 -->
    <bean id="connectionFactory"
          class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
        <property name="hostName" value="192.168.44.128"></property>
        <property name="port" value="6379"/>
    </bean>
</beans>
```

**秒杀业务控制器（单线程版）**

```java
//  测试秒杀（单线程版）
@Controller
public class FlashSale {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/flashSale")
    //synchronized 只能解决一个Tomcat的并发问题，锁的是一个进程下的线程并发，如果分布式环境多个进程并发这种方案失效
    public @ResponseBody synchronized String flashSale(){
        //1.从redis获取数据：手机的库存数量
        int phoneCount = Integer.parseInt(stringRedisTemplate.opsForValue().get("phone"));
        //2. 判断手机的数量是否足够
        if(phoneCount > 0){
            phoneCount--;
            //库存减少后将数据保存回Redis
            stringRedisTemplate.opsForValue().set("phone", phoneCount + "");
            System.out.println("库存-1，当前库存： " + phoneCount);
        }else{
            System.out.println("库存不足!");
        }
        return "over!";
    }
}
```

![0108](D:\Program\MyNotes\notes_image\Redis\Redis0108.png)



#### 3.8.2 高并发测试

1. 启动两次工程，端口号分别为 8001 和 8002。
2. 使用 nginx 做负载均衡，修改 nginx.conf 配置：

```nginx
upstream sga{
    server 192.168.44.1:8001;
    server 192.168.44.1:8002;
    }
	
server {
    		listen 80;
    		server_name localhost;
    		#charset koi8-r;
    		#access_log logs/host.access.log main;
location / {
        proxy_pass http://sga;
    }
}
```

启动 nginx：

```bash
/usr/sbin/nginx -c /etc/nginx/nginx.conf
```

3. 使用 JMeter 模拟 1 秒内发出 400 个 http 请求，会发现同一个商品会被两台服务器同时抢购（分布式环境下 synchronized 锁失效，出现超卖问题）。

   ![0109](D:\Program\MyNotes\notes_image\Redis\Redis0109.png)



#### 3.8.3 实现分布式锁的思路

1. 因为 redis 是<font color="red">**单线程**</font>的，所以命令具备原子性，使用`setnx`命令实现锁：保存 k-v。如果 k 不存在，保存（当前线程加锁），执行完成后删除 k 表示释放锁；如果 k 已存在，阻塞线程执行，表示有锁。
2. 风险 **1**：如果加锁成功后，执行业务代码过程中出现异常，不执行后续代码，导致未删除 k（释放锁失败），会造成死锁。解决方案：为锁设置过期时间（如 10 秒后 redis 自动删除 k）。
3. 风险 **2**：高并发下，线程执行时间不同导致锁被误释放。例如：线程 1 执行需 13 秒，10 秒后锁自动过期；线程 2 加锁后执行 3 秒，线程 1 执行完进入 finally 块删除 k（误释放线程 2 的锁），导致锁永久失效。解决方案：给每个线程的锁设置唯一标识（如 UUID），释放锁时判断标识是否为当前线程的标识。
4. 风险 **3**：过期时间难以设定（10 秒太短可能不够用，60 秒太长浪费资源）。解决方案：开启定时器线程，当锁的剩余过期时间小于总过期时间的 1/3 时，自动延长过期时间（“续命” 机制）。
5. 结论：自己实现分布式锁复杂度高，推荐使用成熟的分布式锁框架（如 Redisson）。

![0110](D:\Program\MyNotes\notes_image\Redis\Redis0110.png)



#### 3.8.4 Redisson 实现分布式锁

Redis 是最流行的 NoSQL 数据库解决方案之一，而 Java 是世界上最流行的编程语言之一。虽然两者自然适配，但 Redis 没有对 Java 提供原生支持，需使用第三方库（如 Redisson）操作 Redis。Redisson 在`java.util`常用接口基础上，提供了一系列分布式特性的工具类，简化分布式锁实现。

**基于 Redisson 的秒杀控制器（分布式锁版）**	

```java
// @Description: 测试秒杀（Redisson分布式锁版）
@Controller
public class TestKill {
    @Autowired
    private Redisson redisson;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @RequestMapping("kill")
    public @ResponseBody String kill() {
        // 定义商品唯一标识（作为锁的key）
        String productKey = "HUAWEI-P40";
        // 通过Redisson获取分布式锁（底层集成setnx、过期时间等机制）
        RLock rLock = redisson.getLock(productKey); 
        // 上锁（设置过期时间为30秒，防止死锁）
        rLock.lock(30, TimeUnit.SECONDS);
        try{
            // 1.从redis中获取手机的库存数量
            int phoneCount = Integer.parseInt(stringRedisTemplate.opsForValue().get("phone"));
            // 2.判断手机库存是否足够秒杀
            if (phoneCount > 0) {
                phoneCount--;
                // 库存减少后，将新库存值保存回redis
                stringRedisTemplate.opsForValue().set("phone", phoneCount + "");
                System.out.println("库存-1，剩余:" + phoneCount);
            } else {
                System.out.println("库存不足!");
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            // 释放锁（必须在finally块中执行，确保锁一定被释放）
            rLock.unlock();
        }
        return "over!";
    }
    
    // 配置Redisson实例（单例）
    @Bean
    public Redisson redisson(){
        Config config = new Config();
        // 方式1：使用单个redis服务器
        config.useSingleServer().setAddress("redis://192.168.204.141:6379").setDatabase(0);
        
        // 方式2：使用redis集群（根据实际集群节点配置）
        // config.useClusterServers()
        //      .setScanInterval(2000)
        //      .addNodeAddress("redis://192.168.204.141:6379",
        //                      "redis://192.168.204.142:6379",
        //                      "redis://192.168.204.143:6379");
        return (Redisson) Redisson.create(config);
    }
}
```

**分布式锁方案对比**

实现分布式锁的方案有很多，例如 Zookeeper（特点：高可靠性）、Redis（特点：高性能）。

目前工业界应用最多的分布式锁方案仍是 Redis，因其高性能特性更适配高并发场景（如秒杀、抢购）。

