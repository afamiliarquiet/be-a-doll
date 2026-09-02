package ink.iridith.be_a_doll.mixin.client;

import ink.iridith.be_a_doll.BeASelf;
import ink.iridith.be_a_doll.letters.C2SEssenceAlterationLetter;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class SelfInventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

	public SelfInventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
		super(screenHandler, playerInventory, text);
	}

	// Hey! You, the one reading this code!
	// Are you annoyed/displeased/disgusted/revolted/terrified?
	// Tell me how I can do better! Save me! Please! Please, anyone, help me! Is anyone there?!
	// i don't like screens
	@Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/EffectRenderingInventoryScreen;mouseReleased(DDI)Z"), cancellable = true)
	private void clicky(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		// todone i think - if im injecting head i may want to help out with the mouseReleased thing but.. mess
		//  i don't want to let super get called because that does other slot stuff
		if (BeASelf.isMouseInSurvivalSelf(mouseX, mouseY, this.leftPos, this.topPos) && this.minecraft != null && this.minecraft.player != null) {
			ItemStack cursorStack = this.menu.getCarried();
			ItemStack clickProcessedStack = null;

			if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				PacketDistributor.sendToServer(new C2SEssenceAlterationLetter(true));
				clickProcessedStack = BeASelf.clickSelf(cursorStack, this.minecraft.player, true);
			} else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				PacketDistributor.sendToServer(new C2SEssenceAlterationLetter(false));
				clickProcessedStack = BeASelf.clickSelf(cursorStack, this.minecraft.player, false);
			}

			if (clickProcessedStack != null) {
				this.menu.setCarried(clickProcessedStack);
				cir.setReturnValue(true);
			}
		}
	}
}
