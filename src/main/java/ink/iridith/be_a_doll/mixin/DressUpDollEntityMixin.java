package ink.iridith.be_a_doll.mixin;

import ink.iridith.be_a_doll.BeAMaid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// be informed: this mixin to add armor stand functionality to players is, unsurprisingly,
// pretty much entirely a copy of ArmorStandEntity's interactAt code. that's it.
// (with irrelevant bits trimmed like disabled slots, and variable names made more readable for my sake)
@Mixin(Entity.class)
public abstract class DressUpDollEntityMixin {
	@Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
	public void interactAt(Player thatGrabbyPlayer, Vec3 hitPos, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		// why do you think this, intellij? you don't complain if they're separate
		// ohhh you're a hater of the instanceof x isDoll ship. i get it it's not the best pairing but it is valid ok
		//noinspection ConstantValue
		if ((Object)this instanceof Player thisDoll && BeAMaid.isDoll(thisDoll) && thatGrabbyPlayer.isSecondaryUseActive()) {
			ItemStack itemStack = thatGrabbyPlayer.getItemInHand(hand);
			if (thatGrabbyPlayer.isSpectator()) {
				cir.setReturnValue(InteractionResult.SUCCESS);
			} else if (thatGrabbyPlayer.level().isClientSide) {
				cir.setReturnValue(InteractionResult.SUCCESS);
			} else {
				EquipmentSlot preferredSlot = thisDoll.getEquipmentSlotForItem(itemStack);
				// because players don't normally get equipped i need to add this next line (player is missing some filters)
				preferredSlot = preferredSlot != EquipmentSlot.BODY ? preferredSlot : EquipmentSlot.MAINHAND;
				if (itemStack.isEmpty()) {
					EquipmentSlot aimedSlot = this.be_a_doll$getSlotFromPosition(thisDoll, hitPos);
					if (thisDoll.hasItemInSlot(aimedSlot) && this.be_a_doll$equip(thisDoll, thatGrabbyPlayer, aimedSlot, itemStack, hand)) {
						cir.setReturnValue(InteractionResult.SUCCESS);
					}
				} else {
					if (this.be_a_doll$equip(thisDoll, thatGrabbyPlayer, preferredSlot, itemStack, hand)) {
						cir.setReturnValue(InteractionResult.SUCCESS);
					}
				}
			}
		}
		// else.. not my problem, which is probably a pass if there's no other mixins
		// feels a lil sad to mixin into Entity instead of PlayerEntity but this should be much safer?
		// and interact(At) calls aren't a super common thing anyway so adding a single instanceof to it is not a big deal
		// the taste of antimatter has certainly faded
	}

	@Unique
	private EquipmentSlot be_a_doll$getSlotFromPosition(Player thisDoll, Vec3 hitPos) {
		EquipmentSlot chosenSlot = EquipmentSlot.MAINHAND;
		double relativeAimedHeight = hitPos.y / (thisDoll.getScale() * thisDoll.getAgeScale());

		if (relativeAimedHeight >= 1.6 && thisDoll.hasItemInSlot(EquipmentSlot.HEAD)) {
			chosenSlot = EquipmentSlot.HEAD;
		} else if (relativeAimedHeight >= 0.9 && relativeAimedHeight < 1.6 && thisDoll.hasItemInSlot(EquipmentSlot.CHEST)) {
			chosenSlot = EquipmentSlot.CHEST;
		} else if (relativeAimedHeight >= 0.4 && relativeAimedHeight < 1.2 && thisDoll.hasItemInSlot(EquipmentSlot.LEGS)) {
			chosenSlot = EquipmentSlot.LEGS;
		} else if (relativeAimedHeight < 0.55 && thisDoll.hasItemInSlot(EquipmentSlot.FEET)) {
			chosenSlot = EquipmentSlot.FEET;
		} else if (!thisDoll.hasItemInSlot(EquipmentSlot.MAINHAND) && thisDoll.hasItemInSlot(EquipmentSlot.OFFHAND)) {
			chosenSlot = EquipmentSlot.OFFHAND;
		}

		return chosenSlot;
	}

	@Unique
	private boolean be_a_doll$equip(Player thisDoll, Player thatGrabbyPlayer, EquipmentSlot slot, ItemStack playerStack, InteractionHand hand) {
		ItemStack dollStack = thisDoll.getItemBySlot(slot);

		if (thatGrabbyPlayer.hasInfiniteMaterials() && dollStack.isEmpty() && !playerStack.isEmpty()) {
			thisDoll.setItemSlot(slot, playerStack.copyWithCount(1));
			return true;
		} else if (playerStack.isEmpty() || playerStack.getCount() <= 1) {
			thisDoll.setItemSlot(slot, playerStack);
			thatGrabbyPlayer.setItemInHand(hand, dollStack);
			return true;
		} else if (!dollStack.isEmpty()) {
			return false;
		} else {
			thisDoll.setItemSlot(slot, playerStack.split(1));
			return true;
		}
	}
}
