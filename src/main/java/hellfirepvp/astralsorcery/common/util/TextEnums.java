/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Enum-based localization system
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

/**
 * TextEnums - Enum-based localization system (Recommended Approach)
 * <p>
 * This class provides a type-safe, compile-time checked way to manage localization keys.
 * It follows the pattern used by Twist Space Technology mod.
 * <p>
 * <b>Benefits of this approach:</b>
 * <ul>
 * <li>Type-safe: Compile-time checking prevents typos in keys</li>
 * <li>Self-documenting: All localization keys are defined in one place</li>
 * <li>Easy to use: Just call {@code enumName.toString()} to get localized text</li>
 * <li>Built-in translations: Comments with #tr mark translations in code</li>
 * </ul>
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * // 1. Define your enum entry (with #tr comments)
 * // #tr NameMyItem
 * // # My Awesome Item
 * // #zh_CN 我的超棒物品
 * NameMyItem("NameMyItem"),
 *
 * // 2. In your code, use it directly
 * public class ItemMyItem extends AstralBaseItem {
 *     public ItemMyItem() {
 *         super();
 *         this.setUnlocalizedName(NameMyItem.getKey());
 *     }
 *
 *     public String getLocalizedName() {
 *         return NameMyItem.toString();
 *     }
 * }
 *
 * // 3. In language files:
 * # en_us.lang
 * NameMyItem=My Awesome Item
 *
 * # zh_cn.lang
 * NameMyItem=我的超棒物品
 * </pre>
 * <p>
 * <b>Translation Comment Format:</b>
 * 
 * <pre>
 * // #tr YourTranslationKey
 * // # English Translation
 * // #zh_CN 中文翻译
 * YourTranslationKey("YourTranslationKey"),
 * </pre>
 *
 * @author Astral Sorcery Team
 * @version 1.7.10
 */
public enum TextEnums {

    // ========================================================================
    // General Mod Information
    // ========================================================================

    // #tr Mod_AstralSorcery
    // # Astral Sorcery
    // #zh_CN 星辉魔法
    Mod_AstralSorcery("Mod_AstralSorcery"),

    // #tr ItemGroup_AstralSorcery
    // # Astral Sorcery
    // #zh_CN 星辉魔法
    ItemGroup_AstralSorcery("itemGroup.astralsorcery"),

    // #tr ItemGroup_Papers
    // # [AS] Constellation Papers
    // #zh_CN [AS] 星图
    ItemGroup_Papers("itemGroup.astralsorcery.papers"),

    // #tr ItemGroup_Crystals
    // # [AS] Attuned Crystals
    // #zh_CN [AS] 共振水晶
    ItemGroup_Crystals("itemGroup.astralsorcery.crystals"),

    // ========================================================================
    // Common UI/Tooltip Strings
    // ========================================================================

    // #tr Misc_MoreInformation
    // # <Hold SHIFT for more details>
    // #zh_CN <按住 SHIFT 来查看详细内容>
    Misc_MoreInformation("misc.moreInformation"),

    // #tr Misc_CraftInformation
    // # §7Click to see recipe
    // #zh_CN §7点击查看合成
    Misc_CraftInformation("misc.craftInformation"),

    // #tr JEI_Category_Well
    // # Lightwell
    // #zh_CN 聚星缸
    JEI_Category_Well("jei.category.well"),

    // #tr JEI_Category_Grindstone
    // # Grindstone
    // #zh_CN 砂轮
    JEI_Category_Grindstone("jei.category.grindstone"),

    // #tr JEI_Category_Infuser
    // # Starlight Infusion
    // #zh_CN 星能聚合器
    JEI_Category_Infuser("jei.category.infuser"),

    // #tr JEI_Category_Transmutation
    // # Starlight Transmutation
    // #zh_CN 星辉转化
    JEI_Category_Transmutation("jei.category.transmutation"),

    // ========================================================================
    // Altar Categories
    // ========================================================================

    // #tr Altar_Discovery
    // # Luminous Crafting Table
    // #zh_CN 星辉合成台
    Altar_Discovery("jei.category.altar.discovery"),

    // #tr Altar_Attunement
    // # Starlight Crafting Altar
    // #zh_CN 星辉祭坛
    Altar_Attunement("jei.category.altar.attunement"),

    // #tr Altar_Constellation
    // # Celestial Altar
    // #zh_CN 天辉祭坛
    Altar_Constellation("jei.category.altar.constellation"),

    // #tr Altar_Trait
    // # Iridescent Altar
    // #zh_CN 五彩祭坛
    Altar_Trait("jei.category.altar.trait");

    // ========================================================================
    // Internal Implementation
    // ========================================================================

    /**
     * Obsolete method - use tr(key) directly from ASUtils instead.
     *
     * @param key the localization key
     * @return the localized string
     */
    @Deprecated
    public static String tr(String key) {
        return ASUtils.tr(key);
    }

    /**
     * Obsolete method with format - use tr(key, format) directly from ASUtils instead.
     *
     * @param key    the localization key
     * @param format format arguments
     * @return the localized and formatted string
     */
    @Deprecated
    public static String tr(String key, Object... format) {
        return ASUtils.tr(key, format);
    }

    private final String text;
    private final String key;

    /**
     * Constructor for TextEnum entry
     *
     * @param key the localization key
     */
    TextEnums(String key) {
        this.key = key;
        this.text = ASUtils.tr(key);
    }

    /**
     * Get the localized text for this enum entry.
     * <p>
     * Returns the translated text based on current game language.
     * If no translation is found, returns the key itself.
     *
     * @return the localized text
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Get the localization key for this enum entry.
     *
     * @return the key (used for setUnlocalizedName, etc.)
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the localized text (explicit method).
     * <p>
     * Same as {@link #toString()}, but more readable in some contexts.
     *
     * @return the localized text
     */
    public String getText() {
        return text;
    }
}
