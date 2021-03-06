package com.denizenscript.depenizen.bukkit.support;

import com.denizenscript.depenizen.bukkit.DepenizenPlugin;
import com.denizenscript.depenizen.bukkit.support.bungee.BungeeSupport;
import net.aufdemrand.denizen.utilities.debugging.dB;
import com.denizenscript.depenizen.bukkit.Settings;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Supported {

    private static SupportManager supportManager;
    private static Map<String, Class<? extends Support>> supportClasses;

    public static void setup(DepenizenPlugin depenizen, PluginManager pluginManager, ClassLoader loader) {
        getSupportClasses(loader);
        supportManager = new SupportManager(depenizen);

        // Register plugin supports
        for (String name : depenizen.getDescription().getSoftDepend()) {
            try {
                if (set(name.toUpperCase(), pluginManager.getPlugin(name))) {
                    DepenizenPlugin.depenizenLog(name + " hooked, enabling add-ons.");
                }
                else {
                    DepenizenPlugin.depenizenLog(name + " not found, add-ons will not enable.");
                }
            }
            catch (Exception e) {
                dB.echoError("Error while hooking plugin '" + name + "'");
                dB.echoError(e);
            }
        }

        // Register BungeeCord support
        if (Settings.socketEnabled()) {
            DepenizenPlugin.depenizenLog("BungeeCord socket enabled in config, enabling add-ons.");
            supportManager.register(new BungeeSupport());
        }
        else {
            DepenizenPlugin.depenizenLog("BungeeCord socket disabled in config, add-ons will not enable.");
        }

        supportManager.registerNewObjects();
    }

    private static boolean set(String name, Plugin plugin) {
        try {
            if (plugin == null) {
                return false;
            }
            supportManager.register(Support.setPlugin(supportClasses.get(name), plugin));
            return true;
        }
        catch (IllegalAccessException e) {
            dB.echoError("Report this error to Morphan1 or the Denizen dev team: SupportedIllegalAccess-" + name);
            dB.echoError(e);
        }
        catch (InstantiationException e) {
            dB.echoError("Report this error to Morphan1 or the Denizen dev team: SupportedInstantiationSupport-" + name);
            dB.echoError(e);
        }
        return false;
    }

    private static void getSupportClasses(ClassLoader loader) {
        supportClasses = new HashMap<String, Class<? extends Support>>();
        final String pkg = "com.denizenscript.depenizen.bukkit.support.plugins";
        try {
            final Enumeration<URL> resources = loader.getResources(pkg.replace('.', '/'));
            URL url;
            while (resources.hasMoreElements()) {
                url = resources.nextElement();
                if (url == null) {
                    continue;
                }
                URLConnection conn = url.openConnection();
                if (conn instanceof JarURLConnection) {
                    final JarFile jarFile = ((JarURLConnection) conn).getJarFile();
                    final Enumeration<JarEntry> entries = jarFile.entries();
                    String name;
                    while (entries.hasMoreElements()) {
                        name = entries.nextElement().getName().replace('/', '.');
                        if (name.startsWith(pkg) && name.endsWith("Support.class")) {
                            String key = name.replace("Support.class", "");
                            key = key.substring(key.lastIndexOf('.') + 1).toUpperCase();
                            try {
                                supportClasses.put(key,
                                        (Class<? extends Support>) Class.forName(name.substring(0, name.lastIndexOf('.'))));
                            }
                            catch (Throwable e) {
                                dB.echoError(e);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            dB.echoError(e);
        }
    }
}
