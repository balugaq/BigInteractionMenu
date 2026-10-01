# BigInteractionMenu

[![JitPack](https://jitpack.io/v/balugaq/BigInteractionMenu.svg)](https://jitpack.io/#balugaq/BigInteractionMenu)

基于展示实体（Display Entities）的 Minecraft Paper 依赖库：在 3D 世界中渲染「箱子界面」风格的网格式即时交互菜单。

物品网格、视线悬停高亮、滚轮翻页、点击交互——不占用任何容器 GUI，玩家直接与摆在世界里的菜单交互。

> 写过 Slimefun 的 `BlockMenuPreset` / `ChestMenu`？API 是同款思路，秒上手。

## 特性

- **世界内 3D 网格菜单**：`ItemDisplay` 渲染物品 + `TextDisplay` 渲染标题与数量角标，观感与箱子 GUI 一致
- **视线悬停**：看向即高亮、离开即熄灭（自实现射线检测——Bukkit 的 rayTrace 无法命中 Display 实体）
- **滚轮翻页**：滑动物品栏即滚动网格，内置潜行保护与翻页上限
- **点击交互**：左右键 / 潜行判定，按槽位注册 `ClickHandler`
- **事件总线**：悬停进入/离开、点击、滚动、离格，全部可监听、可取消
- **六向朝向**：XY / XZ / YZ 平面及各自反转，按放置位置的 yaw/pitch 自动匹配
- **自动显隐**：玩家靠近才渲染、远离自动隐藏，缓解 FPS 压力

## 环境要求

- Java 21+
- Paper 1.20+（`api-version: 1.20`，在 1.21.11 上开发测试）

## 安装（服务器管理员）

BIM 以插件形式提供运行时：将构建产物（或从 Release/JitPack 下载的 jar）放入服务端 `plugins/` 目录。

使用 BIM API 的插件，请在 `plugin.yml` 中将 BIM 声明为前置：

```yaml
depend: [BigInteractionMenu]
```

## 开发者接入

### 方式一：JitPack（推荐）

```kotlin
// build.gradle.kts
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.balugaq:BigInteractionMenu:<tag>")
}
```

Maven：

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.balugaq</groupId>
    <artifactId>BigInteractionMenu</artifactId>
    <version>Tag</version>
    <scope>provided</scope>
</dependency>
```

`<tag>` 替换为 [Releases](https://github.com/balugaq/BigInteractionMenu/releases) 中最新的 git tag。

### 方式二：内置源码编译（composite build）

把本仓库作为子模块引入你的工程，随你的插件一起编译：

```kotlin
// settings.gradle.kts
includeBuild("BigInteractionMenu")

// build.gradle.kts
dependencies {
    compileOnly("io.github.balugaq:BigInteractionMenu:0.0.1")
}
```

### 方式三：本地 Jar

```kotlin
dependencies {
    compileOnly(files("libs/BigInteractionMenu-0.0.1.jar"))
}
```

> 无论哪种方式，开发期都以 `compileOnly` 引入——运行时由服务器上安装的 BIM 插件提供实现。

## 快速上手

**1. 定义一个网格预设**（继承 `ActiveGridPreset` 即可获得箱子风格的完整渲染与交互）：

```java
public final class ExampleMenu extends ActiveGridPreset {
    public static final ExampleMenu MENU = new ExampleMenu(); // 预设建议单例：构造即注册

    public ExampleMenu() {
        // identifier、高(height)、宽(width)；gap 省略时默认 0.12f
        super(new NamespacedKey("example", "menu"), 9, 9);
    }

    @Override
    public void init(ActiveGrid active, int idx, InteractUnit unit) {
        // 注意：此时 unit 尚未注册完毕，放初始数据用 setItemUnsafe，且放在 super.init 之前
        if (idx == 13) {
            active.setItemUnsafe(idx, new ItemStack(Material.DIAMOND, 64));
        }
        super.init(active, idx, unit); // 生成 物品 + 标题 + 数量角标 三件套并渲染当前物品

        // 按槽位注册点击处理；不注册则默认取消交互
        if (idx == 13) {
            active.clickHandlers.put(idx, dto -> {
                if (dto.isLeftClick()) {
                    dto.player().sendMessage("你点击了钻石！");
                }
            });
        }
    }

    @Override
    public int entriesSize() {
        return 81; // 用于限定滚动偏移上限（按行滚动）
    }

    @Override
    public void tick() {
        // 网格自己的 ticker，按 tickInterval() 节奏触发
    }

    @Override
    public int tickInterval() {
        return 20;
    }
}
```

**2. 放置网格并操作它**：

```java
Location loc = player.getLocation(); // location 的 yaw/pitch 决定平面与朝向，自动吸附最近的合法朝向
ExampleMenu.MENU.place(loc);

// 放置完成后随时更新物品（自动刷新渲染）
ActiveGrid grid = GridDataCache.activeGrids().get(BlockPos.from(loc));
grid.setItem(13, new ItemStack(Material.EMERALD, 3));
```

**3. 监听事件**（可选，全部可取消）：

```java
public class MyListener implements Listener {
    @EventHandler
    public void onClickUnit(PlayerInteractUnitEvent event) {
        ClickDTO dto = event.getDto();
        // event.setCancelled(true); // 取消本次交互
    }

    @EventHandler
    public void onScroll(PlayerScrollGridEvent event) { /* ... */ }

    @EventHandler
    public void onHover(PlayerOnHoverUnitEvent event) { /* ... */ }

    @EventHandler
    public void onOffHover(PlayerOffHoverUnitEvent event) { /* ... */ }

    @EventHandler
    public void onOffGrid(PlayerOffGridEvent event) { /* ... */ }
}
```

## API 概览

| 类 | 说明 |
|---|---|
| `GridPreset` | 网格预设（抽象）。定义尺寸、gap、渲染与交互钩子；构造即注册进注册表 |
| `ActiveGridPreset` | 开箱即用的箱子风格实现：三件套渲染、悬停高亮、滚轮翻页 |
| `ActiveGrid` | 一个已放置的网格实例：物品槽、点击处理器、`scrollOffset`、显隐包围盒 |
| `InteractUnit` | 单个可交互单元：`itemDisplay` / `titleDisplay` / `amountDisplay` |
| `ClickHandler` / `ClickDTO` | 点击回调与交互数据（左右键、潜行、玩家、被点物品） |
| `GridOrientation` | 网格平面与朝向（6 种常量），按 yaw/pitch 自动匹配 |
| `GridPresetRegistry` | 预设注册表（`GridPreset` 构造时自动注册，也可按 `NamespacedKey` 反查） |
| `GridDataCache` | 运行时缓存：`activeGrids`（按方块位置）/ `index`（实体→单元）/ `watching`（玩家→悬停单元） |
| `GridUtil` | `placeGrid` / `removeGrid` / `rayTraceUnit` / `formatAmount` 等工具与常量 |

需要更细粒度的控制（自绘单元格、自定义背景、非箱子风格交互），直接继承 `GridPreset` 实现抽象方法即可——`ActiveGridPreset` 只是一个参考实现。

## 网格朝向

放置网格时，根据 `Location` 的 yaw/pitch 自动匹配最接近的合法朝向：

| 朝向 | 平面 | 观察位置 |
|---|---|---|
| `XY` | 竖直 | 南侧（+Z，默认） |
| `XY_REVERSED` | 竖直 | 北侧（−Z），同一平面反转 180° |
| `XZ` | 水平 | 上方俯视 |
| `XZ_REVERSED` | 水平 | 下方仰视 |
| `YZ` | 竖直 | 东侧（+X） |
| `YZ_REVERSED` | 竖直 | 西侧（−X） |

## 交互机制

- **视线悬停**：Bukkit 的 `rayTrace` 不会命中 Display 实体，BIM 自实现「点到视线射线距离」检测。看向单元时 `onHover`（物品点亮、标题与数量浮现），离开时 `offHover`。
- **滚轮翻页**：以物品栏槽位变化（`PlayerItemHeldEvent`，含 0↔8 环绕）为滚动信号；潜行时默认忽略，防止误触；`entriesSize()` 约束翻页上限，`scrollOffset` 每步一行（= 网格宽度）。
- **点击**：`PlayerInteractEvent`（含点击空气）经射线命中单元后分发；未注册 `ClickHandler` 的槽位默认取消事件，避免与世界误交互。
- **自动显隐**：每 tick 检查显隐包围盒——10 格内出现玩家即显示（`onShow`），50 格内无人则隐藏（`onHide`），远处网格不渲染。

## 自己构建

```bash
./gradlew build        # 产物：build/libs/BigInteractionMenu-<version>.jar
./gradlew runServer    # 起本地 1.21.11 测试服（自动 eula + 拷贝插件，调试端口 5001）
```

## License

[MIT](LICENSE) © 2026 balugaq
