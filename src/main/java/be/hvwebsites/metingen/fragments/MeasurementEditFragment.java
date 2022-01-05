package be.hvwebsites.metingen.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.ManageEntities;
import be.hvwebsites.metingen.R;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.entities.Location;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class MeasurementEditFragment extends Fragment {
    private EntitiesViewModel viewModel;
    private int iDToUpdate = StaticData.ITEM_NOT_FOUND;

    // Toegevoegd vanuit android tutorial
    public MeasurementEditFragment(){
        super(R.layout.fragment_measurement_edit);
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
        return inflater.inflate(R.layout.fragment_measurement_edit, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // button
        Button saveButton = view.findViewById(R.id.buttonSaveMsrmnt);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(EntitiesViewModel.class);

        // Wat zijn de argumenten die werden meegegeven
        String action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);
        if (action.equals(StaticData.ACTION_UPDATE)){
            iDToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_ID);
            // Bepaal geselecteerde location bepalen obv meegegeven index
            Location locationToUpdate = (Location) viewModel.getLocationById(new IDNumber(iDToUpdate));
            // Vul Scherm in met gegevens
            EditText nameView = view.findViewById(R.id.locationNameMsrmnt);
            nameView.setText(locationToUpdate.getEntityName());
            saveButton.setText(SpecificData.BUTTON_AANPASSEN);
        }

        // Als button ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definitie inputvelden
                EditText nameView = view.findViewById(R.id.editNameLocation);
                // Gegevens overnemen vh scherm
                if (action.equals(StaticData.ACTION_UPDATE)){ // Update
                    viewModel.getLocationById(new IDNumber(iDToUpdate)).setEntityName(String.valueOf(nameView.getText()));
                }else { // New
                    Location newLocation = new Location(viewModel.getBasedir(), false);
                    newLocation.setEntityName(String.valueOf(nameView.getText()));
                    viewModel.getLocationList().add(newLocation);
                }
                viewModel.storeLocations();
                Intent replyIntent = new Intent(getContext(), ManageEntities.class);
                replyIntent.putExtra(SpecificData.ENTITY_TYPE, SpecificData.ENTITY_TYPE_1);
                startActivity(replyIntent);
            }
        });
    }
}