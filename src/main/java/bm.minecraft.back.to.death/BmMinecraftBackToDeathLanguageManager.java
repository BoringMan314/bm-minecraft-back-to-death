package bm.minecraft.back.to.death;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Loads player-facing text from the language selected by this build. */
public final class BmMinecraftBackToDeathLanguageManager {
    private static final String FALLBACK_LANGUAGE = "zh_TW";

    private final BmMinecraftBackToDeathPlugin plugin;
    private YamlConfiguration languageConfig = new YamlConfiguration();

    public BmMinecraftBackToDeathLanguageManager(BmMinecraftBackToDeathPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveResource("active-language.yml", true);
        String language = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "active-language.yml"))
                .getString("language", FALLBACK_LANGUAGE);
        String resourcePath = "lang/" + language + ".yml";
        if (plugin.getResource(resourcePath) == null) {
            plugin.getLogger().warning(fallbackConsole("unknown-language")
                    .replace("{language}", language)
                    .replace("{fallback}", FALLBACK_LANGUAGE));
            resourcePath = "lang/" + FALLBACK_LANGUAGE + ".yml";
        }
        YamlConfiguration bundledConfig = loadBundledConfiguration(resourcePath);
        File languageFile = new File(plugin.getDataFolder(), resourcePath);
        if (!languageFile.isFile()) {
            plugin.saveResource(resourcePath, false);
        }
        boolean canUpdate = upgradeLanguageFile(languageFile, resourcePath, bundledConfig);
        if (!canUpdate) {
            languageConfig = bundledConfig;
            return;
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        languageConfig.setDefaults(bundledConfig);
        saveMissingDefaults(languageFile);
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }

    public String getString(String path) {
        return languageConfig.getString(path, "");
    }

    public String getConsole(String key) {
        return languageConfig.getString("console." + key, key);
    }

    private YamlConfiguration loadBundledConfiguration(String resourcePath) {
        try (InputStream input = plugin.getResource(resourcePath)) {
            if (input != null) {
                return YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
            }
        } catch (Exception exception) {
            plugin.getLogger().warning(fallbackConsole("language-defaults-load-failed")
                    .replace("{resource}", resourcePath));
        }
        return new YamlConfiguration();
    }

    private boolean upgradeLanguageFile(File languageFile, String resourcePath, YamlConfiguration bundledConfig) {
        if (!languageFile.isFile()) {
            return true;
        }
        int localVersion = YamlConfiguration.loadConfiguration(languageFile).getInt("language-format-version", 0);
        int bundledVersion = bundledConfig.getInt("language-format-version", 0);
        if (localVersion >= bundledVersion) {
            return true;
        }

        Path original = languageFile.toPath();
        Path backup = original.resolveSibling(languageFile.getName() + ".pre-v" + bundledVersion + ".bak");
        Path temporary = original.resolveSibling(languageFile.getName() + ".bundled.tmp");
        boolean backupCreated = false;
        try (InputStream input = plugin.getResource(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Missing bundled resource: " + resourcePath);
            }
            Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
            backupCreated = true;
            Files.copy(temporary, original, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception exception) {
            if (backupCreated) {
                try {
                    Files.copy(backup, original, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception restoreException) {
                    exception.addSuppressed(restoreException);
                }
            }
            plugin.getLogger().warning(bundledConfig.getString("console.language-upgrade-failed",
                            fallbackConsole("language-upgrade-failed"))
                    .replace("{file}", languageFile.getName())
                    .replace("{error}", String.valueOf(exception.getMessage())));
            return false;
        } finally {
            try {
                Files.deleteIfExists(temporary);
            } catch (Exception ignored) {
                // The migration result is unaffected by temporary-file cleanup.
            }
        }
    }

    private String fallbackConsole(String key) {
        String resourcePath = "lang/" + FALLBACK_LANGUAGE + ".yml";
        try (InputStream input = plugin.getResource(resourcePath)) {
            if (input != null) {
                return YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8))
                        .getString("console." + key, key);
            }
        } catch (Exception ignored) {
            // Returning the key still identifies the missing console message.
        }
        return key;
    }

    private void saveMissingDefaults(File languageFile) {
        languageConfig.options().copyDefaults(true);
        try {
            languageConfig.save(languageFile);
        } catch (Exception exception) {
            plugin.getLogger().warning(getConsole("language-update-failed")
                    .replace("{file}", languageFile.getName()));
        }
    }
}
