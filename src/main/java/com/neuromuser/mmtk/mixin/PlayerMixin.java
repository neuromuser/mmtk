package com.neuromuser.mmtk.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

	@Inject(method = "getCurrentPlayerStrVsBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk$allowAdventureBlockBreaking(Block<?> block, CallbackInfoReturnable<Float> cir) {
		Player self = (Player) (Object) this;

		if (self.getGamemode() == Gamemode.adventure) {
			cir.setReturnValue(1.0f);
		}
	}
}
