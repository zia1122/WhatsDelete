package com.malam.whatsdelet.nolastseen.hiddenchat.Model;

import androidx.recyclerview.widget.GridLayoutManager;

public class Recyclerview_Spaning_Model extends GridLayoutManager.SpanSizeLookup {

    int spanPos, spanCnt1, spanCnt2;

    public Recyclerview_Spaning_Model(int spanPos, int spanCnt1, int spanCnt2) {
        super();
        this.spanPos = spanPos;
        this.spanCnt1 = spanCnt1;
        this.spanCnt2 = spanCnt2;
    }

    @Override
    public int getSpanSize(int position) {
        if (position>=9){
            return (position % spanPos ==0 ? spanCnt2 : spanCnt1);
    }
        return 0;
    }
}