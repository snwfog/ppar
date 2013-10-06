package com.sunnyd;

import com.sunnyd.annotations.*;
import com.sunnyd.database.Manager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/*****
 * 
 * 
 * @author Mike
 * All Children isa relation/children of a model primary key is the parent's primary key
 */

public class Base {

    @tableAttr
    private Integer id;

    private Boolean updateFlag = false;

    public Base() {

    }

    public Base(HashMap<String, Object> HM) {
        this.setId((Integer) HM.get("id"));

        // Get Caller ClassName
        Class<?> classObject = this.getClass();
        Base.setAttributes(classObject, this, HM);
  
        this.setUpdateFlag(false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T find(int id) {
        // Since this is a static method, to get caller of method we must look
        // in the stack trace to get class
        // At this point stack should look like this:
        // [java.lang.Thread.getStackTrace(Unknown Source), com.sunnyd.Base.find(Base.java:79), com.sunnyd.models.Person.main(Person.java:20), .....so on]
        // TODO:Need a better solution than stack to get caller class
        
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        String className = ste[2].getClassName();
        try {
            
            //Get class attribute from database
            String tableName = getClassTableName(className);
            HashMap<String, Object> HM = Manager.find(id, tableName);
            
            //Get inherited values from parent table
            HashMap<String, Object> parentDatas = getSuperDatas((Integer)HM.get("id"), Class.forName(className));
            if(parentDatas != null){
                HM.putAll(parentDatas);
            }
            
            return (T) Class.forName(className).getConstructor(HashMap.class).newInstance(HM);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T findAll() {
        return null;
    }

    public boolean update() {
        if(this.getUpdateFlag()) {
            HashMap<String, Object> updateAttributes = getTableAttributeNameAndValue(this);
            this.setUpdateFlag(false);
            return Manager.update(this.getId(), getTableName(), updateAttributes);
        }
        return false;
    }

    public Boolean Destroy() {
        return Manager.destroy(this.getId(), getTableName());
    }

    public boolean save() {
        if (this.getId() == null) {
            HashMap<String, Object> attrToPersist = Base.getTableAttributeNameAndValue(this);
            int id = Manager.save(getClassTableName(this.getClassName()), attrToPersist);
            if (id != 0) {
                this.setId(id);
                return true;
            }
        }
        return false;
    }

    
    /****** MUTATOR ****************************************************/
    // Common Mutator in all child class.

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getUpdateFlag() {
        return this.updateFlag;
    }

    public void setUpdateFlag(Boolean flag) {
        this.updateFlag = flag;
    }

    
    /******** Private ***********************************************/
    private static void setAttributes(Class<?> classObject, Object objectOfInterest, HashMap<String, Object> data){
        //Get all table attribute from this class
        Field[] fields = Base.getTableField(classObject);
        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            
            if (data.containsKey(fieldName)) {
                Object value = data.get(fieldName);
                String capitalizeField = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                java.lang.reflect.Method method;
                try {
                    method = classObject.getDeclaredMethod("set" + capitalizeField, fieldType);
                    method.invoke(objectOfInterest, fieldType.cast(value));
                } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    e.printStackTrace();
                    break;// If method does not have setMethod then it is
                          // not a db Attribute
                }

            }
        }
        
       //Verify if model inherit another model
       if(classObject.getAnnotation(inherit.class) !=null){
           setAttributes(classObject.getSuperclass(), objectOfInterest, data);
       }
        
    }
    
    private static HashMap<String, Object> getSuperDatas(Integer id, Class<?> classObject){
        HashMap<String,Object> parentResult = null;
        if(classObject.getAnnotation(inherit.class) != null ){
            //Get Parent class datas
            parentResult = Manager.find(id, getClassTableName(classObject.getSuperclass().getName()));
            
            //if get Parent's Parent datas
            HashMap<String, Object> call = getSuperDatas((Integer)parentResult.get("id"), classObject.getSuperclass());
            if( call !=null){
                parentResult.putAll(call);
            }
            
        }
        return parentResult;
    }
    
    
    private String getClassName() {
        return this.getClass().getName();
    }
    
    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String getClassTableName(String className) {
        String name = null;
        try {
            name = Class.forName(className).getDeclaredField("tableName").get(null).toString();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    private String getTableName() {
        try {
            return this.getClass().getDeclaredField("tableName").get(null).toString();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<String, Object> getTableAttributeNameAndValue(Object classObject) {
        Field[] classFields = classObject.getClass().getDeclaredFields();
        HashMap<String, Object> tableAttributes = new HashMap<String, Object>();
        for (int i = 0; i < classFields.length; i++) {
            Field field = classFields[i];
            Annotation attrAnnotation = field.getAnnotation(tableAttr.class);
            if (attrAnnotation != null) {
                try {
                    String fieldName = field.getName();
                    Method method = classObject.getClass().getDeclaredMethod("get" + capitalize(fieldName));
                    Object value = method.invoke(classObject);
                    tableAttributes.put(field.getName(), value);
                } catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableAttributes;
    }

    private static Field[] getTableField(Class<?> classObject) {
        Field[] classFields = classObject.getDeclaredFields();
        List<Field> tableAttributes = new ArrayList<Field>();
        for (int i = 0; i < classFields.length; i++) {
            Field field = classFields[i];
            Annotation attrAnnotation = field.getAnnotation(tableAttr.class);
            if (attrAnnotation != null) {
                try {
                    tableAttributes.add(field);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return tableAttributes.toArray(new Field[tableAttributes.size()]);
    }
}
