package cn.elytra.code.example;

import cn.elytra.code.api.localeV1.PluginLocaleManagerV1;
import cn.elytra.code.api.utils.Senders;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class UseLocalePerPlayerPlugin extends JavaPlugin {

	public final PluginLocaleManagerV1 localeManager;

	public UseLocalePerPlayerPlugin() {
		this.localeManager = new PluginLocaleManagerV1(this, "en", "zh");
	}

	@Override
	public void onEnable() {
		localeManager.loadAndRegisterLocaleYaml("en");
		localeManager.loadAndRegisterLocaleYaml("zh");
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		// The translation is defined in [/test/resources/locale/]
		// If the sender's language settings is 'zh' => '这是一段测试文本'
		// If the sender's language settings is 'en' => 'This is a test text'
		Senders.sendMessage(sender, "test");
		return true;
	}
}
