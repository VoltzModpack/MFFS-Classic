package mffs.event;

import mffs.IDelayedEventHandler;
import mffs.base.TileEntityInventory;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import java.util.ArrayList;
import java.util.Iterator;

public class BlockInventoryDropDelayedEvent extends BlockDropDelayedEvent {

	private TileEntityInventory projector;

	public BlockInventoryDropDelayedEvent(IDelayedEventHandler handler, int ticks, Block block, World world, Vector3 position, TileEntityInventory projector) {
		super(handler, ticks, block, world, position);
		this.projector = projector;
	}

	protected void onEvent() {
		if (super.position.getBlockID(super.world) == super.block.blockID) {
			ArrayList itemStacks = super.block.getBlockDropped(super.world, super.position.intX(), super.position.intY(), super.position.intZ(), super.position.getBlockMetadata(super.world), 0);
			Iterator i$ = itemStacks.iterator();

			while (i$.hasNext()) {
				ItemStack itemStack = (ItemStack) i$.next();
				this.projector.mergeIntoInventory(itemStack);
			}

			super.position.setBlock(super.world, 0);
		}

	}
}
