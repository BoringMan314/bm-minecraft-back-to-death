package bm.minecraft.back.to.death;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Saves a player's most recent death position and implements /back. */
public final class BmMinecraftBackToDeathPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
    private final Map<UUID, DeathPoint> deathPoints = new HashMap<>();
    private File dataFile;
    private BmMinecraftBackToDeathLanguageManager languageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        languageManager = new BmMinecraftBackToDeathLanguageManager(this);
        languageManager.reload();
        dataFile = new File(getDataFolder(), "data.yml");
        loadDeathPoints();

        getServer().getPluginManager().registerEvents(this, this);
        registerCommand("back");
        registerCommand("bm-minecraft-back-to-death");
        getLogger().info(languageManager.getConsole("enabled").replace("{version}", getPluginMeta().getVersion()));
    }

    @Override
    public void onDisable() {
        saveDeathPoints();
        if (languageManager != null) {
            getLogger().info(languageManager.getConsole("disabled"));
        }
    }

    private void registerCommand(String name) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            throw new IllegalStateException(languageManager.getConsole("missing-command")
                    .replace("{command}", name));
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isFeatureEnabled()) {
            return;
        }

        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        deathPoints.put(player.getUniqueId(), DeathPoint.from(deathLocation));
        saveDeathPoints();
        message(player, "death-saved");
        message(player, "death-location",
                "{x}", coordinate(deathLocation.getX()),
                "{y}", coordinate(deathLocation.getY()),
                "{z}", coordinate(deathLocation.getZ()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("back")) {
            if (args.length != 0) {
                message(sender, "usage-back");
                return true;
            }
            return runBack(sender);
        }
        return runManagement(sender, args);
    }

    private boolean runBack(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            message(sender, "player-only");
            return true;
        }
        if (!isFeatureEnabled()) {
            message(player, "feature-disabled");
            return true;
        }

        DeathPoint point = deathPoints.get(player.getUniqueId());
        if (point == null) {
            message(player, "no-death-location");
            return true;
        }
        Location target = point.toLocation();
        if (target == null) {
            message(player, "world-not-found");
            return true;
        }

        player.teleportAsync(target).thenAccept(success -> {
            if (success) {
                message(player, "back-success");
            } else {
                message(player, "world-not-found");
            }
        });
        return true;
    }

    private boolean runManagement(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        if (!canManage(sender)) {
            message(sender, "no-permission");
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "0" -> {
                getConfig().set("enabled", false);
                saveConfig();
                message(sender, "disabled");
            }
            case "1" -> {
                getConfig().set("enabled", true);
                saveConfig();
                message(sender, "enabled");
            }
            case "reload" -> {
                reloadConfig();
                languageManager.reload();
                message(sender, "reloaded");
            }
            case "info" -> message(sender, "info", "{version}", getPluginMeta().getVersion());
            case "status" -> message(sender, "status",
                    "{status}", languageManager.getString(isFeatureEnabled()
                            ? "messages.status-enabled"
                            : "messages.status-disabled"),
                    "{count}", Integer.toString(deathPoints.size()));
            default -> showHelp(sender);
        }
        return true;
    }

    private boolean canManage(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return true;
        }
        return !getConfig().getBoolean("admin-require-op", true)
                || sender.isOp()
                || sender.hasPermission("bm-minecraft-back-to-death.admin");
    }

    private boolean isFeatureEnabled() {
        return getConfig().getBoolean("enabled", true);
    }

    private void showHelp(CommandSender sender) {
        for (String key : List.of("help-header", "help-back", "help-toggle", "help-reload", "help-info", "help-status", "help-footer")) {
            sender.sendMessage(color(languageManager.getString("messages." + key)));
        }
    }

    private void message(CommandSender sender, String messageKey, String... replacements) {
        String text = languageManager.getString("messages." + messageKey);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            text = text.replace(replacements[i], replacements[i + 1]);
        }
        sender.sendMessage(color(text));
    }

    private String color(String value) {
        return ChatColor.translateAlternateColorCodes('&', value == null ? "" : value);
    }

    private String coordinate(double value) {
        return Long.toString((long) Math.floor(value));
    }

    private void loadDeathPoints() {
        deathPoints.clear();
        if (!dataFile.isFile()) {
            return;
        }
        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection deaths = data.getConfigurationSection("deaths");
        if (deaths == null) {
            return;
        }
        for (String id : deaths.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(id);
                ConfigurationSection entry = deaths.getConfigurationSection(id);
                DeathPoint point = DeathPoint.fromSection(entry);
                if (point != null) {
                    deathPoints.put(uuid, point);
                }
            } catch (IllegalArgumentException ignored) {
                getLogger().warning(languageManager.getConsole("invalid-player-uuid")
                        .replace("{uuid}", id));
            }
        }
    }

    private void saveDeathPoints() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().severe(languageManager.getConsole("data-directory-create-failed"));
            return;
        }
        YamlConfiguration data = new YamlConfiguration();
        for (Map.Entry<UUID, DeathPoint> entry : deathPoints.entrySet()) {
            entry.getValue().write(data.createSection("deaths." + entry.getKey()));
        }
        try {
            data.save(dataFile);
        } catch (IOException exception) {
            getLogger().severe(languageManager.getConsole("data-save-failed")
                    .replace("{error}", String.valueOf(exception.getMessage())));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("bm-minecraft-back-to-death") || args.length != 1 || !canManage(sender)) {
            return Collections.emptyList();
        }
        String start = args[0].toLowerCase(Locale.ROOT);
        Collection<String> choices = List.of("0", "1", "info", "status", "reload");
        List<String> result = new ArrayList<>();
        for (String choice : choices) {
            if (choice.startsWith(start)) {
                result.add(choice);
            }
        }
        return result;
    }

    private record DeathPoint(String worldId, String worldName, double x, double y, double z, float yaw, float pitch) {
        static DeathPoint from(Location location) {
            World world = location.getWorld();
            return new DeathPoint(world.getUID().toString(), world.getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }

        static DeathPoint fromSection(ConfigurationSection section) {
            if (section == null || !section.isString("world-id") || !section.isString("world-name")) {
                return null;
            }
            return new DeathPoint(section.getString("world-id"), section.getString("world-name"),
                    section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                    (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
        }

        Location toLocation() {
            World world = null;
            try {
                world = Bukkit.getWorld(UUID.fromString(worldId));
            } catch (IllegalArgumentException ignored) {
                // Old data can still be recovered by world name below.
            }
            if (world == null) {
                world = Bukkit.getWorld(worldName);
            }
            return world == null ? null : new Location(world, x, y, z, yaw, pitch);
        }

        void write(ConfigurationSection section) {
            section.set("world-id", worldId);
            section.set("world-name", worldName);
            section.set("x", x);
            section.set("y", y);
            section.set("z", z);
            section.set("yaw", yaw);
            section.set("pitch", pitch);
        }
    }
}
