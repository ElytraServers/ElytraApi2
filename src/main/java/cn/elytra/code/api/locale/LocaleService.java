package cn.elytra.code.api.locale;

import cn.elytra.code.api.ElytraApi;
import cn.elytra.code.api.utils.StreamReaders;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Locale Service Provider
 *
 * @see cn.elytra.code.api.localeV2.ELocale
 */
public class LocaleService {

	private final ElytraApi api;

	/**
	 * Language Code
	 * <p>
	 * eg: "en", "zh", "jp"
	 */
	private String suggestedLanguage = Locale.ENGLISH.getLanguage();

	public LocaleService(ElytraApi api) {
		this.api = api;
	}

	public void loadConfig() {
		String lang = api.getConfig().getString("language");
		if (lang != null) {
			setSuggestedLanguage(new Locale(lang));
		} else {
			throw new IllegalStateException("Configuration error. Section 'language' is unavailable.");
		}
	}

	/**
	 * The suggested language code. Maybe not available in some plugin!
	 *
	 * @return the suggested language code
	 */
	@NotNull
	public String getSuggestedLanguage() {
		return suggestedLanguage;
	}

	/**
	 * Set the suggested language code. Can be absent in some plugin.
	 *
	 * @param locale the suggested code
	 */
	public void setSuggestedLanguage(@NotNull Locale locale) {
		if (!new Locale(suggestedLanguage).equals(locale)) {
			this.suggestedLanguage = locale.getLanguage();
		}
	}

	/**
	 * Return the InputStream of file existing in the specified Plugin JAR pack.
	 *
	 * @param plugin specified plugin
	 * @param path   path to the file
	 * @return InputStream
	 * @throws LocaleSetupException thrown when the file doesn't exist
	 */
	@NotNull
	protected InputStream getFileInJar(Plugin plugin, String path) throws LocaleSetupException {
		InputStream is;
		if ((is = plugin.getResource(path)) != null) {
			return is;
		} else {
			throw new LocaleSetupException("File not found. [path=" + path + "]", LocaleSetupException.TYPE_FILE_MISSING);
		}
	}

	public ILocale loadLocaleYaml(Plugin plugin, String path) {
		final InputStream is = getFileInJar(plugin, path);
		final YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(is, Charsets.UTF_8));
		return new ILocale() {
			@Override
			public String format(String key, Object... args) {
				return yaml.contains(key) ? String.format(yaml.getString(key, "@ERROR@"), args) : key;
			}

			@Override
			public boolean has(String key) {
				return yaml.contains(key);
			}
		};
	}

	public ILocale loadLocaleJson(Plugin plugin, String path) {
		final InputStream is = getFileInJar(plugin, path);
		final JsonElement json = new JsonParser().parse(new InputStreamReader(is, Charsets.UTF_8));
		if (json.isJsonObject()) {
			final JsonObject root = json.getAsJsonObject();
			return new ILocale() {
				@Override
				public String format(String key, Object... args) {
					return root.has(key) ? String.format(root.getAsJsonPrimitive(key).getAsString(), args) : key;
				}

				@Override
				public boolean has(String key) {
					return root.has(key);
				}
			};
		} else {
			throw new LocaleSetupException("Root in the Json file is not a Object.", LocaleSetupException.TYPE_JSON_ROOT_NOT_FIT);
		}
	}

	@Deprecated
	public ILocale loadLocaleMinecraftLang(Plugin plugin, String path) {
		final InputStream is = getFileInJar(plugin, path);
		final List<String> lines = StreamReaders.readLines(is);
		final Map<String, String> map = Maps.newHashMap();

		for (String line : lines) {
			String[] kv = line.split("=", 2);
			if (kv.length == 2) {
				map.put(kv[0], kv[1]);
			} else {
				api.getLogger().warning(() -> "Unable to parse the line (\"" + line + "\") in " + path + ".");
			}
		}

		return new ILocale() {
			@Override
			public String format(String key, Object... args) {
				return map.containsKey(key) ? String.format(map.get(key), args) : key;
			}

			@Override
			public boolean has(String key) {
				return map.containsKey(key);
			}
		};
	}

}
