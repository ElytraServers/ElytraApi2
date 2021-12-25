package cn.elytra.code.example;

import cn.elytra.code.api.localeV2.ELocale;
import org.bukkit.plugin.java.JavaPlugin;

public class UseV3LocalePlugin extends JavaPlugin {

	public UseV3LocalePlugin() {
		ELocale.initPluginLocaleYaml(this, "en", "zh");
		getLogger().info(ELocale.translate("test"));
	}

}
