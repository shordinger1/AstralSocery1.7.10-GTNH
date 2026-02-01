# Astral Sorcery 1.7.10 迁移 - 后续开发指南

## 项目概述

你正在进行 Astral Sorcery 模组从 1.12.2 到 1.7.10 的迁移工作。这是一项复杂的 Minecraft Forge 模组降级任务，需要深入理解两个版本的 API 差异。

### 当前项目状态 (更新于 2026-01-30)
- **总体进度**: 约 38% (更准确的评估)
- **配方系统**: 70% 完成 (38个配方已注册，~52个缺失)
- **Entity 系统**: 100% 完成 (16/16)
- **TileEntity 基础**: 60% 完成 (11个基类，核心逻辑大量TODO)
- **Block 注册**: 85% 完成 (32个方块注册，部分为stub)
- **Item 注册**: 80% 完成 (26个物品注册，部分功能缺失)
- **工具类**: 100% 完成 (15/15)
- **网络层**: 100% 完成 (NetworkWrapper + 3种包类型)
- **粒子效果系统**: 100% 完成 (8个类框架)
- **渲染系统**: 40% 完成 (TESR框架存在，具体实现缺失)
- **GUI系统**: 20% 完成 (仅NEI配方查看器)
- **世界生成**: 10% 完成 (框架存在，无生成器注册)
- **待实现系统**: 约 62%

---

## 下一阶段开发优先级 (2026-01-30更新)

### 🔴 P0 - 核心游戏玩法阻塞 (必须立即完成)

#### 1. 完成 TileAltar 实现
**文件**: `common/tile/TileAltar.java`
**状态**: SKELETON (~50个TODO)
**依赖**: 无

**需要实现**:
- [ ] 取消注释并修复结构匹配逻辑 (Lines 222, 592, 663, 722)
- [ ] 实现配方合成系统集成 (Lines 236, 362, 478, 519, 567, 689, 735, 783, 820)
- [ ] 添加星光交互逻辑 (Lines 850, 869, 885, 894, 912, 924)
- [ ] 实现客户端渲染效果 (Lines 289, 351)
- [ ] 添加声音系统 (Line 304)
- [ ] 实现星座聚焦 (Lines 319, 330, 343)

**测试验证**:
- [ ] 放置祭坛后能看到渲染
- [ ] 能放入物品进行合成
- [ ] 配方能正常匹配和执行
- [ ] 星光消耗正常
- [ ] NEI能显示配方

#### 2. 实现基础星座系统
**文件**: `common/constellation/`
**状态**: 接口存在，实现不完整
**依赖**: 无

**需要实现**:
- [ ] 完善 IConstellation 接口实现
- [ ] 添加星座发现逻辑 (ConstellationPaper功能)
- [ ] 实现天空星座计算 (ConstellationSkyHandler)
- [ ] 添加星座效果系统
- [ ] 创建星座GUI/屏幕

**测试验证**:
- [ ] 能通过Constellation Paper发现星座
- [ ] 星座在天空正确显示
- [ ] 星座能影响配方和设备

#### 3. 创建缺失的 TileEntities
**文件**: `common/tile/`
**状态**: 框架存在，具体实现缺失

**优先级顺序**:
1. **TileCollectorCrystal** - 星光网络核心
2. **TileCrystalLens** - 星光传输
3. **TileWell连接** - 修复BlockWell的createNewTileEntity
4. **TileObservatory** - 星座研究
5. **TileRitualPedestal** - 仪式系统
6. **TileBore** - 钻孔机

**每个TileEntity需要**:
- [ ] 继承正确的基类 (AstralBaseTileEntity或TileReceiverBaseInventory)
- [ ] 实现NBT读写
- [ ] 实现客户端同步 (PacketTileUpdate)
- [ ] 注册到RegistryTileEntities
- [ ] 创建对应的TESR (如果需要渲染)

### 🟡 P1 - 核心功能 (高优先级)

#### 4. 星光网络基础
**文件**: `common/block/network/` (需要创建)
**状态**: 未实现

**需要实现**:
- [ ] BlockStarlightNetwork 基类
- [ ] ITransmissionReceiver 接口
- [ ] 基础传输逻辑
- [ ] 网络可视化 (光束渲染)
- [ ] 网络GUI (可选)

**测试验证**:
- [ ] 收集水晶能收集星光
- [ ] 透镜能传输星光
- [ ] 能看到星光传输光束

#### 5. 世界生成
**文件**: `common/world/RegistryWorldGenerators.java`
**状态**: 框架存在，无生成器注册

**需要实现**:
- [ ] Rock Crystal矿石生成器 (Overworld)
- [ ] Marble大矿脉生成
- [ ] Celestial Crystal稀有矿石
- [ ] Starmetal矿石 (如果需要)
- [ ] 基础结构 (古代神祠等)

**配置参数**:
```java
// 参考 AstralBaseWorldGenerator 中的配置
ORE_ULTRA_RARE = 2   // 极稀有: 每2个chunk 1个矿脉
ORE_RARE = 4         // 稀有: 每4个chunk 1个矿脉
ORE_COMMON = 8       // 普通: 每8个chunk 1个矿脉
```

#### 6. GUI系统
**文件**: `client/gui/`
**状态**: 仅NEI查看器存在

**需要实现**:
1. **Altar GUI** (最高优先级)
   - 显示配方槽位
   - 显示星光进度
   - 显示配方输出
   - 支持物品交互

2. **Well GUI**
   - 液体槽位
   - 灌注操作

3. **Sextant GUI**
   - 星座查找器
   - 方向指示器

### 🟢 P2 - 内容扩展 (中优先级)

#### 7. 工具功能实现
**文件**: `common/item/wand/`, `common/item/tool/`

**需要实现**:
- [ ] ItemWand的核心功能 (增强、粒子、HUD、充能)
- [ ] ItemArchitectWand的方块放置逻辑
- [ ] ItemExchangeWand的方块交换逻辑
- [ ] ItemGrappleWand的抓钩逻辑
- [ ] ItemIlluminationWand的光放置逻辑
- [ ] ItemSextant的查找系统 (GUI、方向指示、结构搜索、粒子轨迹)
- [ ] 水晶工具的特殊能力

#### 8. 高级祭坛系统
**文件**: `common/tile/TileAttunementAltar.java`, `TileConstellationAltar.java`

**需要实现**:
- [ ] Attunement Altar功能
- [ ] Constellation Altar功能
- [ ] 升级配方 (Discovery→Attunement→Constellation→Trait)
- [ ] 结构验证逻辑

#### 9. 高级方块
**文件**: `common/block/`

**需要实现**:
- [ ] Observatory功能
- [ ] Bore系统 (Liquid Bore, Vortex Bore)
- [ ] Ritual系统 (Pedestal + Link)
- [ ] Celestial Gateway
- [ ] Tree Beacon
- [ ] Starlight Infuser

### 🔵 P3 - 优化和扩展 (低优先级)

#### 10. 高级渲染和特效
- [ ] 水晶实体渲染优化
- [ ] 星光网络光束渲染
- [ ] 粒子特效完善
- [ ] 工具交互声音

#### 11. 其他缺失配方
- [ ] 添加RECIPE_ANALYSIS.md中剩余的~52个配方
- [ ] 特殊机器配方 (Telescope, Grindstone, Prism等)

---

## 开发工作流 (更新)

### 推荐开发顺序

**阶段1: 核心玩法** (1-2周)
```
1. TileAltar实现 → 2. 基础星座系统 → 3. 核心TileEntities
   ↓
测试: 基础祭坛合成流程可用
```

**阶段2: 星光网络** (1周)
```
4. 星光网络基础 → 5. 收集水晶和透镜 → 6. 网络可视化
   ↓
测试: 星光能收集和传输
```

**阶段3: 世界生成** (3-5天)
```
7. 矿石生成器 → 8. Marble生成 → 9. 基础结构
   ↓
测试: 世界中能找到矿石和Marble
```

**阶段4: GUI和交互** (1周)
```
10. Altar GUI → 11. Well GUI → 12. Sextant GUI
    ↓
测试: 所有设备都有GUI
```

**阶段5: 工具和高级功能** (1-2周)
```
13. 工具实现 → 14. 高级祭坛 → 15. 高级方块
    ↓
测试: 完整游戏循环可用
```

**阶段6: 内容完善** (持续)
```
16. 剩余配方 → 17. 优化和特效 → 18. 平衡性调整
```

---

## 重要提醒

### 开发前检查清单
- [ ] 阅读本文档
- [ ] 阅读DEVELOPMENT_GUIDE.md (CLAUDE.md)
- [ ] 阅读CODEBASE_STATUS_2026-01-30.md了解当前状态
- [ ] 检查依赖是否满足
- [ ] 查看original目录中的原版实现
- [ ] 确认使用1.7.10 API
- [ ] 确认使用util.math库

### 代码质量检查
- [ ] 编译通过 (`gradlew compileJava`)
- [ ] 使用1.7.10 API (无1.12.2特性)
- [ ] 继承正确基类
- [ ] 添加@Override注解
- [ ] 添加必要的TODO注释
- [ ] 不盲目实现，参考库代码

### 测试验证
- [ ] 功能正常运行
- [ ] 客户端和服务器都正常
- [ ] NEI能显示配方
- [ ] 无致命错误
- [ ] 性能可接受

---

## 常见问题 (更新)

### Q: 为什么总体进度从45%降到38%?
A: 之前是粗略估计，现在基于详细的代码库分析。实际上虽然配方系统进展很大，但核心游戏玩法系统(Altar、星座、星光网络)仍大量TODO，需要真实评估。

### Q: 为什么先完成TileAltar而不是世界生成?
A: 因为祭坛是核心玩法，没有它就无法测试配方。世界生成虽然重要，但可以先用创造模式获取物品进行开发。

### Q: 配方系统完成了70%，为什么还有这么多缺失?
A: 已注册的38个配方都是高优先级的，剩下的~52个主要是：
- 升级配方 (3个)
- 特殊机器配方 (10个)
- 高级变体配方 (39个)
这些依赖尚未实现的方块/物品，需要分批实现。

### Q: 如何判断某个系统可以开始实现?
A: 检查以下条件：
1. 基类已实现 (如AstralBaseTileEntity)
2. 依赖的系统已就绪 (如星座系统依赖星空计算)
3. 注册框架已存在 (如RegistryTileEntities)
4. 参考代码已找到 (original + 1.7.10库)

---

**文档版本**: 2.0
**创建日期**: 2026-01-30
**最后更新**: 2026-01-30
**适用项目**: Astral Sorcery 1.7.10 迁移
**状态**: ✅ 活跃维护

**重要提醒**：
- 🔴 始终优先参考库中的现有代码
- 🔴 没找到可借鉴代码时跳过，不要盲目修改
- 🔴 使用TODO注释标注未实现功能
- 🔴 每次修改后必须编译测试
- 🟢 新增: 优先完成P0核心游戏玩法阻塞
- 🟢 新增: 参考CODEBASE_STATUS_2026-01-30.md了解详细状态

**祝开发顺利！** 🚀

---

## 核心工作流程

### 第一步：准备工作

在开始任何迁移工作前，必须：

1. **阅读 DEVELOPMENT_GUIDE.md**
   - 查找相关组件的本地化名称
   - 了解组件的继承关系
   - 查看注册流程示例
   - 确认基类和接口

2. **查看深度分析报告**
   - 位置: `D:\GTNH\AstraSorcery\深度分析\`
   - 查找对应组件的详细分析
   - 理解组件的设计意图
   - 了解与其他组件的依赖关系

3. **检查 original 目录**
   - 位置: `D:\GTNH\AstraSorcery\original\`
   - 对比 1.12.2 版本的原始实现
   - 确保功能正确迁移

---

## 迁移方法论（来自 compile_fix.txt）

### 项目路径
```
工作目录: D:\GTNH\AstraSorcery\AstralSorcery1.7.10\src
库文件:   D:\GTNH\AstraSorcery\MinecraftLib\
原始代码: D:\GTNH\AstraSorcery\AstralSorcery1.12.2\src
参考项目: D:\GTNH\AstraSorcery\Twist-Space-Technology-Mod\src\main\java
```

### 核心原则

#### 1. 最小化修复原则
所有非Java代码必须存放在 `generate` 文件夹下。
修复时必须按优先级查找相关代码：

**优先级顺序：**
1. ✅ **搜索1.7.10库中的类似代码**（版本差异导致签名改变）
2. ✅ **搜索参考项目 TST**（类似功能实现）
3. ⚠️ **从1.12.2库中迁移**（需要做兼容性处理）
4. ❌ **自己实现**（仅当以上都不可行时）

#### 2. 类迁移规则（从1.12.2迁移时）
**重要检查清单：**
- [ ] 类名匹配：1.7.10中是否有同名类？
- [ ] 如果有：**不要迁移**，使用1.7.10的类
- [ ] 如果没有：迁移到 `migration` 文件夹
- [ ] 更改 package 签名
- [ ] 批量修改所有 import 路径

#### 3. 错误分类处理

**类型A：重载错误**
```
原因：方法签名在不同版本中改变
解决方案：
1. 搜索1.7.10版本的类似功能
2. 修改方法签名与内部实现
3. 不要从1.12.2迁移
```

**类型B：类无法识别**
```
原因：类不存在或包路径改变
解决方案：
1. 搜索 MinecraftLib\1.12.2 中的代码
2. 检查 1.7.10 是否有同名类
3. 如果没有，迁移到 migration 文件夹
4. 批量修改 import 路径
```

**类型C：函数调用错误**
```
原因：方法不存在或签名改变
解决方案：
1. 搜索这个函数在1.7.10中的存在
2. 如果存在：修改调用代码
3. 如果不存在：实现兼容1.7.10的版本
```

**类型D：批量类似错误**
```
解决方案：
1. 编写 Python 批处理脚本
2. 批量处理所有类似问题
3. 测试确保没有遗漏
```

#### 4. 🔴 关键安全规则

**必须遵守：**
1. ✅ 优先查看提供的所有库中是否有相似代码可以借鉴
2. ✅ 借鉴 = 类似类中有函数签名相似或功能相似的函数
3. ✅ 必须比较两个类文件后才能修改
4. ❌ 如果没找到可借鉴的代码，**跳过这段**
5. ❌ 不要盲目修改代码

**错误示例：**
```java
// ❌ 错误：直接使用不存在的类
import net.minecraft.network.packet.Packet132TileEntityData;

// ✅ 正确：搜索1.7.10中对应的类
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
```

---

## 组件分析框架（来自 分析.txt）

### 分析任务：设计链路分析

对于每个组件（Item, Block, Entity, TileEntity等），需要分析：

#### 1. 类定义分析
- [ ] 基类在1.7.10中的 package name
- [ ] 继承层次结构
- [ ] 实现的接口

#### 2. 注册阶段分析
- [ ] 注册操作是什么
- [ ] 应该在什么代码中进行注册
- [ ] 注册时机（预初始化/初始化/后初始化）

#### 3. 资源注册分析
- [ ] 外部名称/注册名称
- [ ] 贴图资源
- [ ] I18N 本地化
- [ ] 其他属性注册

### 参考代码库
```
MinecraftLib/TST           - Twist Space Technology (参考实现)
MinecraftLib/gt5            - GregTech 5 (参考实现)
MinecraftLib/1.7.10         - Minecraft 1.7.10源码
MinecraftLib/modularui      - Modular UI (参考实现)
```

### 输出要求
为每个组件创建：
1. ✅ 设计链路指导文档
2. ✅ 参考基类（如 `BaseBlock extends Block`）
3. ✅ 包含所有可 override 的函数
4. ✅ 注册流程示例
5. ✅ 资源绑定示例

---

## 深度分析框架（来自 深度分析.txt）

### 分析任务：具体实现分析

对于每个游戏实体，需要：

#### 1. 注册分析
- [ ] 注册阶段操作
- [ ] 注册代码位置
- [ ] 注册参数说明

#### 2. 资源分析
- [ ] 注册名称/外部名称
- [ ] 贴图路径和绑定
- [ ] 本地化键
- [ ] 其他属性

#### 3. 代码行级分析
**对每个函数和属性：**
- [ ] 逐行抽象功能分析
- [ ] ❌ 不涉及具体代码实现
- [ ] ✅ 描述逻辑意图
- [ ] ✅ 标注关键算法

### 分析流程
1. 列出分析计划
2. 完善计划
3. 检索所有相关代码库
4. 生成分析报告（每个组件单独文件夹）
5. 创建 forget.txt（列出未分析的文件）

---

## 迁移执行步骤（来自 迁移.txt）

### Block组件迁移示例流程

#### 第一步：参考深度分析
1. 阅读 `深度分析` 文件夹根目录的文件
2. 查看 `深度分析\Block` 中的所有方块分析
3. 理解每个方块的设计意图

#### 第二步：执行迁移
1. **使用1.7.10的API**
   ```java
   // 继承正确的基类
   public class YourBlock extends AstralBaseBlock {
       // 使用1.7.10的方法签名
   }
   ```

2. **使用正确的数学库**
   ```java
   // ✅ 正确：使用util.math下的库
   import hellfirepvp.astralsorcery.common.util.math.BlockPos;
   import hellfirepvp.astralsorcery.common.util.math.Vec3d;

   // ❌ 错误：使用原生类
   import net.minecraft.util.BlockPos; // 1.7.10没有这个
   ```

3. **处理无法实现的功能**
   ```java
   // ✅ 使用TODO注释标记
   // TODO: Re-enable after FeatureX is migrated
   // original code: someComplexFeature();
   // Placeholder implementation:
   placeholderLogic();
   ```

#### 第三步：对比验证
1. 阅读 `D:\GTNH\AstraSorcery\original` 中的高版本实现
2. 对比功能是否正确实现
3. 如果没有实现，添加注释而不是自己写

---

## 核心API差异速查

### Entity系统

| 概念 | 1.12.2 | 1.7.10 |
|------|--------|--------|
| 实体数据 | EntityDataManager | DataWatcher |
| 获取乘客 | getPassengers() | riddenByEntity |
| 坐标系统 | BlockPos | utilmath.BlockPos |
| 位置向量 | Vec3d | utilmath.Vec3d |

```java
// 1.12.2
this.dataManager.set(FLAG, true);
List<Entity> passengers = this.getPassengers();

// 1.7.10
this.getDataWatcher().updateObject(FLAG, 1);
Entity passenger = this.riddenByEntity;
```

### TileEntity系统

| 概念 | 1.12.2 | 1.7.10 |
|------|--------|--------|
| 物品处理 | Capability系统 | IInventory接口 |
| 空物品 | ItemStack.EMPTY | null |
| 物品数量 | stack.getCount() | stack.stackSize |
| 坐标 | getPos() | xCoord, yCoord, zCoord |
| 网络包 | S35PacketUpdateTileEntity | S35PacketUpdateTileEntity (相同) |

```java
// 1.12.2
ItemStack stack = ItemStack.EMPTY;
int count = stack.getCount();
BlockPos pos = this.getPos();

// 1.7.10
ItemStack stack = null;  // 或检查 stack == null
int count = stack.stackSize;
int x = this.xCoord;
```

### 渲染系统

| 概念 | 1.12.2 | 1.7.10 |
|------|--------|--------|
| 顶点构建 | BufferBuilder | Tessellator |
| 顶点添加 | pos(x,y,z).tex(u,v).endVertex() | addVertexWithUV(x, y, z, u, v) |
| FML注解 | net.minecraftforge.fml | cpw.mods.fml |
| @SideOnly | 相同 | 相同 |

```java
// 1.12.2
BufferBuilder bb = Tessellator.getInstance().getBuffer();
bb.pos(x, y, z).tex(u, v).endVertex();

// 1.7.10
Tessellator tess = Tessellator.instance;
tess.addVertexWithUV(x, y, z, u, v);
```

### 注册系统

```java
// 1.12.2
GameRegistry.registerTileEntity(TileClass.class, MODID + ":name");

// 1.7.10
GameRegistry.registerTileEntity(TileClass.class, MODID + ":name");
// 相同，但需要在正确时机调用
```

---

## 关键组件位置速查

### 基础组件
```
AstralBaseBlock          - common/base/AstralBaseBlock.java
AstralBaseItem           - common/base/AstralBaseItem.java
AstralBaseTileEntity     - common/tile/base/AstralBaseTileEntity.java
TileReceiverBaseInventory - common/tile/base/TileReceiverBaseInventory.java
```

### 工具类
```
Vector3                  - common/util/data/Vector3.java
NBTHelper                - common/util/NBTHelper.java
MiscUtils                - common/util/MiscUtils.java
EntityUtils              - common/util/EntityUtils.java
```

### 数学库（重要！）
```
BlockPos                 - common/util/math/BlockPos.java
Vec3d                    - common/util/math/Vec3d.java
MathHelper              - common/util/math/MathHelper.java (可用的1.7.10版本)
```

### 注册类
```
RegistryEntities         - common/registry/RegistryEntities.java
RegistryTileEntities     - common/registry/RegistryTileEntities.java
RegistryBlocks           - common/registry/RegistryBlocks.java
RegistryItems            - common/registry/RegistryItems.java
```

### 粒子系统（已实现）
```
EffectHelper             - common/client/effect/EffectHelper.java
EntityComplexFX          - common/client/effect/EntityComplexFX.java
EntityFXFacingParticle   - common/client/effect/EntityFXFacingParticle.java
EffectHandler            - common/client/effect/EffectHandler.java
```

---

## 常见错误模式

### 错误1：使用不存在的包
```java
// ❌ 错误
import net.minecraft.util.math.BlockPos;

// ✅ 正确
import hellfirepvp.astralsorcery.common.util.math.BlockPos;
```

### 错误2：错误的数据监视器API
```java
// ❌ 错误
return this.getDataWatcher().getWatchableObjectBoolean(IDX);

// ✅ 正确
return this.getDataWatcher().getWatchableObjectInt(IDX) != 0;
```

### 错误3：错误的ItemStack处理
```java
// ❌ 错误
if (stack == ItemStack.EMPTY) { }
int count = stack.getCount();

// ✅ 正确
if (stack == null || stack.stackSize <= 0) { }
int count = stack.stackSize;
```

### 错误4：错误的TileEntity坐标
```java
// ❌ 错误
BlockPos pos = this.getPos();
int x = pos.getX();

// ✅ 正确
int x = this.xCoord;
int y = this.yCoord;
int z = this.zCoord;
```

### 错误5：错误的FML注解
```java
// ❌ 错误
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// ✅ 正确
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
```

---

## 编译和测试流程

### 1. 编译检查
```bash
cd D:\GTNH\AstraSorcery\AstralSorcery1.7.10
gradlew compileJava
```

### 2. 完整构建
```bash
gradlew build
```

### 3. 错误处理
如果编译失败：
1. ✅ 查看错误信息
2. ✅ 参考本文档的"错误分类处理"
3. ✅ 搜索相关库中的类似代码
4. ✅ 修复后重新编译
5. ❌ 不要盲目修改

### 4. 功能验证
- [ ] 代码编译通过
- [ ] 对比 original 中的实现
- [ ] 确认功能逻辑正确
- [ ] TODO注释清晰标注

---

## TODO处理原则

### 查找TODO
```bash
cd AstralSorcery1.7.10\src
grep -r "TODO" --include="*.java" > all_todos.txt
```

### TODO分类
1. **简单TODO** (90个)
   - 无依赖关系
   - 可以直接实现
   - 优先处理

2. **系统TODO** (312个)
   - 需要完整系统支持
   - 等待基础组件完成
   - 分批处理

3. **已解决TODO** (34+个)
   - EffectHelper相关 (30个)
   - Vector3相关 (6个)
   - 粒子系统相关 (34个)

### TODO处理流程
```
1. 识别TODO类型
2. 检查依赖是否满足
3. 如果满足：实现并移除TODO
4. 如果不满足：保留TODO，添加依赖说明
5. 测试编译
```

---

## 开发检查清单

在提交任何代码前，确认：

### 代码质量
- [ ] 使用1.7.10 API
- [ ] 使用util.math库
- [ ] 继承正确的基类
- [ ] 实现必要的接口
- [ ] 没有编译错误
- [ ] 没有编译警告

### 功能完整性
- [ ] 对比original实现
- [ ] 功能逻辑正确
- [ ] TODO标注清晰
- [ ] 不存在的功能有注释

### 注册完整性
- [ ] 在Registry中注册
- [ ] 注册名称正确
- [ ] 本地化名称添加
- [ ] 资源路径正确

### 文档更新
- [ ] 更新PROGRESS文件
- [ ] 记录新增组件
- [ ] 标注未完成部分

---

## 立即可实现的任务（优先级排序）

### Week 3-4: 基础系统（推荐）
1. **ConstellationSystem** (7个TODO)
   - 星座数据结构
   - 天空计算
   - 星光收集

2. **PacketSystem** (2个TODO)
   - 网络通信
   - 客户端同步

3. **AltarSystem** (6个TODO)
   - 祭坛合成逻辑
   - 结构匹配
   - 星座需求

### Week 5-8: 高级功能
1. 渲染系统完善
2. GUI系统
3. 高级TileEntity
4. 实体AI完善

---

## 深度分析文件夹结构

```
深度分析/
├── Block/                  # 方块组件分析
├── Entity/                 # 实体组件分析
├── Item/                   # 物品组件分析
├── TileEntity/             # 方块实体分析
├── 集成组件/               # 集成系统分析
├── 星光网络/               # 星光传输系统
├── 仪式系统分析汇总.md      # 仪式系统
├── forget.txt              # 未分析文件清单
├── COMPLETE_INDEX.md       # 完整索引
├── PROGRESS.md             # 进度报告
└── README.md               # 分析说明
```

### 重要分析报告
1. **COMPLETE_INDEX.md** - 所有组件索引
2. **PROGRESS_FINAL.md** - 最终进度报告
3. **forget.txt** - 未分析文件（可参考）

---

## 总结：开发工作流

```
┌─────────────────────────────────────────┐
│ 1. 阅读 DEVELOPMENT_GUIDE.md            │
│    查找组件信息、继承关系、注册流程      │
└─────────────────┬───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│ 2. 查看深度分析文件夹                    │
│    理解组件设计、依赖关系、实现细节      │
└─────────────────┬───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│ 3. 对比 original 目录                    │
│    确认高版本实现、功能逻辑              │
└─────────────────┬───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│ 4. 执行迁移                              │
│    使用1.7.10 API、util.math库          │
│    标注无法实现的功能                    │
└─────────────────┬───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│ 5. 编译测试                              │
│    gradlew compileJava                  │
│    修复编译错误                          │
└─────────────────┬───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│ 6. 注册组件                              │
│    添加到Registry、本地化、资源          │
└─────────────────┬───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│ 7. 更新文档                              │
│    PROGRESS、TODO状态、完成报告          │
└─────────────────────────────────────────┘
```

---

## 附录：参考文档清单

### 必读文档
1. ✅ **DEVELOPMENT_GUIDE.md** - 完整开发指南
2. ✅ **PROGRESS_UPDATE_2026-01-29.md** - 最新进度
3. ✅ **compile_fix.txt** - 迁移方法论
4. ✅ **分析.txt** - 组件分析框架
5. ✅ **深度分析.txt** - 深度分析框架
6. ✅ **迁移.txt** - 迁移执行步骤

### 参考分析
- TODO_DEPENDENCY_ANALYSIS_REPORT.md
- IMPLEMENTATION_PRIORITY_GUIDE.md
- 深度分析/COMPLETE_INDEX.md
- 深度分析/PROGRESS_FINAL.md

### 代码库路径
```
原始代码: D:\GTNH\AstraSorcery\AstralSorcery1.12.2\src
工作代码: D:\GTNH\AstraSorcery\AstralSorcery1.7.10\src
参考项目: D:\GTNH\AstraSorcery\Twist-Space-Technology-Mod\src\main\java
1.7.10库: D:\GTNH\AstraSorcery\MinecraftLib\1.7.10
```

---

**文档版本**: 1.0
**创建日期**: 2026-01-30
**适用项目**: Astral Sorcery 1.7.10 迁移
**状态**: ✅ 活跃维护

**重要提醒**：
- 🔴 始终优先参考库中的现有代码
- 🔴 没找到可借鉴代码时跳过，不要盲目修改
- 🔴 使用TODO注释标注未实现功能
- 🔴 每次修改后必须编译测试

**祝开发顺利！** 🚀
