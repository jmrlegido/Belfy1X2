package jm.q1x2.activities;


import jm.q1x2.R;
import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.transobj.Quiniela;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Preferencias;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

public class QuinielaGrabar extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_input_aceptar_cancelar);

        TextView label= (TextView) findViewById(R.id.texto_etiqueta);
        label.setText("Nombre (opcional):");
        // la anchura del diálogo será la de la pantalla
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); 
    }    

    public void boton_aceptar(View v)
    {
        Bundle extras = getIntent().getExtras();
        Quiniela quinielaHecha= (Quiniela) extras.getSerializable(Constantes.QUINIELA_GRABAR);
    	EditText contenido = (EditText) findViewById(R.id.valor);
    	String nombreQuin= contenido.getText().toString();

    	QuinielaOp quinOp= new QuinielaOp();
    	
    	int usuId= Preferencias.getUsuarioId(getApplicationContext());
    	if (nombreQuin.length() > 0  &&  quinOp.existeQuinielaConNombre(nombreQuin, usuId, quinielaHecha.getJornada() ,getApplicationContext()))
        	contenido.setError("Ya existe otra quiniela de esta jornada con ese nombre");    		
    	else
    	{
	    	quinielaHecha.setNombre(contenido.getText().toString());
	    	quinOp.grabarQuiniela(quinielaHecha, usuId, getApplicationContext());
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
