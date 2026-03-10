package com.neuromuser.mmtk;

import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.util.*;

public class GlobalRules {
	private static final GlobalRulesData data = GlobalRulesData.load();

	private static String getItemKey(Item item) {
		return item.getKey();
	}

	private static String getBlockKey(Block<?> block) {
		return block.getKey();
	}

	public static void setGlobalRule(Item item, String blockKey, boolean isBreak, boolean state) {
		String itemKey = getItemKey(item);
		Map<String, Set<String>> targetMap = isBreak ? data.getBreakRules() : data.getPlaceRules();

		if (state) {
			targetMap.computeIfAbsent(itemKey, k -> new HashSet<>()).add(blockKey);
		} else if (targetMap.containsKey(itemKey)) {
			targetMap.get(itemKey).remove(blockKey);
			if (targetMap.get(itemKey).isEmpty()) {
				targetMap.remove(itemKey);
			}
		}

		data.save();
	}

	public static void setAllGlobal(boolean isBreak, boolean state) {
		if (isBreak) {
			data.setAllCanBreak(state);
		} else {
			data.setAllCanPlace(state);
		}
		data.save();
	}

	public static boolean canDoGlobal(Item item, String blockKey, boolean isBreak) {
		if (isBreak && data.isAllCanBreak()) return true;
		if (!isBreak && data.isAllCanPlace()) return true;

		String itemKey = getItemKey(item);
		Map<String, Set<String>> targetMap = isBreak ? data.getBreakRules() : data.getPlaceRules();

		return targetMap.containsKey(itemKey) && targetMap.get(itemKey).contains(blockKey);
	}

	public static Set<String> getGlobalBlocksForItem(Item item, boolean isBreak) {
		String itemKey = getItemKey(item);
		Map<String, Set<String>> targetMap = isBreak ? data.getBreakRules() : data.getPlaceRules();
		return targetMap.getOrDefault(itemKey, new HashSet<>());
	}

	public static void saveData() {
		data.save();
	}
}
