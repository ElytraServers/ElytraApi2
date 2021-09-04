package cn.elytra.code.api.localeV1;

import cn.elytra.code.api.locale.ILocale;
import cn.elytra.code.api.locale.LocaleService;
import cn.elytra.code.api.locale.LocaleSetupException;
import cn.elytra.code.api.locale.PluginLocaleManager;
import cn.elytra.code.api.utils.Senders;
import com.google.common.collect.Maps;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Optimized Plugin Locale Manager.
 * <p>
 * This manager can set locale for each player.
 *
 * @since v1.1
 * @see Senders#sendMessage(CommandSender, String, Object...) send localized message
 */
public class PluginLocaleManagerV1 extends PluginLocaleManager {

	private final Map<String, ILocale> localeCache = Maps.newHashMap();

	public PluginLocaleManagerV1(@NotNull Plugin plugin, @NotNull String defaultLang, @Nullable String... allowedLang) {
		super(plugin, defaultLang, allowedLang);
	}

	/**
	 * @see LocaleService#loadLocaleYaml(Plugin, String)
	 */
	public ILocale loadLocaleYaml(String language) {
		final LocaleService service = getLocaleService();

		try {
			return service.loadLocaleYaml(plugin, "locale/" + language + ".yml");
		} catch (LocaleSetupException ex) {
			return exceptionHandler.apply(ex);
		}
	}

	/**
	 * @see LocaleService#loadLocaleJson(Plugin, String)
	 */
	public ILocale loadLocaleJson(String language) {
		final LocaleService service = getLocaleService();

		try {
			return service.loadLocaleJson(plugin, "locale/" + language + ".json");
		} catch (LocaleSetupException ex) {
			return exceptionHandler.apply(ex);
		}
	}

	public ILocale loadAndRegisterLocaleYaml(String language) {
		ILocale locale = loadLocaleYaml(language);
		registerLocale(language, locale);
		return locale;
	}

	public ILocale loadAndRegisterLocaleJson(String language) {
		ILocale locale = loadLocaleJson(language);
		registerLocale(language, locale);
		return locale;
	}

	/**
	 * Register a {@link ILocale} instance for getting in {@link #getLocale(String)}.
	 *
	 * @param language the language code
	 * @param locale   the {@code ILocale} instance.
	 */
	public ILocale registerLocale(String language, ILocale locale) {
		return localeCache.put(language, locale);
	}

	public ILocale unregisterLocale(String language) {
		return localeCache.remove(language);
	}

	public void unregisterAll() {
		localeCache.clear();
	}

	public int getCacheSize() {
		return localeCache.size();
	}

	public Set<String> getAvailableLanguages() {
		return localeCache.keySet();
	}

	@NotNull
	public ILocale getLocale(String language) {
		if (localeCache.containsKey(language)) {
			return localeCache.get(language);
		} else {
			throw new LocaleNotExistsException();
		}
	}

	@NotNull
	public ILocale getLocaleOrDefault(String language, @NotNull ILocale defaultLocale) {
		return localeCache.getOrDefault(language, defaultLocale);
	}

	/**
	 * Get the specified {@code ILocale} if present. The 1st {@code ILocale} in the cache if not present.
	 *
	 * @param language the language code. eg: en, zh
	 * @throws LocaleNotExistsException if no {@code ILocale} instance in the cache
	 */
	public ILocale getLocaleOrDefault(String language) {
		if (localeCache.containsKey(language)) {
			return getLocale(language);
		} else {
			final Optional<ILocale> defaultLocale = localeCache.values().stream().findFirst();
			if (defaultLocale.isPresent()) {
				return defaultLocale.get();
			} else {
				throw new LocaleNotExistsException();
			}
		}
	}

}
