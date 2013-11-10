package com.sunnyd.database.hash;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.sunnyd.models.Peer;

public class PeerFunnel implements Funnel<Peer> {

    public PeerFunnel() {
    }

    @Override
    public void funnel( Peer peer, PrimitiveSink into ) {
        into.putString( peer.getFirstName(), Charsets.UTF_8 )
                .putString( peer.getLastName(), Charsets.UTF_8 )
                .putString( peer.getEmail(), Charsets.UTF_8 )
                .putString( peer.getUserName(), Charsets.UTF_8 )
                .putString( peer.getPassword(), Charsets.UTF_8 )
                .putInt( peer.getPoint() )
                .putInt( peer.getRankId() )
                .putString( peer.getPersonalWebsite(), Charsets.UTF_8 );
    }
}
