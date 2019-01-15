package basiccomponents.common.tileentity;

import basiccomponents.common.BasicComponents;
import com.google.common.io.ByteArrayDataInput;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import universalelectricity.core.block.IElectricityStorage;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.IElectricityNetwork;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.core.item.IItemElectric;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityStorage;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

;

public class TileEntityBatteryBox extends TileEntityElectricityStorage implements IElectricityStorage, IPacketReceiver, ISidedInventory {

	private ItemStack[] containingItems = new ItemStack[2];
	public final Set playersUsing = new HashSet();

	public void updateEntity() {
		super.updateEntity();
		if (!this.isDisabled() && !super.worldObj.isRemote) {
			this.setJoules(this.getJoules() - ElectricItemHelper.chargeItem(this.containingItems[0], this.getJoules(), this.getVoltage()));
			this.setJoules(this.getJoules() + ElectricItemHelper.dechargeItem(this.containingItems[1], this.getMaxJoules() - this.getJoules(), this.getVoltage()));
			ForgeDirection outputDirection = ForgeDirection.getOrientation(this.getBlockMetadata() - 4 + 2);
			TileEntity inputTile = VectorHelper.getConnectorFromSide(super.worldObj, new Vector3(this), outputDirection.getOpposite());
			TileEntity outputTile = VectorHelper.getConnectorFromSide(super.worldObj, new Vector3(this), outputDirection);
			IElectricityNetwork inputNetwork = ElectricityNetworkHelper.getNetworkFromTileEntity(inputTile, outputDirection.getOpposite());
			IElectricityNetwork outputNetwork = ElectricityNetworkHelper.getNetworkFromTileEntity(outputTile, outputDirection);
			if (outputNetwork != null && inputNetwork != outputNetwork) {
				double outputWatts = Math.min(outputNetwork.getRequest(this).getWatts(), Math.min(this.getJoules(), 10000.0D));
				if (this.getJoules() > 0.0D && outputWatts > 0.0D) {
					outputNetwork.startProducing(this, outputWatts / this.getVoltage(), this.getVoltage());
					this.setJoules(this.getJoules() - outputWatts);
				} else {
					outputNetwork.stopProducing(this);
				}
			}
		}

		this.setJoules(this.getJoules() - 5.0E-5D);
		if (!super.worldObj.isRemote && super.ticks % 3L == 0L) {
			Iterator i$ = this.playersUsing.iterator();

			while (i$.hasNext()) {
				EntityPlayer player = (EntityPlayer) i$.next();
				PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket(), (Player) player);
			}
		}

	}

	public boolean canConnect(ForgeDirection direction) {
		return direction == ForgeDirection.getOrientation(this.getBlockMetadata() - 4 + 2) || direction == ForgeDirection.getOrientation(this.getBlockMetadata() - 4 + 2).getOpposite();
	}

	protected EnumSet getConsumingSides() {
		return EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata() - 4 + 2).getOpposite());
	}

	public Packet getDescriptionPacket() {
		return PacketManager.getPacket(BasicComponents.CHANNEL, this, this.getJoules(), super.disabledTicks);
	}

	public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) {
		try {
			this.setJoules(dataStream.readDouble());
			super.disabledTicks = dataStream.readInt();
		} catch (Exception var7) {
			var7.printStackTrace();
		}

	}

	public void openChest() {
	}

	public void closeChest() {
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.containingItems.length) {
				this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}

	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
			if (this.containingItems[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.containingItems[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		par1NBTTagCompound.setTag("Items", var2);
	}

	public int getSizeInventory() {
		return this.containingItems.length;
	}

	public ItemStack getStackInSlot(int par1) {
		return this.containingItems[par1];
	}

	public ItemStack decrStackSize(int par1, int par2) {
		if (this.containingItems[par1] != null) {
			ItemStack var3;
			if (this.containingItems[par1].stackSize <= par2) {
				var3 = this.containingItems[par1];
				this.containingItems[par1] = null;
				return var3;
			} else {
				var3 = this.containingItems[par1].splitStack(par2);
				if (this.containingItems[par1].stackSize == 0) {
					this.containingItems[par1] = null;
				}

				return var3;
			}
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.containingItems[par1] != null) {
			ItemStack var2 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var2;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		this.containingItems[par1] = par2ItemStack;
		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}

	}

	public String getInvName() {
		return LanguageRegistry.instance().getStringLocalization("tile.basiccomponents:bcMachine.1.name");
	}

	public int getInventoryStackLimit() {
		return 1;
	}

	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
		return super.worldObj.getBlockTileEntity(super.xCoord, super.yCoord, super.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double) super.xCoord + 0.5D, (double) super.yCoord + 0.5D, (double) super.zCoord + 0.5D) <= 64.0D;
	}

	public double getMaxJoules() {
		return 5000000.0D;
	}

	public boolean isInvNameLocalized() {
		return true;
	}

	public boolean isStackValidForSlot(int slotID, ItemStack itemstack) {
		return itemstack.getItem() instanceof IItemElectric;
	}

	public int[] getAccessibleSlotsFromSide(int slotID) {
		return new int[]{0, 1};
	}

	public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
		if (this.isStackValidForSlot(slotID, itemstack)) {
			if (slotID == 0) {
				return ((IItemElectric) itemstack.getItem()).getReceiveRequest(itemstack).getWatts() > 0.0D;
			}

			if (slotID == 1) {
				return ((IItemElectric) itemstack.getItem()).getProvideRequest(itemstack).getWatts() > 0.0D;
			}
		}

		return false;
	}

	public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
		if (this.isStackValidForSlot(slotID, itemstack)) {
			if (slotID == 0) {
				return ((IItemElectric) itemstack.getItem()).getReceiveRequest(itemstack).getWatts() <= 0.0D;
			}

			if (slotID == 1) {
				return ((IItemElectric) itemstack.getItem()).getProvideRequest(itemstack).getWatts() <= 0.0D;
			}
		}

		return false;
	}
}
