package mffs.item.mode;

import calclavia.lib.CalculationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import mffs.render.model.ModelCube;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.vector.Region3;

import java.util.HashSet;
import java.util.Set;

public class ItemModeCube extends ItemMode {

	public ItemModeCube(int i, String name) {
		super(i, name);
	}

	public ItemModeCube(int i) {
		this(i, "modeCube");
	}

	public Set getExteriorPoints(IFieldInteraction projector) {
		Set fieldBlocks = new HashSet();
		Vector3 posScale = projector.getPositiveScale();
		Vector3 negScale = projector.getNegativeScale();

		for (float x = (float) (-negScale.intX()); x <= (float) posScale.intX(); x += 0.5F) {
			for (float z = (float) (-negScale.intZ()); z <= (float) posScale.intZ(); z += 0.5F) {
				for (float y = (float) (-negScale.intY()); y <= (float) posScale.intY(); y += 0.5F) {
					if (y == (float) (-negScale.intY()) || y == (float) posScale.intY() || x == (float) (-negScale.intX()) || x == (float) posScale.intX() || z == (float) (-negScale.intZ()) || z == (float) posScale.intZ()) {
						fieldBlocks.add(new Vector3((double) x, (double) y, (double) z));
					}
				}
			}
		}

		return fieldBlocks;
	}

	public Set getInteriorPoints(IFieldInteraction projector) {
		Set fieldBlocks = new HashSet();
		Vector3 posScale = projector.getPositiveScale();
		Vector3 negScale = projector.getNegativeScale();

		for (int x = -negScale.intX(); x <= posScale.intX(); ++x) {
			for (int z = -negScale.intZ(); z <= posScale.intZ(); ++z) {
				for (int y = -negScale.intY(); y <= posScale.intY(); ++y) {
					fieldBlocks.add(new Vector3((double) x, (double) y, (double) z));
				}
			}
		}

		return fieldBlocks;
	}

	public boolean isInField(IFieldInteraction projector, Vector3 position) {
		Vector3 projectorPos = new Vector3((TileEntity) projector);
		projectorPos.add(projector.getTranslation());
		Vector3 relativePosition = position.clone().subtract(projectorPos);
		CalculationHelper.rotateByAngle(relativePosition, (double) (-projector.getRotationYaw()), (double) (-projector.getRotationPitch()));
		Region3 region = new Region3(projector.getNegativeScale().clone().multiply(-1.0D), projector.getPositiveScale());
		return region.isIn(relativePosition);
	}

	@SideOnly(Side.CLIENT)
	public void render(IProjector projector, double x, double y, double z, float f, long ticks) {
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		ModelCube.INSTNACE.render();
	}
}
