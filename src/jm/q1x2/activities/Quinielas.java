package jm.q1x2.activities;

import jm.q1x2.R;
import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class Quinielas extends Activity 
{
	private int SUBACTIVITY_HACER_QUINIELA= 1;
	private int SUBACTIVITY_VER_QUINIELAS_PASADAS= 2;
	private int SUBACTIVITY_COMPROBAR_QUINIELAS_SEMANA_PASADA= 3;
	private int SUBACTIVITY_COMPROBAR_QUINIELAS_ESTA_SEMANA_TIEMPO_REAL= 4;
	//private int SUBACTIVITY_QUINIELAS_PATRONES= 4;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_quinielas);
        
        LinearLayout opc1= (LinearLayout) findViewById(R.id.hacer_quiniela_esta_semana);
        opc1.setOnClickListener(new OnClickListener()
        {
        	 public void onClick(View v)
        	 {
  	 		   Intent i = new Intent(getApplicationContext(), QuinielaHacer.class);			
			   startActivityForResult(i, SUBACTIVITY_HACER_QUINIELA);		   	    	 
        	 }
        });

        LinearLayout opc1a= (LinearLayout) findViewById(R.id.hacer_quiniela_manual);
        opc1a.setOnClickListener(new OnClickListener()
        {
        	 public void onClick(View v)
        	 {
  	 		   Intent i = new Intent(getApplicationContext(), QuinielaHacerManual.class);			
			   startActivityForResult(i, SUBACTIVITY_HACER_QUINIELA);		   	    	 
        	 }
        });
        
        LinearLayout opc3= (LinearLayout) findViewById(R.id.comprobar_quinielas_semana_pasada);
        opc3.setOnClickListener(new OnClickListener()
        {
        	 public void onClick(View v)
        	 {
  	 		   Intent i = new Intent(getApplicationContext(), QuinielaComprobarSemanaPasada.class);			
			   startActivityForResult(i, SUBACTIVITY_COMPROBAR_QUINIELAS_SEMANA_PASADA);		   	    	 
        	 }
        });
         
        LinearLayout opc2= (LinearLayout) findViewById(R.id.ver_quinielas_pasadas);
        opc2.setOnClickListener(new OnClickListener()
        {
        	 public void onClick(View v)
        	 {
  	 		   Intent i = new Intent(getApplicationContext(), QuinielaVerResultadosTodas.class);			
			   startActivityForResult(i, SUBACTIVITY_VER_QUINIELAS_PASADAS);		   	    	 
        	 }
        });

        ponerSiProcedeSeguimientoEnTiempoReal();
    }

    private void ponerSiProcedeSeguimientoEnTiempoReal()
    {
        LinearLayout opc4= (LinearLayout) findViewById(R.id.ver_quinielas_tiempo_real);
        opc4.setVisibility(View.INVISIBLE);
        
        if (Utils.estamosEnPeriodoDeTiempoTalQueLaJornadaHaEmpezadoPeroNoAcabado()  
        	&& QuinielaOp.hayAlgunaQuinielaDeEstaSemanaSinCorregir(getApplicationContext()) )
        {
        	opc4.setVisibility(View.VISIBLE);
	        opc4.setOnClickListener(new OnClickListener()
	        {
	        	 public void onClick(View v)
	        	 {
	  	 		   Intent i = new Intent(getApplicationContext(), QuinielaComprobarEstaSemanaEnTiempoReal.class);			
				   startActivityForResult(i, SUBACTIVITY_COMPROBAR_QUINIELAS_ESTA_SEMANA_TIEMPO_REAL);		   	    	 
	        	 }
	        });
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
	    super.onActivityResult(requestCode, resultCode, data);
	    ponerSiProcedeSeguimientoEnTiempoReal();
	    if (requestCode == SUBACTIVITY_COMPROBAR_QUINIELAS_ESTA_SEMANA_TIEMPO_REAL)
	    {
	    	if (resultCode == Constantes.RESULT_CODE_SE_HAN_COMPLETADO_TODOS_LOS_PARTIDOS_EN_TIEMPO_REAL)
	    	{
  	 		   Intent i = new Intent(getApplicationContext(), QuinielaComprobarSemanaPasada.class);			
			   startActivityForResult(i, SUBACTIVITY_COMPROBAR_QUINIELAS_SEMANA_PASADA);		   	    	 	    		
	    	}
	    }
    }    
    
}
