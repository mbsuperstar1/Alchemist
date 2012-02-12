package net.apunch.alchemist;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.trait.Character;
import net.citizensnpcs.api.npc.trait.SaveId;
import net.citizensnpcs.api.util.DataKey;

@SaveId("alchemist")
public class AlchemistCharacter extends Character {
    private String recipeName;
    private BrewingSession session;

    @Override
    public void load(DataKey key) throws NPCLoadException {
        recipeName = key.getString("recipe");
        if (Alchemist.getRecipe(recipeName) == null)
            throw new NPCLoadException("No recipe with the name '" + recipeName + "' exists.");
    }

    @Override
    public void onRightClick(NPC npc, Player player) {
        if (session != null) {
            if (!session.getPlayer().getName().equals(player.getName())) {
                player.sendMessage(ChatColor.RED + npc.getName() + " is busy with another player. Come back later!");
                return;
            }
            if (session.handleClick())
                session = null;
        } else {
            // TODO cooldowns
            session = new BrewingSession(player, npc, Alchemist.getRecipe(recipeName));
            session.initialize();
        }
    }

    @Override
    public void save(DataKey key) {
        key.setString("recipe", recipeName);
    }
}