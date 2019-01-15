package mffs.item.card;

import icbm.api.IItemFrequency;
import mffs.base.TileEntityFrequency;
import mffs.card.ItemCard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class ItemCardFrequency extends ItemCard implements IItemFrequency {

	public ItemCardFrequency(String name, int i) {
		super(i, name);
	}

	public ItemCardFrequency(int i) {
		this("cardFrequency", i);
	}

	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		list.add("Frequency: " + this.getFrequency(itemStack));
	}

	public int getFrequency(ItemStack itemStack) {
		if (itemStack != null) {
			if (itemStack.getTagCompound() == null) {
				itemStack.setTagCompound(new NBTTagCompound());
			}

			return itemStack.getTagCompound().getInteger("frequency");
		} else {
			return 0;
		}
	}

	public void setFrequency(int frequency, ItemStack itemStack) {
		if (itemStack != null) {
			if (itemStack.getTagCompound() == null) {
				itemStack.setTagCompound(new NBTTagCompound());
			}

			itemStack.getTagCompound().setInteger("frequency", frequency);
		}

	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (!world.isRemote && player.isSneaking()) {
			this.setFrequency(world.rand.nextInt(15), itemStack);
			player.addChatMessage("Generated random frequency: " + this.getFrequency(itemStack));
		}

		return itemStack;
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityFrequency) {
			if (!world.isRemote) {
				((TileEntityFrequency) tileEntity).setFrequency(this.getFrequency(itemStack));
				world.markBlockForUpdate(x, y, z);
				player.addChatMessage("Frequency set to: " + this.getFrequency(itemStack));
			}

			return true;
		} else {
			return false;
		}
	}
}
