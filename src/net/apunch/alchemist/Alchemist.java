package net.apunch.alchemist;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.YamlStorage;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class Alchemist extends JavaPlugin {
    private YamlStorage config;
    private static final Set<PotionRecipe> recipes = new HashSet<PotionRecipe>();

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, " v" + getDescription().getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        config = new YamlStorage(getDataFolder() + File.separator + "config.yml", "Alchemist NPCs Configuration");
        CitizensAPI.getCharacterManager().register(AlchemistCharacter.class);

        try {
            populateRecipes();
        } catch (NPCLoadException ex) {
            getLogger().log(Level.SEVERE, "Issue enabling plugin: " + ex.getMessage());
        }

        getLogger().log(Level.INFO, " v" + getDescription().getVersion() + " enabled.");
    }

    public static PotionRecipe getRecipe(String name) {
        for (PotionRecipe recipe : recipes) {
            if (recipe.getName().equals(name))
                return recipe;
        }
        return null;
    }

    private void populateRecipes() throws NPCLoadException {
        for (DataKey recipeKey : config.getKey("recipes").getSubKeys()) {
            try {
                ItemStack[] ingredients = new ItemStack[36];
                for (DataKey id : recipeKey.getRelative("ingredients").getIntegerSubKeys())
                    ingredients[Integer.parseInt(id.name())] = getIngredient(id);

                recipes.add(new PotionRecipe(recipeKey.name(), PotionEffectType.getByName(recipeKey
                        .getString("result.effect").toUpperCase().replace('-', '_')), recipeKey
                        .getInt("result.duration") * 20, recipeKey.getInt("result.amplifier"), ingredients));
            } catch (Exception ex) {
                throw new NPCLoadException("Invalid configuration for the recipe '" + recipeKey.name() + "'."
                        + ex.getMessage());
            }
        }
    }

    private ItemStack getIngredient(DataKey key) throws NPCLoadException {
        try {
            ItemStack item = new ItemStack(Material.getMaterial(key.getString("name").toUpperCase().replace('-', '_')),
                    key.getInt("amount"), (short) key.getLong("data"));
            if (key.keyExists("enchantments")) {
                Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
                for (DataKey subKey : key.getRelative("enchantments").getSubKeys()) {
                    Enchantment enchantment = Enchantment.getByName(subKey.name().toUpperCase().replace('-', '_'));
                    if (enchantment != null && enchantment.canEnchantItem(item))
                        enchantments.put(
                                enchantment,
                                subKey.getInt("") <= enchantment.getMaxLevel() ? subKey.getInt("") : enchantment
                                        .getMaxLevel());
                }
                item.addEnchantments(enchantments);
            }
            return item;
        } catch (Exception ex) {
            throw new NPCLoadException("Invalid item. " + ex.getMessage());
        }
    }
}