package universalelectricity.prefab.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.lang.reflect.Method;
import java.util.Random;

public abstract class BlockAdvanced extends BlockContainer {

	public BlockAdvanced(Material material) {
		super(material);
//		super(id, material);
		this.setHardness(0.6F);
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		world.getBlockMetadata(x, y, z);
		if (this.isUsableWrench(entityPlayer, entityPlayer.inventory.getCurrentItem(), x, y, z)) {
			this.damageWrench(entityPlayer, entityPlayer.inventory.getCurrentItem(), x, y, z);
			if (entityPlayer.isSneaking() && this.onSneakUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ)) {
				return true;
			}

			if (this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ)) {
				return true;
			}
		}

		return entityPlayer.isSneaking() && this.onSneakMachineActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ) ? true : this.onMachineActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
	}

	public boolean isUsableWrench(EntityPlayer entityPlayer, ItemStack itemStack, int x, int y, int z) {
		if (entityPlayer != null && itemStack != null) {
			Class wrenchClass = itemStack.getItem().getClass();

			try {
				Method methodCanWrench = wrenchClass.getMethod("canWrench", EntityPlayer.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
				return (Boolean) methodCanWrench.invoke(itemStack.getItem(), entityPlayer, x, y, z);
			} catch (NoClassDefFoundError var8) {
			} catch (Exception var9) {
			}

			try {
				if (wrenchClass == Class.forName("ic2.core.item.tool.ItemToolWrench") || wrenchClass == Class.forName("ic2.core.item.tool.ItemToolWrenchElectric")) {
					return itemStack.getItemDamage() < itemStack.getMaxDamage();
				}
			} catch (Exception var10) {
			}
		}

		return false;
	}

	public boolean damageWrench(EntityPlayer entityPlayer, ItemStack itemStack, int x, int y, int z) {
		if (this.isUsableWrench(entityPlayer, itemStack, x, y, z)) {
			Class wrenchClass = itemStack.getItem().getClass();

			Method methodWrenchDamage;
			try {
				methodWrenchDamage = wrenchClass.getMethod("wrenchUsed", EntityPlayer.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
				methodWrenchDamage.invoke(itemStack.getItem(), entityPlayer, x, y, z);
				return true;
			} catch (Exception var8) {
			}

			try {
				if (wrenchClass == Class.forName("ic2.core.item.tool.ItemToolWrench") || wrenchClass == Class.forName("ic2.core.item.tool.ItemToolWrenchElectric")) {
					methodWrenchDamage = wrenchClass.getMethod("damage", ItemStack.class, Integer.TYPE, EntityPlayer.class);
					methodWrenchDamage.invoke(itemStack.getItem(), itemStack, 1, entityPlayer);
					return true;
				}
			} catch (Exception var9) {
			}
		}

		return false;
	}

	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		return this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
	}

	public TileEntity createNewTileEntity(World var1) {
		return null;
	}

	public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
		this.dropEntireInventory(world, x, y, z, block, par6);
		super.breakBlock(world, x, y, z, block, par6);
	}

	public void dropEntireInventory(World world, int x, int y, int z, Block block, int par6) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity != null && tileEntity instanceof IInventory) {
			IInventory inventory = (IInventory) tileEntity;

			for (int var6 = 0; var6 < inventory.getSizeInventory(); ++var6) {
				ItemStack var7 = inventory.getStackInSlot(var6);
				if (var7 != null) {
					Random random = new Random();
					float var8 = random.nextFloat() * 0.8F + 0.1F;
					float var9 = random.nextFloat() * 0.8F + 0.1F;
					float var10 = random.nextFloat() * 0.8F + 0.1F;

					while (var7.stackSize > 0) {
						int var11 = random.nextInt(21) + 10;
						if (var11 > var7.stackSize) {
							var11 = var7.stackSize;
						}

						var7.stackSize -= var11;
						EntityItem var12 = new EntityItem(world, (double) ((float) x + var8), (double) ((float) y + var9), (double) ((float) z + var10), new ItemStack(var7.itemID, var11, var7.getItemDamage()));
						if (var7.hasTagCompound()) {
							var12.getEntityItem().setTagCompound((NBTTagCompound) var7.getTagCompound().copy());
						}

						float var13 = 0.05F;
						var12.motionX = (double) ((float) random.nextGaussian() * var13);
						var12.motionY = (double) ((float) random.nextGaussian() * var13 + 0.2F);
						var12.motionZ = (double) ((float) random.nextGaussian() * var13);
						world.spawnEntityInWorld(var12);
					}
				}
			}
		}

	}
}
