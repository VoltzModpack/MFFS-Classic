package mffs.event;

import mffs.DelayedEvent;
import mffs.IDelayedEventHandler;
import mffs.ManipulatorHelper;
import mffs.api.ISpecialForceManipulation;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class BlockPreMoveDelayedEvent extends DelayedEvent {

	private World world;
	private Vector3 position;
	private Vector3 newPosition;

	public BlockPreMoveDelayedEvent(IDelayedEventHandler handler, int ticks, World world, Vector3 position, Vector3 newPosition) {
		super(handler, ticks);
		this.world = world;
		this.position = position;
		this.newPosition = newPosition;
	}

	protected void onEvent() {
		if (!this.world.isRemote) {
			TileEntity tileEntity = this.position.getTileEntity(this.world);
			if (tileEntity instanceof ISpecialForceManipulation) {
				((ISpecialForceManipulation) tileEntity).move(this.newPosition.intX(), this.newPosition.intY(), this.newPosition.intZ());
			}

			Block block = this.position.getBlock(this.world);
			int blockMetadata = this.position.getBlockMetadata(this.world);
			NBTTagCompound tileData = new NBTTagCompound();
			if (tileEntity != null) {
				tileEntity.writeToNBT(tileData);
			}

			ManipulatorHelper.setBlockSneaky(this.world, this.position, 0, 0, (TileEntity) null);
			super.handler.getQuedDelayedEvents().add(new BlockPostMoveDelayedEvent(super.handler, 0, this.world, this.position, this.newPosition, block, blockMetadata, tileEntity, tileData));
		}

	}
}
