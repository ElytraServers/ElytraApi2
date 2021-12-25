package cn.elytra.code.api.localeV2;

import cn.elytra.code.api.ElytraApi;
import cn.elytra.code.api.locale.ILocale;
import cn.elytra.code.api.locale.LocaleService;
import cn.elytra.code.api.locale.LocaleSetupException;
import cn.elytra.code.api.utils.MapHelper;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 第三版本地化管理器
 *
 * @see ELocale#translate(String, Object...) 获取本地化内容
 * @see ELocale#translate(Locale, String, Object...) 依照语言获取本地化内容
 * @see ELocale#register(ILocaleEntries, Locale) 注册本地化接口
 * @see ELocale#loadLocaleYaml(Plugin, String) 使用 Yaml 读取本地化键值表
 * @see ELocale#loadLocaleJson(Plugin, String) 使用 Json 读取本地化键值表
 */
public class ELocale {

	private static final Logger LOGGER = Logger.getLogger("E-Locale");

	private static final Map<Locale, MergedLocaleEntries> TRANSLATORS = Maps.newHashMap();

	private static final LocaleService SERVICE = ElytraApi.instance().localeService;

	/**
	 * 注册本地化接口
	 *
	 * @param translator 本地化接口
	 * @param locale     本地化语言
	 */
	public static void register(@NotNull ILocaleEntries translator, @Nullable Locale locale) {
		MapHelper.getOrConstruct(TRANSLATORS, Locale.ROOT, MergedLocaleEntries::new).merge(translator, true);
		MapHelper.getOrConstruct(TRANSLATORS, locale, MergedLocaleEntries::new).merge(translator, true);
	}

	/**
	 * 获取本地化内容
	 *
	 * @param locale 语言
	 * @param key    本地化键
	 * @param format 用于 {@link String#format(String, Object...)} 的内容
	 * @return 本地化内容
	 */
	@NotNull
	public static String translate(@Nullable Locale locale, @NotNull String key, Object... format) {
		Validate.notNull(key);

		if(locale == null) {
			locale = Locale.forLanguageTag(SERVICE.getSuggestedLanguage());
		}

		MergedLocaleEntries root = TRANSLATORS.get(Locale.ROOT);
		MergedLocaleEntries entries = TRANSLATORS.get(locale);

		if(entries != null && entries.has(key)) {
			return entries.format(key, format);
		} else if(root != null && root.has(key)) {
			return root.format(key, format);
		} else {
			return ChatColor.ITALIC + key;
		}
	}

	/**
	 * 获取本地化内容
	 *
	 * @param key    本地化键
	 * @param format 用于 {@link String#format(String, Object...)} 的内容
	 * @return 本地化内容
	 */
	@NotNull
	public static String translate(String key, Object... format) {
		return translate(null, key, format);
	}

	@NotNull
	public static Map<Locale, MergedLocaleEntries> getTranslators() {
		return Maps.newHashMap(TRANSLATORS);
	}

	/**
	 * 加载位于给定插件zip包中的 './locale/[language].yml' 的语言信息为本地化键值表
	 *
	 * @param plugin   给定插件
	 * @param language 给定语言
	 * @return 本地化键值表
	 * @throws LocaleSetupException 当文件读取出现错误时抛出
	 */
	@NotNull
	public static ILocaleEntries loadLocaleYaml(Plugin plugin, String language) throws LocaleSetupException {
		return loadLocaleYaml0(plugin, "locale/" + language + ".yml");
	}

	/**
	 * 加载位于给定插件zip包中的 './locale/[language].json' 的语言信息为本地化键值表
	 *
	 * @param plugin   给定插件
	 * @param language 给定语言
	 * @return 本地化键值表
	 * @throws LocaleSetupException 当文件读取出现错误时抛出
	 */
	@NotNull
	public static ILocaleEntries loadLocaleJson(Plugin plugin, String language) throws LocaleSetupException {
		return loadLocaleJson0(plugin, language);
	}

	@NotNull
	public static ILocaleEntries[] initPluginLocaleYaml(Plugin plugin, String... languages) throws LocaleSetupException {
		ILocaleEntries[] result = new ILocaleEntries[languages.length];

		for(int i = 0; i < languages.length; i++) {
			String lang = languages[i];
			Locale locale = Locale.forLanguageTag(lang);

			ElytraApi.LOGGER.info("Loading language " + locale + "(" + lang + ")" + " for plugin " + plugin.getName());

			ILocaleEntries entries = loadLocaleYaml(plugin, lang);
			register(entries, locale);
			result[i] = entries;
		}

		return result;
	}

	@NotNull
	public static ILocaleEntries[] initPluginLocaleJson(Plugin plugin, String... languages) throws LocaleSetupException {
		ILocaleEntries[] result = new ILocaleEntries[languages.length];

		for(int i = 0; i < languages.length; i++) {
			final String lang = languages[i];
			final Locale locale = Locale.forLanguageTag(lang);

			ElytraApi.LOGGER.info("Loading language " + locale + " for plugin " + plugin.getName());

			ILocaleEntries entries = loadLocaleJson(plugin, lang);
			register(entries, locale);
			result[i] = entries;
		}

		return result;
	}

	// INTERNALS

	@NotNull
	private static ILocaleEntries loadLocaleYaml0(Plugin plugin, String path) {
		final InputStream is = getFileInJar(plugin, path);
		final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(is, Charsets.UTF_8));
		final Map<String, String> locales = YamlHelper.convert(yaml);
		return () -> locales;
	}

	@NotNull
	private static ILocaleEntries loadLocaleJson0(Plugin plugin, String path) {
		final InputStream is = getFileInJar(plugin, path);
		final JsonElement json = new JsonParser().parse(new InputStreamReader(is, Charsets.UTF_8));
		if(json.isJsonObject()) {
			final JsonObject root = json.getAsJsonObject();
			final Map<String, String> locales = root.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsString()));
			return () -> locales;
		} else {
			throw new LocaleSetupException("Root in the Json file is not a Object.", LocaleSetupException.TYPE_JSON_ROOT_NOT_FIT);
		}
	}

	@NotNull
	private static InputStream getFileInJar(Plugin plugin, String path) throws LocaleSetupException {
		InputStream is;
		if((is = plugin.getResource(path)) != null) {
			return is;
		} else {
			throw new LocaleSetupException("File not found. [path=" + path + "]", LocaleSetupException.TYPE_FILE_MISSING);
		}
	}

}
