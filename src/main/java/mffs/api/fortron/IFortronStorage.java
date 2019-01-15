package mffs.api.fortron;

public interface IFortronStorage {

	void setFortronEnergy(int var1);

	int getFortronEnergy();

	int getFortronCapacity();

	int requestFortron(int var1, boolean var2);

	int provideFortron(int var1, boolean var2);
}
