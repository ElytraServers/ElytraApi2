package cn.elytra.code.api.psettings;

import cn.elytra.code.api.ElytraApi;
import cn.elytra.code.api.utils.Loggers;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Settings for Players.
 *
 * @since v1.1
 */
public abstract class PlayerSettings extends YamlConfiguration {

	public static final String PS_ELYTRA_API_LANGUAGE = "elytra.api.language";

	public static PlayerSettings get(OfflinePlayer player) {
		return ElytraApi.instance().settingsManager.getPlayerSettings(player);
	}

	public abstract File getFilePath();

	public void save() {
		try {
			save0();
		} catch (IOException ex) {
			Loggers.error("Unable to save PlayerSettings to '{0}'.", ex, this.getFilePath());
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
	public static PlayerSettings loadConfiguration(@NotNull File file) {
		Validate.notNull(file, "File cannot be null");

		PlayerSettings config = new PlayerSettings() {
			@Override
			public File getFilePath() {
				return file;
			}
		};

		try {
			config.load(file);
		} catch (FileNotFoundException ignored) {
		} catch (IOException | InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}

		return config;
	}

	public String getLanguage() {
		return getString(PS_ELYTRA_API_LANGUAGE);
	}

	public void setLanguage(String lang) {
		set(PS_ELYTRA_API_LANGUAGE, lang);
	}

}
