package buildcraft.api.gates;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.LinkedList;

public interface IActionProvider {

	LinkedList getNeighborActions(Block var1, TileEntity var2);
}
