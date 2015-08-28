package jm.q1x2.bbdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import jm.q1x2.config.Config;
import jm.q1x2.utils.CargaFichero;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Notificaciones;
import jm.q1x2.utils.Utils;
 
public class Q1x2SQLiteHelper extends SQLiteOpenHelper 
{   
	private String[] sqlCreate = null;
    Context ctx= null;
    
    public Q1x2SQLiteHelper(Context contexto, String nombre, CursorFactory factory, int version) 
    {
        super(contexto, nombre, factory, version);
        ctx= contexto;
    }

    private String[] getSentenciasSQLDeFichero(String fich)
        	throws FileNotFoundException, IOException
    {
    	String contenido= getContenidoFichero(fich).trim();
		return contenido.split(";");
    }
        
    private String[] getSentenciasSQLMigracion(String version, SQLiteDatabase con )
        	throws FileNotFoundException, MalformedURLException, IOException
    {
    	String contenido= "";
		try 
		{			
			StringBuffer _url= new StringBuffer(Config.getURLCarpetaMigracionesBBDD())
											.append("/mig_").append(version).append(".sql");
			contenido= CargaFichero.getContenidoURL(_url.toString());
			if (contenido == null)
			{
    			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
    				Log.w(Constantes.LOG_TAG, "Se ha producido un error al cargar el fichero de migración de BBDD ("+version+")");
    			Utils.registrarIncidencia("Se ha producido un error al cargar el fichero de migración de BBDD ("+version+")", con);
			}
		}
		catch (MalformedURLException e) 
		{
			if (Log.isLoggable(Constantes.LOG_TAG, Log.ERROR))
				Log.e(Constantes.LOG_TAG, "Se ha producido un error al cargar el fichero de migración de BBDD ("+version+")", e);
			throw e;
		}
		catch (IOException e) 
		{ 
			if (Log.isLoggable(Constantes.LOG_TAG, Log.ERROR))
				Log.e(Constantes.LOG_TAG, "Se ha producido un error al cargar el fichero de migración de BBDD ("+version+"). La conexión a la URL no es posible.", e);
			throw e;
		}
    	
    	return contenido==null?null:contenido.split(";");
    }
    
    private String getContenidoFichero(String fich)
        	throws FileNotFoundException, IOException
    {
	     byte[] bites= new byte[1024*100];    	
 	     InputStream is = ctx.getAssets().open(fich);
	     is.read(bites);
	     is.close();
	     String contenido= new String(bites);
	     contenido= contenido.replaceAll("[\n\r]", "");
         return contenido;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) 
    {
    	try
    	{
			if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))
				Log.i(Constantes.LOG_TAG, "Se va a crear la BB.DD. - onCreate.");
    		if (sqlCreate == null)
    			sqlCreate= getSentenciasSQLDeFichero("jm1x2_create.sql");
    		for (String sql : sqlCreate)
    			Basedatos.db_execSQL_log(db, sql.trim());
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
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) 
    {
    	/*
    	 * En       android:versionCode="28"     android:versionName="5.0"
				al ser la primera versión de la temporada 2015/16, no hay actualización.
				Siempre será onCreate
		   Las 2 siguientes líneas SÓLO deberían estar presentes si hay 1 única versión de BB.DD. (la 15082401).
		   En el momento en que haya más, hay que quitarlas 
    	 */
    	onCreate(db);
    	if (true) return;
    	
    	
    	try
    	{
			if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))
				Log.i(Constantes.LOG_TAG, "Se va a actualizar la BB.DD. - onUpgrade. Versión anterior: "+versionAnterior+". Versión nueva: "+versionNueva);
    		if (versionAnterior != versionNueva)
    		{
    			String versiones_bbdd= Notificaciones.getNotificacion(Constantes.NOTIFICACION_VERSIONES_BBDD);
    			String[] vers= versiones_bbdd.split(",");
    			String sVersionAnterior= new Integer(versionAnterior).toString();
    			for(String ver : vers)
    			{
    				if (ver.trim().compareTo(sVersionAnterior) > 0)
    				{
        				String[] sqlsMig= getSentenciasSQLMigracion(ver, db);
        				if (sqlsMig != null)
        				{
	        	    		for (String sql : sqlsMig)
	        	    			Basedatos.db_execSQL_log(db, sql);
        				}
    				}
    			}
    		}
			if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))
				Log.i(Constantes.LOG_TAG, "BB.DD. migrada correctamente (versión "+db.getVersion()+")");
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

}