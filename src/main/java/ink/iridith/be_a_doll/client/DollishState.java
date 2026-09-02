package ink.iridith.be_a_doll.client;

import ink.iridith.be_a_doll.BeADoll;
import net.minecraft.network.chat.Component;

public interface DollishState {
	void be_a_doll$setVariant(BeADoll.Variant variant);
	BeADoll.Variant be_a_doll$getVariant();
	void be_a_doll$setDoll(boolean doll);
	boolean be_a_doll$isDoll();
	void be_a_doll$setTargeted(boolean targeted);
	boolean be_a_doll$isTargeted();
	void be_a_doll$setDollName(Component name);
	Component be_a_doll$getDollName();
}
