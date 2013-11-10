package com.sunnyd.database.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.models.GrandChild;

/**
 * Created with IntelliJ IDEA.
 * User: snw
 * Date: 11/10/2013
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class GrandChildFunnel implements Funnel<GrandChild> {

    public GrandChildFunnel() {
    }

    @Override
    public void funnel( GrandChild gc, PrimitiveSink into ) {
        into.putString( gc.getGrandChildName(), Charsets.UTF_8 );
    }
}
