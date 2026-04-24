package com.tp.gestiondepenses.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tp.gestiondepenses.model.Depense;
import com.tp.gestiondepenses.repository.DepenseRepository;
import com.tp.gestiondepenses.repository.RevenuRepository;

import java.util.Calendar;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private DepenseRepository depenseRepository;
    private RevenuRepository revenuRepository;

    private LiveData<List<Depense>> lastFiveDepenses;
    private MutableLiveData<Double> solde = new MutableLiveData<>();
    private MutableLiveData<Double> totalDepenses = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        depenseRepository = new DepenseRepository(application);
        revenuRepository = new RevenuRepository(application);

        lastFiveDepenses = depenseRepository.getLastFiveDepenses();
        calculerSolde();
    }

    private void calculerSolde() {
        Calendar calendar = Calendar.getInstance();
        String mois = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String annee = String.valueOf(calendar.get(Calendar.YEAR));

        // On utilise new Thread() au lieu de AppDatabase.databaseWriteExecutor
        new Thread(() -> {
            double totalRev = revenuRepository.getTotalRevenusParMois(mois, annee);
            double totalDep = depenseRepository.getTotalDepensesParMois(mois, annee);

            solde.postValue(totalRev - totalDep);
            totalDepenses.postValue(totalDep);
        }).start();
    }

    public LiveData<List<Depense>> getLastFiveDepenses() {
        return lastFiveDepenses;
    }

    public MutableLiveData<Double> getSolde() {
        return solde;
    }

    public MutableLiveData<Double> getTotalDepenses() {
        return totalDepenses;
    }

    public void refresh() {
        calculerSolde();
    }
}