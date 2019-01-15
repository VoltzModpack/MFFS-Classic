package mffs.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.ModularForceFieldSystem;
import mffs.api.IForceFieldBlock;
import mffs.api.IProjector;
import mffs.api.fortron.IFortronStorage;
import mffs.api.modules.IModule;
import mffs.api.security.IBiometricIdentifier;
import mffs.api.security.Permission;
import mffs.base.BlockBase;
import mffs.render.RenderForceField;
import mffs.tileentity.TileEntityForceField;
import micdoodle8.mods.galacticraft.API.IPartialSealedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.CustomDamageSource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BlockForceField extends BlockBase implements IForceFieldBlock, IPartialSealedBlock {

	public BlockForceField(int id) {
		super(id, "forceField", Material.glass);
		this.setBlockUnbreakable();
		this.setResistance(999.0F);
		this.setCreativeTab((CreativeTabs) null);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	protected boolean canSilkHarvest() {
		return false;
	}

	public int quantityDropped(Random random) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return RenderForceField.ID;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
		return i1 == super.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
	}

	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityForceField && ((TileEntityForceField) tileEntity).getProjector() != null) {
			Iterator i$ = ((TileEntityForceField) tileEntity).getProjector().getModuleStacks(((TileEntityForceField) tileEntity).getProjector().getModuleSlots()).iterator();

			while (i$.hasNext()) {
				ItemStack moduleStack = (ItemStack) i$.next();
				if (((IModule) moduleStack.getItem()).onCollideWithForceField(world, x, y, z, entityPlayer, moduleStack)) {
					return;
				}
			}
		}

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		if (this.getProjector(world, x, y, z) != null) {
			IBiometricIdentifier BiometricIdentifier = this.getProjector(world, x, y, z).getBiometricIdentifier();
			List entities = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double) x, (double) y, (double) z, (double) (x + 1), (double) y + 0.9D, (double) (z + 1)));
			Iterator i$ = entities.iterator();

			while (i$.hasNext()) {
				EntityPlayer entityPlayer = (EntityPlayer) i$.next();
				if (entityPlayer != null && entityPlayer.isSneaking()) {
					if (entityPlayer.capabilities.isCreativeMode) {
						return null;
					}

					if (BiometricIdentifier != null && BiometricIdentifier.isAccessGranted(entityPlayer.username, Permission.FORCE_FIELD_WARP)) {
						return null;
					}
				}
			}
		}

		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox((double) ((float) x + f), (double) ((float) y + f), (double) ((float) z + f), (double) ((float) (x + 1) - f), (double) ((float) (y + 1) - f), (double) ((float) (z + 1) - f));
	}

	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityForceField && this.getProjector(world, x, y, z) != null) {
			Iterator i$ = ((TileEntityForceField) tileEntity).getProjector().getModuleStacks(((TileEntityForceField) tileEntity).getProjector().getModuleSlots()).iterator();

			while (i$.hasNext()) {
				ItemStack moduleStack = (ItemStack) i$.next();
				if (((IModule) moduleStack.getItem()).onCollideWithForceField(world, x, y, z, entity, moduleStack)) {
					return;
				}
			}

			IBiometricIdentifier biometricIdentifier = this.getProjector(world, x, y, z).getBiometricIdentifier();
			if ((new Vector3(entity)).distanceTo((new Vector3((double) x, (double) y, (double) z)).add(0.4D)) < 0.5D && entity instanceof EntityLiving && !world.isRemote) {
				((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 80, 3));
				((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 1));
				boolean hasPermission = false;
				List entities = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double) x, (double) y, (double) z, (double) (x + 1), (double) y + 0.9D, (double) (z + 1)));
				Iterator i$ = entities.iterator();

				while (i$.hasNext()) {
					EntityPlayer entityPlayer = (EntityPlayer) i$.next();
					if (entityPlayer != null && entityPlayer.isSneaking()) {
						if (entityPlayer.capabilities.isCreativeMode) {
							hasPermission = true;
							break;
						}

						if (biometricIdentifier != null && biometricIdentifier.isAccessGranted(entityPlayer.username, Permission.FORCE_FIELD_WARP)) {
							hasPermission = true;
						}
					}
				}

				if (!hasPermission) {
					entity.attackEntityFrom(CustomDamageSource.electrocution, Integer.MAX_VALUE);
				}
			}
		}

	}

	public Icon getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityForceField) {
			ItemStack checkStack = ((TileEntityForceField) tileEntity).camoStack;
			if (checkStack != null) {
				try {
					Block block = Block.blocksList[((ItemBlock) checkStack.getItem()).getBlockID()];
					Integer[] allowedRenderTypes = new Integer[]{0, 1, 4, 31, 20, 39, 5, 13, 23, 6, 8, 7, 12, 29, 30, 14, 16, 17};
					if (Arrays.asList(allowedRenderTypes).contains(block.getRenderType())) {
						Icon icon = block.getIcon(side, checkStack.getItemDamage());
						if (icon != null) {
							return icon;
						}
					}
				} catch (Exception var11) {
					var11.printStackTrace();
				}
			}
		}

		return this.getIcon(side, iBlockAccess.getBlockMetadata(x, y, z));
	}

	public int colorMultiplier(IBlockAccess iBlockAccess, int x, int y, int z) {
		try {
			TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityForceField) {
				ItemStack checkStack = ((TileEntityForceField) tileEntity).camoStack;
				if (checkStack != null) {
					try {
						return Block.blocksList[((ItemBlock) checkStack.getItem()).getBlockID()].colorMultiplier(iBlockAccess, x, y, x);
					} catch (Exception var8) {
						var8.printStackTrace();
					}
				}
			}
		} catch (Exception var9) {
			var9.printStackTrace();
		}

		return super.colorMultiplier(iBlockAccess, x, y, z);
	}

	public int getLightValue(IBlockAccess iBlockAccess, int x, int y, int z) {
		try {
			TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityForceField) {
				IProjector zhuYao = ((TileEntityForceField) tileEntity).getProjectorSafe();
				if (zhuYao instanceof IProjector) {
					return (int) ((float) Math.min(zhuYao.getModuleCount(ModularForceFieldSystem.itemModuleGlow, new int[0]), 64) / 64.0F * 15.0F);
				}
			}
		} catch (Exception var7) {
			var7.printStackTrace();
		}

		return 0;
	}

	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double d, double d1, double d2) {
		return 2.14748365E9F;
	}

	public TileEntity createNewTileEntity(World world) {
		return new TileEntityForceField();
	}

	public void weakenForceField(World world, int x, int y, int z, int joules) {
		IProjector projector = this.getProjector(world, x, y, z);
		if (projector != null) {
			((IFortronStorage) projector).provideFortron(joules, true);
		}

		world.setBlock(x, y, z, 0, 0, 3);
	}

	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return null;
	}

	public IProjector getProjector(IBlockAccess iBlockAccess, int x, int y, int z) {
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
		return tileEntity instanceof TileEntityForceField ? ((TileEntityForceField) tileEntity).getProjector() : null;
	}

	public boolean isSealed(World world, int x, int y, int z) {
		return true;
	}
}
