package com.sunnyd;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.sunnyd.annotations.ActiveRecordField;
import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.annotations.ActiveRelationHasOne;
import com.sunnyd.annotations.ActiveRelationManyToMany;
import com.sunnyd.database.Manager;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.*;

/**
 * **
 * 
 * @author Mike All Children isa relation/children of a model primary key is the
 *         parent's primary key
 */

public class Base implements IModel {

    @ActiveRecordField
    private Integer id;
    @ActiveRecordField
    private Date    creationDate;
    @ActiveRecordField
    private Date    lastModifiedDate;
    private Boolean updateFlag = false;

    public Base() {
    }

    public Base(Map<String, Object> HM) {
        id = (Integer) HM.get("id");
        creationDate = (Date) HM.get("creationDate");
        lastModifiedDate = (Date) HM.get("lastModifiedDate");
        
        // Get Caller ClassName
        Class<?> classObject = this.getClass();

        //Default values
        Map<String, Object> defaultValues = getDefaultValues();    
        
        //HM has data is more valuable than defaultValues
        defaultValues.putAll(HM);
        

        // Set Attribute
        Base.setAttributes(classObject, this, defaultValues);

        // Set updateDateFlag to false after setter methods
        this.setUpdateFlag(false);
    }

    public static void setAttributes(Class<?> classObject, Object instanceObject, Map<String, Object> data) {
        
        // Get all activeRecord/table attribute from THIS class
        Field[] fields = classObject.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            //set field value from MAP using field name
            if (data.containsKey(fieldName)) {
                Object value = data.get(fieldName);
                try {
                    if (value != null) {
                        
                        if(!Modifier.isFinal(field.getModifiers())){
                            field.setAccessible(true);
                            field.set(instanceObject, fieldType.cast(value));
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace(); 
                }
            } else {
                if(field.getAnnotation(ActiveRecordField.class) != null)
                    System.out.println("MAP does not contain activeRecord Field " + fieldName);
            }
        }

        // Verify if model inherit another model
        if (classObject.getAnnotation(ActiveRecordInheritFrom.class) != null) {
            //RECURSIVE call to use parent methods to set data
            setAttributes(classObject.getSuperclass(), instanceObject, data);
        }

    }

/**
 **********************************FIND****************************************
**/
    public <T> T find(int id) {
        return find(id, this.getClass().getCanonicalName());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T find(Map<String, Object> conditions) {

        String tableName = BaseHelper.getClassTableName(this.getClass().getCanonicalName());
        List<Map<String, Object>> resultList = Manager.findAll(tableName, conditions);
        if (resultList.size() == 1) {
            Constructor cons;
            try {
                cons = (Constructor<T>) this.getClass().getConstructor(Map.class);
                return (T) cons.newInstance(resultList.get(0));
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else if (resultList.size() > 1) {
            System.out.println("Found more than 1 matching condition");
        } else {
            System.out.println("Found none");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T find(int id, String canonicalClassName) {
        try {
            // Get class attribute from database
            String tableName = BaseHelper.getClassTableName(canonicalClassName);
            Map<String, Object> HM = Manager.find(id, tableName);

            // NullCheck
            if (HM == null) {
                return null;
            }
            if (HM.size() <= 0) {
                return null;
            }
            // Get inherited values from parent table
            Map<String, Object> parentDatas = BaseHelper.getSuperDatas((Integer) HM.get("id"),
                    Class.forName(canonicalClassName));

            if (parentDatas != null) {
                // Merge parent's table data's into map
                HM.putAll(parentDatas);
            }
            return (T) Class.forName(canonicalClassName).getConstructor(Map.class).newInstance(HM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Base> List<T> findAll(Map<String, Object> conditions) {
        String canonicalClassName = this.getClass().getCanonicalName();
        List<Map<String, Object>> list = Manager.findAll(BaseHelper.getClassTableName(canonicalClassName), conditions);

        List<T> arrayList = new ArrayList<T>(list.size());
        Constructor cons = null;
        try {
            cons = this.getClass().getConstructor(Map.class);
            for (Map<String, Object> attr : list)
                arrayList.add((T) cons.newInstance(attr));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    @SuppressWarnings("unchecked")
    public <T extends Base> List<T> queryAll(String sqlQuery) {
        List<Map<String, Object>> list = Manager.findAll(sqlQuery);
        List<T> arrayList = new ArrayList<T>(list.size());
        Constructor<T> cons = null;
        try {
            cons = (Constructor<T>) this.getClass().getConstructor(Map.class);
            for (Map<String, Object> attr : list)
                arrayList.add((T) cons.newInstance(attr));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    public static DSLContext startQuery() {
        Connection conn = null;
        return DSL.using(conn, SQLDialect.MYSQL);
    }

    /**
     **********************************UPDATE****************************************
    **/
    public boolean update() {
        if (this.id != null) {
            if (this.getUpdateFlag()) {
                boolean allUpdated = update(this.getClass(), this);
                if (allUpdated) {
                    this.setUpdateFlag(false);
                    return allUpdated;
                }
            }
        } else {
            System.out.println("UH OH new object. Try saving first");
        }
        return false;
    }

    private static <T extends Base> boolean update(Class<? extends Base> classObject, T instanceObject) {
        Map<String, Object> updateAttributes = BaseHelper.getTableFieldNameAndValue(classObject, instanceObject);
        if (classObject.getAnnotation(ActiveRecordInheritFrom.class) != null) {

            @SuppressWarnings("unchecked")
            boolean updated = Base.update((Class<T>) classObject.getSuperclass(), instanceObject);
            if (!updated) {
                System.out.println("Could not update " + ((Base) instanceObject).getId() + " " + classObject.getName());
                return false;
            }
        }

        updateRelation(classObject, instanceObject);
        return Manager.update(((Base) instanceObject).getId(), classObject, updateAttributes);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Base> boolean updateRelation(Class<? extends Base> classObject, T instanceObject) {
        Field[] fields = classObject.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();

            // Handling for ActiveRelation ManyToMany and HasMany
            if (annotations.length > 0){
                String annotationName = annotations[0].annotationType().getSimpleName();
                if (annotationName.contentEquals("ActiveRelationManyToMany") || annotationName.contentEquals("ActiveRelationHasMany"))
                    updateManyRelation(field, annotations[0], classObject, instanceObject);
            }

        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Base> void updateManyRelation(Field field, Annotation annotation, Class<T> classObject,
            Object instanceObject) {
        
        int id = ((Base) instanceObject).getId();
        field.setAccessible(true);
        String relationName = annotation.annotationType().getSimpleName();
        String relationCanonicalName = BaseHelper.getGenericCanonicalClassName(field);
        String relationSimpleName = BaseHelper.getGenericSimpleName(field);
        String relationTable = relationName.contentEquals("ActiveRelationManyToMany") ? ((ActiveRelationManyToMany) annotation).relationTable() : BaseHelper.getClassTableName(relationSimpleName);

        // Get Current Collection in Memory Field
        List<Object> collection = null;
        try {
            collection = (List<Object>) field.get(instanceObject);
            if (collection == null) {
                // Either not initiated or no collection
                return;
            }
        } catch (IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        // Get old collection from database by querying the table for [className]Id
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put(StringUtils.uncapitalize(classObject.getSimpleName()) + "Id", id);
        ArrayList<Map<String, Object>> results = Manager.findAll(relationTable, condition);

        // Extract saved/old collection ids
        Iterator<Map<String, Object>> savedCollection = results.iterator();
        List<Integer> oldIds = new ArrayList<Integer>();
        while (savedCollection.hasNext()) {
            Map<String, Object> object = savedCollection.next();
            
            //Many to many fieldname is [objectname]Id
            String idFieldName = relationName.contentEquals("ActiveRelationManyToMany") ? StringUtils.uncapitalize(relationSimpleName) + "Id" : "id";
            oldIds.add((Integer) object.get(idFieldName));
        }

        try {
            for (int i = 0; i < collection.size(); i++) {
                Method getId = Class.forName(relationCanonicalName).getMethod("getId");
                Integer collectionObjectId = (Integer) getId.invoke(collection.get(i));
                
                // Object in collection is new
                if (collectionObjectId == null) 
                {
                    switch (relationName.toString()) {

                        case "ActiveRelationManyToMany":
                            newObjectManyToMany(id, collection.get(i), relationCanonicalName, relationSimpleName,
                                    relationTable, classObject);
                            break;

                        case "ActiveRelationHasMany":
                            newObjectHasMany(id, collection.get(i), relationCanonicalName, classObject);
                            break;
                        default:
                            break;
                    }

                } 
                //Already saved Object but is new to this object's collection save according to activerelation
                else if (collectionObjectId > 0 && !oldIds.contains(collectionObjectId)) 
                {
                    switch (relationName.toString()) {
                        case "ActiveRelationManyToMany":
                            existingObjectManyToMany(id, collectionObjectId, relationSimpleName, relationTable, classObject);
                            break;

                        case "ActiveRelationHasMany":
                            existingObjectHasMany(id, collection.get(i), relationCanonicalName, classObject);
                            break;
                        default:
                            break;
                    }
                } 
                // If exists in both list then remove from oldIds list  
                else if (oldIds.contains(collectionObjectId)) {
                    oldIds.remove(collectionObjectId);
                }

            }

            // Existing ids in oldIds list are those to be removed from db
            if (oldIds.size() > 0) {
                Iterator<Integer> oldIdIter = oldIds.iterator();
                while (oldIdIter.hasNext()) {
                    if (relationName.contentEquals("ActiveRelationHasMany")) {
                        //null the foreign key
                        int oldId = oldIdIter.next();
                        Map<String, Object> nullField = new HashMap<String, Object>();
                        nullField.put(StringUtils.uncapitalize(classObject.getSimpleName()) + "Id", null);
                        Manager.update(oldId, (Class<T>) Class.forName(relationCanonicalName), nullField); 
                        
                    } else if (relationName.contentEquals("ActiveRelationManyToMany")) {
                        int oldId = oldIdIter.next();
                        condition = new HashMap<String, Object>();
                        condition.put(StringUtils.uncapitalize(classObject.getSimpleName()) + "Id", id);
                        condition.put(StringUtils.uncapitalize(relationSimpleName) + "Id", oldId);
                        if (results.size() > 0) {
                            Manager.destroy(relationTable, condition);
                        }
                    }
                }

            }

        } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException
                | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void newObjectManyToMany(Integer id, Object collectionObject, String relationCanonicalName, String relationSimpleName, String relationTable, Class<?> classObject) {
        // New collection object but doesn't exists in database

        // If id is null, therefore the object id could not have exist in the
        // relation table
        try {
            Method getId = Class.forName(relationCanonicalName).getMethod("getId");
            Method save = Class.forName(relationCanonicalName).getMethod("save");
            save.invoke(collectionObject);
            int newCollectionId = (int) getId.invoke(collectionObject);

            // Save relation
            Map<String, Object> conditions = new HashMap<String, Object>();
            conditions.put(StringUtils.uncapitalize(classObject.getSimpleName()) + "Id", id);
            conditions.put(StringUtils.uncapitalize(relationSimpleName) + "Id", newCollectionId);
            Manager.save(relationTable, conditions);
        } catch (MySQLIntegrityConstraintViolationException | NoSuchMethodException | SecurityException
                | ClassNotFoundException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private static void existingObjectManyToMany(Integer id, Integer collectionObjectId, String relationSimpleName, String relationTable, Class<?> classObject) {
        // An existing object in database added to this collection
        // Save relation
        Map<String, Object> conditions = new HashMap<String, Object>();
        conditions.put(StringUtils.uncapitalize(classObject.getSimpleName()) + "Id", id);
        conditions.put(StringUtils.uncapitalize(relationSimpleName) + "Id", collectionObjectId);
        try {
            Manager.save(relationTable, conditions);

        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
            // Do nothing, user must have added the same
            // object twice in the collection
        }

    }

    private static void newObjectHasMany(Integer id, Object collectionObject, String relationCanonicalName, Class<?> classObject) {
        // Handling: New collection object but doesn't exists in database
        try {
            String setterMethod = "set" + StringUtils.capitalize(classObject.getSimpleName()) + "Id";
            // Set objects in the has many collection to current instanceObject
            // id
            Method setRelationIdMethod = Class.forName(relationCanonicalName).getDeclaredMethod(setterMethod,
                    Integer.class);
            setRelationIdMethod.invoke(collectionObject, id);

            Method save = Class.forName(relationCanonicalName).getMethod("save");
            save.invoke(collectionObject);
        } catch (IllegalAccessException | NoSuchMethodException | SecurityException | ClassNotFoundException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void existingObjectHasMany(Integer id, Object collectionObject, String relationCanonicalName, Class<?> classObject) {
        // Handling: An existing object in database added to
        // update collection object relation
        String setterMethod = "set" + StringUtils.capitalize(classObject.getSimpleName()) + "Id";

        // Set objects in the has many collection to current id
        try {
            Method setRelationIdMethod = Class.forName(relationCanonicalName).getDeclaredMethod(setterMethod,
                    Integer.class);
            setRelationIdMethod.invoke(collectionObject, id);

            // Update existing object
            Method update = Class.forName(relationCanonicalName).getMethod("update");
            update.invoke(collectionObject);
        } catch (IllegalAccessException | NoSuchMethodException | SecurityException | ClassNotFoundException
                | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     **********************************Save****************************************
    **/
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

    private static <T extends Base> int save(Class<?> classObject, Object objectInstance) {
        Map<String, Object> attrToPersist = BaseHelper.getTableFieldNameAndValue(classObject, objectInstance);
        int id = 0;
        if (classObject.getAnnotation(ActiveRecordInheritFrom.class) != null) {
            id = Base.save(classObject.getSuperclass(), objectInstance);
        }
        if (id != 0) {
            attrToPersist.put("id", id);
        }

        // Save object before has many or ManyToMany relation fields
        id = Manager.save((Class<T>) classObject, attrToPersist);
        // id = Manager.save(BaseHelper.getClassTableName(classObject),
        // attrToPersist);
        saveRelation(classObject, objectInstance, id);
        return id;
    }

    // objectInstance contains all the data related to that instance
    // classObject is the class of interest
    public static void saveRelation(Class<?> classObject, Object objectInstance, Integer id) {

        // TODO Refactor large method

        Field[] fields = classObject.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            if( annotations.length > 0){ 
                String annotationName = annotations[0].annotationType().getSimpleName();
                if (annotationName.contentEquals("ActiveRelationHasMany")) {
                    String relationCanonicalName = BaseHelper.getGenericCanonicalClassName(field);
                    try {
                        @SuppressWarnings("unchecked")
                        List<Object> hasManyCollection = (List<Object>) field.get(objectInstance);
                        if (hasManyCollection == null) {
                            continue;
                        }
    
                        for (int i = 0; i < hasManyCollection.size(); i++) {
                            String setterMethod = "set" + StringUtils.capitalize(classObject.getSimpleName()) + "Id";
    
                            // Set objects in the has many collection id to current
                            // id
                            Method setRelationIdMethod = Class.forName(relationCanonicalName).getDeclaredMethod(
                                    setterMethod, Integer.class);
                            setRelationIdMethod.invoke(hasManyCollection.get(i), id);
    
                            // Get collection object id
                            Method getId = Class.forName(relationCanonicalName).getMethod("getId");
                            Integer collectionObjectId = (Integer) getId.invoke(hasManyCollection.get(i));
    
                            // if collection object id is null, save the objects in
                            // the hasMany collection
                            if (collectionObjectId == null) {
                                Method save = Class.forName(relationCanonicalName).getMethod("save");
                                save.invoke(hasManyCollection.get(i));
                            } else {
                                Method update = Class.forName(relationCanonicalName).getMethod("update");
                                update.invoke(hasManyCollection.get(i));
                            }
                        }
    
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException
                            | ClassNotFoundException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (annotationName.contentEquals("ActiveRelationManyToMany")) {
                    String relationCanonicalName = BaseHelper.getGenericCanonicalClassName(field);
                    String relationSimpleName = BaseHelper.getGenericSimpleName(field);
                    String relationTable = ((ActiveRelationManyToMany) annotations[0]).relationTable();
    
                    try {
                        List<Object> manyToManyCollection = (List<Object>) field.get(objectInstance);
                        if (manyToManyCollection == null) {
                            continue;
                        }
    
                        for (int i = 0; i < manyToManyCollection.size(); i++) {
    
                            Method getId = Class.forName(relationCanonicalName).getMethod("getId");
                            Integer collectionObjectId = (Integer) getId.invoke(manyToManyCollection.get(i));
    
                            if (collectionObjectId == null) {
                                // Collection object id is null, save the objects in
                                // the hasMany collection
                                // If id is null, therefore the object id could not
                                // have exist in the relation table
                                Method save = Class.forName(relationCanonicalName).getMethod("save");
                                save.invoke(manyToManyCollection.get(i));
                                int newCollectionId = (int) getId.invoke(manyToManyCollection.get(i));
    
                                // Save relation
                                Map<String, Object> conditions = new HashMap<String, Object>();
                                conditions.put(StringUtils.uncapitalize(classObject.getSimpleName()) + "Id", id);
                                conditions.put(StringUtils.uncapitalize(relationSimpleName) + "Id", newCollectionId);
                                Manager.save(relationTable, conditions);
    
                            } else {
    
                                Map<String, Object> conditions = new HashMap<String, Object>();
                                conditions.put(StringUtils.uncapitalize(classObject.getSimpleName()) + "Id", id);
                                conditions.put(StringUtils.uncapitalize(relationSimpleName) + "Id", collectionObjectId);
                                ArrayList<Map<String, Object>> results = Manager.findAll(relationTable, conditions);
                                if (results.size() == 0) {
                                    Manager.save(relationTable, conditions);
                                }
                            }
                        }
    
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException
                            | ClassNotFoundException | InvocationTargetException
                            | MySQLIntegrityConstraintViolationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     **********************************Destroy****************************************
    **/
    public boolean destroy() {
        boolean success = destroyHierarchy(this.getClass(), this.getId());
        if (success) {
            id = null;
        }
        return success;
    }

    // Delete Parent Data after child has been deleted
    private static boolean destroyHierarchy(Class<?> classObject, Integer id) {
        String tableName = BaseHelper.getClassTableName(classObject);
        boolean success = Manager.destroy(id, tableName);
        if (classObject.getAnnotation(ActiveRecordInheritFrom.class) != null) {
            success = Base.destroyHierarchy(classObject.getSuperclass(), id);
        }
        return success;
    }

    /**
     **********************************Relation Lazy Load****************************************
    **/
    public void initRelation(String attributeName) {
        // Conditions
        // id == null initialize empty list
        // id != null and relation is loaded exit
        // if id != null and relation isn't loaded, get collection of objects

        try {
            Field relation = this.getClass().getDeclaredField(attributeName);
            Annotation[] relationAnnotations = relation.getAnnotations();

            // LazyLoading
            relation.setAccessible(true);

            if (relationLoaded(relation)) {
                return;
            }

            if (this.id == null) {
                try {
                    if (!relationAnnotations[0].annotationType().getSimpleName().contentEquals("ActiveRelationHasOne")) {
                        relation.set(this, new ArrayList<Object>());
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            switch (relationAnnotations[0].annotationType().getSimpleName()) {
                case "ActiveRelationHasMany":
                    relationHasMany(relation, null);
                    break;
                case "ActiveRelationHasOne":
                    relationHasOne(relation);
                    break;
                case "ActiveRelationManyToMany":
                    try {
                        relationHasMany(relation, ((ActiveRelationManyToMany) relationAnnotations[0]).relationTable());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

        } catch (NoSuchFieldException | SecurityException | NegativeArraySizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean relationLoaded(Field relation) {
        try {
            if (relation.get(this) != null) {
                System.out.println("alreadyInitlized");
                return true;
            }
        } catch (IllegalArgumentException | IllegalAccessException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        return false;
    }

    private void relationHasOne(Field relation) {
        String relationCanonicalClassName = relation.getType().getCanonicalName();
        String relationSimpleName = relation.getType().getSimpleName();
        Object relationObject = null;
        try {
            Field relationIdField = this.getClass().getDeclaredField(
                    StringUtils.uncapitalize(relationSimpleName) + "Id");
            relationIdField.setAccessible(true);
            Method findMethod = Class.forName(relationCanonicalClassName).getMethod("find", int.class, String.class);
            Object newInstance = Class.forName(relationCanonicalClassName).getConstructor().newInstance();
            if (relationIdField.get(this) == null) {
                return;
            }
            relationObject = findMethod.invoke(newInstance, relationIdField.get(this), relationCanonicalClassName);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        relation.setAccessible(true);
        try {
            relation.set(this, relationObject);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void relationHasMany(Field relation, String relationTableName) {

        // Get object class from Arraylist or list generic type

        String relationCanonicalClassName = BaseHelper.getGenericCanonicalClassName(relation);
        String relationSimpleClassName = BaseHelper.getGenericSimpleName(relation);
        ;
        String simpleClassName = this.getClass().getSimpleName();

        boolean isManyToMany = relationTableName == null ? false : true;
        relationTableName = relationTableName == null ? BaseHelper.getClassTableName(relationSimpleClassName)
                : relationTableName;

        Class<?> relationClass = null;
        try {
            relationClass = Class.forName(relationCanonicalClassName);
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Condition use current object ID for has many query
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put(StringUtils.uncapitalize(simpleClassName) + "Id", this.getId());
        // Get results
        ArrayList<Map<String, Object>> results = Manager.findAll(relationTableName, condition);

        // Following: create instance of relation class and add to array
        int size = results.size();
        List<Object> collection = new ArrayList<Object>();
        for (int i = 0; i < size; i++) {
            try {
                if (!isManyToMany) {
                    // ActiveRelationHasMany
                    collection.add(relationClass.getConstructor(Map.class).newInstance(results.get(i)));
                } else {
                    // ActiveRelationManyToMany
                    Class<?> classObjectOfRelation = Class.forName(relationCanonicalClassName);
                    Object a = classObjectOfRelation.getMethod("find", int.class).invoke(
                            classObjectOfRelation.getConstructor().newInstance(),
                            (int) results.get(i).get(StringUtils.uncapitalize(relationSimpleClassName) + "Id"));
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

    /**
     * *** MUTATOR ***************************************************
     */
    // Common Mutator in all child class.
    public Integer getId() {
        return this.id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public Boolean getUpdateFlag() {
        return this.updateFlag;
    }

    public void setUpdateFlag(Boolean flag) {
        this.updateFlag = flag;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * ***** Private **********************************************
     */
    private String getClassName() {
        return this.getClass().getName();
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
    
    private Map<String, Object> getDefaultValues(){
        try {
            return this.getClass().getConstructor().newInstance().toMap(true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> toMap() {
        return BaseHelper.getTableFieldNameAndValue(this);
    }
    

    
//    private Map<String, Object> getDefaultValues(){
//        try {
//            Base temp = (Base)this.getClass().getConstructor().newInstance();
//            return temp.toMapExcludeNullField();
//        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
//                | NoSuchMethodException | SecurityException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }
    
    
    
    
    public Map<String, Object> toMap(boolean excludeNull){     
        Map<String, Object> attributes = new HashMap<String, Object>();
        Class<?> classObject = this.getClass();
        boolean hasSuper = true;
        
        while(hasSuper){
            Field[] classFields = classObject.getDeclaredFields();
            for (int i = 0; i < classFields.length; i++) {
                Field field = classFields[i];
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        if( value == null && excludeNull)
                            continue;
                        attributes.put(field.getName(), value);
                    } catch (IllegalArgumentException | SecurityException | IllegalAccessException  e) {
                        e.printStackTrace();
                    }
            }
            
            if(classObject.getAnnotation(ActiveRecordInheritFrom.class) != null){
                classObject = classObject.getSuperclass();
            }else{
                hasSuper = false;
            }
            
        }
        return attributes;   
    }

}
