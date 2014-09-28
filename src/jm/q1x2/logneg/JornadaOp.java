package jm.q1x2.logneg;

import java.util.ArrayList;

import jm.q1x2.activities.Jm1x2Principal.TareaSegundoPlano;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.bbdd.dao.JornadaDao;
import jm.q1x2.transobj.Equipo;
import jm.q1x2.transobj.Jornada;
import jm.q1x2.transobj.Partido;
import jm.q1x2.utils.CargaFichero;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Notificaciones;
import jm.q1x2.utils.Preferencias;
import jm.q1x2.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class JornadaOp 
{	
    public static boolean hayDatosQueActualizar(Context ctx, int division)
    {
    	boolean hay= false;
    	int ultimaJornadaCargada= getUltimaJornadaCargada(ctx, division);
    	int ultimaJornadaDisponible= getUltimaJornadaDisponible(ctx, division);
    	hay= ultimaJornadaCargada != ultimaJornadaDisponible;
    	if (!hay)
        	hay= (getNumJornadasCargadas(ctx, division) != ultimaJornadaCargada);
    	return hay;
    }
    
    public static boolean actualizarDatos(Context ctx, SQLiteDatabase con, TareaSegundoPlano task, float prcMin, float prcMax)
    {
    	task.publicarProgreso(prcMin);
    	
    	StringBuffer ok= null;

        JornadaDao jorDao= new JornadaDao(con);
        EquipoDao eqDao= new EquipoDao(con);

        
        /*
         * Primera división
         */
    	int ultimaJornadaCargada_div1= getUltimaJornadaCargada(ctx, Equipo.DIVISION_1);
    	int ultimaJornadaDisponible_div1= getUltimaJornadaDisponible(ctx, Equipo.DIVISION_1);

    	ArrayList<Integer> jornadasACargar_div1= new ArrayList<Integer>();

    	String[] plantillaDisponibles= new String[ultimaJornadaDisponible_div1+1];
    	for (int k= 1; k<= ultimaJornadaDisponible_div1; k++)
    		plantillaDisponibles[k]= null;

    	String jornadasNoDisponibles_div1= Notificaciones.getNotificacion(Constantes.NOTIFICACION_JORNADAS_NODISPONIBLES_DIV1);
    	if (jornadasNoDisponibles_div1 != null)
    	{
	    	String[] jorNoDisp= jornadasNoDisponibles_div1.split(",");
	    	for (String jor : jorNoDisp) 
	    	{
	    		int j= new Integer(jor.trim()).intValue();
	    		plantillaDisponibles[j]= "x";
			}
    	}

    	ArrayList<Integer> jornadasAnterioresAunNoCargadas_div1= getJornadasAnterioresAunNoCargadas(ctx, ultimaJornadaCargada_div1, Equipo.DIVISION_1);
    	for (Integer _jor : jornadasAnterioresAunNoCargadas_div1)
    	{
    		int jor= _jor.intValue();
    		if (plantillaDisponibles[jor] == null)
    			jornadasACargar_div1.add(new Integer(jor));
    	}	    	
    	
    	if (ultimaJornadaCargada_div1 < ultimaJornadaDisponible_div1)
    	{
	    	for (int k= ultimaJornadaCargada_div1+1; k<= ultimaJornadaDisponible_div1; k++)
	    	{
	    		if (plantillaDisponibles[k] == null)
	    			jornadasACargar_div1.add(new Integer(k));
	    	}
    	}
    	
    	
        /*
         * Segunda división
         */
    	int ultimaJornadaCargada_div2= getUltimaJornadaCargada(ctx, Equipo.DIVISION_2);
    	int ultimaJornadaDisponible_div2= getUltimaJornadaDisponible(ctx, Equipo.DIVISION_2);

    	ArrayList<Integer> jornadasACargar_div2= new ArrayList<Integer>();

    	plantillaDisponibles= new String[ultimaJornadaDisponible_div2+1];
    	for (int k= 1; k<= ultimaJornadaDisponible_div2; k++)
    		plantillaDisponibles[k]= null;

    	String jornadasNoDisponibles_div2= Notificaciones.getNotificacion(Constantes.NOTIFICACION_JORNADAS_NODISPONIBLES_DIV2);
    	if (jornadasNoDisponibles_div2 != null)
    	{
	    	String[] jorNoDisp= jornadasNoDisponibles_div2.split(",");
	    	for (String jor : jorNoDisp) 
	    	{
	    		int j= new Integer(jor.trim()).intValue();
	    		plantillaDisponibles[j]= "x";
			}
    	}

    	ArrayList<Integer> jornadasAnterioresAunNoCargadas_div2= getJornadasAnterioresAunNoCargadas(ctx, ultimaJornadaCargada_div2, Equipo.DIVISION_2);
    	for (Integer _jor : jornadasAnterioresAunNoCargadas_div2)
    	{
    		int jor= _jor.intValue();
    		if (plantillaDisponibles[jor] == null)
    			jornadasACargar_div2.add(new Integer(jor));
    	}	    	
    	
    	if (ultimaJornadaCargada_div2 < ultimaJornadaDisponible_div2)
    	{
	    	for (int k= ultimaJornadaCargada_div2+1; k<= ultimaJornadaDisponible_div2; k++)
	    	{
	    		if (plantillaDisponibles[k] == null)
	    			jornadasACargar_div2.add(new Integer(k));
	    	}
    	}
    	
    	
    	
    	
    	
    	
    	int numJornadasACargar_div1= jornadasACargar_div1.size();
    	int numJornadasACargar_div2= jornadasACargar_div2.size();
    	int numJornadasACargar= numJornadasACargar_div1 + numJornadasACargar_div2;
    	int contador= 0;
    	for (int m1= 0; m1< numJornadasACargar_div1; m1++)
    	{
    		contador++;
    		int k= jornadasACargar_div1.get(m1);
    		Jornada jorDiv1= getJornada(Preferencias.getTemporadaActual(ctx), Equipo.DIVISION_1, k);

    		if ( jorDiv1 != null 
    				&& EquipoOp.existenEquipos(jorDiv1.getTemporada(), jorDiv1.getDivision(), jorDiv1.getPartidos(), ctx, eqDao))
    		{    		
	    		if (jorDiv1!=null  &&  !jorDao.estaGrabadaJornada(jorDiv1))
	    			jorDao.grabarJornada(jorDiv1);
	
	        	if (jorDiv1!=null)
	        	{
	        		if (ok == null)
	        			ok= new StringBuffer().append(" (div1) "+k);
	        		else
	        			ok.append(", ").append(k);
	        	}
    		}
    		else
    		{
    			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
    				Log.w(Constantes.LOG_TAG, "No se ha podido cargar la jornada "+k+" de primera división");
    		}

    		task.publicarProgresoEnRango(prcMin, prcMax, (float) (contador*100.0/numJornadasACargar));
    	}
    	boolean esPrimerLogJornadaDeDiv2= true;
    	for (int m2= 0; m2< numJornadasACargar_div2; m2++)
    	{
    		contador++;
    		int k= jornadasACargar_div2.get(m2);
    		Jornada jorDiv2= getJornada(Preferencias.getTemporadaActual(ctx), Equipo.DIVISION_2, k);

    		if ( jorDiv2 != null 
    				&& EquipoOp.existenEquipos(jorDiv2.getTemporada(), jorDiv2.getDivision(), jorDiv2.getPartidos(), ctx, eqDao))
    		{    		
	    		if (jorDiv2!=null  &&  !jorDao.estaGrabadaJornada(jorDiv2))
	    			jorDao.grabarJornada(jorDiv2);
	
	        	if (jorDiv2!=null)
	        	{
	        		if (ok == null)
	        			ok= new StringBuffer().append(" (div2) "+k);
	        		else
	        		{
	        			if (esPrimerLogJornadaDeDiv2)
	        				ok.append(" (div2) ").append(k);
	        			else
	        				ok.append(", ").append(k);
	        		}
	        		esPrimerLogJornadaDeDiv2= false;
	        	}
    		}
    		else
    		{
    			if (Log.isLoggable(Constantes.LOG_TAG, Log.WARN))
    				Log.w(Constantes.LOG_TAG, "No se ha podido cargar la jornada "+k+" de segunda división");
    		}

    		task.publicarProgresoEnRango(prcMin, prcMax, (float) (contador*100.0/numJornadasACargar));
    	}

		if (Log.isLoggable(Constantes.LOG_TAG, Log.INFO))
			Log.i(Constantes.LOG_TAG, (ok==null?"":"Se han cargado las jornadas: "+ok.toString()) );
		
		if (ok != null)
			Notificaciones.notificarDescarga(ctx, "jornadas: "+ok.toString());

    	task.publicarProgreso(prcMax);
		
    	return true;  // no quiero que el usuario se entere de posibles errores
    }
        
    private static void cargarFicheroDePartidosSuspendidos_o_Corregidos(String fich, Context ctx, SQLiteDatabase con)
    {
        JornadaDao jorDao= new JornadaDao(con);

		try
		{
			String datosFichero = CargaFichero.getContenidoFicheroJornadaPartidosSuspendidos_o_Corregidos(fich+".json");		
				
	    	JSONObject jObject = new JSONObject(datosFichero);
			int temp = jObject.getInt("temporada");
			JSONArray partidos = jObject.getJSONArray("partidos");
			for (int k= 0; k< partidos.length(); k++) 
			{
				JSONObject partido= partidos.getJSONObject(k);
				int division= partido.getInt("division");
				int jornada= partido.getInt("jornada_num");
				JSONObject datosPartido= partido.getJSONObject("partido");
				String localNombre= datosPartido.getString("local_nombre");
				int localGoles= datosPartido.getInt("local_goles");
				String visitNombre= datosPartido.getString("visit_nombre");
				int visitGoles= datosPartido.getInt("visit_goles");
				
				Partido par= new Partido();
				par.setTemporada(temp);
				par.setDivision(division);
				par.setJornada(jornada);
				par.setIdEquipoLocal(localNombre);
				par.setLocalGoles(localGoles);
				par.setIdEquipoVisit(visitNombre);
				par.setVisitGoles(visitGoles);
				jorDao.modificarOAnadirPartido(par);
			}						
		}
		catch(JSONException e)
		{
			Log.e(Constantes.LOG_TAG, "Error leyendo fichero de partidos suspendidos o corregidos: "+fich, e);
		}
	}   
    
    
    public static int getUltimaJornadaCargada(Context ctx, int division)
    {
    	// se mirará en BB.DD.
    	int ultima = 0;
    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
        JornadaDao jorDao= new JornadaDao(con);
        ultima= jorDao.getUltimaJornadaCargada(Preferencias.getTemporadaActual(ctx), division);
        con.close();
    	return ultima;
    }

    private static ArrayList<Integer> getJornadasAnterioresAunNoCargadas(Context ctx, int ultimaJornadaCargada, int division)
    {
    	// se mirará en BB.DD.
    	ArrayList<Integer> ret= new ArrayList<Integer>();
	    ArrayList<Integer> jornadasEnBBDD= new ArrayList<Integer>();

	    SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
        JornadaDao jorDao= new JornadaDao(con);
        jornadasEnBBDD= jorDao.getJornadasAnterioresCargadas(Preferencias.getTemporadaActual(ctx), ultimaJornadaCargada, division);
        con.close();
    	
		for (int k=1; k<= ultimaJornadaCargada; k++)
		{
			if (!Utils.estaElementoEnArray(k, jornadasEnBBDD))
				ret.add(new Integer(k));
		}
		return ret;
    }

    private static int getNumJornadasCargadas(Context ctx, int division)
    {
    	// se mirará en BB.DD.
    	int ret= 0;
	    SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
        JornadaDao jorDao= new JornadaDao(con);
        ret= jorDao.getNumJornadasCargadas(Preferencias.getTemporadaActual(ctx), division);
        con.close();    	
		return ret;
    }
    
    private static int getUltimaJornadaDisponible(Context ctx, int division)
    {
    	String notifUltJorDisp= Notificaciones.getNotificacion(division==Equipo.DIVISION_1 ? Constantes.NOTIFICACION_JORNADA_DIV1 : Constantes.NOTIFICACION_JORNADA_DIV2);
    	if (notifUltJorDisp == null)
    		return 0;
    	else
    		return new Integer(notifUltJorDisp.trim()).intValue();
    }
 
	public static Jornada getJornada(int temporada, int _division, int _jornada) 
	{
		try
		{
	    	String fich= "temp"+temporada+"_div"+_division+"_jor"+_jornada+".json";
			String datosJornada = CargaFichero.getContenidoFicheroJornada(fich);		
			if (datosJornada == null  ||  datosJornada.length()==0)  // tendrá longitud 0 si no existe en servidor
				return null;
				
	    	JSONObject jObject = new JSONObject(datosJornada);
			JSONObject jornada = jObject.getJSONObject("jornada");
			int temp = jornada.getInt("temporada");
			int div = jornada.getInt("division");
			int jornadaNum = jornada.getInt("jornada_num");
			if (temp!=temporada || div!=_division || jornadaNum!=_jornada)
			{
				Log.e(Constantes.LOG_TAG, "El fichero de la temporada "+temporada+", jornada "+_jornada+", división "+_division+" indica en sus datos que la temporada es "+temp+", la jornada es "+jornadaNum+" y la división "+div);
				return null;
			}
	
			Jornada jor= new Jornada(temp, div, jornadaNum);
			JSONArray partidos = jornada.getJSONArray("partidos");
			for (int k= 0; k< partidos.length(); k++) 
			{
				Partido par= new Partido();
				JSONArray partido= partidos.getJSONArray(k);  //  {"spo": 1}, {"rso": 2}
				JSONObject local= partido.getJSONObject(0);  // {"spo": 1}
				JSONObject visit= partido.getJSONObject(1);  // {"rso": 2}
				String localNombre= local.names().get(0).toString();
				par.setIdEquipoLocal(localNombre);
				par.setLocalGoles(local.getInt(localNombre));
				String visitNombre= visit.names().get(0).toString();
				par.setIdEquipoVisit(visitNombre);
				par.setVisitGoles(visit.getInt(visitNombre));
				jor.anadirPartido(par);
			}
						
			return jor;
		}
		catch(JSONException e)
		{
			Log.e(Constantes.LOG_TAG, "Error leyendo fichero de jornada "+_jornada + ", división "+_division, e);
			return null;
		}
	}   
	
    public static int getUltimaTemporadaCargada(Context ctx)
    {
    	// se mirará en BB.DD.
    	int ultima = 0;
    	SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
        JornadaDao jorDao= new JornadaDao(con);
        ultima= jorDao.getUltimaTemporadaCargada();
        con.close();
    	return ultima;
    }
	
}
