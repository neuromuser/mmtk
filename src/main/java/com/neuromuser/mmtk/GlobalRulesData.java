package com.neuromuser.mmtk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalRulesData {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DATA_PATH = FabricLoader.getInstance().getConfigDir().resolve("mmtk_rules.json");

    private Map<String, Set<String>> breakRules = new HashMap<>();
    private Map<String, Set<String>> placeRules = new HashMap<>();
    private boolean allCanBreak = false;
    private boolean allCanPlace = false;

    public Map<String, Set<String>> getBreakRules() {
        return breakRules;
    }

    public Map<String, Set<String>> getPlaceRules() {
        return placeRules;
    }

    public boolean isAllCanBreak() {
        return allCanBreak;
    }

    public boolean isAllCanPlace() {
        return allCanPlace;
    }

    public void setAllCanBreak(boolean allCanBreak) {
        this.allCanBreak = allCanBreak;
    }

    public void setAllCanPlace(boolean allCanPlace) {
        this.allCanPlace = allCanPlace;
    }

    public static GlobalRulesData load() {
        if (DATA_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(DATA_PATH.toFile())) {
                Type type = new TypeToken<GlobalRulesData>(){}.getType();
                return GSON.fromJson(reader, type);
            } catch (IOException e) {
                MMTK.LOGGER.error("Failed to load global rules", e);
            }
        }
        return new GlobalRulesData();
    }

    public void save() {
        try (Writer writer = new FileWriter(DATA_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            MMTK.LOGGER.error("Failed to save global rules", e);
        }
    }
}
