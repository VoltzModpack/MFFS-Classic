package mffs.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.base.TileEntityBase;
import mffs.render.model.ModelForceManipulator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderForceManipulator extends TileEntitySpecialRenderer {

	public static final String TEXTURE_ON = "forceManipulator_on.png";
	public static final String TEXTURE_OFF = "forceManipulator_off.png";
	public static final ModelForceManipulator MODEL = new ModelForceManipulator();

	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f) {
		TileEntityBase tileEntity = (TileEntityBase) t;
		if (tileEntity.isActive()) {
			this.bindTextureByName("/mods/mffs/textures/models/forceManipulator_on.png");
		} else {
			this.bindTextureByName("/mods/mffs/textures/models/forceManipulator_off.png");
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		MODEL.render(0.0625F);
		GL11.glPopMatrix();
	}
}
