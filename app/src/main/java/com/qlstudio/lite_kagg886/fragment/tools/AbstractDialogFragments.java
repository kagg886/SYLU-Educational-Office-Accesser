package com.qlstudio.lite_kagg886.fragment.tools;

import android.content.Context;
import android.view.View;
import androidx.appcompat.app.AlertDialog;

public abstract class AbstractDialogFragments {

    private final AlertDialog dialog;
    private final View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.show();
        }
    };
    private String text;
    private int id;

    public AbstractDialogFragments(Context ctx) {
        this.dialog = initDialog(ctx);
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public String getText() {
        return text;
    }

    public AbstractDialogFragments setText(String text) {
        this.text = text;
        return this;
    }

    public int getId() {
        return id;
    }

    public AbstractDialogFragments setId(int id) {
        this.id = id;
        return this;
    }

    public abstract AlertDialog initDialog(Context c);
}
