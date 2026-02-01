/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Liquid starlight fluid
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.fluid;

import net.minecraftforge.fluids.Fluid;

/**
 * FluidLiquidStarlight - Liquid starlight fluid (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Liquid form of starlight</li>
 * <li>Luminosity: 15 (bright light source)</li>
 * <li>Density: 1001 (slightly heavier than water)</li>
 * <li>Viscosity: 300 (medium thickness)</li>
 * <li>Temperature: 120 (cold)</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes from 1.12.2:</b>
 * <ul>
 * <li>Constructor: No ResourceLocation parameters</li>
 * <li>Texture: Set via setIcons() instead of ResourceLocation</li>
 * <li>Sound: SoundEvent → String sound name</li>
 * <li>No EnumRarity in 1.7.10</li>
 * <li>No setFillSound() - handled by bucket items</li>
 * </ul>
 * <p>
 * <b>Interaction:</b>
 * <ul>
 * <li>Converts cold fluids to ice or cobblestone</li>
 * <li>Converts hot fluids to sand or custom ore</li>
 * <li>Gives night vision to players</li>
 * <li>Converts wood logs to infused wood</li>
 * </ul>
 * <p>
 * <b>Registration:</b>
 * 
 * <pre>
 * // In RegistryBlocks or similar:
 * FluidLiquidStarlight fluid = new FluidLiquidStarlight();
 * FluidRegistry.registerFluid(fluid);
 * fluid.setBlock(blockLiquidStarlight);
 *
 * // Client-side icon registration (in ClientProxy):
 * fluid.setIcons(stillIcon, flowingIcon);
 * </pre>
 */
public class FluidLiquidStarlight extends Fluid {

    /**
     * Constructor for 1.7.10
     * 1.12.2: super(name, still, flow)
     * 1.7.10: super(name)
     */
    public FluidLiquidStarlight() {
        // IMPORTANT: The fluid is created here but must be registered BEFORE
        // passing it to BlockFluidClassic constructor
        // See RegistryBlocks.registerFluids() for proper registration order
        super("astralsorcery.liquidStarlight");

        // Set fluid properties
        this.setLuminosity(15); // Bright light
        this.setDensity(1001); // Slightly heavier than water (1000)
        this.setViscosity(300); // Medium viscosity
        this.setTemperature(120); // Cold fluid
        this.setUnlocalizedName("astralsorcery.liquidStarlight");

        // NOTE: In 1.7.10:
        // - Icons are registered separately via setIcons(stillIcon, flowingIcon)
        // - Rarity is handled by bucket items, not Fluid class
        // - Fill sounds are handled by bucket items
        // - These will be set during registration in RegistryBlocks
    }

}
