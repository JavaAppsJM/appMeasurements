package be.hvwebsites.metingen.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.ManageEntities;
import be.hvwebsites.metingen.R;
import be.hvwebsites.metingen.constants.ElectricityPriceType;
import be.hvwebsites.metingen.constants.HomeService;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.entities.Location;
import be.hvwebsites.metingen.entities.Meter;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class MeterEditFragment extends Fragment {
    private EntitiesViewModel viewModel;
    private int iDToUpdate = StaticData.ITEM_NOT_FOUND;
    private String locationSelection = "";

    // Toegevoegd vanuit android tutorial
    public MeterEditFragment(){
        super(R.layout.fragment_meter_edit);
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
        return inflater.inflate(R.layout.fragment_meter_edit, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Schermvelden labels invullen
        // LocationMeterLabel
        TextView labelLocation = view.findViewById(R.id.labelLocationMeter);
        labelLocation.setText(SpecificData.LABEL_LOCATION_T2);
        // Meter Name
        TextView meterNameLabel = view.findViewById(R.id.labelNameMeter);
        meterNameLabel.setText(SpecificData.LABEL_METERNAME_T2);
        // NutMeter
        TextView nutMeterLabel = view.findViewById(R.id.labelNut);
        nutMeterLabel.setText(SpecificData.LABEL_NUT_T2);
        // Radiobuttons nut meter
        RadioButton nutWater = view.findViewById(R.id.radioButton1);
        nutWater.setText(SpecificData.RADIOBUTTON_WATER_T2);
        nutWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonNutClicked(v);
            }
        });
        RadioButton nutElectricity = view.findViewById(R.id.radioButton2);
        nutElectricity.setText(SpecificData.RADIOBUTTON_ELECTRICITY_T2);
        nutElectricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonNutClicked(v);
            }
        });
        // TypeMeter
        TextView typeMeterLabel = view.findViewById(R.id.labelTypeMeter);
        typeMeterLabel.setText(SpecificData.LABEL_TYPE_T2);
        // Radiobuttons type meter
        RadioButton typeDag = view.findViewById(R.id.radioButton3);
        typeDag.setText(SpecificData.RADIOBUTTON_DAY_T2);
        typeDag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonTypeClicked(v);
            }
        });
        RadioButton typeNacht = view.findViewById(R.id.radioButton4);
        typeNacht.setText(SpecificData.RADIOBUTTON_NIGHT_T2);
        typeNacht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonTypeClicked(v);
            }
        });
        RadioButton typeExNacht = view.findViewById(R.id.radioButton5);
        typeExNacht.setText(SpecificData.RADIOBUTTON_XNIGHT_T2);
        typeExNacht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonTypeClicked(v);
            }
        });
        // button
        Button saveButton = view.findViewById(R.id.buttonSaveMeter);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(EntitiesViewModel.class);

        // Wat zijn de argumenten die werden meegegeven
        // De actie
        String action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);
        if (action.equals(StaticData.ACTION_UPDATE)){
            iDToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_ID);
            // Bepaal geselecteerde meter obv meegegeven Id
            Meter meterToUpdate = viewModel.getMeterById(new IDNumber(iDToUpdate));
            // Vul Scherm in met gegevens vd bestaande meter
            EditText nameView = view.findViewById(R.id.editNameMeter);
            nameView.setText(meterToUpdate.getEntityName());
            // Radiobuttons invullen vlgns gegevens meter
            if (meterToUpdate.getHomeServiceType() == HomeService.WATER){
                nutWater.setChecked(true);
            }else {
                nutElectricity.setChecked(true);
            }
            saveButton.setText(SpecificData.BUTTON_AANPASSEN);
            // De locatie vd meter
            locationSelection = viewModel.getLocationById(meterToUpdate.getMeterLocationId()).getEntityName();
        }else {
            // new
            // De locatie vd meter
            locationSelection = requireArguments().getString(SpecificData.LOCATION_SPIN);
        }
        TextView locationMeter = view.findViewById(R.id.locationMeter);
        locationMeter.setText(locationSelection);

        // Als button ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Meter to save
                Meter meterToSave;
                // Schermvelden in meter to save steken
                EditText nameMeter = view.findViewById(R.id.editNameMeter);
                // Update = bestaande meter ; new = nieuwe meter
                if (action.equals(StaticData.ACTION_UPDATE)){
                    meterToSave = viewModel.getMeterById(new IDNumber(iDToUpdate));
//                    viewModel.getMeterById(new IDNumber(iDToUpdate)).setEntityName(String.valueOf(nameMeter.getText()));
                }else {
                    meterToSave = new Meter(viewModel.getBasedir(), false);
                    // Location bepalen voor de nieuwe meter obv de locationselection
                    Location meterLocation = viewModel.getLocationByName(locationSelection);
                    meterToSave.setMeterLocationId(meterLocation.getEntityId());
                }
                // Meter gegevens invullen/aanpassen
                meterToSave.setEntityName(String.valueOf(nameMeter.getText()));
                if (nutWater.isChecked()){
                    meterToSave.setHomeServiceType(HomeService.WATER);
                }else if (nutElectricity.isChecked()){
                    meterToSave.setHomeServiceType(HomeService.ELECTRICITY);
                }else {
                    meterToSave.setHomeServiceType(HomeService.OTHER);
                }
                if (typeDag.isChecked()){
                    meterToSave.setElectricityPriceType(ElectricityPriceType.DAY);
                }else if (typeNacht.isChecked()){
                    meterToSave.setElectricityPriceType(ElectricityPriceType.NIGHT);
                }else if (typeExNacht.isChecked()){
                    meterToSave.setElectricityPriceType(ElectricityPriceType.EXCLUSIVE_NIGHT);
                }else {
                    meterToSave.setElectricityPriceType(ElectricityPriceType.OTHER);
                }
                // Meter toevoegen/updaten in de meterlist
                if (action.equals(StaticData.ACTION_UPDATE)){
                    // Bepaal index vd meter die moet aangepast worden obv ID
                    int indexMeterToSave = viewModel.getMeterIndexById(new IDNumber(iDToUpdate));
                    if (indexMeterToSave != StaticData.ITEM_NOT_FOUND){
                        // indien index gevonden, replacen
                        viewModel.getMeterList().set(indexMeterToSave, meterToSave);
                    }else {
                        // Meterindex niet gevonden
                        boolean meterNietGevonden = true;
                    }
                }else {
                    // nieuwe meter toevoegen in de list
                    viewModel.getMeterList().add(meterToSave);
                }
                // Bewaren
                viewModel.storeMeters();
                // Terug gaan nr lijst
                Intent replyIntent = new Intent(getContext(), ManageEntities.class);
                replyIntent.putExtra(SpecificData.ENTITY_TYPE, SpecificData.ENTITY_TYPE_2);
                replyIntent.putExtra(SpecificData.LOCATION_SPIN, locationSelection);
                startActivity(replyIntent);
            }
        });
    }

    public void onRadioButtonNutClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    // Water is gekozen
                    break;
            case R.id.radioButton2:
                if (checked)
                    // Elektriciteit is gekozen
                    break;
        }
    }

    public void onRadioButtonTypeClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton3:
                if (checked)
                    // Dag is gekozen
                    break;
            case R.id.radioButton4:
                if (checked)
                    // Nacht is gekozen
                    break;
            case R.id.radioButton5:
                if (checked)
                    // XNacht is gekozen
                    break;
        }
    }
}