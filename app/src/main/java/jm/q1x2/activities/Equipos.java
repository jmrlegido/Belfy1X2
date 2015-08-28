package jm.q1x2.activities;

import java.util.ArrayList;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.EquipoDao;
import jm.q1x2.transobj.Equipo;
import jm.q1x2.transobj.vista.ObjetoIconoTexto;
import jm.q1x2.utils.Constantes;
import jm.q1x2.utils.Preferencias;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Equipos extends ListActivity 
{
	private ArrayList<String> id_elemsLista;
	LinearLayout layoutPrincipal;

	private int MENU_RESULTADOS= 1;
	private int MENU_ESTADISTICAS= 2;
	
    private ArrayList<ObjetoIconoTexto> m_equipos = new ArrayList<ObjetoIconoTexto>();;
    private AdaptadorEquipo m_adaptador;	
    private int temporadaActual= -1;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_equipos);

		temporadaActual= Preferencias.getPreferenciaInt(getApplicationContext(), Constantes.PREFERENCIAS_TEMPORADA_ACTUAL, -1);    	
        
	    ListView lista = (ListView) findViewById(android.R.id.list);
	    registerForContextMenu(lista);

	    ToggleButton b= (ToggleButton) findViewById(R.id.boton1);
	    b.setChecked(true);
        actualizarListaEquipos(temporadaActual);
    }
    
	public void boton_division(View v) 
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
		
        SQLiteDatabase con= Basedatos.getConexion(this, Basedatos.LECTURA);
        EquipoDao eqDao= new EquipoDao(con);       
        ArrayList<Equipo> eqs= eqDao.getEquipos(temporada, division, Preferencias.getUsuarioId(getApplicationContext()));
        Basedatos.cerrarConexion(con);  //[jm] con.close();
                
        id_elemsLista= new ArrayList<String>();
        m_equipos= new ArrayList<ObjetoIconoTexto>();
        for (Equipo eq: eqs)
        {
        	id_elemsLista.add(eq.getId());
        	m_equipos.add(new ObjetoIconoTexto(eq.getId(), eq.getNombre()));
        }
        
        this.m_adaptador = new AdaptadorEquipo(this, R.layout.cada_equipo, m_equipos);
        setListAdapter(this.m_adaptador);        
	}
    
	private class AdaptadorEquipo extends ArrayAdapter<ObjetoIconoTexto> 
	{
        private ArrayList<ObjetoIconoTexto> items;

        public AdaptadorEquipo(Context context, int textViewResourceId, ArrayList<ObjetoIconoTexto> items) 
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
                    v = vi.inflate(R.layout.cada_equipo, null);
                }
                ObjetoIconoTexto o = items.get(position);
                if (o != null) 
                {
                	ImageView img= (ImageView) v.findViewById(R.id.icono_equipo);
                	if (img != null)
                		img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("esc_"+o.getIcono(), "drawable", getPackageName())));
                    TextView tt = (TextView) v.findViewById(R.id.nombre_equipo);
                    if (tt != null)
                        tt.setText(o.getTexto());
                }
                return v;
        }
	}	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
	  super.onListItemClick(l, v, position, id);
      accionConCadaEquipo(position);
	}
	
    @Override    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
      super.onCreateContextMenu(menu, v, menuInfo);
      final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;      
      accionConCadaEquipo(new Long(info.id).intValue());
    }

    public void accionConCadaEquipo(final int pos) 
    {
      final int[] itemsId = {MENU_RESULTADOS, MENU_ESTADISTICAS};
      final CharSequence[] itemsDesc = {"Resultados", "Estadísticas"};
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	  ObjetoIconoTexto selec= m_equipos.get(pos);
      builder.setTitle(selec.getTexto());
      builder.setItems(itemsDesc, new DialogInterface.OnClickListener() 
      {
          public void onClick(DialogInterface dialog, int item) 
          {
              int iPos= pos;
        	  String id= id_elemsLista.get(iPos);
        	  ObjetoIconoTexto obj= m_equipos.get(iPos);
        	  
        	  if (itemsId[item] == MENU_RESULTADOS)
        	  {
	           	   Intent i = new Intent(getApplicationContext(), EquipoResultados.class);
	           	   i.putExtra(Constantes.EQUIPO_SELEC_ID, id);
	           	   i.putExtra(Constantes.EQUIPO_SELEC_NOMBRE, obj.getTexto());
	       		   startActivityForResult(i, 0);
        	  }
        	  else if (itemsId[item] == MENU_ESTADISTICAS)
        	  {
	       	   	   Intent i = new Intent(getApplicationContext(), EquipoEstadisticas.class);
	       		   i.putExtra(Constantes.EQUIPO_SELEC_ID, id);
	       		   i.putExtra(Constantes.EQUIPO_SELEC_NOMBRE, obj.getTexto());
	       		   startActivityForResult(i, 0);
        	  }
          }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }
    
}
