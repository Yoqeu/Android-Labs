package com.example.exchanger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KeyboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyboardFragment extends Fragment {
    String input;
    Boolean dot = false;
    private DataViewModel model;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public KeyboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static KeyboardFragment newInstance(String param1, String param2) {
        KeyboardFragment fragment = new KeyboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final String LOG_TAG = ButtonsFragment.class.getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final int[] buttonsId = {R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8,
                R.id.b9, R.id.b10, R.id.b11, R.id.imageButton2};
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.b1: b1Click(); break;
                    case R.id.b2: b2Click(); break;
                    case R.id.b3: b3Click(); break;
                    case R.id.b4: b4Click(); break;
                    case R.id.b5: b5Click(); break;
                    case R.id.b6: b6Click(); break;
                    case R.id.b7: b7Click(); break;
                    case R.id.b8: b8Click(); break;
                    case R.id.b9: b9Click(); break;
                    case R.id.b10: b10Click(); break;
                    case R.id.b11: b11Click(); break;
                    case R.id.imageButton2: delete(); break;
                }
            }
        };
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        model = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        final Observer<String> value = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.equals("")) {
                    input = s;
                }
            }
        };
        model.getInput().observe(getViewLifecycleOwner(), value);
        Button[] b = new Button[11];
        for (int i = 0; i < 11; i++) {
            b[i] = view.findViewById(buttonsId[i]);
            b[i].setOnClickListener(listener);
        }
        ImageButton del = view.findViewById(R.id.imageButton2);
        del.setOnClickListener(listener);
        return view;
    }

    public void b1Click() {
        if (input == null) {
            input = "1";
        }
        else {
            input += "1";
        }
        model.setInput(input);
    }

    public void b10Click() {
        if (!dot) {
            if (input == null) {
                input = ".";
            } else {
                input += ".";
            }
        }
        dot = true;
        model.setInput(input);
    }

    public void b9Click() {
        if (input == null) {
            input = "9";
        }
        else {
            input += "9";
        }
        model.setInput(input);
    }

    public void b8Click() {
        if (input == null) {
            input = "8";
        }
        else {
            input += "8";
        }
        model.setInput(input);
    }

    public void b7Click() {
        if (input == null) {
            input = "7";
        }
        else {
            input += "7";
        }
        model.setInput(input);
    }

    public void b6Click() {
        if (input == null) {
            input = "6";
        }
        else {
            input += "6";
        }
        model.setInput(input);
    }

    public void b5Click() {
        if (input == null) {
            input = "5";
        }
        else {
            input += "5";
        }
        model.setInput(input);
    }

    public void b4Click() {
        if (input == null) {
            input = "4";
        }
        else {
            input += "4";
        }
        model.setInput(input);
    }

    public void b2Click() {
        if (input == null) {
            input = "2";
        }
        else {
            input += "2";
        }
        model.setInput(input);
    }

    public void b3Click() {
        if (input == null) {
            input = "3";
        }
        else {
            input += "3";
        }
        model.setInput(input);
    }

    public void b11Click() {
        if (input == null) {
            input = "0";
        }
        else {
            input += "0";
        }
        model.setInput(input);
    }

    public void delete() {
        if (input != null && !input.equals("")) {
            input = input.substring(0, input.length() - 1);
            if (!input.contains(".")) {
                dot = false;
            }
            model.setInput(input);
        }
    }
}