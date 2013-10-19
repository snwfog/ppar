package com.sunnyd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sunnyd.annotations.ActiveRelationHasOne;
import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.annotations.ActiveRecordField;
import com.sunnyd.database.Manager;
import org.codehaus.plexus.util.StringUtils;

public class BaseHelper {
    
    public static void main(String[] args) {
        BaseHelper a = new BaseHelper();
        System.out.println(BaseHelper.getClassTableName(a.getClass()));
    }

    static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    static String unCapitalize(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    //Get classObject attribute and value
    static HashMap<String, Object> getTableFieldNameAndValue(Object classObject) {
        return getTableFieldNameAndValue(classObject.getClass(), classObject);
    }
    
    //For inheritance, get parent field and value and invoke on object fields
    static HashMap<String, Object> getTableFieldNameAndValue(Class<?> parentClass, Object classObject) {
        Field[] classFields = parentClass.getDeclaredFields();
        HashMap<String, Object> tableAttributes = new HashMap<String, Object>();
        for (int i = 0; i < classFields.length; i++) {
            Field field = classFields[i];
            Annotation attrAnnotation = field.getAnnotation(ActiveRecordField.class);
            if (attrAnnotation != null) {
                try {
                    String fieldName = field.getName();
                    Method method = parentClass.getDeclaredMethod("get" + StringUtils.capitalise(fieldName));
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
                String capitalizeField = capitalize(fieldName);
                
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
       if(classObject.getAnnotation(ActiveRecordInheritFrom.class) !=null){
           setAttributes(classObject.getSuperclass(), instanceObject, data);
       }
        
    }

    static HashMap<String, Object> getSuperDatas(Integer id, Class<?> classObject){
        HashMap<String,Object> parentResult = null;
        if(classObject.getAnnotation(ActiveRecordInheritFrom.class) != null ){
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

    static String getClassTableName(Class<?> classObject) {
        String className = classObject.getSimpleName();
        return getClassTableName(className);    
    }
    
    static String getClassTableName(String className){
        if(className.contains(".")){
            String[] canonicalNameSplit = className.split("\\.");
            className = canonicalNameSplit[canonicalNameSplit.length-1];
        } 
        className = Manager.toUnderscoreCase(className);
        
        //suffix concat to ending of string
        Map<String, String> addSuffixes = new HashMap<String, String>();
        
        //suffixes replaces the ending of string
        Map<String, String> replaceSuffixes = new HashMap<String, String>();
        
        //Add suffix here
        addSuffixes.put("es", "s,x,z,ch,sh");
        replaceSuffixes.put("ies", "y");
        
        //Concat suffix
        Iterator<Entry<String, String>> iter = addSuffixes.entrySet().iterator();
        String name = processSuffix(iter, className, false);
        if(className != name){
            return name;
        }
        
        //replace with suffix
        iter = replaceSuffixes.entrySet().iterator();
        name = processSuffix(iter, className, true);
        if(className != name){
            return name;
        }
       
        //Default
        return className+"s";
    }
    
    private static String processSuffix(Iterator<Map.Entry<String,String>> iter, String name, boolean replace){
        while (iter.hasNext()){
            Map.Entry<String, String> pairs = (Map.Entry<String, String>)iter.next();
            for(String ending : pairs.getValue().split(",")){
                if(name.endsWith(ending)){
                       String suffix = pairs.getKey();
                       name = replace ? name.substring(0, name.length()-ending.length()).concat(suffix) : name+suffix;
                       return name ;
                }
            }
        }
        return name;
    }

    
    static Field[] getTableField(Class<?> classObject) {
        Field[] classFields = classObject.getDeclaredFields();
        List<Field> tableAttributes = new ArrayList<Field>();
        for (int i = 0; i < classFields.length; i++) {
            Field field = classFields[i];
            Annotation attrAnnotation = field.getAnnotation(ActiveRecordField.class);
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
   
    
    
    // given a caller class name, returns all the @hasOne attributes in that class
    static Field[] getHasOneField(Class<?> classObject) {
        Field[] classFields = classObject.getDeclaredFields();
        List<Field> tableAttributes = new ArrayList<Field>();
        for (int i = 0; i < classFields.length; i++) {
            Field field = classFields[i];
            Annotation attrAnnotation = field.getAnnotation(ActiveRelationHasOne.class);
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
