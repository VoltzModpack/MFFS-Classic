package mffs;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import universalelectricity.core.vector.Vector3;

import java.lang.reflect.Method;

public class ManipulatorHelper {

	public static final String[] CHUNK_RELIGHT_BLOCK = new String[]{"relightBlock", "func_76615_h"};
	public static final String[] CHUNK_PROPOGATE_SKY_LIGHT_OCCLUSION = new String[]{"propagateSkylightOcclusion", "func_76595_e"};

	public static void setBlockSneaky(World world, Vector3 position, int id, int metadata, TileEntity tileEntity) {
		Chunk chunk = world.getChunkFromChunkCoords(position.intX() >> 4, position.intZ() >> 4);
		Vector3 chunkPosition = new Vector3((double) (position.intX() & 15), (double) (position.intY() & 15), (double) (position.intZ() & 15));
		int heightMapIndex = chunkPosition.intZ() << 4 | chunkPosition.intX();
		if (position.intY() >= chunk.precipitationHeightMap[heightMapIndex] - 1) {
			chunk.precipitationHeightMap[heightMapIndex] = -999;
		}

		int heightMapValue = chunk.heightMap[heightMapIndex];
		world.removeBlockTileEntity(position.intX(), position.intY(), position.intZ());
		ExtendedBlockStorage extendedBlockStorage = chunk.getBlockStorageArray()[position.intY() >> 4];
		if (extendedBlockStorage == null) {
			extendedBlockStorage = new ExtendedBlockStorage(position.intY() >> 4 << 4, !world.provider.hasNoSky);
			chunk.getBlockStorageArray()[position.intY() >> 4] = extendedBlockStorage;
		}

		extendedBlockStorage.setExtBlockID(chunkPosition.intX(), chunkPosition.intY(), chunkPosition.intZ(), id);
		extendedBlockStorage.setExtBlockMetadata(chunkPosition.intX(), chunkPosition.intY(), chunkPosition.intZ(), metadata);
		if (position.intY() >= heightMapValue) {
			chunk.generateSkylightMap();
		} else {
			if (chunk.getBlockLightOpacity(chunkPosition.intX(), position.intY(), chunkPosition.intZ()) > 0) {
				if (position.intY() >= heightMapValue) {
					relightBlock(chunk, Vector3.add(chunkPosition, new Vector3(0.0D, 1.0D, 0.0D)));
				}
			} else if (position.intY() == heightMapValue - 1) {
				relightBlock(chunk, chunkPosition);
			}

			propagateSkylightOcclusion(chunk, chunkPosition);
		}

		chunk.isModified = true;
		world.updateAllLightTypes(position.intX(), position.intY(), position.intZ());
		if (tileEntity != null) {
			world.setBlockTileEntity(position.intX(), position.intY(), position.intZ(), tileEntity);
		}

		world.markBlockForUpdate(position.intX(), position.intY(), position.intZ());
	}

	public static void relightBlock(Chunk chunk, Vector3 position) {
		try {
			Method m = ReflectionHelper.findMethod(Chunk.class, (Object) null, CHUNK_RELIGHT_BLOCK, new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
			m.invoke(chunk, position.intX(), position.intY(), position.intZ());
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}

	public static void propagateSkylightOcclusion(Chunk chunk, Vector3 position) {
		try {
			Method m = ReflectionHelper.findMethod(Chunk.class, (Object) null, CHUNK_PROPOGATE_SKY_LIGHT_OCCLUSION, new Class[]{Integer.TYPE, Integer.TYPE});
			m.invoke(chunk, position.intX(), position.intZ());
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}
}
