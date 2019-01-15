package mffs.item.mode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import mffs.render.model.ModelPlane;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector3;

import java.util.HashSet;
import java.util.Set;

public class ItemModeTube extends ItemModeCube {

	public ItemModeTube(int i) {
		super(i, "modeTube");
	}

	public Set getExteriorPoints(IFieldInteraction projector) {
		Set fieldBlocks = new HashSet();
		ForgeDirection direction = projector.getDirection(((TileEntity) projector).worldObj, ((TileEntity) projector).xCoord, ((TileEntity) projector).yCoord, ((TileEntity) projector).zCoord);
		Vector3 posScale = projector.getPositiveScale();
		Vector3 negScale = projector.getNegativeScale();

		for (float x = (float) (-negScale.intX()); x <= (float) posScale.intX(); x += 0.5F) {
			for (float z = (float) (-negScale.intZ()); z <= (float) posScale.intZ(); z += 0.5F) {
				for (float y = (float) (-negScale.intY()); y <= (float) posScale.intY(); y += 0.5F) {
					if (direction != ForgeDirection.UP && direction != ForgeDirection.DOWN && (y == (float) (-negScale.intY()) || y == (float) posScale.intY())) {
						fieldBlocks.add(new Vector3((double) x, (double) y, (double) z));
					} else if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH || z != (float) (-negScale.intZ()) && z != (float) posScale.intZ()) {
						if (direction != ForgeDirection.WEST && direction != ForgeDirection.EAST && (x == (float) (-negScale.intX()) || x == (float) posScale.intX())) {
							fieldBlocks.add(new Vector3((double) x, (double) y, (double) z));
						}
					} else {
						fieldBlocks.add(new Vector3((double) x, (double) y, (double) z));
					}
				}
			}
		}

		return fieldBlocks;
	}

	@SideOnly(Side.CLIENT)
	public void render(IProjector projector, double x, double y, double z, float f, long ticks) {
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
		ModelPlane.INSTNACE.render();
		GL11.glTranslatef(1.0F, 0.0F, 0.0F);
		ModelPlane.INSTNACE.render();
		GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.5F, 0.0F, 0.0F);
		ModelPlane.INSTNACE.render();
		GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
		ModelPlane.INSTNACE.render();
	}
}
