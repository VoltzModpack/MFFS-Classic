package universalelectricity.prefab.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.implement.IRotatable;

public abstract class BlockRotatable extends BlockAdvanced implements IRotatable {

	public BlockRotatable(int id, Material material) {
		super(id, material);
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving, ItemStack itemStack) {
		int angle = MathHelper.floor_double((double) (entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int change = 3;
		switch (angle) {
			case 0:
				change = 2;
				break;
			case 1:
				change = 5;
				break;
			case 2:
				change = 3;
				break;
			case 3:
				change = 4;
		}

		this.setDirection(world, x, y, z, ForgeDirection.getOrientation(change));
	}

	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ) {
		this.setDirection(world, x, y, z, ForgeDirection.getOrientation(ForgeDirection.ROTATION_MATRIX[0][this.getDirection(world, x, y, z).ordinal()]));
		return true;
	}

	public ForgeDirection getDirection(IBlockAccess world, int x, int y, int z) {
		return ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
	}

	public void setDirection(World world, int x, int y, int z, ForgeDirection facingDirection) {
		world.setBlockMetadataWithNotify(x, y, z, facingDirection.ordinal(), 3);
	}
}
