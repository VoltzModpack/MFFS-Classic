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

		int var4;
		for (var4 = 0; var4 < 2; ++var4) {
			for (int var4 = 0; var4 < 4; ++var4) {
				this.addSlotToContainer(new SlotBase(tileEntity, var4 + var4 * 4 + 2, 99 + var4 * 18, 31 + var4 * 18));
			}
		}

		for (var4 = 0; var4 < 9; ++var4) {
			this.addSlotToContainer(new SlotBase(tileEntity, var4 + 8 + 2, 9 + var4 * 18, 69));
		}

		this.addPlayerInventory(player);
	}
}
