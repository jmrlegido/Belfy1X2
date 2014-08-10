package jm.q1x2.activities;


import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.QuinielaDao;
import jm.q1x2.transobj.FactoresPesos;
import jm.q1x2.transobj.PatronFactoresPesos;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Preferencias;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

public class QuinielaGrabarPatron extends Activity 
{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_input_aceptar_cancelar);

        // la anchura del diálogo será la de la pantalla
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); 
        
        TextView eti= (TextView) findViewById(R.id.texto_etiqueta);
        eti.setText("Nombre del patrón:");
        EditText v= (EditText) findViewById(R.id.valor);
        v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
   }    

    public void boton_aceptar(View v)
    {
		EditText nombre= (EditText) findViewById(R.id.valor);

		Bundle extras = getIntent().getExtras();
		FactoresPesos f= (FactoresPesos) extras.getSerializable(Constantes.QUINIELA_PATRON_GRABAR);

        SQLiteDatabase con= Basedatos.getConexion(v.getContext(), Basedatos.ESCRITURA);
		QuinielaDao qDao= new QuinielaDao(con);
				
		PatronFactoresPesos pat= new PatronFactoresPesos(nombre.getText().toString(), f);
		if (qDao.existePatronQuiniela(nombre.getText().toString()))
			nombre.setError("Existe otro patrón con ese nombre");
		else
		{
			qDao.grabarPatronQuiniela(pat, Preferencias.getUsuarioId(getApplicationContext()));
        	Mensajes.alerta(getApplicationContext(), "patrón grabado: "+nombre.getText());
    		setResult(RESULT_OK);
		}
        con.close();
		
       	finish();        
    }

    public void boton_cancelar(View v)
    {
		setResult(RESULT_CANCELED);    	
    	finish();
    }
}
