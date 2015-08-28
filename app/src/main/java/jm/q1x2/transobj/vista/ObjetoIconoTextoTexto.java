package jm.q1x2.transobj.vista;

public class ObjetoIconoTextoTexto 
{
	private String icono;
	private String texto1;
	private String texto2;

	public ObjetoIconoTextoTexto(String i, String t1, String t2)
	{
		icono= i;
		texto1= t1;
		texto2= t2;
	}
	
	public String getIcono() {
		return icono;
	}
	public void setIcono(String icono) {
		this.icono = icono;
	}
	public String getTexto1() {
		return texto1;
	}
	public void setTexto1(String texto) {
		this.texto1 = texto;
	}
	public String getTexto2() {
		return texto2;
	}
	public void setTexto2(String texto) {
		this.texto2 = texto;
	}
}
