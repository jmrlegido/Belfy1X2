package jm.q1x2.transobj;

public class Usuario 
{
	private int id;
	private String nombre;
	
	public Usuario(int c, String n)
	{
		id= c;
		nombre= n;
	}
	public Usuario(String n)
	{
		nombre= n;
	}
	
	public int getId() 
	{
		return id;
	}
	public void setId(int codigo) 
	{
		this.id = codigo;
	}
	
	public String getNombre() 
	{
		return nombre;
	}
	public void setNombre(String nombre) 
	{
		this.nombre = nombre;
	}
	
	@Override
	public String toString() 
	{
		return nombre;
	}
	
}
