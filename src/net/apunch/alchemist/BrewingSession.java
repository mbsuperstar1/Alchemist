package net.apunch.alchemist;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class BrewingSession {
    private final Player player;
    private final NPC npc;
    private final PotionRecipe recipe;

    public BrewingSession(Player player, NPC npc, PotionRecipe recipe) {
        this.player = player;
        this.npc = npc;
        this.recipe = recipe;
    }

    public void initialize() {
        npc.chat(ChatColor.YELLOW + "Hello there, " + player.getName()
                + ". Give me what I need and I will brew you a potion!");
    }

    // Return whether the session should end
    public boolean handleClick() {
        if (recipe.hasIngredient(player.getItemInHand())) {
            // TODO add fake item entities around NPC for each ingredient added?
            recipe.removeIngredient(player.getItemInHand());
            player.setItemInHand(null);
            if (recipe.isComplete()) {
                applyResult();
                npc.chat(ChatColor.GREEN + "I have combined the ingredients to concoct a potion of " + ChatColor.GOLD
                        + getEffectName().replace('-', ' ') + ChatColor.GREEN + ".");
                return true;
            }
            npc.chat(ChatColor.YELLOW + "The recipe is not complete yet! Give me more!");
        } else
            npc.chat(ChatColor.RED + "Why would I want that? Give me something else!");
        return false;
    }

    public Player getPlayer() {
        return player;
    }

    private String getEffectName() {
        return recipe.getResult().getType().getName().toLowerCase().replace('_', '-');
    }

    private void applyResult() {
        player.playEffect(player.getLocation(), Effect.POTION_BREAK, 0);
        player.getWorld().dropItemNaturally(npc.getBukkitEntity().getLocation(),
                new Potion(PotionType.getByEffect(recipe.getResult().getType())).toItemStack(1));
    }
}