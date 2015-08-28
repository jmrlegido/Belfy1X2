package jm.q1x2.transobj.vista;

public class ObjetoIconoTextoNumero 
{
	private String icono;
	private String texto;
	private int numero;

	public ObjetoIconoTextoNumero(String i, String t, int n)
	{
		icono= i;
		texto= t;
		numero= n;
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

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}
}
