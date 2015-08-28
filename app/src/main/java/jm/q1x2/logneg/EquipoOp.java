package jm.q1x2.logneg;

import java.util.ArrayList;

import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.transobj.Partido;
import jm.q1x2.utils.Constantes;
import android.content.Context;
import android.util.Log;

public class EquipoOp 
{
	public static final int EQUIPO_LOCAL_Y_VISITANTE= 1;
	public static final int EQUIPO_LOCAL= 2;
	public static final int EQUIPO_VISITANTE= 3;
	
	/*
	 * Valores posibles de 'division': 1 (primera div.), 2 (segunda div.), 0 (ambas)
	 */
    public static boolean existenEquipos(int temporada, int division, ArrayList<Partido> partidos, Context ctx, EquipoDao eqDao)
    {
    	boolean ret= true;
    	for (Partido par : partidos) 
    	{
    		if (!eqDao.existeEquipo(temporada, division, par.getIdEquipoLocal())  
    			|| !eqDao.existeEquipo(temporada, division, par.getIdEquipoVisit())	)
    		{
    			Log.e(Constantes.LOG_TAG, "En la temporada "+temporada+", división "+division+" se ha indicado un código de equipo erróneo: ["+par.getIdEquipoLocal()+"]  o  ["+par.getIdEquipoVisit()+"]");
    			return false;
    		}
		}
    	return ret;
    }
	
}
