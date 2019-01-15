package mffs.tileentity;

import com.google.common.io.ByteArrayDataInput;
import mffs.ModularForceFieldSystem;
import mffs.Settings;
import mffs.api.card.ICardIdentification;
import mffs.api.security.IBiometricIdentifier;
import mffs.api.security.Permission;
import mffs.base.TileEntityBase;
import mffs.base.TileEntityFrequency;
import mffs.item.card.ItemCardFrequency;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TileEntityBiometricIdentifier extends TileEntityFrequency implements IBiometricIdentifier {

	public static final int SLOT_COPY = 12;

	public boolean isAccessGranted(String username, Permission permission) {
		if (!this.isActive()) {
			return true;
		} else if (ModularForceFieldSystem.proxy.isOp(username) && Settings.OP_OVERRIDE) {
			return true;
		} else {
			for (int i = 0; i < this.getSizeInventory(); ++i) {
				ItemStack itemStack = this.getStackInSlot(i);
				if (itemStack != null && itemStack.getItem() instanceof ICardIdentification && username.equalsIgnoreCase(((ICardIdentification) itemStack.getItem()).getUsername(itemStack)) && ((ICardIdentification) itemStack.getItem()).hasPermission(itemStack, permission)) {
					return true;
				}
			}

			return username.equalsIgnoreCase(this.getOwner());
		}
	}

	public void onReceivePacket(int packetID, ByteArrayDataInput dataStream) throws IOException {
		super.onReceivePacket(packetID, dataStream);
		ICardIdentification idCard;
		if (packetID == TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal()) {
			if (this.getManipulatingCard() != null) {
				idCard = (ICardIdentification) this.getManipulatingCard().getItem();
				int id = dataStream.readInt();
				Permission permission = Permission.getPermission(id);
				if (permission != null) {
					if (!idCard.hasPermission(this.getManipulatingCard(), permission)) {
						idCard.addPermission(this.getManipulatingCard(), permission);
					} else {
						idCard.removePermission(this.getManipulatingCard(), permission);
					}
				} else {
					ModularForceFieldSystem.LOGGER.severe("Error handling security station permission packet: " + id + " - " + permission);
				}
			}
		} else if (packetID == TileEntityBase.TilePacketType.STRING.ordinal() && this.getManipulatingCard() != null) {
			idCard = (ICardIdentification) this.getManipulatingCard().getItem();
			idCard.setUsername(this.getManipulatingCard(), dataStream.readUTF());
		}

	}

	public boolean isStackValidForSlot(int slotID, ItemStack itemStack) {
		return slotID == 0 ? itemStack.getItem() instanceof ItemCardFrequency : itemStack.getItem() instanceof ICardIdentification;
	}

	public String getOwner() {
		ItemStack itemStack = this.getStackInSlot(2);
		return itemStack != null && itemStack.getItem() instanceof ICardIdentification ? ((ICardIdentification) itemStack.getItem()).getUsername(itemStack) : null;
	}

	public void onInventoryChanged() {
		super.onInventoryChanged();
		if (this.getManipulatingCard() != null && this.getStackInSlot(12) != null && this.getStackInSlot(12).getItem() instanceof ICardIdentification) {
			ICardIdentification masterCard = (ICardIdentification) this.getManipulatingCard().getItem();
			ICardIdentification copyCard = (ICardIdentification) this.getStackInSlot(12).getItem();
			Permission[] arr$ = Permission.getPermissions();
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Permission permission = arr$[i$];
				if (masterCard.hasPermission(this.getManipulatingCard(), permission)) {
					copyCard.addPermission(this.getStackInSlot(12), permission);
				} else {
					copyCard.removePermission(this.getStackInSlot(12), permission);
				}
			}
		}

	}

	public int getSizeInventory() {
		return 13;
	}

	public int getInventoryStackLimit() {
		return 1;
	}

	public ItemStack getManipulatingCard() {
		return this.getStackInSlot(1) != null && this.getStackInSlot(1).getItem() instanceof ICardIdentification ? this.getStackInSlot(1) : null;
	}

	public void setActive(boolean flag) {
		if (this.getOwner() != null || !flag) {
			super.setActive(flag);
		}

	}

	public Set getBiometricIdentifiers() {
		Set set = new HashSet();
		set.add(this);
		return set;
	}
}
