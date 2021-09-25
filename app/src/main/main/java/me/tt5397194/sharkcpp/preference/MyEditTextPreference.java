package me.tt5397194.sharkcpp.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.EditTextPreference;

public class MyEditTextPreference extends EditTextPreference {
    public MyEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getSummary() {
        //让EditTextPreference的summary显示当前的值
        String summary = this.getText();
        if (summary == null || summary.equals("")) {
            return "null";
        }
        return summary;
    }
}
