package mffs.api.security;

import mffs.api.IActivatable;
import mffs.api.IBiometricIdentifierLink;
import mffs.api.fortron.IFortronFrequency;
import mffs.api.modules.IModuleAcceptor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Set;

public interface IInterdictionMatrix extends IInventory, IFortronFrequency, IModuleAcceptor, IBiometricIdentifierLink, IActivatable {

	int getWarningRange();

	int getActionRange();

	boolean mergeIntoInventory(ItemStack var1);

	Set getFilteredItems();

	boolean getFilterMode();

	int getFortronCost();
}
