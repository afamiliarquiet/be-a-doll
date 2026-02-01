package io.github.afamiliarquiet.be_a_doll.mixin;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.api.ScaleData;

// be informed: this mixin to add armor stand functionality to players is, unsurprisingly,
// pretty much entirely a copy of ArmorStandEntity's interactAt code. that's it.
// (with irrelevant bits trimmed like disabled slots, and variable names made more readable for my sake)
@Mixin(Entity.class)
public abstract class DressUpDollEntityMixin {
	@Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
	public void interactAt(PlayerEntity thatGrabbyPlayer, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		// why do you think this, intellij? you don't complain if they're separate
		// ohhh you're a hater of the instanceof x isDoll ship. i get it it's not the best pairing but it is valid ok
		//noinspection ConstantValue
		if ((Object)this instanceof PlayerEntity thisDoll && BeAMaid.isDoll(thisDoll) && thatGrabbyPlayer.shouldCancelInteraction()) {
			ItemStack itemStack = thatGrabbyPlayer.getStackInHand(hand);
			if (thatGrabbyPlayer.isSpectator()) {
				cir.setReturnValue(ActionResult.SUCCESS);
			} else if (thatGrabbyPlayer.getWorld().isClient) {
				cir.setReturnValue(ActionResult.SUCCESS);
			} else {
				EquipmentSlot preferredSlot = thisDoll.getPreferredEquipmentSlot(itemStack);
				// because players don't normally get equipped i need to add this next line (player is missing some filters)
				preferredSlot = preferredSlot != EquipmentSlot.CHEST ? preferredSlot : EquipmentSlot.MAINHAND;
				if (itemStack.isEmpty()) {
					EquipmentSlot aimedSlot = this.be_a_doll$getSlotFromPosition(thisDoll, hitPos);
					if (thisDoll.hasStackEquipped(aimedSlot) && this.be_a_doll$equip(thisDoll, thatGrabbyPlayer, aimedSlot, itemStack, hand)) {
						cir.setReturnValue(ActionResult.SUCCESS);
					}
				} else {
					if (this.be_a_doll$equip(thisDoll, thatGrabbyPlayer, preferredSlot, itemStack, hand)) {
						cir.setReturnValue(ActionResult.SUCCESS);
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
	private EquipmentSlot be_a_doll$getSlotFromPosition(PlayerEntity thisDoll, Vec3d hitPos) {
		EquipmentSlot chosenSlot = EquipmentSlot.MAINHAND;
		ScaleData scaleData = BeALibrarian.DOLL_SCALE_TYPE.getScaleData(thisDoll);
		double relativeAimedHeight = hitPos.y / (scaleData.getScale() * thisDoll.getScaleFactor());

		if (relativeAimedHeight >= 1.6 && thisDoll.hasStackEquipped(EquipmentSlot.HEAD)) {
			chosenSlot = EquipmentSlot.HEAD;
		} else if (relativeAimedHeight >= 0.9 && relativeAimedHeight < 1.6 && thisDoll.hasStackEquipped(EquipmentSlot.CHEST)) {
			chosenSlot = EquipmentSlot.CHEST;
		} else if (relativeAimedHeight >= 0.4 && relativeAimedHeight < 1.2 && thisDoll.hasStackEquipped(EquipmentSlot.LEGS)) {
			chosenSlot = EquipmentSlot.LEGS;
		} else if (relativeAimedHeight < 0.55 && thisDoll.hasStackEquipped(EquipmentSlot.FEET)) {
			chosenSlot = EquipmentSlot.FEET;
		} else if (!thisDoll.hasStackEquipped(EquipmentSlot.MAINHAND) && thisDoll.hasStackEquipped(EquipmentSlot.OFFHAND)) {
			chosenSlot = EquipmentSlot.OFFHAND;
		}

		return chosenSlot;
	}

	@Unique
	private boolean be_a_doll$equip(PlayerEntity thisDoll, PlayerEntity thatGrabbyPlayer, EquipmentSlot slot, ItemStack playerStack, Hand hand) {
		ItemStack dollStack = thisDoll.getEquippedStack(slot);

		if (thatGrabbyPlayer.getAbilities().creativeMode && dollStack.isEmpty() && !playerStack.isEmpty()) {
			thisDoll.equipStack(slot, playerStack.copyWithCount(1));
			return true;
		} else if (playerStack.isEmpty() || playerStack.getCount() <= 1) {
			thisDoll.equipStack(slot, playerStack);
			thatGrabbyPlayer.setStackInHand(hand, dollStack);
			return true;
		} else if (!dollStack.isEmpty()) {
			return false;
		} else {
			thisDoll.equipStack(slot, playerStack.split(1));
			return true;
		}
	}
}
