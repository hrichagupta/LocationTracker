package com.example.android.locationtracker;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;


public class NearByPlacesActivity extends AppCompatActivity {

    ArrayList<SuitCaseContact> arrayList;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_places);

        recyclerView = findViewById(R.id.recycle);
        arrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(NearByPlacesActivity.this));

        addData(R.drawable.account, "Accounting");
        addData(R.drawable.airport, "Airport");
        addData(R.drawable.seabottom, "Aquarium");
        addData(R.drawable.atm, "Atm");
        addData(R.drawable.sandwich, "Bakery");
        addData(R.drawable.bank, "Bank");
        addData(R.drawable.bar, "Bar");
        addData(R.drawable.makeup, "Beauty Salon");
        addData(R.drawable.busstop, "Bus Station");
        addData(R.drawable.coffeemachine, "Cafe");
        addData(R.drawable.church, "Church");
        addData(R.drawable.townhall, "City Hall");
        addData(R.drawable.clothes, "Clothing Store");
        addData(R.drawable.toothbrush, "Dentist");
        addData(R.drawable.doctor, "Doctor");
        addData(R.drawable.electrician, "Electrician");
        addData(R.drawable.onlineshopping, "Electronics Store");
        addData(R.drawable.firestation, "Fire Station");
        addData(R.drawable.gasstation, "Gas Station");
        addData(R.drawable.machine, "Gym");
        addData(R.drawable.conditioner, "Hair Care");
        addData(R.drawable.storing, "Hardware Store");
        addData(R.drawable.ritual, "Hindu Temple");
        addData(R.drawable.groceries, "Home Goods Store");
        addData(R.drawable.hospital, "Hospital");
        addData(R.drawable.necklace, "Jewelry Store");
        addData(R.drawable.basket, "Laundry");
        addData(R.drawable.lawyer, "Lawyer");
        addData(R.drawable.books, "Library");
        addData(R.drawable.istanbul, "Mosque");
        addData(R.drawable.cinema, "Movie Theater");
        addData(R.drawable.movingtruck, "Moving Company");
        addData(R.drawable.canvas, "Museum");
        addData(R.drawable.nightclub, "Night Club");
        addData(R.drawable.park, "Park");
        addData(R.drawable.garage, "Parking");
        addData(R.drawable.petshop, "Pet Store");
        addData(R.drawable.pharmacy, "Pharmacy");
        addData(R.drawable.policecar, "Police");
        addData(R.drawable.postoffice, "Post Office");
        addData(R.drawable.burger, "Restaurant");
        addData(R.drawable.classroom, "School");
        addData(R.drawable.gymshoes, "Shoe Store");
        addData(R.drawable.shoppingcart, "Shopping Mall");
        addData(R.drawable.supermarket, "Supermarket");
        addData(R.drawable.trainstation, "Train Station");
        addData(R.drawable.lion, "Zoo");


        RecyclerAdapter adapter = new RecyclerAdapter(NearByPlacesActivity.this, arrayList);
        recyclerView.setAdapter(adapter);

    }

    private void addData(int imgIcon, String txtPlace) {
        SuitCaseContact suitCaseContact = new SuitCaseContact();
        suitCaseContact.imgIcon = imgIcon;
        suitCaseContact.txtPlace = txtPlace;
        arrayList.add(suitCaseContact);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(NearByPlacesActivity.this,ContentActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
