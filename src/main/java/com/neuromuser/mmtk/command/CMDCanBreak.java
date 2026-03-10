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
import net.minecraft.core.util.collection.NamespaceID;

public class CMDCanBreak implements Command<CommandSource> {

	@Override
	public int run(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();

		if (!(source.getSender() instanceof Player)) {
			source.sendMessage("Only players can hold items.");
			return 0;
		}

		Player player = (Player) source.getSender();
		ItemStack heldItem = player.inventory.getCurrentItem();

		if (heldItem == null) {
			source.sendMessage("You must be holding an item!");
			return 0;
		}

		BlockInput blockInput = context.getArgument("block", BlockInput.class);
		// Inside your run method in CMDCanBreak.java
		Block<?> block = blockInput.getBlock();
		if (block == null) return 0;

// Instead of getDisplayName(), get the raw translation key (e.g., "tile.dirt")
		String translationKey = block.getKey();
		NamespaceID nid = block.namespaceId();
		String id = nid.value();

		CompoundTag tag = heldItem.getData();
		if (!tag.containsKey("CanBreak")) {
			tag.put("CanBreak", new ListTag());
		}

		ListTag canBreakList = tag.getList("CanBreak");

// Check for duplicates using the translationKey
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
			source.sendMessage("Item can now break: " + new ItemStack(block).getDisplayName());
		}

		return 1;
	}
}
