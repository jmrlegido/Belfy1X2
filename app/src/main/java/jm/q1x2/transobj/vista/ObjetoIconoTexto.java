package jm.q1x2.transobj.vista;

public class ObjetoIconoTexto 
{
	private String icono;
	private String texto;

	public ObjetoIconoTexto(String i, String t)
	{
		icono= i;
		texto= t;
	}
	
	public String getIcono() {
		return icono;
	}
	public void setIcono(String icono) {
		this.icono = icono;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
}
