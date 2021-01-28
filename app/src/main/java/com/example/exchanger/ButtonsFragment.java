package com.example.exchanger;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.ArrayList;

public class ButtonsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Spinner spinner1, spinner2;
    String category;
    String selected1, selected2;
    EditText editText;
    TextView textView;
    DataViewModel model;
    UnitManager unitManager = new UnitManager();
    ArrayList<Unit> units;
    ArrayList<Unit_t> units_t;

    private String mParam1;
    private String mParam2;

    public ButtonsFragment() {
        // Required empty public constructor
    }

/*    public static CurrFragment1 newInstance(String param1, String param2) {
        CurrFragment1 fragment = new CurrFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public int getLayout() {
        return R.layout.fragment_buttons;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void content(View view) {
        category = requireActivity().getIntent().getStringExtra("unitName");
        spinner1 = view.findViewById(R.id.spinner1);
        spinner2 = view.findViewById(R.id.spinner2);
        editText = view.findViewById(R.id.editTextNumber);
        model = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = editText.getInputType();
                editText.setInputType(InputType.TYPE_NULL);
                editText.onTouchEvent(event);
                editText.setInputType(inType);
                return true;
            }
        });
        textView = view.findViewById(R.id.textView3);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                convert(s.toString());
            }
        });
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convert(editText.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner1.setOnItemSelectedListener(listener);
        spinner2.setOnItemSelectedListener(listener);
        fillSpinner(category);
        final Observer<String> valueInput = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                editText.setText(s);
            }
        };
        model.getInput().observe(getViewLifecycleOwner(), valueInput);
    }

    private void swap() {
        ArrayAdapter adapter1 = (ArrayAdapter) spinner1.getAdapter();
        ArrayAdapter adapter2 = (ArrayAdapter) spinner2.getAdapter();
        int pos1 = adapter1.getPosition(spinner1.getSelectedItem().toString());
        int pos2 = adapter2.getPosition(spinner2.getSelectedItem().toString());
        spinner1.setSelection(pos2);
        spinner2.setSelection(pos1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        content(view);
        ImageButton button = view.findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swap();
            }
        });
        ImageButton button1 = view.findViewById(R.id.imageButton3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("tag_input", editText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Text copied successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton button2 = view.findViewById(R.id.imageButton4);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("tag_output", textView.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Text copied successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public String[] getUnitNames(ArrayList<Unit> arrayList) {
        String[] names = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            names[i] = arrayList.get(i).name;
        }
        return names;
    }

    public String[] getUnitNames_t(ArrayList<Unit_t> arrayList) {
        String[] names = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            names[i] = arrayList.get(i).name;
        }
        return names;
    }

    public void createSpinner(Spinner spinner, String[] units) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
    }

    public void fillSpinner(String category) {
        units = new ArrayList<>(unitManager.getValues(category));
        units_t = new ArrayList<>(unitManager.getValues_t(category));
        if (Objects.equals(category, "TEMPERATURE"))
        {
            createSpinner(spinner1, getUnitNames_t(units_t));
            createSpinner(spinner2, getUnitNames_t(units_t));
        }
        else
        {
            createSpinner(spinner1, getUnitNames(units));
            createSpinner(spinner2, getUnitNames(units));
        }
        }

    public void algorithm(int n) {
        selected1 = spinner1.getSelectedItem().toString();
        selected2 = spinner2.getSelectedItem().toString();
        float number = Float.parseFloat(editText.getText().toString());
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (selected1.equals(units.get(i).name) && selected2.equals(units.get(j).name)) {
                    if (i == j) {
                        textView.setText(String.valueOf(number));
                        continue;
                    }
                    textView.setText(String.valueOf(number * units.get(i).coefficient * (1 / units.get(j).coefficient)));
                }
            }
        }
    }

    public void algorithm_t(int n) {
        selected1 = spinner1.getSelectedItem().toString();
        selected2 = spinner2.getSelectedItem().toString();
        float number = Float.parseFloat(editText.getText().toString());
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (selected1.equals(units_t.get(i).name) && selected2.equals(units_t.get(j).name)) {
                    if (i == j) {
                        textView.setText(String.valueOf(number));
                        continue;
                    }
                    if (selected1.equals("Celsium") && selected2.equals("Kelvin"))
                    {
                        textView.setText(String.valueOf(number + units_t.get(j).diff));
                    }
                    else if (selected1.equals("Celsium") && selected2.equals("Fahrenheit"))
                    {
                        textView.setText(String.valueOf(number * units_t.get(j).diff + 32));
                    }
                    else if (selected1.equals("Kelvin") && selected2.equals("Celsium"))
                    {
                        textView.setText(String.valueOf(number - units_t.get(i).diff));
                    }
                    else if (selected1.equals("Kelvin") && selected2.equals("Fahrenheit"))
                    {
                        textView.setText(String.valueOf((number - 273.15) * units_t.get(j).diff + 32));
                    }
                    else if (selected1.equals("Fahrenheit") && selected2.equals("Celsium"))
                    {
                        textView.setText(String.valueOf((number - 32) * (1 / units_t.get(i).diff)));
                    }
                    else if (selected1.equals("Fahrenheit") && selected2.equals("Kelvin"))
                    {
                        textView.setText(String.valueOf(number + (1 / units_t.get(i).diff) - 32 + 273.15));
                    }
                }
            }
        }
    }

    public void convert(String s) {
        if (!s.equals("")) {
            if (Objects.equals(category, "DISTANCE") || Objects.equals(category, "WEIGHT")) {
                algorithm(6);
            }
            else if (Objects.equals(category, "CURRENCY")){
                algorithm( 4);
            }

            else {
                algorithm_t(3);
            }
        }
        else {
            textView.setText("");
        }
        model.setOutput(textView.getText().toString());
    }
}