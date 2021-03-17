package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Reciept extends AppCompatActivity {
    TextView hour;
    TextView Price;
    TextView HourPrice;
    TextView Fine;
    TextView extraPrice;
    String EndDateOldformat;
    String EndTime;
    String StartDateOldFormat;
    String StartTime;
    PayPalConfiguration m_configuration;
    //the id is the link to paypal account,we have to create an app and get its id
    String m_PaypalClientId = "AScx5pid7ihx_S0cSmYHCnNBKfmUvRFxLXCiEjfdphQSpce0Q2NEYIHB7lYrKFvh_8X1D9eDJtxyNNO7";
    Intent m_service;
    int m_paypalRequestCode = 999; //or any number
    int x = 8;
    int HOURS;
    int HPrice;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth ;
    String StartT;
    String UserEndTime;
    boolean check = false;
    TextView ExtraHours;
    String ExtraH;
    int ExtraHO;
    TextView Total;
    int TotalExtra;
    int TotalParking;
    int Fineint;
    int TotalPrice;
    String Lan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciept);
        //hour = (TextView)findViewById(R.id.hour_id);
        Price = (TextView)findViewById(R.id.priceP);
        Fine = (TextView)findViewById(R.id.fine);
        ExtraHours = (TextView)findViewById(R.id.extraHours);
        HourPrice = (TextView) findViewById(R.id.HourPrice);
        Total = (TextView) findViewById(R.id.totalPrice);
        extraPrice = (TextView) findViewById(R.id.extraPrice);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("ParkirApp");
        firebaseAuth = FirebaseAuth.getInstance();

        //Add CheckPay
        //databaseReference.child("users").child("FCI_Garage").child(firebaseAuth.getCurrentUser().getUid()).child("checkPAY").setValue(check);
        ReadFromFirebase();
        //Pay part
        m_configuration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //sandox for test,production for real
                .clientId(m_PaypalClientId);

        m_service = new Intent(this, PayPalService.class);
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,m_configuration);
        startService(m_service); //paypal service ,listening to paypal app*/

    }

    void ReadFromFirebase()
    {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String hourPrice = dataSnapshot.child("GARAGE").child("FCI_Garage").child("HourPrice").getValue().toString();
                 ExtraH = dataSnapshot.child("GARAGE").child("FCI_Garage").child("ExtraHours").getValue().toString();
                //User(Start&End)Date
                StartDateOldFormat = dataSnapshot.child("users").child("FCI_Garage").child(firebaseAuth.getCurrentUser().getUid()).child("startDate").getValue().toString();//StartDateOfUser
                String UserEndDateOldFormat = dataSnapshot.child("users").child("FCI_Garage").child(firebaseAuth.getCurrentUser().getUid()).child("endDate").getValue().toString();//EndDateOfUser
                //User(Start&End)Time
                StartT = dataSnapshot.child("users").child("FCI_Garage").child(firebaseAuth.getCurrentUser().getUid()).child("startTime").getValue().toString();
                String UserEndT = dataSnapshot.child("users").child("FCI_Garage").child(firebaseAuth.getCurrentUser().getUid()).child("endTime").getValue().toString();
                //Fine
                String garage_Fine = dataSnapshot.child("GARAGE").child("FCI_Garage").child("Fine").getValue().toString();

                 Lan = dataSnapshot.child("users").child("FCI_Garage").child(firebaseAuth.getCurrentUser().getUid()).child("lan").getValue().toString();
                //IoTEnd(Time&Date)
                String Period = dataSnapshot.child("iot").child("FCI_Garage").child(Lan).child("time").getValue().toString();


                //HourPrice Integer
                HPrice = Integer.parseInt(hourPrice);
                ExtraHO = Integer.parseInt(ExtraH);

                StartTime = StartT.substring(0,StartT.length()-1);// check format 00:00
                UserEndTime = UserEndT.substring(0,UserEndT.length()-1);// check format 00:00

                //IoT
                String[] parts = Period.split("T");
                EndDateOldformat = parts[0];// IOTEnd Date 2020-07-12
                String EndTimeIotForm = parts[1];
                EndTime = EndTimeIotForm.substring(0,EndTimeIotForm.length()-1); //check time format 00:00

                //IoT EndDate new Format
                String Enddate = EndDateFun(EndDateOldformat);//12/7/2020

                //Time new format
                String StartTimeNewFormat = TimeFormat(StartTime);
                String UserEndTimeNewFormat = TimeFormat(UserEndTime);
                String IotEndTimeNewFormat = TimeFormat(EndTime);
               // hour.setText(IotEndTimeNewFormat);

                Date UserEndDate = null;
                try {
                    UserEndDate = new SimpleDateFormat("dd/M/yyyy").parse(UserEndDateOldFormat);
                } catch (
                        ParseException e) {
                    e.printStackTrace();
                }


                Date StartDate= null;
                try {
                    StartDate = new SimpleDateFormat("dd/M/yyyy").parse(StartDateOldFormat);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date EndDate = null;
                try {
                    EndDate = new SimpleDateFormat("dd/M/yyyy").parse(Enddate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                //TimeHours for later comparison
                int USHour = SplitTime(StartTimeNewFormat);
                int UEHour = SplitTime(UserEndTimeNewFormat);
                int IotEHour = SplitTime(IotEndTimeNewFormat);

                //Give a fine or not
                if((UserEndDate.compareTo(EndDate) != 0) || (UEHour != IotEHour))
                {
                    Fine.setText(garage_Fine+"$");
                    Fineint = Integer.parseInt(garage_Fine);

                }else
                {
                    Fineint = 0;
                    Fine.setText(""+Fineint);
                }


                //calculating parkinghours Price
                if(((StartDate.compareTo(UserEndDate))==0)&&(UEHour != USHour ))
                {
                    if (USHour < UEHour) {
                        int startTimeMin = toMins(StartTimeNewFormat);
                        int endTimeMin = toMins(UserEndTimeNewFormat);
                        int totalUserMin = endTimeMin - startTimeMin;
                        HOURS = totalUserMin / 60;
                        int minutes = totalUserMin % 60;
                        TotalParking = (HOURS * HPrice) + (int)((minutes / 60.0)*(double) HPrice);
                        Price.setText("" + TotalParking + "$");
                        HourPrice.setText(""+HOURS+":"+minutes);

                    } else if (USHour > UEHour) {
                        int startTimeMin = toMins(StartTimeNewFormat);
                        int endTimeMin = toMins(UserEndTimeNewFormat);
                        int totalUserMin = endTimeMin - startTimeMin;
                        HOURS = (totalUserMin / 60);
                        int H = HOURS + 24;
                        int minutes = totalUserMin % 60;
                        TotalParking = (H * HPrice)+(int)((minutes / 60.0)*(double) HPrice); ;
                        Price.setText("" + TotalParking + "$");
                        HourPrice.setText(""+H+":"+minutes);
                    }
                }
                else {

                        UserHourinDays(StartTimeNewFormat, UserEndTimeNewFormat, StartDate, UserEndDate);

                }
                //calculate parking ExtraHours
                if((UserEndDate.compareTo(EndDate) == 0) && (UEHour == IotEHour))
                {
                    ExtraHours.setText("0$");
                    ExtraHO = 0;
                    extraPrice.setText(""+ExtraHO);

                }
                else if((UserEndDate.compareTo(EndDate) == 0) &&(UEHour != IotEHour) )
                {
                    if( UEHour < IotEHour)
                    {
                        int startTimeMin = toMins(UserEndTimeNewFormat);
                        int endTimeMin = toMins(IotEndTimeNewFormat);
                        int totalUserMin = endTimeMin - startTimeMin;
                        HOURS= totalUserMin / 60;
                        int minutes = totalUserMin % 60;
                        TotalExtra = (HOURS*ExtraHO)+(int)((minutes / 60.0)*(double) ExtraHO);
                        ExtraHours.setText(""+TotalExtra+"$");
                        extraPrice.setText(""+HOURS+":"+minutes);
                    }
                    if( UEHour > IotEHour)
                    {
                        int startTimeMin = toMins(UserEndTimeNewFormat);
                        int endTimeMin = toMins(IotEndTimeNewFormat);
                        int totalUserMin = endTimeMin - startTimeMin;
                        HOURS= (totalUserMin / 60);
                        int H = HOURS + 24;
                        int minutes = totalUserMin % 60;
                        TotalExtra = (H*ExtraHO)+(int)((minutes / 60.0)*(double) ExtraHO);
                        ExtraHours.setText(""+TotalExtra+"$");
                        extraPrice.setText(""+H+":"+minutes);
                    }

                }
                else if((UserEndDate.compareTo(EndDate) != 0) &&(UEHour != IotEHour))
                {
                    UserHourinDaysExtra(UserEndTimeNewFormat,IotEndTimeNewFormat,UserEndDate,EndDate);
                }
                TotalPrice =  TotalParking+TotalExtra+Fineint;
                Total.setText(TotalPrice+"$");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void pay(View view) {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //  String hourPrice = dataSnapshot.child("GARAGE").child("FCI_Garage").child("HourPrice").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        PayPalPayment payment = new PayPalPayment(new BigDecimal(TotalPrice),"USD",
                "Test Payment with Paypal",PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,m_configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);
        startActivityForResult(intent,m_paypalRequestCode);
    }




    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == m_paypalRequestCode)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                //we have to confirm that payment worked
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null) {
                    String state = confirmation.getProofOfPayment().getState();
                    if (state.equals("approved"))
                    {
                        //if the payment worked state will be approved
                        Toast.makeText(this,"Approved :)",Toast.LENGTH_LONG).show();
                        check = true;
                        Intent intent = new Intent(this,Barrier.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(this,"Error in payment :(",Toast.LENGTH_LONG).show();
                        check = false;
                    }
                }
                else
                    Toast.makeText(this,"Confirmation is Null :(",Toast.LENGTH_LONG).show();
            }
            if(check == true)
            {
                databaseReference.child("users").child("FCI_Garage").child(firebaseAuth.getCurrentUser().getUid()).child("checkPAY").setValue(check);
            }


        }
    }
    public String EndDateFun(String DateOldFormat)
    {
        final String OLD_FORMAT = "yyyy-MM-dd";
        final String NEW_FORMAT = "dd/M/yyyy";

        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(DateOldFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        return newDateString;
    }

    public String TimeFormat(String TimeOldFormat)
    {
        final String OLD_FORMAT = "hh:m";
        final String NEW_FORMAT = "HH:mm";

        String newTimeString;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(TimeOldFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newTimeString = sdf.format(d);
        return newTimeString;
    }

    public int SplitTime(String time)
    {
        String[] parts = time.split(":");
        String stringHours = parts[0];
        int IntegerHour = Integer.parseInt(stringHours);
        return  IntegerHour;
    }
    private static int toMins(String s) {
        String[] hourMin = s.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
    }
    private void UserHourinDays(String sT,String eT,Date sd,Date ed) {
        String[] hourMins = sT.split(":");
        int hours = Integer.parseInt(hourMins[0]);
        String[] hourMine = eT.split(":");
        int houre = Integer.parseInt(hourMine[0]);
        if(hours < houre)
        {
            int startTimeMin = toMins(sT);
            int endTimeMin = toMins(eT);
            int totalUserMin = endTimeMin - startTimeMin;

            long diff = ed.getTime() - sd.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            HOURS = (totalUserMin / 60)+ (int) (days * 24);
            int minutes = totalUserMin % 60;
            TotalParking = (HOURS*HPrice)+(int)((minutes / 60.0)*(double) HPrice);
            Price.setText(""+TotalParking+"$");
            HourPrice.setText(""+HOURS+":"+minutes);


        }
        else if(hours > houre)
        {
            int startTimeMin = toMins(sT);
            int endTimeMin = toMins(eT);
            int totalUserMin = endTimeMin - startTimeMin;

            long diff = ed.getTime() - sd.getTime();
            long days0 = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            long days = days0 - 1;
            int UserHourinninus = (totalUserMin / 60);
            HOURS = UserHourinninus+24+(((int)days)*24);
            int minutes = totalUserMin % 60;
            TotalParking = (HOURS*HPrice)+(int)((minutes / 60.0)*(double) HPrice);
            Price.setText(""+TotalParking+"$");
            HourPrice.setText(""+HOURS+":"+minutes);

        }

    }
    private void UserHourinDaysExtra(String sT,String eT,Date sd,Date ed) {
        String[] hourMins = sT.split(":");
        int hours = Integer.parseInt(hourMins[0]);
        String[] hourMine = eT.split(":");
        int houre = Integer.parseInt(hourMine[0]);
        if(hours < houre)
        {
            int startTimeMin = toMins(sT);
            int endTimeMin = toMins(eT);
            int totalUserMin = endTimeMin - startTimeMin;

            long diff = ed.getTime() - sd.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            HOURS = (totalUserMin / 60)+ (int) (days * 24);
            int minutes = totalUserMin % 60;
            TotalExtra = (HOURS*ExtraHO)+(int)((minutes / 60.0)*(double) ExtraHO);
            ExtraHours.setText(""+TotalExtra+"$");
            extraPrice.setText(""+HOURS+":"+minutes);
        }
        else if((hours > houre))
        {
            int startTimeMin = toMins(sT);
            int endTimeMin = toMins(eT);
            int totalUserMin = endTimeMin - startTimeMin;

            long diff = ed.getTime() - sd.getTime();
            long days0 = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            long days = days0 - 1;
            int UserHourinninus = (totalUserMin / 60);
            HOURS = UserHourinninus+24+(((int)days)*24);
            int minutes = totalUserMin % 60;
            TotalExtra = (HOURS*ExtraHO)+(int)((minutes / 60.0)*(double) ExtraHO);
            ExtraHours.setText(""+TotalExtra+"$");
            extraPrice.setText(""+HOURS+":"+minutes);

        }

    }
    public String convertDateToString(Date indate)
    {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/M/yyyy");
        try{
            dateString = sdfr.format( indate );
        }catch (Exception ex ){
            System.out.println(ex);
        }
        return dateString;
    }

}
