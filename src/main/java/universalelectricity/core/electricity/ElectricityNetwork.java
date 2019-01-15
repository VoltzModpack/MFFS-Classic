package universalelectricity.core.electricity;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IConnectionProvider;
import universalelectricity.core.block.INetworkProvider;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.path.PathfinderChecker;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

import java.util.*;
import java.util.Map.Entry;

public class ElectricityNetwork implements IElectricityNetwork {

	private final HashMap producers = new HashMap();
	private final HashMap consumers = new HashMap();
	private final Set conductors = new HashSet();

	public ElectricityNetwork() {
	}

	public ElectricityNetwork(IConductor... conductors) {
		this.conductors.addAll(Arrays.asList(conductors));
	}

	public void startProducing(TileEntity tileEntity, ElectricityPack electricityPack) {
		if (tileEntity != null && electricityPack.getWatts() > 0.0D) {
			this.producers.put(tileEntity, electricityPack);
		}

	}

	public void startProducing(TileEntity tileEntity, double amperes, double voltage) {
		this.startProducing(tileEntity, new ElectricityPack(amperes, voltage));
	}

	public boolean isProducing(TileEntity tileEntity) {
		return this.producers.containsKey(tileEntity);
	}

	public void stopProducing(TileEntity tileEntity) {
		this.producers.remove(tileEntity);
	}

	public void startRequesting(TileEntity tileEntity, ElectricityPack electricityPack) {
		if (tileEntity != null && electricityPack.getWatts() > 0.0D) {
			this.consumers.put(tileEntity, electricityPack);
		}

	}

	public void startRequesting(TileEntity tileEntity, double amperes, double voltage) {
		this.startRequesting(tileEntity, new ElectricityPack(amperes, voltage));
	}

	public boolean isRequesting(TileEntity tileEntity) {
		return this.consumers.containsKey(tileEntity);
	}

	public void stopRequesting(TileEntity tileEntity) {
		this.consumers.remove(tileEntity);
	}

	public ElectricityPack getProduced(TileEntity... ignoreTiles) {
		ElectricityPack totalElectricity = new ElectricityPack(0.0D, 0.0D);
		Iterator it = this.producers.entrySet().iterator();

		while (true) {
			label50:
			while (true) {
				Entry pairs;
				do {
					if (!it.hasNext()) {
						return totalElectricity;
					}

					pairs = (Entry) it.next();
				} while (pairs == null);

				TileEntity tileEntity = (TileEntity) pairs.getKey();
				if (tileEntity == null) {
					it.remove();
				} else if (tileEntity.isInvalid()) {
					it.remove();
				} else if (tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity) {
					it.remove();
				} else {
					if (ignoreTiles != null) {
						TileEntity[] arr$ = ignoreTiles;
						int len$ = ignoreTiles.length;

						for (int i$ = 0; i$ < len$; ++i$) {
							TileEntity ignoreTile = arr$[i$];
							if (tileEntity == ignoreTile) {
								continue label50;
							}
						}
					}

					ElectricityPack pack = (ElectricityPack) pairs.getValue();
					if (pairs.getKey() != null && pairs.getValue() != null && pack != null) {
						double newWatts = totalElectricity.getWatts() + pack.getWatts();
						double newVoltage = Math.max(totalElectricity.voltage, pack.voltage);
						totalElectricity.amperes = newWatts / newVoltage;
						totalElectricity.voltage = newVoltage;
					}
				}
			}
		}
	}

	public ElectricityPack getRequest(TileEntity... ignoreTiles) {
		ElectricityPack totalElectricity = this.getRequestWithoutReduction();
		totalElectricity.amperes = Math.max(totalElectricity.amperes - this.getProduced(ignoreTiles).amperes, 0.0D);
		return totalElectricity;
	}

	public ElectricityPack getRequestWithoutReduction() {
		ElectricityPack totalElectricity = new ElectricityPack(0.0D, 0.0D);
		Iterator it = this.consumers.entrySet().iterator();

		while (it.hasNext()) {
			Entry pairs = (Entry) it.next();
			if (pairs != null) {
				TileEntity tileEntity = (TileEntity) pairs.getKey();
				if (tileEntity == null) {
					it.remove();
				} else if (tileEntity.isInvalid()) {
					it.remove();
				} else if (tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity) {
					it.remove();
				} else {
					ElectricityPack pack = (ElectricityPack) pairs.getValue();
					if (pack != null) {
						totalElectricity.amperes += pack.amperes;
						totalElectricity.voltage = Math.max(totalElectricity.voltage, pack.voltage);
					}
				}
			}
		}

		return totalElectricity;
	}

	public ElectricityPack consumeElectricity(TileEntity tileEntity) {
		ElectricityPack totalElectricity = new ElectricityPack(0.0D, 0.0D);

		try {
			ElectricityPack tileRequest = (ElectricityPack) this.consumers.get(tileEntity);
			if (this.consumers.containsKey(tileEntity) && tileRequest != null) {
				totalElectricity = this.getProduced();
				if (totalElectricity.getWatts() > 0.0D) {
					ElectricityPack totalRequest = this.getRequestWithoutReduction();
					totalElectricity.amperes *= tileRequest.amperes / totalRequest.amperes;
					int distance = this.conductors.size();
					double ampsReceived = totalElectricity.amperes - totalElectricity.amperes * totalElectricity.amperes * this.getTotalResistance() / totalElectricity.voltage;
					double voltsReceived = totalElectricity.voltage - totalElectricity.amperes * this.getTotalResistance();
					totalElectricity.amperes = ampsReceived;
					totalElectricity.voltage = voltsReceived;
					return totalElectricity;
				}
			}
		} catch (Exception var10) {
			FMLLog.severe("Failed to consume electricity!", new Object[0]);
			var10.printStackTrace();
		}

		return totalElectricity;
	}

	public HashMap getProducers() {
		return this.producers;
	}

	public List getProviders() {
		List providers = new ArrayList();
		providers.addAll(this.producers.keySet());
		return providers;
	}

	public HashMap getConsumers() {
		return this.consumers;
	}

	public List getReceivers() {
		List receivers = new ArrayList();
		receivers.addAll(this.consumers.keySet());
		return receivers;
	}

	public void cleanUpConductors() {
		Iterator it = this.conductors.iterator();

		while (it.hasNext()) {
			IConductor conductor = (IConductor) it.next();
			if (conductor == null) {
				it.remove();
			} else if (((TileEntity) conductor).isInvalid()) {
				it.remove();
			} else {
				conductor.setNetwork(this);
			}
		}

	}

	public void refreshConductors() {
		this.cleanUpConductors();

		try {
			Iterator it = this.conductors.iterator();

			while (it.hasNext()) {
				IConductor conductor = (IConductor) it.next();
				conductor.updateAdjacentConnections();
			}
		} catch (Exception var3) {
			FMLLog.severe("Universal Electricity: Failed to refresh conductor.", new Object[0]);
			var3.printStackTrace();
		}

	}

	public double getTotalResistance() {
		double resistance = 0.0D;

		IConductor conductor;
		for (Iterator i$ = this.conductors.iterator(); i$.hasNext(); resistance += conductor.getResistance()) {
			conductor = (IConductor) i$.next();
		}

		return resistance;
	}

	public double getLowestCurrentCapacity() {
		double lowestAmp = 0.0D;
		Iterator i$ = this.conductors.iterator();

		while (true) {
			IConductor conductor;
			do {
				if (!i$.hasNext()) {
					return lowestAmp;
				}

				conductor = (IConductor) i$.next();
			} while (lowestAmp != 0.0D && conductor.getCurrentCapcity() >= lowestAmp);

			lowestAmp = conductor.getCurrentCapcity();
		}
	}

	public Set getConductors() {
		return this.conductors;
	}

	public void mergeConnection(IElectricityNetwork network) {
		if (network != null && network != this) {
			ElectricityNetwork newNetwork = new ElectricityNetwork();
			newNetwork.getConductors().addAll(this.getConductors());
			newNetwork.getConductors().addAll(network.getConductors());
			newNetwork.cleanUpConductors();
		}

	}

	public void splitNetwork(IConnectionProvider splitPoint) {
		if (splitPoint instanceof TileEntity) {
			this.getConductors().remove(splitPoint);
			ForgeDirection[] arr$ = ForgeDirection.values();
			int i = arr$.length;

			for (int i$ = 0; i$ < i; ++i$) {
				ForgeDirection dir = arr$[i$];
				if (dir != ForgeDirection.UNKNOWN) {
					Vector3 splitVec = new Vector3((TileEntity) splitPoint);
					TileEntity tileAroundSplit = VectorHelper.getTileEntityFromSide(((TileEntity) splitPoint).worldObj, splitVec, dir);
					if (this.producers.containsKey(tileAroundSplit)) {
						this.stopProducing(tileAroundSplit);
						this.stopRequesting(tileAroundSplit);
					}
				}
			}

			TileEntity[] connectedBlocks = splitPoint.getAdjacentConnections();

			for (i = 0; i < connectedBlocks.length; ++i) {
				TileEntity connectedBlockA = connectedBlocks[i];
				if (connectedBlockA instanceof IConnectionProvider) {
					for (int ii = 0; ii < connectedBlocks.length; ++ii) {
						TileEntity connectedBlockB = connectedBlocks[ii];
						if (connectedBlockA != connectedBlockB && connectedBlockB instanceof IConnectionProvider) {
							Pathfinder finder = new PathfinderChecker(((TileEntity) splitPoint).worldObj, (IConnectionProvider) connectedBlockB, new IConnectionProvider[]{splitPoint});
							finder.init(new Vector3(connectedBlockA));
							if (finder.results.size() > 0) {
								Iterator i$ = finder.closedSet.iterator();

								while (i$.hasNext()) {
									Vector3 node = (Vector3) i$.next();
									TileEntity nodeTile = node.getTileEntity(((TileEntity) splitPoint).worldObj);
									if (nodeTile instanceof INetworkProvider && nodeTile != splitPoint) {
										((INetworkProvider) nodeTile).setNetwork(this);
									}
								}
							} else {
								IElectricityNetwork newNetwork = new ElectricityNetwork();
								Iterator i$ = finder.closedSet.iterator();

								while (i$.hasNext()) {
									Vector3 node = (Vector3) i$.next();
									TileEntity nodeTile = node.getTileEntity(((TileEntity) splitPoint).worldObj);
									if (nodeTile instanceof INetworkProvider && nodeTile != splitPoint) {
										newNetwork.getConductors().add((IConductor) nodeTile);
									}
								}

								newNetwork.cleanUpConductors();
							}
						}
					}
				}
			}
		}

	}

	public String toString() {
		return "ElectricityNetwork[" + this.hashCode() + "|Wires:" + this.conductors.size() + "]";
	}
}
