# Claude Code快速入门与配置

## 一、环境准备

### 系统要求

- **操作系统**：[Linux](https:) (Ubuntu 18.04+, CentOS 7+)、macOS 10.15+、Windows 10+
- **网络连接**：稳定的互联网连接
- **存储空间**：至少 500MB 可用磁盘空间

### 安装 Node.js

在开始安装前，请先检查您的 Node.js 版本：

```bash
node --version
npm --version
```

- 下载地址：https://nodejs.org/zh-cn/download/
- 安装文档：https://help.aliyun.com/zh/sdk/developer-reference/install-node-js-in-windows

### 安装 Git

在开始安装前，请先检查您的 git 版本：

```bash
git --version
```

- 下载地址：https://git-scm.com/downloads

- 安装文档：https://git-scm.com/book/zh/v2/起步-安装-Git

### Linux/macOS 安装

```bash
# 全局安装 Claude Code
sudo npm install -g @anthropic-ai/claude-code

# 验证安装
claude --version
```

### Windows 安装

```bash
# 以管理员身份打开 Powershell 或命令提示符

# 全局安装
npm install -g @anthropic-ai/claude-code

# 验证安装
claude --version
```



### 配置代理网络和 Claude API Key

![0101](D:\Program\MyNotes\notes_image\ClaudeCode\ClaudeCode0101.png)

#### 1、在当前项目目录下创建.claude/settings.json

```json
{
  "env": {
    "HTTP_PROXY": "http://127.0.0.1:7890",
    "HTTPS_PROXY": "http://127.0.0.1:7890"
  }
}
```

#### 2、配置 API Key 环境变量

```bash
setx ANTHROPIC_API_KEY "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
setx CLAUDE_CODE_GIT_BASH_PATH "D:\software\git\git-2.36.1_x64\bin\bash.exe"
```





## 二、控制台启动与基础操作

###  控制台启动 claude code

终端输入：`claude`，之后需要登录 claude 官网授权

![0101](D:\Program\MyNotes\notes_image\ClaudeCode\ClaudeCode0101.png)



### 2.1 基础操作：命令与配置是起点

#### 1. 吃透帮助命令，解锁所有可能

想快速上手？从 `claude --help` 开始。这个命令会列出所有可用参数和命令，比如 `-p` 用于非交互式输出、`-c` 继续最近的对话、`--model` 指定模型等。记住常用参数能让操作效率翻倍，比如用 `claude -r` 恢复历史会话，或 `--output-format json` 导出结构化结果。

| 命令          | 解释       | 适用场景                             |
| ------------- | ---------- | ------------------------------------ |
| `/clear`      | 清空上下文 | 切换需求时，或需要重新开始           |
| `/compact`    | 压缩对话   | 要保持对话，但不希望建立过强的记忆时 |
| `/exit`       | 退出       | 用完 CC 时                           |
| `/forget`     | 单条遗忘   | 让 CC 不继承，AP 用户可控制          |
| `/AgentAgain` | 仅输出     | 切换使用体验                         |
| `/model`      | 模型配置   | 200K 对话使用 gpt-4o 等              |
| `/stats`      | 状态       | 查看当前 CC 的状态                   |
| `/doctor`     | 检测       | 检测 CC 的安装状态                   |

![0102](D:\Program\MyNotes\notes_image\ClaudeCode\ClaudeCode0102.png)

#### 2. 与 IDE 无缝集成：编码不切换窗口

在 VS Code、Cursor 等IDE中使用 Claude Code，只需两步：

- 安装插件：搜索 “Claude Code”，认准Anthropic，点击安装后即可使用。

![0103](D:\Program\MyNotes\notes_image\ClaudeCode\ClaudeCode0103.png)



### 2.2 核心模式：按场景切换，效率拉满

#### （1）自助编辑模式：免确认批量操作

适合高频需求的点对点修改场景，按下`shift+K`可一次写好代码，此时 Claude 会自动识别代码漏洞，无需手动输入，比如提示 “创建一个酷炫的 todo-list 应用”，它会直接生成文件并提交，省去反复确认的时间。

![0104]()

#### （2）Plan 模式：前期规划神器

面对项目重构或复杂问题，按下`shift+K`再选择 Plan 模式，它会快速为你梳理方案。比如提问 “重构我的前端项目 todoist”，会自动梳理技术栈、页面结构、解决方案等，确认后再执行。若不满意可直接说 “ 重新规划 ”，直到符合预期。

![0105]()

#### （3）Yolo 模式：全权限放手干

重构代码、启动新项目或修复复杂 bug 时，用 `claude --dangerously-skip-permissions` 进入 Yolo 模式。此时 Claude 拥有更高权限，可直接执行更多操作（需注意安全，建议在沙箱环境使用）。进入后仍能用 `Shift+Tab` 调整模式，灵活切换权限粒度。

```bash
claude --dangerously-skip-permissions
```



## 三、CLAUDE.md：全局记忆的核心

和聊天机器人交流时，我们知道 “系统提示词” 很重要，会持续影响 AI 的行为。那么 CC 中，CLAUDE.md 也是类似的地位。一个典型的工作流是：

> 建初始 CLAUDE.md → 对话直到长度接近溢出，运行 /compact 续命 → 达到里程碑时要求 CC 根据进度更新 CLAUDE.md → 循环直到结束

可以看到 CLAUDE.md 就是一个持续发挥作用的全局变量。而且 CC 往里写入时一般做了充分的缩略，所以可读性很好。

### CLAUDE.md 注意事项

- 文件不要太长，毕竟 CC 会默认读入这个文件
- 会话时为了省事，说 claude.md 时 CC 也可以懂
- 文件里适合放提醒事项，比如 “要求 CC 每次宣布成功时都要带上证据文件链接”，以及 “代理服务端口是 9890”。然后会话时，可以要求 CC“查询 claude.md 相关部分”。
- 官方的 "#" 进文档据 GPT 说有个 bug 不稳定。



## 四、会话管理：避免失控，高效推进

### 1. 随时暂停与回滚

- 工作中按 `Esc` 可暂停当前操作，比如发现 Claude 安装依赖超时、思路跑偏时，及时中断能减少无效操作。
- 按 `Esc` 两次可回退到历史对话节点（注意无 redo 功能，回退前确认）。
- 代码不满意？直接说 “回滚到上次的代码”，Claude 会自动恢复之前版本。

### 2. 应对历史溢出

当会话提示 “Context left until auto-compact: 3%”，说明历史记录快满了。此时会自动触发压缩（约 150 秒），也可手动用 `/compact` 命令续命，避免对话中断。

```
Context left until auto-compact: 3%
```

### 3. 恢复与查看历史

- 用 `claude -c` 直接进入上次对话；
- 用 `claude -r` 选择历史会话恢复，适合中途退出后继续工作。



## 五、资源监控与批量任务：把控节奏不浪费

### 1. 实时监控 token 用量

想知道每天 / 每小时消耗多少资源？运行 `npx ccusage@latest` 查看按天用量，或 `npx ccusage blocks --live` 实时监控消耗速度。若速度过快，可手动处理 `git commit` 等费 token 的操作，避免超额。

```bash
npx ccusage@latest
```

### 2. 批量任务高效处理

需要执行几十个重复任务（比如批量生成文档章节）用脚本式用法：

1. 把任务按行写入 `TASK.md`（一行一个任务）；
2. 运行命令：

```bash
cat TASK.md | while IFS= read -r line; do echo $line; claude -p "$line" --debug; done
```

可加 `timeout` 防止单个任务卡死，同时用 `--allowedTools "Edit"` 限制权限，避免意外操作。注意不要并发执行，否则可能触发限额封禁。









