package universalelectricity.core.block;

import net.minecraft.tileentity.TileEntity;

public interface IConnectionProvider extends IConnector {

	TileEntity[] getAdjacentConnections();

	void updateAdjacentConnections();
}
