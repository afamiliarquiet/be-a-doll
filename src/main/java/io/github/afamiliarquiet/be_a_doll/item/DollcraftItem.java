package io.github.afamiliarquiet.be_a_doll.item;

import com.google.common.collect.Multimap;
import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeABirdwatcher;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollRepairedLetter;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class DollcraftItem extends Item {
	public static final int USE_COOLDOWN = 26;

	private final Multimap<EntityAttribute, EntityAttributeModifier> dollcraftAttributes;

	private final BeADoll.Variant variant;

	public DollcraftItem(Settings settings, Multimap<EntityAttribute, EntityAttributeModifier> dollcraftAttributes, BeADoll.Variant variant) {
		super(settings);
		this.dollcraftAttributes = dollcraftAttributes;
		this.variant = variant;
	}

	// care for self
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (BeAMaid.isDoll(user) && !findCareMaterial(user, user).isEmpty()) {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(user.getActiveItem());
		}

		return super.use(world, user, hand);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BRUSH;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 62;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (user instanceof PlayerEntity praiseTheDoll) {
			ItemStack material = findCareMaterial(praiseTheDoll, praiseTheDoll);
			if (material.isEmpty()) {
				user.stopUsingItem();
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
		super.usageTick(world, user, stack, remainingUseTicks);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof PlayerEntity doll) {
			performCare(doll, doll, stack, user.getActiveHand(), false);
		}

		return super.finishUsing(stack, world, user);
	}

	// care for other
	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (entity instanceof PlayerEntity doll && !user.getItemCooldownManager().isCoolingDown(stack.getItem())) {
			ActionResult careResult = performCare(user, doll, stack, hand, true);
			if (careResult.isAccepted()) {
//				UseCooldownComponent cooldownComponent = stack.get(DataComponentTypes.USE_COOLDOWN);
//				if (cooldownComponent != null) {
//					cooldownComponent.set(stack, user);
//				}

				return careResult;
			}
		}

		return super.useOnEntity(stack, user, entity, hand);
	}

	public ActionResult performCare(PlayerEntity user, PlayerEntity doll, ItemStack dollcraftStack, Hand hand, boolean doExtraEffects) {
		if (BeAMaid.isDoll(doll) && BeALibrarian.inspectDollMaterial(doll) == this.getVariant()) {
			ItemStack material = findCareMaterial(user, doll);
			if (!material.isEmpty()) {
				if (doExtraEffects) {
					if (!user.getWorld().isClient()) {
						S2CDollRepairedLetter letter = new S2CDollRepairedLetter(doll.getId(), material.copy());
						PlayerLookup.tracking(doll).forEach(player -> {
							if (player != user) {
								ServerPlayNetworking.send(player, letter);
							}
						});
						ServerPlayNetworking.send((ServerPlayerEntity) doll, letter);
					}
					SoundEvent careSound = BeALibrarian.inspectDollMaterial(doll).getCareSound();
					user.getWorld().playSound(user, doll.getX(), doll.getY(), doll.getZ(), careSound, SoundCategory.PLAYERS, 1f, doll.getRandom().nextFloat() * 0.2f + 0.9f);
					spawnRepairParticles(doll, material, 16);
				}

				caringIsCaring(doll);
				material.split(1);
				dollcraftStack.damage(1, user, stack -> user.sendToolBreakStatus(hand));
				user.getItemCooldownManager().set(dollcraftStack.getItem(), USE_COOLDOWN);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}

	private void caringIsCaring(PlayerEntity doll) {
		// dolls get full saturation and some absorption every time because i love them (because they are love)
		doll.playSound(BeABirdwatcher.CARE_COMPLETE, 1f, doll.getRandom().nextFloat() * 0.2f + 0.9f);
		doll.getHungerManager().add(4, 5);
		doll.addStatusEffect(new StatusEffectInstance(BeAWitch.CARED_FOR.value(), -1, 2, false, false));
	}

	public ItemStack findCareMaterial(PlayerEntity user, PlayerEntity doll) {
		if (this.getVariant() != BeALibrarian.inspectDollMaterial(doll)) {
			return ItemStack.EMPTY;
		}

		if (user.getAbilities().creativeMode || user.getWorld().isClient() && !user.isMainPlayer()) { // otherclientplayers have no inv, so cheat for particles
			return this.getVariant().getDefaultCareMaterial().getDefaultStack();
		} else {
			Predicate<ItemStack> predicate = stack -> stack.isIn(this.getVariant().getCareMaterialTag());

			for (int i = 0; i < user.getInventory().size(); i++) {
				ItemStack current = user.getInventory().getStack(i);
				if (predicate.test(current)) {
					return current;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	public static void spawnRepairParticles(PlayerEntity doll, ItemStack material, int count) {
		if (material == null || material.isEmpty()) {
			return;
		}
		for (int i = 0; i < count; i++) {
			Vec3d vel = new Vec3d(
				(doll.getRandom().nextFloat() - 0.5) * 0.1,
				Math.random() * 0.1 + 0.1,
				(doll.getRandom().nextFloat() - 0.5) * 0.1);

			Box dollHouse = doll.getBoundingBox();
			Vec3d pos = new Vec3d(
				doll.getRandom().nextDouble() * dollHouse.getXLength(),
				doll.getRandom().nextDouble() * dollHouse.getYLength(),
				doll.getRandom().nextDouble() * dollHouse.getZLength()
			);
			pos = pos.add(dollHouse.minX, dollHouse.minY, dollHouse.minZ);

			doll.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, material), pos.x, pos.y, pos.z, vel.x, vel.y + 0.05, vel.z);
		}
	}

	public BeADoll.Variant getVariant() {
		return this.variant;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.isOf(Items.IRON_NUGGET); // hardcoded because backporting laziness
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return this.dollcraftAttributes != null && slot == EquipmentSlot.MAINHAND ? this.dollcraftAttributes : super.getAttributeModifiers(slot);
	}
}
