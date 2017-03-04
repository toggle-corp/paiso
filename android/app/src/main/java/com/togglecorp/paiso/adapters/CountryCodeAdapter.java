package com.togglecorp.paiso.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.togglecorp.paiso.R;

import java.util.ArrayList;
import java.util.List;

public class CountryCodeAdapter extends BaseAdapter {
    private List<Pair<Integer, String>> mCodes;

    public CountryCodeAdapter(Context context) {
        String[] codes = context.getResources().getStringArray(R.array.country_codes);

        mCodes = new ArrayList<>();
        for (String code: codes) {
            String[] codeParts = code.split(",");
            mCodes.add(new Pair<>(Integer.parseInt(codeParts[0]), codeParts[1]));
        }
    }

    @Override
    public int getCount() {
        return mCodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mCodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mCodes.get(position).first;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(String.format("+%d (%s)", mCodes.get(position).first, mCodes.get(position).second));

        return convertView;
    }
}
