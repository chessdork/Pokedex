package com.github.chessdork.smogon;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayPokemonFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DisplayPokemonFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String POKEMON_NAME = "pokemon_name";

    private String mName;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Parameter 1.
     * @return A new instance of fragment DisplayPokemonFragment.
     */

    public static DisplayPokemonFragment newInstance(String name) {
        DisplayPokemonFragment fragment = new DisplayPokemonFragment();
        Bundle args = new Bundle();
        args.putString(POKEMON_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    public DisplayPokemonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(POKEMON_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_display_pokemon, container, false);
        TextView textView = (TextView) view.findViewById(R.id.pokemon_name);
        textView.setText(mName);
        return view;
    }
}
