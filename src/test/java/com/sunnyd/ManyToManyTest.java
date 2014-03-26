package com.sunnyd;

import com.sunnyd.database.Connector;
import com.sunnyd.database.Manager;
import com.sunnyd.database.fixtures.Prep;
import com.sunnyd.models.Category;
import com.sunnyd.models.Group;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ManyToManyTest extends Base {

    static final Logger logger = LoggerFactory.getLogger( ManyToManyTest.class );
    private Integer groupId = null;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init( "groups" );
        Prep.init( "categories" );
    }

    public void prepTable() throws SQLException {
        Prep.purgeAllRecord( "groups", false );
        Prep.resetPrimaryKey( "groups", false );
        Prep.purgeAllRecord( "categories", false );
        Prep.resetPrimaryKey( "categories", false );
        Prep.purgeAllRecord( "groups_categories", false );
    }

    @Test
    public void TestSaveWithExistingObject() {
        logger.warn( "TestSaveWithExistingObject" );
        try {
            prepTable();
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Group g = new Group();
        g.setGroupName( "group group group" );
        g.save();
        groupId = g.getId();

        Category c1 = new Category();
        c1.setCategoryName( "category1" );
        c1.save();



        Category c2 = new Category();
        c2.setCategoryName( "category2" );
        c2.save();


        g.getCategories().add( c1 );
        g.getCategories().add( c2 );
        g.update();

        String query =
                "SELECT * FROM groups_categories WHERE category_id=" + c1.getId() + " AND group_id = " + g.getId();
        String query2 =
                "SELECT * FROM groups_categories WHERE category_id=" + c2.getId() + " AND group_id = " + g.getId();
        ResultSet rs = Manager.rawSQLfind( query );
        ResultSet rs2 = Manager.rawSQLfind( query2 );

        try {
            Assert.assertTrue( rs.next() );
            Assert.assertTrue( rs2.next() );
        } catch ( SQLException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            try {
                rs.close();
                rs2.close();
            } catch ( SQLException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            Connection a = Connector.getConnection();
            if ( !a.isClosed() ) {
                a.close();
            }
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test(dependsOnMethods = { "TestSaveWithExistingObject" })
    public void TestInitializing() {
        logger.warn( "TestInitializing" );
        Group g = new Group().find( groupId );
        //Verify lazy loading

        Field field;
        try {
            field = g.getClass().getDeclaredField( "categories" );
            field.setAccessible( true );
            Assert.assertNull( field.get( g ) );
        } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
            Assert.fail();
            e.printStackTrace();
        }
        Assert.assertNotNull( g.getCategories() );
    }



    @Test
    public void TestSaveWithNewObject() {
        logger.warn( "TestSaveWithNewObject" );
        Group g = new Group();
        g.setGroupName( "group group group" );

        Category c1 = new Category();
        c1.setCategoryName( "new_category1" );

        Category c2 = new Category();
        c2.setCategoryName( "new_category2" );


        g.getCategories().add( c1 );
        g.getCategories().add( c2 );
        
        c2.save();
       
        g.save();

        String query =
                "SELECT * FROM groups_categories WHERE category_id=" + c1.getId() + " AND group_id = " + g.getId();
        String query2 =
                "SELECT * FROM groups_categories WHERE category_id=" + c2.getId() + " AND group_id = " + g.getId();
        ResultSet rs = Manager.rawSQLfind( query );
        ResultSet rs2 = Manager.rawSQLfind( query2 );

        try {
            Assert.assertTrue( rs.next() );
            Assert.assertTrue( rs2.next() );
        } catch ( SQLException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            try {
                rs.close();
                rs2.close();
            } catch ( SQLException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            Connection a = Connector.getConnection();
            if ( !a.isClosed() ) {
                a.close();
            }
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    @Test(dependsOnMethods = { "TestInitializing" })
    public void TestReplacingCollection() {
        logger.warn( "TestReplacingCollection" );
        Group g = new Group().find( groupId );
        List<Category> oldCategory = g.getCategories();
        List<Category> newCategory = new ArrayList<Category>();

        Category newC1 = new Category();
        newC1.setCategoryName( "newnewnew1" );

        Category newC2 = new Category();
        newC2.setCategoryName( "newnewnew2" );

        Category newC3 = new Category();
        newC3.setCategoryName( "newnewnew3" );

        newCategory.add( newC1 );
        newCategory.add( newC2 );
        newCategory.add( newC3 );

        Category oldC1 = oldCategory.remove( 0 );
        System.out.println( "aoidjaodja" + oldCategory.size() );
        Category oldC2 = oldCategory.remove( 0 );
        newCategory.add( oldC1 );

        g.setCategories( newCategory );
        g.update();


        String query =
                "SELECT * FROM groups_categories WHERE category_id=" + newC1.getId() + " AND group_id = " + g.getId();
        String query2 =
                "SELECT * FROM groups_categories WHERE category_id=" + newC2.getId() + " AND group_id = " + g.getId();
        String query3 =
                "SELECT * FROM groups_categories WHERE category_id=" + newC3.getId() + " AND group_id = " + g.getId();
        String query4 =
                "SELECT * FROM groups_categories WHERE category_id=" + oldC1.getId() + " AND group_id = " + g.getId();
        String query5 =
                "SELECT * FROM groups_categories WHERE category_id=" + oldC2.getId() + " AND group_id = " + g.getId();
        ResultSet rs = Manager.rawSQLfind( query );
        ResultSet rs2 = Manager.rawSQLfind( query2 );
        ResultSet rs3 = Manager.rawSQLfind( query3 );
        ResultSet rs4 = Manager.rawSQLfind( query4 );
        ResultSet rs5 = Manager.rawSQLfind( query5 );

        try {
            Assert.assertTrue( rs.next() );
            Assert.assertTrue( rs2.next() );
            Assert.assertTrue( rs3.next() );
            Assert.assertTrue( rs4.next() );
            Assert.assertFalse( rs5.next() );
        } catch ( SQLException e1 ) {
            e1.printStackTrace();
        } finally {
            try {
                rs.close();
                rs2.close();
                rs3.close();
                rs4.close();
                rs5.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }

        try {
            Connection a = Connector.getConnection();
            if ( !a.isClosed() ) {
                a.close();
            }
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test(dependsOnMethods = { "TestReplacingCollection" })
    public void TestRemovingObjectFromCollection() {
        logger.warn( "TestRemovingObjectFromCollection" );
        Group g = new Group().find( groupId );
        System.out.println( g );
        Category c1 = g.getCategories().remove( 0 );
        g.setUpdateFlag( true );
        g.update();

        String query =
                "SELECT * FROM groups_categories WHERE category_id=" + c1.getId() + " AND group_id = " + g.getId();
        ResultSet rs = Manager.rawSQLfind( query );

        try {
            Assert.assertFalse( rs.next() );
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            Connection a = Connector.getConnection();
            if ( !a.isClosed() ) {
                a.close();
            }
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
