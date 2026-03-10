package com.neuromuser.mmtk.mixin;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import com.neuromuser.mmtk.GlobalRules;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = Item.class, remap = false)
public class ItemTooltipMixin {

	@Inject(method = "getTranslatedDescription", at = @At("RETURN"), cancellable = true)
	private void mmtk$appendAdventureTooltips(ItemStack itemstack, CallbackInfoReturnable<String> cir) {
		I18n i18n = I18n.getInstance();
		StringBuilder sb = new StringBuilder(cir.getReturnValue());

		Set<String> breakBlocks = collectBreakBlocks(itemstack);
		Set<String> placeBlocks = collectPlaceBlocks(itemstack);

		appendTooltipSection(sb, breakBlocks, "mmtk.tooltip.can_break", "§a", i18n);
		appendTooltipSection(sb, placeBlocks, "mmtk.tooltip.can_place_on", "§e", i18n);

		cir.setReturnValue(sb.toString());
	}

	@Unique
	private Set<String> collectBreakBlocks(ItemStack stack) {

		Set<String> blocks = new HashSet<>(GlobalRules.getGlobalBlocksForItem(stack.getItem(), true));

		if (stack.getData().containsKey("CanBreak")) {
			ListTag list = stack.getData().getList("CanBreak");
			for (int i = 0; i < list.tagCount(); i++) {
				Tag<?> tag = list.tagAt(i);
				if (tag instanceof CompoundTag) {
					blocks.add(((CompoundTag) tag).getString("blockKey"));
				}
			}
		}

		return blocks;
	}

	@Unique
	private Set<String> collectPlaceBlocks(ItemStack stack) {

		Set<String> blocks = new HashSet<>(GlobalRules.getGlobalBlocksForItem(stack.getItem(), false));

		if (stack.getData().containsKey("CanPlaceOn")) {
			ListTag list = stack.getData().getList("CanPlaceOn");
			for (int i = 0; i < list.tagCount(); i++) {
				Tag<?> tag = list.tagAt(i);
				if (tag instanceof CompoundTag) {
					blocks.add(((CompoundTag) tag).getString("blockKey"));
				}
			}
		}

		return blocks;
	}

	@Unique
	private void appendTooltipSection(StringBuilder sb, Set<String> blocks, String langKey, String color, I18n i18n) {
		if (blocks.isEmpty()) return;

		sb.append("\n ").append(color).append(i18n.translateKey(langKey));
		for (String blockKey : blocks) {
			sb.append("\n").append(color).append(i18n.translateNameKey(blockKey).replace(" ", "_"));
		}
	}
}
