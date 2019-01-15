package mffs.block;

import mffs.tileentity.TileEntityBiometricIdentifier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBiometricIdentifier extends BlockMachineBlock {

	public BlockBiometricIdentifier(int i) {
		super(i, "biometricIdentifier");
	}

	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBiometricIdentifier();
	}
}
