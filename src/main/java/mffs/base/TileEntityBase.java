package mffs.base;

import com.google.common.io.ByteArrayDataInput;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import dan200.computer.api.IPeripheral;
import mffs.ModularForceFieldSystem;
import mffs.api.IActivatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityDisableable;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TileEntityBase extends TileEntityDisableable implements IPacketReceiver, IRotatable, IRedstoneReceptor, IActivatable, IPeripheral {

	private boolean isActive = false;
	private boolean isRedstoneActive = false;
	public final List playersUsing = new ArrayList();
	public float animation = 0.0F;

	public List getPacketUpdate() {
		List objects = new ArrayList();
		objects.add(TileEntityBase.TilePacketType.DESCRIPTION.ordinal());
		objects.add(this.isActive);
		return objects;
	}

	public void updateEntity() {
		super.updateEntity();
		if (super.ticks % 4L == 0L && this.playersUsing.size() > 0) {
			Iterator i$ = this.playersUsing.iterator();

			while (i$.hasNext()) {
				EntityPlayer player = (EntityPlayer) i$.next();
				PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket(), (Player) player);
			}
		}

	}

	public Packet getDescriptionPacket() {
		return PacketManager.getPacket("MFFS", this, this.getPacketUpdate().toArray());
	}

	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) {
		try {
			this.onReceivePacket(dataStream.readInt(), dataStream);
		} catch (Exception var7) {
			ModularForceFieldSystem.LOGGER.severe(MessageFormat.format("Packet receiving failed: {0}", this.getClass().getSimpleName()));
			var7.printStackTrace();
		}

	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		if (packetID == TileEntityBase.TilePacketType.DESCRIPTION.ordinal()) {
			boolean prevActive = this.isActive;
			this.isActive = dataStream.readBoolean();
			if (prevActive != this.isActive) {
				super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
			}
		} else if (packetID == TileEntityBase.TilePacketType.TOGGLE_ACTIVATION.ordinal()) {
			this.isRedstoneActive = !this.isRedstoneActive;
			if (this.isRedstoneActive) {
				this.setActive(true);
			} else {
				this.setActive(false);
			}
		}

	}

	public boolean isPoweredByRedstone() {
		return super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord, super.zCoord);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.isActive = nbttagcompound.getBoolean("isActive");
		this.isRedstoneActive = nbttagcompound.getBoolean("isRedstoneActive");
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setBoolean("isActive", this.isActive);
		nbttagcompound.setBoolean("isRedstoneActive", this.isRedstoneActive);
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean flag) {
		this.isActive = flag;
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
	}

	public ForgeDirection getDirection(IBlockAccess world, int x, int y, int z) {
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	public void setDirection(World world, int x, int y, int z, ForgeDirection facingDirection) {
		super.worldObj.setBlockMetadataWithNotify(super.xCoord, super.yCoord, super.zCoord, facingDirection.ordinal(), 3);
	}

	public void onPowerOn() {
		this.setActive(true);
	}

	public void onPowerOff() {
		if (!this.isRedstoneActive && !super.worldObj.isRemote) {
			this.setActive(false);
		}

	}

	public static enum TilePacketType {
		NONE,
		DESCRIPTION,
		FREQUENCY,
		TOGGLE_ACTIVATION,
		TOGGLE_MODE,
		INVENTORY,
		STRING,
		FXS,
		TOGGLE_MODE_2,
		TOGGLE_MODE_3;
	}
}
