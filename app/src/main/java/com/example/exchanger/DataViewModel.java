package com.example.exchanger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel {
    private final MutableLiveData<String> input = new MutableLiveData<>();
    private final MutableLiveData<String> output = new MutableLiveData<>();

    public void setInput(String item) {
        input.setValue(item);
    }

    public void setOutput(String item) {
        output.setValue(item);
    }

    public LiveData<String> getInput() {
        return input;
    }

    public LiveData<String> getOutput() {
        return output;
    }
}