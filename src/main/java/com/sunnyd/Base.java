package com.sunnyd;

import com.sunnyd.annotations.Method;
import com.sunnyd.database.Manager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


public class Base
{

    public Base(HashMap<Object, Object> HM)
    {
        //Get Caller ClassName
        String className = this.getClass().getName();

        //Instantiate Current Class
        Class<?> classObject = null;
        try {
            classObject = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Get all object fields
        Field[] fields = classObject.getDeclaredFields();
        for (Field field : fields)
        {
            String fieldName = field.getName();
            Object value = HM.get(fieldName);
            String capitalizeField = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            java.lang.reflect.Method method;
            try
            {
                try
                {
                    /*Expect all DB attribute to have mutator methods and is named
                        getAttributeName
                        setAttributeName
                        ex: attribute id
                            getId()
                            setId()
                     */
                    method = classObject.getDeclaredMethod("set" + capitalizeField, field.getType());
                }
                catch (NoSuchMethodException e)
                {
                    break;// If method does not have setMethod then it is not a db Attribute
                }

                //2nd Verication: Verify method belong to a dbAttribute using annotaitons
                //TODO verify SOLUTION 1: all getter Setter method = setDBFirstName or use annotations
                Annotation ARMethod = method.getAnnotation(Method.class);

                method.invoke(this, value);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T find(int id)
    {
        HashMap<Object, Object> HM = new Manager().find(id);
        if (HM == null)
        {
            return null;
        }

        //Since this is a static method, to get caller of method we must look in the stack trace to get class
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        String className = ste[ste.length - 1].getClassName();
        try
        {
            return (T) Class.forName(className).getConstructor(HashMap.class).newInstance(HM);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
        {
            e.printStackTrace();
        }

        return null;
    }


}

