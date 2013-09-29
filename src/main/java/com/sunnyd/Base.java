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
    String className = this.getClass().getName();

    //GetClass
//		Class.forName(className)

    try
    {
      Field[] fields = Class.forName(className).getDeclaredFields();

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
            method = Class.forName(className).getDeclaredMethod("set" + capitalizeField, field.getType());
          }
          catch (NoSuchMethodException e)
          {
            break;// if method is not a setter or a Getter
          }

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
    catch (SecurityException | ClassNotFoundException e)
    {
      e.printStackTrace();
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

