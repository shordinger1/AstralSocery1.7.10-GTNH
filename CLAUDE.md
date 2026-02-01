# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Minecraft 1.7.10 mod port** - Astral Sorcery (originally for 1.12.2) being ported to 1.7.10 Forge (10.13.4.1614) as part of the GTNH (GregTech New Horizons) ecosystem.

**Key constraints:**
- Target: Minecraft 1.7.10 with Forge 10.13.4.x
- Java 8 compatible
- Must use 1.7.10 API only (no 1.12.2 features)
- Follows GTNH modding conventions

## Build Commands

```bash
# Set up the workspace
./gradlew clean setupDecompWorkspace

# Build the mod
./gradlew build

# Run client (for testing)
./gradlew runClient

# Run server
./gradlew runServer

# Clean build
./gradlew clean build
```

## Architecture

### Proxy Pattern (Client/Server Separation)

The mod uses FML's `@SidedProxy` annotation to separate client and server code:

- **CommonProxy** (`hellfirepvp.astralsorcery.CommonProxy`): Base proxy with shared code
- **ClientProxy** (`hellfirepvp.astralsorcery.client.ClientProxy`): Client-specific code (renderers, effects, GUIs)
- Server proxy is the CommonProxy (no separate server implementation)

**Critical:** Always check `world.isRemote` before executing client-side code:
- `world.isRemote == true` → Client side
- `world.isRemote == false` → Server side

### Base Class Hierarchy

All game objects inherit from custom base classes that provide common functionality:

```
AstralBaseBlock    → All blocks
AstralBaseItem     → All items
AstralBaseTileEntity → All TileEntities
AstralBaseEntity   → All entities
AstralBasePotion   → All potion effects
```

**Never bypass these base classes** - they provide essential helper methods for side checking, NBT handling, logging, etc.

### Registry Pattern

All game objects are registered through centralized registry classes in `common/registry/`:

- **RegistryBlocks**: Block registration
- **RegistryItems**: Item registration
- **RegistryEntities**: Entity registration
- **RegistryTileEntities**: TileEntity registration
- **RegistryWorldGenerators**: World generation registration

Static references are stored in:
- **BlocksAS**: Block instances
- **ItemsAS**: Item instances

### Package Structure

```
hellfirepvp.astralsorcery/
├── AstralSorcery.java         # Main mod class with @Mod annotation
├── CommonProxy.java            # Common proxy (server-side)
├── client/                     # Client-only code
│   ├── ClientProxy.java        # Client proxy
│   ├── effect/                 # Particle effects and visual effects
│   ├── event/                  # Client-side event handlers
│   ├── gui/                    # GUI screens
│   ├── renderer/               # TileEntity and entity renderers
│   └── util/                   # Client utilities
└── common/                     # Shared code
    ├── base/                   # Base classes (AstralBaseBlock, etc.)
    ├── block/                  # Block implementations
    ├── constellation/          # Constellation system
    ├── entity/                 # Entity implementations
    ├── handler/                # Event handlers, GUI handlers
    ├── item/                   # Item implementations
    ├── lib/                    # Constants, static references (BlocksAS, ItemsAS)
    ├── network/                # Network packets
    ├── potion/                 # Potion effects
    ├── registry/               # Registry classes
    ├── tile/                   # TileEntity implementations
    ├── util/                   # Utilities (LogHelper, NBTHelper, etc.)
    └── world/                  # World generation
```

## Development Rules

### 1. Version Compatibility (CRITICAL)

**DO NOT:**
- Copy code from 1.12.2 version directly
- Use newer Forge APIs not present in 1.7.10
- Use the modern registry system (use `GameRegistry` instead)
- Assume methods exist without checking 1.7.10 API

**DO:**
- Check API availability against Minecraft 1.7.10 / Forge 10.13.4
- Reference existing 1.7.10 mods in the GTNH ecosystem (TST, GT5, BartWorks)
- Use `GameRegistry.registerBlock()`, `GameRegistry.registerItem()`, etc.
- Always use `@SideOnly(Side.CLIENT)` for client-only code

### 2. Registration

All registration must happen in appropriate lifecycle phases via proxies:

**PreInit (preInit):**
- Register blocks, items, entities, TileEntities
- Load configuration

**Init (init):**
- Register event handlers
- Register network packets
- Register GUI handlers
- Register renderers (client only)

**PostInit (postInit):**
- Register recipes
- Complete mod integration

### 3. TileEntity Synchronization

For TileEntities that need to sync to client:

```java
// In AstralBaseTileEntity subclasses:
@Override
public boolean canUpdate() {
    return true; // Enable tick updates
}

// Trigger sync when data changes
markForUpdate();

// Custom NBT methods for save/load
@Override
protected void readCustomNBT(NBTTagCompound compound) { }
@Override
protected void writeCustomNBT(NBTTagCompound compound) { }
```

### 4. Side-Safe Code

Use helper methods from base classes:

```java
// Instead of:
if (!world.isRemote) { /* server code */ }

// Use:
if (isServer(world)) { /* server code */ }
if (isClient(world)) { /* client code */ }
```

### 5. Logging

Always use `LogHelper` for consistent logging:

```java
LogHelper.info("Information message");
LogHelper.debug("Debug message");
LogHelper.warn("Warning message");
LogHelper.error("Error message");

// Method entry/exit tracking
LogHelper.entry("methodName");
LogHelper.exit("methodName");
```

### 6. NBT Operations

Use `NBTHelper` for complex NBT operations:

```java
// Write
NBTHelper.writeBlockPos(compound, "Pos", pos);
NBTHelper.writeItemStack(compound, "Item", stack);

// Read
BlockPos pos = NBTHelper.readBlockPos(compound, "Pos");
ItemStack stack = NBTHelper.readItemStack(compound, "Item");
```

## Key Systems

### Constellation System

Located in `common/constellation/`:

- **IConstellation**: Base interface for all constellations
- **Constellation**: Base implementation
- Constellations have types: Major, Minor, Weak
- Each constellation has effects, colors, and behaviors

### Crystal System

Crystals have properties that affect their behavior:

- **Size**: Affects tool efficiency
- **Purity**: Affects crafting speed and quality
- **Fractality**: Affects special abilities
- Tool stats are calculated from these properties

### Starlight Network

A network of blocks that collect and transmit starlight:

- **Collector Crystals**: Collect starlight from sky
- **Lenses**: Transmit and filter starlight
- **Relays**: Redirect starlight
- **Altars**: Consume starlight for crafting

### Effect System (Client)

Located in `client/effect/`:

- **IComplexEffect**: Interface for complex effects
- **EffectHandler**: Manages all active effects
- **EffectHelper**: Factory methods for common effects
- Effects are updated and rendered each tick on client

## GTNH Integration

This mod integrates with several GTNH mods:

**Dependencies (checked in Constants):**
- GregTech 5 (GT5.Unofficial)
- Blood Magic
- Botania
- Thaumcraft
- Thaumic Tinkerer

**Compatibility:**
- Check `Constants.hasGregTech`, etc. before integrating
- Use GTNHLib for shared utilities
- Follow GTNH conventions for ores, materials, etc.

## Configuration

Configuration is handled by `ConfigurationHandler` in `common/config/`:

Categories:
- `general`: General mod settings
- `client`: Client-only settings (rendering, effects)
- `server`: Server-side settings (gameplay, crafting)
- `debug`: Debug options

## Altar Recipe System (Completed 2026-01-30)

### Recipe Architecture

The altar recipe system is now **70% complete** with 38 recipes registered across all altar levels.

**Location**: `common/crafting/altar/`

**Core Components**:
- **ASAltarRecipe**: Recipe class with shaped/shapeless support
- **AltarRecipeRegistry**: Central recipe storage and lookup
- **ASAltarRecipes**: Recipe registration (all recipes registered here)
- **ShapedRecipeSlot**: Enum for 3x3 grid slot mapping (0-8)
- **AltarRecipeViewer**: NEI integration for recipe display

### Recipe Levels

The mod supports 5 altar levels (defined in `TileAltar.AltarLevel`):
1. **DISCOVERY**: Basic altar - no constellation required
2. **ATTUNEMENT**: Mid-tier altar - constellation discovery
3. **CONSTELLATION_CRAFT**: Constellation-specific crafting
4. **TRAIT_CRAFT**: Trait-based crafting
5. **BRILLIANCE**: Advanced tier

### Recipe Types

**Shapeless Recipes** (物品顺序无关):
```java
// Simple 3-item recipe
ItemStack[] inputs = {
    new ItemStack(ItemsAS.item1),
    new ItemStack(ItemsAS.item2),
    new ItemStack(ItemsAS.item3)
};
ASAltarRecipe recipe = new ASAltarRecipe(
    TileAltar.AltarLevel.DISCOVERY,
    inputs,
    outputStack,
    null,  // No constellation
    200,   // Starlight required
    100,   // Crafting time (ticks)
    false  // Not shaped (shapeless)
);
```

**Shaped Recipes** (精确槽位):
```java
// 3x3 grid with precise slot positions
ItemStack[] inputs = new ItemStack[9];
inputs[ShapedRecipeSlot.UPPER_CENTER.ordinal()] = new ItemStack(ItemsAS.center);
inputs[ShapedRecipeSlot.LEFT.ordinal()] = new ItemStack(ItemsAS.left);
inputs[ShapedRecipeSlot.RIGHT.ordinal()] = new ItemStack(ItemsAS.right);
// ... other slots

ASAltarRecipe recipe = new ASAltarRecipe(
    TileAltar.AltarLevel.DISCOVERY,
    inputs,
    outputStack,
    null,
    200,
    100,
    true,  // Shaped recipe
    3,     // Width
    3      // Height
);
```

### Adding New Recipes

**Steps**:

1. **Check ASAltarRecipes.java** to see existing recipes
2. **Find the appropriate helper method** for the altar level:
   - `addDiscoveryRecipe()` - Shapeless discovery recipes
   - `addShapedDiscoveryRecipe()` - Shaped discovery recipes
   - `addShapedAttunementRecipe()` - Shaped attunement recipes
   - `addShapedConstellationRecipe()` - Shaped constellation recipes
   - `addShapedTraitRecipe()` - Shaped trait recipes

3. **Add recipe in the correct register method**:
   - `registerDiscoveryRecipes()` - Lines 84-378
   - `registerAttunementRecipes()` - Lines 384-698
   - `registerConstellationRecipes()` - Lines 704-986
   - `registerTraitRecipes()` - Lines 988-1018
   - `registerSpecialItemRecipes()` - Lines 1024-1118

4. **Use placeholder items** if items don't exist yet:
   ```java
   // Use stone as placeholder
   new ItemStack(Blocks.stone)
   ```

5. **Set appropriate parameters**:
   - **Starlight**: Typical values 100-1000 (based on recipe complexity)
   - **Crafting Time**: Typical values 50-300 ticks (2.5-15 seconds)
   - **Constellation**: Required for ATTUNEMENT and above levels
   - **Width/Height**: For shaped recipes (1-3)

### Recipe Slot Mapping

For shaped recipes, use `ShapedRecipeSlot` enum:

```
0 (UPPER_LEFT)    1 (UPPER_CENTER)   2 (UPPER_RIGHT)
3 (LEFT)          4 (CENTER)          5 (RIGHT)
6 (LOWER_LEFT)    7 (LOWER_CENTER)   8 (LOWER_RIGHT)
```

### Currently Registered Recipes (38 total)

**Discovery Altar (16 recipes)**:
- Rock Crystal → Rock Crystal Simple
- Paper + Crystal + Infused Wood → Constellation Paper
- Book + Crystal + Marble → Journal
- Infused Glass, Colored Lens, Hand Telescope, Knowledge Fragment
- Fragment Capsule, Knowledge Share, Usable Dust, Crafting Component
- Celestial Crystal, Wand, Sextant, Illumination Powder x16, Nocturnal Powder x4

**Attunement Altar (17 recipes)**:
- Tuned crystals (Rock + Celestial)
- Charged crystal tools (Pickaxe, Sword, Axe, Shovel)
- Special wands (Grapple, Architect, Exchange, Illumination)
- Rose Branch Bow, Linking Tool, Sky Resonator, Sextant
- Knowledge Share, Shifting Star, Perk Seal (shaped recipes)

**Constellation Altar (9 recipes)**:
- Infused Glass, Ritual Link x2 (shaped)
- 7 Colored Lenses (Fire, Break, Growth, Damage, Regeneration, Push, Spectral)

**Trait Altar (1 recipe)**:
- Observatory (shaped)

**Special Items (5 recipes)**:
- Perk Gem, Perk Seal, Shifting Star, Cape, Enchantment Amulet

### Missing Recipes (~52 total)

See `docs/RECIPE_ANALYSIS.md` for complete list of missing recipes.

**High Priority Missing**:
- Upgrade recipes (Discovery→Attunement→Constellation→Trait)
- Observatory constellation-specific recipes
- Lightwell, Tree Beacon, Bore variants
- Celestial Gateway, Drawing Table
- Special machine recipes (Telescope, Grindstone, Prism, Collector Crystals)

### NEI Integration

Recipes are automatically displayed in NEI through `AltarRecipeViewer`:
- Click item → shows altar recipes
- Background texture: `resources/assets/astralsorcery/textures/gui/nei/`
- Recipe layout: 3x3 grid + output + starlight/time display

### Common Recipe Patterns

**Pattern 1: Crystal + Base + Material** (shapeless)
```java
new ItemStack[] {
    new ItemStack(ItemsAS.craftingComponent, 1, meta),  // Crystal
    new ItemStack(baseItem),                             // Base
    new ItemStack(materialItem)                          // Material
}
```

**Pattern 2: Cross Pattern** (shaped, 3x3)
```java
//     [Center]
// [Left] [Center] [Right]
//     [Center]
new ItemStack[] {
    null, centerItem, null,
    leftItem, centerItem, rightItem,
    null, centerItem, null
}
```

**Pattern 3: Corner Pattern** (shaped, 3x3)
```java
// [UL]  [UR]
// [LL]  [LR]
new ItemStack[] {
    ul, null, ur,
    ll, null, lr,
    null, null, null
}
}
```

### Recipe Testing

After adding recipes:
1. **Compile**: `gradlew compileJava`
2. **Launch game**: `gradlew runClient`
3. **Test in NEI**:
   - Press 'R' on output item
   - Verify recipe appears
   - Check slot positions (for shaped recipes)
   - Verify starlight/time display
4. **Test in-game**:
   - Place altar
   - Insert items in correct slots
   - Verify recipe matches
   - Verify output item appears

---

## Testing

When testing changes:

1. Always test on both client and server
2. Check for side-specific code not properly guarded
3. Verify TileEntity synchronization
4. Test with and without other GTNH mods present
5. Check for class loading issues on dedicated server
6. For recipes: Test NEI display and in-game crafting

---

## Current Development Status (2026-01-30)

### What's Been Completed Recently

1. **Altar Recipe System** (70% complete)
   - 38 recipes registered (Discovery, Attunement, Constellation, Trait)
   - Shaped and shapeless recipe support
   - NEI integration with custom GUI
   - Helper methods for all altar levels

2. **Code Quality Fixes**
   - Fixed BlockMarbleSlab.java (syntax errors, obfuscated methods)
   - Fixed BlockMarbleDoubleSlab.java (missing @Override)
   - Replaced obfuscated method names with proper 1.7.10 API

3. **Documentation**
   - Created RECIPE_ANALYSIS.md (complete recipe audit)
   - Created CODEBASE_STATUS_2026-01-30.md (detailed codebase analysis)
   - Updated FUTURE_DEVELOPMENT_PROMPT.md (development priorities)

### Overall Progress: ~38%

**Completed Systems**:
- ✅ Network layer (NetworkWrapper + 3 packet types)
- ✅ Registry framework (blocks, items, TileEntities, entities)
- ✅ Recipe system framework (70% of recipes registered)
- ✅ Base classes (AstralBaseBlock, AstralBaseItem, AstralBaseTileEntity)
- ✅ Math utilities (BlockPos, Vec3d, MathHelper)
- ✅ Particle effect framework
- ✅ Entity system (16/16 entities)

**Partially Complete**:
- 🟡 Block implementations (32 registered, many stub)
- 🟡 Item implementations (26 registered, missing functionality)
- 🟡 TileEntity implementations (11 base classes, core logic TODO-heavy)
- 🟡 Rendering (TESR framework exists, specific renders missing)
- 🟡 Constellation system (interface exists, implementations incomplete)

**Not Started**:
- 🔴 Altar crafting integration (recipes exist, altar doesn't use them yet)
- 🔴 Starlight network (base classes missing)
- 🔴 World generation (framework exists, no generators registered)
- 🔴 GUI system (only NEI viewer, no Altar/Well/Sextant GUIs)
- 🔴 Structure matching system (referenced but not implemented)

### Next Development Phase (P0 Priorities)

1. **Complete TileAltar Implementation**
   - Uncomment and fix structure matching (~50 TODOs)
   - Integrate recipe system with altar
   - Add starlight consumption logic
   - Implement client-side rendering

2. **Implement Basic Constellation System**
   - Complete IConstellation implementations
   - Add constellation discovery logic
   - Implement ConstellationSkyHandler

3. **Create Missing TileEntities**
   - TileCollectorCrystal (starlight network core)
   - TileCrystalLens (starlight transmission)
   - Connect TileWell to BlockWell

See `FUTURE_DEVELOPMENT_PROMPT.md` for detailed priorities.

---

## Important: API Migration Patterns

### Recipe System Example

When implementing features that differ between 1.7.10 and 1.12.2:

**1.7.10 Approach** (Current):
```java
// Uses ItemStack[] for recipe inputs
ItemStack[] inputs = new ItemStack[9];
inputs[0] = new ItemStack(item1);

// Uses simple array for slot matching
if (inputs[slot] != null && inputs[slot].getItem() == requiredItem) {
    // Match
}
```

**1.12.2 Approach** (Original):
```java
// Uses NonNullList<ItemStack>
NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);

// Uses Ingredient system
Ingredient ingredient = Ingredient.fromItem(item1);
if (ingredient.apply(inputs[slot])) {
    // Match
}
```

**Key Differences**:
- 1.7.10: `ItemStack stack` - check for null
- 1.12.2: `ItemStack.EMPTY` - no null checks needed
- 1.7.10: Direct item comparison
- 1.12.2: Ingredient system with flexible matching

## Common Pitfalls

1. **Missing @SideOnly annotations**: Client-side code in common classes will crash dedicated server
2. **Incorrect NBT handling**: Always call super methods when overriding read/writeNBT
3. **Not checking isRemote**: Logic executing on wrong side causes desync
4. **Unregistered objects**: Always register in appropriate registry class
5. **Missing proxy delegation**: Lifecycle events must call super methods
6. **Resource paths**: Use `Constants.RESOURCE_ROOT` for texture/resource paths

## Dependencies Management

The project uses GTNH's dependency system with `elytraModpackVersion`. When adding dependencies in `dependencies.gradle`:

```groovy
// For GTNH mods
implementation(gtnh("ModName"))

// For external mods
implementation(rfg.deobf('curse.maven:project-id:file-id'))
```

## Code Quality

- Disable Spotless: `disableSpotless = true` in gradle.properties
- Disable Checkstyle: `disableCheckstyle = true` in gradle.properties
- Follow existing code style in the project
- Add Javadoc comments to all public classes and methods