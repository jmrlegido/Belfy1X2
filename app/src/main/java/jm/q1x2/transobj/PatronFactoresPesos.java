package jm.q1x2.transobj;

public class PatronFactoresPesos 
{
	private int id;
	private String nombre;
	private FactoresPesos factores;
	
	public PatronFactoresPesos()
	{}
	
	public PatronFactoresPesos(String n, FactoresPesos f)
	{
		this.nombre= n;
		this.factores= f;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String n) {
		this.nombre = n;
	}
	public FactoresPesos getFactoresPesos() {
		return factores;
	}
	public void setFactoresPesos(FactoresPesos f) {
		this.factores = f;
	}

}
