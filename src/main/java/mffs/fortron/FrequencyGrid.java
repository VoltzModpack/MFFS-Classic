package mffs.fortron;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import icbm.api.IBlockFrequency;
import mffs.api.fortron.IFortronFrequency;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FrequencyGrid {

	private static FrequencyGrid CLIENT_INSTANCE = new FrequencyGrid();
	private static FrequencyGrid SERVER_INSTANCE = new FrequencyGrid();
	private final Set frequencyGrid = new HashSet();

	public void register(IBlockFrequency tileEntity) {
		try {
			Iterator it = this.frequencyGrid.iterator();

			while (it.hasNext()) {
				IBlockFrequency frequency = (IBlockFrequency) it.next();
				if (frequency == null) {
					it.remove();
				} else if (((TileEntity) frequency).isInvalid()) {
					it.remove();
				} else if ((new Vector3((TileEntity) frequency)).equals(new Vector3((TileEntity) tileEntity))) {
					it.remove();
				}
			}
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		this.frequencyGrid.add(tileEntity);
	}

	public void unregister(IBlockFrequency tileEntity) {
		this.frequencyGrid.remove(tileEntity);
		this.cleanUp();
	}

	public Set get() {
		return this.frequencyGrid;
	}

	public Set get(int frequency) {
		Set set = new HashSet();
		Iterator i$ = this.get().iterator();

		while (i$.hasNext()) {
			IBlockFrequency tile = (IBlockFrequency) i$.next();
			if (tile != null && !((TileEntity) tile).isInvalid() && tile.getFrequency() == frequency) {
				set.add(tile);
			}
		}

		return set;
	}

	public void cleanUp() {
		try {
			Iterator it = this.frequencyGrid.iterator();

			while (it.hasNext()) {
				IBlockFrequency frequency = (IBlockFrequency) it.next();
				if (frequency == null) {
					it.remove();
				} else if (((TileEntity) frequency).isInvalid()) {
					it.remove();
				} else if (((TileEntity) frequency).worldObj.getBlockTileEntity(((TileEntity) frequency).xCoord, ((TileEntity) frequency).yCoord, ((TileEntity) frequency).zCoord) != (TileEntity) frequency) {
					it.remove();
				}
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}

	public Set get(World world, Vector3 position, int radius, int frequency) {
		Set set = new HashSet();
		Iterator i$ = this.get(frequency).iterator();

		while (i$.hasNext()) {
			IBlockFrequency tileEntity = (IBlockFrequency) i$.next();
			if (((TileEntity) tileEntity).worldObj == world && Vector3.distance(new Vector3((TileEntity) tileEntity), position) <= (double) radius) {
				set.add(tileEntity);
			}
		}

		return set;
	}

	public Set getFortronTiles(World world, Vector3 position, int radius, int frequency) {
		Set set = new HashSet();
		Iterator i$ = this.get(frequency).iterator();

		while (i$.hasNext()) {
			IBlockFrequency tileEntity = (IBlockFrequency) i$.next();
			if (((TileEntity) tileEntity).worldObj == world && tileEntity instanceof IFortronFrequency && Vector3.distance(new Vector3((TileEntity) tileEntity), position) <= (double) radius) {
				set.add((IFortronFrequency) tileEntity);
			}
		}

		return set;
	}

	public static void reinitiate() {
		CLIENT_INSTANCE = new FrequencyGrid();
		SERVER_INSTANCE = new FrequencyGrid();
	}

	public static FrequencyGrid instance() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? SERVER_INSTANCE : CLIENT_INSTANCE;
	}
}
