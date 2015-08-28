package jm.q1x2.transobj;

import java.util.ArrayList;

public class Jornada 
{
	private int temporada;
	private int division;
	private int jornadaNum;
	private ArrayList<Partido> partidos= new ArrayList<Partido>();
	
	public Jornada(int _temporada, int jorNum)
	{
		temporada= _temporada;
		jornadaNum= jorNum;
	}
	public Jornada(int _temporada, int div, int jorNum)
	{
		temporada= _temporada;
		division= div;
		jornadaNum= jorNum;
	}
	
	public int getTemporada() {
		return temporada;
	}
	public void setTemporada(int temp) {
		this.temporada = temp;
	}
	public int getDivision() {
		return division;
	}
	public void setDivision(int division) {
		this.division = division;
	}
	public int getJornadaNum() {
		return jornadaNum;
	}
	public void setJornadaNum(int jornadaNum) {
		this.jornadaNum = jornadaNum;
	}
	public ArrayList<Partido> getPartidos() {
		return partidos;
	}
	public void setPartidos(ArrayList<Partido> partidos) {
		this.partidos = partidos;
	}
	
	public void anadirPartido(Partido par)
	{
		this.partidos.add(par);
	}
}
