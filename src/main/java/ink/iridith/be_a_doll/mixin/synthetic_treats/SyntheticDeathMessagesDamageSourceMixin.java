package ink.iridith.be_a_doll.mixin.synthetic_treats;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import ink.iridith.be_a_doll.BeAMaid;
import ink.iridith.be_a_doll.diary.BeAResearcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DamageSource.class)
public class SyntheticDeathMessagesDamageSourceMixin {
	@Shadow
	@Final
	private Holder<DamageType> type;

	@ModifyExpressionValue(method = "getLocalizedDeathMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageType;msgId()Ljava/lang/String;"))
	private String addDollQualifierIfIWant(String original, @Local(argsOnly = true) LivingEntity killed) {
		if (killed instanceof Player beWaryOfDoll && BeAMaid.isDoll(beWaryOfDoll)) {
			if (type.is(BeAResearcher.DOLL_MODIFIES_MESSAGE)) {
				return "doll." + original;
			}
		}
		return original;
	}
}
