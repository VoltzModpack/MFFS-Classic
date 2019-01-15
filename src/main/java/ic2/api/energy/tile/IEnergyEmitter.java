package ic2.api.energy.tile;

import ic2.api.Direction;
import net.minecraft.tileentity.TileEntity;

public interface IEnergyEmitter extends IEnergyTile {

	boolean emitsEnergyTo(TileEntity var1, Direction var2);
}
