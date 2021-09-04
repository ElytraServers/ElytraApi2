package cn.elytra.code.api.locale;

import cn.elytra.code.api.ElytraApi;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Plugin Locale Manager.
 * <p>
 * Manages locales for a single Plugin.
 *
 * @since v1.0-rc3
 */
public class PluginLocaleManager {

	protected final Plugin plugin;
	protected final String defaultLang;
	protected final String[] allowedLang;

	protected Function<LocaleSetupException, ILocale> exceptionHandler = (ex) -> {
		ex.printStackTrace();
		return null;
	};

	public PluginLocaleManager(@NotNull Plugin plugin, @NotNull String defaultLang, @Nullable String... allowedLang) {
		this.plugin = plugin;
		this.defaultLang = defaultLang;
		this.allowedLang = (String[]) ArrayUtils.add(allowedLang, defaultLang);
	}

	@NotNull
	public LocaleService getLocaleService() {
		return Objects.requireNonNull(JavaPlugin.getPlugin(ElytraApi.class).localeService,
				"LocaleService is NULL!");
	}

	public void setExceptionHandler(Function<LocaleSetupException, ILocale> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * @return The suggested language if presents, default language else.
	 */
	@NotNull
	public String getLanguageAvailable() {
		String suggest = getLocaleService().getSuggestedLanguage();
		if (ArrayUtils.contains(allowedLang, suggest)) {
			return suggest;
		} else {
			return defaultLang;
		}
	}

	/**
	 * @see LocaleService#loadLocaleYaml(Plugin, String)
	 */
	public ILocale loadLocaleYaml() {
		final String language = getLanguageAvailable();
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
	public ILocale loadLocaleJson() {
		final String language = getLanguageAvailable();
		final LocaleService service = getLocaleService();

		try {
			return service.loadLocaleJson(plugin, "locale/" + language + ".json");
		} catch (LocaleSetupException ex) {
			return exceptionHandler.apply(ex);
		}
	}

}
