package cn.elytra.code.api.localeV1.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerChangeLanguageEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final String language;

	public PlayerChangeLanguageEvent(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
