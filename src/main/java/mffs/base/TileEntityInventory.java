package mffs.base;

import com.google.common.io.ByteArrayDataInput;
import dan200.computer.api.IComputerAccess;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.network.PacketManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TileEntityInventory extends TileEntityBase implements IInventory {

	protected ItemStack[] inventory = new ItemStack[this.getSizeInventory()];

	public List getPacketUpdate() {
		List objects = new ArrayList();
		objects.addAll(super.getPacketUpdate());
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		objects.add(nbt);
		return objects;
	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		super.onReceivePacket(packetID, dataStream);
		if (super.worldObj.isRemote && (packetID == TileEntityBase.TilePacketType.DESCRIPTION.ordinal() || packetID == TileEntityBase.TilePacketType.INVENTORY.ordinal())) {
			this.readFromNBT(PacketManager.readNBTTagCompound(dataStream));
		}

	}

	public void sendInventoryToClients() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		PacketManager.sendPacketToClients(PacketManager.getPacket("MFFS", this, TileEntityBase.TilePacketType.INVENTORY.ordinal(), nbt));
	}

	public ItemStack getStackInSlot(int i) {
		return this.inventory[i];
	}

	public String getInvName() {
		return this.getBlockType().getLocalizedName();
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.inventory[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.inventory[i] != null) {
			ItemStack itemstack1;
			if (this.inventory[i].stackSize <= j) {
				itemstack1 = this.inventory[i];
				this.inventory[i] = null;
				return itemstack1;
			} else {
				itemstack1 = this.inventory[i].splitStack(j);
				if (this.inventory[i].stackSize == 0) {
					this.inventory[i] = null;
				}

				return itemstack1;
			}
		} else {
			return null;
		}
	}

	public void openChest() {
	}

	public void closeChest() {
	}

	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord) == this;
	}

	public ItemStack getStackInSlotOnClosing(int slotID) {
		if (this.inventory[slotID] != null) {
			ItemStack itemstack = this.inventory[slotID];
			this.inventory[slotID] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isInvNameLocalized() {
		return true;
	}

	public boolean isStackValidForSlot(int slotID, ItemStack itemStack) {
		return true;
	}

	public boolean canIncreaseStack(int slotID, ItemStack itemStack) {
		if (this.getStackInSlot(slotID) == null) {
			return true;
		} else {
			return this.getStackInSlot(slotID).stackSize + 1 <= 64 ? this.getStackInSlot(slotID).isItemEqual(itemStack) : false;
		}
	}

	public void incrStackSize(int slot, ItemStack itemStack) {
		if (this.getStackInSlot(slot) == null) {
			this.setInventorySlotContents(slot, itemStack.copy());
		} else if (this.getStackInSlot(slot).isItemEqual(itemStack)) {
			++this.getStackInSlot(slot).stackSize;
		}

	}

	public Set getCards() {
		Set cards = new HashSet();
		cards.add(this.getStackInSlot(0));
		return cards;
	}

	public ItemStack tryPlaceInPosition(ItemStack itemStack, Vector3 position, ForgeDirection dir) {
		TileEntity tileEntity = position.getTileEntity(super.worldObj);
		ForgeDirection direction = dir.getOpposite();
		if (tileEntity != null && itemStack != null) {
			if (tileEntity instanceof TileEntityMulti) {
				Vector3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;
				if (mainBlockPosition != null && !(mainBlockPosition.getTileEntity(super.worldObj) instanceof TileEntityMulti)) {
					return this.tryPlaceInPosition(itemStack, mainBlockPosition, direction);
				}
			} else {
				int i;
				int i;
				if (tileEntity instanceof TileEntityChest) {
					TileEntityChest[] chests = new TileEntityChest[]{(TileEntityChest) tileEntity, null};

					for (i = 2; i < 6; ++i) {
						ForgeDirection searchDirection = ForgeDirection.getOrientation(i);
						Vector3 searchPosition = position.clone();
						searchPosition.modifyPositionFromSide(searchDirection);
						if (searchPosition.getTileEntity(super.worldObj) != null && searchPosition.getTileEntity(super.worldObj).getClass() == chests[0].getClass()) {
							chests[1] = (TileEntityChest) searchPosition.getTileEntity(super.worldObj);
							break;
						}
					}

					TileEntityChest[] arr$ = chests;
					i = chests.length;

					for (int i$ = 0; i$ < i; ++i$) {
						TileEntityChest chest = arr$[i$];
						if (chest != null) {
							for (int i = 0; i < chest.getSizeInventory(); ++i) {
								itemStack = this.addStackToInventory(i, chest, itemStack);
								if (itemStack == null) {
									return null;
								}
							}
						}
					}
				} else if (tileEntity instanceof ISidedInventory) {
					ISidedInventory inventory = (ISidedInventory) tileEntity;
					int[] slots = inventory.getAccessibleSlotsFromSide(direction.ordinal());

					for (i = 0; i < slots.length; ++i) {
						if (inventory.canInsertItem(slots[i], itemStack, direction.ordinal())) {
							itemStack = this.addStackToInventory(slots[i], inventory, itemStack);
						}

						if (itemStack == null) {
							return null;
						}
					}
				} else if (tileEntity instanceof IInventory) {
					IInventory inventory = (IInventory) tileEntity;

					for (i = 0; i < inventory.getSizeInventory(); ++i) {
						itemStack = this.addStackToInventory(i, inventory, itemStack);
						if (itemStack == null) {
							return null;
						}
					}
				}
			}
		}

		return itemStack.stackSize <= 0 ? null : itemStack;
	}

	public ItemStack addStackToInventory(int slotIndex, IInventory inventory, ItemStack itemStack) {
		if (inventory.getSizeInventory() > slotIndex) {
			ItemStack stackInInventory = inventory.getStackInSlot(slotIndex);
			if (stackInInventory == null) {
				inventory.setInventorySlotContents(slotIndex, itemStack);
				if (inventory.getStackInSlot(slotIndex) == null) {
					return itemStack;
				}

				return null;
			}

			if (stackInInventory.isItemEqual(itemStack) && stackInInventory.isStackable()) {
				stackInInventory = stackInInventory.copy();
				int stackLim = Math.min(inventory.getInventoryStackLimit(), itemStack.getMaxStackSize());
				int rejectedAmount = Math.max(stackInInventory.stackSize + itemStack.stackSize - stackLim, 0);
				stackInInventory.stackSize = Math.min(Math.max(stackInInventory.stackSize + itemStack.stackSize - rejectedAmount, 0), inventory.getInventoryStackLimit());
				itemStack.stackSize = rejectedAmount;
				inventory.setInventorySlotContents(slotIndex, stackInInventory);
			}
		}

		return itemStack.stackSize <= 0 ? null : itemStack;
	}

	public boolean mergeIntoInventory(ItemStack itemStack) {
		if (!super.worldObj.isRemote) {
			ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				ForgeDirection direction = arr$[i$];
				if (itemStack != null) {
					itemStack = this.tryPlaceInPosition(itemStack, (new Vector3(this)).modifyPositionFromSide(direction), direction);
				}
			}

			if (itemStack != null) {
				super.worldObj.spawnEntityInWorld(new EntityItem(super.worldObj, (double) super.xCoord + 0.5D, (double) (super.yCoord + 1), (double) super.zCoord + 0.5D, itemStack));
			}
		}

		return false;
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList nbtTagList = nbttagcompound.getTagList("Items");
		this.inventory = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbtTagList.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtTagList.tagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.inventory.length) {
				this.inventory[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList nbtTagList = new NBTTagList();

		for (int i = 0; i < this.inventory.length; ++i) {
			if (this.inventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.inventory[i].writeToNBT(nbttagcompound1);
				nbtTagList.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbtTagList);
	}

	public String getType() {
		return this.getInvName();
	}

	public String[] getMethodNames() {
		return new String[]{"isActivate", "setActivate"};
	}

	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
		switch (method) {
			case 0:
				return new Object[]{this.isActive()};
			case 1:
				this.setActive((Boolean) arguments[0]);
				return null;
			default:
				throw new Exception("Invalid method.");
		}
	}

	public boolean canAttachToSide(int side) {
		return true;
	}

	public void attach(IComputerAccess computer) {
	}

	public void detach(IComputerAccess computer) {
	}
}
