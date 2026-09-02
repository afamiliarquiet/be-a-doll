package ink.iridith.be_a_doll.item;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.BeAMaid;
import ink.iridith.be_a_doll.diary.BeABirdwatcher;
import ink.iridith.be_a_doll.diary.BeACollector;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import ink.iridith.be_a_doll.diary.BeAWitch;
import ink.iridith.be_a_doll.letters.S2CDollRepairedLetter;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DollcraftItem extends Item {
	public static final int USE_COOLDOWN = 26;

	public DollcraftItem(Properties settings) {
		super(settings);
	}

	// care for self
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player user, @NotNull InteractionHand hand) {
		if (BeAMaid.isDoll(user) && !findCareMaterial(user, user).isEmpty()) {
			user.startUsingItem(hand);
			return InteractionResultHolder.consume(user.getUseItem());
		}

		return super.use(world, user, hand);
	}

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
		return UseAnim.BRUSH;
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity user) {
		return 62;
	}

	@Override
	public void onUseTick(@NotNull Level world, @NotNull LivingEntity user, @NotNull ItemStack stack, int remainingUseTicks) {
		if (user instanceof Player praiseTheDoll) {
			ItemStack material = findCareMaterial(praiseTheDoll, praiseTheDoll);
			if (material.isEmpty()) {
				user.releaseUsingItem();
			} else {
				if (remainingUseTicks < 6 && remainingUseTicks > -6) { // trying to account for latency lag with the -6
					if (remainingUseTicks % 4 == 3) {
						praiseTheDoll.playSound(BeABirdwatcher.CARE_COMPLETE.get(), 1f, praiseTheDoll.getRandom().nextFloat() * 0.2f + 0.9f);
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
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity user) {
		if (user instanceof Player doll) {
			performCare(doll, doll, stack, user.getUsedItemHand(), false);
		}

		return super.finishUsingItem(stack, world, user);
	}

	// care for other
	@Override
	public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player user, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
		if (entity instanceof Player doll && !user.getCooldowns().isOnCooldown(stack.getItem())) {
			InteractionResult careResult = performCare(user, doll, stack, hand, true);
			if (careResult.consumesAction()) {
//				UseCooldownComponent cooldownComponent = stack.get(DataComponentTypes.USE_COOLDOWN);
//				if (cooldownComponent != null) {
//					cooldownComponent.set(stack, user);
//				}

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
						// todone - take a look at this and make sure it's working right. things different now.
						//  ideally this would dodge the player who started it. maybe i need to add that info to letter?
						// yea it looks good enough to me. listen neodolls i love you but i'm not gonna sit here forever and make sure every detail is exactly the same as.. any other version
//						PlayerLookup.tracking(doll).forEach(player -> {
//							if (player != user) {
//								PacketDistributor.sendToPlayer(player, letter);
//							}
//						});
//						PacketDistributor.sendToPlayer((ServerPlayer) doll, letter);
						PacketDistributor.sendToPlayersTrackingEntityAndSelf(doll, letter);
					}
					SoundEvent careSound = BeALibrarian.inspectDollMaterial(doll).getCareSound();
					user.level().playSound(user, doll.getX(), doll.getY(), doll.getZ(), careSound, SoundSource.PLAYERS, 1f, doll.getRandom().nextFloat() * 0.2f + 0.9f);
					spawnRepairParticles(doll, material, 16);
				}

				caringIsCaring(doll);
				material.split(1);
				dollcraftStack.hurtAndBreak(1, user, LivingEntity.getSlotForHand(hand));
				user.getCooldowns().addCooldown(dollcraftStack.getItem(), USE_COOLDOWN);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	private void caringIsCaring(Player doll) {
		// dolls get full saturation and some absorption every time because i love them (because they are love)
		doll.playSound(BeABirdwatcher.CARE_COMPLETE.get(), 1f, doll.getRandom().nextFloat() * 0.2f + 0.9f);
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
		return components().getOrDefault(BeACollector.DOLL_VARIANT_COMPONENT.get(), BeADoll.Variant.DEFAULT);
	}

	@Override
	public boolean isValidRepairItem(@NotNull ItemStack stack, ItemStack ingredient) {
		return ingredient.is(Items.IRON_NUGGET); // hardcoded because backporting laziness
	}
}
