package com.android.iunoob.bloodbank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.iunoob.bloodbank.R;
import com.android.iunoob.bloodbank.viewmodels.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ProgressDialog pd;

    EditText text1, text2;
    Spinner spinner1, spinner2;
    Button btnpost;

    FirebaseDatabase fdb;
    DatabaseReference db_ref;
    FirebaseAuth mAuth;

    Calendar cal;
    String uid;
    String Time, Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        getSupportActionBar().setTitle("Yêu cầu máu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text1 = findViewById(R.id.getMobile);
        text2 = findViewById(R.id.getLocation);

        spinner1 = findViewById(R.id.SpinnerBlood);
        spinner2 = findViewById(R.id.SpinnerDivision);

        btnpost = findViewById(R.id.postbtn);

        cal = Calendar.getInstance();

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        month+=1;
        Time = "";
        Date = "";
        String ampm="AM";

        if(cal.get(Calendar.AM_PM) ==1)
        {
            ampm = "PM";
        }

        if(hour<10)
        {
            Time += "0";
        }
        Time += hour;
        Time +=":";

        if(min<10) {
            Time += "0";
        }

        Time +=min;
        Time +=(" "+ampm);

        Date = day+"/"+month+"/"+year;

        FirebaseUser cur_user = mAuth.getInstance().getCurrentUser();

        if(cur_user == null)
        {
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
        } else {
            uid = cur_user.getUid();
        }

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("posts");

        try {
            btnpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();
                    final Query findname = fdb.getReference("users").child(uid);

                    if(text1.getText().length() == 0)
                    {
                        Toast.makeText(getApplicationContext(), "Nhập số điện thoại của bạn!",
                                Toast.LENGTH_LONG).show();
                    }
                    else if(text2.getText().length() == 0)
                    {
                        Toast.makeText(getApplicationContext(), "Nhập vị trí của bạn!",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        findname.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    db_ref.child(uid).child("Tên").setValue(dataSnapshot.getValue(UserData.class).getName());
                                    db_ref.child(uid).child("Liên hệ").setValue(text1.getText().toString());
                                    db_ref.child(uid).child("Địa chỉ").setValue(text2.getText().toString());
                                    db_ref.child(uid).child("Phân công").setValue(spinner2.getSelectedItem().toString());
                                    db_ref.child(uid).child("Nhóm máu").setValue(spinner1.getSelectedItem().toString());
                                    db_ref.child(uid).child("Số lần").setValue(Time);
                                    db_ref.child(uid).child("Ngày").setValue(Date);
                                    Toast.makeText(PostActivity.this, "Yêu cầu hiến máu của bạn đã được đăng",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(PostActivity.this, Dashboard.class));

                                } else {
                                    Toast.makeText(getApplicationContext(), "Database error occured.",
                                            Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("User", databaseError.getMessage());

                            }
                        });
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        pd.dismiss();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
