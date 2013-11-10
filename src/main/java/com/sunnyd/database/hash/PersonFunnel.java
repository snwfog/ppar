package com.sunnyd.database.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.models.Person;

/**
 * Created with IntelliJ IDEA.
 * User: snw
 * Date: 11/10/2013
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class PersonFunnel implements Funnel<Person> {

    public PersonFunnel() {
    }

    @Override
    public void funnel( Person person, PrimitiveSink into ) {
        into.putString( person.getFirstName(), Charsets.UTF_8 ).putString( person.getLastName(), Charsets.UTF_8 );
    }

}
