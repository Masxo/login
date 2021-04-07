package com.bs.login.ui.home;

import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bs.login.FragmentActivityCommunicator;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    //#region PROPERTIES & VALUES
    private MutableLiveData<List<Map<String, Object>>> DB_dataset;
    private MutableLiveData<FragmentActivityCommunicator> communicator;
    //#endregion

    //#region CONSTRUCTOR
    public HomeViewModel() {
        DB_dataset = new MutableLiveData<>();
        communicator = new MutableLiveData<>();
    }
    //#endregion

    //#region GETTERS, SETTERS & ETC.
    public LiveData<List<Map<String, Object>>> setDB_dataset(List<Map<String, Object>> DB_dataset) {
        if (DB_dataset != null)
            this.DB_dataset.setValue(DB_dataset);
        return this.DB_dataset;
    }

    public LiveData<FragmentActivityCommunicator> setCommunicator(FragmentActivityCommunicator communicator) {
        if (communicator != null)
            this.communicator.setValue(communicator);
        return this.communicator;
    }
    //#endregion
}