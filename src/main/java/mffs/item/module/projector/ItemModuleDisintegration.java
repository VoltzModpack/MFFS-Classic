package mffs.item.module.projector;

import mffs.IDelayedEventHandler;
import mffs.MFFSHelper;
import mffs.ModularForceFieldSystem;
import mffs.api.Blacklist;
import mffs.api.IProjector;
import mffs.base.TileEntityBase;
import mffs.base.TileEntityInventory;
import mffs.event.BlockDropDelayedEvent;
import mffs.event.BlockInventoryDropDelayedEvent;
import mffs.item.module.ItemModule;
import mffs.tileentity.TileEntityForceFieldProjector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.ILiquid;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ItemModuleDisintegration extends ItemModule {

	private int blockCount = 0;

	public ItemModuleDisintegration(int id) {
		super(id, "moduleDisintegration");
		this.setMaxStackSize(1);
		this.setCost(20.0F);
	}

	public boolean onProject(IProjector projector, Set fields) {
		this.blockCount = 0;
		return false;
	}

	public int onProject(IProjector projector, Vector3 position) {
		if (projector.getTicks() % 40L == 0L) {
			TileEntity tileEntity = (TileEntity) projector;
			int blockID = position.getBlockID(tileEntity.worldObj);
			Block block = Block.blocksList[blockID];
			if (block != null) {
				if (projector.getModuleCount(ModularForceFieldSystem.itemModuleCamouflage, new int[0]) > 0) {
					int blockMetadata = position.getBlockMetadata(tileEntity.worldObj);
					Set filterStacks = new HashSet();
					int[] arr$ = projector.getModuleSlots();
					int len$ = arr$.length;

					for (int i$ = 0; i$ < len$; ++i$) {
						int i = arr$[i$];
						ItemStack checkStack = projector.getStackInSlot(i);
						Block filterBlock = MFFSHelper.getFilterBlock(checkStack);
						if (filterBlock != null) {
							filterStacks.add(checkStack);
						}
					}

					boolean contains = false;
					Iterator i$ = filterStacks.iterator();

					while (i$.hasNext()) {
						ItemStack filterStack = (ItemStack) i$.next();
						if (filterStack.isItemEqual(new ItemStack(blockID, 1, blockMetadata))) {
							contains = true;
							break;
						}
					}

					if (!contains) {
						return 1;
					}
				}

				if (!Blacklist.disintegrationBlacklist.contains(block) && !(block instanceof BlockFluid) && !(block instanceof ILiquid)) {
					PacketManager.sendPacketToClients(PacketManager.getPacket("MFFS", (TileEntity) projector, TileEntityBase.TilePacketType.FXS.ordinal(), 2, position.intX(), position.intY(), position.intZ()), ((TileEntity) projector).worldObj);
					if (projector.getModuleCount(ModularForceFieldSystem.itemModuleCollection, new int[0]) > 0) {
						((TileEntityForceFieldProjector) projector).getDelayedEvents().add(new BlockInventoryDropDelayedEvent((IDelayedEventHandler) projector, 39, block, tileEntity.worldObj, position, (TileEntityInventory) projector));
					} else {
						((TileEntityForceFieldProjector) projector).getDelayedEvents().add(new BlockDropDelayedEvent((IDelayedEventHandler) projector, 39, block, tileEntity.worldObj, position));
					}

					if (this.blockCount++ >= projector.getModuleCount(ModularForceFieldSystem.itemModuleSpeed, new int[0]) / 3) {
						return 2;
					}

					return 1;
				}

				return 1;
			}
		}

		return 1;
	}
}
