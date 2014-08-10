package jm.q1x2.activities;

import java.util.ArrayList;

import jm.q1x2.R;
import jm.q1x2.bbdd.Basedatos;
import jm.q1x2.bbdd.dao.ConfigDao;
import jm.q1x2.bbdd.dao.QuinielaDao;
import jm.q1x2.logneg.QuinielaOp;
import jm.q1x2.transobj.FactoresPesos;
import jm.q1x2.transobj.PatronFactoresPesos;
import jm.q1x2.utils.Mensajes;
import jm.q1x2.utils.Preferencias;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QuinielaCargarPatron extends ListActivity 
{
	private ArrayList<Integer> id_elemsLista;
    private ArrayList<String> m_patrones= new ArrayList<String>();
    private AdaptadorEquipo m_adaptador;	

	private int MENU_ELIMINAR= 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_quinielas_patrones);
            
		SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.LECTURA);
        actualizarListaPatrones(con);
        con.close();

	    ListView lista = (ListView) findViewById(android.R.id.list);	    
	    registerForContextMenu(lista);
	    lista.setTextFilterEnabled(true);
	    lista.setOnItemClickListener(new OnItemClickListener() 
	    {
	      public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	      {
			   Integer idPatron= id_elemsLista.get(position);
			   String nomPatron= m_patrones.get(position);
			   
       		   SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);
       		   QuinielaDao qDao= new QuinielaDao(con);
       		   ConfigDao configDao= new ConfigDao(con);
       		   FactoresPesos f= qDao.getFactoresDePatron(idPatron.intValue());        		
       		   configDao.actualizarParamsConfigQuinielas(Preferencias.getUsuarioId(getApplicationContext()), f);
       		   con.close();			   
       		   
	    	   Mensajes.alerta(getApplicationContext(), "Patrón cargado: " + nomPatron);
	    	   finish();
	      }
	    });	    
    }    
        
	private void actualizarListaPatrones(SQLiteDatabase con)
	{
		QuinielaOp qOp= new QuinielaOp();
        ArrayList<PatronFactoresPesos> pats= qOp.getPatronesUsuarioIdNombre(Preferencias.getUsuarioId(getApplicationContext()), con);
        if (pats.size() > 0)
        {
			id_elemsLista= new ArrayList<Integer>();             
	        m_patrones= new ArrayList<String>();
	        for (PatronFactoresPesos pat : pats) 
	        {
				id_elemsLista.add(pat.getId());
	        	m_patrones.add(new String(pat.getNombre()));
	        }
	        this.m_adaptador = new AdaptadorEquipo(this, R.layout.lay_elemlista_desc, m_patrones);
	        setListAdapter(this.m_adaptador);
        }
        else
        {
        	Mensajes.alerta(getApplicationContext(), "Este usuario no tiene ningún patrón grabado.");
        	finish();
        }
	}
	
	private class AdaptadorEquipo extends ArrayAdapter<String> 
	{
        private ArrayList<String> items;

        public AdaptadorEquipo(Context context, int textViewResourceId, ArrayList<String> items) 
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
                    v = vi.inflate(R.layout.lay_elemlista_desc, null);
                }
                String nombre = items.get(position);
                if (nombre != null) 
                {
                    TextView tt = (TextView) v.findViewById(R.id.descripcion);
                    //tt.setHeight(99);
                    if (tt != null)
                        tt.setText(nombre);
                }
                return v;
        }
	}		
    

    /*
     * menú contextual de los elementos de la lista
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
      super.onCreateContextMenu(menu, v, menuInfo);
      
      final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
      final int[] itemsId = {MENU_ELIMINAR};
      final CharSequence[] itemsDesc = {"Eliminar"};
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
  	  String selec= m_patrones.get(new Long(info.id).intValue());
      builder.setTitle(selec);
      builder.setItems(itemsDesc, new DialogInterface.OnClickListener() 
      {
          public void onClick(DialogInterface dialog, int item) 
          {
          	  Integer idPatron= id_elemsLista.get(new Long(info.id).intValue());
        	  
          	  if (itemsId[item] == MENU_ELIMINAR)
        	  {        		           		  
	       		   SQLiteDatabase con= Basedatos.getConexion(getApplicationContext(), Basedatos.ESCRITURA);
	       		   QuinielaOp qOp= new QuinielaOp();
	       		   qOp.borrarPatron(idPatron.intValue(), con);
	       		   actualizarListaPatrones(con);
	       		   con.close();	           	  
	       		   Mensajes.alerta(getApplicationContext(), "Patrón eliminado correctamente");
        	  }
          }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }
	
}
