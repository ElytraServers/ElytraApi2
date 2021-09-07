package cn.elytra.code.api.localeV1;

import cn.elytra.code.api.ElytraApi;
import cn.elytra.code.api.locale.ILocale;
import cn.elytra.code.api.locale.LocaleService;
import cn.elytra.code.api.locale.LocaleSetupException;
import cn.elytra.code.api.locale.PluginLocaleManager;
import cn.elytra.code.api.utils.Senders;
import com.google.common.collect.Maps;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static cn.elytra.code.api.psettings.PlayerSettings.PS_ELYTRA_API_LANGUAGE;

/**
 * Optimized Plugin Locale Manager.
 * <p>
 * This manager can set locale for each player.
 *
 * @see Senders#sendMessage(CommandSender, String, Object...) send localized message
 * @since v1.1
 */
public class PluginLocaleManagerV1 extends PluginLocaleManager {

	private static final Map<String, PluginLocaleManagerV1> MGR_CACHE = Maps.newHashMap();

	private final Map<String, ILocale> localeCache = Maps.newHashMap();

	public PluginLocaleManagerV1(@NotNull Plugin plugin, @NotNull String defaultLang, @Nullable String... allowedLang) {
		super(plugin, defaultLang, allowedLang);
		MGR_CACHE.put(plugin.getName(), this);
	}

	/**
	 * Get the PluginLocaleManagerV1 of given plugin.
	 *
	 * @param plugin the plugin
	 * @return the instance for the plugin.
	 */
	public static PluginLocaleManagerV1 of(Plugin plugin) {
		return MGR_CACHE.get(plugin.getName());
	}

	public static PluginLocaleManagerV1 of(String pluginName) {
		return MGR_CACHE.get(pluginName);
	}

	/**
	 * @deprecated Use {@link #loadLocaleYaml(String)} instead.
	 */
	@Override
	@Deprecated
	public ILocale loadLocaleYaml() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated Use {@link #loadLocaleJson(String)} instead.
	 */
	@Override
	@Deprecated
	public ILocale loadLocaleJson() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the Locale from Yaml file.
	 *
	 * @param language the locale code
	 * @return the Locale instance.
	 */
	public ILocale loadLocaleYaml(String language) {
		final LocaleService service = getLocaleService();

		try {
			return service.loadLocaleYaml(plugin, "locale/" + language + ".yml");
		} catch(LocaleSetupException ex) {
			return exceptionHandler.apply(ex);
		}
	}


	/**
	 * Get the Locale from Json file.
	 *
	 * @param language the locale code
	 * @return the Locale instance.
	 */
	public ILocale loadLocaleJson(String language) {
		final LocaleService service = getLocaleService();

		try {
			return service.loadLocaleJson(plugin, "locale/" + language + ".json");
		} catch(LocaleSetupException ex) {
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

	/**
	 * Get the very first available locale instance.
	 *
	 * @return the first available Locale instance.
	 */
	public ILocale getLocaleAvailable() {
		return getLocaleOrDefault(null);
	}

	@NotNull
	public ILocale getLocale(String language) {
		if(localeCache.containsKey(language)) {
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
		if(localeCache.containsKey(language)) {
			return getLocale(language);
		} else {
			final Optional<ILocale> defaultLocale = localeCache.values().stream().findFirst();
			if(defaultLocale.isPresent()) {
				return defaultLocale.get();
			} else {
				throw new LocaleNotExistsException();
			}
		}
	}

	/**
	 * Wrap Logger into FormatLogger with given locale.
	 *
	 * @param logger the logger
	 * @param locale the name of locale
	 * @return the FormatLogger instance
	 * @throws LocaleNotExistsException if the locale doesn't exist
	 */
	public FormatLogger convertFormatLogger(Logger logger, String locale) {
		return new FormatLogger(logger, getLocale(locale));
	}

	/**
	 * Wrap Logger into FormatLogger with given locale if it exists or default locale else.
	 *
	 * @param logger        the logger
	 * @param locale        the name of locale
	 * @param defaultLocale the default locale
	 * @return the FormatLogger instance
	 */
	public FormatLogger convertFormatLoggerOrDefault(Logger logger, String locale, ILocale defaultLocale) {
		return new FormatLogger(logger, getLocaleOrDefault(locale, defaultLocale));
	}

	/**
	 * Wrap Logger into FormatLogger with given locale if it exists or default locale else.
	 *
	 * @param logger the logger
	 * @param locale the name of locale
	 * @return the FormatLogger instance
	 * @throws LocaleNotExistsException if no Locale has registered.
	 */
	public FormatLogger convertFormatLoggerOrDefault(Logger logger, String locale) {
		return new FormatLogger(logger, getLocaleOrDefault(locale));
	}

	public FormatLogger getFormatLogger(String locale) {
		return convertFormatLogger(plugin.getLogger(), locale);
	}

	public FormatLogger getFormatLoggerOrDefault(String locale, ILocale defaultLocale) {
		return convertFormatLoggerOrDefault(plugin.getLogger(), locale, defaultLocale);
	}

	public FormatLogger getFormatLoggerOrDefault(String locale) {
		return convertFormatLoggerOrDefault(plugin.getLogger(), locale);
	}

	/**
	 * Send messages to the given sender.
	 * @param sender the receiver
	 * @param key    the key to the i18n text
	 * @param args   the values to the placeholders
	 */
	public void sendMessage(CommandSender sender, String key, Object...args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			String lang = ElytraApi.instance().settingsManager.getPlayerSettings(player).getString(PS_ELYTRA_API_LANGUAGE);
			sender.sendMessage(getLocaleOrDefault(lang).format(key, args));
		} else {
			sender.sendMessage(getLocaleAvailable().format(key, args));
		}
	}

	/**
	 * Send messages to the given sender.
	 *
	 * @param plugin the plugin(to get the locale instance)
	 * @param sender the receiver
	 * @param key    the key to the i18n text
	 * @param args   the values to the placeholders
	 */
	public static void sendMessage(Plugin plugin, CommandSender sender, String key, Object...args) {
		PluginLocaleManagerV1 mgr;
		if((mgr = of(plugin)) != null) {
			mgr.sendMessage(sender, key, args);
		} else {
			throw new LocaleManagerNotExistsException();
		}
	}

}
