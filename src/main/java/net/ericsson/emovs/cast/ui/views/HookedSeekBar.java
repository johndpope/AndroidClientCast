package net.ericsson.emovs.cast.ui.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Joao Coelho on 2018-02-23.
 */

public class HookedSeekBar extends android.support.v7.widget.AppCompatSeekBar {
    public HookedSeekBar(Context context) {
        super(context);
    }

    public HookedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HookedSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getMax() {
        return super.getMax();
    }

    @Override
    public int getMin() {
        return super.getMin();
    }

    @Override
    public int getProgress() {
        return super.getProgress();
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
        return;
    }

    @Override
    public void setMin(int min) {
        super.setMin(min);
        return;
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
    }

    @Override
    public void setProgress(int progress, boolean animate) {
        super.setProgress(progress, animate);
    }

    @Override
    public void setIndeterminate (boolean indeterminate) {
        super.setIndeterminate(indeterminate);
    }

}
