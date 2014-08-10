package jm.q1x2.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.IncidenciaDao;
import jm.q1x2.logneg.QuinielaOp;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils 
{
	private static Random semillaRandon= new Random(System.currentTimeMillis());

	public static boolean hayConexionInternet(Context act)
	{
	    boolean HaveConnectedWifi = false;
	    boolean HaveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo)
	    {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                HaveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                HaveConnectedMobile = true;
	    }
	    return HaveConnectedWifi || HaveConnectedMobile;
	}	

	/*
	 * esta función debería notificarme a mí como desarrollador la incidencia:
	 * por ejemplo, para darme cuenta de que no he dejado en la ubicación web el fichero de migración de BB.DD.
	 */
	public static void registrarIncidencia(String txtIncidencia, SQLiteDatabase con)
	{
        IncidenciaDao incDao= new IncidenciaDao(con);
        incDao.registrarIncidencia(txtIncidencia);
	}
	
	public static void registrarIncidencia(String txtIncidencia, Context ctx)
	{
    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
    	registrarIncidencia(txtIncidencia, con);
        con.close();		
	}
	
	/*
	 * 20110724 --> 24/07/2011
	 */
	public static String getFecha(int iFecha)
	{
		StringBuilder ret= new StringBuilder();
		String f= ""+iFecha;
		ret.append(f.substring(6, 8))
		   .append("/")
		   .append(f.substring(4, 6))
		   .append("/")
		   .append(f.substring(0, 4));
		return ret.toString();
	}
	
	/*
	 * Formato 20110924
	 */
	public static String hoy()
	{
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}
	
	public static int generarClienteId()
	{
		// número aleatorio entre 1 y 999999
		return getNumAleatorio(999998) + 1;
	}

	/*
	 * genera número entre 0 y 'hasta' incluídos ambos
	 */
	public static int getNumAleatorio(int hasta)
	{
		return (semillaRandon.nextInt(hasta+1));
	}

    public static boolean estaElementoEnArray(int k, ArrayList<Integer> array)
    {
	   boolean ret= false;
	   for (Integer _item: array)
	   {
		   int item= _item.intValue();
		   if (item == k)
		   {
			   ret= true;
			   break;
		   }
	   }
	   return ret;
    }

    public static String getVersionAplicacion(Context ctx)
    {
    	String ret= "1.0";  // versión que se dará si no lo lee del MANIFEST
    	try 
    	{
    	   PackageManager manager = ctx.getPackageManager();
    	   PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
    	   ret= info.versionName;
    	} 
    	catch (Exception e) 
    	{
    		ret= "0.1";  // versión que dará si hay algún error al obtenerlo del MANIFEST
    	}
   		return ret;
    }

    public static int getVersionCodeAplicacion(Context ctx)
    {
    	int ret= 1;  // versión que se dará si no lo lee del MANIFEST
    	try 
    	{
    	   PackageManager manager = ctx.getPackageManager();
    	   PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
    	   ret= info.versionCode;
    	} 
    	catch (Exception e) 
    	{
    		ret= 0;  // versión que dará si hay algún error al obtenerlo del MANIFEST
    	}
   		return ret;
    }
    
	/*
	 * devolverá  VERDADERO si yo sólo si estamos entre     sábado 19:00 y martes 01:00 
	 * en caso contrario devolverá  FALSO
	 */
	public static boolean estamosEnPeriodoDeTiempoTalQueLaJornadaHaEmpezadoPeroNoAcabado()
	{
		boolean ret= true;
		
        Calendar cal= Calendar.getInstance();
        int diaSem= cal.get(Calendar.DAY_OF_WEEK);
        if (diaSem >= Calendar.WEDNESDAY  &&  diaSem <= Calendar.FRIDAY)
        	ret= false;
        else if (diaSem == Calendar.SATURDAY)
        {
        	if (cal.get(Calendar.HOUR_OF_DAY) < 19)
        		ret= false;
        }
        else if (diaSem == Calendar.TUESDAY)
        {
        	if (cal.get(Calendar.HOUR_OF_DAY) > 1)
        		ret= false;
        }
		return ret;
	}

	/*
	 * 78  --> 1 min. 18 seg.
	 * 137 --> 2 min. 17 seg.
	 * 34  --> 34 seg.
	 */
	public static String fromSegundosToMinSec(int segundos)
	{
		int seg= segundos;
		int min= seg/60;
		if (min > 0)
			seg-= 60*min;
		return (min>0?""+min+" min. ":"") + (seg>0?""+seg+" seg.":"");
	}
	
	/*
	 * aquí se tiene en cuenta que 'iPronosticoUsuario' puede contener dobles o triples
	 */
	public static boolean resultadoAcertado(int iPronosticoUsuario, int iResultadoOK)
	{
		boolean ret= false;
		if (iResultadoOK == QuinielaOp.RES_1  ||  iResultadoOK == QuinielaOp.RES_X  || iResultadoOK == QuinielaOp.RES_2)
		{
			if (iPronosticoUsuario == QuinielaOp.RES_1X2)
				ret= true;
			else
			{
				if (iResultadoOK == QuinielaOp.RES_1  &&
					 (iPronosticoUsuario==QuinielaOp.RES_1 || iPronosticoUsuario==QuinielaOp.RES_1X || iPronosticoUsuario==QuinielaOp.RES_12)	)
					ret= true;
				else if (iResultadoOK == QuinielaOp.RES_X  &&
					 (iPronosticoUsuario==QuinielaOp.RES_X || iPronosticoUsuario==QuinielaOp.RES_1X || iPronosticoUsuario==QuinielaOp.RES_X2)	)
					ret= true;
				else if (iResultadoOK == QuinielaOp.RES_2  &&
					 (iPronosticoUsuario==QuinielaOp.RES_2 || iPronosticoUsuario==QuinielaOp.RES_12 || iPronosticoUsuario==QuinielaOp.RES_X2)	)
					ret= true;
			}
		}
		return ret;
	}
	
	/*
	 * params es un array de strings donde los elementos: 
	 * 		en posiciones impares son los textos a sustituir
	 * 		en posiciones pares son los textos por los que se sustituirán
	 * Ejemplo: 
	 *   reemplazarParamsUrl("http://server.com/ress/?v1={id1}&v2={id2}", new String[]{"id1","valor1","id2","valor2"})
	 * devuelve
	 *   http://server.com/ress/?v1=valor&v2=valor2
	 *   
	 * NOTA: los parámetros siempre deben venir rodeados de llaves
	 */
	public static String reemplazarParamsUrl(String url, String[] params)
	{
		String ret= url;
		for (int k=0; k< params.length; k+=2)
			ret= ret.replaceAll("\\{"+params[k]+"\\}", params[k+1]);
		
		return ret;
	}
	
}
