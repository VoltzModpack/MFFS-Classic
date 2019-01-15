package mffs.container;

import mffs.base.ContainerBase;
import mffs.slot.SlotBase;
import mffs.slot.SlotCard;
import mffs.tileentity.TileEntityForceFieldProjector;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerForceFieldProjector extends ContainerBase {

	public ContainerForceFieldProjector(EntityPlayer player, TileEntityForceFieldProjector tileEntity) {
		super(tileEntity);
		this.addSlotToContainer(new SlotCard(tileEntity, 0, 10, 89));
		this.addSlotToContainer(new SlotCard(tileEntity, 1, 28, 89));
		this.addSlotToContainer(new SlotBase(tileEntity, 2, 118, 45));
		int i = 3;

		int xSlot;
		int ySlot;
		for (xSlot = 0; xSlot < 4; ++xSlot) {
			for (ySlot = 0; ySlot < 4; ++ySlot) {
				if ((xSlot != 1 || ySlot != 1) && (xSlot != 2 || ySlot != 2) && (xSlot != 1 || ySlot != 2) && (xSlot != 2 || ySlot != 1)) {
					this.addSlotToContainer(new SlotBase(tileEntity, i, 91 + 18 * xSlot, 18 + 18 * ySlot));
					++i;
				}
			}
		}

		for (xSlot = 0; xSlot < 3; ++xSlot) {
			for (ySlot = 0; ySlot < 2; ++ySlot) {
				this.addSlotToContainer(new SlotBase(tileEntity, i, 19 + 18 * xSlot, 36 + 18 * ySlot));
				++i;
			}
		}

		this.addPlayerInventory(player);
	}
}
