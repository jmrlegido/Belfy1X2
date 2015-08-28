package jm.q1x2.transobj;

import java.io.Serializable;

public class Partido implements Serializable 
{
	private static final long serialVersionUID = -5013674212414473944L;
	
	private String idEquipoLocal;
	private String idEquipoVisit;
	private String nombreEquipoLocal;
	private String nombreEquipoVisit;
	private int localGoles;
	private int visitGoles;
	private int jornada;
	private int temporada;
	private int division;
	private int resultadoQuiniela;  // posibles valores: constantes QuinielaOp.RES_......
	private int partidoNum;
	
	public Partido() {}
	public Partido(String l, String v)
	{
		idEquipoLocal= l;
		idEquipoVisit= v;
	}
	
	public String getIdEquipoLocal() {
		return idEquipoLocal;
	}
	public void setIdEquipoLocal(String idEquipoLocal) {
		this.idEquipoLocal = idEquipoLocal;
	}
	public String getIdEquipoVisit() {
		return idEquipoVisit;
	}
	public void setIdEquipoVisit(String idEquipoVisit) {
		this.idEquipoVisit = idEquipoVisit;
	}
	public int getLocalGoles() {
		return localGoles;
	}
	public void setLocalGoles(int localGoles) {
		this.localGoles = localGoles;
	}
	public int getVisitGoles() {
		return visitGoles;
	}
	public void setVisitGoles(int visitGoles) {
		this.visitGoles = visitGoles;
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
	public String getNombreEquipoLocal() {
		return nombreEquipoLocal;
	}
	public void setNombreEquipoLocal(String nombreEquipoLocal) {
		this.nombreEquipoLocal = nombreEquipoLocal;
	}
	public String getNombreEquipoVisit() {
		return nombreEquipoVisit;
	}
	public void setNombreEquipoVisit(String nombreEquipoVisit) {
		this.nombreEquipoVisit = nombreEquipoVisit;
	}
	public int getResultadoQuiniela() {
		return resultadoQuiniela;
	}
	public void setResultadoQuiniela(int resultadoQuiniela) {
		this.resultadoQuiniela = resultadoQuiniela;
	}
	public int getPartidoNum() {
		return partidoNum;
	}
	public void setPartidoNum(int partidoNum) {
		this.partidoNum = partidoNum;
	}
	public int getDivision() {
		return division;
	}
	public void setDivision(int division) {
		this.division = division;
	}
	
}
