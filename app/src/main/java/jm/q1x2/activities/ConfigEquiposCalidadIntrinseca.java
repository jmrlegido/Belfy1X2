package jm.q1x2.activities;


import java.util.ArrayList;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.transobj.Equipo;
import jm.q1x2.transobj.vista.ObjetoIconoTextoNumero;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Preferencias;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ConfigEquiposCalidadIntrinseca extends ListActivity
{
	private ArrayList<String> id_elemsLista;
	LinearLayout layoutPrincipal;

    private ArrayList<ObjetoIconoTextoNumero> m_equipos = null;
    private AdaptadorEquipo m_adaptador;
    int temporadaActual= -1;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_equipos_calidad_intrinseca);
        m_equipos = new ArrayList<ObjetoIconoTextoNumero>();
       
	    ListView lista = (ListView) findViewById(android.R.id.list);
	    lista.setOnItemClickListener(new OnItemClickListener() 
	    {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		      {
				    String idEquipo= id_elemsLista.get(position);			   
	         		Intent i = new Intent(getApplicationContext(), ConfigEquipoCalidadIntrinseca.class);			
	        	    i.putExtra(Constantes.EQUIPO_SELEC_ID, idEquipo);
	        		startActivityForResult(i, 1);  // 1 por poner algo		   
		      }
		    });
        
	    ToggleButton b= (ToggleButton) findViewById(R.id.boton1);
	    b.setChecked(true);
	    temporadaActual= Preferencias.getTemporadaActual(getApplicationContext());
        actualizarListaEquipos(temporadaActual);
    }

	public void boton_division(View v) 
	{
		actualizarListaEquipos(temporadaActual);
	}
    
    /** Called when an activity called by using startActivityForResult finishes. */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	actualizarListaEquipos(temporadaActual);        
    }
    
	private void actualizarListaEquipos(int temporada)
	{
		int division= -1;
		ToggleButton b1= (ToggleButton) findViewById(R.id.boton1);
		ToggleButton b2= (ToggleButton) findViewById(R.id.boton2);
		if (b1.isChecked() && b2.isChecked())
			division= Equipo.DIVISION_1_Y_2;
		else if (b1.isChecked())
			division= Equipo.DIVISION_1;
		else if (b2.isChecked())
			division= Equipo.DIVISION_2;

		SQLiteDatabase con= Basedatos.getConexion(this, Basedatos.ESCRITURA);
        EquipoDao eqDao= new EquipoDao(con);       
        ArrayList<Equipo> eqs= eqDao.getEquipos(temporada, division, Preferencias.getUsuarioId(getApplicationContext()), Equipo.ORDEN_CALIDAD_INTRINSECA);
        Basedatos.cerrarConexion(con);  //[jm] con.close();
                
        id_elemsLista= new ArrayList<String>();
        m_equipos= new ArrayList<ObjetoIconoTextoNumero>();
        for (Equipo eq: eqs)
        {
        	id_elemsLista.add(eq.getId());
        	m_equipos.add(new ObjetoIconoTextoNumero(eq.getId(), eq.getNombre(), eq.getCalidadIntrinseca()));
        }
        
        this.m_adaptador = new AdaptadorEquipo(this, R.layout.cada_equipo_calidad_intrinseca, m_equipos);
        setListAdapter(this.m_adaptador);        
	}
    
	private class AdaptadorEquipo extends ArrayAdapter<ObjetoIconoTextoNumero> 
	{
        private ArrayList<ObjetoIconoTextoNumero> items;

        public AdaptadorEquipo(Context context, int textViewResourceId, ArrayList<ObjetoIconoTextoNumero> items) 
        {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
                View v = convertView;
                if (v == null) 
                {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.cada_equipo_calidad_intrinseca, null);
                }
                ObjetoIconoTextoNumero o = items.get(position);
                if (o != null) 
                {
                	ImageView img= (ImageView) v.findViewById(R.id.icono_equipo);
                	if (img != null)
                		img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("esc_"+o.getIcono(), "drawable", getPackageName())));
                    TextView tt = (TextView) v.findViewById(R.id.nombre_equipo);
                    if (tt != null)
                        tt.setText(o.getTexto());
                    TextView ci = (TextView) v.findViewById(R.id.calidad_intrinseca);
                    if (ci != null)
                        ci.setText(""+o.getNumero());
                }
                return v;
        }
	}	
	
}
