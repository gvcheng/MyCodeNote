# Linux光速入门



## 一、基础知识

### 1.1 Linux是什么？

Linux 是一种开源的免费使用的类 Unix 操作系统。它的内核于1991年由Linus Tovards首次发布，同时该作者也是Git分布式版本控制系统的作者。

![0101](https://github.com/gvcheng/note_images/blob/main/Linux_img/LinuxQuickStart_img/LinuxQuickStart0101.png)



### 1.2 Linux架构

- **硬件**：系统运行的物理基础。
- **内核**：操作系统核心，管理进程、内存、设备驱动、文件和网络系统。
- **Shell**：用户与内核的交互接口，命令解释器，支持 Shell 编程语言。
- **应用程序**：基于内核和 Shell 运行的各类软件。

![0102](https://github.com/gvcheng/note_images/blob/main/Linux_img/LinuxQuickStart_img/LinuxQuickStart0102.png)





## 二、Vim编辑器

**vi中有三种常用的模式：命令模式、插入模式、尾行模式**

![0103](https://github.com/gvcheng/note_images/blob/main/Linux_img/LinuxQuickStart_img/LinuxQuickStart0103.png)

### 2.1  **命令模式（Command Mode，默认模式）**

- **进入方式**：打开 `vi` 时默认进入该模式；从其他模式按 `Esc` 键返回。
- **核心功能**：用于执行光标移动、文本选择、删除、复制、粘贴等操作，不直接输入文本。
- 常用操作示例：
  - 光标移动：`h`（左）、`j`（下）、`k`（上）、`l`（右），或 `gg`（到文件首）、`G`（到文件尾）。
  - 文本操作：`dd`（删除当前行）、`yy`（复制当前行）、`p`（粘贴）、`x`（删除光标处字符）。
  - 切换模式：按 `i`（进入插入模式）、`:`（进入尾行模式）。

### 2. 2 **插入模式（Insert Mode）**

![0104](https://github.com/gvcheng/note_images/blob/main/Linux_img/LinuxQuickStart_img/LinuxQuickStart0104.png)

- **进入方式**：在命令模式下按 `i`（光标前插入）、`a`（光标后插入）、`o`（当前行下方新增一行并插入）等键。
- **核心功能**：直接输入和编辑文本，类似普通文本编辑器的编辑状态。
- **退出方式**：按 `Esc` 键返回命令模式。

### 2.3 **尾行模式（Ex Mode 或 Last Line Mode）**

- **进入方式**：在命令模式下按 `:` 键，光标会移至屏幕底部（尾行）并显示 `:` 提示符。
- **核心功能**：执行文件操作（保存、退出）、全局替换、搜索等高级命令。
- 常用操作示例：
  - 文件操作：`:w`（保存）、`:q`（退出）、`:wq`（保存并退出）、`:q!`（强制退出不保存）。
  - 搜索替换：`:1,10(行号) s/old/new`（替换当前行第一个 `old` 为 `new`）、`:%s/old/new/g`（全局替换所有 `old` 为 `new`）。
  - 跳转行：`:n`（跳转到第 `n` 行，如 `:5` 跳转到第 5 行）。
  - 撤销键：`u`(undo)
- **退出方式**：执行命令后自动返回命令模式；或按 `Esc` 键放弃命令并返回。





## 三、常用命令

### 3.1  文件与目录操作（最基础）

这是 Linux 操作的基石，几乎所有场景都会用到。

```markdown
1. ls ： 列出目录内容。
   - `ls -l`：显示详细信息（权限、大小、修改时间等）。
   - `ls -a`：显示隐藏文件（以 “.” 开头的文件）。

2. cd ： 切换目录。
   - `cd /home`：切换到`/home`目录（绝对路径）。
   - `cd ..`：回到上一级目录。
   - `cd ~`：回到当前用户的家目录。

3. pwd ： 显示当前所在目录的完整路径。

4. mkdir ： 创建新目录。
   - `mkdir test`：创建名为`test`的目录。
   - `mkdir -p a/b/c`：递归创建多级目录（无需手动先建 a 和 a/b）。

5. rm ： 删除文件或目录（慎用，删除后难恢复）。
   - `rm file.txt`：删除`file.txt`文件。
   - `rm -r test`：删除`test`目录及其下所有内容。
   - `rm -f file.txt`：强制删除，不提示确认。

6. cp ： 复制文件或目录。
   - `cp file1.txt file2.txt`：将`file1.txt`复制为`file2.txt`。
   - `cp -r dir1 dir2`：复制`dir1`目录到`dir2`（若`dir2`不存在则创建）。

7. mv ： 移动或重命名文件 / 目录。
   - `mv file.txt /home`：将`file.txt`移动到`/home`目录。
   - `mv oldname.txt newname.txt`：将文件重命名。
```



### 3.2  文件内容查看与编辑

用于查看日志、配置文件，或简单修改文本内容。

```markdown
1. cat ： 一次性显示整个文件内容（适合小文件）。
   - `cat file.txt`：直接输出`file.txt`的所有内容。
   - `cat -n file.txt`：显示内容并带行号。

2. more/less ： 分页查看大文件（避免内容刷屏）。
   - `more file.log`：按页显示，按`Enter`下一行，按`q`退出。
   - `less file.log`：功能更全，支持上下键滚动，按`q`退出。

3. head/tail ： 查看文件开头或结尾内容。
   - `head -10 file.txt`：查看文件前 10 行。
   - `tail -f log.txt`：实时跟踪文件末尾内容（常用于看日志实时输出）。

4. vi/vim ： Linux 自带的文本编辑器（功能强大，需简单学习）。
   - `vim file.txt`：用 vim 打开`file.txt`（若文件不存在则新建）。
   - 编辑模式：按`i`进入，可修改内容；按`Esc`退出编辑模式。
   - 保存退出：按`Esc`后，输入`:wq`（保存并退出）；输入`:q!`（不保存强制退出）。
```



### 3.3  系统管理与进程查看

用于监控系统状态、管理运行中的程序。

```markdown
1. top ： 实时查看系统资源占用和进程状态（类似 Windows 任务管理器）。
   - 按`P`：按 CPU 使用率排序。
   - 按`M`：按内存使用率排序。
   - 按`q`：退出 top 界面。
   
2. ps ： 查看当前用户的进程（静态快照）。
   - `ps aux`：查看系统中所有进程的详细信息（最常用）。
   - `ps aux | grep java`：筛选出包含 “java” 的进程（配合`grep`使用）。
   
3. kill ： 终止指定进程。
   - 先通过`ps aux`找到进程 ID（PID），再执行`kill PID`。
   - `kill -9 1234`：强制终止 PID 为 1234 的进程（当普通`kill`无效时用）。
   
4. df ： 查看磁盘空间使用情况。
   - `df -h`：以人类可读的格式（GB/MB）显示，更易理解。
   
5. free ： 查看内存使用情况。
   - `free -h`：同样以 GB/MB 格式显示，清晰查看已用、空闲内存。
```



### 3.4  用户与权限管理（重要）

Linux 是多用户系统，权限控制是核心安全机制。

```markdown
1. sudo ： 以管理员（root）权限执行命令（普通用户需知道自己的密码）。
   - `sudo apt install nginx`：以管理员权限安装 nginx 软件（Ubuntu/Debian 系统）。
   
2. su： 切换用户。
   - `su root`：切换到 root 用户（需输入 root 密码）。
   - `su - user1`：切换到 user1 用户，并加载其环境变量。
   
3. chmod ： 修改文件或目录的权限（读 r=4、写 w=2、执行 x=1）。
   - `chmod 755 file.sh`：所有者（u）有 rwx（7=4+2+1），组用户（g）和其他用户（o）有 rx（5=4+1）。
   - `chmod +x file.sh`：给所有用户添加执行权限（常用，让脚本可运行）。
   
4. chown ： 修改文件或目录的所有者和所属组。
   - `chown user1:group1 file.txt`：将 file.txt 的所有者改为 user1，所属组改为 group1。
```



### 3.5  其他高频实用命令

```markdown
1. ping ： 测试网络连通性。
   - `ping baidu.com`：测试是否能连接百度，按`Ctrl+C`停止。
   
2. wget ： 从网络上下载文件。
   - `wget https://example.com/file.zip`：直接下载指定 URL 的文件。
   
3. grep ： 在文件或输出中搜索指定字符串（强大的文本搜索工具）。
   - `grep "error" log.txt`：在 log.txt 中搜索包含 “error” 的行。
   - `ps aux | grep "python"`：结合管道符，搜索 python 相关的进程。
   
4. tar ： 打包压缩 / 解压文件（处理.tar、.tar.gz 等格式）。
   - 压缩：`tar -zcvf archive.tar.gz dir1/`：将 dir1 目录压缩为 archive.tar.gz。
   - 解压：`tar -zxvf archive.tar.gz`：将 archive.tar.gz 解压到当前目录。
```
























