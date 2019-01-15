package mffs.item.mode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.ModularForceFieldSystem;
import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import mffs.render.model.ModelCube;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector3;

import java.util.HashSet;
import java.util.Set;

public class ItemModeSphere extends ItemMode {

	public ItemModeSphere(int i) {
		super(i, "modeSphere");
	}

	public Set getExteriorPoints(IFieldInteraction projector) {
		Set fieldBlocks = new HashSet();
		int radius = projector.getModuleCount(ModularForceFieldSystem.itemModuleScale, new int[0]);
		int steps = (int) Math.ceil(3.141592653589793D / Math.atan(1.0D / (double) radius / 2.0D));

		for (int phi_n = 0; phi_n < 2 * steps; ++phi_n) {
			for (int theta_n = 0; theta_n < steps; ++theta_n) {
				double phi = 6.283185307179586D / (double) steps * (double) phi_n;
				double theta = 3.141592653589793D / (double) steps * (double) theta_n;
				Vector3 point = (new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi))).multiply((double) radius);
				fieldBlocks.add(point);
			}
		}

		return fieldBlocks;
	}

	public Set getInteriorPoints(IFieldInteraction projector) {
		Set fieldBlocks = new HashSet();
		Vector3 translation = projector.getTranslation();
		int radius = projector.getModuleCount(ModularForceFieldSystem.itemModuleScale, new int[0]);

		for (int x = -radius; x <= radius; ++x) {
			for (int z = -radius; z <= radius; ++z) {
				for (int y = -radius; y <= radius; ++y) {
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
		return (new Vector3((TileEntity) projector)).add(projector.getTranslation()).distanceTo(position) < (double) projector.getModuleCount(ModularForceFieldSystem.itemModuleScale, new int[0]);
	}

	@SideOnly(Side.CLIENT)
	public void render(IProjector projector, double x1, double y1, double z1, float f, long ticks) {
		float scale = 0.15F;
		GL11.glScalef(scale, scale, scale);
		float radius = 1.5F;
		int steps = (int) Math.ceil(3.141592653589793D / Math.atan(1.0D / (double) radius / 2.0D));

		for (int phi_n = 0; phi_n < 2 * steps; ++phi_n) {
			for (int theta_n = 0; theta_n < steps; ++theta_n) {
				double phi = 6.283185307179586D / (double) steps * (double) phi_n;
				double theta = 3.141592653589793D / (double) steps * (double) theta_n;
				Vector3 vector = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
				vector.multiply((double) radius);
				GL11.glTranslated(vector.x, vector.y, vector.z);
				ModelCube.INSTNACE.render();
				GL11.glTranslated(-vector.x, -vector.y, -vector.z);
			}
		}

	}
}
