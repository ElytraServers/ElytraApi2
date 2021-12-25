package cn.elytra.code.api.utils;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class MapHelper {

	@NotNull
	public static <K, V> V getOrConstruct(Map<K, V> map, K key, Supplier<V> constructor) {
		if(!map.containsKey(key)) {
			map.put(key, Objects.requireNonNull(constructor.get()));
		}
		return Objects.requireNonNull(map.get(key));
	}

}
