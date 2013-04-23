package mffs.item.mode;

import java.util.HashSet;
import java.util.Set;

import mffs.ModularForceFieldSystem;
import mffs.api.IProjector;
import mffs.render.model.ModelCube;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemModeSphere extends ItemMode
{
	public ItemModeSphere(int i)
	{
		super(i, "modeSphere");
	}

	@Override
	public Set<Vector3> getExteriorPoints(IProjector projector)
	{
		Set<Vector3> fieldBlocks = new HashSet<Vector3>();
		int radius = projector.getModuleCount(ModularForceFieldSystem.itemModuleScale);

		int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / radius / 2));

		for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
		{
			for (int theta_n = 0; theta_n < steps; theta_n++)
			{
				double phi = Math.PI * 2 / steps * phi_n;
				double theta = Math.PI / steps * theta_n;

				Vector3 point = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi)).multiply(radius);
				fieldBlocks.add(point);
			}
		}

		return fieldBlocks;
	}

	@Override
	public Set<Vector3> getInteriorPoints(IProjector projector)
	{
		Set<Vector3> fieldBlocks = new HashSet<Vector3>();
		int radius = projector.getModuleCount(ModularForceFieldSystem.itemModuleScale);

		int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / radius / 2));

		for (int r = 0; r < radius; r++)
		{
			for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
			{
				for (int theta_n = 0; theta_n < steps; theta_n++)
				{
					double phi = Math.PI * 2 / steps * phi_n;
					double theta = Math.PI / steps * theta_n;

					Vector3 point = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi)).multiply(radius);
					fieldBlocks.add(point);
				}
			}
		}

		return fieldBlocks;
	}

	@Override
	public boolean isInField(IProjector projector, Vector3 position)
	{
		return new Vector3((TileEntity) projector).add(projector.getTranslation()).distanceTo(position) < projector.getModuleCount(ModularForceFieldSystem.itemModuleScale);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(IProjector projector, double x1, double y1, double z1, float f, long ticks)
	{
		float scale = 0.15f;
		GL11.glScalef(scale, scale, scale);

		float radius = 1.5f;
		int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / radius / 2));

		for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
		{
			for (int theta_n = 0; theta_n < steps; theta_n++)
			{
				double phi = Math.PI * 2 / steps * phi_n;
				double theta = Math.PI / steps * theta_n;

				Vector3 vector = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
				vector.multiply(radius);
				GL11.glTranslated(vector.x, vector.y, vector.z);
				ModelCube.INSTNACE.render();
				GL11.glTranslated(-vector.x, -vector.y, -vector.z);
			}
		}
	}
}