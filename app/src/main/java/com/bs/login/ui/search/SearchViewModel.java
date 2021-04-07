package com.bs.login.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;

import java.util.List;
import java.util.Map;

public class SearchViewModel extends ViewModel {

    //#region PROPERTIES & VALUES
    private MutableLiveData<CollectionReference> DB;
    private MutableLiveData<List<String>> data;
    //#endregion

    //#region CONSTRUCTOR
    public SearchViewModel() {
        DB = new MutableLiveData<>();
        data = new MutableLiveData<>();
    }
    //#endregion

    //#region GETTERS, SETTERS & ETC.
    public LiveData<CollectionReference> setDB(CollectionReference DB) {
        if (DB != null)
            this.DB.setValue(DB);
        return this.DB;
    }

    public LiveData<List<String>> setData(List<String> data) {
        if (data != null)
            this.data.setValue(data);
        return this.data;
    }
    //#endregion
}