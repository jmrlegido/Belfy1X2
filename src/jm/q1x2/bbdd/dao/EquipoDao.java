package jm.q1x2.bbdd.dao;


import java.math.BigDecimal;
import java.util.ArrayList;

import jm.q1x2.logneg.EquipoOp;
import jm.q1x2.transobj.Equipo;
import jm.q1x2.transobj.EstadisticasEquipo;
import jm.q1x2.transobj.Partido;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EquipoDao extends DaoGeneral
{
   public EquipoDao(SQLiteDatabase con)
   {
	   super(con);
   }

   public ArrayList<Equipo> getEquipos(int temporada)
   {
	   ArrayList<Equipo> ret= new ArrayList<Equipo>();
       String sql= "SELECT e.id, e.nombre " +
  				" FROM equipos e, equipos_temporada et " +
  				" WHERE et.id_temporada= " + temporada +
  					" and et.id_equipo=e.id" ;
       
       Cursor c = con.rawQuery(sql, null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
       {
            do 
            {
            	 Equipo eq= new Equipo(c.getString(0), c.getString(1));
                 ret.add(eq);
            } 
            while(c.moveToNext());
       }                       
       c.close();
       return ret;
   }
   
   public ArrayList<Equipo> getEquipos(int temporada, int div, int idUsuario)
   {
	   return getEquipos(temporada, div, idUsuario, Equipo.ORDEN_NOMBRE);
   }
   
   public ArrayList<Equipo> getEquipos(int temporada, int div, int idUsuario, int orden)
   {
	   ArrayList<Equipo> ret= new ArrayList<Equipo>();
       String sql= "SELECT e.id, e.nombre, eu.calidad_intrinseca, et.division " +
  				" FROM equipos e, equipos_temporada et, equipos_usuarios eu " +
  				" WHERE et.id_temporada= " + temporada +
  					" and eu.id_temporada= " + temporada +
  					" and et.id_equipo=e.id" +
  					" and eu.id_equipo=e.id" +
  					" and eu.id_usuario=" + idUsuario +
  				    (div == Equipo.DIVISION_1_Y_2 ? "" : " and et.division="+div) ;
       if (orden==Equipo.ORDEN_NOMBRE)
    	   sql+= " order by e.nombre";
       else if (orden==Equipo.ORDEN_CALIDAD_INTRINSECA)
    	   sql+= " order by eu.calidad_intrinseca desc";
       
       Cursor c = con.rawQuery(sql, null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
       {
            do 
            {
            	 Equipo eq= new Equipo(c.getString(0), c.getString(1));
            	 eq.setCalidadIntrinseca(c.getInt(2));
            	 eq.setDivision(c.getInt(3));
                 ret.add(eq);
            } 
            while(c.moveToNext());
       }                       
       c.close();
       return ret;
   }

   public Equipo getEquipo(String idEq, int idTemporada, int idUsuario)
   {
	   Equipo ret= null;
       String sql= "SELECT e.nombre, eu.calidad_intrinseca "+
       			   " from equipos e, equipos_usuarios eu " +
       			   " where e.id=eu.id_equipo " +
       			   		" and e.id='"+idEq+"'" +
       			   		" and eu.id_temporada="+idTemporada +    	   
       					" and eu.id_usuario="+idUsuario;    	   
       
       Cursor c = con.rawQuery(sql, null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
       {
    	   ret= new Equipo(idEq, c.getString(0));
    	   ret.setCalidadIntrinseca(c.getInt(1));
       }
       c.close();
       return ret;
	   
   }

   public String getNombreEquipo(String id)
   {
	   String ret= null;
       String sql= "SELECT nombre from equipos where id='"+id+"'";    	   
       
       Cursor c = con.rawQuery(sql, null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
    	   ret= c.getString(0);
       c.close();
       return ret;
	   
   }
   
   public boolean existeEquipo(int temporada, int div, String codEquipo)
   {
	   boolean ret= false;
       String sql= "SELECT count(et.id_equipo) " +
  				" FROM equipos_temporada et " +
  				" WHERE et.id_temporada= " + temporada +
  					" and et.id_equipo='"+codEquipo+"'" +
  				    (div == Equipo.DIVISION_1_Y_2 ? "" : " and et.division="+div) ;    	   
       
       Cursor c = con.rawQuery(sql, null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
    	   ret= (c.getInt(0) > 0);
       c.close();
       return ret;
   }
   
   public void actualizarCalidadIntrinseca(int temporada, String idEquipo, int idUsuario, int cal)
   {
	   con.execSQL("update equipos_usuarios set calidad_intrinseca="+cal+" where id_equipo='"+idEquipo+"' and id_usuario="+idUsuario+" and id_temporada="+temporada);
   }
   
   public ArrayList<Partido> getPartidos(int temporada, String idEquipo)
   {
	   ArrayList<Partido> ret= new ArrayList<Partido>();
       String sql= "SELECT jornada_num, id_local, id_visitante, goles_local, goles_visitante " +
			" FROM jornadas " +
			" WHERE id_temporada= " + temporada +
				" and (id_local='"+idEquipo+"' or id_visitante='"+idEquipo+"') " +
			" order by jornada_num";    	   

		Cursor c = con.rawQuery(sql, null);          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
		{
		   do 
		   {
			   Partido p= new Partido();
			   p.setTemporada(temporada);
			   p.setJornada(c.getInt(0));
			   p.setIdEquipoLocal(c.getString(1));
			   p.setIdEquipoVisit(c.getString(2));
			   p.setNombreEquipoLocal(getNombreEquipo(c.getString(1)));
			   p.setNombreEquipoVisit(getNombreEquipo(c.getString(2)));
			   p.setLocalGoles(c.getInt(3));
			   p.setVisitGoles(c.getInt(4));
			   
			   ret.add(p);
		   } 
		   while(c.moveToNext());
		}                       
		c.close();
	   return ret;
   }
   
   public EstadisticasEquipo getEstadisticas(int temporada, String idEquipo)
   {
	   EstadisticasEquipo e= new EstadisticasEquipo();

	   int numPartidosLocal= getNumPartidos(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL);
	   int numPartidosVisit= getNumPartidos(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
	   int numPartidosTotal= numPartidosLocal + numPartidosVisit;
	   
	   e.setPtosPorPartido(getPuntosPorPartido(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE, numPartidosTotal));
	   e.setPtosPorPartidoLocal(getPuntosPorPartido(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL, numPartidosLocal));
	   e.setPtosPorPartidoVisit(getPuntosPorPartido(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE, numPartidosVisit));

	   int ganLocal= getNumPartidosGanados(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL);
	   int empLocal= getNumPartidosEmpatados(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL);
	   int perLocal= getNumPartidosPerdidos(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL);
	   int ganVisit= getNumPartidosGanados(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
	   int empVisit= getNumPartidosEmpatados(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
	   int perVisit= getNumPartidosPerdidos(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
	   
	   e.setPartidosGanados(ganLocal+ganVisit);
	   e.setPartidosEmpatados(empLocal+empVisit);
	   e.setPartidosPerdidos(perLocal+perVisit);
	   e.setPartidosJugados(ganLocal+ganVisit+empLocal+empVisit+perLocal+perVisit);

	   e.setPartidosLocalGanados(ganLocal);
	   e.setPartidosLocalEmpatados(empLocal);
	   e.setPartidosLocalPerdidos(perLocal);

	   e.setPartidosVisitanteGanados(ganVisit);
	   e.setPartidosVisitanteEmpatados(empVisit);
	   e.setPartidosVisitantePerdidos(perVisit);
	   
	   e.setGolesPorPartido(getGolesPorPartido(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL_Y_VISITANTE, numPartidosTotal));
	   e.setGolesPorPartidoLocal(getGolesPorPartido(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL, numPartidosLocal));
	   e.setGolesPorPartidoVisit(getGolesPorPartido(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE, numPartidosVisit));

	   return e;
   }
   
   
   /*
    * el parámetro totalLocalVisit debe tener por valor una de las 3 ctes: EQUIPO_LOCAL_Y_VISITANTE, EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public BigDecimal getPuntosPorPartido(int temporada, String idEquipo, int totalLocalVisit, int partidos)
   {
	   if (partidos == 0)
		   return new BigDecimal(0);
	   else
	   {	   
		   int ptos= 0;
		   if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL_Y_VISITANTE)
			   ptos= getPuntos(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL) + getPuntos(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
		   else if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL)
			   ptos= getPuntos(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL);
		   else if (totalLocalVisit == EquipoOp.EQUIPO_VISITANTE)
			   ptos= getPuntos(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
	       return new BigDecimal(ptos*1.0/partidos).setScale(1, BigDecimal.ROUND_HALF_UP);
	   }
   }
   
   /*
    * el parámetro localVisit debe tener por valor una de las ctes: EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getPuntos(int temporada, String idEquipo, int localVisit)
   {
	   int ret= 0;
       String sql= "SELECT goles_local, goles_visitante " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada +
  					(localVisit==EquipoOp.EQUIPO_LOCAL ? " and id_local='"+idEquipo+"' " : " and id_visitante='"+idEquipo+"' ");
       
       Cursor c = con.rawQuery(sql, null);
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
		{
		   do 
		   {
			   int gl= c.getInt(0);
			   int gv= c.getInt(1);
			   if (gl == gv)
				   ret+= 1;
			   else if (localVisit == EquipoOp.EQUIPO_LOCAL  &&  gl > gv)
	   			   ret+= 3;
			   else if (localVisit == EquipoOp.EQUIPO_VISITANTE  &&  gl < gv)
	   			   ret+= 3;
		   } 
		   while(c.moveToNext());
		}                       
       c.close();
       return ret;
   }

   /*
    * el parámetro localVisit debe tener por valor una de las ctes:  EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getNumPartidos(int temporada, String idEquipo, int localVisit)
   {
	   int ret= 0;
       String sql= "SELECT count(id) " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada +
  					(localVisit==EquipoOp.EQUIPO_LOCAL ? " and id_local='"+idEquipo+"' " : " and id_visitante='"+idEquipo+"' ");
       
       Cursor c = con.rawQuery(sql, null);
       if (c.moveToFirst())
		   ret= c.getInt(0);
       c.close();
       return ret;
   }

   /*
    * el parámetro localVisit debe tener por valor una de las ctes: EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getNumPartidosGanados(int temporada, String idEquipo, int localVisit)
   {
	   int ret= 0;
       String sql= "SELECT count(id) " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada +
  					(localVisit==EquipoOp.EQUIPO_LOCAL ? " and id_local='"+idEquipo+"' " : " and id_visitante='"+idEquipo+"' ") +
  					" and goles_local "+(localVisit==EquipoOp.EQUIPO_LOCAL ? ">":"<")+" goles_visitante ";
       
       Cursor c = con.rawQuery(sql, null);
       if (c.moveToFirst())
		   ret= c.getInt(0);
       c.close();
       return ret;
   }

   /*
    * el parámetro localVisit debe tener por valor una de las ctes:  EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getNumPartidosEmpatados(int temporada, String idEquipo, int localVisit)
   {
	   int ret= 0;
       String sql= "SELECT count(id) " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada +
  					(localVisit==EquipoOp.EQUIPO_LOCAL ? " and id_local='"+idEquipo+"' " : " and id_visitante='"+idEquipo+"' ") +
  					" and goles_local = goles_visitante ";
       
       Cursor c = con.rawQuery(sql, null);
       if (c.moveToFirst())
		   ret= c.getInt(0);
       c.close();
       return ret;
   }

   /*
    * el parámetro localVisit debe tener por valor una de las ctes: EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getNumPartidosPerdidos(int temporada, String idEquipo, int localVisit)
   {
	   int ret= 0;
       String sql= "SELECT count(id) " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada +
  					(localVisit==EquipoOp.EQUIPO_LOCAL ? " and id_local='"+idEquipo+"' " : " and id_visitante='"+idEquipo+"' ") +
  					" and goles_local "+(localVisit==EquipoOp.EQUIPO_LOCAL ? "<":">")+" goles_visitante ";
       
       Cursor c = con.rawQuery(sql, null);
       if (c.moveToFirst())
		   ret= c.getInt(0);
       c.close();
       return ret;
   }

   /*
    * el parámetro totalLocalVisit debe tener por valor una de las 3 ctes: EQUIPO_LOCAL_Y_VISITANTE, EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public BigDecimal getGolaverajePorPartido(int temporada, String idEquipo, int totalLocalVisit, int partidos)
   {
	   if (partidos == 0)
		   return new BigDecimal(0);
	   else
	   {
		   int golaveraje= 0;
		   if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL_Y_VISITANTE)
			   golaveraje= getGolesAFavor(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL) + getGolesAFavor(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE) - getGolesEnContra(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL) - getGolesEnContra(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
		   else if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL)
			   golaveraje= getGolesAFavor(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL) - getGolesEnContra(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL);
		   else if (totalLocalVisit == EquipoOp.EQUIPO_VISITANTE)
			   golaveraje= getGolesAFavor(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE) - getGolesEnContra(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
		   return new BigDecimal(golaveraje*1.0/partidos).setScale(1, BigDecimal.ROUND_HALF_UP);
	   }
   }
   
   /*
    * el parámetro totalLocalVisit debe tener por valor una de las 3 ctes: EQUIPO_LOCAL_Y_VISITANTE, EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public BigDecimal getGolesPorPartido(int temporada, String idEquipo, int totalLocalVisit, int partidos)
   {
	   if (partidos == 0)
		   return new BigDecimal(0);
	   else
	   {
		   int goles= 0;
		   if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL_Y_VISITANTE)
			   goles= getGoles(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL) + getPuntos(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
		   else if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL)
			   goles= getGoles(temporada, idEquipo, EquipoOp.EQUIPO_LOCAL);
		   else if (totalLocalVisit == EquipoOp.EQUIPO_VISITANTE)
			   goles= getGoles(temporada, idEquipo, EquipoOp.EQUIPO_VISITANTE);
		   return new BigDecimal(goles*1.0/partidos).setScale(1, BigDecimal.ROUND_HALF_UP);
	   }
   }
   
   /*
    * el parámetro localVisit debe tener por valor una de las ctes:  EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getGoles(int temporada, String idEquipo, int localVisit)
   {
	   return getGolesAFavor(temporada, idEquipo, localVisit);
   }

   /*
    * el parámetro localVisit debe tener por valor una de las ctes:  EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getGolesAFavor(int temporada, String idEquipo, int localVisit)
   {
	   int ret= 0;
       String sql= "SELECT sum(" + (localVisit==EquipoOp.EQUIPO_LOCAL ? "goles_local" : "goles_visitante") + ") " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada +
  					(localVisit==EquipoOp.EQUIPO_LOCAL ? " and id_local='"+idEquipo+"' " : " and id_visitante='"+idEquipo+"' ");
       
       Cursor c = con.rawQuery(sql, null);
	   if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
			ret= c.getInt(0);
       c.close();
       return ret;
   }

   /*
    * el parámetro localVisit debe tener por valor una de las ctes:  EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getGolesEnContra(int temporada, String idEquipo, int localVisit)
   {
	   int ret= 0;
       String sql= "SELECT sum(" + (localVisit==EquipoOp.EQUIPO_LOCAL ? "goles_visitante" : "goles_local") + ") " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada +
  					(localVisit==EquipoOp.EQUIPO_LOCAL ? " and id_local='"+idEquipo+"' " : " and id_visitante='"+idEquipo+"' ");
       
       Cursor c = con.rawQuery(sql, null);
	   if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
			ret= c.getInt(0);
       c.close();
       return ret;
   }
   
   /*
    * el parámetro totalLocalVisit debe tener por valor una de las ctes: EQUIPO_LOCAL_Y_VISITANTE, EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getPuntosUltimos4Partidos(int temporada, String idEquipo, int totalLocalVisit)
   {
	   int ret= 0;
       String sql= "SELECT id_local, goles_local, goles_visitante " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada ;
       if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL_Y_VISITANTE)
    	   sql+= " and (id_local='"+idEquipo+"' or id_visitante='"+idEquipo+"')" ;
       else if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL)
    	   sql+= " and (id_local='"+idEquipo+"' )" ;
       else if (totalLocalVisit == EquipoOp.EQUIPO_VISITANTE)
    	   sql+= " and (id_visitante='"+idEquipo+"')" ;
  	   sql+= " ORDER BY jornada_num desc";
       
       Cursor c = con.rawQuery(sql, null);
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
		{
		   int cont= 1;
		   do 
		   {
			   boolean bEsLocal= false;
			   String idLocal= c.getString(0);
			   int gl= c.getInt(1);
			   int gv= c.getInt(2);
			   
			   if (idEquipo.equals(idLocal))
				   bEsLocal= true;
			   
			   if (gl == gv)
				   ret+= 1;
			   else if (bEsLocal  &&  gl > gv)
	   			   ret+= 3;
			   else if (!bEsLocal  &&  gl < gv)
	   			   ret+= 3;
			   cont++;
		   } 
		   while(c.moveToNext() && cont <= 4);
		}                       
       c.close();
       return ret;
   }
      
   /*
    * el parámetro totalLocalVisit debe tener por valor una de las ctes: EQUIPO_LOCAL_Y_VISITANTE, EQUIPO_LOCAL, EQUIPO_VISITANTE
    */
   public int getPartidosJugadosUltimasXJornadas(int temporada, String idEquipo, int numJornadas, int totalLocalVisit)
   {
	   int ret= 0;
       String sql= "SELECT id " +
  				" FROM jornadas " +
  				" WHERE id_temporada= " + temporada ;
       if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL_Y_VISITANTE)
    	   sql+= " and (id_local='"+idEquipo+"' or id_visitante='"+idEquipo+"')" ;
       else if (totalLocalVisit == EquipoOp.EQUIPO_LOCAL)
    	   sql+= " and (id_local='"+idEquipo+"' )" ;
       else if (totalLocalVisit == EquipoOp.EQUIPO_VISITANTE)
    	   sql+= " and (id_visitante='"+idEquipo+"')" ;
  	   sql+= " ORDER BY jornada_num desc";
       
       Cursor c = con.rawQuery(sql, null);
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
		{
		   int cont= 1;
		   do 
		   {
			   ret++;
			   cont++;
		   } 
		   while(c.moveToNext() && cont <= 4);
		}                       
       c.close();
       return ret;
   }
   
   public void anadirEquipoUsuario(String idEquipo, int idUsuario, int temporada, int calidadIntrinseca)   
   {
	   ContentValues val = new ContentValues();	   
	   val.put("id_equipo", idEquipo);
	   val.put("id_usuario", idUsuario);
	   val.put("id_temporada", temporada);
	   val.put("calidad_intrinseca", calidadIntrinseca); 
       con.insert("equipos_usuarios", null, val);
   }

   public int getValorDefectoCalidadIntrinsecaEquipo(String idEquipo, int idTemporada)
   {
	   int ret= -1;
       String sql= "SELECT calidad_intrinseca " +
  				" FROM valoresdefecto_equipos_calidadintrinseca " +
  				" WHERE id_temporada= " + idTemporada + " and id_equipo='" + idEquipo + "'";
       
       Cursor c = con.rawQuery(sql, null);
       if (c.moveToFirst())
		   ret= c.getInt(0);
       c.close();
       return ret;
   }

}
