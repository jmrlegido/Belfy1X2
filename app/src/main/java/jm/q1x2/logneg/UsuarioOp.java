package jm.q1x2.logneg;

import java.util.ArrayList;

import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.bbdd.dao.UsuarioDao;
import jm.q1x2.transobj.Partido;
import jm.q1x2.transobj.Usuario;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Preferencias;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UsuarioOp 
{
	public static String USUARIO_DEMO_NOMBRE= "demo";

	public static int VALORDEF_ALEATORIEDAD= 40;
	public static int VALORDEF_CALIDADINTRINSECA= 80;
	public static int VALORDEF_FACTORCAMPO= 60;
	public static int VALORDEF_GOLAVERAJE= 50;
	public static int VALORDEF_GOLAVERAJE_LOCALVISIT= 45;
	public static int VALORDEF_PUNTOSPARTIDO= 60;
	public static int VALORDEF_PUNTOSPARTIDO_LOCALVISIT= 65;
	public static int VALORDEF_ULTIMOS4= 70;
	public static int VALORDEF_ULTIMOS4_LOCALVISIT= 55;
	
	
	public Usuario getUsuarioDemo(Context ctx)
	{
        SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.LECTURA);
        UsuarioDao usuDao= new UsuarioDao(con);
        Usuario ret= usuDao.getUsuarioDemo();
        Basedatos.cerrarConexion(con);  //[jm] con.close();
        return ret;
	}

	public Usuario crearUsuarioDemo(Context ctx)
	{
        SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.ESCRITURA);
        UsuarioDao usuDao= new UsuarioDao(con);
        Usuario ret= usuDao.crearUsuarioDemo();
        Basedatos.cerrarConexion(con);  //[jm] con.close();
        return ret;
	}

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

	public static void crearRegistrosPrefUsuarioSiNoExisten(Context ctx)
	{
		int idUsu= Preferencias.getUsuarioId(ctx);
		int idTemp= Preferencias.getTemporadaActual(ctx);
		
        SQLiteDatabase con= Basedatos.getConexion(ctx, Basedatos.ESCRITURA);
        UsuarioDao usuDao= new UsuarioDao(con);
        if (!usuDao.existenDatosDePrefDeEsteUsuarioEnEstaTemporada(idUsu, idTemp))
        	usuDao.rellenarTablaEquiposUsuariosConValoresPorDefecto(idUsu);
        Basedatos.cerrarConexion(con);  //[jm] con.close();
		
	}
}
