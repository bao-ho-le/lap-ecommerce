package com.ptithcm.frontend.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ptithcm.frontend.models.ProfileOption;
import com.ptithcm.frontend.repository.MockEcommerceRepository;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final MockEcommerceRepository repository;
    private final MutableLiveData<List<ProfileOption>> options = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = MockEcommerceRepository.getInstance(application);
        options.setValue(repository.getProfileOptions());
    }

    public LiveData<List<ProfileOption>> getOptions() {
        return options;
    }

    public String getUserName() {
        return repository.getUserName();
    }

    public String getUserEmail() {
        return repository.getUserEmail();
    }
}