package mffs.item.card;

import mffs.MFFSHelper;
import mffs.api.card.ICardIdentification;
import mffs.api.security.Permission;
import mffs.card.ItemCard;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.prefab.TranslationHelper;

import java.util.List;

public class ItemCardID extends ItemCard implements ICardIdentification {

	private static final String NBT_PREFIX = "mffs_permission_";

	public ItemCardID(int i) {
		super(i, "cardIdentification");
	}

	public ItemCardID(int i, String name) {
		super(i, name);
	}

	public boolean hitEntity(ItemStack itemStack, EntityLiving entityLiving, EntityLiving par3EntityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			this.setUsername(itemStack, ((EntityPlayer) entityLiving).username);
		}

		return false;
	}

	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
		if (this.getUsername(itemStack) != null && !this.getUsername(itemStack).isEmpty()) {
			info.add("Username: " + this.getUsername(itemStack));
		} else {
			info.add("Unidentified");
		}

		String tooltip = "";
		boolean isFirst = true;
		Permission[] arr$ = Permission.getPermissions();
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Permission permission = arr$[i$];
			if (this.hasPermission(itemStack, permission)) {
				if (!isFirst) {
					tooltip = tooltip + ", ";
				}

				isFirst = false;
				tooltip = tooltip + TranslationHelper.getLocal("gui." + permission.name + ".name");
			}
		}

		if (tooltip != null && tooltip.length() > 0) {
			info.addAll(MFFSHelper.splitStringPerWord(tooltip, 5));
		}

	}

	public ItemStack onItemRightClick(ItemStack itemStack, World par2World, EntityPlayer entityPlayer) {
		this.setUsername(itemStack, entityPlayer.username);
		return itemStack;
	}

	public void setUsername(ItemStack itemStack, String username) {
		NBTTagCompound nbtTagCompound = MFFSHelper.getNBTTagCompound(itemStack);
		nbtTagCompound.setString("name", username);
	}

	public String getUsername(ItemStack itemStack) {
		NBTTagCompound nbtTagCompound = MFFSHelper.getNBTTagCompound(itemStack);
		return nbtTagCompound != null && nbtTagCompound.getString("name") != "" ? nbtTagCompound.getString("name") : null;
	}

	public boolean hasPermission(ItemStack itemStack, Permission permission) {
		NBTTagCompound nbt = MFFSHelper.getNBTTagCompound(itemStack);
		return nbt.getBoolean("mffs_permission_" + permission.id);
	}

	public boolean addPermission(ItemStack itemStack, Permission permission) {
		NBTTagCompound nbt = MFFSHelper.getNBTTagCompound(itemStack);
		nbt.setBoolean("mffs_permission_" + permission.id, true);
		return false;
	}

	public boolean removePermission(ItemStack itemStack, Permission permission) {
		NBTTagCompound nbt = MFFSHelper.getNBTTagCompound(itemStack);
		nbt.setBoolean("mffs_permission_" + permission.id, false);
		return false;
	}
}
