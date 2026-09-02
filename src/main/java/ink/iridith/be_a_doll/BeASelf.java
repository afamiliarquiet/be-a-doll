package ink.iridith.be_a_doll;

import ink.iridith.be_a_doll.diary.BeABirdwatcher;
import ink.iridith.be_a_doll.diary.BeACollector;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import ink.iridith.be_a_doll.diary.BeAWitch;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

public class BeASelf {
	// todone - make more mixins where player is rendered, like creative inv and horse i think
	// nevermind not doing horse it'd be annoying to do it there and there's no need to play w/ essence on a horse
	public static @Nullable ItemStack clickSelf(ItemStack cursorStack, Player player, boolean inserting) {
		if (inserting) {
			if (cursorStack.is(BeACollector.ESSENCE_FRAGMENT.get())) {
				BeADoll.Variant variant = cursorStack.get(BeACollector.DOLL_VARIANT_COMPONENT);
				if (variant != null) {
					doEssencePlaceEffects(player, variant);
					return ItemStack.EMPTY;
				}
			}
		} else {
			if (cursorStack.isEmpty()) {
				doEssenceTakeEffects(player);
				ItemStack fragment = BeACollector.ESSENCE_FRAGMENT.get().getDefaultInstance();
				fragment.set(BeACollector.DOLL_VARIANT_COMPONENT, BeALibrarian.inspectSupposedPlayer(player));
				return fragment;
			}
		}

		// all else fails..
		return null;
	}

	private static void doEssencePlaceEffects(Player player, BeADoll.Variant variant) {
		player.addEffect(new MobEffectInstance(BeAWitch.OVERFLOWING, 300, 1));
		if (player.level().isClientSide()) {
			player.playNotifySound(BeABirdwatcher.ESSENCE_PLACE.get(), SoundSource.PLAYERS, 1f, player.getRandom().nextFloat() * 0.2f + 0.9f);
		} else {
			BeAMaid.setDoll(player, variant);
		}
	}

	private static void doEssenceTakeEffects(Player player) {
		MobEffectInstance fragmentation = player.getEffect(BeAWitch.FRAGMENTED);
		player.addEffect(new MobEffectInstance(
			BeAWitch.FRAGMENTED,
			1200 + (fragmentation != null ? fragmentation.getDuration() : 0),
			(fragmentation != null ? fragmentation.getAmplifier() + 1 : 0)
		));
		if (player.level().isClientSide()) {
			player.playNotifySound(BeABirdwatcher.ESSENCE_TAKE.get(), SoundSource.PLAYERS, 1f, player.getRandom().nextFloat() * 0.2f + 0.9f);
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
