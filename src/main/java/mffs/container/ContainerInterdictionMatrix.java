package mffs.container;

import mffs.base.ContainerBase;
import mffs.slot.SlotBase;
import mffs.slot.SlotCard;
import mffs.tileentity.TileEntityInterdictionMatrix;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerInterdictionMatrix extends ContainerBase {

	public ContainerInterdictionMatrix(EntityPlayer player, TileEntityInterdictionMatrix tileEntity) {
		super(tileEntity);
		this.addSlotToContainer(new SlotCard(tileEntity, 0, 87, 89));
		this.addSlotToContainer(new SlotBase(tileEntity, 1, 69, 89));

		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 4; k++) {
				this.addSlotToContainer(new SlotBase(tileEntity, k + i * 4 + 2, 99 + k * 18, 31 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new SlotBase(tileEntity, i + 8 + 2, 9 + i * 18, 69));
		}

		this.addPlayerInventory(player);
	}

}
