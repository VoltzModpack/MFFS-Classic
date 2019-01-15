package mffs.api.modules;

import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import universalelectricity.core.vector.Vector3;

import java.util.Set;

public interface IProjectorMode {

	Set getExteriorPoints(IFieldInteraction var1);

	Set getInteriorPoints(IFieldInteraction var1);

	boolean isInField(IFieldInteraction var1, Vector3 var2);

	void render(IProjector var1, double var2, double var4, double var6, float var8, long var9);
}
