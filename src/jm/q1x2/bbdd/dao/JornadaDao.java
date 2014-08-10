package jm.q1x2.bbdd.dao;


import java.util.ArrayList;

import jm.q1x2.transobj.Jornada;
import jm.q1x2.transobj.Partido;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class JornadaDao extends DaoGeneral
{
   public JornadaDao(SQLiteDatabase con)
   {
	   super(con);
   }
	
   public boolean estaGrabadaJornada(Jornada jor)
   {
	   boolean ret= false;
       Cursor c = con.rawQuery("SELECT count(id) FROM jornadas where id_temporada=? and division=? and jornada_num=?", 
       										new String[]{""+jor.getTemporada(), ""+jor.getDivision(), ""+jor.getJornadaNum()});          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
    	   ret= (c.getInt(0) > 0);
       c.close();
                              
       return ret;	   
   }
   
   public void grabarJornada(Jornada jor)
   {
	   /*
	    * CREATE TABLE jornadas (
	    * 	id integer PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 
	    * 	id_temporada integer NOT NULL , 
	    * 	division integer NOT NULL ,
	    * 	jornada_num integer NOT NULL , 
	    * 	id_local varchar NOT NULL , 
	    * 	id_visitante varchar NOT NULL , 
	    * 	goles_local integer NOT NULL  DEFAULT 0, 
	    * 	goles_visitante integer NOT NULL  DEFAULT 0);
	    */	   
	   ArrayList<Partido> partidos= jor.getPartidos();
	   ContentValues val= null;
	   for (Partido par : partidos) 
	   {
		   val = new ContentValues();	   
		   val.put("id_temporada", jor.getTemporada());
		   val.put("division", jor.getDivision());
		   val.put("jornada_num", jor.getJornadaNum());
		   val.put("id_local", par.getIdEquipoLocal());
		   val.put("id_visitante", par.getIdEquipoVisit());
		   val.put("goles_local", par.getLocalGoles());
		   val.put("goles_visitante", par.getVisitGoles());
	       con.insert("jornadas", null, val);
	   }	   
   }
   
   public int getUltimaJornadaCargada(int temporada, int division)
   {
	    int ret= 0;
        Cursor c = con.rawQuery("SELECT max(jornada_num) FROM jornadas where id_temporada=? and division=?", new String[]{""+temporada, ""+division});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
			ret= c.getInt(0);
		c.close();
		return ret;
   }

   public ArrayList<Integer> getJornadasAnterioresCargadas(int temporada, int ultimaJornadaCargada, int division)
   {
	    ArrayList<Integer> jornadasEnBBDD= new ArrayList<Integer>();
        Cursor c = con.rawQuery("SELECT jornada_num FROM jornadas where id_temporada=? and division=? and jornada_num<=? order by jornada_num ASC", new String[]{""+temporada, ""+division, ""+ultimaJornadaCargada});          
        if (c.moveToFirst()) 
        {
            do 
            {
                 jornadasEnBBDD.add(new Integer(c.getInt(0)));
            } 
            while(c.moveToNext());
        }                       
		c.close();
		return jornadasEnBBDD;
   }

   public int getNumJornadasCargadas(int temporada, int division)
   {
	    int ret= 0;
        Cursor c = con.rawQuery("SELECT count(distinct(jornada_num)) FROM jornadas where id_temporada=? and division=?", new String[]{""+temporada, ""+division});          
        if (c.moveToFirst()) 
            ret= c.getInt(0);
		c.close();
		return ret;
   }
   
   public int getUltimaTemporadaCargada()
   {
	    int ret= 0;
        Cursor c = con.rawQuery("SELECT max(id) FROM temporadas", null);          
		if (c.moveToFirst())  
			ret= c.getInt(0);
		c.close();
		return ret;
   }
   
   public void modificarOAnadirPartido(Partido par)
   {
	   // borro partido (si hubiera):
       con.execSQL("delete from jornadas where  id_temporada= " + par.getTemporada() +
       									  " and division=" + par.getDivision() +
       									  " and jornada_num=" + par.getJornada() +
       									  " and id_local='" + par.getIdEquipoLocal() +"'"+
       									  " and id_visitante='" + par.getIdEquipoVisit() +"'" );
	   	   
	   // añado partido:
       ContentValues val = new ContentValues();	   
	   val.put("id_temporada", par.getTemporada());
	   val.put("division", par.getDivision());
	   val.put("jornada_num", par.getJornada());
	   val.put("id_local", par.getIdEquipoLocal());
	   val.put("id_visitante", par.getIdEquipoVisit());
	   val.put("goles_local", par.getLocalGoles());
	   val.put("goles_visitante", par.getVisitGoles());
       con.insert("jornadas", null, val);       
   }
}
