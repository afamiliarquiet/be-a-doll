package ink.iridith.be_a_doll.mixin.shoulder_riding;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class RiptideIgnorePassengersLivingEntityMixin extends Entity {
	public RiptideIgnorePassengersLivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@ModifyVariable(method = "checkAutoSpinAttack", at = @At("STORE"))
	private Entity isTarget(Entity possibleTarget) {
		// should hopefully effectively make the instanceof check false
		return this.equals(possibleTarget.getVehicle()) ? null : possibleTarget;
	}
}
