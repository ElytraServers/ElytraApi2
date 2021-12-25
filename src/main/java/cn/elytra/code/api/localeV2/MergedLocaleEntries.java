package cn.elytra.code.api.localeV2;

import com.google.common.collect.Maps;

import java.util.Map;

public class MergedLocaleEntries implements ILocaleEntries {

	private final Map<String, String> entries = Maps.newHashMap();

	@Override
	public Map<String, String> getEntries() {
		return entries;
	}

	public void merge(ILocaleEntries entries, boolean cover) {
		if(cover) {
			this.entries.putAll(entries.getEntries());
		} else {
			entries.getEntries().forEach((key, val) -> {
				if(!this.entries.containsKey(key)) {
					this.entries.put(key, val);
				}
			});
		}
	}

	@Override
	public String toString() {
		return getEntries().toString();
	}
}
