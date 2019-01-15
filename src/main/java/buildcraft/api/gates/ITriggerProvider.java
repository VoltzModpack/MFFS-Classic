package buildcraft.api.gates;

import buildcraft.api.transport.IPipe;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.LinkedList;

public interface ITriggerProvider {

	LinkedList getPipeTriggers(IPipe var1);

	LinkedList getNeighborTriggers(Block var1, TileEntity var2);
}
