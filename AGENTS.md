# AGENTS.md

给在本仓库工作的 AI 代理（与人类协作者）的工程指南。

## TL;DR

- 本项目是 **Paper 依赖库**：用展示实体（ItemDisplay/TextDisplay）在 3D 世界渲染网格式即时交互菜单——Slimefun `BlockMenuPreset` 的世界内版。交付物 = 可运行的前置插件 + 可被其他插件 `compileOnly` 引用的 API（JitPack：`com.github.balugaq:BigInteractionMenu:<tag>`）。
- 改代码后必跑：`./gradlew compileJava`。
- 视觉/朝向/尺寸类改动只做**静态核对**，结论必须标注「待游戏内目视确认」；禁止自行开浏览器、截图或进游戏代验。

## 构建 / 运行

| 命令 | 用途 |
|---|---|
| `./gradlew compileJava` | 快速编译验证（首选） |
| `./gradlew build` | 完整构建，产物 `build/libs/BigInteractionMenu-<v>.jar`（shadowJar，relocate libby） |
| `./gradlew test` | 单测（JUnit5 + Mockito，目前基本为空） |
| `./gradlew runServer` | 起本地 1.21.11 测试服：自动写 eula、拷贝插件；服务目录取 `gradle.properties` 的 `server.run.dir`；5001 为调试端口 |

- Java 21（toolchain 强制）；所有源码/资源 UTF-8。
- Gradle daemon 偶发锁 `.gradle/.../fileHashes.lock`（拒绝访问）：先 `./gradlew --stop` 再编。

## 包结构

| 位置 | 职责 |
|---|---|
| `com.balugaq.bim.BIMMain` | 主类：注册 `MenuListener` + `GridTickTask`（每 tick）；libby 运行时依赖管理 |
| `com.balugaq.bim.grid` | 核心：预设/实例/交互/朝向/渲染 |
| `com.balugaq.bim.events` | 5 个自定义 Bukkit 事件 |
| `com.balugaq.bim.general` | 通用工具（`BlockPos`、`TransformationBuilder`） |

### grid/ 速查

| 类 | 职责 |
|---|---|
| `GridPreset` | 抽象预设（`identifier`/`height`/`width`/`gap`），定义 `init`/`onHover`/`offHover`/`updateDisplayItem`/`tick` 等钩子；**构造即注册**进 `GridPresetRegistry` |
| `ActiveGridPreset` | 开箱即用的箱子风格实现：三件套渲染、悬停高亮、滚轮翻页、潜行保护 |
| `ActiveGrid` | 已放置实例：`items`、`clickHandlers`、`scrollOffset`、显隐包围盒 |
| `InteractUnit` | 单元格三件套（itemDisplay/titleDisplay/amountDisplay），`idx = h*width + w` |
| `GridOrientation` | 朝向系统（6 常量），`fromYawPitch(yaw, pitch)` 球面角距离自动匹配 |
| `GridUtil` | `placeGrid`/`removeGrid`/`rayTraceUnit`/`formatAmount` + 亮度/透明度常量 |
| `GridTickTask` | 每 tick：`hoverCheck`（视线悬停）+ `gridCheck`（显隐） |
| `MenuListener` | PlayerInteractEvent / PlayerItemHeldEvent 分发；玩家退出/死亡清理 |
| `ClickDTO` / `ClickHandler` / `ScrollResult` | 交互数据与回调 DTO |

## 数据流

- **放置**：`GridPreset.place(loc)` → `GridUtil.placeGrid`：双层循环生成 `InteractUnit`（idx = h*width+w）→ `option.init(...)` 生成三件套 → 写入 `GridDataCache`（`activeGrids` 按 `BlockPos`、`index` 按实体、`watching` 按玩家 UUID）→ `defaultBackground()` 时 `addDefaultBackground` 画背景板与分隔条。
- **每刻**：`GridTickTask` → ① 视线命中变化触发 `onHover`/`offHover` 及 PlayerOn/OffHoverUnitEvent、PlayerOffGridEvent；网格被悬停期间按 `tickInterval()` 驱动 `tick()`。② 显隐包围盒检查 → `onShow`/`onHide`。
- **点击**：`PlayerInteractEvent`（主手、含点空气）→ rayTrace 命中单元 → `PlayerInteractUnitEvent`（可取消）→ 该槽 `ClickHandler`（未注册默认 `ClickHandler.cancel`）。
- **滚轮**：`PlayerItemHeldEvent` 槽位差 → `PlayerScrollGridEvent`（可取消）→ `GridPreset.onScroll`（潜行默认忽略）→ `updateDisplayItem` 刷新。
- **关服**：`onDisable` → 各网格 `onDestroy` 清实体。

## 关键不变量（改渲染/交互前必读）

1. **一切位置偏移走 `GridOrientation.apply(base, a, b, c)`**：a 沿宽度、b 沿网格上方、c 沿深度（观察者方向）。禁止裸 `add(x, y, z)`——那是把 XY 平面写死。
2. 固定朝向实体（背景板/分隔条）spawn 后必须 `setRotation(o.getYaw(), o.getPitch())`；billboard 竖直平面用 `FIXED`，水平面（XZ 系）用 `CENTER`（`VERTICAL` 会侧对俯视玩家）。
3. `text_opacity` 只用 `GridUtil.TEXT_OPACITY_HIDDEN(0)` / `TEXT_OPACITY_SHOWN(255)`。**1~26 渲染为不可见**（4~26 被客户端着色器按 alpha<0.1 丢弃），别用中间值。
4. **字体像素模型**（TextDisplay 背景定尺寸的唯一正解）：实体变换缩放为 s 时，1 字体像素 = s/40 格；行高 10px = s/4 格（与字形无关）；背景宽 = 最宽行文本宽 + 1px（固定内边距）；`setLineWidth` 只是**换行阈值**，不会撑宽背景。`"."` 字宽 1px，中文 9px。宽度用首行 "." 数铺（−1 抵消内边距），高度用行数铺。
5. 单个 TextDisplay 的文本 **≤65535 字节**，超大背景必须拆成多个实体分块。
6. ItemDisplay 的 GUI 物品模型基准：rotY(180°) 后正面朝 +Z；其他朝向左乘 `o.getItemRotation()`。
7. 悬停检测是自实现射线（Bukkit rayTrace 不命中 Display）：点到射线垂直距离阈值 0.12，见 `GridUtil.rayTraceEntity`。

## 已知坑

- `GridPreset.init` 内**不要调 `active.setItem(...)`**——会 NPE（此时 unit 尚未注册进 units）。用 `setItemUnsafe`，且放在 `super.init` **之前**（super.init 读取并渲染当前物品）。`placeGrid` 完成后才能正常用 `setItem`。
- `InteractUnit` 手写了 `toString`：`@Data` 生成的 toString 会沿 grid→units→unit 循环引用爆 `StackOverflowError`。新增字段时保持手写。
- `GridPreset` **构造即注册**：同 identifier 重复 new 会覆盖注册表，预设应做成单例。
- `BlockPos` 用 `WeakReference<World>` 持世界（防内存泄漏），改这里前想清楚。
- `placeGrid` 不清理同 `BlockPos` 的旧网格，重复 place 会实体堆积；调用方应先 `GridUtil.removeGrid(BlockPos)`。

## 发布

- JitPack：`jitpack.yml` 指定 openjdk21。打 git tag 后自动构建，坐标 `com.github.balugaq:BigInteractionMenu:<tag>`。
- Maven Central 链路已配置（maven-publish + signing + central portal），需 `SIGNING_KEY`/`SIGNING_PASSWORD` 环境变量；pom 的 `description` 勿留模板占位。

## 红线

- 不做 git 提交/推送/历史改写——提交权归主人，工作区留到可通过验收即可，并给出建议的提交信息。
- 改动落地后：编译通过 + grep 复查确认落地，结论如实上报；视觉类一律注明「待游戏内目视确认」，不得把静态结论当视觉验收结论。
