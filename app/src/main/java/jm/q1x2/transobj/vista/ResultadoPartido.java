package jm.q1x2.transobj.vista;

import jm.q1x2.transobj.Partido;

public class ResultadoPartido 
{
	int jornada;
	String eqLocal;
	String eqVisit;
	int golesLocal;
	int golesVisit;
	
	public ResultadoPartido(Partido par)
	{
		jornada= par.getJornada();
		eqLocal= par.getNombreEquipoLocal();
		eqVisit= par.getNombreEquipoVisit();
		golesLocal= par.getLocalGoles();
		golesVisit= par.getVisitGoles();
	}
	
	public int getJornada() {
		return jornada;
	}
	public void setJornada(int jornada) {
		this.jornada = jornada;
	}
	public String getEqLocal() {
		return eqLocal;
	}
	public void setEqLocal(String eqLocal) {
		this.eqLocal = eqLocal;
	}
	public String getEqVisit() {
		return eqVisit;
	}
	public void setEqVisit(String eqVisit) {
		this.eqVisit = eqVisit;
	}
	public int getGolesLocal() {
		return golesLocal;
	}
	public void setGolesLocal(int golesLocal) {
		this.golesLocal = golesLocal;
	}
	public int getGolesVisit() {
		return golesVisit;
	}
	public void setGolesVisit(int golesVisit) {
		this.golesVisit = golesVisit;
	}
}
