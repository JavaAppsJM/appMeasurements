package be.hvwebsites.metingen.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.EditEntity;
import be.hvwebsites.metingen.ManageEntities;
import be.hvwebsites.metingen.R;
import be.hvwebsites.metingen.adapters.TextItemListAdapter;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.entities.Location;
import be.hvwebsites.metingen.entities.Meter;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class MeasurementListFragment extends Fragment {
    private EntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    private String locationSelection = "";
    private String meterSelection = "";

    public MeasurementListFragment() {
        super(R.layout.fragment_measurement);
    }

    private FragmentManager getSupportFragmentManager() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measurement, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(EntitiesViewModel.class);

        // Wat zijn de argumenten die werden meegegeven
        locationSelection = requireArguments().getString(SpecificData.LOCATION_SPIN);
        meterSelection = requireArguments().getString(SpecificData.METER_SPIN);

        // Recyclerview definieren
        RecyclerView recyclerView = view.findViewById(R.id.recycler_measurements);
        final TextItemListAdapter adapter = new TextItemListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Location spinner definieren
        Spinner locationSpinner = (Spinner) view.findViewById(R.id.spinner_location);
        ArrayAdapter<String> locSpinAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_item);
        locSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Spinner vullen met locations
        List<String> locationNameList = viewModel.getLocationNameList();
        if (locationNameList.size() == 0){
            locationSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                    locSpinAdapter, R.layout.contact_spinner_nothing_selected, getContext()));
        }else {
            locationSpinner.setAdapter(locSpinAdapter);
        }
        locSpinAdapter.addAll(locationNameList);
        int locationIndex = getListIndexByName(locationSelection, locationNameList);
        // Als er al een locationSelection is moet die ingevyld worden in de spinner
        if (locationIndex != StaticData.ITEM_NOT_FOUND){
            // animate parameter met false staan om het onnodig afvuren vd spinner tegen te gaan
            locationSpinner.setSelection(locationIndex, false);
        }

        // Meter spinner definieren
        Spinner meterSpinner = (Spinner) view.findViewById(R.id.spinner_meter);
        ArrayAdapter<String> meterSpinAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_item);
        meterSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Spinner vullen met meters
        Location location = viewModel.getLocationByName(locationSelection);
        List<String> meterNameList = new ArrayList<>();
        if (location != null){
            meterNameList = viewModel.getMeterNameList(location);
        }
        if (meterNameList.size() == 0){
            meterSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                    meterSpinAdapter, R.layout.contact_spinner_nothing_selected, getContext()));
        }else {
            meterSpinner.setAdapter(meterSpinAdapter);
        }
        meterSpinAdapter.addAll(meterNameList);
        int meterIndex = getListIndexByName(meterSelection, meterNameList);
        // Als er al een meterSelection is moet die ingevyld worden in de spinner
        if (meterIndex != StaticData.ITEM_NOT_FOUND){
            // animate parameter met false staan om het onnodig afvuren vd spinner tegen te gaan
            meterSpinner.setSelection(meterIndex, false);
        }

        // Recyclerview invullen
        Location locSelection = viewModel.getLocationByName(locationSelection);
        Meter meterSelect = viewModel.getMeterByNameForLocation(meterSelection, locSelection);
        itemList.clear();
        itemList.addAll(viewModel.getMeasurementsforMeter(locSelection, meterSelect));
        adapter.setEntityType(SpecificData.ENTITY_TYPE_3);
        adapter.setItemList(itemList);
        if (itemList == null){
            Toast.makeText(getActivity(),
                    SpecificData.NO_MEASUREMENTS_YET,
                    Toast.LENGTH_LONG).show();
        }

        // selection listener activeren voor location spinner
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationSelection = String.valueOf(parent.getItemAtPosition(position));
                viewModel.setSpinnerSelection(locationSelection);

                // meterspinner opnieuw invullen met de meters voor de gekozen location
                Location location = viewModel.getLocationByName(locationSelection);
                List<String> meterNameList = viewModel.getMeterNameList(location);
                if (meterNameList.size() == 0){
                    meterSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                            meterSpinAdapter, R.layout.contact_spinner_nothing_selected, getContext()));
                }else {
                    meterSpinner.setAdapter(meterSpinAdapter);
                }
                meterSpinAdapter.addAll(meterNameList);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // selection listener activeren voor meter spinner
        meterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Als er geen locatie selection is, moet je nog geen meter kiezen
                if (locationSelection.equals("")){
                    // do nothing
                }else {
                    // er is een locationSelection dus de selectie vr de meter wordt aanvaard
                    meterSelection = String.valueOf(parent.getItemAtPosition(position));
                    viewModel.setSpinnerSelection(meterSelection);
                    // measurements bepalen obv locationSelection en meterSelection
                    Location locSelection = viewModel.getLocationByName(locationSelection);
                    if (locSelection != null){
                        Meter meterSelect = viewModel.getMeterByNameForLocation(meterSelection, locSelection);
                        if (meterSelect != null){
                            itemList.clear();
                            itemList.addAll(viewModel.getMeasurementsforMeter(locSelection, meterSelect));
                            adapter.setEntityType(SpecificData.ENTITY_TYPE_3);
                            adapter.setItemList(itemList);
                            if (itemList == null){
                                Toast.makeText(getActivity(),
                                        SpecificData.NO_MEASUREMENTS_YET,
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public int getListIndexByName(String inName, List<String> inList){
        // bepaalt een index obv name
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).equals(inName)) {
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }


}