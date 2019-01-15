package universalelectricity.core.path;

import net.minecraftforge.common.util.ForgeDirection;
import universalelectricity.core.vector.Vector3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PathfinderAStar extends Pathfinder {

	public IPathCallBack callBackCheck;
	public Set openSet;
	public HashMap navigationMap;
	public HashMap gScore;
	public HashMap fScore;
	public Vector3 goal;

	public PathfinderAStar(IPathCallBack callBack, Vector3 goal) {
		super(callBack);
		this.goal = goal;
	}

	public boolean findNodes(Vector3 start) {
		this.openSet.add(start);
		this.gScore.put(start, 0.0D);
		this.fScore.put(start, (Double) this.gScore.get(start) + this.getHeuristicEstimatedCost(start, this.goal));

		label64:
		while (!this.openSet.isEmpty()) {
			Vector3 currentNode = null;
			double lowestFScore = 0.0D;
			Iterator i$ = this.openSet.iterator();

			while (true) {
				Vector3 neighbor;
				do {
					if (!i$.hasNext()) {
						if (currentNode == null) {
							return false;
						}

						if (this.callBackCheck.onSearch(this, currentNode)) {
							return false;
						}

						if (currentNode.equals(this.goal)) {
							super.results = this.reconstructPath(this.navigationMap, this.goal);
							return true;
						}

						this.openSet.remove(currentNode);
						super.closedSet.add(currentNode);
						i$ = this.getNeighborNodes(currentNode).iterator();

						while (true) {
							double tentativeGScore;
							do {
								do {
									if (!i$.hasNext()) {
										continue label64;
									}

									neighbor = (Vector3) i$.next();
									tentativeGScore = (Double) this.gScore.get(currentNode) + currentNode.distanceTo(neighbor);
								} while (super.closedSet.contains(neighbor) && tentativeGScore >= (Double) this.gScore.get(neighbor));
							} while (this.openSet.contains(neighbor) && tentativeGScore >= (Double) this.gScore.get(neighbor));

							this.navigationMap.put(neighbor, currentNode);
							this.gScore.put(neighbor, tentativeGScore);
							this.fScore.put(neighbor, (Double) this.gScore.get(neighbor) + this.getHeuristicEstimatedCost(neighbor, this.goal));
							this.openSet.add(neighbor);
						}
					}

					neighbor = (Vector3) i$.next();
				} while (currentNode != null && (Double) this.fScore.get(neighbor) >= lowestFScore);

				currentNode = neighbor;
				lowestFScore = (Double) this.fScore.get(neighbor);
			}
		}

		return false;
	}

	public Pathfinder reset() {
		this.openSet = new HashSet();
		this.navigationMap = new HashMap();
		return super.reset();
	}

	public Set reconstructPath(HashMap nagivationMap, Vector3 current_node) {
		Set path = new HashSet();
		path.add(current_node);
		if (nagivationMap.containsKey(current_node)) {
			path.addAll(this.reconstructPath(nagivationMap, (Vector3) nagivationMap.get(current_node)));
			return path;
		} else {
			return path;
		}
	}

	public double getHeuristicEstimatedCost(Vector3 start, Vector3 goal) {
		return start.distanceTo(goal);
	}

	public Set getNeighborNodes(Vector3 vector) {
		if (this.callBackCheck != null) {
			return this.callBackCheck.getConnectedNodes(this, vector);
		} else {
			Set neighbors = new HashSet();

			for (int i = 0; i < 6; ++i) {
				neighbors.add(vector.clone().modifyPositionFromSide(ForgeDirection.getOrientation(i)));
			}

			return neighbors;
		}
	}
}
