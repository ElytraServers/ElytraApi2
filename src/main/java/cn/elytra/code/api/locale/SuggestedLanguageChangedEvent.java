package cn.elytra.code.api.locale;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SuggestedLanguageChangedEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final String suggestedLanguage, oldSuggestedLanguage;

	public SuggestedLanguageChangedEvent(@NotNull String suggestedLanguage, @NotNull String oldSuggestedLanguage) {
		this.suggestedLanguage = suggestedLanguage;
		this.oldSuggestedLanguage = oldSuggestedLanguage;
	}

	@NotNull
	public String getSuggestedLanguage() {
		return suggestedLanguage;
	}

	@NotNull
	public String getOldSuggestedLanguage() {
		return oldSuggestedLanguage;
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
