package com.neuromuser.mmtk.mixin;

import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.util.helper.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerController.class, remap = false)
public class PlayerControllerMixin {

	@Shadow
	protected final Minecraft mc;

	public PlayerControllerMixin(Minecraft mc) {
		this.mc = mc;
	}

	// This stops the initial click/punch
	@Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopStartDestroy(int x, int y, int z, Side side, double xHit, double yHit, boolean repeat, CallbackInfo ci) {
		if (mc.thePlayer != null && mc.thePlayer.gamemode == Gamemode.adventure) {
			ci.cancel();
		}
	}

	// This stops the held-down mining progress
	@Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopContinueDestroy(int x, int y, int z, Side side, double xHit, double yHit, CallbackInfo ci) {
		if (mc.thePlayer != null && mc.thePlayer.gamemode == Gamemode.adventure) {
			ci.cancel();
		}
	}
}
