package net.gnomeffinway.depenizen.extensions.factions;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.util.IdUtil;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.utilities.debugging.dB;
import net.gnomeffinway.depenizen.extensions.dObjectExtension;
import net.gnomeffinway.depenizen.objects.dFaction;

public class FactionsPlayerNPCExtension extends dObjectExtension {

    public static boolean describes(dObject obj) {
        return obj instanceof dPlayer || obj instanceof dNPC;
    }

    public static FactionsPlayerNPCExtension getFrom(dObject obj) {
        if (!describes(obj)) return null;
        else return new FactionsPlayerNPCExtension(obj);
    }

    private FactionsPlayerNPCExtension(dObject obj) {
        String name = obj instanceof dPlayer ? ((dPlayer) obj).getName()
                : obj instanceof dNPC ? ((dNPC) obj).getName()
                : null;
        if (name == null) {
            dB.echoError("Invalid dObject! Must be a dPlayer or dNPC!");
            return;
        }
        player = MPlayer.get(IdUtil.getId(name));
        if (player == null) {
            // Attempt to force NPCs into Factions
            player = MPlayerColl.get().create(IdUtil.getId(name));
        }
    }

    MPlayer player = null;

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute.startsWith("factions")) {

            attribute = attribute.fulfill(1);

            // <--[tag]
            // @attribute <p@player.factions.power>
            // @returns Element(Double)
            // @description
            // Returns the player's power level.
            // @plugin Depenizen, Factions
            // -->
            // <--[tag]
            // @attribute <n@npc.factions.power>
            // @returns Element(Double)
            // @description
            // Returns the NPC's power level.
            // @plugin Depenizen, Factions
            // -->
            if (attribute.startsWith("power")) {
                return new Element(player.getPower()).getAttribute(attribute.fulfill(1));
            }

            else if (player.hasFaction()) {

                // <--[tag]
                // @attribute <p@player.factions.role>
                // @returns Element
                // @description
                // Returns the player's role in their faction.
                // @plugin Depenizen, Factions
                // -->
                // <--[tag]
                // @attribute <n@npc.factions.role>
                // @returns Element
                // @description
                // Returns the NPC's role in their faction.
                // @plugin Depenizen, Factions
                // -->
                if (attribute.startsWith("role")) {
                    if (player.getRole() != null)
                        return new Element(player.getRole().toString()).getAttribute(attribute.fulfill(1));
                }

                // <--[tag]
                // @attribute <p@player.factions.title>
                // @returns Element
                // @description
                // Returns the player's title.
                // @plugin Depenizen, Factions
                // -->
                // <--[tag]
                // @attribute <n@npc.factions.title>
                // @returns Element
                // @description
                // Returns the NPC's title.
                // @plugin Depenizen, Factions
                // -->
                else if (attribute.startsWith("title")) {
                    if (player.hasTitle())
                        return new Element(player.getTitle()).getAttribute(attribute.fulfill(1));
                }
            }

        }

        // <--[tag]
        // @attribute <p@player.faction>
        // @returns dFaction
        // @description
        // Returns the player's faction.
        // @plugin Depenizen, Factions
        // -->
        // <--[tag]
        // @attribute <n@npc.faction>
        // @returns dFaction
        // @description
        // Returns the NPC's faction.
        // @plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("faction")) {
            return new dFaction(player.getFaction()).getAttribute(attribute.fulfill(1));
        }

        return null;

    }

}