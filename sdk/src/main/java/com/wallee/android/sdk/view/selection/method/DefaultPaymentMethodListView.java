package com.wallee.android.sdk.view.selection.method;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Default view for listing the payment methods.
 */
public class DefaultPaymentMethodListView extends RecyclerView {
    public DefaultPaymentMethodListView(Context context) {
        super(context);
    }

    public DefaultPaymentMethodListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultPaymentMethodListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
