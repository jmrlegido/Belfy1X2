package jm.q1x2.activities;

import jm.q1x2.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class Config extends Activity 
{
	private int SUBACTIVITY_EQUIPOS_CALIDAD_INTRINSECA= 1;
	private int SUBACTIVITY_PARAMS_QUINIELAS= 2;	
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_config);
        
        LinearLayout opc1= (LinearLayout) findViewById(R.id.equipos_calidad_intrinseca);
        opc1.setOnClickListener(new OnClickListener()
        {
        	 public void onClick(View v)
        	 {
  	 		   Intent i = new Intent(getApplicationContext(), ConfigEquiposCalidadIntrinseca.class);			
  			   startActivityForResult(i, SUBACTIVITY_EQUIPOS_CALIDAD_INTRINSECA);		   	    	 
        	 }
        });

        LinearLayout opc2= (LinearLayout) findViewById(R.id.params_quinielas);
        opc2.setOnClickListener(new OnClickListener()
        {
        	 public void onClick(View v)
        	 {
  	 		   Intent i = new Intent(getApplicationContext(), ConfigParametrosQuinielas.class);			
  			   startActivityForResult(i, SUBACTIVITY_PARAMS_QUINIELAS);		   	    	 
        	 }
        });
    }

}
