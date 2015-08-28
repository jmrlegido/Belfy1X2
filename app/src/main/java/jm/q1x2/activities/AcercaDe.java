package jm.q1x2.activities;


import jm.q1x2.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class AcercaDe extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_acercade);
        
        // la anchura del diálogo será la de la pantalla
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);         
   }    

    public void boton_aceptar(View v)
    {
       	finish();
    }
}
