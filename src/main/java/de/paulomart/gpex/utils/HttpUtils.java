package de.paulomart.gpex.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;

public class HttpUtils {
	
	public static String requestHttp(String path){
		try {
			return unsafeRequestHttp(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String unsafeRequestHttp(String path) throws IOException{
		URL url = new URL(path);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
		urlConnection.setRequestProperty("content-type","application/x-www-form-urlencoded;charset=utf-8");
		urlConnection.setRequestProperty("User-Agent", "GPEX Webrequest");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		
		try {
			String line;

			while((line = reader.readLine()) != null){
				builder.append(line);
				builder.append('\n');
			}
			
		} finally {
			reader.close();
		}

		return builder.toString();
	}
	
	public static InputStream requestStreamHttp(String path){
		try {
			return unsaferequestStreamHttp(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static InputStream unsaferequestStreamHttp(String path) throws IOException{
		URL url = new URL(path);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
		urlConnection.setRequestProperty("content-type","application/x-www-form-urlencoded;charset=utf-8");
		urlConnection.setRequestProperty("User-Agent", "GPEX Webrequest");
		
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
		fos.getChannel().transferFrom(Channels.newChannel(requestStreamHttp(url)), 0, Long.MAX_VALUE);
		fos.close();
		return true;
	}	
}
