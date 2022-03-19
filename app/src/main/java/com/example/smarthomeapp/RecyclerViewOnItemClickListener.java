package com.example.smarthomeapp;

import android.view.View;

public interface RecyclerViewOnItemClickListener {
    void onClick(View v, int position);
    void onClickEdit(View v, int position);
    void onClickDel(View v, int position);
}
