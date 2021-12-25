package cn.elytra.code.api.localeV2;

import com.google.common.collect.Maps;
import org.bukkit.configuration.MemorySection;

import java.util.Map;

class YamlHelper {

	public static Map<String, String> convert(MemorySection sec) {
		return convert("", sec);
	}

	private static Map<String, String> convert(String prefix, MemorySection sec) {
		final Map<String, String> result = Maps.newLinkedHashMap();

		sec.getValues(true).forEach((k, v) -> {
			// System.out.println(k+"("+k.getClass().getTypeName()+")"+" -> "+v+"("+v.getClass().getTypeName()+")");
			String prefixStr = !prefix.isEmpty() ? prefix + "." : "";
			if(v instanceof String) {
				result.put(prefixStr + k, (String) v);
			} else if(v instanceof MemorySection) {
				result.putAll(convert(prefixStr + k, (MemorySection) v));
			} else {
				System.err.println("Unsupported Type!");
			}
		});

		return result;
	}

}
