package jm.q1x2.activities;


import jm.q1x2.R;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class QuinielaVerPronosticoNotif extends QuinielaVerPronostico 
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }    

    protected void accionesPresentacion()
    {
        setContentView(R.layout.lay_quiniela);
        
        // la anchura del diálogo será la de la pantalla
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); 

        Button bot= (Button) findViewById(R.id.boton_grabar_quiniela);
        bot.setVisibility(View.GONE);
        bot= (Button) findViewById(R.id.boton_aceptar);
        bot.setVisibility(View.VISIBLE);
    }
    
    public void btn_aceptar(View v)
    {
    	finish();
    }
}
