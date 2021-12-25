package cn.elytra.code.api.personality;

import cn.elytra.code.api.ElytraApi;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Settings for Players.
 *
 * @since v1.1
 */
public abstract class Personality extends YamlConfiguration {

	public static final String PS_ELYTRA_API_LANGUAGE = "elytra.api.language";

	public static Personality get(OfflinePlayer player) {
		return ElytraApi.instance().settingsManager.getOrCreate(player);
	}

	public abstract File getFilePath();

	public void save() {
		try {
			save0();
		} catch(IOException ex) {
			ElytraApi.LOGGER.log(Level.WARNING, "Unable to save PlayerSettings to " + this.getFilePath(), ex);
		}
	}

	public void save0() throws IOException {
		this.save(getFilePath());
	}

	public void setNoSave(@NotNull String path, @Nullable Object value) {
		super.set(path, value);
	}

	@Override
	public void set(@NotNull String path, @Nullable Object value) {
		super.set(path, value);
		save();
	}

	/**
	 * Get PlayerSettings from File.
	 *
	 * @param file The input file
	 * @return The instance
	 */
	@NotNull
	public static Personality loadConfiguration(@NotNull File file) {
		Validate.notNull(file, "File cannot be null");

		Personality config = new Personality() {
			@Override
			public File getFilePath() {
				return file;
			}
		};

		try {
			config.load(file);
		} catch(FileNotFoundException ignored) {
		} catch(IOException | InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}

		return config;
	}

	public String getLanguage() {
		return getString(PS_ELYTRA_API_LANGUAGE);
	}

	public void setLanguage(String lang) {
		set(PS_ELYTRA_API_LANGUAGE, lang);
		// Dispatch events - PlayerChangeLanguageEvent
		// Bukkit.getPluginManager().callEvent(new PlayerChangeLanguageEvent(lang));
	}

	// QUICK METHODS

	/**
	 * 仅用于 {@link cn.elytra.code.api.localeV2.ELocale#translate(Locale, String, Object...)}
	 *
	 * @param sender 玩家
	 * @return 语言
	 */
	@Nullable
	public static Locale getLanguage(CommandSender sender) {
		if(sender instanceof OfflinePlayer) {
			return Locale.forLanguageTag(Personality.get((OfflinePlayer) sender).getLanguage());
		} else {
			return null;
		}
	}

}
