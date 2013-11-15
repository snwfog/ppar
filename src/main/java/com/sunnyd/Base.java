package com.sunnyd;


import com.sunnyd.annotations.ActiveRecordField;
import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.database.Manager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/*****
 *
 *
 * @author Mike All Children isa relation/children of a model primary key is the
 *         parent's primary key
 */

public class Base implements IModel {

    @ActiveRecordField
    private Integer id;
    @ActiveRecordField
    private Date creationDate;
    @ActiveRecordField
    private Date lastModifiedDate;
    private Boolean updateFlag = false;

    public Base() {
    }

    public Base( Map<String, Object> HM ) {
        id = (Integer) HM.get( "id" );
        creationDate = (Date) HM.get( "creationDate" );
        lastModifiedDate = (Date) HM.get( "lastModifiedDate" );

        // Get Caller ClassName
        Class<?> classObject = this.getClass();

        // Set Attribute
        Base.setAttributes( classObject, this, HM );

        // Set updateDateFlag to false after setter methods
        this.setUpdateFlag( false );
    }

    @SuppressWarnings("unchecked")
    public static <T> T find( int id, String canonicalClassName ) {
        try {
            // Get class attribute from database
            String tableName = BaseHelper.getClassTableName( canonicalClassName );
            Map<String, Object> HM = Manager.find( id, tableName );
            // Get inherited values from parent table
            Map<String, Object> parentDatas = BaseHelper.getSuperDatas( (Integer) HM.get( "id" ), Class.forName( canonicalClassName ) );

            if ( parentDatas != null ) {
                // Merge parent's table data's into map
                HM.putAll( parentDatas );
            }
            return (T) Class.forName( canonicalClassName ).getConstructor( Map.class ).newInstance( HM );
        } catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e ) {
            e.printStackTrace();
        }
        return null;
    }

    //    public static <T extends Base> T find(int id) {
    //        //TODO BUG: if calling lets say Document.find in Peer main method, the class is peer...
    //        // Since this is a static method, to get caller of method we must look
    //        // in stack
    //        // At this point stack should look like this:
    //        // [java.lang.Thread.getStackTrace(Unknown Source),
    //        // com.sunnyd.Base.find(Base.java:79),
    //        // com.sunnyd.models.Person.main(Person.java:20), .....so on]
    //        // TODO:Need a better solution than stack to get caller class
    //        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
    //        String className = ste[2].getClassName();
    //        System.out.println(Arrays.asList(Thread.currentThread().getStackTrace()).toString());
    //        return find(id, className);
    //    }

    @Deprecated
    @SuppressWarnings("unchecked")
    // test find for hasOne
    public static <T> T find_hasOne( int id ) {
        // Since this is a static method, to get caller of method we must look
        // in stack
        // At this point stack should look like this:
        // [java.lang.Thread.getStackTrace(Unknown Source),
        // com.sunnyd.Base.find(Base.java:79),
        // com.sunnyd.models.Person.main(Person.java:20), .....so on]
        // TODO:Need a better solution than stack to get caller class
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        String className = ste[2].getClassName();
        try {
            // Get class attribute from database
            String tableName = BaseHelper.getClassTableName( className );
            Map<String, Object> HM = Manager.find( id, tableName );

            // Get inherited values from parent table
            Map<String, Object> parentDatas = BaseHelper.getSuperDatas( (Integer) HM.get( "id" ), Class.forName( className ) );

            if ( parentDatas != null ) {
                // Merge parent's table data's into map
                HM.putAll( parentDatas );
            }
            return (T) Class.forName( className ).getConstructor( Map.class ).newInstance( HM );
        } catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T findAll() {
        return null;
    }

    private static <T extends Base> boolean update( Class<T> classObject, Object instanceObject ) {
        Map<String, Object> updateAttributes = BaseHelper.getTableFieldNameAndValue( classObject, instanceObject );
        if ( classObject.getAnnotation( ActiveRecordInheritFrom.class ) != null ) {
            boolean updated = Base.update( (Class<T>)classObject.getSuperclass(), instanceObject );
            if ( !updated ) {
                System.out.println( "Could not update " + ((Base) instanceObject).getId() + " " + classObject.getName() );
                return false;
            }
        }
        
        updateRelation(classObject, instanceObject);
        return Manager.update( ((Base) instanceObject).getId(), classObject , updateAttributes );
    }

    // Delete Parent Data after child has been deleted
    private static boolean destroyHierarchy( Class<?> classObject, Integer id ) {
        String tableName = BaseHelper.getClassTableName( classObject );
        boolean success = Manager.destroy( id, tableName );
        if ( classObject.getAnnotation( ActiveRecordInheritFrom.class ) != null ) {
            success = Base.destroyHierarchy( classObject.getSuperclass(), id );
        }
        return success;
    }

    
    
    /***********************************************SAVE********************************************************/
    public boolean save() {
        int newId = 0;
        if (this.getId() == null) {
            newId = save(this.getClass(), this);
            if (newId != 0) {
                id = newId;
                return true;
            }
        }
        return false;
    }

    private static <T extends Base> Integer save( Class<T> classObject, Object objectInstance ) {
        Map<String, Object> attrToPersist = BaseHelper.getTableFieldNameAndValue( classObject, objectInstance );
        Integer id = 0;
        if ( classObject.getAnnotation( ActiveRecordInheritFrom.class ) != null ) {
            id = Base.save( (Class<T>)classObject.getSuperclass(), objectInstance );
            // System.out.println(id);
        }
        if ( id != 0 ) {
        //Save object before has many or ManyToMany relation fields
        try {
            attrToPersist.put( "id", id );
        } catch (MySQLIntegrityConstraintViolationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Save object before has many or manytomany relation fields
        id = Manager.save( classObject , attrToPersist );
        saveRelation( classObject, objectInstance, id );
        return id;
    }

    //objectInstance contains all the data related to that instance
    //classObject is the class interest
    public static void saveRelation( Class<?> classObject, Object objectInstance, Integer id ) {
        
        //TODO Refactor large method
        
        Field[] fields = classObject.getDeclaredFields();
        for ( Field field : fields ) {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            if ( annotations.length > 0 && annotations[0].annotationType().getSimpleName().contentEquals( "ActiveRelationHasMany" ) ) {
                String relationCanonicalName = BaseHelper.getGenericCanonicalClassName( field );
                try {
                    field.setAccessible( true );
                    @SuppressWarnings("unchecked")
                    List<Object> hasManyCollection = (List<Object>) field.get( objectInstance );
                    if(hasManyCollection ==null){
                        continue;
                    }
                    
                    for ( int i = 0; i < hasManyCollection.size(); i++ ) {
                        String setterMethod = "set" + StringUtils.capitalize( classObject.getSimpleName() ) + "Id";

                        //Set objects in the has many collection id to current id
                        Method setRelationIdMethod = Class.forName( relationCanonicalName ).getDeclaredMethod( setterMethod, Integer.class );
                        setRelationIdMethod.invoke( hasManyCollection.get( i ), id );

                        //Get collection object id
                        Method getId = Class.forName( relationCanonicalName ).getMethod( "getId" );
                        Integer collectionObjectId = (Integer) getId.invoke( hasManyCollection.get( i ) );

                        //if collection object id is null, save the objects in the hasMany collection
                        if ( collectionObjectId == null ) {
                            Method save = Class.forName( relationCanonicalName ).getMethod( "save" );
                            save.invoke( hasManyCollection.get( i ) );
                        } else {
                            Method update = Class.forName( relationCanonicalName ).getMethod( "update" );
                            update.invoke( hasManyCollection.get( i ) );
                        }
                    }

                } catch ( IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | ClassNotFoundException | InvocationTargetException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if ( annotations.length > 0 && annotations[0].annotationType().getSimpleName().contentEquals( "ActiveRelationManyToMany" ) ) {
                //TODO add manager relation saving
            }
        }
    }

    public static void setAttributes( Class<?> classObject, Object instanceObject, Map<String, Object> data ) {
        // Get all table attribute from this class
        Field[] fields = BaseHelper.getTableField( classObject );
        for ( Field field : fields ) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            if ( data.containsKey( fieldName ) ) {
                Object value = data.get( fieldName );
                String capitalizeField = StringUtils.capitalize( fieldName );
                java.lang.reflect.Method method;
                try {
                    method = classObject.getDeclaredMethod( "set" + capitalizeField, fieldType );
                    
                    if(manyToManyCollection == null){      
                        continue;
                    }
                    
                    for (int i = 0; i<manyToManyCollection.size(); i++){
                        
                        Method getId = Class.forName(relationCanonicalName).getMethod("getId");
                        Integer collectionObjectId = (Integer) getId.invoke(manyToManyCollection.get(i)); 
                        
                        if(collectionObjectId == null){
                            //Collection object id is null, save the objects in the hasMany collection
                            //If id is null, therefore the object id could not have exist in the relation table
                            Method save = Class.forName(relationCanonicalName).getMethod("save");
                            save.invoke(manyToManyCollection.get(i));             
                            int newCollectionId = (int) getId.invoke(manyToManyCollection.get(i));             
                                       
                            //Save relation
                            HashMap<String, Object> conditions = new HashMap<String, Object>();
                            conditions.put(StringUtils.uncapitalize(classObject.getSimpleName())+"Id",id);
                            conditions.put(StringUtils.uncapitalize(relationSimpleName)+"Id", newCollectionId);
                            Manager.save(relationTable, conditions);
                          
                        }else{
                            
                              HashMap<String, Object> conditions = new HashMap<String, Object>();
                              conditions.put(StringUtils.uncapitalize(classObject.getSimpleName())+"Id",id);
                    method.invoke( instanceObject, fieldType.cast( value ) );
                              ArrayList<HashMap<String, Object>> results = Manager.findAll(relationTable, conditions);
                              if(results.size() == 0){
                                  Manager.save(relationTable, conditions);
                              }
                        }
                    }      
                    
                } catch ( NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e ) {
                    e.printStackTrace();
                    break;// If method does not have setMethod then it is
                    // not a db Attribute
                }

            }
        }

        // Verify if model inherit another model
        if ( classObject.getAnnotation( ActiveRecordInheritFrom.class ) != null ) {
            setAttributes( classObject.getSuperclass(), instanceObject, data );
        }

    }

    public <T> T find( int id ) {
        return find( id, this.getClass().getCanonicalName() );
    }

    public boolean update() {
        if ( this.getUpdateFlag() ) {
            boolean allUpdated = update( this.getClass(), this );
            if ( allUpdated ) {
                this.setUpdateFlag( false );
                return allUpdated;
            }
        }
        return false;
    }

    public Boolean Destroy() {
        boolean success = destroyHierarchy( this.getClass(), this.getId() );
        if ( success ) {
            id = null;
        }
        return success;
    }

    public boolean save() {
        int newId = 0;
        if ( this.getId() == null ) {
            newId = save( this.getClass(), this );
            if ( newId != 0 ) {
                id = newId;
                return true;
            }
        }
        return false;
    }

    /*********************************Relations************************************************/
    public void initRelation( String attributeName ) {
        //Conditions
        // id == null initialize empty list
        // id != null and relation is loaded exit
        // if id != null and relation isn't loaded, get collection of objects

        try {
            Field relation = this.getClass().getDeclaredField( attributeName );
            Annotation[] relationAnnotations = relation.getAnnotations();

            //LazyLoading
            relation.setAccessible( true );

            if ( this.id == null ) {
                try {
                    if(!relationAnnotations[0].annotationType().getSimpleName().contentEquals("ActiveRelationHasOne")){
                    relation.set( this, new ArrayList<Object>() );
                    }
                } catch ( IllegalArgumentException | IllegalAccessException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if ( relationLoaded( relation ) ) {
                return;
            }

            switch ( relationAnnotations[0].annotationType().getSimpleName() ) {
                case "ActiveRelationHasMany":
                    relationHasMany( relation, null );
                    break;
                case "ActiveRelationHasOne":
                    relationHasOne( relation );
                    break;

                case "ActiveRelationManyToMany":
                    try {
                        relationHasMany( relation, relationAnnotations[0].getClass().getMethod( "relationTable" ).invoke( relationAnnotations[0] ).toString() );
                    } catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException
                            | NoSuchMethodException e ) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

        } catch ( NoSuchFieldException | SecurityException | NegativeArraySizeException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean relationLoaded( Field relation ) {
        try {
            if ( relation.get( this ) != null ) {
                System.out.println( "alreadyInitlized" );
                return true;
            }
        } catch ( IllegalArgumentException | IllegalAccessException e2 ) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        return false;
    }

    private void relationHasOne( Field relation ) {
        String relationCanonicalClassName = relation.getType().getCanonicalName();
        String relationSimpleName = relation.getType().getSimpleName();
        Object relationObject = null;
        try {
            Field relationIdField = this.getClass().getDeclaredField( relationSimpleName.toLowerCase().trim() + "Id" );
            relationIdField.setAccessible( true );
            Method findMethod = Class.forName( relationCanonicalClassName ).getMethod( "find", int.class, String.class );
            Object newInstance = Class.forName(relationCanonicalClassName).getConstructor().newInstance();
            if(relationIdField.get(this) == null){
                return;
            }
            relationObject = findMethod.invoke( null, relationIdField.get( this ), relationCanonicalClassName );
        } catch ( NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        relation.setAccessible( true );
        try {
            relation.set( this, relationObject );
        } catch ( IllegalArgumentException | IllegalAccessException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
private void relationHasMany( Field relation, String relationTableName ) {
        String relationCanonicalClassName = BaseHelper.getGenericCanonicalClassName(relation); 
        String relationSimpleClassName = BaseHelper.getGenericSimpleName(relation);;
        String simpleClassName = this.getClass().getSimpleName();
        
        
    
        relationTableName = relationTableName == null ? BaseHelper.getClassTableName(relationSimpleClassName) : relationTableName;
       
        Class<?> relationClass = null;
        try {
            relationClass = Class.forName(relationCanonicalClassName);
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        //Condition use current object ID for has many query
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put(StringUtils.uncapitalize(simpleClassName) + "Id", this.getId());
        //Get results
        ArrayList<HashMap<String, Object>> results = Manager.findAll(
                relationTableName, condition);

        //Following: create instance of relation class and add to array
        int size = results.size();
        List<Object> collection = new ArrayList<Object>();
        for (int i = 0; i < size; i++) {
            try {
                if(!isManyToMany){
                    //ActiveRelationHasMany
                    collection.add(relationClass.getConstructor(HashMap.class).newInstance(results.get(i)) );
                }else{
                    //ActiveRelationManyToMany
                    Class<?> classObjectOfRelation = Class.forName(relationCanonicalClassName); 
                    Object a = classObjectOfRelation.getMethod("find", int.class).invoke(classObjectOfRelation.getConstructor().newInstance(), (int)results.get(i).get(StringUtils.uncapitalize(relationSimpleClassName)+"Id"));
                    collection.add(a);
                }
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        relation.setAccessible(true);
        try {
            relation.set(this, collection);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void relationManyToMany(Field relation, String relationTableName){
        
        //Get object class from Arraylist or list generic type

        String relationCanonicalClassName = BaseHelper.getGenericCanonicalClassName( relation );
        String relationSimpleClassName = BaseHelper.getGenericSimpleName( relation );
        ;
        String simpleClassName = this.getClass().getSimpleName();

        relationTableName = relationTableName == null ? BaseHelper.getClassTableName( relationSimpleClassName.toLowerCase() ) : relationTableName;

        Class<?> relationClass = null;
        try {
            relationClass = Class.forName( relationCanonicalClassName );
        } catch ( ClassNotFoundException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //Condition use current object ID for has many query
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put( simpleClassName.toLowerCase() + "Id", this.getId() );
        //Get results
        ArrayList<Map<String, Object>> results = Manager.findAll( relationTableName, condition );

        //Following: create instance of relation class and add to array
        int size = results.size();
        List<Object> collection = new ArrayList<Object>();
        for ( int i = 0; i < size; i++ ) {
            try {
                collection.add( relationClass.getConstructor( Map.class ).newInstance( results.get( i ) ) );
            } catch ( InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException e ) {
                e.printStackTrace();
            }
        }

        relation.setAccessible( true );
        try {
            relation.set( this, collection );
        } catch ( IllegalArgumentException | IllegalAccessException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /****** MUTATOR ****************************************************/
    // Common Mutator in all child class.
    
    
    public String toString(){
        return toMap().toString();
    }
    
    public Map<String, Object> toMap(){
        Field[] fields = this.getClass().getDeclaredFields();
        Map<String, Object> acorn = new HashMap<String, Object>();
        for(Field field: fields){
            field.setAccessible(true);
            try {
                acorn.put(field.getName(), field.get(this));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return acorn;
    }
    
    public Integer getId() {
        return this.id;
    }

    private void setId( Integer id ) {
        this.id = id;
    }

    public Boolean getUpdateFlag() {
        return this.updateFlag;
    }

    public void setUpdateFlag( Boolean flag ) {
        this.updateFlag = flag;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate( Date creationDate ) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate( Date lastModifiedDate ) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /******** Private ***********************************************/
    private String getClassName() {
        return this.getClass().getName();
    }

    private String getTableName() {
        try {
            return this.getClass().getDeclaredField( "tableName" ).get( null ).toString();
        } catch ( IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
