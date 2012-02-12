package net.apunch.alchemist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
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
        for (ItemStack stack : ingredients) {
            if (stack != null
                    && stack.getTypeId() == ingredient.getTypeId()
                    && stack.getDurability() == ingredient.getDurability()
                    && ((/*
                          * stack.getEnchantments() != null &&
                          * ingredient.getEnchantments() != null &&
                          */stack.getEnchantments().equals(ingredient.getEnchantments())) || (stack.getEnchantments() == null && ingredient
                            .getEnchantments() == null)))
                return true;
        }
        return false;
    }

    // Returns whether the player had enough of the item
    public boolean removeIngredientFromHand(Player player) {
        ItemStack ingredient = player.getItemInHand();
        // Try to remove entire stack
        if (ingredients.contains(ingredient)) {
            System.out.println("full stack had");
            ingredients.remove(ingredient);
            player.setItemInHand(null);
            return true;
        }
        for (ItemStack stack : ingredients) {
            if (stack != null && stack.getTypeId() == ingredient.getTypeId()
                    && stack.getDurability() == ingredient.getDurability()
                    && stack.getEnchantments().equals(ingredient.getEnchantments())) {
                System.out.println("stack data == ingredient data: " + stack);
                if (stack.getAmount() - ingredient.getAmount() > 0) {
                    stack.setAmount(stack.getAmount() - ingredient.getAmount());
                    return true;
                }
                System.out.println("had enough and more!");
                ingredients.remove(stack);
                ingredient.setAmount(ingredient.getAmount() - stack.getAmount());
                player.setItemInHand(ingredient);
                return true;
            }
        }
        return false;
    }

    public boolean isComplete() {
        return ingredients.size() == 1;
    }
}