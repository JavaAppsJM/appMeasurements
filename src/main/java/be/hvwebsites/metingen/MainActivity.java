package be.hvwebsites.metingen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.adapters.TextItemListAdapter;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.entities.Location;
import be.hvwebsites.metingen.entities.Meter;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class MainActivity extends AppCompatActivity {
    private EntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    private String locationSelection = "";
    private String meterSelection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Data ophalen
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(EntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
        } else if (viewModelStatus.getReturnCode() == 100) {
            Toast.makeText(MainActivity.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Ophalen Cookies voor spinners location & meter
        CookieRepository cookieRepository = new CookieRepository(baseDir);
        locationSelection = cookieRepository.getCookieValueFromLabel(SpecificData.LOCATION_SPIN);
        meterSelection = cookieRepository.getCookieValueFromLabel(SpecificData.METER_SPIN);

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recycler_measurements);
        final TextItemListAdapter adapter = new TextItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Location spinner definieren
        Spinner locationSpinner = (Spinner) findViewById(R.id.spinner_location);
        ArrayAdapter<String> locSpinAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        locSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Spinner vullen met locations
        List<String> locationNameList = viewModel.getLocationNameList();
        if (locationNameList.size() == 0){
            locationSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                    locSpinAdapter, R.layout.contact_spinner_nothing_selected, this));
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
        Spinner meterSpinner = (Spinner) findViewById(R.id.spinner_meter);
        ArrayAdapter<String> meterSpinAdapter = new ArrayAdapter(this,
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
                    meterSpinAdapter, R.layout.contact_spinner_nothing_selected, this));
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
            Toast.makeText(this,
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
                            meterSpinAdapter, R.layout.contact_spinner_nothing_selected, getApplicationContext()));
                }else {
                    meterSpinner.setAdapter(meterSpinAdapter);
                }
                meterSpinAdapter.clear();
                meterSpinAdapter.addAll(meterNameList);

                // locationselection in Cookie updaten
                if (cookieRepository.bestaatCookie(SpecificData.LOCATION_SPIN) != StaticData.ITEM_NOT_FOUND){
                    cookieRepository.deleteCookie(SpecificData.LOCATION_SPIN);
                }
                Cookie locCookie = new Cookie();
                locCookie.setCookieLabel(SpecificData.LOCATION_SPIN);
                locCookie.setCookieValue(locationSelection);
                cookieRepository.addCookie(locCookie);

                // TODO: meting lijst leeg maken of recyclerview clearen
                itemList.clear();
                adapter.setEntityType(SpecificData.ENTITY_TYPE_3);
                adapter.setItemList(itemList);
                if (itemList == null){
                    Toast.makeText(MainActivity.this,
                            SpecificData.NO_MEASUREMENTS_YET,
                            Toast.LENGTH_LONG).show();
                }
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
                                Toast.makeText(MainActivity.this,
                                        SpecificData.NO_MEASUREMENTS_YET,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        // meterselection in Cookie updaten
                        if (cookieRepository.bestaatCookie(SpecificData.METER_SPIN) != StaticData.ITEM_NOT_FOUND){
                            cookieRepository.deleteCookie(SpecificData.METER_SPIN);
                        }
                        Cookie locCookie = new Cookie();
                        locCookie.setCookieLabel(SpecificData.METER_SPIN);
                        locCookie.setCookieValue(meterSelection);
                        cookieRepository.addCookie(locCookie);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Button definieren
        Button newbutton = findViewById(R.id.button_new_msrmnt);
        newbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!locationSelection.equals(StaticData.EMPTY_STRING))
                        && (!meterSelection.equals(StaticData.EMPTY_STRING))){
                    // locatie & meter zijn gekend, er mag een nieuwe meting geregistreerd worden
                    Intent mIntent = new Intent(MainActivity.this, EditMeasurement.class);
                    mIntent.putExtra(SpecificData.ENTITY_TYPE, SpecificData.ENTITY_TYPE_3);
                    mIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                    mIntent.putExtra(SpecificData.LOCATION_SPIN, locationSelection);
                    mIntent.putExtra(SpecificData.METER_SPIN, meterSelection);
                    startActivity(mIntent);
                }else {
                    Toast.makeText(MainActivity.this,
                            "locatie of meter zijn nog niet gekend, dus kan er geen meting geregistreerd worden",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent mainIntent = new Intent(MainActivity.this, ManageEntities.class);
        switch (item.getItemId()) {
            case R.id.menu_beheer_locaties:
                // ga naar activity ManageEntities vr locaties
                mainIntent.putExtra(SpecificData.ENTITY_TYPE, SpecificData.ENTITY_TYPE_1);
                startActivity(mainIntent);
                return true;
            case R.id.menu_beheer_meters:
                // ga naar activity ManageEntities vr meters
                mainIntent.putExtra(SpecificData.ENTITY_TYPE, SpecificData.ENTITY_TYPE_2);
                startActivity(mainIntent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
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