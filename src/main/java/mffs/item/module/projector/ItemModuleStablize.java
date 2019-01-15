package mffs.item.module.projector;

import calclavia.lib.CalculationHelper;
import mffs.ModularForceFieldSystem;
import mffs.api.Blacklist;
import mffs.api.IProjector;
import mffs.base.TileEntityBase;
import mffs.item.module.ItemModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquid;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.PacketManager;

import java.util.HashMap;
import java.util.Set;

public class ItemModuleStablize extends ItemModule {

	private int blockCount = 0;

	public ItemModuleStablize(int id) {
		super(id, "moduleStabilize");
		this.setMaxStackSize(1);
		this.setCost(20.0F);
	}

	public boolean onProject(IProjector projector, Set fields) {
		this.blockCount = 0;
		return false;
	}

	public int onProject(IProjector projector, Vector3 position) {
		int[] blockInfo = null;
		if (projector.getTicks() % 40L == 0L) {
			if (projector.getMode() instanceof ItemModeCustom) {
				HashMap fieldBlocks = ((ItemModeCustom) projector.getMode()).getFieldBlockMap(projector, projector.getModeStack());
				Vector3 fieldCenter = (new Vector3((TileEntity) projector)).add(projector.getTranslation());
				Vector3 relativePosition = position.clone().subtract(fieldCenter);
				CalculationHelper.rotateByAngle(relativePosition, (double) (-projector.getRotationYaw()), (double) (-projector.getRotationPitch()));
				blockInfo = (int[]) fieldBlocks.get(relativePosition.round());
			}

			for (int dir = 0; dir < 6; ++dir) {
				ForgeDirection direction = ForgeDirection.getOrientation(dir);
				TileEntity tileEntity = VectorHelper.getTileEntityFromSide(((TileEntity) projector).worldObj, new Vector3((TileEntity) projector), direction);
				if (tileEntity instanceof IInventory) {
					IInventory inventory = (IInventory) tileEntity;

					for (int i = 0; i < inventory.getSizeInventory(); ++i) {
						ItemStack checkStack = inventory.getStackInSlot(i);
						if (checkStack != null && checkStack.getItem() instanceof ItemBlock && (blockInfo == null || blockInfo[0] == ((ItemBlock) checkStack.getItem()).getBlockID())) {
							try {
								if (((TileEntity) projector).worldObj.canPlaceEntityOnSide(((ItemBlock) checkStack.getItem()).getBlockID(), position.intX(), position.intY(), position.intZ(), false, 0, (Entity) null, checkStack)) {
									int metadata = blockInfo != null ? blockInfo[1] : (checkStack.getHasSubtypes() ? checkStack.getItemDamage() : 0);
									Block block = blockInfo != null ? Block.blocksList[blockInfo[0]] : null;
									if (!Blacklist.stabilizationBlacklist.contains(block) && !(block instanceof BlockFluid) && !(block instanceof ILiquid)) {
										((ItemBlock) checkStack.getItem()).placeBlockAt(checkStack, (EntityPlayer) null, ((TileEntity) projector).worldObj, position.intX(), position.intY(), position.intZ(), 0, 0.0F, 0.0F, 0.0F, metadata);
										inventory.decrStackSize(i, 1);
										PacketManager.sendPacketToClients(PacketManager.getPacket("MFFS", (TileEntity) projector, TileEntityBase.TilePacketType.FXS.ordinal(), 1, position.intX(), position.intY(), position.intZ()), ((TileEntity) projector).worldObj);
										if (this.blockCount++ >= projector.getModuleCount(ModularForceFieldSystem.itemModuleSpeed, new int[0]) / 3) {
											return 2;
										}

										return 1;
									}

									return 1;
								}
							} catch (Exception var12) {
								var12.printStackTrace();
							}
						}
					}
				}
			}
		}

		return 1;
	}
}
