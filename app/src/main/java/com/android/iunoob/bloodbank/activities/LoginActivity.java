package com.android.iunoob.bloodbank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.iunoob.bloodbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button signin, signup, resetpass;
    private EditText inputemail, inputpassword;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //được gọi khi hoạt động Login được tạo, thiết lập sự kiện lấy
        //thông tin người dùng hiện tại
        super.onCreate(savedInstanceState);

        //thiết lập layout cho Login
        setContentView(R.layout.activity_login);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        //kiểm tra xem người dùng đã đăng nhập hay chưa
        if(mAuth.getCurrentUser() != null)
        {
            //getCurrentUser() của đối tượng FirebaseAuth
            //nếu người dùng đã đăng nhập thì tạo một đối tượng Intent
            //chuyển hướng đến Activity DashBoard
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);

            //kết thúc activity hiện tại, để ngăn người dùng quay lại màn hình
            //đăng nhập sau khi đã đăng nhập thành công
            finish();
        }

        //khởi tạo các biến và liên kết thành phần giao diện
        inputemail = findViewById(R.id.input_username);
        inputpassword = findViewById(R.id.input_password);
        signin = findViewById(R.id.button_login);
        signup = findViewById(R.id.button_register);
        resetpass = findViewById(R.id.button_forgot_password);

        //thiết lập sự kiện cho signin, signup, resetpass
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputemail.getText().toString()+"";
                final String password = inputpassword.getText().toString()+"";

                try {
                    //kiểm tra độ dài của email và mật khẩu sau đó thực hiện đăng nhập
                    if(password.length()>0 && email.length()>0) {
                        pd.show();
                        //phương thức của FirebaseAuth để kiểm tra thông tin đăng nhập
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //Nếu thông tin đăng nhập không chính xác sẽ hiển thị thông báo
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Quá trình xác thực thất bại",
                                                    Toast.LENGTH_LONG).show();
                                            Log.v("error", task.getException().getMessage());
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        pd.dismiss();
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Vui lòng điền hết tất cả các trường.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        //Khi ng dùng nhấn vào nút đăng ký Activity Profile sẽ hđ
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        //đặt lại mk
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RestorePassword.class);
                startActivity(intent);
            }
        });


    }

}
