package de.paulomart.gpex.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;

public class HttpUtils {
	
	public static InputStream requestHttp(String path){
		try {
			return unsafeRequestHttp(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static InputStream unsafeRequestHttp(String path) throws IOException{
		URL url = new URL(path);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("content-type","application/x-www-form-urlencoded");
		urlConnection.setRequestProperty("User-Agent", "ServerCorePlugin");
		return urlConnection.getInputStream();
	}
	
	public static boolean downloadToFile(String url, File output){
		try {
			return unsafeDownloadToFile(url, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static boolean unsafeDownloadToFile(String url, File output) throws IOException{
		output.createNewFile();
		FileOutputStream fos = new FileOutputStream(output);
		fos.getChannel().transferFrom(Channels.newChannel(requestHttp(url)), 0, Long.MAX_VALUE);
		fos.close();
		return true;
	}	
}
