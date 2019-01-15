package ic2.api.crops;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public interface ICropTile {

	short getID();

	void setID(short var1);

	byte getSize();

	void setSize(byte var1);

	byte getGrowth();

	void setGrowth(byte var1);

	byte getGain();

	void setGain(byte var1);

	byte getResistance();

	void setResistance(byte var1);

	byte getScanLevel();

	void setScanLevel(byte var1);

	NBTTagCompound getCustomData();

	int getNutrientStorage();

	void setNutrientStorage(int var1);

	int getHydrationStorage();

	void setHydrationStorage(int var1);

	int getWeedExStorage();

	void setWeedExStorage(int var1);

	byte getHumidity();

	byte getNutrients();

	byte getAirQuality();

	World getWorld();

	ChunkCoordinates getLocation();

	int getLightLevel();

	boolean pick(boolean var1);

	boolean harvest(boolean var1);

	void reset();

	void updateState();

	boolean isBlockBelow(Block var1);

	ItemStack generateSeeds(short var1, byte var2, byte var3, byte var4, byte var5);
}
