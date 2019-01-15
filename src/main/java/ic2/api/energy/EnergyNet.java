package ic2.api.energy;

import ic2.api.energy.tile.IEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.lang.reflect.Method;

public final class EnergyNet {

	Object energyNetInstance;
	private static Method EnergyNet_getForWorld;
	private static Method EnergyNet_addTileEntity;
	private static Method EnergyNet_removeTileEntity;
	private static Method EnergyNet_emitEnergyFrom;
	private static Method EnergyNet_getTotalEnergyConducted;
	private static Method EnergyNet_getTotalEnergyEmitted;
	private static Method EnergyNet_getTotalEnergySunken;

	public static EnergyNet getForWorld(World world) {
		try {
			if (EnergyNet_getForWorld == null) {
				EnergyNet_getForWorld = Class.forName(getPackage() + ".core.EnergyNet").getMethod("getForWorld", World.class);
			}

			return new EnergyNet(EnergyNet_getForWorld.invoke((Object) null, world));
		} catch (Exception var2) {
			throw new RuntimeException(var2);
		}
	}

	private EnergyNet(Object energyNetInstance) {
		this.energyNetInstance = energyNetInstance;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void addTileEntity(TileEntity addedTileEntity) {
		try {
			if (EnergyNet_addTileEntity == null) {
				EnergyNet_addTileEntity = Class.forName(getPackage() + ".core.EnergyNet").getMethod("addTileEntity", TileEntity.class);
			}

			EnergyNet_addTileEntity.invoke(this.energyNetInstance, addedTileEntity);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void removeTileEntity(TileEntity removedTileEntity) {
		try {
			if (EnergyNet_removeTileEntity == null) {
				EnergyNet_removeTileEntity = Class.forName(getPackage() + ".core.EnergyNet").getMethod("removeTileEntity", TileEntity.class);
			}

			EnergyNet_removeTileEntity.invoke(this.energyNetInstance, removedTileEntity);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public int emitEnergyFrom(IEnergySource energySource, int amount) {
		try {
			if (EnergyNet_emitEnergyFrom == null) {
				EnergyNet_emitEnergyFrom = Class.forName(getPackage() + ".core.EnergyNet").getMethod("emitEnergyFrom", IEnergySource.class, Integer.TYPE);
			}

			return (Integer) EnergyNet_emitEnergyFrom.invoke(this.energyNetInstance, energySource, amount);
		} catch (Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public long getTotalEnergyConducted(TileEntity tileEntity) {
		try {
			if (EnergyNet_getTotalEnergyConducted == null) {
				EnergyNet_getTotalEnergyConducted = Class.forName(getPackage() + ".core.EnergyNet").getMethod("getTotalEnergyConducted", TileEntity.class);
			}

			return (Long) EnergyNet_getTotalEnergyConducted.invoke(this.energyNetInstance, tileEntity);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public long getTotalEnergyEmitted(TileEntity tileEntity) {
		try {
			if (EnergyNet_getTotalEnergyEmitted == null) {
				EnergyNet_getTotalEnergyEmitted = Class.forName(getPackage() + ".core.EnergyNet").getMethod("getTotalEnergyEmitted", TileEntity.class);
			}

			return (Long) EnergyNet_getTotalEnergyEmitted.invoke(this.energyNetInstance, tileEntity);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public long getTotalEnergySunken(TileEntity tileEntity) {
		try {
			if (EnergyNet_getTotalEnergySunken == null) {
				EnergyNet_getTotalEnergySunken = Class.forName(getPackage() + ".core.EnergyNet").getMethod("getTotalEnergySunken", TileEntity.class);
			}

			return (Long) EnergyNet_getTotalEnergySunken.invoke(this.energyNetInstance, tileEntity);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	private static String getPackage() {
		Package pkg = EnergyNet.class.getPackage();
		if (pkg != null) {
			String packageName = pkg.getName();
			return packageName.substring(0, packageName.length() - ".api.energy".length());
		} else {
			return "ic2";
		}
	}
}
