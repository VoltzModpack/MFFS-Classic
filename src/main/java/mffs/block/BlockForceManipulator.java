package mffs.block;

import mffs.base.BlockMachine;
import mffs.tileentity.TileEntityForceManipulator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockForceManipulator extends BlockMachine {

	public BlockForceManipulator(int i) {
		super(i, "manipulator");
	}

	public static int determineOrientation(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		if (MathHelper.abs((float) entityPlayer.posX - (float) x) < 2.0F && MathHelper.abs((float) entityPlayer.posZ - (float) z) < 2.0F) {
			double var5 = entityPlayer.posY + 1.82D - (double) entityPlayer.yOffset;
			if (var5 - (double) y > 2.0D) {
				return 1;
			}

			if ((double) y - var5 > 0.0D) {
				return 0;
			}
		}

		int var7 = MathHelper.floor_double((double) (entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return var7 == 0 ? 2 : (var7 == 1 ? 5 : (var7 == 2 ? 3 : (var7 == 3 ? 4 : 0)));
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack stack) {
		int metadata = determineOrientation(world, x, y, z, (EntityPlayer) par5EntityLiving);
		world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
	}

	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ) {
		int mask = 7;
		int rotMeta = world.getBlockMetadata(x, y, z);
		int masked = rotMeta & ~mask;
		ForgeDirection orientation = ForgeDirection.getOrientation(rotMeta & mask);
		ForgeDirection rotated = orientation.getRotation(ForgeDirection.getOrientation(side));
		world.setBlockMetadataWithNotify(x, y, z, rotated.ordinal() & mask | masked, 3);
		return true;
	}

	public TileEntity createNewTileEntity(World world) {
		return new TileEntityForceManipulator();
	}
}
