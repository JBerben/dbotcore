package org.darkstorm.runescape.util;

import java.io.*;


public class DirectoryCache implements Cache {
	private final File directory;

	public DirectoryCache(File directory) {
		if(!directory.exists())
			directory.mkdirs();
		this.directory = directory;
	}

	@Override
	public boolean isCached(String name) {
		for(String fileName : directory.list())
			if(fileName.equals(name + ".cache"))
				return true;
		return false;
	}

	@Override
	public byte[] loadCache(String name) {
		if(!isCached(name))
			return null;
		File file = new File(directory, name + ".cache");
		if(!file.exists())
			return null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			FileInputStream in = new FileInputStream(file);
			int read;
			byte[] buffer = new byte[1024];
			while((read = in.read(buffer)) != -1)
				byteOut.write(buffer, 0, read);
			in.close();
			byteOut.flush();
			byte[] fileData = byteOut.toByteArray();
			return fileData;
		} catch(IOException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	@Override
	public void saveCache(String name, byte[] cache) {
		File file = new File(directory, name + ".cache");
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(cache);
			out.flush();
			out.close();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void removeCache(String name) {
		File file = new File(directory, name + ".cache");
		if(!file.exists())
			return;
		file.delete();
	}

	@Override
	public void clearCache() {
		for(File file : directory.listFiles())
			file.delete();
	}
}
