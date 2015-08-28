package jm.q1x2.bbdd.dao;


import jm.q1x2.transobj.FactoresPesos;
import jm.q1x2.transobj.Usuario;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ConfigDao extends DaoGeneral
{
   public ConfigDao(SQLiteDatabase con)
   {
	   super(con);
   }
	
   public void anadir(Usuario usu)
   {       
       con.execSQL("INSERT INTO usuarios (nombre) VALUES ('" + usu.getNombre() +"')");
   }

   public void actualizarParamsConfigQuinielas(int idUsuario, FactoresPesos fact)
   {       
	   String sql= "update pesos_factores set " +
	   				"aleatoriedad="+fact.getAleatoriedad()+", calidad_intrinseca="+fact.getCalidadIntrinseca()+", factor_campo="+fact.getFactorCampo()+
	   					", golaveraje="+fact.getGolaveraje()+", golaveraje_localvisit="+fact.getGolaverajeLocalVisit()+
	   					", puntos_partido="+fact.getPuntosPartido()+", puntos_partido_localvisit="+fact.getPuntosPartidoLocalVisit()+
	   					", ultimos4="+fact.getUltimos4()+", ultimos4_localvisit="+fact.getUltimos4LocalVisit() +   
			   	   " where id_usuario= "+idUsuario;	   
       con.execSQL(sql);
   }

   public FactoresPesos getParamsConfigQuinielas(int idUsuario)
   {
	   FactoresPesos fact= null;
       
       Cursor c = con.rawQuery("SELECT aleatoriedad, calidad_intrinseca, factor_campo, golaveraje, golaveraje_localvisit, puntos_partido, puntos_partido_localvisit, ultimos4, ultimos4_localvisit" +
       						   " FROM pesos_factores where id_usuario="+idUsuario, null);          
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
   
}
