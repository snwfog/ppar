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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunnyd.annotations.ActiveRelationHasOne;
import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.annotations.ActiveRecordField;
import com.sunnyd.database.Manager;

import org.apache.commons.lang3.StringUtils;

public class BaseHelper {

    //Get classObject attribute and value
    static Map<String, Object> getTableFieldNameAndValue(Object classObject) {
        return getTableFieldNameAndValue(classObject.getClass(), classObject);
    }
    
    //For inheritance, get parent field and value and invoke on object fields
    static Map<String, Object> getTableFieldNameAndValue(Class<?> parentClass, Object classObject) {
        Field[] classFields = parentClass.getDeclaredFields();
        Map<String, Object> tableAttributes = new HashMap<String, Object>();
        for (int i = 0; i < classFields.length; i++) {
            Field field = classFields[i];
            Annotation attrAnnotation = field.getAnnotation(ActiveRecordField.class);
            if (attrAnnotation != null) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(classObject);
                    tableAttributes.put(field.getName(), value);
                } catch (IllegalArgumentException | SecurityException | IllegalAccessException  e) {
                    e.printStackTrace();
                }
            }
        }
        return tableAttributes;
    }


    static Map<String, Object> getSuperDatas(Integer id, Class<?> classObject){
        Map<String,Object> parentResult = null;
        if(classObject.getAnnotation(ActiveRecordInheritFrom.class) != null ){
            //Get Parent class datas
            parentResult = Manager.find(id, BaseHelper.getClassTableName(classObject.getSuperclass().getName()));
            
            //if get Parent's Parent datas
            Map<String, Object> call = getSuperDatas((Integer)parentResult.get("id"), classObject.getSuperclass());
            if( call !=null){
                parentResult.putAll(call);
            }
            
        }
        return parentResult;
    }

    public static String getClassTableName(Class<?> classObject) {
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
    
    
    static String getGenericCanonicalClassName(Field arrayListField){
      //Get object class from Arraylist or list generic type
        String genericClass = arrayListField.toGenericString();
        Pattern classPattern = Pattern.compile("<.*>");
        Matcher m = classPattern.matcher(genericClass);
        while(m.find()){
            String temp = m.group();
            return m.group().substring(1, temp.length()-1);        
        }

        
        return null;
    }
    
    
    static String getGenericSimpleName(Field arrayListField){
        Pattern classPattern = Pattern.compile("\\w[a-zA-z]*$");
        Matcher m = classPattern.matcher(getGenericCanonicalClassName(arrayListField));
        
        while(m.find()){
            return m.group();      
        }
        
        return null;
    }
    
    static String annotationSimpleName(Annotation  a){
        return a.annotationType().getSimpleName();
    }

}
