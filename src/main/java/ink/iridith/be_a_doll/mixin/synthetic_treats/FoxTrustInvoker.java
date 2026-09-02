package ink.iridith.be_a_doll.mixin.synthetic_treats;

import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(Fox.class)
public interface FoxTrustInvoker {
	@Invoker("trusts")
	boolean invokeTrusts(UUID uuid);
}
