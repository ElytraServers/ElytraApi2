package cn.elytra.code.api.localeV2;

import cn.elytra.code.api.locale.ILocale;

import java.util.Map;

/**
 * 已有键值表的本地化接口
 *
 * @see cn.elytra.code.api.locale.ILocale 本地化接口
 */
public interface ILocaleEntries extends ILocale {

	@Override
	default String format(String key, Object... args) {
		return has(key) ? String.format(getEntries().get(key), args) : key;
	}

	@Override
	default boolean has(String key) {
		return getEntries().containsKey(key);
	}

	/**
	 * 本地化键值表
	 * <p>
	 * Key 为键值，用 {@code .} 分割，Value 为本地化文本。
	 *
	 * @return 本地化键值表
	 */
	Map<String, String> getEntries();
}
