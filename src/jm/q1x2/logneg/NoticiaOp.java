package jm.q1x2.logneg;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jm.q1x2.config.Config;
import jm.q1x2.transobj.Noticia;
import jm.q1x2.transobj.Noticias;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class NoticiaOp 
{
	/*
	 * devuelve null si no hay conexión a internet o realmente no hay noticias posteriores a <ultimaMostrada>
	 */
	public static Noticias getNoticias(String ultimaMostrada, Context ctx) 
	{
		Noticias ret= null;
		
		if (Utils.hayConexionInternet(ctx))
		{
			try
			{
				String sUrl= Utils.reemplazarParamsUrl(Config.getURLNoticias(), new String[]{"ultima", (ultimaMostrada==null?"":ultimaMostrada)
																							,"versioncode", ""+Utils.getVersionCodeAplicacion(ctx)}); 
				URL url = new URL(sUrl);
				URLConnection connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) 
				{
		            DataInputStream dis = new DataInputStream(connection.getInputStream());
		            StringBuffer contenido = new StringBuffer();
		            String linea;
		            while ((linea = dis.readLine()) != null)
		                contenido.append(linea);	            
		            dis.close();
		            
		            if (contenido.length() > 0)  /* si no ha habido ningún error en el servidor */
		            {
				    	JSONObject jsonObj = new JSONObject(contenido.toString());
						String ultima = jsonObj.getString("ultima");
						JSONArray noticias = jsonObj.getJSONArray("noticias");
						ret= new Noticias();
						ret.setUltima(ultima);
						for (int k= 0; k< noticias.length(); k++) 
							ret.anadirNoticia(new Noticia(noticias.getString(k)));
		            }
				}
				else
				{
	    			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
	    				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar las noticias.");
				}
			}
			catch (MalformedURLException e) 
			{
				Log.e(Constantes.LOG_TAG, "Se ha producido un error al cargar las notificaciones.", e);
			}
			catch (IOException e) 
			{ 
				Log.e(Constantes.LOG_TAG, "Se ha producido un error al cargar las notificaciones. La conexión a la URL no es posible.", e);
			}
			catch(JSONException e)
			{
				Log.e(Constantes.LOG_TAG, "Error parseando json devuelto por el servidor", e);
			}
			catch (Exception e) 
			{ 
				Log.e(Constantes.LOG_TAG, "Se ha producido un error cargando las notificaciones. La conexión a la URL no es posible.", e);
			}		
		}
		return ret;
	}   

}
