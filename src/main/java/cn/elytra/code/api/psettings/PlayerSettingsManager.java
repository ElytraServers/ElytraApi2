package cn.elytra.code.api.psettings;

import cn.elytra.code.api.ElytraApi;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Player Settings Manager.
 */
public class PlayerSettingsManager {

	private final ElytraApi plugin;
	private final File dataDirectory;

	private final List<Configuration> defaults = Lists.newArrayList();

	private final LoadingCache<UUID, PlayerSettings> caches;

	public PlayerSettingsManager(ElytraApi plugin) {
		this.plugin = plugin;
		this.dataDirectory = new File(plugin.getDataFolder(), "/PlayerSettings");

		CacheLoader<UUID, PlayerSettings> loader = new CacheLoader<UUID, PlayerSettings>() {
			@Override
			public PlayerSettings load(UUID uuid) {
				return loadSettings(uuid);
			}
		};

		this.caches = CacheBuilder.newBuilder()
				.initialCapacity(16)
				.maximumSize(256)
				.expireAfterAccess(10, TimeUnit.MINUTES)
				.build(loader);
	}

	private File getSpecifiedFile(UUID uuid) {
		return new File(dataDirectory, "PlayerSettings_"+uuid+".json");
	}

	private PlayerSettings loadSettings(UUID uuid) {
		final PlayerSettings ps = PlayerSettings.loadConfiguration(getSpecifiedFile(uuid));
		defaults.forEach(ps::addDefaults);
		return ps;
	}

	public PlayerSettings getSettings(UUID uuid) {
		return caches.getUnchecked(uuid);
	}

	public PlayerSettings getPlayerSettings(OfflinePlayer player) {
		return getSettings(player.getUniqueId());
	}

	public void addDefaults(Configuration config) {
		defaults.add(config);
	}

}
