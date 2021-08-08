package cn.elytra.code.example;

import cn.elytra.code.api.locale.ILocale;
import cn.elytra.code.api.locale.LocaleService;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class UseLocaleServicePlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		RegisteredServiceProvider<LocaleService> rsp = getServer().getServicesManager()
				.getRegistration(LocaleService.class);
		if(rsp == null) {
			getLogger().info("LocaleService Missing!");
		} else {
			ILocale locale = rsp.getProvider().loadLocaleYaml(this, "locale/zh.yml");
			getLogger().info(
					locale.format("elytra.api.plugin.enabled")
			);
		}
	}
}
