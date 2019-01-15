package ic2.api.crops;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

public abstract class Crops {

	public static Crops instance;

	public abstract void addBiomeBonus(BiomeGenBase var1, int var2, int var3);

	public abstract int getHumidityBiomeBonus(BiomeGenBase var1);

	public abstract int getNutrientBiomeBonus(BiomeGenBase var1);

	public abstract CropCard[] getCropList();

	public abstract short registerCrop(CropCard var1);

	public abstract boolean registerCrop(CropCard var1, int var2);

	public abstract boolean registerBaseSeed(ItemStack var1, int var2, int var3, int var4, int var5, int var6);

	public abstract BaseSeed getBaseSeed(ItemStack var1);

	@SideOnly(Side.CLIENT)
	public abstract void startSpriteRegistration(IconRegister var1);

	public abstract int getIdFor(CropCard var1);
}
