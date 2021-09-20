package me.tt5397194.sharkcpp.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.tt5397194.sharkcpp.R;

public class SwitchAdapter extends BaseAdapter {

    private Context m_content;
    public List<String> list_title;
    public List<String> list_value;
    public Switch[] list_switch;
    public List<Boolean> list_check;

    public SwitchAdapter(Context context) {
        m_content = context;
        list_title = new ArrayList<>();
        list_value = new ArrayList<>();
        list_check = new ArrayList<>();
    }

    public void addItem(String title, String value, boolean check) {
        list_title.add(title);
        list_check.add(check);
        list_value.add(value);
    }

    @Override
    public int getCount() {
        return list_title.size();
    }

    @Override
    public Object getItem(int i) {
        return list_title.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(m_content).inflate(R.layout.app_list_item, null);
        }
        TextView tv = view.findViewById(R.id.tv);
        tv.setText(list_title.get(i));

        if (list_switch == null) {
            list_switch = new Switch[list_title.size()];
        }
        Switch sv = view.findViewById(R.id.sv);
        sv.setChecked(list_check.get(i));
        list_switch[i] = sv;
        return view;
    }
}
