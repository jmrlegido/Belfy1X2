package jm.q1x2.activities;


import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.UsuarioDao;
import jm.q1x2.transobj.Usuario;
import jm.q1x2.utils.Mensajes;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public class UsuarioNuevo extends Activity 
{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_input_aceptar_cancelar);
        
        // la anchura del diálogo será la de la pantalla
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); 
        
        // el campo valor tendrá como máximo 12 caracteres:
        EditText v= (EditText) findViewById(R.id.valor);
        v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
   }    

    public void boton_aceptar(View v)
    {
		EditText usu= (EditText) findViewById(R.id.valor);

        SQLiteDatabase con= Basedatos.getConexion(v.getContext(), Basedatos.ESCRITURA);
        UsuarioDao usuDao= new UsuarioDao(con);
        Usuario obj= new Usuario(usu.getText().toString());
        int usuNuevo= usuDao.anadir(obj);
        Basedatos.cerrarConexion(con);  //[jm] con.close();
		
        if (usuNuevo == -1)
        	usu.setError("Existe otro usuario con ese nombre");
        else
        {
        	Mensajes.alerta(getApplicationContext(), "usuario añadido: "+usu.getText());
    		setResult(RESULT_OK);
        	finish();
        }
    }

    public void boton_cancelar(View v)
    {
		setResult(RESULT_CANCELED);    	
    	finish();
    }
}
