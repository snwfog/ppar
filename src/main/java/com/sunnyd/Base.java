package com.sunnyd;

import com.sunnyd.annotations.*;
import com.sunnyd.database.Manager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Base
{
	@tableAttr
	private Integer id;
	
	private Boolean updateFlag = false;
	
    public Base(HashMap<String, Object> HM)
    {
        this.setId((Integer)HM.get("id"));
        
        //Get Caller ClassName
        String className = getClassName();
        Class<?> classObject = this.getClass();
        //Get all object fields

        
        Field[] fields = classObject.getDeclaredFields();
        for (Field field : fields)
        {
        	Annotation tableAttr = field.getAnnotation(tableAttr.class);

        	if(tableAttr != null){
        		
	            String fieldName = field.getName();
	            Class<?> fieldType = field.getType();
	            if(HM.containsKey(fieldName)){
		            Object value = HM.get(fieldName);
		            String capitalizeField = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		
		            java.lang.reflect.Method method;
	                try
	                {                	
	                    method = classObject.getDeclaredMethod("set" + capitalizeField, fieldType);
		                method.invoke(this, fieldType.cast(value));
	                }
	                catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
	                {
	                	e.printStackTrace();
	                    break;// If method does not have setMethod then it is not a db Attribute
	                }

	            }
        	}
        }
    }

    
    @SuppressWarnings("unchecked")
    public static <T> T find(int id)
    {
        //Since this is a static method, to get caller of method we must look in the stack trace to get class
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        //At this point stack should look like this [java.lang.Thread.getStackTrace(Unknown Source), com.sunnyd.Base.find(Base.java:79), com.sunnyd.models.Person.main(Person.java:20), .....so on]
        //TODO:Need a better solution than stack to get caller class
        String className = ste[2].getClassName();
        try
        {
        	String tableName = getClassDBTableName(className);
        	HashMap<String, Object> HM = Manager.find(id, tableName); 
        	if (HM == null)
	        {
	            return null;
	        }
            return (T) Class.forName(className).getConstructor(HashMap.class).newInstance(HM);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static <T> T findAll(){
    	return null;
    }
    
    public boolean update(){
    	if(getUpdateFlag()){
	    	HashMap<String, Object> updateAttributes = new HashMap<String, Object>();
	    	Field[] classFields = this.getClass().getDeclaredFields();
	    	for(Field field : classFields){
	    		Annotation tableAttr = field.getAnnotation(tableAttr.class);
	    		if(tableAttr != null){
	    			try {
	    				String fieldName = field.getName();
	    				java.lang.reflect.Method method;
	    				method = this.getClass().getDeclaredMethod("get" + capitalize(fieldName));
						updateAttributes.put(fieldName, method.invoke(this));
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
						e.printStackTrace();
					}
	    		}
	    	}
	    	
	    	this.setUpdateFlag(false);
	    	Manager.update(this.getId(), getClassDBTableName(getClassName()), updateAttributes);
    	}
    	
    	return true;
    }
    
    public Boolean Destroy(){
    	return Manager.destroy(this.getId(), getClassDBTableName(getClassName()));
    }
    
    /******MUTATOR****************************************************/
    //Must have Mutator in all child class.
    
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
    
    public Boolean getUpdateFlag(){
    	return this.updateFlag;
    }
    
    public void setUpdateFlag(Boolean flag){
    	this.updateFlag = flag;
    }
	
	/********Private***********************************************/
    private String getClassName(){
    	return this.getClass().getName();
    }
    
    private static String getClassDBTableName(String className){
    	String name = null;
    	try {
			name = Class.forName(className).getDeclaredField("tableName").get(null).toString();
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    	return name;
    }
    
    
    private static String capitalize(String s){
    	return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}

