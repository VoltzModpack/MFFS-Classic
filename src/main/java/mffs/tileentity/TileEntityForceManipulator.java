package mffs.tileentity;

import com.google.common.io.ByteArrayDataInput;
import dan200.computer.api.IComputerAccess;
import mffs.ModularForceFieldSystem;
import mffs.Settings;
import mffs.api.Blacklist;
import mffs.api.ISpecialForceManipulation;
import mffs.api.modules.IModule;
import mffs.api.modules.IProjectorMode;
import mffs.base.TileEntityBase;
import mffs.card.ItemCard;
import mffs.event.BlockPreMoveDelayedEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TileEntityForceManipulator extends TileEntityFieldInteraction {

	public static final int ANIMATION_TIME = 20;
	public Vector3 anchor = null;
	public int displayMode = 1;
	public boolean isCalculatingManipulation = false;
	public Set manipulationVectors = null;
	public boolean doAnchor = true;

	public void updateEntity() {
		super.updateEntity();
		if (this.anchor == null) {
			this.anchor = new Vector3();
		}

		if (this.getMode() != null && Settings.ENABLE_MANIPULATOR) {
			if (!super.worldObj.isRemote && this.manipulationVectors != null && !this.isCalculatingManipulation) {
				ForgeDirection dir = this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagList nbtList = new NBTTagList();
				int i = 0;
				Iterator i$ = this.manipulationVectors.iterator();

				while (i$.hasNext()) {
					Vector3 position = (Vector3) i$.next();
					if (this.moveBlock(position, dir) && this.isBlockVisible(position) && i < Settings.MAX_FORCE_FIELDS_PER_TICK) {
						nbtList.appendTag(position.writeToNBT(new NBTTagCompound()));
						++i;
					}
				}

				nbt.setByte("type", (byte) 2);
				nbt.setTag("list", nbtList);
				PacketManager.sendPacketToClients(PacketManager.getPacket("MFFS", this, TileEntityBase.TilePacketType.FXS.ordinal(), nbt), super.worldObj, new Vector3(this), 60.0D);
				if (this.doAnchor) {
					this.anchor = this.anchor.modifyPositionFromSide(dir);
				}

				this.updatePushedObjects(0.022F);
				this.manipulationVectors = null;
				this.onInventoryChanged();
			}

			if (this.isActive() && super.ticks % 20L == 0L && this.requestFortron(this.getFortronCost(), false) > 0) {
				if (!super.worldObj.isRemote) {
					this.requestFortron(this.getFortronCost(), true);
					(new ManipulatorCalculationThread(this)).start();
				}

				if (this.getModuleCount(ModularForceFieldSystem.itemModuleSilence, new int[0]) <= 0) {
					super.worldObj.playSoundEffect((double) super.xCoord + 0.5D, (double) super.yCoord + 0.5D, (double) super.zCoord + 0.5D, "mffs.fieldmove", 0.6F, 1.0F - super.worldObj.rand.nextFloat() * 0.1F);
				}

				this.setActive(false);
			}

			if (!super.worldObj.isRemote) {
				if (!super.isCalculated) {
					this.calculateForceField();
				}

				if (super.ticks % 120L == 0L && !super.isCalculating && Settings.HIGH_GRAPHICS && this.getDelayedEvents().size() <= 0 && this.displayMode > 0) {
					NBTTagCompound nbt = new NBTTagCompound();
					NBTTagList nbtList = new NBTTagList();
					int i = 0;
					Iterator i$ = this.getInteriorPoints().iterator();

					while (true) {
						Vector3 position;
						do {
							do {
								if (!i$.hasNext()) {
									nbt.setByte("type", (byte) 1);
									nbt.setTag("list", nbtList);
									PacketManager.sendPacketToClients(PacketManager.getPacket("MFFS", this, TileEntityBase.TilePacketType.FXS.ordinal(), nbt), super.worldObj, new Vector3(this), 60.0D);
									return;
								}

								position = (Vector3) i$.next();
							} while (!this.isBlockVisible(position));
						} while (this.displayMode != 2 && position.getBlockID(super.worldObj) <= 0);

						if (i < Settings.MAX_FORCE_FIELDS_PER_TICK) {
							nbtList.appendTag(position.writeToNBT(new NBTTagCompound()));
							++i;
						}
					}
				}
			}
		}

	}

	public boolean isBlockVisible(Vector3 position) {
		int i = 0;
		ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			ForgeDirection direction = arr$[i$];
			Vector3 checkPos = position.clone().modifyPositionFromSide(direction);
			int blockID = checkPos.getBlockID(super.worldObj);
			if (blockID > 0 && Block.blocksList[blockID] != null && Block.blocksList[blockID].isOpaqueCube()) {
				++i;
			}
		}

		return i < ForgeDirection.VALID_DIRECTIONS.length;
	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		super.onReceivePacket(packetID, dataStream);
		if (packetID == TileEntityBase.TilePacketType.FXS.ordinal() && super.worldObj.isRemote) {
			NBTTagCompound nbt = PacketManager.readNBTTagCompound(dataStream);
			byte type = nbt.getByte("type");
			NBTTagList nbtList = (NBTTagList) nbt.getTag("list");

			for (int i = 0; i < nbtList.tagCount(); ++i) {
				Vector3 vector = Vector3.readFromNBT((NBTTagCompound) nbtList.tagAt(i)).add(0.5D);
				if (type == 1) {
					ModularForceFieldSystem.proxy.renderHologram(super.worldObj, vector, 1.0F, 1.0F, 1.0F, 30, vector.clone().modifyPositionFromSide(this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord)));
				} else if (type == 2) {
					ModularForceFieldSystem.proxy.renderHologram(super.worldObj, vector, 1.0F, 0.0F, 0.0F, 30, vector.clone().modifyPositionFromSide(this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord)));
					this.updatePushedObjects(0.022F);
				}
			}
		} else if (packetID == TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal() && !super.worldObj.isRemote) {
			this.anchor = null;
			this.onInventoryChanged();
		} else if (packetID == TileEntityBase.TilePacketType.TOGGLE_MODE_2.ordinal() && !super.worldObj.isRemote) {
			this.displayMode = (this.displayMode + 1) % 3;
		} else if (packetID == TileEntityBase.TilePacketType.TOGGLE_MODE_3.ordinal() && !super.worldObj.isRemote) {
			this.doAnchor = !this.doAnchor;
		}

	}

	public int getFortronCost() {
		return (int) (((double) super.getFortronCost() + this.anchor.getMagnitude()) * 1000.0D);
	}

	public void onInventoryChanged() {
		super.onInventoryChanged();
		super.isCalculated = false;
	}

	protected boolean canMove() {
		Set mobilizationPoints = this.getInteriorPoints();
		ForgeDirection dir = this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
		Iterator i$ = mobilizationPoints.iterator();

		Vector3 targetPosition;
		int blockID;
		do {
			do {
				label45:
				while (true) {
					Vector3 position;
					do {
						if (!i$.hasNext()) {
							return true;
						}

						position = (Vector3) i$.next();
					} while (position.getBlockID(super.worldObj) <= 0);

					if (Blacklist.forceManipulationBlacklist.contains(Block.blocksList[position.getBlockID(super.worldObj)])) {
						return false;
					}

					TileEntity tileEntity = position.getTileEntity(super.worldObj);
					if (tileEntity instanceof ISpecialForceManipulation && !((ISpecialForceManipulation) tileEntity).preMove(position.intX(), position.intY(), position.intZ())) {
						return false;
					}

					targetPosition = position.clone().modifyPositionFromSide(dir);
					if (targetPosition.getTileEntity(super.worldObj) == this) {
						return false;
					}

					Iterator i$ = mobilizationPoints.iterator();

					while (i$.hasNext()) {
						Vector3 checkPos = (Vector3) i$.next();
						if (checkPos.equals(targetPosition)) {
							continue label45;
						}
					}

					blockID = targetPosition.getBlockID(super.worldObj);
					break;
				}
			} while (blockID == 0);
		} while (blockID > 0 && (Block.blocksList[blockID].isBlockReplaceable(super.worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ()) || Block.blocksList[blockID] instanceof BlockFluid));

		return false;
	}

	protected boolean moveBlock(Vector3 position, ForgeDirection direction) {
		if (!super.worldObj.isRemote) {
			Vector3 newPosition = position.clone().modifyPositionFromSide(direction);
			TileEntity tileEntity = position.getTileEntity(super.worldObj);
			int blockID = position.getBlockID(super.worldObj);
			if (blockID > 0 && tileEntity != this) {
				this.getDelayedEvents().add(new BlockPreMoveDelayedEvent(this, 20, super.worldObj, position, newPosition));
				return true;
			}
		}

		return false;
	}

	public void updatePushedObjects(float amount) {
		ForgeDirection dir = this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
		AxisAlignedBB axisalignedbb = this.getSearchAxisAlignedBB();
		if (axisalignedbb != null) {
			List entities = super.worldObj.getEntitiesWithinAABB(Entity.class, axisalignedbb);
			Iterator i$ = entities.iterator();

			while (i$.hasNext()) {
				Entity entity = (Entity) i$.next();
				entity.addVelocity((double) (amount * (float) dir.offsetX), (double) (amount * (float) dir.offsetY), (double) (amount * (float) dir.offsetZ));
			}
		}

	}

	public AxisAlignedBB getSearchAxisAlignedBB() {
		Vector3 positiveScale = (new Vector3(this)).add(this.getTranslation()).add(this.getPositiveScale());
		Vector3 negativeScale = (new Vector3(this)).add(this.getTranslation()).subtract(this.getNegativeScale());
		Vector3 minScale = new Vector3(Math.min(positiveScale.x, negativeScale.x), Math.min(positiveScale.y, negativeScale.y), Math.min(positiveScale.z, negativeScale.z));
		Vector3 maxScale = new Vector3(Math.max(positiveScale.x, negativeScale.x), Math.max(positiveScale.y, negativeScale.y), Math.max(positiveScale.z, negativeScale.z));
		return AxisAlignedBB.getAABBPool().getAABB((double) minScale.intX(), (double) minScale.intY(), (double) minScale.intZ(), (double) maxScale.intX(), (double) maxScale.intY(), (double) maxScale.intZ());
	}

	public boolean isStackValidForSlot(int slotID, ItemStack itemStack) {
		if (slotID != 0 && slotID != 1) {
			if (slotID == 2) {
				return itemStack.getItem() instanceof IProjectorMode;
			} else {
				return slotID >= 15 ? true : itemStack.getItem() instanceof IModule;
			}
		} else {
			return itemStack.getItem() instanceof ItemCard;
		}
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.anchor = Vector3.readFromNBT(nbt.getCompoundTag("anchor"));
		this.displayMode = nbt.getInteger("displayMode");
		this.doAnchor = nbt.getBoolean("doAnchor");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (this.anchor != null) {
			nbt.setCompoundTag("anchor", this.anchor.writeToNBT(new NBTTagCompound()));
		}

		nbt.setInteger("displayMode", this.displayMode);
		nbt.setBoolean("doAnchor", this.doAnchor);
	}

	public Vector3 getTranslation() {
		return super.getTranslation().clone().add(this.anchor);
	}

	public int getSizeInventory() {
		return 21;
	}

	public String[] getMethodNames() {
		return new String[]{"isActivate", "setActivate", "resetAnchor"};
	}

	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
		switch (method) {
			case 2:
				this.anchor = null;
				return null;
			default:
				return super.callMethod(computer, method, arguments);
		}
	}
}
