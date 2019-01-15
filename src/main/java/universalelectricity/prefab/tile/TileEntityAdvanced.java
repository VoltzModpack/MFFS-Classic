package universalelectricity.prefab.tile;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityAdvanced extends TileEntity {

	protected long ticks = 0L;

	public void updateEntity() {
		if (this.ticks == 0L) {
			this.initiate();
		}

		if (this.ticks >= Long.MAX_VALUE) {
			this.ticks = 1L;
		}

		++this.ticks;
	}

	public void initiate() {
	}

	public int getBlockMetadata() {
		if (super.blockMetadata == -1) {
			super.blockMetadata = super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord);
		}

		return super.blockMetadata;
	}

	public Block getBlockType() {
		if (super.blockType == null) {
			super.blockType = Block.blocksList[super.worldObj.getBlockId(super.xCoord, super.yCoord, super.zCoord)];
		}

		return super.blockType;
	}
}
