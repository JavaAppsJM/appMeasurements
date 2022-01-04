package be.hvwebsites.metingen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.metingen.constants.SpecificData;
import be.hvwebsites.metingen.fragments.LocationEditFragment;
import be.hvwebsites.metingen.fragments.MeasurementListFragment;
import be.hvwebsites.metingen.viewmodels.EntitiesViewModel;

public class MainActivity extends AppCompatActivity {
    private EntitiesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, EditEntity.class);
                mainIntent.putExtra(SpecificData.ENTITY_TYPE, SpecificData.ENTITY_TYPE_3);
                mainIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                startActivity(mainIntent);
            }
        });

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

        // Fragment bundle samenstellen om mee te geven aan fragment
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putString(SpecificData.LOCATION_SPIN,
                cookieRepository.getCookieValueFromLabel(SpecificData.LOCATION_SPIN));
        fragmentBundle.putString(SpecificData.METER_SPIN,
                cookieRepository.getCookieValueFromLabel(SpecificData.METER_SPIN));

        // Creeer fragment_measurements
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.frag_measurements, MeasurementListFragment.class, fragmentBundle)
                    .commit();
        }






/*

        if (locationSpin.equals(String.valueOf(CookieRepository.COOKIE_NOT_FOUND))){
            // er is nog geen default location
            // nothing selected spinner definieren voor locationSpinner
//            shopFilterSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
//                    shopFilterAdapter, R.layout.contact_spinner_row_nothing_selected, this
//            ));
            // Alle metingen ophalen ongefilterd
//            checkboxList.clear();
//            checkboxList.addAll(viewModel.convertProductsToCheckboxs(
//                    viewModel.getProductList(),
//                    SpecificData.PRODUCT_DISPLAY_SMALL));
        }else {
            // er is een default locatie, bepaal locatie
            String meterSpin = cookieRepository.getCookieValueFromLabel(SpecificData.METER_SPIN);
            if (meterSpin.equals(String.valueOf(CookieRepository.COOKIE_NOT_FOUND))){
                // er is nog geen default meter
                // nothing selected spinner definieren voor MeterSpinner

                // Metingen ophalen, gefilterd op locatie
            }else{
                // er is een default meter, bepaal meter

                // Metingen ophalen, gefilterd op locatie en meter
            }
//            // Vul checkboxlist mt produkten gefilterd obv shopfilter
//            checkboxList.clear();
//            composeCheckboxList();
//            // spinner met selectie gebruiken
//            shopFilterSpinner.setAdapter(shopFilterAdapter);
//            // animate parameter met false staan om het onnodig afvuren vd spinner tegen te gaan
//            shopFilterSpinner.setSelection(viewModel.getShopIndexById(shopFilter.getEntityId()), false);
        }


        // Ophalen metingen vr opgegeven location & meter
*/

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
}