package basiccomponents.common.tileentity;

import basiccomponents.common.BasicComponents;
import net.minecraft.block.Block;
import universalelectricity.prefab.tile.TileEntityConductor;

public class TileEntityCopperWire extends TileEntityConductor {

	public static double RESISTANCE = 0.05D;
	public static double MAX_AMPS = 200.0D;

	public TileEntityCopperWire() {
		super.channel = BasicComponents.CHANNEL;
	}

	public double getResistance() {
		return RESISTANCE;
	}

	public double getCurrentCapcity() {
		return MAX_AMPS;
	}

	public void updateEntity() {
		super.updateEntity();
		if (this.getNetwork() != null && super.ticks % 20L == 0L && this.getNetwork().getProduced().amperes > this.getCurrentCapcity() && !super.worldObj.isRemote) {
			super.worldObj.setBlock(super.xCoord, super.yCoord, super.zCoord, Block.fire.blockID);
		}

	}
}
