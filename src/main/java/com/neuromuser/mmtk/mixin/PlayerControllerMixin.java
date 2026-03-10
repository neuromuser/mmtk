package com.neuromuser.mmtk.mixin;

import com.mojang.nbt.tags.Tag;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import com.mojang.nbt.tags.ListTag;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerController.class, remap = false)
public class PlayerControllerMixin {

	@Mutable
	@Final
	@Shadow
	protected final Minecraft mc;

	public PlayerControllerMixin(Minecraft mc) {
		this.mc = mc;
	}

	@Unique
	private boolean canPlayerBreakBlock(int x, int y, int z) {
		if (mc.thePlayer == null) return false;
		if (mc.thePlayer.gamemode != Gamemode.adventure) return true;

		ItemStack heldItem = mc.thePlayer.inventory.getCurrentItem();
		// Safety check for null data to avoid crashes
		if (heldItem != null && heldItem.getData().containsKey("CanBreak")) {

			int blockId = mc.currentWorld.getBlockId(x, y, z);
			Block<?> targetBlock = Blocks.getBlock(blockId);

			if (targetBlock != null) {
				// Get the translation key (e.g., "tile.dirt") to match what CMDCanBreak saves
				String targetKey = targetBlock.getKey();
				ListTag list = heldItem.getData().getList("CanBreak");

				for (int i = 0; i < list.tagCount(); i++) {
					Tag<?> tag = list.tagAt(i);

					if (tag instanceof com.mojang.nbt.tags.CompoundTag) {
						com.mojang.nbt.tags.CompoundTag entry = (com.mojang.nbt.tags.CompoundTag) tag;
						// Now comparing "tile.dirt" to "tile.dirt"
						if (entry.getString("blockKey").equals(targetKey)) {
							return true;
						}
					}
					else if (tag instanceof com.mojang.nbt.tags.StringTag) {
						String legacyKey = ((com.mojang.nbt.tags.StringTag) tag).getValue();
						if (legacyKey.equals(targetKey)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}	@Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopStartDestroy(int x, int y, int z, Side side, double xHit, double yHit, boolean repeat, CallbackInfo ci) {
		if (!canPlayerBreakBlock(x, y, z)) {
			ci.cancel();
		}
	}

	@Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
	private void mmtk_stopContinueDestroy(int x, int y, int z, Side side, double xHit, double yHit, CallbackInfo ci) {
		if (!canPlayerBreakBlock(x, y, z)) {
			ci.cancel();
		}
	}
}
