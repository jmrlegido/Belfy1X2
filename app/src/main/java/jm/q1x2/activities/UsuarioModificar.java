package jm.q1x2.activities;


import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.UsuarioDao;
import jm.q1x2.transobj.Usuario;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Preferencias;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UsuarioModificar extends UsuarioNuevo 
{
	private int idUsuario;
	private boolean bEsElUsuarioActual;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
    	idUsuario= extras.getInt(Constantes.ID_USUARIO_MODIF_BORRAR);
    	bEsElUsuarioActual= extras.getBoolean(Constantes.ES_USUARIO_ACTUAL);
        SQLiteDatabase con= Basedatos.getConexion(this, Basedatos.LECTURA);
        UsuarioDao usuDao= new UsuarioDao(con);
        Usuario usu= usuDao.getUsuario(idUsuario);
        Basedatos.cerrarConexion(con);  //[jm] con.close();
        EditText v= (EditText) findViewById(R.id.valor);
    	v.setText(usu.getNombre());
    }    

    @Override
    public void boton_aceptar(View v)
    {    	    	    	
		EditText usu= (EditText) findViewById(R.id.valor);
		String nombreUsuario= usu.getText().toString();
        SQLiteDatabase con= Basedatos.getConexion(v.getContext(), Basedatos.ESCRITURA);
        UsuarioDao usuDao= new UsuarioDao(con);
        Usuario obj= new Usuario(idUsuario, nombreUsuario);
        usuDao.modificar(obj);
        Basedatos.cerrarConexion(con);  //[jm] con.close();
		
        if (bEsElUsuarioActual)
	 	    Preferencias.grabarPreferenciaString(getApplicationContext(), Constantes.PREFERENCIAS_USUARIO_NOMBRE, nombreUsuario);
        
		Mensajes.alerta(getApplicationContext(), "Usuario modificado correctamente");
		setResult(RESULT_OK);
		finish();
    }
       
}
