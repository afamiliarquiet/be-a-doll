package io.github.afamiliarquiet.be_a_doll.item;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeABirdwatcher;
import io.github.afamiliarquiet.be_a_doll.diary.BeACollector;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollRepairedLetter;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class DollcraftItem extends Item {

	public DollcraftItem(Properties settings) {
		super(settings.useCooldown(1.3f)
			.component(DataComponents.WEAPON, new Weapon(1)));
	}

	// care for self
	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		if (BeAMaid.isDoll(user) && !findCareMaterial(user, user).isEmpty()) {
			user.startUsingItem(hand);
			return InteractionResult.CONSUME;
		}

		return super.use(world, user, hand);
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BRUSH;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity user) {
		return 62;
	}

	@Override
	public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (user instanceof Player praiseTheDoll) {
			ItemStack material = findCareMaterial(praiseTheDoll, praiseTheDoll);
			if (material.isEmpty()) {
				user.releaseUsingItem();
			} else {
				if (remainingUseTicks < 6 && remainingUseTicks > -6) { // trying to account for latency lag with the -6
					if (remainingUseTicks % 4 == 3) {
						praiseTheDoll.playSound(BeABirdwatcher.CARE_COMPLETE, 1f, praiseTheDoll.getRandom().nextFloat() * 0.2f + 0.9f);
					}
				} else if (remainingUseTicks % 10 == 7) {
					spawnRepairParticles(praiseTheDoll, material, 5);
					SoundEvent careSound = BeALibrarian.inspectDollMaterial(praiseTheDoll).getCareSound();
					praiseTheDoll.playSound(careSound, 1f, praiseTheDoll.getRandom().nextFloat() * 0.2f + 0.9f);
				}
			}
		}
		super.onUseTick(world, user, stack, remainingUseTicks);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
		if (user instanceof Player doll) {
			performCare(doll, doll, stack, user.getUsedItemHand(), false);
		}

		return super.finishUsingItem(stack, world, user);
	}

	// care for other
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
		if (entity instanceof Player doll && !user.getCooldowns().isOnCooldown(stack)) {
			InteractionResult careResult = performCare(user, doll, stack, hand, true);
			if (careResult.consumesAction()) {
				UseCooldown cooldownComponent = stack.get(DataComponents.USE_COOLDOWN);
				if (cooldownComponent != null) {
					cooldownComponent.apply(stack, user);
				}

				return careResult;
			}
		}

		return super.interactLivingEntity(stack, user, entity, hand);
	}

	public InteractionResult performCare(Player user, Player doll, ItemStack dollcraftStack, InteractionHand hand, boolean doExtraEffects) {
		if (BeAMaid.isDoll(doll) && BeALibrarian.inspectDollMaterial(doll) == this.getVariant()) {
			ItemStack material = findCareMaterial(user, doll);
			if (!material.isEmpty()) {
				if (doExtraEffects) {
					if (!user.level().isClientSide()) {
						S2CDollRepairedLetter letter = new S2CDollRepairedLetter(doll.getId(), material.copy());
						PlayerLookup.tracking(doll).forEach(player -> {
							if (player != user) {
								ServerPlayNetworking.send(player, letter);
							}
						});
						ServerPlayNetworking.send((ServerPlayer) doll, letter);
					}
					SoundEvent careSound = BeALibrarian.inspectDollMaterial(doll).getCareSound();
					user.level().playSound(user, doll.getX(), doll.getY(), doll.getZ(), careSound, SoundSource.PLAYERS, 1f, doll.getRandom().nextFloat() * 0.2f + 0.9f);
					spawnRepairParticles(doll, material, 16);
				}

				caringIsCaring(doll);
				material.split(1);
				dollcraftStack.hurtAndBreak(1, user, LivingEntity.getSlotForHand(hand));
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	private void caringIsCaring(Player doll) {
		// dolls get full saturation and some absorption every time because i love them (because they are love)
		doll.playSound(BeABirdwatcher.CARE_COMPLETE, 1f, doll.getRandom().nextFloat() * 0.2f + 0.9f);
		doll.getFoodData().eat(4, 5);
		doll.addEffect(new MobEffectInstance(BeAWitch.CARED_FOR, -1, 2, false, false));
	}

	public ItemStack findCareMaterial(Player user, Player doll) {
		if (this.getVariant() != BeALibrarian.inspectDollMaterial(doll)) {
			return ItemStack.EMPTY;
		}

		if (user.hasInfiniteMaterials() || user.level().isClientSide() && !user.isLocalPlayer()) { // otherclientplayers have no inv, so cheat for particles
			return this.getVariant().getDefaultCareMaterial().getDefaultInstance();
		} else {
			Predicate<ItemStack> predicate = stack -> stack.is(this.getVariant().getCareMaterialTag());

			for (int i = 0; i < user.getInventory().getContainerSize(); i++) {
				ItemStack current = user.getInventory().getItem(i);
				if (predicate.test(current)) {
					return current;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	public static void spawnRepairParticles(Player doll, ItemStack material, int count) {
		if (material == null || material.isEmpty()) {
			return;
		}
		for (int i = 0; i < count; i++) {
			Vec3 vel = new Vec3(
				(doll.getRandom().nextFloat() - 0.5) * 0.1,
				Math.random() * 0.1 + 0.1,
				(doll.getRandom().nextFloat() - 0.5) * 0.1);

			AABB dollHouse = doll.getBoundingBox();
			Vec3 pos = new Vec3(
				doll.getRandom().nextDouble() * dollHouse.getXsize(),
				doll.getRandom().nextDouble() * dollHouse.getYsize(),
				doll.getRandom().nextDouble() * dollHouse.getZsize()
			);
			pos = pos.add(dollHouse.getMinPosition());

			doll.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, material), pos.x, pos.y, pos.z, vel.x, vel.y + 0.05, vel.z);
		}
	}

	public BeADoll.Variant getVariant() {
		return components().getOrDefault(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.DEFAULT);
	}
}
