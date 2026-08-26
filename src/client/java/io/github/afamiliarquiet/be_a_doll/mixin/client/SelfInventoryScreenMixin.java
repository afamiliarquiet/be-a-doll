package io.github.afamiliarquiet.be_a_doll.mixin.client;

import io.github.afamiliarquiet.be_a_doll.BeASelf;
import io.github.afamiliarquiet.be_a_doll.letters.C2SEssenceAlterationLetter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class SelfInventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {
	public SelfInventoryScreenMixin(InventoryMenu handler, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
		super(handler, recipeBook, inventory, title);
	}

	// Hey! You, the one reading this code!
	// Are you annoyed/displeased/disgusted/revolted/terrified?
	// Tell me how I can do better! Save me! Please! Please, anyone, help me! Is anyone there?!
	// i don't like screens
	@Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;mouseReleased(Lnet/minecraft/client/input/MouseButtonEvent;)Z"), cancellable = true)
	private void clicky(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
		// todone i think - if im injecting head i may want to help out with the mouseReleased thing but.. mess
		//  i don't want to let super get called because that does other slot stuff
		if (BeASelf.isMouseInSurvivalSelf(event.x(), event.y(), this.leftPos, this.topPos) && this.minecraft.player != null) {
			ItemStack cursorStack = this.menu.getCarried();
			ItemStack clickProcessedStack = null;

			if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				ClientPlayNetworking.send(new C2SEssenceAlterationLetter(true));
				clickProcessedStack = BeASelf.clickSelf(cursorStack, this.minecraft.player, true);
			} else if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				ClientPlayNetworking.send(new C2SEssenceAlterationLetter(false));
				clickProcessedStack = BeASelf.clickSelf(cursorStack, this.minecraft.player, false);
			}

			if (clickProcessedStack != null) {
				this.menu.setCarried(clickProcessedStack);
				cir.setReturnValue(true);
			}
		}
	}
}
