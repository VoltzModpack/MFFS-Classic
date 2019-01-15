package mffs.tileentity;

import com.google.common.io.ByteArrayDataInput;
import mffs.MFFSHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;

public class TileEntityForceField extends TileEntityAdvanced implements IPacketReceiver {

	private Vector3 projector = null;
	public ItemStack camoStack = null;

	public boolean canUpdate() {
		return false;
	}

	public Packet getDescriptionPacket() {
		if (this.getProjector() != null) {
			int itemID = -1;
			int itemMetadata = -1;
			if (this.camoStack != null) {
				itemID = this.camoStack.itemID;
				itemMetadata = this.camoStack.getItemDamage();
			}

			return PacketManager.getPacket("MFFS", this, this.projector.intX(), this.projector.intY(), this.projector.intZ(), itemID, itemMetadata);
		} else {
			return null;
		}
	}

	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) {
		try {
			this.setProjector(new Vector3((double) dataStream.readInt(), (double) dataStream.readInt(), (double) dataStream.readInt()));
			super.worldObj.markBlockForRenderUpdate(super.xCoord, super.yCoord, super.zCoord);
			this.camoStack = null;
			int itemID = dataStream.readInt();
			int itemMetadata = dataStream.readInt();
			if (itemID != -1 && itemMetadata != -1) {
				this.camoStack = new ItemStack(Block.blocksList[itemID], 1, itemMetadata);
			}
		} catch (Exception var8) {
			var8.printStackTrace();
		}

	}

	public void setProjector(Vector3 position) {
		this.projector = position;
		if (!super.worldObj.isRemote) {
			this.refreshCamoBlock();
		}

	}

	public TileEntityForceFieldProjector getProjector() {
		if (this.getProjectorSafe() != null) {
			return this.getProjectorSafe();
		} else {
			if (!super.worldObj.isRemote) {
				super.worldObj.setBlock(super.xCoord, super.yCoord, super.zCoord, 0);
			}

			return null;
		}
	}

	public TileEntityForceFieldProjector getProjectorSafe() {
		return this.projector == null || !(this.projector.getTileEntity(super.worldObj) instanceof TileEntityForceFieldProjector) || !super.worldObj.isRemote && !((TileEntityForceFieldProjector) this.projector.getTileEntity(super.worldObj)).getCalculatedField().contains(new Vector3(this)) ? null : (TileEntityForceFieldProjector) this.projector.getTileEntity(super.worldObj);
	}

	public void refreshCamoBlock() {
		if (this.getProjectorSafe() != null) {
			this.camoStack = MFFSHelper.getCamoBlock(this.getProjector(), new Vector3(this));
		}

	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.projector = Vector3.readFromNBT(nbt.getCompoundTag("projector"));
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (this.getProjector() != null) {
			nbt.setCompoundTag("projector", this.projector.writeToNBT(new NBTTagCompound()));
		}

	}
}
