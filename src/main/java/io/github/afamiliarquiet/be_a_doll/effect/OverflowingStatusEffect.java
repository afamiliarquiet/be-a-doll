package io.github.afamiliarquiet.be_a_doll.effect;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.NonNull;

public class OverflowingStatusEffect extends MobEffect {
	public OverflowingStatusEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public boolean applyEffectTick(@NonNull ServerLevel world, @NonNull LivingEntity entity, int amplifier) {
		if (entity instanceof Player playerEntity) {
			if (BeAMaid.isDoll(playerEntity)) { // dolls get a bit of repairs
				FoodData hungry = playerEntity.getFoodData();
				if (hungry.needsFood()) {
					hungry.eat(1, 0f);
				} else {
					hungry.eat(1, 1f);
				}
			}

			// everybody gets a little bit of regen, but effectively one amplifier level less up til overflowing 6
			if (entity.getHealth() < entity.getMaxHealth()) {
				entity.heal(0.5f);
			}
		}

		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		int i = 50 >> amplifier;
		return i == 0 || duration % i == 0;
	}

	@Override
	public void onEffectStarted(@NonNull LivingEntity entity, int amplifier) {
		BeAWitch.annihilate(entity, entity.getEffect(BeAWitch.OVERFLOWING), entity.getEffect(BeAWitch.FRAGMENTED));
		super.onEffectStarted(entity, amplifier);
	}
}
