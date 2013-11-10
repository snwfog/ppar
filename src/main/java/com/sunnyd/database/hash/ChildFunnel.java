package com.sunnyd.database.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.models.Child;

/**
 * Created with IntelliJ IDEA.
 * User: snw
 * Date: 11/10/2013
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChildFunnel implements Funnel<Child> {

    public ChildFunnel() {
    }

    @Override
    public void funnel( Child child, PrimitiveSink into ) {
        into.putString( child.getChildName(), Charsets.UTF_8 );
    }
}
