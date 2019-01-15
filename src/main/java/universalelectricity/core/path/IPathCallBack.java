package universalelectricity.core.path;

import universalelectricity.core.vector.Vector3;

import java.util.Set;

public interface IPathCallBack {

	Set getConnectedNodes(Pathfinder var1, Vector3 var2);

	boolean onSearch(Pathfinder var1, Vector3 var2);
}
