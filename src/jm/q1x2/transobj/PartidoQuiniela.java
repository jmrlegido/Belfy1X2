package jm.q1x2.transobj;

public class PartidoQuiniela 
{
	private String eq1;
	private String eq2;
	private int resultado1x2;
	private int resIdRivales;
	private int resId1;
	private int resIdx;
	private int resId2;
	private boolean finalizado;
	private boolean acertado;
	
	
	public PartidoQuiniela()
	{}
	
	public PartidoQuiniela(String _eq1, String _eq2, int _resIdRivales)
	{
		eq1= _eq1;
		eq2= _eq2;
		resIdRivales= _resIdRivales;
	}
	
	public PartidoQuiniela(String _eq1, String _eq2, int _resultado1x2, int _resIdRivales, int _resId1, int _resIdx, int _resId2)
	{
		eq1= _eq1;
		eq2= _eq2;
		resultado1x2= _resultado1x2;
		resIdRivales= _resIdRivales;
		resId1= _resId1;
		resIdx= _resIdx;
		resId2= _resId2;
	}

	
	public String getEq1() {
		return eq1;
	}

	public void setEq1(String eq1) {
		this.eq1 = eq1;
	}

	public String getEq2() {
		return eq2;
	}

	public void setEq2(String eq2) {
		this.eq2 = eq2;
	}

	public int getResultado1x2() {
		return resultado1x2;
	}

	public void setResultado1x2(int resultado1x2) {
		this.resultado1x2 = resultado1x2;
	}

	public int getResIdRivales() {
		return resIdRivales;
	}

	public void setResIdRivales(int resIdRivales) {
		this.resIdRivales = resIdRivales;
	}

	public int getResId1() {
		return resId1;
	}

	public void setResId1(int resId1) {
		this.resId1 = resId1;
	}

	public int getResIdx() {
		return resIdx;
	}

	public void setResIdx(int resIdx) {
		this.resIdx = resIdx;
	}

	public int getResId2() {
		return resId2;
	}

	public void setResId2(int resId2) {
		this.resId2 = resId2;
	}

	public boolean estaAcertado() {
		return acertado;
	}

	public void ponerAcertado(boolean b) {
		this.acertado = b;
	}

	public boolean esFinalizado() {
		return finalizado;
	}

	public void ponerFinalizado(boolean b) {
		this.finalizado = b;
	}

	
}
