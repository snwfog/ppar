package com.sunnyd.database.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.Base;
import com.sunnyd.annotations.ActiveRecordField;
import com.sunnyd.models.Person;

import java.lang.reflect.Field;

public class FunnelFactory {
    public static <T extends Base> Funnel<T> getInstance(Class<T> klazz)
    {
        Funnel<T> funnel = new Funnel<T>()
        {
            @Override
            public void funnel( T instance, PrimitiveSink into ) {
                // Get all the fields
                Field[] fields = instance.getClass().getDeclaredFields();

                for (Field f : fields)
                {
                    if (f.getAnnotation( ActiveRecordField.class ) != null)
                    {
                        f.setAccessible( true );
                        Class<?> fieldType = f.getType();

                        try
                        {
                            if (fieldType == Integer.class)
                            {
                                into.putInt( (int) f.get( instance ) );
                            }
                            else if (fieldType == String.class)
                            {
                                into.putString( f.get(instance ).toString(), Charsets.UTF_8 );

                            }
                            else if (fieldType == Double.class)
                            {
                                into.putDouble(f.getDouble( instance) );
                            }
                            else
                            {
                                throw new Throwable("Could not find associate Funnel for class of type: <" +
                                fieldType.toString() + ">.");
                            }

                        }
                        catch (IllegalAccessException e)
                        {

                        }
                        catch (Throwable t)
                        {
                            System.out.println(t);
                            t.printStackTrace();
                        }
                    }
                }



            }
        };

        return funnel;
    }

    public static void main( String[] args ) {
        Funnel<Person> personFunnel = FunnelFactory.getInstance( Person.class);
        Person p = new Person();
        p.setFirstName( "Charles" );
        p.setLastName( "Yang" );

        Hasher hasher = Hashing.sha256().newHasher();
        String newHashCode = "";
        newHashCode = hasher.putObject( p, personFunnel ).hash().toString();
    }
}
