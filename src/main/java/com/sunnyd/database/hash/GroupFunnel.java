package com.sunnyd.database.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.models.Group;

public class GroupFunnel implements Funnel<Group> {

    public GroupFunnel() {
    }

    @Override
    public void funnel( Group group, PrimitiveSink into ) {
        into.putString( group.getGroupName(), Charsets.UTF_8 );
    }
}