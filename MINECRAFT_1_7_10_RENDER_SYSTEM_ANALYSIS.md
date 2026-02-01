# Minecraft 1.7.10 渲染系统完整分析报告

## 目录
1. [渲染系统架构概览](#渲染系统架构概览)
2. [Block 渲染系统](#block-渲染系统)
3. [TileEntity 渲染系统](#tileentity-渲染系统)
4. [Item 渲染系统](#item-渲染系统)
5. [OBJ 模型系统](#obj-模型系统)
6. [纹理映射机制](#纹理映射机制)
7. [四种渲染错误的根本原因](#四种渲染错误的根本原因)
8. [修复方案](#修复方案)

---

## 渲染系统架构概览

### 核心渲染流程

```
WorldRenderer → RenderBlocks → [根据 getRenderType() 分发]
                                    ├─ 0: 标准方块渲染
                                    ├─ 1-31: 特殊类型渲染
                                    └─ -1: TESR 渲染
```

### 关键类

| 类 | 职责 |
|---|---|
| `net.minecraft.client.renderer.RenderBlocks` | 核心方块渲染器 |
| `net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer` | TESR 基类 |
| `net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher` | TESR 分发器 |
| `net.minecraftforge.client.model.IModelCustom` | 自定义模型接口 |
| `net.minecraftforge.client.model.obj.WavefrontObject` | OBJ 模型实现 |

---

## Block 渲染系统

### getRenderType() 返回值详解

```java
// RenderBlocks.java:332
public boolean renderBlockByRenderType(Block block, int x, int y, int z)
{
    int renderType = block.getRenderType();  // 获取渲染类型

    switch (renderType)
    {
        case 0:  // 标准立方体
            return renderStandardBlock(block, x, y, z);

        case 1:  // 十字形（植物、 flowers）
            return renderCrossedBlocks(...);

        case 2:  // 植物（高草、灌木）
            return renderBlockPlant(...);

        case 3:  // 火炬
            return renderBlockTorch(...);

        // ... 更多特殊类型

        case -1: // TESR 渲染（由 TileEntityRendererDispatcher 处理）
            return false; // RenderBlocks 不处理，跳过

        default: // 自定义渲染类型
            ISimpleBlockRenderingHandler handler =
                RenderingRegistry.getHandler(renderType);
            return handler.renderWorldBlock(...);
    }
}
```

### 标准 Block 渲染流程（renderType = 0）

```
1. Block.getIcon(side, meta) → 获取 IIcon
2. Tessellator.addVertexWithUV() → 绘制面
3. 使用 uvRotateXX 处理 UV 旋转
4. 应用环境光遮蔽（AO）
```

### 关键方法

```java
// Block.java
public int getRenderType() {
    return 0;  // 默认标准渲染
}

public IIcon getIcon(int side, int meta) {
    return this.blockIcon;  // 返回纹理图标
}

public boolean isOpaqueCube() {
    return true;  // 是否是不透明立方体
}

public boolean renderAsNormalBlock() {
    return true;  // 是否作为标准方块渲染
}
```

---

## TileEntity 渲染系统

### TESR 触发条件

```java
// Block 必须满足以下条件才会使用 TESR：
1. getRenderType() == -1
2. hasTileEntity(metadata) == true
3. createNewTileEntity(world, meta) 返回非 null
```

### TESR 基本结构

```java
public abstract class TileEntitySpecialRenderer {
    public abstract void renderTileEntityAt(
        TileEntity tile,
        double x, double y, double z,
        float partialTicks
    );

    protected void bindTexture(ResourceLocation location) {
        // 绑定纹理
    }
}
```

### TileEntityChestRenderer 实际例子

```java
public class TileEntityChestRenderer extends TileEntitySpecialRenderer {
    private ModelChest chestModel = new ModelChest();
    private ResourceLocation texture =
        new ResourceLocation("textures/entity/chest/normal.png");

    public void renderTileEntityAt(TileEntityChest tile, double x, double y, double z, float pt) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        // 绑定纹理
        this.bindTexture(texture);

        // 渲染 Java 模型
        chestModel.renderAll();

        GL11.glPopMatrix();
    }
}
```

### TESR 注册

```java
// ClientProxy 或 RegistryRender
ClientRegistry.bindTileEntitySpecialRenderer(
    TileGrindstone.class,
    new TESRGrindstone()
);
```

---

## OBJ 模型系统

### IModelCustom 接口

```java
public interface IModelCustom {
    String getType();  // "OBJ" 或 "Techne"

    // 渲染所有部分
    void renderAll();

    // 仅渲染指定组
    void renderOnly(String... groupNames);

    // 渲染单个部分（按组名）
    void renderPart(String partName);

    // 渲染除指定组外的所有部分
    void renderAllExcept(String... excludedGroupNames);
}
```

### OBJ 文件格式与组

```
# OBJ 文件结构
v 0.0 0.0 0.0       # 顶点
vt 0.0 0.0          # UV 坐标
vn 0.0 1.0 0.0      # 法线

g GroupName         # 组开始（重要！）
f 1/1/1 2/2/2 3/3/3  # 面（使用当前组）

g OtherGroup        # 另一个组
f ...
```

### WavefrontObject 实现

```java
public class WavefrontObject implements IModelCustom {
    public ArrayList<Vertex> vertices;
    public ArrayList<TextureCoordinate> textureCoordinates;
    public ArrayList<GroupObject> groupObjects;  // 组列表

    @Override
    public void renderAll() {
        for (GroupObject group : groupObjects) {
            group.render();
        }
    }

    @Override
    public void renderPart(String partName) {
        for (GroupObject group : groupObjects) {
            if (group.name.equals(partName)) {
                group.render();
                return;
            }
        }
    }
}
```

### OBJ 模型加载

```java
// Forge AdvancedModelLoader
IModelCustom model = AdvancedModelLoader.loadModel(
    new ResourceLocation("modid", "models/obj/block/model.obj")
);

// 模型类型自动检测（通过文件扩展名）
// → ObjModelLoader 加载 .obj
// → TechneModelLoader 加载 .tcn
```

---

## 纹理映射机制

### 纹理路径解析

```
资源路径格式: "domain:path"

例如:
  "astralsorcery:textures/models/altar/altar_1_top.png"

解析为:
  assets/astralsorcery/textures/models/altar/altar_1_top.png
```

### OBJ 模型的纹理映射

OBJ 模型使用 **单一纹理**：

```java
// OBJ 模型的纹理绑定
IModelCustom model = AdvancedModelLoader.loadModel(...);
ResourceLocation texture = new ResourceLocation("modid", "textures/.../texture.png");

// 渲染时
bindTexture(texture);
model.renderAll();  // 所有面使用同一纹理
```

**关键限制：OBJ 模型在 1.7.10 中只能使用单个纹理！**

### 多纹理解决方法

#### 方法 1：使用 OBJ 组

```java
// OBJ 文件必须定义组
g side
f ...   // 侧面面

g top
f ...   // 顶面面

g bottom
f ...   // 底面面

// Java 代码
bindTexture(textureSide);
model.renderPart("side");

bindTexture(textureTop);
model.renderPart("top");

bindTexture(textureBottom);
model.renderPart("bottom");
```

#### 方法 2：使用 Java Model 类

```java
// 像原版 Chest 一样
public class ModelAltar extends ModelBase {
    ModelRenderer pillar;
    ModelRenderer base;
    ModelRenderer top;

    public ModelAltar() {
        textureWidth = 128;
        textureHeight = 32;

        // 每个 ModelRenderer 可以有不同的 UV 坐标
        pillar = new ModelRenderer(this, 0, 0);
        pillar.addBox(...);

        base = new ModelRenderer(this, 0, 16);
        base.addBox(...);

        top = new ModelRenderer(this, 64, 0);
        top.addBox(...);
    }
}
```

---

## 四种渲染错误的根本原因

### 1. machine.grindstone - 完全空白

**原因**：
```java
// BlockMachine.java:183
public TileEntity createNewTileEntity(World world, int meta) {
    return null;  // ← 返回 null！
}
```

**分析**：
1. `BlockMachine.getRenderType() = -1` → 使用 TESR
2. `TESRGrindstone` 检查 TileEntity 类型
3. `createNewTileEntity()` 返回 null
4. TESR 收到 null，不渲染任何内容

**修复**：
```java
public TileEntity createNewTileEntity(World world, int meta) {
    switch (meta) {
        case META_GRINDSTONE:
            return new TileGrindstone();
        case META_TELESCOPE:
            return new TileTelescope();
        default:
            return new TileGrindstone(); // 默认
    }
}
```

---

### 2. block.bore - 紫黑色方块

**原因**：
```java
// BlockBore.java 没有覆盖 getRenderType()
// → 使用默认值 0（标准方块渲染）

// BlockBore.java 没有注册 icons
public IIcon getIcon(int side, int meta) {
    return null;  // ← 返回 null！
}
```

**分析**：
1. `getRenderType()` 未覆盖 → 返回 0（标准方块）
2. 标准方块渲染调用 `getIcon()` 获取纹理
3. `getIcon()` 返回 null
4. Minecraft 显示缺失纹理（紫黑色）

**修复**：
```java
// 选项 A: 使用 TESR + OBJ
@Override
public int getRenderType() {
    return -1;  // 使用 TESR
}

public TileEntity createNewTileEntity(World world, int meta) {
    return new TileBore();
}

// 选项 B: 使用标准方块渲染
@SideOnly(Side.CLIENT)
private IIcon iconSide;
@SideOnly(Side.CLIENT)
private IIcon iconTop;

@SideOnly(Side.CLIENT)
public void registerBlockIcons(IIconRegister reg) {
    iconSide = reg.registerIcon("astralsorcery:bore_side");
    iconTop = reg.registerIcon("astralsorcery:bore_top");
}

@SideOnly(Side.CLIENT)
public IIcon getIcon(int side, int meta) {
    return side == 1 ? iconTop : iconSide;
}
```

---

### 3. gateway - 贴图错位

**原因**：
```java
// TESRCelestialGateway.java
bindTexture(texture);  // 只绑定一个纹理
model.renderAll();     // 渲染所有部分
```

**分析**：
1. OBJ 模型可能有多个组
2. 只绑定了一个纹理
3. 所有部分使用同一纹理，导致贴图错位

**需要检查**：
```bash
# 检查 OBJ 文件是否有组定义
cat celestial_gateway.obj | grep "^g "
```

**修复**：
```java
// 如果 OBJ 有多个纹理需求
if (model.getType().equals("OBJ")) {
    // 渲染平台
    bindTexture(platformTexture);
    model.renderPart("Platform");

    // 渲染柱子
    bindTexture(pillarTexture);
    model.renderPart("Pillar");

    // 渲染球体
    bindTexture(orbTexture);
    model.renderPart("Orb");
}
```

---

### 4. altar - 特定角度显示紫黑色

**原因**：
```java
// TESRAltar.java 中的 renderFace() 方法
private void renderFace(Tessellator t, ..., IIcon icon, ...) {
    // UV 坐标计算可能有误
    double minU = icon.getInterpolatedU(u1 * 16);
    double maxU = icon.getInterpolatedU(u2 * 16);
    // ...
}
```

**分析**：
1. `renderFace()` 的 UV 坐标计算不正确
2. 面的顶点顺序可能有误（导致背面剔除）
3. 某些角度的 UV 映射错误

**问题代码**：
```java
// 当前实现的问题
if (Math.abs(yDiff) < 0.001) { // 水平面
    if (Math.abs(zDiff) > 0.001) {
        // 这个分支的顶点顺序可能错误
        t.addVertexWithUV(x2, y1, z1, maxU, minV);
        t.addVertexWithUV(x1, y1, z1, minU, minV);
        t.addVertexWithUV(x1, y2, z2, minU, maxV);
        t.addVertexWithUV(x2, y2, z2, maxU, maxV);
    }
}
```

**修复**：
```java
// 使用标准 Tessellator 绘制
// 参考 RenderBlocks.renderFaceXPos() 等方法

private void renderFaceNorth(Tessellator t, double x1, double y1, double z1,
                             double x2, double y2, double z2, IIcon icon) {
    double minU = icon.getInterpolatedU(0);
    double maxU = icon.getInterpolatedU(16);
    double minV = icon.getInterpolatedV(0);
    double maxV = icon.getInterpolatedV(16);

    t.addVertexWithUV(x1, y1, z1, minU, minV);
    t.addVertexWithUV(x2, y1, z1, maxU, minV);
    t.addVertexWithUV(x2, y2, z1, maxU, maxV);
    t.addVertexWithUV(x1, y2, z1, minU, maxV);
}

// 或更简单：使用 RenderBlocks 的标准方法
```

---

## 修复方案总结

### 问题 1: Grindstone（空白）

```java
// BlockMachine.java
@Override
public TileEntity createNewTileEntity(World world, int meta) {
    switch (meta) {
        case META_GRINDSTONE:
            return new TileGrindstone();
        case META_TELESCOPE:
            return new TileTelescope();
        default:
            return new TileGrindstone();
    }
}
```

### 问题 2: Bore（紫黑色）

```java
// BlockBore.java - 选项 A: 使用标准渲染
@Override
public int getRenderType() {
    return 0;  // 标准方块渲染
}

@SideOnly(Side.CLIENT)
private IIcon iconSide;

@SideOnly(Side.CLIENT)
public void registerBlockIcons(IIconRegister reg) {
    iconSide = reg.registerIcon("astralsorcery:bore_side");
}

@SideOnly(Side.CLIENT)
public IIcon getIcon(int side, int meta) {
    return iconSide;
}

@Override
public boolean isOpaqueCube() {
    return false;  // 自定义形状
}
```

### 问题 3: Gateway（贴图错位）

```bash
# 首先检查 OBJ 文件
grep "^g " celestial_gateway.obj

# 如果有多个组，修改 TESR
```

```java
// TESRCelestialGateway.java
@Override
public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float pt) {
    // 检查模型是否有组
    if (model.getType().equals("OBJ")) {
        // 渲染各个部分
        bindTexture(new ResourceLocation("astralsorcery",
            "textures/models/celestialgateway/platform.png"));
        model.renderPart("Platform");

        // ... 其他部分
    } else {
        // 单一纹理
        bindTexture(texture);
        model.renderAll();
    }
}
```

### 问题 4: Altar（UV 错误）

```java
// TESRAltar.java - 简化实现
private void renderBaseAltarModel(TileAltar altar, double x, double y, double z) {
    BlockAltar block = (BlockAltar) altar.getBlockType();
    int meta = altar.getBlockMetadata();

    // 使用 RenderBlocks 渲染标准立方体部分
    RenderBlocks renderer = new RenderBlocks(altar.getWorldObj());

    // 设置边界
    renderer.setRenderBounds(0.25, 0.125, 0.25, 0.75, 0.59375, 0.75);
    renderer.renderStandardBlock(block, altar.xCoord, altar.yCoord, altar.zCoord);

    // 或使用 Tessellator 但参考原版实现
    // 参考 RenderBlocks.renderFaceXPos() 等方法
}
```

---

## 最佳实践建议

### 1. 选择正确的渲染方式

```
┌─────────────────┬──────────────────┬──────────────────┐
│ 方块类型        │ 渲染方式         │ getRenderType()  │
├─────────────────┼──────────────────┼──────────────────┤
│ 标准方块        │ RenderBlocks     │ 0                │
│ 简单变形方块    │ Icon + UV        │ 0                │
│ 复杂 3D 模型    │ Java Model + TESR│ -1               │
│ OBJ 单纹理      │ OBJ + TESR       │ -1               │
│ OBJ 多纹理      │ OBJ 组 + TESR    │ -1               │
│ 动态渲染        │ TESR             │ -1               │
└─────────────────┴──────────────────┴──────────────────┘
```

### 2. TESR 实现清单

```java
✅ Block.getRenderType() == -1
✅ Block.hasTileEntity() == true
✅ Block.createNewTileEntity() 返回正确的 TileEntity
✅ ClientRegistry.bindTileEntitySpecialRenderer() 已注册
✅ TESR.bindTexture() 绑定纹理
✅ GL11 状态保存/恢复
✅ 模型/几何体正确绘制
```

### 3. 调试技巧

```java
// 在 TESR 中添加日志
@Override
public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float pt) {
    if (tile == null) {
        LogHelper.warn("[TESR] TileEntity is null!");
        return;
    }

    if (!(tile instanceof ExpectedType)) {
        LogHelper.warn("[TESR] Wrong type: " + tile.getClass());
        return;
    }

    LogHelper.debug("[TESR] Rendering at " + x + ", " + y + ", " + z);
    // ... 渲染代码
}
```

---

## 参考资料

- `net.minecraft.client.renderer.RenderBlocks` - 标准方块渲染
- `net.minecraft.client.renderer.tileentity.TileEntityChestRenderer` - TESR 例子
- `net.minecraftforge.client.model.obj.WavefrontObject` - OBJ 实现
- `cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler` - 自定义渲染

---

**报告生成时间**: 2026-01-31
**分析基础**: Minecraft 1.7.10 + Forge 10.13.4 原版代码
