package com.neuromuser.mmtk.mixin;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemStack.class, remap = false)
public class ItemStackMixin {
    @Inject(method = "damageItem", at = @At("HEAD"), cancellable = true)
    private void mmtk_makeUnbreakable(int amount, Entity entity, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;

        if (self.getData() != null && self.getData().getBoolean("Unbreakable")) {
            ci.cancel();
        }
    }
}
