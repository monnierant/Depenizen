package com.denizenscript.depenizen.bukkit.events.essentials;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import net.ess3.api.events.GodStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// <--[event]
// @Events
// player god mode enabled
// player god mode disabled
// player god mod status changes
//
// @Regex ^on player god mode (enabled|disabled|status changes)$
//
// @Cancellable true
//
// @Triggers when a player's god mode status changes.
//
// @Context
// <context.status> Returns the player's god mode status.
//
// @Plugin DepenizenBukkit, Essentials
//
// -->

public class PlayerGodModeStatusScriptEvent extends BukkitScriptEvent implements Listener {

    public static PlayerGodModeStatusScriptEvent instance;
    public GodStatusChangeEvent event;
    public Element god;

    public PlayerGodModeStatusScriptEvent() {
        instance = this;
    }

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player god mode");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String status = CoreUtilities.getXthArg(3, lower);
        if (status.equals("enabled") && god.asBoolean()) {
            return true;
        }
        else if (status.equals("disabled") && !god.asBoolean()) {
            return true;
        }
        else {
            return lower.startsWith("player god mod status changes");
        }
    }

    @Override
    public String getName() {
        return "PlayerGodModeStatus";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        GodStatusChangeEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(dPlayer.mirrorBukkitPlayer(event.getAffected().getBase()), null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("status")) {
            return god;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerAFKStatus(GodStatusChangeEvent event) {
        god = new Element(event.getValue());
        cancelled = event.isCancelled();
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
