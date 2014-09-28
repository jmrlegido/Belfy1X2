package jm.q1x2.bbdd;

import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Notificaciones;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Basedatos 
{    	
    private static String nombreBBDD= "bd1x2";
	public static final int LECTURA= 1;
	public static final int ESCRITURA= 2;
	
	public static SQLiteDatabase getConexion(Context ctx, int lecturaEscritura)
	{
		SQLiteDatabase con = null;
		
		String versiones_bbdd= Notificaciones.getNotificacion(Constantes.NOTIFICACION_VERSIONES_BBDD);
		if (versiones_bbdd != null)
		{
			String[] vers= versiones_bbdd.split(",");
			int versionBBDD= new Integer(vers[vers.length-1]).intValue();		
			
			Q1x2SQLiteHelper sqliteHelper = new Q1x2SQLiteHelper(ctx, nombreBBDD, null, versionBBDD);
			if (lecturaEscritura == LECTURA)
				con = sqliteHelper.getReadableDatabase();
			else
				con = sqliteHelper.getWritableDatabase();
		}
		return con;
	}

    public static void db_execSQL_log(SQLiteDatabase db, String sql)
    {
    	if (!sql.startsWith("#"))   // sería un comentario
    	{
			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
				Log.d(Constantes.LOG_TAG, "sql> "+sql);
	    	db.execSQL(sql);    	
			if (Log.isLoggable(Constantes.LOG_TAG, Log.DEBUG))
				Log.d(Constantes.LOG_TAG, "\tsql << OK ");
    	}
    }
	
}
