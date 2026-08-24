package io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Fox.class)
public interface FoxTrustInvoker {
	@Invoker("trusts")
	public boolean invokeTrusts(LivingEntity test);
}
