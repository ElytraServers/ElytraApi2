package cn.elytra.code.api.locale;

import cn.elytra.code.api.ElytraApi;
import cn.elytra.code.api.annotation.ApiFeature;
import cn.elytra.code.api.annotation.ApiVersion;
import cn.elytra.code.api.utils.StreamReaders;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApiFeature(since = ApiVersion.V1)
public class LocaleService {

	private final ElytraApi api;

	/**
	 * 语言代码
	 *
	 * 例如："en"，"zh"
	 */
	private String suggestedLanguage = Locale.ENGLISH.getLanguage();

	public LocaleService(ElytraApi api) {
		this.api = api;
	}

	public void loadConfig() {
		String lang = api.getConfig().getString("language");
		if(lang != null) {
			setSuggestedLanguage(new Locale(lang));
		} else {
			throw new IllegalStateException("Configuration error. Section 'language' is unavailable.");
		}
	}

	@NotNull
	public String getSuggestedLanguage() {
		return suggestedLanguage;
	}

	public void setSuggestedLanguage(@NotNull Locale locale) {
		if(!new Locale(suggestedLanguage).equals(locale)) {
			final String oldSuggested = suggestedLanguage;
			final String newSuggested = locale.getLanguage();

			this.suggestedLanguage = newSuggested;
			Bukkit.getPluginManager().callEvent(new SuggestedLanguageChangedEvent(newSuggested, oldSuggested));
		}
	}

	/**
	 * Return the InputStream of file, which is in the specified Plugin.
	 *
	 * @param plugin Specified Plugin
	 * @param path   Path to the file
	 * @return InputStream
	 * @throws LocaleSetupException thrown when the file doesn't exists
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
