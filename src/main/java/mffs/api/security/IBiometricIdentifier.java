package mffs.api.security;

import net.minecraft.item.ItemStack;

public interface IBiometricIdentifier {

	boolean isAccessGranted(String var1, Permission var2);

	String getOwner();

	ItemStack getManipulatingCard();
}
