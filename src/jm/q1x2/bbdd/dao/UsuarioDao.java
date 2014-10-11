package jm.q1x2.bbdd.dao;


import java.util.ArrayList;

import jm.q1x2.config.Config;
import jm.q1x2.logneg.UsuarioOp;
import jm.q1x2.transobj.Usuario;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsuarioDao extends DaoGeneral
{
   public UsuarioDao(SQLiteDatabase con)
   {
	   super(con);
   }
	
   /*
    * devuelve -1 si el nombre de usuario ya existe
    */
   public int anadir(Usuario usu)
   {       
	   if (getUsuarioPorNombre(usu.getNombre()) != null)
		   return -1;
	   else
	   {
		   ContentValues val = new ContentValues();	   
		   val.put("nombre", usu.getNombre());
	       long idUsu= con.insert("usuarios", null, val);

		   // se crea su registro correspondiente en la tabla  pesos_factores: 
		   val = new ContentValues();	   
		   val.put("id_usuario", idUsu);
		   val.put("aleatoriedad", UsuarioOp.VALORDEF_ALEATORIEDAD); 
		   val.put("calidad_intrinseca", UsuarioOp.VALORDEF_CALIDADINTRINSECA); 
		   val.put("factor_campo", UsuarioOp.VALORDEF_FACTORCAMPO); 
		   val.put("golaveraje", UsuarioOp.VALORDEF_GOLAVERAJE); 
		   val.put("golaveraje_localvisit", UsuarioOp.VALORDEF_GOLAVERAJE_LOCALVISIT);
		   val.put("puntos_partido", UsuarioOp.VALORDEF_PUNTOSPARTIDO);
		   val.put("puntos_partido_localvisit", UsuarioOp.VALORDEF_PUNTOSPARTIDO_LOCALVISIT);
		   val.put("ultimos4", UsuarioOp.VALORDEF_ULTIMOS4);
		   val.put("ultimos4_localvisit", UsuarioOp.VALORDEF_ULTIMOS4_LOCALVISIT);
	       con.insert("pesos_factores", null, val);

		   // se crea su registro correspondiente en la tabla  equipos_usuarios:
	       rellenarTablaEquiposUsuariosConValoresPorDefecto(idUsu);

		   return 1;
	   }
   }
   
   public void modificar(Usuario usu)
   {       
       con.execSQL("update usuarios set nombre='"+usu.getNombre()+"' where id="+usu.getId());
   }

   public void borrar(long id)
   {       
       con.execSQL("delete from usuarios where id="+id);
       con.execSQL("delete from pesos_factores where id_usuario="+id);
       con.execSQL("delete from equipos_usuarios where id_usuario="+id);
       con.execSQL("delete from quinielas_usuarios where id_usuario="+id);
   }
   
   public void borrarTodosUsuarios()
   {       
       con.execSQL("delete from usuarios");
       con.execSQL("delete from pesos_factores");
       con.execSQL("delete from equipos_usuarios");
       con.execSQL("delete from quinielas_usuarios");
   }

   public Usuario getUsuario(long id)
   {
	   Usuario ret= null;       
       Cursor c = con.rawQuery("SELECT id,nombre FROM usuarios where id="+id, null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
    	   ret= new Usuario(c.getInt(0), c.getString(1));
       c.close();
       return ret;
   }

   public Usuario getUsuarioPorNombre(String nombre)
   {
	   Usuario ret= null;       
       Cursor c = con.rawQuery("SELECT id,nombre FROM usuarios where nombre='"+nombre+"'", null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
    	   ret= new Usuario(c.getInt(0), c.getString(1));
       c.close();
       return ret;
   }
   
   public Usuario getUsuarioDemo()
   {
	   Usuario ret= null;       
       Cursor c = con.rawQuery("SELECT id,nombre FROM usuarios where nombre='"+UsuarioOp.USUARIO_DEMO_NOMBRE+"'", null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
    	   ret= new Usuario(c.getInt(0), c.getString(1));
       c.close();
       return ret;
   }

   public Usuario crearUsuarioDemo()
   {
	   anadir(new Usuario(UsuarioOp.USUARIO_DEMO_NOMBRE));
       return getUsuarioDemo();
   }
   
   public ArrayList<Usuario> getUsuarios()
   {
	   ArrayList<Usuario> ret= new ArrayList<Usuario>();
       
       Cursor c = con.rawQuery(" SELECT id,nombre FROM usuarios", null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
       {
            do 
            {
                 ret.add(new Usuario(c.getInt(0), c.getString(1)));
            } 
            while(c.moveToNext());
       }                       
       c.close();
       return ret;
   }

   public ArrayList<String> getNombres()
   {
	   ArrayList<String> ret= new ArrayList<String>();
       
       Cursor c = con.rawQuery("SELECT nombre FROM usuarios", null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
       {
            do 
            {
                 ret.add(c.getString(0));
            } 
            while(c.moveToNext());
       }                       
       c.close();
       return ret;
   }

   public int getNumUsuarios()
   {
	   int ret= 0;       
       Cursor c = con.rawQuery("SELECT count(id) as cuantos FROM usuarios", null);          
       if (c.moveToFirst())  // nos aseguramos de que existe al menos un registro 
            ret= c.getInt(0);
       c.close();
       return ret;
   }
   
   public boolean existenDatosDePrefDeEsteUsuarioEnEstaTemporada(int idUsu, int idTemp)
   {
	   boolean ret= false;
       Cursor c = con.rawQuery("SELECT count(*) as cuantos FROM equipos_usuarios "+
    		   				   " where id_temporada=? and id_usuario=?", new String[]{""+idTemp, ""+idUsu});          
       if (c.moveToFirst())
    	   ret= (c.getInt(0) > 0);
       c.close();
	   return ret;
   }
   
   public void rellenarTablaEquiposUsuariosConValoresPorDefecto(long idUsu)
   {
	   int iTemporadaActual= new Integer(Config.getTemporadaActual()).intValue();
	   
       Cursor c = con.rawQuery("SELECT id_equipo,calidad_intrinseca FROM valoresdefecto_equipos_calidadintrinseca where id_temporada="+iTemporadaActual, null);          
       if (c.moveToFirst())
       {
    	   ContentValues val= null;
           do 
           {
        	   String idEquipo= c.getString(0);
        	   int iCalidadIntrinseca= c.getInt(1);

        	   val = new ContentValues();	   
        	   val.put("id_usuario", idUsu);
        	   val.put("id_temporada", iTemporadaActual); 
        	   val.put("id_equipo", idEquipo); 
        	   val.put("calidad_intrinseca", iCalidadIntrinseca); 
               con.insert("equipos_usuarios", null, val);
           } 
           while(c.moveToNext());    	   
       }
       c.close();
   }
   
}
