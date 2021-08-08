package cn.elytra.code.api.locale;

public interface ILocale {

	String format(String key, Object...args);

	ILocale EMPTY_LOCALE = (key, args) -> key;

}
