package mffs.event;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;
import mffs.DelayedEvent;
import mffs.IDelayedEventHandler;
import mffs.api.ISpecialForceManipulation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class BlockNotifyDelayedEvent extends DelayedEvent {

	private World world;
	private Vector3 position;

	public BlockNotifyDelayedEvent(IDelayedEventHandler handler, int ticks, World world, Vector3 position) {
		super(handler, ticks);
		this.world = world;
		this.position = position;
	}

	protected void onEvent() {
		if (!this.world.isRemote) {
			this.world.notifyBlocksOfNeighborChange(this.position.intX(), this.position.intY(), this.position.intZ(), this.position.getBlock(this.world));
			TileEntity newTile = this.position.getTileEntity(this.world);
			if (newTile != null) {
				if (newTile instanceof ISpecialForceManipulation) {
					((ISpecialForceManipulation) newTile).postMove();
				}

				if (Loader.isModLoaded("BuildCraft|Factory")) {
					try {
						Class clazz = Class.forName("buildcraft.factory.TileQuarry");
						if (clazz == newTile.getClass()) {
							ReflectionHelper.setPrivateValue(clazz, newTile, true, new String[]{"isAlive"});
						}
					} catch (Exception var3) {
						var3.printStackTrace();
					}
				}
			}
		}
	}

}
