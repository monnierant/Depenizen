package com.denizenscript.depenizen.bukkit.objects;

import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Fetchable;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.TagContext;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Location;

public class dTown implements dObject {

    /////////////////////
    //   OBJECT FETCHER
    /////////////////

    public static dTown valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("town")
    public static dTown valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }

        ////////
        // Match town name

        string = string.replace("town@", "");
        try {
            return new dTown(TownyUniverse.getDataSource().getTown(string));
        }
        catch (NotRegisteredException e) {
            return null;
        }
    }

    public static boolean matches(String arg) {
        arg = arg.replace("town@", "");
        return TownyUniverse.getDataSource().hasTown(arg);
    }

    /////////////////////
    //   STATIC CONSTRUCTORS
    /////////////////

    Town town = null;

    public dTown(Town town) {
        this.town = town;
    }

    public static dTown fromWorldCoord(WorldCoord coord) {
        if (coord == null) {
            return null;
        }
        try {
            return new dTown(coord.getTownBlock().getTown());
        }
        catch (NotRegisteredException e) {
            return null;
        }
    }

    /////////////////////
    //   dObject Methods
    /////////////////

    private String prefix = "Town";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public dTown setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String debug() {
        return (prefix + "='<A>" + identify() + "<G>' ");
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String getObjectType() {
        return "Town";
    }

    @Override
    public String identify() {
        return "town@" + town.getName();
    }

    @Override
    public String identifySimple() {
        // TODO: Properties?
        return identify();
    }

    public Town getTown() {
        return town;
    }

    public Boolean equals(dTown town) {
        return CoreUtilities.toLowerCase(town.getTown().getName()).equals(CoreUtilities.toLowerCase(this.getTown().getName()));
    }

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <town@town.assistants>
        // @returns dList(dPlayer)
        // @description
        // Returns a list of the town's assistants.
        // @Plugin DepenizenBukkit, Towny
        // -->
        if (attribute.startsWith("assistants")) {
            dList list = new dList();
            for (Resident resident : town.getAssistants()) {
                list.add(dPlayer.valueOf(resident.getName()).identify());
            }
            return list.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.balance>
        // @returns Element(Decimal)
        // @description
        // Returns the current money balance of the town.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("balance")) {
            try {
                return new Element(town.getHoldingBalance()).getAttribute(attribute.fulfill(1));
            }
            catch (EconomyException e) {
                if (!attribute.hasAlternative()) {
                    dB.echoError("Invalid economy response!");
                }
            }
        }

        // <--[tag]
        // @attribute <town@town.board>
        // @returns Element
        // @description
        // Returns the town's current board.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("board")) {
            return new Element(town.getTownBoard())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.is_open>
        // @returns Element(Boolean)
        // @description
        // Returns true if the town is currently open.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("isopen") || attribute.startsWith("is_open")) {
            return new Element(town.isOpen())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.is_public>
        // @returns Element(Boolean)
        // @description
        // Returns true if the town is currently public.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("ispublic") || attribute.startsWith("is_public")) {
            return new Element(town.isPublic())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.mayor>
        // @returns dPlayer
        // @description
        // Returns the mayor of the town.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("mayor")) {
            return dPlayer.valueOf(town.getMayor().getName())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.name>
        // @returns Element
        // @description
        // Returns the town's names.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("name")) {
            return new Element(town.getName())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.nation>
        // @returns dNation
        // @description
        // Returns the nation that the town belongs to.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("nation")) {
            try {
                return new dNation(town.getNation())
                        .getAttribute(attribute.fulfill(1));
            }
            catch (NotRegisteredException e) {
            }
        }

        // <--[tag]
        // @attribute <town@town.player_count>
        // @returns Element(Number)
        // @description
        // Returns the number of players in the town.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("playercount") || attribute.startsWith("player_count")) {
            return new Element(town.getNumResidents())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.residents>
        // @returns dList(dPlayer)
        // @description
        // Returns a list of the town's residents.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("residents")) {
            dList list = new dList();
            for (Resident resident : town.getResidents()) {
                list.add(dPlayer.valueOf(resident.getName()).identify());
            }
            return list.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.size>
        // @returns Element(Number)
        // @description
        // Returns the number of blocks the town owns.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("size")) {
            return new Element(town.getPurchasedBlocks())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.spawn>
        // @returns dLocation
        // @description
        // Returns the spawn point of the town.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("spawn")) {
            try {
                return new dLocation(town.getSpawn().getBlock().getLocation())
                        .getAttribute(attribute.fulfill(1));
            }
            catch (TownyException e) {
            }
        }

        // <--[tag]
        // @attribute <town@town.tag>
        // @returns Element
        // @description
        // Returns the town's tag.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("tag")) {
            return new Element(town.getTag())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.taxes>
        // @returns Element(Decimal)
        // @description
        // Returns the town's current taxes.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("taxes")) {
            return new Element(town.getTaxes())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.outposts>
        // @returns dList(dLocation)
        // @description
        // Returns a list of the town's outpost locations.
        // @Plugin DepenizenBukkit, Towny
        // -->
        else if (attribute.startsWith("outposts")) {
            dList posts = new dList();
            for (Location p : town.getAllOutpostSpawns()) {
                posts.add(new dLocation(p).identify());
            }
            return posts.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <town@town.type>
        // @returns Element
        // @description
        // Always returns 'Town' for dTown objects. All objects fetchable by the Object Fetcher will return the
        // type of object that is fulfilling this attribute.
        // @Plugin DepenizenBukkit, Towny
        // -->
        if (attribute.startsWith("type")) {
            return new Element("Town").getAttribute(attribute.fulfill(1));
        }

        return new Element(identify()).getAttribute(attribute);

    }
}
