package universalelectricity.prefab.multiblock;

import com.google.common.io.ByteArrayDataInput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

public class TileEntityMulti extends TileEntity implements IPacketReceiver {

	public Vector3 mainBlockPosition;
	public String channel;

	public TileEntityMulti() {
	}

	public TileEntityMulti(String channel) {
		this.channel = channel;
	}

	public void setMainBlock(Vector3 mainBlock) {
		this.mainBlockPosition = mainBlock;
		if (!super.worldObj.isRemote) {
			super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
		}

	}

	public Packet getDescriptionPacket() {
		if (this.mainBlockPosition == null) {
			return null;
		} else {
			if (this.channel == null || this.channel == "" && this.getBlockType() instanceof BlockMulti) {
				this.channel = ((BlockMulti) this.getBlockType()).channel;
			}

			return PacketManager.getPacket(this.channel, this, this.mainBlockPosition.intX(), this.mainBlockPosition.intY(), this.mainBlockPosition.intZ());
		}
	}

	public void onBlockRemoval() {
		if (this.mainBlockPosition != null) {
			TileEntity tileEntity = super.worldObj.getBlockTileEntity(this.mainBlockPosition.intX(), this.mainBlockPosition.intY(), this.mainBlockPosition.intZ());
			if (tileEntity != null && tileEntity instanceof IMultiBlock) {
				IMultiBlock mainBlock = (IMultiBlock) tileEntity;
				if (mainBlock != null) {
					mainBlock.onDestroy(this);
				}
			}
		}

	}

	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer) {
		if (this.mainBlockPosition != null) {
			TileEntity tileEntity = super.worldObj.getBlockTileEntity(this.mainBlockPosition.intX(), this.mainBlockPosition.intY(), this.mainBlockPosition.intZ());
			if (tileEntity != null && tileEntity instanceof IMultiBlock) {
				return ((IMultiBlock) tileEntity).onActivated(par5EntityPlayer);
			}
		}

		return false;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.mainBlockPosition = Vector3.readFromNBT(nbt.getCompoundTag("mainBlockPosition"));
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (this.mainBlockPosition != null) {
			nbt.setCompoundTag("mainBlockPosition", this.mainBlockPosition.writeToNBT(new NBTTagCompound()));
		}

	}

	public boolean canUpdate() {
		return false;
	}

	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) {
		try {
			this.mainBlockPosition = new Vector3((double) dataStream.readInt(), (double) dataStream.readInt(), (double) dataStream.readInt());
		} catch (Exception var7) {
			var7.printStackTrace();
		}

	}
}
