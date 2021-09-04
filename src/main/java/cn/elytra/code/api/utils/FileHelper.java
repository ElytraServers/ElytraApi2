package cn.elytra.code.api.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {

	/**
	 * Correct the type of the file and its parent. Then create the new file and its parent directory unless exists.
	 * @param file the new file
	 * @throws IOException If an I/O error occurred
	 */
	public static void create(File file) throws IOException {
		File parent = file.getParentFile();

		if (parent.exists() && !parent.isDirectory()) {
			if(!parent.delete()) { // Delete the Parent Directory, and throw Exception when failure occurs.
				throw new RuntimeException("Cannot delete directory("+parent+") for file("+file+").");
			}
		}
		if(!parent.exists()) {
			if(!parent.mkdirs()) {
				throw new RuntimeException("Cannot create directory("+parent+") for file. "+file);
			}
		}
		if(file.exists() && !file.isFile()) {
			if(!file.delete()) { // Delete the File, and throw Exception when failure occurs.
				throw new RuntimeException("Cannot delete file("+file+").");
			}
		}
		if(!file.exists()) {
			if(!file.createNewFile()) {
				throw new RuntimeException("Cannot create file("+file+").");
			}
		}
	}

	public static void delete(File file) throws IOException {
		Files.delete(file.toPath());
	}

	public static void write(File file, String str, Charset charset) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
		bw.write(str);
	}

	public static void move(File src, File dest) throws IOException {
		Path pathSrc = src.toPath();
		Path pathDest = dest.toPath();
		Files.move(pathSrc, pathDest);
	}

	@NotNull
	public static FileInputStream getInputStream(File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	@Nullable
	public static FileInputStream getInputStreamNoThrow(File file) {
		try {
			return getInputStream(file);
		} catch(IOException ex) {
			return null;
		}
	}

	@NotNull
	public static FileInputStream getInputStreamRuntimeThrow(File file) {
		try {
			return getInputStream(file);
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@NotNull
	public static FileOutputStream getOutputStream(File file) throws FileNotFoundException {
		return new FileOutputStream(file);
	}

	@Nullable
	public static FileOutputStream getOutputStreamNoThrow(File file) {
		try {
			return getOutputStream(file);
		} catch(IOException ex) {
			return null;
		}
	}

	@NotNull
	public static FileOutputStream getOutputStreamRuntimeThrow(File file) {
		try {
			return getOutputStream(file);
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
