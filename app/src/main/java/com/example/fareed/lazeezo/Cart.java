package com.example.fareed.lazeezo;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fareed.lazeezo.Common.Common;
import com.example.fareed.lazeezo.Database.Database;
import com.example.fareed.lazeezo.Helper.RecyclerItemTouchHelper;
import com.example.fareed.lazeezo.Interface.RecyclerItemTouchHelperListener;
import com.example.fareed.lazeezo.Model.DataMessage;
import com.example.fareed.lazeezo.Model.MyResponse;
import com.example.fareed.lazeezo.Model.Notification;
import com.example.fareed.lazeezo.Model.Order;
import com.example.fareed.lazeezo.Model.Request;
import com.example.fareed.lazeezo.Model.Sender;
import com.example.fareed.lazeezo.Model.Token;
import com.example.fareed.lazeezo.Model.User;
import com.example.fareed.lazeezo.Remote.APIService;
import com.example.fareed.lazeezo.Remote.IGoogleServices;
import com.example.fareed.lazeezo.ViewHolder.CartAdapter;
import com.example.fareed.lazeezo.ViewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener ,
        LocationListener, RecyclerItemTouchHelperListener {



    IGoogleServices mGoogleMapServices;
    private static final int LOCATION_REQUEST_CODE = 9999;
    private static final int PLAY_SERVICES_REQUEST = 9997;
    APIService mService;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    MaterialSpinner addressSpinner,paymentSpinner;
    FirebaseDatabase database;
    DatabaseReference requests;
    Button btnPlace;
    Place shippinAddress;
    String address="";
    public TextView txtTotalPrice;
    List<Order> cart=new ArrayList<>();
    CartAdapter adapter;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL=5000;
    private static int FASTEST_INTERVAL=3000;
    private static int DISPLACEMENT=10;

    RelativeLayout rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        mGoogleMapServices=Common.getGoogleMapAPI();
        rootView=(RelativeLayout)findViewById(R.id.rootLayout);
        //if(ActivityCompat.checkSelfPermission(this,Manifest.permission))

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },LOCATION_REQUEST_CODE);
        }else{
            if(checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
            }
        }
        mService=Common.getFCMService();
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");



        btnPlace=(Button)findViewById(R.id.btnPlaceOrder);
        txtTotalPrice=(TextView)findViewById(R.id.total);


        recyclerView=(RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.size()>0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Cart is Empty", Toast.LENGTH_SHORT).show();
            }
        });
        loadListFood();
    }

    private void createLocationRequest() {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();

                        //displayLocation();
                    }
                }
                break;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_REQUEST).show();
            }else {
                Toast.makeText(this, "The device is not Supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Step");
        alertDialog.setMessage("Drop Your Address");

        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.order_add_comment,null);
        addressSpinner=(MaterialSpinner)view.findViewById(R.id.selectAddType);
        addressSpinner.setItems("Ship To this Address","Home Address");


        paymentSpinner=(MaterialSpinner)view.findViewById(R.id.paymentMethod);
        paymentSpinner.setItems("Credit Card","Cash On Delivery","Lazeezo Balance");


        paymentSpinner.setHint("Choose Payment Method");
        addressSpinner.setHint("Choose Address Type");
        paymentSpinner.setSelected(false);
        addressSpinner.setSelected(false);

       final PlaceAutocompleteFragment edtAddress=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter Your Address");
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextColor(Color.WHITE);


            Log.i("onItemSelecte", "onItemSelected: ");

            Double lat=0.0,lng=0.0;
            if(mLastLocation!=null){
                 lat= mLastLocation.getLatitude();
                 lng=mLastLocation.getLongitude();
            }
            mGoogleMapServices.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
                   lat,
                    lng))
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response.body().toString());
                                JSONArray resultsArray=jsonObject.getJSONArray("results");
                                JSONObject firstObject=resultsArray.getJSONObject(0);
                                address=firstObject.getString("formatted_address");
                                ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                        .setText(address.toString());
                                Log.i("onRespone", "onResponse: "+address.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(Cart.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        addressSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(addressSpinner.getText().toString().equals("Ship To this Address")){
                    Log.i("onItemSelecte", "onItemSelected: ");
                    mGoogleMapServices.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()))
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        JSONObject jsonObject=new JSONObject(response.body().toString());
                                        JSONArray resultsArray=jsonObject.getJSONArray("results");
                                        JSONObject firstObject=resultsArray.getJSONObject(0);
                                        address=firstObject.getString("formatted_address");
                                        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                                .setText(address.toString());
                                        Log.i("onRespone", "onResponse: "+address.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(Cart.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {

                    if(Common.currentUser.getHomeAddress()!=null || !TextUtils.isEmpty(Common.currentUser.getHomeAddress())){
                        address=Common.currentUser.getHomeAddress();
                        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                .setText(address.toString());
                    }else{
                        Toast.makeText(Cart.this, "Kindly Update Your Home Address", Toast.LENGTH_SHORT).show();
                    }
//                    if(!TextUtils.isEmpty(Common.currentUser.getHomeAddress()) ||
//                            Common.currentUser.getHomeAddress()==null)
//                        Toast.makeText(Cart.this, "Kindly Update Your Home Address", Toast.LENGTH_SHORT).show();
//                    else
//                        address=Common.currentUser.getHomeAddress();
                }
            }
        });
        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippinAddress=place;
            }

            @Override
            public void onError(Status status) {
                Log.e("Error",status.getStatusMessage());
            }
        });
       // final AutoCompleteTextView edtAddress=(AutoCompleteTextView)view.findViewById(R.id.edtAddress);
        final AutoCompleteTextView edtComment=(AutoCompleteTextView)view.findViewById(R.id.edtComment);


        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if (shippinAddress!=null)
                        address=shippinAddress.getAddress().toString();

                if (TextUtils.isEmpty(address)){
                    Toast.makeText(Cart.this, "Enter Address Or Choose Type Of Address", Toast.LENGTH_SHORT).show();

                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                }
                Log.i("onClick: ",paymentSpinner.getText().toString());
                if(paymentSpinner.getText().toString().equals("Cash On Delivery")){
                    Request request=new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address,
                        txtTotalPrice.getText().toString(),
                        "0",
                        edtComment.getText().toString(),
                        "COD",
                        "Unpaid",
                        String.format("%s,%s",mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                        cart
                    );
                    String order_number=String.valueOf(System.currentTimeMillis());
                requests.child(order_number).setValue(request);
                new Database(getApplicationContext()).cleanCart(Common.currentUser.getPhone());
                sendNotification(order_number);
                Toast.makeText(Cart.this, "Order has been Placed\nThank You", Toast.LENGTH_SHORT).show();
                finish();

                }else if(!String.valueOf(paymentSpinner.getSelectedIndex()).toString().equals("Lazeezo Balance")){

                    final Locale locale=new Locale("en","PK");
                    NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                    //txtTotalPrice.setText(fmt.format(total));
                    double balance=Common.formatCurreny(txtTotalPrice.getText().toString(),locale).doubleValue();
                    Double lat=0.0,lng=0.0;
                    if(mLastLocation!=null){
                        lat= mLastLocation.getLatitude();
                        lng=mLastLocation.getLongitude();
                    }
                    if(Common.currentUser.getBalance()>=balance){
                        Request request=new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                edtComment.getText().toString(),
                                "COD",
                                "Paid",
                                String.format("%s,%s",lat,lng),
                                cart
                        );
                        final String order_number=String.valueOf(System.currentTimeMillis());
                        requests.child(order_number).setValue(request);
                        new Database(getApplicationContext()).cleanCart(Common.currentUser.getPhone());
                        Toast.makeText(Cart.this, "Order has been Placed\nThank You", Toast.LENGTH_SHORT).show();

                        double amount=Common.currentUser.getBalance()-balance;
                        Map<String,Object> update_balance=new HashMap<>();
                        update_balance.put("balance",balance);
                        FirebaseDatabase.getInstance()
                                .getReference("User")
                                .updateChildren(update_balance)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance()
                                                    .getReference("User")
                                                    .child(Common.currentUser.getPhone())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Common.currentUser=dataSnapshot.getValue(User.class);
                                                            sendNotification(order_number);

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                });

                        finish();

                    }else{
                        Toast.makeText(Cart.this, "Your balance is not sufficient", Toast.LENGTH_SHORT).show();
                    }

                }else if(!String.valueOf(paymentSpinner.getSelectedIndex()).toString().equals("Credit Card")){

                    Request request=new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            address,
                            txtTotalPrice.getText().toString(),
                            "0",
                            edtComment.getText().toString(),
                            "COD",
                            "Paid",
                            String.format("%s,%s",mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                            cart
                    );
                    String order_number=String.valueOf(System.currentTimeMillis());
                    requests.child(order_number).setValue(request);
                    new Database(getApplicationContext()).cleanCart(Common.currentUser.getPhone());
                    sendNotification(order_number);
                    Toast.makeText(Cart.this, "Order has been Placed\nThank You", Toast.LENGTH_SHORT).show();
                    finish();

                }
                else {
                    Toast.makeText(Cart.this, "Please Select Payment Method", Toast.LENGTH_SHORT).show();
                }
//                Request request=new Request(
//                        Common.currentUser.getPhone(),
//                        Common.currentUser.getName(),
//                        shippinAddress.getAddress().toString(),
//                        txtTotalPrice.getText().toString(),
//                        "0",
//                        edtComment.getText().toString(),
//                        cart
//                );
//
//                String order_number=String.valueOf(System.currentTimeMillis());
//                requests.child(order_number).setValue(request);
//                new Database(getApplicationContext()).cleanCart();
//                sendNotification(order_number);
//                Toast.makeText(Cart.this, "Order has been Placed\nThank You", Toast.LENGTH_SHORT).show();
//                finish();
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }

        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialog.show();
    }

    private void sendNotification(final String order_number) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        final Query data=tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Token serverToken=postSnapshot.getValue(Token.class);

//                    Notification notification=new Notification("Lazeezo","You've new Order "+order_number);
//                    Sender content=new Sender(serverToken.getToken(),notification);

                    Map<String,String> dataSend=new HashMap<>();
                    dataSend.put("title","Lazeezo");
                    dataSend.put("message","You have New Order '"+order_number+"'");
                    DataMessage dataMessage=new DataMessage(serverToken.getToken(),dataSend);

                    String test=new Gson().toJson(dataMessage);

                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    Log.i("OnDataChange", "Your are In");
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank You Order, Placed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }


                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Error",t.getMessage());
                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart=new Database(this).getCarts(Common.currentUser.getPhone());
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total=0;
        for (Order order:cart)
                total+=(Integer.parseInt(order.getPrice())*Integer.parseInt(order.getQuantity()));

        Locale locale=new Locale("en","PK");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(this).cleanCart(Common.currentUser.getPhone());
        for (Order item:cart)
            new Database(this).addToCart(item);
        loadListFood();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            Log.d("location","Your Location"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude());
        }else {
            Log.d("Location","Could not get Location");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        displayLocation();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder){
            String name=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            final Order deleteItem=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);

            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(),Common.currentUser.getPhone());
            final Locale locale=new Locale("en","PK");
            int total=0;
            List<Order> orders=new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for (Order item:orders)
                total+=(Integer.parseInt(item.getPrice())*Integer.parseInt(item.getQuantity()));
            NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));

            Snackbar snackbar=Snackbar.make(rootView,name+" removed from cart",Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);
                    int total=0;
                    List<Order> orders=new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for (Order item:orders)
                        total+=(Integer.parseInt(item.getPrice())*Integer.parseInt(item.getQuantity()));
                    NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));
                }
            });
            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();
        }
    }
}
