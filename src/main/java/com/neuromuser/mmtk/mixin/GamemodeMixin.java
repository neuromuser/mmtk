package com.neuromuser.mmtk.mixin;

import net.minecraft.core.player.gamemode.Gamemode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gamemode.class)
public class GamemodeMixin {

	@Inject(method = "canInteract", at = @At("HEAD"), cancellable = true)
	private void mmtk$allowAdventureInteract(CallbackInfoReturnable<Boolean> cir) {
		if (((Object)this) == Gamemode.adventure) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "doBlockBreakingAnim", at = @At("HEAD"), cancellable = true)
	private void mmtk$allowAdventureBlockBreaking(CallbackInfoReturnable<Boolean> cir) {
		if (((Object)this) == Gamemode.adventure) cir.setReturnValue(true);
	}
}
