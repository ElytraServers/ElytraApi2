package cn.elytra.code.api.personality;

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
public class PersonalityManager {

	private final File dataDirectory;

	private final List<Configuration> defaults = Lists.newArrayList();

	private final LoadingCache<UUID, Personality> caches;

	public PersonalityManager(ElytraApi plugin) {
		this.dataDirectory = new File(plugin.getDataFolder(), "/PlayerSettings");

		CacheLoader<UUID, Personality> loader = new CacheLoader<UUID, Personality>() {
			@Override
			public Personality load(UUID uuid) {
				return loadPersonality(uuid);
			}
		};

		this.caches = CacheBuilder.newBuilder()
				.initialCapacity(16)
				.maximumSize(256)
				.expireAfterAccess(10, TimeUnit.MINUTES)
				.build(loader);
	}

	private File getSpecifiedFile(UUID uuid) {
		return new File(dataDirectory, uuid+".pn.yml");
	}

	private Personality loadPersonality(UUID uuid) {
		final Personality ps = Personality.loadConfiguration(getSpecifiedFile(uuid));
		defaults.forEach(ps::addDefaults);
		return ps;
	}

	public Personality getOrCreate(UUID uuid) {
		return caches.getUnchecked(uuid);
	}

	public Personality getOrCreate(OfflinePlayer player) {
		return getOrCreate(player.getUniqueId());
	}

	public void addDefaults(Configuration config) {
		defaults.add(config);
	}

}
