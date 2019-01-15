package universalelectricity.prefab.flag;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import universalelectricity.core.vector.Vector3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModFlag extends FlagBase {

	private final List flagWorlds = new ArrayList();

	public ModFlag(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt != null) {
			Iterator dimensions = nbt.getTags().iterator();

			while (dimensions.hasNext()) {
				NBTTagCompound dimensionCompound = (NBTTagCompound) dimensions.next();

				try {
					int dimensionID = Integer.parseInt(dimensionCompound.getName().replace("dim_", ""));
					World world = DimensionManager.getWorld(dimensionID);
					FlagWorld flagWorld = new FlagWorld(world);
					flagWorld.readFromNBT(dimensionCompound);
					this.flagWorlds.add(flagWorld);
				} catch (Exception var7) {
					System.out.println("Mod Flag: Failed to read dimension data: " + dimensionCompound.getName());
					var7.printStackTrace();
				}
			}
		}

	}

	public void writeToNBT(NBTTagCompound nbt) {
		if (nbt != null) {
			Iterator i$ = this.flagWorlds.iterator();

			while (i$.hasNext()) {
				FlagWorld worldData = (FlagWorld) i$.next();

				try {
					nbt.setTag("dim_" + worldData.world.provider.dimensionId, worldData.getNBT());
				} catch (Exception var5) {
					System.out.println("Mod Flag: Failed to save world flag data: " + worldData.world);
					var5.printStackTrace();
				}
			}
		}

	}

	public FlagWorld getFlagWorld(World world) {
		FlagWorld worldData = null;
		if (world != null) {
			Iterator i$ = this.flagWorlds.iterator();

			while (i$.hasNext()) {
				FlagWorld data = (FlagWorld) i$.next();
				if (data.world != null && data.world.provider != null && data.world.provider.dimensionId == world.provider.dimensionId) {
					worldData = data;
					break;
				}
			}

			if (worldData == null) {
				worldData = new FlagWorld(world);
				this.flagWorlds.add(worldData);
			}
		}

		return worldData;
	}

	public boolean containsValue(World world, String flagName, String checkValue, Vector3 position) {
		return this.getFlagWorld(world).containsValue(flagName, checkValue, position);
	}

	public List getFlagWorlds() {
		return this.flagWorlds;
	}
}
