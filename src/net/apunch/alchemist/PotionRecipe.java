package net.apunch.alchemist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionRecipe {
    private final String name;
    private PotionEffect result;
    private Set<ItemStack> ingredients;

    public PotionRecipe(String name, PotionEffectType type, int duration, int amplifier, ItemStack... required) {
        this.name = name;
        result = new PotionEffect(type, duration, amplifier);
        ingredients = new HashSet<ItemStack>(Arrays.asList(required));
    }

    public String getName() {
        return name;
    }

    public PotionEffect getResult() {
        return result;
    }

    public boolean hasIngredient(ItemStack ingredient) {
        return ingredients.contains(ingredient);
    }

    public void removeIngredient(ItemStack ingredient) {
        ingredients.remove(ingredient);
        System.out.println("ingredients: " + ingredients.size());
    }

    public boolean isComplete() {
        return ingredients.size() == 1;
    }
}