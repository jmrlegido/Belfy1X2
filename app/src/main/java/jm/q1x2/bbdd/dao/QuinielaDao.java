package jm.q1x2.bbdd.dao;


import java.util.ArrayList;

import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.transobj.FactoresPesos;
import jm.q1x2.transobj.Partido;
import jm.q1x2.transobj.PatronFactoresPesos;
import jm.q1x2.transobj.Quiniela;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QuinielaDao extends DaoGeneral
{
   public QuinielaDao(SQLiteDatabase con)
   {
	   super(con);
   }
	   
   public void actualizarAciertos(int quinId, int aciertos)
   {
       con.execSQL("update quinielas set aciertos="+aciertos+" where id="+quinId);
   }
   
   /*
    * devolverá null si no existe ningún registro en la tabla  quinielas_disponibles_server  para esa jornada
    */
   public Quiniela getDefinicionQuinielaCorregidaJornada(int temporada, int jornada)
   {
	   // lo lee de BB.DD.
	   Quiniela quin= null;
       Cursor c = con.rawQuery("SELECT par.id_local, par.id_visitante, par.partido_num, par.resultado_1x2 " +
    		   				   " FROM quinielas_disponibles_server q, quinielas_partidos_disponibles_server par " +
    		   				   " WHERE par.id_quiniela=q.id" +
    		   				   		" and q.disponible_correccion = " + Quiniela.QUINIELA_CORREGIDA_SI +
    		   				   		" and q.id_temporada=? and q.fecha=?" +
    		   				   " ORDER BY par.partido_num", 
       					new String[]{""+temporada, ""+jornada});          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
       {
    	   quin= new Quiniela(temporada, jornada);
           do 
           {
        	   Partido par= new Partido(c.getString(0), c.getString(1));
        	   par.setPartidoNum(c.getInt(2));
        	   par.setResultadoQuiniela(QuinielaOp.signoQuinielaString2Int(c.getString(3)));
        	   quin.annadirPartido(par);
           } 
           while(c.moveToNext());
       }
       c.close();
                              
       return quin;	   
   }
   public Quiniela getDefinicionQuinielaJornada(int temporada, int jornada)
   {
	   Quiniela quin= null;
       Cursor c = con.rawQuery("SELECT par.id_local, par.id_visitante, par.partido_num " +
    		   				   " FROM quinielas_disponibles_server q, quinielas_partidos_disponibles_server par " +
    		   				   " WHERE par.id_quiniela=q.id" +
    		   				   		/*" and q.disponible_correccion = 1" +*/
    		   				   		" and q.id_temporada=? and q.fecha=?" +
    		   				   " ORDER BY par.partido_num", 
       					new String[]{""+temporada, ""+jornada});          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
       {
    	   quin= new Quiniela(temporada, jornada);
           do 
           {
        	   Partido par= new Partido(c.getString(0), c.getString(1));
        	   par.setPartidoNum(c.getInt(2));
        	   quin.annadirPartido(par);
           } 
           while(c.moveToNext());
       }
       c.close();
                              
       return quin;	   
   }
      
   public boolean estaGrabadaQuinielaCorregida(Quiniela q)
   {
	   boolean ret= false;
       Cursor c = con.rawQuery("SELECT count(id) FROM quinielas_disponibles_server where id_temporada=? and fecha=? and disponible_correccion=1", 
       										new String[]{""+q.getTemporada(), ""+q.getJornada()});          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
    	   ret= (c.getInt(0) > 0);
       c.close();
                              
       return ret;	   
   }
      
   public boolean existeQuinielaConNombre(String nombreQuin, int usuId, int jornada)
   {
	   boolean ret= false;
       Cursor c = con.rawQuery("SELECT count(q.id) "+
    		   					" FROM quinielas q, quinielas_usuarios qu "+
    		   					" where qu.id_quiniela=q.id "+
    		   						" and qu.id_usuario=?" +
    		   						" and q.fecha=?" +
    		   						" and q.nombre=?",
       						new String[]{""+usuId, ""+jornada, nombreQuin});          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
    	   ret= (c.getInt(0) > 0);
       c.close();
                              
       return ret;	   
   }
   
   private void borrarQuinielaDisponibleServer(int temporada, int jor)
   {
	    Cursor c = con.rawQuery("SELECT id " +
	    						" from quinielas_disponibles_server " +
	    						" where id_temporada=? and fecha=?",
					new String[]{""+temporada, ""+jor});
	    int id= -1;
		if (c.moveToFirst())
			id= c.getInt(0);
		c.close();
		
		if (id != -1)
		{
			con.execSQL("delete from quinielas_disponibles_server where id="+id);
			con.execSQL("delete from quinielas_partidos_disponibles_server where id_quiniela="+id);
		}
   }
   
   public void grabarQuinielaCorregida(Quiniela q)
   {
	   borrarQuinielaDisponibleServer(q.getTemporada(), q.getJornada());  // para borrar esta misma quiniela (pero teniendo disponible_correccion=0)
	   
	   ArrayList<Partido> partidos= q.getPartidos();
	   ContentValues val= null;

	   val = new ContentValues();	   
	   val.put("id_temporada", q.getTemporada());
	   val.put("fecha", q.getJornada());
	   val.put("disponible_correccion", Quiniela.QUINIELA_CORREGIDA_SI);
       long idQuin= con.insert("quinielas_disponibles_server", null, val);
	   
	   for (int k=1; k<=partidos.size(); k++) 
	   {
		   Partido par= partidos.get(k-1);
		   val = new ContentValues();
		   val.put("id_quiniela", idQuin);
		   val.put("id_local", par.getIdEquipoLocal());
		   val.put("id_visitante", par.getIdEquipoVisit());
		   val.put("partido_num", par.getPartidoNum());
		   val.put("resultado_1x2", QuinielaOp.signoQuinielaInt2String(par.getResultadoQuiniela()));
	       con.insert("quinielas_partidos_disponibles_server", null, val);	       
	   }	   
   }
   
   
   public void grabarDefinicionQuinielaJornada(Quiniela qui)
   {
	   ArrayList<Partido> partidos= qui.getPartidos();
	   ContentValues val= null;

	   val = new ContentValues();	   
	   val.put("id_temporada", qui.getTemporada());
	   val.put("fecha", qui.getJornada());
	   val.put("disponible_correccion", 0);
       long idQuin= con.insert("quinielas_disponibles_server", null, val);
	   
	   for (int k=1; k<=partidos.size(); k++) 
	   {
		   Partido par= partidos.get(k-1);
		   val = new ContentValues();
		   val.put("id_quiniela", idQuin);
		   val.put("id_local", par.getIdEquipoLocal());
		   val.put("id_visitante", par.getIdEquipoVisit());
		   val.put("partido_num", par.getPartidoNum());
	       con.insert("quinielas_partidos_disponibles_server", null, val);	       
	   }	   		
   }

   public void grabarQuiniela(Quiniela q, int idUsuario)
   {
	   ArrayList<Partido> partidos= q.getPartidos();
	   ContentValues val= null;

	   val = new ContentValues();	   
	   val.put("id_temporada", q.getTemporada());
	   val.put("fecha", q.getJornada());
	   val.put("nombre", q.getNombre());
       long idQuin= con.insert("quinielas", null, val);
	   
	   for (int k=1; k<=partidos.size(); k++) 
	   {
		   Partido par= partidos.get(k-1);
		   val = new ContentValues();
		   val.put("id_quiniela", idQuin);
		   val.put("id_local", par.getIdEquipoLocal());
		   val.put("id_visitante", par.getIdEquipoVisit());
		   val.put("partido_num", k);
		   val.put("resultado_1x2", QuinielaOp.signoQuinielaInt2String(par.getResultadoQuiniela()));		   
	       con.insert("quinielas_partidos", null, val);	       
	   }	   		

	   val = new ContentValues();	   
	   val.put("id_quiniela", idQuin);
	   val.put("id_usuario", idUsuario);
       con.insert("quinielas_usuarios", null, val);
	   
   }

   	public int getUltimaJornadaQuinielaCorregidaCargada(int temporada)
   	{
	    int ret= 0;
        Cursor c = con.rawQuery("SELECT max(fecha) FROM quinielas_disponibles_server where id_temporada=? and disponible_correccion=1", new String[]{""+temporada});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
			ret= c.getInt(0);
		c.close();
		return ret;   	
	}
   	
   	/*
   	 * se mirará si en la tabla 'quinielas_disponibles_server' hay algún registro con disponible_correccion=0
   	 * 	y tal que la fecha de la quiniela sea superior al día de hoy
   	 */
   	public boolean hayDefinicionesDeQuinielasSinCorreccionDisponible(int temporada, int idUsuario, String hoy)
   	{
   		/*
			CREATE TABLE quinielas (id integer PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 
									id_temporada integer NOT NULL , 
									fecha integer NOT NULL, 
									nombre varchar, 
									aciertos integer default -1  );
			CREATE TABLE quinielas_usuarios (id_quiniela integer NOT NULL , 
									id_usuario integer NOT NULL , PRIMARY KEY (id_quiniela, id_usuario));
			CREATE TABLE quinielas_disponibles_server (id integer PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 
									id_temporada integer NOT NULL , 
									fecha integer NOT NULL, 
									disponible_correccion bool default 0  );
   		 */
   		boolean ret= false;
        Cursor c = con.rawQuery("SELECT count(qs.id) as cuantas " +
				   " FROM quinielas q, " +
				   "	  quinielas_usuarios qu," +
				   "	  quinielas_disponibles_server qs " +
				   " WHERE qu.id_quiniela=q.id" +
				   		" and q.id_temporada=? " +
				   		" and qu.id_usuario=? " + 
				   		" and q.id_temporada=qs.id_temporada " +
				   		" and q.fecha=qs.fecha "+
				   		" and q.fecha < "+hoy +  // porque de lo contrario en el momento que se hace 1 quiniela estaría siempre comprobando (hasta que esté su corrección)
				   		" and qs.disponible_correccion=0 ", 
					new String[]{""+temporada, ""+idUsuario});          
		if (c.moveToFirst()) 
			ret= (c.getInt(0) > 0);
		c.close();
   		return ret;
   	}
   	
   	public ArrayList<Integer> getFechasDeDefinicionQuinielasSinCorreccionDisponible(int temporada, int idUsuario)
   	{
   		ArrayList<Integer> ret= new ArrayList<Integer>();
        Cursor c = con.rawQuery("SELECT distinct(qs.fecha) " +
				   " FROM quinielas q, " +
				   "	  quinielas_usuarios qu," +
				   "	  quinielas_disponibles_server qs " +
				   " WHERE qu.id_quiniela=q.id" +
				   		" and q.id_temporada=? " +
				   		" and qu.id_usuario=? " + 
				   		" and q.id_temporada=qs.id_temporada " +
				   		" and q.fecha=qs.fecha "+
				   		" and qs.disponible_correccion=0 ", 
					new String[]{""+temporada, ""+idUsuario});          
		if (c.moveToFirst()) 
		{
			do 
			{
				ret.add(new Integer(c.getInt(0)));
			} 
			while(c.moveToNext());
		}
		c.close();
   		return ret;
   	}
   	
   	public ArrayList<Quiniela> getTodasQuinielasDeTemporada(int temporada, int idUsuario)
   	{
   		ArrayList<Quiniela> ret= new ArrayList<Quiniela>();
        Cursor c = con.rawQuery("SELECT q.id, q.nombre, q.fecha, q.aciertos " +
				   " FROM quinielas q, " +
				   "	  quinielas_usuarios u " +
				   " WHERE u.id_quiniela=q.id" +
				   		" and q.id_temporada=? " +
				   		" and u.id_usuario=" + idUsuario +
				   " ORDER BY q.fecha desc, q.nombre", 
					new String[]{""+temporada});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
		{
			do 
			{
				Quiniela quin= new Quiniela(temporada, c.getInt(2));
				quin.setId(c.getInt(0));
				quin.setNombre(c.getString(1));
				quin.setPartidos(getPartidosDeQuinielaHecha(c.getInt(0)));
				quin.setAciertos(c.getInt(3));
				ret.add(quin);
			} 
			while(c.moveToNext());
		}
		c.close();
   		return ret;
   	}
   	
   	public ArrayList<Quiniela> getQuinielasHechasJornada(int temporada, int jornada, long idUsuario)
   	{
		/*
		CREATE TABLE quinielas 
			(id integer PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 
			id_temporada integer NOT NULL , 
			fecha integer NOT NULL, 
			nombre varchar,
			aciertos integer default -1  );
		CREATE TABLE quinielas_partidos 
			(id integer PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 
			id_quiniela integer NOT NULL, 
			id_local varchar NOT NULL , 
			id_visitante varchar NOT NULL , 
			partido_num integer NOT NULL , 
			resultado_1x2 CHAR NOT NULL );
		CREATE TABLE quinielas_usuarios 
			(id_quiniela integer NOT NULL , 
			id_usuario integer NOT NULL , PRIMARY KEY (id_quiniela, id_usuario));
		 */   		
   		ArrayList<Quiniela> ret= new ArrayList<Quiniela>();
        Cursor c = con.rawQuery("SELECT q.id, q.nombre, q.aciertos " +
				   " FROM quinielas q, " +
				   "	  quinielas_usuarios u " +
				   " WHERE u.id_quiniela=q.id" +
				   		" and q.id_temporada=? and q.fecha=?" +
				   		" and u.id_usuario=?" + 
				   " ORDER BY q.fecha, q.nombre", 
					new String[]{""+temporada, ""+jornada, ""+idUsuario});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
		{
			do 
			{
				Quiniela quin= new Quiniela(temporada, jornada);
				quin.setId(c.getInt(0));
				quin.setNombre(c.getString(1));
				quin.setPartidos(getPartidosDeQuinielaHecha(c.getInt(0)));
				quin.setAciertos(c.getInt(2));
				ret.add(quin);
			} 
			while(c.moveToNext());
		}
		c.close();
   		return ret;
   	}

   	public Quiniela getQuinielaHecha(int idQuiniela)
   	{		
   		Quiniela quin= null;
        Cursor c = con.rawQuery("SELECT id_temporada, fecha, nombre, q.aciertos " +
				   " FROM quinielas q " +
				   " WHERE id=?" , 
					new String[]{""+idQuiniela});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
		{
			quin= new Quiniela(c.getInt(0), c.getInt(1));
			quin.setId(idQuiniela);
			quin.setNombre(c.getString(2));
			quin.setPartidos(getPartidosDeQuinielaHecha(idQuiniela));
			quin.setAciertos(c.getInt(3));
		}
		c.close();
   		return quin;
   	}
   	
   	private ArrayList<Partido> getPartidosDeQuinielaHecha(int idQuiniela)
   	{
   		ArrayList<Partido> ret= new ArrayList<Partido>();
        Cursor c = con.rawQuery("SELECT id_local, id_visitante, partido_num, resultado_1x2 " +
				   " FROM quinielas_partidos " +
				   " WHERE id_quiniela=" + idQuiniela +
				   " ORDER BY partido_num", null);          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
		{
			do 
			{
				Partido par= new Partido(c.getString(0), c.getString(1));
				par.setPartidoNum(c.getInt(2));
				par.setResultadoQuiniela(QuinielaOp.signoQuinielaString2Int(c.getString(3)));
				ret.add(par);
			} 
			while(c.moveToNext());
		}
		c.close();
   		return ret;
   	}

    public void borrar(int id)
    {       
        con.execSQL("delete from quinielas where id="+id);
        con.execSQL("delete from quinielas_usuarios where id_quiniela="+id);
        con.execSQL("delete from quinielas_partidos where id_quiniela="+id);        
    }
    
    public void borrarPatron(int idPatron)
    {
        con.execSQL("delete from patrones_pesos_factores where id="+idPatron);
    }
    
   	public ArrayList<PatronFactoresPesos> getPatronesUsuarioIdNombre(int idUsuario)
   	{
   		ArrayList<PatronFactoresPesos> ret= new ArrayList<PatronFactoresPesos>();
        Cursor c = con.rawQuery("SELECT id, nombre " +
				   " FROM patrones_pesos_factores " +
        		   " WHERE id_usuario=?" +
				   " ORDER BY nombre", 
					new String[]{""+idUsuario});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
		{
			do 
			{
				PatronFactoresPesos pat= new PatronFactoresPesos();
				pat.setId(c.getInt(0));
				pat.setNombre(c.getString(1));
				
				ret.add(pat);
			} 
			while(c.moveToNext());
		}
		c.close();
   		return ret;
   	}
   	
   	public FactoresPesos getFactoresDePatron(int idPatron)
   	{
   		FactoresPesos fact= null;
        Cursor c = con.rawQuery("SELECT aleatoriedad,calidad_intrinseca,factor_campo,golaveraje,golaveraje_localvisit,puntos_partido,puntos_partido_localvisit,ultimos4,ultimos4_localvisit " +
				   " FROM patrones_pesos_factores " +
        		   " WHERE id=?", 
					new String[]{""+idPatron});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
		{
		   fact= new FactoresPesos();
    	   fact.setAleatoriedad(c.getInt(0));
    	   fact.setCalidadIntrinseca(c.getInt(1));
    	   fact.setFactorCampo(c.getInt(2));
    	   fact.setGolaveraje(c.getInt(3));
    	   fact.setGolaverajeLocalVisit(c.getInt(4));
    	   fact.setPuntosPartido(c.getInt(5));
    	   fact.setPuntosPartidoLocalVisit(c.getInt(6));
    	   fact.setUltimos4(c.getInt(7));
    	   fact.setUltimos4LocalVisit(c.getInt(8));
		}
		c.close();
   		return fact;
   	}
   	
   	public int getNumPatrones(int idUsuario)
   	{
   		int ret= 0;
        Cursor c = con.rawQuery("SELECT count(id) as cuantos" +
				   " FROM patrones_pesos_factores " +
        		   " WHERE id_usuario=?" ,
					new String[]{""+idUsuario});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro		
			ret= c.getInt(0);
		c.close();
   		return ret;
   	}

   	public boolean existePatronQuiniela(String nombre)
   	{
   		boolean ret= false;
   		Cursor c = con.rawQuery("select count(id) as cuantos "+
			 	    			"from patrones_pesos_factores " +
			 	    			"where nombre=?",
								new String[]{""+nombre});
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro		
			ret= (c.getInt(0) > 0);
		c.close();
   		return ret;
   	}
   	
    public void grabarPatronQuiniela(PatronFactoresPesos patron, int idUsuario)
    {       
        FactoresPesos fact= patron.getFactoresPesos();
        ContentValues val = new ContentValues();
	    val.put("id_usuario", idUsuario);
	    val.put("nombre", patron.getNombre());
	    val.put("aleatoriedad", fact.getAleatoriedad());
	    val.put("calidad_intrinseca", fact.getCalidadIntrinseca());
	    val.put("factor_campo", fact.getFactorCampo());
	    val.put("golaveraje", fact.getGolaveraje());
	    val.put("golaveraje_localvisit", fact.getGolaverajeLocalVisit());	    
	    val.put("puntos_partido", fact.getPuntosPartido());
	    val.put("puntos_partido_localvisit", fact.getPuntosPartidoLocalVisit());	    
	    val.put("ultimos4", fact.getUltimos4());
	    val.put("ultimos4_localvisit", fact.getUltimos4LocalVisit());   
        con.insert("patrones_pesos_factores", null, val);	       
    }

    public boolean hayAlgunaQuinielaDeEstaSemanaSinCorregir(int temporada, int jornada, int idUsuario)
    {
    	boolean ret= false;
        Cursor c = con.rawQuery("SELECT count(q.id) " +
				   " FROM quinielas q, " +
				   "	  quinielas_usuarios u " +
				   " WHERE q.aciertos= -1 " +
				   		" and u.id_quiniela=q.id" +
				   		" and q.id_temporada=? and q.fecha=?" +
				   		" and u.id_usuario=?" +
				   " ORDER BY q.fecha, q.nombre", 
					new String[]{""+temporada, ""+jornada, ""+idUsuario});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
			ret= (c.getInt(0) > 0);
		c.close();
   		return ret;
    }
    
   	public ArrayList<Quiniela> getQuinielasDeEstaSemanaSinCorregir(int temporada, int jornada, long idUsuario)
   	{
   		ArrayList<Quiniela> ret= new ArrayList<Quiniela>();
        Cursor c = con.rawQuery("SELECT q.id, q.nombre, q.aciertos " +
				   " FROM quinielas q, " +
				   "	  quinielas_usuarios u " +
				   " WHERE q.aciertos= -1 " +
				   		" and u.id_quiniela=q.id" +
				   		" and q.id_temporada=? and q.fecha=?" +
				   		" and u.id_usuario=?" + 
				   " ORDER BY q.fecha, q.nombre", 
					new String[]{""+temporada, ""+jornada, ""+idUsuario});          
		if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro
		{
			do 
			{
				Quiniela quin= new Quiniela(temporada, jornada);
				quin.setId(c.getInt(0));
				quin.setNombre(c.getString(1));
				quin.setPartidos(getPartidosDeQuinielaHecha(c.getInt(0)));
				quin.setAciertos(c.getInt(2));
				ret.add(quin);
			} 
			while(c.moveToNext());
		}
		c.close();
   		return ret;
   	}
    
}
