package jm.q1x2.utils;

import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Properties;

import jm.q1x2.config.Config;

public class Notificaciones 
{
	private static Properties propNotificaciones= null;
	private static long ultimaActualizacion= 0l;  
	
	private static int TIMEOUT_INFO_PERIODO_ACTUALIZACION_MAXIMA_MIN=   15;  // está en minutos
	private static int TIMEOUT_INFO_PERIODO_ACTUALIZACION_NORMAL_MIN= 6*60;  // está en minutos

    public static String getNotificacion(String key)     
    {
    	actualizarNotificacionesSiProcede();
    	return (propNotificaciones==null ? null : propNotificaciones.getProperty(key));
    }

    private static void actualizarNotificacionesSiProcede()
    {
        long iAhora= System.currentTimeMillis();    		
    	if (procedeActualizar(iAhora))
    		actualizarNotificaciones();
    }

    public static void actualizarNotificaciones()
    {
        long iAhora= System.currentTimeMillis();    		
		eliminarNotificaciones();
		cargarNotificaciones();
		
		ultimaActualizacion= iAhora;
    }
    
    private static boolean procedeActualizar(long iAhora)
    {
    	boolean ret= false;
    	if (propNotificaciones==null)
    		ret= true;
    	else if (Config.esDesarrollo())
    		ret= true;
    	else
    	{
	    	/*
	    	 * Consideraré un periodo máximo de validez para el contenido del fichero de notificaciones:
	    	 * 		- entre domingo 23:00 y martes 10:00   : 15 min. (definido en constante)
	    	 * 		- resto de la semana: 6 horas (definido en constante)
	    	 * Si la diferencia entre el último refresco y ahora es superior a ese tiempo, SÍ procederá actualizar
	    	 */
	    	boolean bEnPeriodoDeMaximaFrecuenciaRefresco= estamosEnPeriodoDeMaximaFrecuenciaRefresco();    	
	    	if (bEnPeriodoDeMaximaFrecuenciaRefresco  &&  iAhora - ultimaActualizacion > TIMEOUT_INFO_PERIODO_ACTUALIZACION_MAXIMA_MIN*60*1000)
	    		ret= true;
	    	else if (!bEnPeriodoDeMaximaFrecuenciaRefresco  &&  iAhora - ultimaActualizacion > TIMEOUT_INFO_PERIODO_ACTUALIZACION_NORMAL_MIN*60*1000)
	    		ret= true;
    	}
    	return ret;
    }
    
    private static boolean estamosEnPeriodoDeMaximaFrecuenciaRefresco()
    {
    	/*
    	 *  TRUE:  entre domingo 23:00 y martes 10:00
    	 *  FALSE: resto 
    	 */
    	boolean ret= true;
    	
    	Calendar cal= Calendar.getInstance();
    	int ds= cal.get(Calendar.DAY_OF_WEEK);
    	if (ds >= Calendar.WEDNESDAY  && ds <= Calendar.SATURDAY)
    		ret= false;
    	else if (ds == Calendar.SUNDAY)
		{
			if (cal.get(Calendar.HOUR_OF_DAY) < 23)
				ret= false;
		}
    	else if (ds == Calendar.TUESDAY)
		{
			if (cal.get(Calendar.HOUR_OF_DAY) > 10)
				ret= false;
		}
    	// el lunes no entra en ningún if porque todo ese día es "true"
    	
    	return ret;
    }
    
	private static void eliminarNotificaciones()
	{
		propNotificaciones= null;
	}
    
	private static void cargarNotificaciones()
	{
		try 
		{			
			URL url = new URL(Config.getURLNotificaciones());
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection)connection;
/*[jm]*/  if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))    Log.i(Constantes.LOG_TAG, "[jm] URL notifs: "+Config.getURLNotificaciones());
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) 
			{
	            DataInputStream dis = new DataInputStream(connection.getInputStream());
	            StringBuffer contenido = new StringBuffer();
	            String linea;
	            while ((linea = dis.readLine()) != null)
	                contenido.append(linea.trim());	            
	            dis.close();
	            propNotificaciones= convertirContenidoNotifEnProperties(contenido.toString());
			}
			else
			{
				//propNotificaciones= null;
    			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
    				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar las notificaciones.");
			}
		}
		catch (MalformedURLException e) 
		{
			//propNotificaciones= null;
			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar las notificaciones.", e);
		}
		catch (IOException e) 
		{ 
			//propNotificaciones= null;
			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar las notificaciones. La conexión a la URL no es posible.", e);
		}
		catch (Exception e) 
		{ 
			//propNotificaciones= null;
			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
				Log.w(Constantes.LOG_TAG, "Se ha producido un error cargando las notificaciones. La conexión a la URL no es posible.", e);
		}
	}	

	private static Properties convertirContenidoNotifEnProperties(String texto)
	{
		/*
		 * Formato de entrada:  prop1=valor1#prop2=valor2#....#propk=valork
		 */
		Properties ret= new Properties();
		String[] props= texto.split("#");
		for (String s : props) 
		{
			String[] par= s.split("=");
			ret.setProperty(par[0], par[1]);
		}
		return ret;
	}
	
	public static void notificarDescarga(Context ctx, String que)
	{
		if (!Config.esDesarrollo())
		{
			String _url= null;
			try 
			{
				int id= Preferencias.getClienteId(ctx);
				String ver= Utils.getVersionAplicacion(ctx);
				String params= "?id="+id+"&ver="+ver+"&que="+que;
				_url= Config.getURLNotificarDescarga() + params.replaceAll(" ", "%20");
				URL url = new URL(_url);
				URLConnection connection= url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				httpConnection.getResponseCode();
			}
			catch (Exception e) 
			{ 
				// no hago nada
				if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
					Log.d(Constantes.LOG_TAG, "Error al invocar URL: "+_url);
			}
		}
	}

	/*
	 * @since 1.6
	 */
	public static void notificarPremio(Context ctx, int fechaQuiniela, int aciertos, int dobles, int triples)
	{
		String _url= null;
		try 
		{
			int id= Preferencias.getClienteId(ctx);
			String params= "?id="+id+"&fecha_quiniela="+fechaQuiniela+"&aciertos="+aciertos+"&dobles="+dobles+"&triples="+triples;
			_url= Config.getURLNotificarPremio() + params.replaceAll(" ", "%20");
			URL url = new URL(_url);
			URLConnection connection= url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			httpConnection.getResponseCode();
		}
		catch (Exception e) 
		{ 
			// no hago nada
			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
				Log.d(Constantes.LOG_TAG, "Error al invocar URL: "+_url);
		}
	}
}
