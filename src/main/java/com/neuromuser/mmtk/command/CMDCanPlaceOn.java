package com.neuromuser.mmtk.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.helpers.BlockInput;

public class CMDCanPlaceOn implements Command<CommandSource> {

	@Override
	public int run(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();

		if (!(source.getSender() instanceof Player)) {
			source.sendMessage("Only players can hold items.");
			return 0;
		}

		Player player = source.getSender();
		ItemStack heldItem = player.inventory.getCurrentItem();

		if (heldItem == null) {
			source.sendMessage("You must be holding an item!");
			return 0;
		}

		BlockInput blockInput = context.getArgument("block", BlockInput.class);
		Block<?> block = blockInput.getBlock();
		if (block == null) return 0;

		String translationKey = block.getKey();

		CompoundTag tag = heldItem.getData();
		if (!tag.containsKey("CanPlaceOn")) {
			tag.put("CanPlaceOn", new ListTag());
		}

		ListTag canBreakList = tag.getList("CanPlaceOn");

		boolean exists = false;
		for (int i = 0; i < canBreakList.tagCount(); i++) {
			CompoundTag entry = (CompoundTag) canBreakList.tagAt(i);
			if (entry.getString("blockKey").equals(translationKey)) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			CompoundTag blockEntry = new CompoundTag();
			blockEntry.putString("blockKey", translationKey);

			canBreakList.addTag(blockEntry);
			source.sendMessage("Block can now be placed on: " + new ItemStack(block).getDisplayName());
		}

		return 1;
	}
}
