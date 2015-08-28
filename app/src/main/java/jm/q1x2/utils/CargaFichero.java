package jm.q1x2.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jm.q1x2.config.Config;
import android.util.Log;

public class CargaFichero 
{
	public static String getContenidoFicheroJornada(String fich)
	{
		return getContenidoFichero(Config.getCarpetaJornadas()+"/"+fich);
	}

	public static String getContenidoFicheroQuiniela(String fich)
	{
		return getContenidoFichero(Config.getCarpetaQuinielas()+"/"+fich);
	}

	public static String getContenidoFicheroQuinielaResultados(String fich)
	{
		return getContenidoFichero(Config.getCarpetaQuinielasResultados()+"/"+fich);
	}

	public static String getContenidoFicheroJornadaPartidosSuspendidos_o_Corregidos(String fich)
	{
		return getContenidoFichero(Config.getCarpetaJornadasPartidosSuspendidos_o_Corregidos()+"/"+fich);
	}
	
	/*
	 * devuelve el contenido del fichero, o 'null' si se produce un error
	 */
	public static String getContenidoURL(String urlFichero) throws MalformedURLException, IOException
	{
		String contenido= null;
		URL url = new URL(urlFichero);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection)connection;
		int responseCode = httpConnection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) 
		{
			byte[] bites= new byte[1024*100];
			InputStream in = httpConnection.getInputStream();
		    in.read(bites);
			in.close();
		    contenido= new String(bites);
		    contenido= contenido.trim();
		    contenido= contenido.replaceAll("[\r\n]", "");
		}
		else
			contenido= null;
		return contenido;
	}

	
	/*
	 * Hasta v1.5, el parámetro era la URL del fichero, y se leía accediendo directamente a la URL.
	 * Desde v1.6, será un PHP el que lo lea y lo devolverá.
	 */
	private static String getContenidoFichero(String fichero)
	{
		String url= Config.getURLLectorFicheros() + "?fich="+fichero;
		String contenido= null;
		try 
		{	
			contenido= CargaFichero.getContenidoURL(url);
			if (contenido == null)
			{
    			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
    				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar el fichero: "+fichero);
			}
			return contenido;
		}
		catch (MalformedURLException e) 
		{
			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar fichero.", e);
			return null;
		}
		catch (IOException e) 
		{ 
			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar fichero.", e);
			return null;
		}
	}	
		
}
