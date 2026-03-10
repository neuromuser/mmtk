package com.neuromuser.mmtk.mixin;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n; // Ensure this import is here
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class, remap = false)
public class ItemTooltipMixin {

	@Inject(method = "getTranslatedDescription", at = @At("RETURN"), cancellable = true)
	private void mmtk$appendCanBreakToDescription(ItemStack itemstack, CallbackInfoReturnable<String> cir) {
		if (!itemstack.getData().containsKey("CanBreak")) return;

		ListTag list = itemstack.getData().getList("CanBreak");
		if (list.tagCount() > 0) {
			I18n i18n = I18n.getInstance();
			StringBuilder sb = new StringBuilder(cir.getReturnValue());

			String header = i18n.translateKey("mmtk.tooltip.can_break");
			sb.append("\n §a").append(header);

			for (int i = 0; i < list.tagCount(); i++) {
				Tag<?> tag = list.tagAt(i);
				if (tag instanceof CompoundTag) {
					String blockKey = ((CompoundTag) tag).getString("blockKey");

					String translatedName = i18n.translateNameKey(blockKey);

					String spacelessName = translatedName.replace(" ", "_");

					sb.append("\n§a").append(spacelessName);
				}
			}
			cir.setReturnValue(sb.toString());
		}
	}
}
