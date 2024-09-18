package codinghacks.org.gpayclone.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import codinghacks.org.gpayclone.utils.DataManager;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final DataManager dataManager;

    public ViewModelFactory(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(dataManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}