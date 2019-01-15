package buildcraft.api.transport;

import net.minecraft.tileentity.TileEntity;

public interface IPipe {

	boolean isWired(IPipe.WireColor var1);

	boolean hasInterface();

	TileEntity getContainer();

	boolean isWireConnectedTo(TileEntity var1, IPipe.WireColor var2);

	public static enum WireColor {
		Red,
		Blue,
		Green,
		Yellow;

		public IPipe.WireColor reverse() {
			switch (this) {
				case Red:
					return Yellow;
				case Blue:
					return Green;
				case Green:
					return Blue;
				default:
					return Red;
			}
		}
	}

	public static enum DrawingState {
		DrawingPipe,
		DrawingRedWire,
		DrawingBlueWire,
		DrawingGreenWire,
		DrawingYellowWire,
		DrawingGate;
	}
}
