package cn.elytra.code.api.command;

import cn.elytra.code.api.utils.Loggers;
import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Abstract Brigadier Command Class.
 * <p>
 * Extend this class and in the Constructor register your command.
 * Then in the Main class of your plugin invoke #register to register.
 * <p>
 * Mojang Brigadier is used in Minecraft Forge, so you may have help in the Forge Forum
 * about how to use Brigadier.
 *
 * @see com.mojang.brigadier.CommandDispatcher
 * @since v1.1
 */
public class BrigadierAbstractCommand implements CommandExecutor, TabCompleter {

	private final String commandName;

	public final CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();

	public BrigadierAbstractCommand(String commandName) {
		this.commandName = commandName;
	}

	/**
	 * Register this to the Bukkit Server
	 *
	 * @param plugin the plugin
	 */
	public void register(Plugin plugin) {
		PluginCommand command = plugin.getServer().getPluginCommand(commandName);
		if (command != null) {
			command.setExecutor(this);
			command.setTabCompleter(this);
		} else {
			Loggers.error("Unable to register null command {0}.", commandName);
		}
	}

	private String getWholeString(String label, String[] args) {
		if (args.length == 0) {
			return label;
		} else {
			return label + ' ' + Joiner.on(' ').join(args);
		}
	}

	@Override
	public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		try {
			dispatcher.execute(getWholeString(label, args), sender);
			return true;
		} catch (CommandSyntaxException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
	}

	@NotNull
	@Override
	public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		CompletableFuture<Suggestions> future = dispatcher.getCompletionSuggestions(dispatcher.parse(getWholeString(label, args), sender));
		Suggestions suggestions = future.join();
		return suggestions.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
	}

	public static LiteralArgumentBuilder<CommandSender> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}

	public static <T> RequiredArgumentBuilder<CommandSender, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
}
