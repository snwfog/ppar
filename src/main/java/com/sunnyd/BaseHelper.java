package com.sunnyd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sunnyd.annotations.hasOne;
import com.sunnyd.annotations.inherit;
import com.sunnyd.annotations.tableAttr;
import com.sunnyd.database.Manager;

public class BaseHelper {

    static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    static HashMap<String, Object> getTableAttributeNameAndValue(Object classObject) {
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
    
    static HashMap<String, Object> getTableAttributeNameAndValue(Class<?> parentClass, Object classObject) {
        Field[] classFields = parentClass.getDeclaredFields();
        HashMap<String, Object> tableAttributes = new HashMap<String, Object>();
        for (int i = 0; i < classFields.length; i++) {
            Field field = classFields[i];
            Annotation attrAnnotation = field.getAnnotation(tableAttr.class);
            if (attrAnnotation != null) {
                try {
                    String fieldName = field.getName();
                    Method method = parentClass.getDeclaredMethod("get" + capitalize(fieldName));
                    Object value = method.invoke(classObject);
                    tableAttributes.put(field.getName(), value);
                } catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableAttributes;
    }


    static void setAttributes(Class<?> classObject, Object instanceObject, HashMap<String, Object> data){
        //Get all table attribute from this class
        Field[] fields = BaseHelper.getTableField(classObject);
        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            
            if (data.containsKey(fieldName)) {
                Object value = data.get(fieldName);
                String capitalizeField = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                
                java.lang.reflect.Method method;
                try {
                    method = classObject.getDeclaredMethod("set" + capitalizeField, fieldType);
                    method.invoke(instanceObject, fieldType.cast(value));
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
           setAttributes(classObject.getSuperclass(), instanceObject, data);
       }
        
    }

    static HashMap<String, Object> getSuperDatas(Integer id, Class<?> classObject){
        HashMap<String,Object> parentResult = null;
        if(classObject.getAnnotation(inherit.class) != null ){
            //Get Parent class datas
            parentResult = Manager.find(id, BaseHelper.getClassTableName(classObject.getSuperclass().getName()));
            
            //if get Parent's Parent datas
            HashMap<String, Object> call = getSuperDatas((Integer)parentResult.get("id"), classObject.getSuperclass());
            if( call !=null){
                parentResult.putAll(call);
            }
            
        }
        return parentResult;
    }

    static String getClassTableName(String className) {
        String name = null;
        try {
            name = Class.forName(className).getDeclaredField("tableName").get(null).toString();
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    static Field[] getTableField(Class<?> classObject) {
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
    
    
    ///harry
    
    
    // given a caller class name, returns all the @hasOne attributes in that class
    static Field[] getHasOneField(Class<?> classObject) {
        Field[] classFields = classObject.getDeclaredFields();
        List<Field> tableAttributes = new ArrayList<Field>();
        for (int i = 0; i < classFields.length; i++) {
            Field field = classFields[i];
            Annotation attrAnnotation = field.getAnnotation(hasOne.class);
            if (attrAnnotation != null) {
                try {
                    tableAttributes.add(field);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableAttributes.toArray(new Field[tableAttributes.size()]);
    }

}
