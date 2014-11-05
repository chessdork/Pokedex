package com.github.chessdork.pokedex.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.chessdork.pokedex.models.PokemonType;

public class TypeView extends TextView {
    public TypeView(Context context) {
        super(context);
    }

    public TypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("deprecation")
    public void setType(PokemonType type) {
        setText(type.getName());
        setBackgroundDrawable(type.createGradient());
    }
}
