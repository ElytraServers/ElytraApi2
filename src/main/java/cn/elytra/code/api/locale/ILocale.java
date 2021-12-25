package cn.elytra.code.api.locale;

/**
 * Locale Interface.
 *
 * @see cn.elytra.code.api.localeV2.ILocaleEntries
 */
public interface ILocale {

	String format(String key, Object... args);

	default boolean has(String key) {
		return !format(key).contentEquals(key);
	}

	ILocale EMPTY_LOCALE = (key, args) -> key;

}
