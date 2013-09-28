package bean;

public class Entity {
	
	private String property1;
	
	public Entity(){
		super();
	}
	
	public Entity(String name){
		property1 = name;
	}
	
	public String getProperty1(){
		return property1;
	}
	
	public void setProperty1(String property1){
		this.property1 = property1;
	}

}
