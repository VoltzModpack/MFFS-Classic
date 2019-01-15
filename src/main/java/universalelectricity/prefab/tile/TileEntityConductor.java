package universalelectricity.prefab.tile;

import com.google.common.io.ByteArrayDataInput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import org.bouncycastle.util.Arrays;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IConnector;
import universalelectricity.core.block.INetworkProvider;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

public abstract class TileEntityConductor extends TileEntityAdvanced implements IConductor, IPacketReceiver {

	private IElectricityNetwork network;
	public boolean[] visuallyConnected = new boolean[]{false, false, false, false, false, false};
	public TileEntity[] connectedBlocks = new TileEntity[]{null, null, null, null, null, null};
	protected String channel = "";

	public void updateConnection(TileEntity tileEntity, ForgeDirection side) {
		if (!super.worldObj.isRemote) {
			if (tileEntity instanceof IConnector && ((IConnector) tileEntity).canConnect(side.getOpposite())) {
				this.connectedBlocks[side.ordinal()] = tileEntity;
				this.visuallyConnected[side.ordinal()] = true;
				if (tileEntity.getClass() == this.getClass() && tileEntity instanceof INetworkProvider) {
					this.getNetwork().mergeConnection(((INetworkProvider) tileEntity).getNetwork());
				}

				return;
			}

			if (this.connectedBlocks[side.ordinal()] != null) {
				this.getNetwork().stopProducing(this.connectedBlocks[side.ordinal()]);
				this.getNetwork().stopRequesting(this.connectedBlocks[side.ordinal()]);
			}

			this.connectedBlocks[side.ordinal()] = null;
			this.visuallyConnected[side.ordinal()] = false;
		}

	}

	public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) {
		if (super.worldObj.isRemote) {
			this.visuallyConnected[0] = dataStream.readBoolean();
			this.visuallyConnected[1] = dataStream.readBoolean();
			this.visuallyConnected[2] = dataStream.readBoolean();
			this.visuallyConnected[3] = dataStream.readBoolean();
			this.visuallyConnected[4] = dataStream.readBoolean();
			this.visuallyConnected[5] = dataStream.readBoolean();
		}

	}

	public void initiate() {
		this.updateAdjacentConnections();
	}

	public void invalidate() {
		if (!super.worldObj.isRemote) {
			this.getNetwork().splitNetwork(this);
		}

		super.invalidate();
	}

	public void updateEntity() {
		super.updateEntity();
		if (!super.worldObj.isRemote && super.ticks % 300L == 0L) {
			this.updateAdjacentConnections();
		}

	}

	public void updateAdjacentConnections() {
		if (super.worldObj != null && !super.worldObj.isRemote) {
			boolean[] previousConnections = (boolean[]) this.visuallyConnected.clone();

			for (byte i = 0; i < 6; ++i) {
				this.updateConnection(VectorHelper.getConnectorFromSide(super.worldObj, new Vector3(this), ForgeDirection.getOrientation(i)), ForgeDirection.getOrientation(i));
			}

			if (!Arrays.areEqual(previousConnections, this.visuallyConnected)) {
				super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
			}
		}

	}

	public Packet getDescriptionPacket() {
		return PacketManager.getPacket(this.channel, this, this.visuallyConnected[0], this.visuallyConnected[1], this.visuallyConnected[2], this.visuallyConnected[3], this.visuallyConnected[4], this.visuallyConnected[5]);
	}

	public IElectricityNetwork getNetwork() {
		if (this.network == null) {
			this.setNetwork(new ElectricityNetwork(new IConductor[]{this}));
		}

		return this.network;
	}

	public void setNetwork(IElectricityNetwork network) {
		this.network = network;
	}

	public TileEntity[] getAdjacentConnections() {
		return this.connectedBlocks;
	}

	public boolean canConnect(ForgeDirection direction) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB((double) super.xCoord, (double) super.yCoord, (double) super.zCoord, (double) (super.xCoord + 1), (double) (super.yCoord + 1), (double) (super.zCoord + 1));
	}
}
