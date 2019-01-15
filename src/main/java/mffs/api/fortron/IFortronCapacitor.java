package mffs.api.fortron;

import java.util.Set;

public interface IFortronCapacitor {

	Set getLinkedDevices();

	int getTransmissionRange();

	int getTransmissionRate();
}
