package jm.q1x2.bbdd.dao;


import android.database.sqlite.SQLiteDatabase;

public class IncidenciaDao extends DaoGeneral
{
   public IncidenciaDao(SQLiteDatabase con)
   {
	   super(con);
   }
	
   public void registrarIncidencia(String txtIncidencia)   
   {
	   con.execSQL("INSERT INTO incidencias (cuando, texto, enviada) VALUES (datetime('now'), '"+txtIncidencia+"', 0) ");
   }
}
