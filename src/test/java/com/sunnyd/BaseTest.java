package com.sunnyd;

import java.util.Date;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;


public class BaseTest {

    @Test
    public void constructorTest() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Integer id = 1;
        Date creationDate = new Date();
        Date lastModifiedDate = new Date();
        map.put( "id", id );
        map.put( "creationDate", creationDate );
        map.put( "lastModifiedDate", lastModifiedDate );

        Base a = new Base( map );
        Assert.assertEquals( id, a.getId() );
        Assert.assertEquals( creationDate, a.getCreationDate() );
        Assert.assertEquals( lastModifiedDate, a.getLastModifiedDate() );
    }

}
