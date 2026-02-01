package io.github.afamiliarquiet.be_a_doll;

import io.github.afamiliarquiet.be_a_doll.diary.BeABirdwatcher;
import io.github.afamiliarquiet.be_a_doll.diary.BeACollector;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BeASelf {
	// todone - make more mixins where player is rendered, like creative inv and horse i think
	// nevermind not doing horse it'd be annoying to do it there and there's no need to play w/ essence on a horse
	public static @Nullable ItemStack clickSelf(ItemStack cursorStack, PlayerEntity player, boolean inserting) {
		if (inserting) {
			if (cursorStack.isOf(BeACollector.ESSENCE_FRAGMENT)) {
				Optional<BeADoll.Variant> variant = BeACollector.getDollVariant(cursorStack);
				if (variant.isPresent()) {
					doEssencePlaceEffects(player, variant.get());
					return ItemStack.EMPTY;
				}
			}
		} else {
			if (cursorStack.isEmpty()) {
				doEssenceTakeEffects(player);
				ItemStack fragment = BeACollector.ESSENCE_FRAGMENT.getDefaultStack();
				BeACollector.setDollVariant(fragment, BeALibrarian.inspectSupposedPlayer(player));
				return fragment;
			}
		}

		// all else fails..
		return null;
	}

	private static void doEssencePlaceEffects(PlayerEntity player, BeADoll.Variant variant) {
		if (player.getWorld().isClient()) {
			player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), BeABirdwatcher.ESSENCE_PLACE, SoundCategory.PLAYERS, 1f, player.getRandom().nextFloat() * 0.2f + 0.9f);
		} else {
			player.addStatusEffect(new StatusEffectInstance(BeAWitch.OVERFLOWING.value(), 300, 1));
			BeAMaid.setDoll(player, variant);
		}
	}

	private static void doEssenceTakeEffects(PlayerEntity player) {
		StatusEffectInstance fragmentation = player.getStatusEffect(BeAWitch.FRAGMENTED.value());
		player.addStatusEffect(new StatusEffectInstance(
			BeAWitch.FRAGMENTED.value(),
			1200 + (fragmentation != null ? fragmentation.getDuration() : 0),
			(fragmentation != null ? fragmentation.getAmplifier() + 1 : 0)
		));
		if (player.getWorld().isClient()) {
			player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), BeABirdwatcher.ESSENCE_TAKE, SoundCategory.PLAYERS, 1f, player.getRandom().nextFloat() * 0.2f + 0.9f);
		}
	}

	public static boolean isMouseInSurvivalSelf(double mouseX, double mouseY, int screenX, int screenY) {
		// inset from the black rectangle by 13
		int inset = 13;
		return mouseX > screenX + inset + 26
				&& mouseX < screenX - inset + 75
				&& mouseY > screenY + inset + 8
				&& mouseY < screenY - inset + 78;
	}

	public static boolean isMouseInCreativeSelf(double mouseX, double mouseY, int screenX, int screenY) {
		// inset from the black rectangle by ...
		int inset = 7;
		return mouseX > screenX + inset + 73
				&& mouseX < screenX - inset + 105
				&& mouseY > screenY + inset + 6
				&& mouseY < screenY - inset + 49;
	}
}
