package cn.elytra.code.example;

import cn.elytra.code.api.ElytraApi;
import cn.elytra.code.api.personality.Personality;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class UsePlayerSettingsPlugin extends JavaPlugin {

	public static final String PS_THIRD_PARTY_PLUGIN_KEY = "some.other.plugin.data.key";
	public static final String DEFAULT_VALUE = "Java is better than Kotlin(x";

	@Override
	public void onEnable() {
		try {
			// Check if ElytraApi exists
			Class.forName("cn.elytra.code.api.ElytraApi");

			// Instantiate a new Configuration
			Configuration defaultValues = new YamlConfiguration();
			// Put the default values in
			defaultValues.set(PS_THIRD_PARTY_PLUGIN_KEY, DEFAULT_VALUE);
			// Add the default configuration as default to PlayerSettingsManager
			ElytraApi.instance().settingsManager.addDefaults(defaultValues);
		} catch (ClassNotFoundException ex) {
			// ElytraApi wasn't loaded. Check your dependency sections.
			throw new RuntimeException(ex);
		}
	}

	public String getData(OfflinePlayer player) {
		return Personality.get(player).getString(PS_THIRD_PARTY_PLUGIN_KEY);
	}

	public void setData(OfflinePlayer player, String data) {
		Personality.get(player).set(PS_THIRD_PARTY_PLUGIN_KEY, data);
	}
}
