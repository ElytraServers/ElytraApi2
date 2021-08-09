package cn.elytra.code.api.locale;

import cn.elytra.code.api.annotation.ApiFeature;
import cn.elytra.code.api.annotation.ApiVersion;

public interface ILocale {

	String format(String key, Object...args);

	@ApiFeature(since = ApiVersion.V1_RC3)
	default boolean has(String key) {
		return !format(key).contentEquals(key);
	}

	ILocale EMPTY_LOCALE = (key, args) -> key;

}
