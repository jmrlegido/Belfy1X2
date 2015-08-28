package jm.q1x2.activities;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.transobj.Equipo;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Preferencias;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ConfigEquipoCalidadIntrinseca extends Activity implements SeekBar.OnSeekBarChangeListener
{
	private TextView equipoTitulo= null;
	private SeekBar barra= null;
	private EditText valor= null;
	
	private String idEquipo= null;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_equipo_calidad_intrinseca);
        
        Bundle extras = getIntent().getExtras();
    	idEquipo= extras.getString(Constantes.EQUIPO_SELEC_ID);
        
        SQLiteDatabase con= Basedatos.getConexion(this, Basedatos.LECTURA);
        EquipoDao eqDao= new EquipoDao(con);       
        Equipo eq= eqDao.getEquipo(idEquipo, Preferencias.getTemporadaActual(getApplicationContext()), Preferencias.getUsuarioId(getApplicationContext()));
        Basedatos.cerrarConexion(con);  //[jm] con.close();
    	String descEquipo= eq.getNombre();
    	int calidadIntrinseca= eq.getCalidadIntrinseca();
    	
        botones();
        
    	ImageView img= (ImageView) findViewById(R.id.eci_icono);
    	if (img != null)
    		img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("esc_"+idEquipo, "drawable", getPackageName())));
        equipoTitulo= (TextView) findViewById(R.id.eci_equipo);
        if (equipoTitulo != null)
        	equipoTitulo.setText(descEquipo);
        valor= (EditText) findViewById(R.id.eci_valor);
        valor.setText(""+calidadIntrinseca);
        barra = (SeekBar) findViewById(R.id.eci_barra);
        barra.setProgress(calidadIntrinseca);
        barra.setSecondaryProgress(calidadIntrinseca);
        barra.setOnSeekBarChangeListener(this);

        valor.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
                valor.setCursorVisible(true);
                valor.setSelection(valor.getText().length());
            }

        });        
        
   }    

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) 
	{
	    valor.setText(""+progress); 
	}    

    public void onStartTrackingTouch(SeekBar seekBar) 
    {
    	valor.setCursorVisible(false);
    }

    public void onStopTrackingTouch(SeekBar seekBar) 
    {}
    
    private void botones()
    {
    	boton_aceptar();
    	boton_cancelar();
    }

    private void boton_aceptar()
    {
        Button btn_aceptar = (Button)findViewById(R.id.eci_boton_aceptar);
        btn_aceptar.setOnClickListener(new Button.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
/*        		private TextView equipoTitulo= null;
        		private SeekBar barra= null;
        		private EditText valor= null;
*/
        		SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);
        		EquipoDao dao= new EquipoDao(con);
        		dao.actualizarCalidadIntrinseca(Preferencias.getTemporadaActual(getApplicationContext()), idEquipo, Preferencias.getUsuarioId(getApplicationContext()), (new Integer(valor.getText().toString())).intValue());
        		Basedatos.cerrarConexion(con);  //[jm] con.close();
        		
        		setResult(RESULT_OK);
        		finish();
        	}
        });        
    }

    private void boton_cancelar()
    {
        Button btn_cancelar = (Button)findViewById(R.id.eci_boton_cancelar);
        btn_cancelar.setOnClickListener(new Button.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
        		finish();
        	}
        });        
    }
}
