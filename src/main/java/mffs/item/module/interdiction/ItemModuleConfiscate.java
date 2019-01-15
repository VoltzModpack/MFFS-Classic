package mffs.item.module.interdiction;

import mffs.api.security.IBiometricIdentifier;
import mffs.api.security.IInterdictionMatrix;
import mffs.api.security.Permission;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.Set;

public class ItemModuleConfiscate extends ItemModuleInterdictionMatrix {

	public ItemModuleConfiscate(int i) {
		super(i, "moduleConfiscate");
	}

	public boolean onDefend(IInterdictionMatrix interdictionMatrix, EntityLiving entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			IBiometricIdentifier biometricIdentifier = interdictionMatrix.getBiometricIdentifier();
			if (biometricIdentifier != null && biometricIdentifier.isAccessGranted(player.username, Permission.DEFENSE_STATION_CONFISCATION)) {
				return false;
			}
		}

		Set controlledStacks = interdictionMatrix.getFilteredItems();
		int confiscationCount = 0;
		IInventory inventory = null;
		if (entityLiving instanceof EntityPlayer) {
			IBiometricIdentifier biometricIdentifier = interdictionMatrix.getBiometricIdentifier();
			if (biometricIdentifier != null && biometricIdentifier.isAccessGranted(((EntityPlayer) entityLiving).username, Permission.BYPASS_INTERDICTION_MATRIX)) {
				return false;
			}

			EntityPlayer player = (EntityPlayer) entityLiving;
			inventory = player.inventory;
		} else if (entityLiving instanceof IInventory) {
			inventory = (IInventory) entityLiving;
		}

		if (inventory != null) {
			for (int i = 0; i < ((IInventory) inventory).getSizeInventory(); ++i) {
				ItemStack checkStack = ((IInventory) inventory).getStackInSlot(i);
				if (checkStack != null) {
					boolean stacksMatch = false;
					Iterator i$ = controlledStacks.iterator();

					while (i$.hasNext()) {
						ItemStack itemStack = (ItemStack) i$.next();
						if (itemStack != null && itemStack.isItemEqual(checkStack)) {
							stacksMatch = true;
							break;
						}
					}

					if (interdictionMatrix.getFilterMode() && stacksMatch || !interdictionMatrix.getFilterMode() && !stacksMatch) {
						interdictionMatrix.mergeIntoInventory(((IInventory) inventory).getStackInSlot(i));
						((IInventory) inventory).setInventorySlotContents(i, (ItemStack) null);
						++confiscationCount;
					}
				}
			}

			if (confiscationCount > 0 && entityLiving instanceof EntityPlayer) {
				((EntityPlayer) entityLiving).addChatMessage("[" + interdictionMatrix.getInvName() + "] " + confiscationCount + " of your item(s) has been confiscated.");
			}

			interdictionMatrix.requestFortron(confiscationCount, true);
		}

		return false;
	}
}
