package jm.q1x2.transobj;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiniela implements Serializable
{
	private static final long serialVersionUID = 8298495993488241919L;
	
	public static int QUINIELA_CORREGIDA_NO = 0;
	public static int QUINIELA_CORREGIDA_SI = 1;
	public static int QUINIELA_CORREGIDA_PARCIAL = 2;
	
	/*
	 *  ACIERTOS_VALOR_PARA_NO_CALCULADO  es el valor por defecto del campo 'aciertos' de la tabla 'quinielas'.
	 *  Si getAciertos() devuelve este valor, significa que aún no se ha calculado su valor.
	 */
	public static int ACIERTOS_VALOR_PARA_NO_CALCULADO= -1;   
	
	private int id;
	private String nombre;
	private int jornada;
	private int temporada;
	private int corregida;
	private ArrayList<Partido> partidos= new ArrayList<Partido>();
	private int aciertos;

	public Quiniela()
	{}

	public Quiniela(int _temporada, int jorNum)
	{
		temporada= _temporada;
		jornada= jorNum;
	}	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public ArrayList<Partido> getPartidos() {
		return partidos;
	}

	public void setPartidos(ArrayList<Partido> partidos) {
		this.partidos = partidos;
	}
	
	public void annadirPartido(Partido par)
	{
		partidos.add(par);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getJornada() {
		return jornada;
	}
	public void setJornada(int jornada) {
		this.jornada = jornada;
	}
	public int getTemporada() {
		return temporada;
	}
	public void setTemporada(int temporada) {
		this.temporada = temporada;
	}
	public int getCorregida() {
		return corregida;
	}
	public void setCorregida(int _cor) {
		this.corregida = _cor;
	}

	public int getAciertos() {
		return this.aciertos;
	}

	public void setAciertos(int ac) {
		this.aciertos = ac;
	}

	public boolean haSidoCalculadoElNumeroDeAciertos()
	{
		return this.getAciertos() != ACIERTOS_VALOR_PARA_NO_CALCULADO;
	}
}
