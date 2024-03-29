package com.example.shravanapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    ToggleButton connection, fms;
    RaspberryIntercom ConnectivityTask;
    RaspberryIntercom2 FmsTask;
    private ProgressDialog pDialog;
    boolean ConnectivityTask_running;
    String c1_num,c2_num,c3_num,c4_num,REGEX_REMOVE_WHITESPACE_HYPHENS;
    public String HOST_NAME="192.168.0.108";





    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public HomeFragment() {
        // Required empty public constructor


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestCheckPermission();

        connection =(ToggleButton)view.findViewById(R.id.connection_tglBtn);
        fms =(ToggleButton)view.findViewById(R.id.ring_tglBtn);
        Log.d("AAYAN:","flag1 from OnViewCreated");

        REGEX_REMOVE_WHITESPACE_HYPHENS = "[\\-\\s]";



        // Shravan Connectivity button functionality
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connection.isChecked())
                {
                    Toast.makeText(getView().getContext(),"Initiating Connection",Toast.LENGTH_SHORT).show();
                    // call asynctask for socket1
                    ConnectivityTask = (RaspberryIntercom) new RaspberryIntercom(getActivity()).execute();
                    ConnectivityTask_running = true;
                }
                else
                {
                    Toast.makeText(getActivity(),"Disconnecting Shravan",Toast.LENGTH_SHORT).show();
                    // call onCancelled for socket1
                    if (ConnectivityTask_running && ConnectivityTask!=null)
                    {
                        ConnectivityTask.onPostExecute(null);
                        ConnectivityTask_running = false;
                        Log.d("AAYAN:","Cancellation request for Socket");
                    }
                    else{
                        connection.setChecked(false);
                    }
                }
            }
        });


        // Find my Shravan button functionality
        fms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fms.isChecked())
                {
                    fms.setChecked(true);
                    Toast.makeText(getView().getContext(),"Asking Device to Ring",Toast.LENGTH_SHORT).show();
                    // calling ring async for socket2
                    try {
                        //set time in mili
                        fms.setChecked(true);
                        fms.setClickable(false);
                        FmsTask = (RaspberryIntercom2) new RaspberryIntercom2().execute();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.d("AAYAN:","out of delay");
                    fms.setClickable(true);
                }
                else{

                }

            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("AAYAN:","flag1 from onDestroy");
    }


    @Override
    public void onDetach() {
        super.onDetach();

        Log.d("AAYAN:","flag1 from onDetach");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d("AAYAN:","flag1 from OnCreate");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("AAYAN:","flag1 from OnCreateView");
                // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    //permission checker

    private void requestCheckPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getActivity(), "Internet permission already granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.INTERNET }, 100);
        }
        else {
            Toast.makeText(getActivity(), "Internet permission already granted", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getActivity(), "SMS Permission not granted!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.SEND_SMS }, 100);
        }
        else {
            Toast.makeText(getActivity(), "SMS Permission already granted!", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getActivity(), "COARSE permission not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, 100);
            Toast.makeText(getActivity(), "COARSE permission granted", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "COARSE permission granted", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getActivity(), "FINE permission not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 100);
        }
        else {
            Toast.makeText(getActivity(), "FINE permission already granted", Toast.LENGTH_SHORT).show();
        }



    }

    // AsyncTask for Connectivity - message, sos

    class RaspberryIntercom extends AsyncTask<String,Void,Void>
    {
        Socket s;
        PrintWriter pw;
        DataOutputStream dout;
        BufferedReader in;
        PrintWriter out;
        public String action, whom, location_string;
        public Integer flag;
        SmsManager smsManager;
        private LocationRequest locationRequest;
        public double latitude = 0,longitude= 0;
        Context mContext;

        public RaspberryIntercom(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(String... strings) {

            try{
                // Socket Initialization

                s= new Socket(HOST_NAME,6666);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(),true);

/*
                // Location Mechanism
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
                    {
                        if (isGPSEnabled())
                        {

                            LocationServices.getFusedLocationProviderClient(getActivity())
                                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                                        @Override
                                        public void onLocationResult(@NonNull LocationResult locationResult) {
                                            super.onLocationResult(locationResult);

                                            LocationServices.getFusedLocationProviderClient(getActivity())
                                                    .removeLocationUpdates(this);

                                            if (locationResult != null && locationResult.getLocations().size() >0){

                                                int index = locationResult.getLocations().size() - 1;
                                                latitude = locationResult.getLocations().get(index).getLatitude();
                                                longitude = locationResult.getLocations().get(index).getLongitude();
                                                Log.d("AAYAN:","Latitude: "+latitude+"   Longi:"+longitude);

                                                //AddressText.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
                                            }
                                        }
                                    }, Looper.getMainLooper());

                        }
                        else
                        {
                            turnOnGPS();
                        }
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 100);
                    }


                }


*/

                SharedPreferences sharedPrefs = getActivity().getSharedPreferences("com.example.shravan.sos_contacts", getActivity().MODE_PRIVATE);
                c1_num = sharedPrefs.getString("c1_num","Number");
                c2_num = sharedPrefs.getString("c2_num","Number");
                c3_num = sharedPrefs.getString("c3_num","Number");
                c4_num = sharedPrefs.getString("c4_num","Number");

                c1_num = c1_num.replaceAll(REGEX_REMOVE_WHITESPACE_HYPHENS, "");
                c2_num = c2_num.replaceAll(REGEX_REMOVE_WHITESPACE_HYPHENS, "");
                c3_num = c3_num.replaceAll(REGEX_REMOVE_WHITESPACE_HYPHENS, "");
                c4_num = c4_num.replaceAll(REGEX_REMOVE_WHITESPACE_HYPHENS, "");


                while (true)
                {

                    String result = in.readLine();
                    JSONObject jsonObject= new JSONObject(result);
                    action = (String) jsonObject.get("action");
                    System.out.println("AAYAN: "+" Value of Action: "+action);




                    if (action.equals("sos"))
                    {
                        whom = jsonObject.getJSONObject("options").getString("op1");
                        System.out.println("AAYAN: "+" Value of to every1: "+whom);

                        if (whom.equals("aayan"))
                        {
                            sendSMS("8652628934","Hello, it's Shravan and I need your assistance. \n\nHere's my location: https://www.google.com/maps/place/19.2832953,72.857772");                    //  Log.d("AAYAN:","within if1");
                            Toast.makeText(mContext, "SOS sent out to "+whom, Toast.LENGTH_SHORT).show();

                            jsonObject = null;
                            Runtime.getRuntime().gc();

                        }
                        else if (whom.equals("everyone"))
                        {
                            if (longitude!=0.0 || latitude!=0.0)
                            {
                                location_string = "Hello, it's Shravan and I need your assistance. \n\nHere's my location: https://www.google.com/maps/place/"+latitude+","+longitude+"";
                                sendSMS(c1_num,location_string);
                                //sendSMS(c2_num,location_string);
                                //sendSMS(c3_num,location_string);
                                //sendSMS(c4_num,location_string);

                                Log.d("AAYAN:","Sent Dynamic location to everyone!");
                            }
                            else {

                                sendSMS(c1_num,"Hello it's Shravan and I need your help. \n\nHere's my location: https://www.google.com/maps/place/19.2832953,72.857772");
                                //sendSMS(c2_num,"Hello it's Shravan and I need your help. \n\nHere's my location: https://www.google.com/maps/place/19.2832953,72.857772");
                                //sendSMS(c3_num,"Hello it's Shravan and I need your help. \n\nHere's my location: https://www.google.com/maps/place/19.2832953,72.857772");
                                //sendSMS(c4_num,"Hello it's Shravan and I need your help. \n\nHere's my location: https://www.google.com/maps/place/19.2832953,72.857772");

                                Log.d("AAYAN:","Sent Static location to everyone!");

                            }
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    final Toast toast = Toast.makeText(mContext, "SOS sent out to "+whom, Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });

                            //Toast.makeText(mContext, "SOS sent out to "+whom, Toast.LENGTH_SHORT).show();

                            jsonObject = null;
                            Runtime.getRuntime().gc();

                            Log.d("AAYAN:","Out of inner loop");

                        }
                    }
                    else if (action.equals("call"))
                    {
                        whom = jsonObject.getJSONObject("options").getString("op1");
                        System.out.println("AAYAN: "+" Value of to every1: "+whom);
                        Log.d("AAYAN: ","call :"+whom);

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(mContext, "Calling "+whom, Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(mContext, "Calling "+whom, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),"Invoking call to "+whom,Toast.LENGTH_SHORT).show();
                        jsonObject = null;
                        Runtime.getRuntime().gc();
                        continue;

                    }
                    else if (action.equals("dc"))
                    {
                        Log.d("AAYAN: ","socket ended");
                        jsonObject = null;
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                final Toast toast = Toast.makeText(mContext,"Disconnecting socket"+whom,Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        Runtime.getRuntime().gc();
                        onPostExecute(null);
                    }
                    else if (action.equals("check"))
                    {
                        Log.d("AAYAN:","check function invoked");
                        out.println("okay");
                    }
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d("AAYAN:","Exception: "+e.toString());

            }

            return null;
        }

   /*     private void turnOnGPS() {

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext())
                    .checkLocationSettings(builder.build());

            result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        Toast.makeText(getContext(), "GPS is already tured on", Toast.LENGTH_SHORT).show();

                    } catch (ApiException e) {

                        switch (e.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                try {
                                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                    resolvableApiException.startResolutionForResult(getActivity(), 2);
                                } catch (IntentSender.SendIntentException ex) {
                                    ex.printStackTrace();
                                }
                                break;

                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                //Device does not have location
                                break;
                        }
                    }
                }
            });



        }

        private boolean isGPSEnabled()
        {
            LocationManager locationManager=null;
            boolean isEnabled = false;

            if (locationManager==null)
            {
                locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            }

            isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isEnabled;

        }
*/

        private void sendSMS(String contactNo,String message) {

            smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contactNo, null, message, null, null);

        }

        @Override
        protected void onPostExecute(Void unused) {

            super.onPostExecute(unused);

            ConnectivityTask_running = false;
            connection.setChecked(false);

            Log.d("AAYAN:","exiting Async from onPostExecute");

            try {
                s.close();
                Log.d("AAYAN:","Exiting Socket");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled(Void unused) {
            ConnectivityTask_running = false;
            connection.setChecked(false);

            Log.d("AAYAN:","exiting Async from onPostExecute");

            try {
                s.close();
                Log.d("AAYAN:","Exiting Socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onCancelled(unused);

        }


    }


    // AsyncTask for FMS, Image Upload

    class RaspberryIntercom2 extends AsyncTask<String,Void,Void>
    {

        Socket s2;
        BufferedReader in;
        PrintWriter out;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getView().getContext());
            pDialog.setMessage("Ringing your Shravan... Please Wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... strings) {

            try
            {

                //Socket Initialization
                s2= new Socket(HOST_NAME,6667);
                in = new BufferedReader(new InputStreamReader(s2.getInputStream()));
                out = new PrintWriter(s2.getOutputStream(),true);

                out.println("fms");

                s2.close();

                Thread.sleep(5000);

            }
            catch (Exception e) {
                Log.d("AAYAN:","ERROR",e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            pDialog.dismiss();

        }
    }

}