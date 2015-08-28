package jm.q1x2.activities;


import java.util.ArrayList;

import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.transobj.Quiniela;
import jm.q1x2.utils.Preferencias;
import android.database.sqlite.SQLiteDatabase;

public class QuinielaComprobarSemanaPasada extends QuinielaVerResultadosTodas
{
    @Override
    protected ArrayList<Quiniela> getQuinielas(int temporada, SQLiteDatabase con)
    {
		QuinielaOp qOp= new QuinielaOp();
		int ultimaJornadaQuinielaCorregida= QuinielaOp.getUltimaJornadaQuinielaCorregidaCargada(con, temporada);		
		return qOp.getQuinielasHechasJornada(temporada, ultimaJornadaQuinielaCorregida, Preferencias.getUsuarioId(getApplicationContext()), con);		
    }
    
    @Override
    protected boolean presentarQuinielasEnTiempoReal_SiHay()
    {
    	return false;
    }
    
}
