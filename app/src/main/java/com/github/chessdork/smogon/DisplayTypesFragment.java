package com.github.chessdork.smogon;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DisplayTypesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_types, container, false);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.table);

        for (Type type : Type.values()) {
            TextView textView = new TextView(getActivity());
            Shader gradient = new LinearGradient(0,0,0,20, type.getColor1(), type.getColor2(), Shader.TileMode.CLAMP);
            textView.getPaint().setShader(gradient);
            textView.setTextColor(Color.BLACK);
            textView.setText(type.getName());
            layout.addView(textView);
        }
        return view;
    }
}
