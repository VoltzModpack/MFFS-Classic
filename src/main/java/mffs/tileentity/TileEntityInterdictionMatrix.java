package mffs.tileentity;

import com.google.common.io.ByteArrayDataInput;
import mffs.ModularForceFieldSystem;
import mffs.Settings;
import mffs.api.modules.IInterdictionMatrixModule;
import mffs.api.modules.IModule;
import mffs.api.security.IBiometricIdentifier;
import mffs.api.security.IInterdictionMatrix;
import mffs.api.security.Permission;
import mffs.base.TileEntityBase;
import mffs.base.TileEntityModuleAcceptor;
import mffs.card.ItemCard;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import java.io.IOException;
import java.util.*;

public class TileEntityInterdictionMatrix extends TileEntityModuleAcceptor implements IInterdictionMatrix {

	private boolean isBanMode = true;

	public TileEntityInterdictionMatrix() {
		super.capacityBase = 30;
		super.startModuleIndex = 2;
		super.endModuleIndex = 9;
	}

	public void updateEntity() {
		super.updateEntity();
		if (!super.worldObj.isRemote && (this.isActive() || this.getStackInSlot(0) != null && this.getStackInSlot(0).itemID == ModularForceFieldSystem.itemCardInfinite.itemID) && super.ticks % 10L == 0L && this.requestFortron(this.getFortronCost() * 10, false) > 0) {
			this.requestFortron(this.getFortronCost() * 10, true);
			this.scan();
		}

	}

	public float getAmplifier() {
		return (float) Math.max(Math.min(this.getActionRange() / 20, 10), 1);
	}

	public void scan() {
		try {
			IBiometricIdentifier biometricIdentifier = this.getBiometricIdentifier();
			AxisAlignedBB emptyBounds = AxisAlignedBB.getBoundingBox((double) super.xCoord, (double) super.yCoord, (double) super.zCoord, (double) (super.xCoord + 1), (double) (super.yCoord + 1), (double) (super.zCoord + 1));
			List warningList = super.worldObj.getEntitiesWithinAABB(EntityLiving.class, emptyBounds.expand((double) this.getWarningRange(), (double) this.getWarningRange(), (double) this.getWarningRange()));
			List actionList = super.worldObj.getEntitiesWithinAABB(EntityLiving.class, emptyBounds.expand((double) this.getActionRange(), (double) this.getActionRange(), (double) this.getActionRange()));
			Iterator i$ = warningList.iterator();

			EntityLiving entityLiving;
			while (i$.hasNext()) {
				entityLiving = (EntityLiving) i$.next();
				if (entityLiving instanceof EntityPlayer && !actionList.contains(entityLiving)) {
					EntityPlayer player = (EntityPlayer) entityLiving;
					boolean isGranted = false;
					if (biometricIdentifier != null && biometricIdentifier.isAccessGranted(player.username, Permission.BYPASS_INTERDICTION_MATRIX)) {
						isGranted = true;
					}

					if (!isGranted && super.worldObj.rand.nextInt(3) == 0) {
						player.addChatMessage("[" + this.getInvName() + "] Warning! You are near the scanning range!");
					}
				}
			}

			if (super.worldObj.rand.nextInt(3) == 0) {
				i$ = actionList.iterator();

				while (i$.hasNext()) {
					entityLiving = (EntityLiving) i$.next();
					this.applyAction(entityLiving);
				}
			}
		} catch (Exception var9) {
			ModularForceFieldSystem.LOGGER.severe("Defense Station has an error!");
			var9.printStackTrace();
		}

	}

	public void applyAction(EntityLiving entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			IBiometricIdentifier biometricIdentifier = this.getBiometricIdentifier();
			if (biometricIdentifier != null && biometricIdentifier.isAccessGranted(player.username, Permission.BYPASS_INTERDICTION_MATRIX)) {
				return;
			}

			if (!Settings.INTERACT_CREATIVE && player.capabilities.isCreativeMode) {
				return;
			}
		}

		Iterator i$ = this.getModuleStacks(new int[0]).iterator();

		while (i$.hasNext()) {
			ItemStack itemStack = (ItemStack) i$.next();
			if (itemStack.getItem() instanceof IInterdictionMatrixModule) {
				IInterdictionMatrixModule module = (IInterdictionMatrixModule) itemStack.getItem();
				if (module.onDefend(this, entityLiving) || entityLiving.isDead) {
					break;
				}
			}
		}

	}

	public List getPacketUpdate() {
		List objects = new LinkedList();
		objects.addAll(super.getPacketUpdate());
		objects.add(this.isBanMode);
		return objects;
	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		super.onReceivePacket(packetID, dataStream);
		if (packetID == TileEntityBase.TilePacketType.DESCRIPTION.ordinal()) {
			this.isBanMode = dataStream.readBoolean();
		} else if (packetID == TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal()) {
			this.isBanMode = !this.isBanMode;
		}

	}

	public boolean isBanMode() {
		return this.isBanMode;
	}

	public int getActionRange() {
		return this.getModuleCount(ModularForceFieldSystem.itemModuleScale, new int[0]);
	}

	public int getWarningRange() {
		return this.getModuleCount(ModularForceFieldSystem.itemModuleWarn, new int[0]) + this.getActionRange() + 3;
	}

	public int getSizeInventory() {
		return 19;
	}

	public Set getFilteredItems() {
		Set stacks = new HashSet();

		for (int i = super.endModuleIndex; i < this.getSizeInventory() - 1; ++i) {
			if (this.getStackInSlot(i) != null) {
				stacks.add(this.getStackInSlot(i));
			}
		}

		return stacks;
	}

	public boolean getFilterMode() {
		return this.isBanMode;
	}

	public boolean isStackValidForSlot(int slotID, ItemStack itemStack) {
		if (slotID != 0 && slotID != 1) {
			return slotID > super.endModuleIndex ? true : itemStack.getItem() instanceof IModule;
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

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.isBanMode = nbt.getBoolean("isBanMode");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("isBanMode", this.isBanMode);
	}
}
