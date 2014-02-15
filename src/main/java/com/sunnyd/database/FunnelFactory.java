package com.sunnyd.database;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.Base;
import com.sunnyd.annotations.ActiveRecordField;
import com.sunnyd.models.Person;

import java.lang.reflect.Field;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunnelFactory {

    static final Logger logger = LoggerFactory.getLogger( FunnelFactory.class );

    public static <T extends Base> Funnel<T> getInstance( Class<T> klazz ) {
        Funnel<T> funnel = new Funnel<T>() {
            @Override
            public void funnel( T instance, PrimitiveSink into ) {
                // Get all the fields
                Field[] fields = instance.getClass().getDeclaredFields();

                for ( Field f : fields ) {
                    if ( f.getAnnotation( ActiveRecordField.class ) != null ) {
                        f.setAccessible( true );
                        Class<?> fieldType = f.getType();

                        try {
                            if ( fieldType == Integer.class ) {
                                Integer value = f.get( instance ) == null ? null : (Integer) f.get( instance );
                                if ( value != null ) {
                                    into.putInt( (int) f.get( instance ) );
                                }
                            } else if ( fieldType == String.class ) {
                                String value = f.get( instance ) == null ? "" : f.get( instance ).toString();
                                into.putString( value, Charsets.UTF_8 );
                            } else if ( fieldType == Double.class ) {

                                Double value = f.get( instance ) == null ? null : (Double) f.get( instance );
                                if ( value != null ) {
                                    into.putDouble( (Double) f.get( instance ) );
                                }

                            } else if ( fieldType == Date.class ) {
                                Date value = f.get( instance ) == null ? null : (Date) f.get( instance );
                                if ( value != null ) {
                                    into.putString( value.toString(), Charsets.UTF_8 );
                                }
                            } else if ( fieldType == java.sql.Date.class ) {
                                java.sql.Date value =
                                        f.get( instance ) == null ? null : (java.sql.Date) f.get( instance );
                                if ( value != null ) {
                                    into.putString( value.toString(), Charsets.UTF_8 );
                                }
                            } else {
                                into.putString( f.get( instance ).toString(), Charsets.UTF_8 );
                                throw new Throwable( "Could not find associate Funnel for class of type: <" +
                                        fieldType.toString() + ">:" + f.getName() + "(" + f.get( instance ) + ")" );
                            }
                        } catch ( Throwable t ) {
                            logger.error( t.toString(), t );
                        }
                    }
                }
            }
        };

        return funnel;
    }

    public static void main( String[] args ) {
        Funnel<Person> personFunnel = FunnelFactory.getInstance( Person.class );
        Person p = new Person();
        p.setFirstName( "Charles" );
        p.setLastName( "Yang" );

        Hasher hasher = Hashing.sha256().newHasher();
        String newHashCode = "";
        newHashCode = hasher.putObject( p, personFunnel ).hash().toString();
    }
}
