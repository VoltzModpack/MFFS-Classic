package mffs.tileentity;

import com.google.common.io.ByteArrayDataInput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.ModularForceFieldSystem;
import mffs.Settings;
import mffs.api.ICache;
import mffs.api.IProjector;
import mffs.api.modules.IModule;
import mffs.api.modules.IProjectorMode;
import mffs.base.TileEntityBase;
import mffs.block.BlockForceField;
import mffs.card.ItemCard;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TileEntityForceFieldProjector extends TileEntityFieldInteraction implements IProjector, ProjectorCalculationThread.IThreadCallBack {

	protected final Set forceFields = new HashSet();

	public TileEntityForceFieldProjector() {
		super.capacityBase = 50;
		super.startModuleIndex = 1;
	}

	public void initiate() {
		super.initiate();
		this.calculateForceField();
	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		super.onReceivePacket(packetID, dataStream);
		if (packetID == TileEntityBase.TilePacketType.FXS.ordinal() && super.worldObj.isRemote) {
			int type = dataStream.readInt();
			Vector3 vector = (new Vector3((double) dataStream.readInt(), (double) dataStream.readInt(), (double) dataStream.readInt())).add(0.5D);
			Vector3 root = (new Vector3(this)).add(0.5D);
			if (type == 1) {
				ModularForceFieldSystem.proxy.renderBeam(super.worldObj, root, vector, 0.6F, 0.6F, 1.0F, 40);
				ModularForceFieldSystem.proxy.renderHologramMoving(super.worldObj, vector, 1.0F, 1.0F, 1.0F, 50);
			} else if (type == 2) {
				ModularForceFieldSystem.proxy.renderBeam(super.worldObj, vector, root, 1.0F, 0.0F, 0.0F, 40);
				ModularForceFieldSystem.proxy.renderHologramMoving(super.worldObj, vector, 1.0F, 0.0F, 0.0F, 50);
			}
		}

	}

	protected void calculateForceField(ProjectorCalculationThread.IThreadCallBack callBack) {
		if (!super.worldObj.isRemote && !super.isCalculating && this.getMode() != null) {
			this.forceFields.clear();
		}

		super.calculateForceField(callBack);
	}

	public void onThreadComplete() {
		this.destroyField();
	}

	public void updateEntity() {
		super.updateEntity();
		if (this.isActive() && this.getMode() != null && this.requestFortron(this.getFortronCost(), false) >= this.getFortronCost()) {
			this.consumeCost();
			if (!super.worldObj.isRemote) {
				if (super.ticks % 10L == 0L) {
					if (!super.isCalculated) {
						this.calculateForceField();
					} else {
						this.projectField();
					}
				}
			} else if (this.isActive()) {
				super.animation += (float) (this.getFortronCost() / 3);
			}

			if (super.ticks % 40L == 0L && this.getModuleCount(ModularForceFieldSystem.itemModuleSilence, new int[0]) <= 0) {
				super.worldObj.playSoundEffect((double) super.xCoord + 0.5D, (double) super.yCoord + 0.5D, (double) super.zCoord + 0.5D, "mffs.field", 0.6F, 1.0F - super.worldObj.rand.nextFloat() * 0.1F);
			}
		} else if (!super.worldObj.isRemote) {
			this.destroyField();
		}

	}

	public int getFortronCost() {
		return super.getFortronCost() + 5;
	}

	public float getAmplifier() {
		return (float) Math.max(Math.min(this.getCalculatedField().size() / 1000, 10), 1);
	}

	public void onInventoryChanged() {
		super.onInventoryChanged();
		this.destroyField();
	}

	public void projectField() {
		if (!super.worldObj.isRemote && super.isCalculated && !super.isCalculating) {
			if (this.forceFields.size() <= 0 && this.getModeStack().getItem() instanceof ICache) {
				((ICache) this.getModeStack().getItem()).clearCache();
			}

			int constructionCount = 0;
			int constructionSpeed = Math.min(this.getProjectionSpeed(), Settings.MAX_FORCE_FIELDS_PER_TICK);
			HashSet fieldToBeProjected = new HashSet();
			fieldToBeProjected.addAll(super.calculatedField);
			Iterator it = this.getModules(this.getModuleSlots()).iterator();

			while (it.hasNext()) {
				IModule module = (IModule) it.next();
				if (module.onProject(this, (Set) fieldToBeProjected)) {
					return;
				}
			}

			it = super.calculatedField.iterator();

			while (true) {
				label105:
				while (true) {
					Vector3 vector;
					do {
						Block block;
						do {
							do {
								label84:
								do {
									while (it.hasNext()) {
										vector = (Vector3) it.next();
										if (fieldToBeProjected.contains(vector)) {
											if (constructionCount > constructionSpeed) {
												return;
											}

											block = Block.blocksList[vector.getBlockID(super.worldObj)];
											continue label84;
										}

										block = Block.blocksList[vector.getBlockID(super.worldObj)];
										if (block == ModularForceFieldSystem.blockForceField && ((BlockForceField) block).getProjector(super.worldObj, vector.intX(), vector.intY(), vector.intZ()) == this) {
											super.worldObj.setBlock(vector.intX(), vector.intY(), vector.intZ(), 0);
										}
									}

									return;
								} while (block != null && (this.getModuleCount(ModularForceFieldSystem.itemModuleDisintegration, new int[0]) <= 0 || block.getBlockHardness(super.worldObj, vector.intX(), vector.intY(), vector.intZ()) == -1.0F) && !block.blockMaterial.isLiquid() && block != Block.snow && block != Block.vine && block != Block.tallGrass && block != Block.deadBush && !block.isBlockReplaceable(super.worldObj, vector.intX(), vector.intY(), vector.intZ()));
							} while (block == ModularForceFieldSystem.blockForceField);
						} while (vector.equals(new Vector3(this)));
					} while (!super.worldObj.getChunkFromBlockCoords(vector.intX(), vector.intZ()).isChunkLoaded);

					Iterator i$ = this.getModules(this.getModuleSlots()).iterator();

					while (i$.hasNext()) {
						IModule module = (IModule) i$.next();
						int flag = module.onProject(this, (Vector3) vector.clone());
						if (flag == 1) {
							continue label105;
						}

						if (flag == 2) {
							return;
						}
					}

					super.worldObj.setBlock(vector.intX(), vector.intY(), vector.intZ(), ModularForceFieldSystem.blockForceField.blockID, 0, 2);
					TileEntity tileEntity = super.worldObj.getBlockTileEntity(vector.intX(), vector.intY(), vector.intZ());
					if (tileEntity instanceof TileEntityForceField) {
						((TileEntityForceField) tileEntity).setProjector(new Vector3(this));
					}

					this.requestFortron(1, true);
					this.forceFields.add(vector);
					++constructionCount;
				}
			}
		}
	}

	public void destroyField() {
		if (!super.worldObj.isRemote && super.isCalculated && !super.isCalculating) {
			HashSet copiedSet = new HashSet();
			copiedSet.addAll(super.calculatedField);
			Iterator it = copiedSet.iterator();

			while (it.hasNext()) {
				Vector3 vector = (Vector3) it.next();
				Block block = Block.blocksList[vector.getBlockID(super.worldObj)];
				if (block == ModularForceFieldSystem.blockForceField) {
					super.worldObj.setBlock(vector.intX(), vector.intY(), vector.intZ(), 0, 0, 3);
				}
			}
		}

		this.forceFields.clear();
		super.calculatedField.clear();
		super.isCalculated = false;
	}

	public void invalidate() {
		this.destroyField();
		super.invalidate();
	}

	public int getProjectionSpeed() {
		return 28 + 28 * this.getModuleCount(ModularForceFieldSystem.itemModuleSpeed, this.getModuleSlots());
	}

	public int getSizeInventory() {
		return 21;
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

	public Set getCards() {
		Set cards = new HashSet();
		cards.add(super.getCard());
		cards.add(this.getStackInSlot(1));
		return cards;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB((double) super.xCoord, (double) super.yCoord, (double) super.zCoord, (double) (super.xCoord + 1), (double) (super.yCoord + 2), (double) (super.zCoord + 1));
	}

	public long getTicks() {
		return super.ticks;
	}
}
