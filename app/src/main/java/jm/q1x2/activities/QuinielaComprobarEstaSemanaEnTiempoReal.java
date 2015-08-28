package jm.q1x2.activities;

import java.util.ArrayList;

import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.transobj.Quiniela;
import android.database.sqlite.SQLiteDatabase;

public class QuinielaComprobarEstaSemanaEnTiempoReal extends QuinielaVerResultadosTodas
{
    @Override
    protected ArrayList<Quiniela> getQuinielas(int temporada, SQLiteDatabase con)
    {
		QuinielaOp qOp= new QuinielaOp();
		ArrayList<Quiniela> ret= qOp.getQuinielasDeEstaSemanaSinCorregir(getApplicationContext(), temporada);
		
		return ret;
    }
 
    @Override
    protected boolean presentarQuinielasEnTiempoReal_SiHay()
    {
    	return true;
    }
    
}
