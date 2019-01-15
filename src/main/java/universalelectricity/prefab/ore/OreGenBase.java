package universalelectricity.prefab.ore;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

public abstract class OreGenBase {

	public String name;
	public String oreDictionaryName;
	public boolean shouldGenerate = false;
	public int blockIndexTexture;
	public ItemStack oreStack;
	public int oreID;
	public int oreMeta;
	public int harvestLevel;
	public String harvestTool;

	public OreGenBase(String name, String oreDiectionaryName, ItemStack stack, String harvestTool, int harvestLevel) {
		if (stack != null) {
			this.name = name;
			this.harvestTool = harvestTool;
			this.harvestLevel = harvestLevel;
			this.oreDictionaryName = oreDiectionaryName;
			this.oreStack = stack;
			this.oreID = stack.itemID;
			this.oreMeta = stack.getItemDamage();
			OreDictionary.registerOre(this.oreDictionaryName, stack);
			MinecraftForge.setBlockHarvestLevel(Block.blocksList[stack.itemID], stack.getItemDamage(), harvestTool, harvestLevel);
		} else {
			FMLLog.severe("ItemStack is null while registering ore generation!", new Object[0]);
		}

	}

	public OreGenBase enable(Configuration config) {
		this.shouldGenerate = shouldGenerateOre(config, this.name);
		return this;
	}

	private static boolean shouldGenerateOre(Configuration configuration, String oreName) {
		configuration.load();
		boolean shouldGenerate = configuration.get("Ore_Generation", "Generate " + oreName, true).getBoolean(true);
		configuration.save();
		return shouldGenerate;
	}

	public abstract void generate(World var1, Random var2, int var3, int var4);

	public abstract boolean isOreGeneratedInWorld(World var1, IChunkProvider var2);
}
