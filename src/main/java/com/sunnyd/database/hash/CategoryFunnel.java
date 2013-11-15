package com.sunnyd.database.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.models.Category;

public class CategoryFunnel implements Funnel<Category> {

    public CategoryFunnel() {
    }

    @Override
    public void funnel( Category category, PrimitiveSink into ) {
        into.putString( category.getCategoryName(), Charsets.UTF_8 );
    }
}