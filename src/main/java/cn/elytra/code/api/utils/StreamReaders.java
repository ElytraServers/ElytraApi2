package cn.elytra.code.api.utils;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class StreamReaders {

	private StreamReaders() {}

	@Nullable
	public static String readText(InputStream is) {
		final Scanner sc = new Scanner(is, "UTF-8");
		if(sc.hasNext()) {
			return sc.next();
		} else {
			return null;
		}
	}

	@NotNull
	public static List<String> readLines(InputStream is) {
		final List<String> r = Lists.newArrayList();
		final Scanner sc = new Scanner(is, "UTF-8");
		while(true) {
			if(sc.hasNextLine()) {
				r.add(sc.nextLine());
			} else {
				return r;
			}
		}
	}

}
