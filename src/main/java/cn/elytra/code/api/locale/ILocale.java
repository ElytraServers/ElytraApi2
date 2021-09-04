package cn.elytra.code.api.locale;

/**
 * Locale Interface.
 * <p>
 * Should be constructed in {@link LocaleService} or {@link PluginLocaleManager}.
 *
 * @see LocaleService
 * @see PluginLocaleManager
 */
public interface ILocale {

	String format(String key, Object... args);

	default boolean has(String key) {
		return !format(key).contentEquals(key);
	}

	ILocale EMPTY_LOCALE = (key, args) -> key;

}
