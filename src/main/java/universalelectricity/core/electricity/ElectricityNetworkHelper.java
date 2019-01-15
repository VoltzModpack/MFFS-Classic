package universalelectricity.core.electricity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import universalelectricity.core.block.IConnector;
import universalelectricity.core.block.INetworkProvider;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class ElectricityNetworkHelper {

	public static void invalidate(TileEntity tileEntity) {
		for (int i = 0; i < 6; ++i) {
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			TileEntity checkTile = VectorHelper.getConnectorFromSide(tileEntity.getWorldObj(), new Vector3(tileEntity), direction);
			if (checkTile instanceof INetworkProvider) {
				IElectricityNetwork network = ((INetworkProvider) checkTile).getNetwork();
				if (network != null) {
					network.stopRequesting(tileEntity);
					network.stopProducing(tileEntity);
				}
			}
		}

	}

	public static EnumSet getDirections(TileEntity tileEntity) {
		EnumSet possibleSides = EnumSet.noneOf(ForgeDirection.class);
		if (tileEntity instanceof IConnector) {
			for (int i = 0; i < 6; ++i) {
				ForgeDirection direction = ForgeDirection.getOrientation(i);
				if (((IConnector) tileEntity).canConnect(direction)) {
					possibleSides.add(direction);
				}
			}
		}

		return possibleSides;
	}

	public static ElectricityPack produceFromMultipleSides(TileEntity tileEntity, ElectricityPack electricityPack) {
		return produceFromMultipleSides(tileEntity, getDirections(tileEntity), electricityPack);
	}

	public static ElectricityPack produceFromMultipleSides(TileEntity tileEntity, EnumSet approachingDirection, ElectricityPack producingPack) {
		ElectricityPack remainingElectricity = producingPack.clone();
		if (tileEntity != null && approachingDirection != null) {
			List connectedNetworks = getNetworksFromMultipleSides(tileEntity, approachingDirection);
			if (connectedNetworks.size() > 0) {
				double wattsPerSide = producingPack.getWatts() / (double) connectedNetworks.size();
				double voltage = producingPack.voltage;
				Iterator i$ = connectedNetworks.iterator();

				while (true) {
					while (i$.hasNext()) {
						IElectricityNetwork network = (IElectricityNetwork) i$.next();
						if (wattsPerSide > 0.0D && producingPack.getWatts() > 0.0D) {
							double amperes = Math.min(wattsPerSide / voltage, network.getRequest(tileEntity).amperes);
							if (amperes > 0.0D) {
								network.startProducing(tileEntity, amperes, voltage);
								remainingElectricity.amperes -= amperes;
							}
						} else {
							network.stopProducing(tileEntity);
						}
					}

					return remainingElectricity;
				}
			}
		}

		return remainingElectricity;
	}

	public static ElectricityPack consumeFromMultipleSides(TileEntity tileEntity, ElectricityPack electricityPack) {
		return consumeFromMultipleSides(tileEntity, getDirections(tileEntity), electricityPack);
	}

	public static ElectricityPack consumeFromMultipleSides(TileEntity tileEntity, EnumSet approachingDirection, ElectricityPack requestPack) {
		ElectricityPack consumedPack = new ElectricityPack();
		if (tileEntity != null && approachingDirection != null) {
			List connectedNetworks = getNetworksFromMultipleSides(tileEntity, approachingDirection);
			if (connectedNetworks.size() > 0) {
				double wattsPerSide = requestPack.getWatts() / (double) connectedNetworks.size();
				double voltage = requestPack.voltage;
				Iterator i$ = connectedNetworks.iterator();

				while (true) {
					while (i$.hasNext()) {
						IElectricityNetwork network = (IElectricityNetwork) i$.next();
						if (wattsPerSide > 0.0D && requestPack.getWatts() > 0.0D) {
							network.startRequesting(tileEntity, wattsPerSide / voltage, voltage);
							ElectricityPack receivedPack = network.consumeElectricity(tileEntity);
							consumedPack.amperes += receivedPack.amperes;
							consumedPack.voltage = Math.max(consumedPack.voltage, receivedPack.voltage);
						} else {
							network.stopRequesting(tileEntity);
						}
					}

					return consumedPack;
				}
			}
		}

		return consumedPack;
	}

	public static List getNetworksFromMultipleSides(TileEntity tileEntity, EnumSet approachingDirection) {
		List connectedNetworks = new ArrayList();

		for (int i = 0; i < 6; ++i) {
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			if (approachingDirection.contains(direction)) {
				Vector3 position = new Vector3(tileEntity);
				position.modifyPositionFromSide(direction);
				TileEntity outputConductor = position.getTileEntity(tileEntity.getWorldObj());
				IElectricityNetwork electricityNetwork = getNetworkFromTileEntity(outputConductor, direction);
				if (electricityNetwork != null && !connectedNetworks.contains(connectedNetworks)) {
					connectedNetworks.add(electricityNetwork);
				}
			}
		}

		return connectedNetworks;
	}

	public static IElectricityNetwork getNetworkFromTileEntity(TileEntity tileEntity, ForgeDirection approachDirection) {
		if (tileEntity != null && tileEntity instanceof INetworkProvider) {
			if (!(tileEntity instanceof IConnector)) {
				return ((INetworkProvider) tileEntity).getNetwork();
			}

			if (((IConnector) tileEntity).canConnect(approachDirection.getOpposite())) {
				return ((INetworkProvider) tileEntity).getNetwork();
			}
		}

		return null;
	}
}
