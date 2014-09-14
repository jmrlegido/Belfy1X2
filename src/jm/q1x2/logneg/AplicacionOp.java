package jm.q1x2.logneg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jm.q1x2.activities.Jm1x2Principal.TareaSegundoPlano;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.config.Config;
import jm.q1x2.utils.CargaFichero;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Notificaciones;
import jm.q1x2.utils.Preferencias;
import jm.q1x2.utils.Utils;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AplicacionOp 
{	
    /*
     * Entiendo por 'datos generales' todo aquello que no es una jornada o una quiniela.
     * Por ejemplo: de repente, en segunda división, el Guadalajara desciende y ocupa su plaza 
     * 				el Murcia (que había descendido)
     */
    public static boolean hayCambiosEnDatosGeneralesSinActualizar(SQLiteDatabase con, Context ctx)
    {
    	int iUltimosCambiosEnDatosGeneralesCargados= Preferencias.getPreferenciaInt(ctx, Constantes.PREFERENCIAS_ULTIMO_CAMBIO_DATOS_GENERALES, -1);
    	String sCambiosEnDatosGeneralesEnNotificaciones= Notificaciones.getNotificacion(Constantes.NOTIFICACION_CAMBIOS_DATOS_GENERALES);
    	int iUltimoCambioEnDatosGeneralesEnNotificaciones= -1;
    	if (sCambiosEnDatosGeneralesEnNotificaciones != null)
    	{
			String[] vCambiosDatosGenEnNotif= sCambiosEnDatosGeneralesEnNotificaciones.split(",");
			String sUltimoCambioEnNotif= vCambiosDatosGenEnNotif[vCambiosDatosGenEnNotif.length-1].trim();
    		iUltimoCambioEnDatosGeneralesEnNotificaciones= Integer.valueOf(sUltimoCambioEnNotif);
    	}
    	return (iUltimosCambiosEnDatosGeneralesCargados < iUltimoCambioEnDatosGeneralesEnNotificaciones);
    }

    public static void actualizarDatosGenerales(Context ctx, SQLiteDatabase con, TareaSegundoPlano task, float prcMin, float prcMax)
    {
    	task.publicarProgreso(prcMin);

    	if (hayCambiosEnDatosGeneralesSinActualizar(con, ctx))
    	{
	    	int iUltimoCargado= Preferencias.getPreferenciaInt(ctx, Constantes.PREFERENCIAS_ULTIMO_CAMBIO_DATOS_GENERALES, -1);
	    	String sCambiosDatosGen= Notificaciones.getNotificacion(Constantes.NOTIFICACION_CAMBIOS_DATOS_GENERALES);
	    	String[] vCambiosDatosGen= sCambiosDatosGen.split(",");
	    	String sUltimoEnNotif= vCambiosDatosGen[vCambiosDatosGen.length-1].trim();
	    	int iUltimoEnNotif= -1;
	    	if (sUltimoEnNotif != null)
	    		iUltimoEnNotif= Integer.valueOf(sUltimoEnNotif);
	
	    	try
	    	{
				if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))
					Log.i(Constantes.LOG_TAG, "Se van a actualizar datos generales. ");
	    		if (iUltimoCargado < iUltimoEnNotif)
	    		{
	    			int iAnterior= iUltimoCargado;
	    			for(String _cambio : vCambiosDatosGen)
	    			{
	    				int cambio= Integer.valueOf(_cambio.trim());
	    				if (cambio > iAnterior)
	    				{
	        				String[] sqlsMig= getSentenciasSQLActualizacionDatos(cambio, con);
	        				if (sqlsMig != null)
	        				{
		        	    		for (String sql : sqlsMig)
		        	    			Basedatos.db_execSQL_log(con, sql);
		        	    		Preferencias.grabarPreferenciaInt(ctx, Constantes.PREFERENCIAS_ULTIMO_CAMBIO_DATOS_GENERALES, cambio);
	        				}
	        				iAnterior= cambio;
	        				if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))
	        					Log.i(Constantes.LOG_TAG, "Realizada actualización de datos: "+cambio);
	    				}
	    			}
	    		}
	    	}
	    	catch (FileNotFoundException e) 
	    	{
				if (Log.isLoggable(Constantes.LOG_TAG, Log.ERROR))
					Log.e(Constantes.LOG_TAG, "", e);
	    	}
	    	catch (IOException e)
	    	{
				if (Log.isLoggable(Constantes.LOG_TAG, Log.ERROR))
					Log.e(Constantes.LOG_TAG, "", e);
	    	}
    	}
    	
    	task.publicarProgreso(prcMax);
    }
    
    
    private static String[] getSentenciasSQLActualizacionDatos(int cambioDatosGen, SQLiteDatabase con )
        	throws FileNotFoundException, MalformedURLException, IOException
    {
    	String contenido= "";
		try 
		{			
			StringBuffer _url= new StringBuffer(Config.getURLCarpetaCambiosDatosGenerales())
											.append("/").append(cambioDatosGen).append(".sql");
			contenido= CargaFichero.getContenidoURL(_url.toString());
			if (contenido == null)
			{
    			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
    				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar el fichero de datos generales ("+cambioDatosGen+")");
    			Utils.registrarIncidencia("Se ha producido un error al cargar el fichero de datos generales ("+cambioDatosGen+")", con);
			}
		}
		catch (MalformedURLException e) 
		{
			if (Log.isLoggable(Constantes.LOG_TAG, Log.ERROR))
				Log.e(Constantes.LOG_TAG, "Se ha producido un error al cargar el fichero de datos generales ("+cambioDatosGen+")", e);
			throw e;
		}
		catch (IOException e) 
		{ 
			if (Log.isLoggable(Constantes.LOG_TAG, Log.ERROR))
				Log.e(Constantes.LOG_TAG, "Se ha producido un error al cargar el fichero de datos generales ("+cambioDatosGen+"). La conexión a la URL no es posible.", e);
			throw e;
		}
    	
    	return contenido==null?null:contenido.split(";");
    }


    /**
     * @since v4.4
     */
	public static void mandarInfoError(String infoerror, Context ctx) 
	{
		if (Utils.hayConexionInternet(ctx))
		{
			try
			{
				infoerror= infoerror.replaceAll(" ", "%20");
				infoerror= infoerror.replaceAll("\\?", "_");
				infoerror= infoerror.replaceAll("&", "_");
				String sUrl= Utils.reemplazarParamsUrl(Config.getURLInfoError(), new String[]{"infoerror", infoerror
																							,"versioncode", ""+Utils.getVersionCodeAplicacion(ctx)}); 
				URL url = new URL(sUrl);
				URLConnection connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) 
				{
	    			if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))
	    				Log.i(Constantes.LOG_TAG, "Información de error mandada correctamente.");
				}
				else
				{
	    			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
	    				Log.w(Constantes.LOG_TAG, "Se ha producido un error al mandar información de error.");
				}
			}
			catch (MalformedURLException e) 
			{
				Log.e(Constantes.LOG_TAG, "Se ha producido un error al mandar información de error.", e);
			}
			catch (Exception e) 
			{ 
				Log.e(Constantes.LOG_TAG, "Se ha producido un error cargando las notificaciones. La conexión a la URL no es posible.", e);
			}		
		}
	}   
   
}
