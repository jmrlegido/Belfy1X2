package jm.q1x2.transobj;

import java.io.Serializable;

public class FactoresPesos implements Serializable
{
	private static final long serialVersionUID = -301486746439109365L;
	
	private int aleatoriedad ; 
	private int calidad_intrinseca;
	private int factor_campo; 
	private int golaveraje; 
	private int golaveraje_localvisit; 
	private int puntos_partido; 
	private int puntos_partido_localvisit; 
	private int ultimos4; 
	private int ultimos4_localvisit;
	
	public int getAleatoriedad() {
		return aleatoriedad;
	}
	public void setAleatoriedad(int aleatoriedad) {
		this.aleatoriedad = aleatoriedad;
	}
	public int getCalidadIntrinseca() {
		return calidad_intrinseca;
	}
	public void setCalidadIntrinseca(int calidad_intrinseca) {
		this.calidad_intrinseca = calidad_intrinseca;
	}
	public int getFactorCampo() {
		return factor_campo;
	}
	public void setFactorCampo(int factor_campo) {
		this.factor_campo = factor_campo;
	}
	public int getGolaveraje() {
		return golaveraje;
	}
	public void setGolaveraje(int golaveraje) {
		this.golaveraje = golaveraje;
	}
	public int getGolaverajeLocalVisit() {
		return golaveraje_localvisit;
	}
	public void setGolaverajeLocalVisit(int golaveraje_localvisit) {
		this.golaveraje_localvisit = golaveraje_localvisit;
	}
	public int getPuntosPartido() {
		return puntos_partido;
	}
	public void setPuntosPartido(int puntos_partido) {
		this.puntos_partido = puntos_partido;
	}
	public int getPuntosPartidoLocalVisit() {
		return puntos_partido_localvisit;
	}
	public void setPuntosPartidoLocalVisit(int puntos_partido_localvisit) {
		this.puntos_partido_localvisit = puntos_partido_localvisit;
	}
	public int getUltimos4() {
		return ultimos4;
	}
	public void setUltimos4(int ultimos4) {
		this.ultimos4 = ultimos4;
	}
	public int getUltimos4LocalVisit() {
		return ultimos4_localvisit;
	}
	public void setUltimos4LocalVisit(int ultimos4_localvisit) {
		this.ultimos4_localvisit = ultimos4_localvisit;
	}

}
