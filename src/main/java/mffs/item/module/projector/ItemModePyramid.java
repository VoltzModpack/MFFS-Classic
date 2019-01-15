package mffs.item.module.projector;

import calclavia.lib.CalculationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import mffs.item.mode.ItemMode;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.vector.Region3;

import java.util.HashSet;
import java.util.Set;

public class ItemModePyramid extends ItemMode {

	public ItemModePyramid(int i) {
		super(i, "modePyramid");
	}

	public Set getExteriorPoints(IFieldInteraction projector) {
		Set fieldBlocks = new HashSet();
		Vector3 posScale = projector.getPositiveScale();
		Vector3 negScale = projector.getNegativeScale();
		int xStretch = posScale.intX() + negScale.intX();
		int yStretch = posScale.intY() + negScale.intY();
		int zStretch = posScale.intZ() + negScale.intZ();
		Vector3 translation = new Vector3(0.0D, (double) (-negScale.intY()), 0.0D);
		int inverseThickness = true;

		for (float y = 0.0F; y <= (float) yStretch; ++y) {
			for (float x = (float) (-xStretch); x <= (float) xStretch; ++x) {
				for (float z = (float) (-zStretch); z <= (float) zStretch; ++z) {
					double yTest = (double) (y / (float) yStretch * 8.0F);
					double xzPositivePlane = (double) ((1.0F - x / (float) xStretch - z / (float) zStretch) * 8.0F);
					double xzNegativePlane = (double) ((1.0F + x / (float) xStretch - z / (float) zStretch) * 8.0F);
					if (x >= 0.0F && z >= 0.0F && Math.round(xzPositivePlane) == Math.round(yTest)) {
						fieldBlocks.add((new Vector3((double) x, (double) y, (double) z)).add(translation));
						fieldBlocks.add((new Vector3((double) x, (double) y, (double) (-z))).add(translation));
					}

					if (x <= 0.0F && z >= 0.0F && Math.round(xzNegativePlane) == Math.round(yTest)) {
						fieldBlocks.add((new Vector3((double) x, (double) y, (double) (-z))).add(translation));
						fieldBlocks.add((new Vector3((double) x, (double) y, (double) z)).add(translation));
					}

					if (y == 0.0F && Math.abs(x) + Math.abs(z) < (float) ((xStretch + yStretch) / 2)) {
						fieldBlocks.add((new Vector3((double) x, (double) y, (double) z)).add(translation));
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
		int xStretch = posScale.intX() + negScale.intX();
		int yStretch = posScale.intY() + negScale.intY();
		int zStretch = posScale.intZ() + negScale.intZ();
		Vector3 translation = new Vector3(0.0D, -0.4D, 0.0D);

		for (float x = (float) (-xStretch); x <= (float) xStretch; ++x) {
			for (float z = (float) (-zStretch); z <= (float) zStretch; ++z) {
				for (float y = 0.0F; y <= (float) yStretch; ++y) {
					Vector3 position = (new Vector3((double) x, (double) y, (double) z)).add(translation);
					if (this.isInField(projector, Vector3.add(position, new Vector3((TileEntity) projector)))) {
						fieldBlocks.add(position);
					}
				}
			}
		}

		return fieldBlocks;
	}

	public boolean isInField(IFieldInteraction projector, Vector3 position) {
		Vector3 posScale = projector.getPositiveScale().clone();
		Vector3 negScale = projector.getNegativeScale().clone();
		int xStretch = posScale.intX() + negScale.intX();
		int yStretch = posScale.intY() + negScale.intY();
		int zStretch = posScale.intZ() + negScale.intZ();
		Vector3 projectorPos = new Vector3((TileEntity) projector);
		projectorPos.add(projector.getTranslation());
		projectorPos.add(new Vector3(0.0D, (double) (-negScale.intY() + 1), 0.0D));
		Vector3 relativePosition = position.clone().subtract(projectorPos);
		CalculationHelper.rotateByAngle(relativePosition, (double) (-projector.getRotationYaw()), (double) (-projector.getRotationPitch()));
		Region3 region = new Region3(negScale.multiply(-1.0D), posScale);
		return region.isIn(relativePosition) && relativePosition.y > 0.0D && 1.0D - Math.abs(relativePosition.x) / (double) xStretch - Math.abs(relativePosition.z) / (double) zStretch > relativePosition.y / (double) yStretch;
	}

	@SideOnly(Side.CLIENT)
	public void render(IProjector projector, double x, double y, double z, float f, long ticks) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float height = 0.5F;
		float width = 0.3F;
		int uvMaxX = 2;
		int uvMaxY = 2;
		Vector3 translation = new Vector3(0.0D, -0.4D, 0.0D);
		tessellator.startDrawing(6);
		tessellator.setColorRGBA(72, 198, 255, 255);
		tessellator.addVertexWithUV(0.0D + translation.x, 0.0D + translation.y, 0.0D + translation.z, 0.0D, 0.0D);
		tessellator.addVertexWithUV((double) (-width) + translation.x, (double) height + translation.y, (double) (-width) + translation.z, (double) (-uvMaxX), (double) (-uvMaxY));
		tessellator.addVertexWithUV((double) (-width) + translation.x, (double) height + translation.y, (double) width + translation.z, (double) (-uvMaxX), (double) uvMaxY);
		tessellator.addVertexWithUV((double) width + translation.x, (double) height + translation.y, (double) width + translation.z, (double) uvMaxX, (double) uvMaxY);
		tessellator.addVertexWithUV((double) width + translation.x, (double) height + translation.y, (double) (-width) + translation.z, (double) uvMaxX, (double) (-uvMaxY));
		tessellator.addVertexWithUV((double) (-width) + translation.x, (double) height + translation.y, (double) (-width) + translation.z, (double) (-uvMaxX), (double) (-uvMaxY));
		tessellator.draw();
		GL11.glPopMatrix();
	}
}
