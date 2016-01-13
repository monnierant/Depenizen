package net.gnomeffinway.depenizen.events.Towny;

import com.palmergames.bukkit.towny.ChunkNotification;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import net.aufdemrand.denizencore.utilities.debugging.dB;
import net.gnomeffinway.depenizen.objects.dTown;
import net.gnomeffinway.depenizen.support.plugins.TownySupport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// <--[event]
// @Events
// towny player exits town
// towny player exits <town>
//
// @Regex ^on towny player exit [^\s]+$
//
// @Cancellable false
//
// @Triggers when a player exits a Towny Town.
//
// @Context
// <context.town> Returns the town the player exited.
//
// @Plugin Depenizen, Towny
//
// -->

public class PlayerExitsTownScriptEvent extends BukkitScriptEvent implements Listener {

    public PlayerExitsTownScriptEvent() {
        instance = this;
    }

    public PlayerExitsTownScriptEvent instance;
    public PlayerChangePlotEvent event;
    public dTown town;


    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("towny player exits");
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String name = CoreUtilities.getXthArg(3, CoreUtilities.toLowerCase(s));
        if (name.equals("town")) {
            return true;
        }
        try {
            Town given = TownyUniverse.getDataSource().getTown(name);
            Town eventTown = TownySupport.fromWorldCoord(event.getFrom());
            if (given == eventTown) {
                return true;
            }
        }
        catch (NotRegisteredException e) {
            dB.echoError("No town found by the name: " + name);
        }
        return false;
    }

    @Override
    public String getName() {
        return "TownyPlayerExitsTown";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        PlayerChangePlotEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new dPlayer(event.getPlayer()), null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("town")) {
            return town;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onTownyPlayerExitsTown(PlayerChangePlotEvent event) {
        try {
            if (!event.getFrom().getTownyWorld().isUsingTowny() || new ChunkNotification(event.getFrom(), event.getTo()).getNotificationString() == null) {
                return;
            }
        }
        catch (NotRegisteredException e) {
            return;
        }
        town = new dTown(TownySupport.fromWorldCoord(event.getFrom()));
        this.event = event;
        fire();
    }
}
