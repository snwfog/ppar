package com.sunnyd.database.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.models.Child;
import com.sunnyd.models.Document;

public class DocumentFunnel implements Funnel<Document> {

    public DocumentFunnel() {
    }

    @Override
    public void funnel( Document document, PrimitiveSink into ) {
        into.putString( document.getDocName(), Charsets.UTF_8 );
    }
}