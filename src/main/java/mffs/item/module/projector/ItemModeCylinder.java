package mffs.item.module.projector;

import calclavia.lib.CalculationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import mffs.item.mode.ItemMode;
import mffs.render.model.ModelCube;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector3;

import java.util.HashSet;
import java.util.Set;

public class ItemModeCylinder extends ItemMode {

	private static final int RADIUS_Expansion = 0;

	public ItemModeCylinder(int i) {
		super(i, "modeCylinder");
	}

	public Set getExteriorPoints(IFieldInteraction projector) {
		Set fieldBlocks = new HashSet();
		Vector3 posScale = projector.getPositiveScale();
		Vector3 negScale = projector.getNegativeScale();
		int radius = (posScale.intX() + negScale.intX() + posScale.intZ() + negScale.intZ()) / 2;
		int height = posScale.intY() + negScale.intY();

		for (float x = (float) (-radius); x <= (float) radius; ++x) {
			for (float z = (float) (-radius); z <= (float) radius; ++z) {
				for (float y = 0.0F; y < (float) height; ++y) {
					if ((y == 0.0F || y == (float) (height - 1)) && x * x + z * z + 0.0F <= (float) (radius * radius)) {
						fieldBlocks.add(new Vector3((double) x, (double) y, (double) z));
					}

					if (x * x + z * z + 0.0F <= (float) (radius * radius) && x * x + z * z + 0.0F >= (float) ((radius - 1) * (radius - 1))) {
						fieldBlocks.add(new Vector3((double) x, (double) y, (double) z));
					}
				}
			}
		}

		return fieldBlocks;
	}

	public Set getInteriorPoints(IFieldInteraction projector) {
		Set fieldBlocks = new HashSet();
		Vector3 translation = projector.getTranslation();
		Vector3 posScale = projector.getPositiveScale();
		Vector3 negScale = projector.getNegativeScale();
		int radius = (posScale.intX() + negScale.intX() + posScale.intZ() + negScale.intZ()) / 2;
		int height = posScale.intY() + negScale.intY();

		for (int x = -radius; x <= radius; ++x) {
			for (int z = -radius; z <= radius; ++z) {
				for (int y = 0; y < height; ++y) {
					Vector3 position = new Vector3((double) x, (double) y, (double) z);
					if (this.isInField(projector, Vector3.add(position, new Vector3((TileEntity) projector)).add(translation))) {
						fieldBlocks.add(position);
					}
				}
			}
		}

		return fieldBlocks;
	}

	public boolean isInField(IFieldInteraction projector, Vector3 position) {
		Vector3 posScale = projector.getPositiveScale();
		Vector3 negScale = projector.getNegativeScale();
		int radius = (posScale.intX() + negScale.intX() + posScale.intZ() + negScale.intZ()) / 2;
		Vector3 projectorPos = new Vector3((TileEntity) projector);
		projectorPos.add(projector.getTranslation());
		Vector3 relativePosition = position.clone().subtract(projectorPos);
		CalculationHelper.rotateByAngle(relativePosition, (double) (-projector.getRotationYaw()), (double) (-projector.getRotationPitch()));
		return relativePosition.x * relativePosition.x + relativePosition.z * relativePosition.z + 0.0D <= (double) (radius * radius);
	}

	@SideOnly(Side.CLIENT)
	public void render(IProjector projector, double x, double y, double z, float f, long ticks) {
		float scale = 0.15F;
		float detail = 0.5F;
		GL11.glScalef(scale, scale, scale);
		float radius = 1.5F;
		int i = 0;

		for (float renderX = -radius; renderX <= radius; renderX += detail) {
			for (float renderZ = -radius; renderZ <= radius; renderZ += detail) {
				for (float renderY = -radius; renderY <= radius; renderY += detail) {
					if (renderX * renderX + renderZ * renderZ + 0.0F <= radius * radius && renderX * renderX + renderZ * renderZ + 0.0F >= (radius - 1.0F) * (radius - 1.0F) || (renderY == 0.0F || renderY == radius - 1.0F) && renderX * renderX + renderZ * renderZ + 0.0F <= radius * radius) {
						if (i % 2 == 0) {
							Vector3 vector = new Vector3((double) renderX, (double) renderY, (double) renderZ);
							GL11.glTranslated(vector.x, vector.y, vector.z);
							ModelCube.INSTNACE.render();
							GL11.glTranslated(-vector.x, -vector.y, -vector.z);
						}

						++i;
					}
				}
			}
		}

	}
}
