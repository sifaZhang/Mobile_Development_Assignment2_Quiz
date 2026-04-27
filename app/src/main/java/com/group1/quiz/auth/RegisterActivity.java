package com.group1.quiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.quiz.R;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.models.Users;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Users;

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends BaseActivity {

    private EditText etName, etEmail, etPassword, etRePassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

            int bottomPadding = Math.max(imeInsets.bottom, navigationBars.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);

            // 键盘弹出时（ime bottom > 0），自动滚动到焦点 View
            if (imeInsets.bottom > 0) {
                View focusedView = getCurrentFocus();
                if (focusedView != null) {
                    ScrollView scrollView = findViewById(R.id.main);
                    scrollView.post(() -> {
                        // 计算 focusedView 相对于 ScrollView 的位置
                        int[] location = new int[2];
                        focusedView.getLocationInWindow(location);
                        int[] scrollLocation = new int[2];
                        scrollView.getLocationInWindow(scrollLocation);

                        int scrollTo = location[1] - scrollLocation[1]
                                + scrollView.getScrollY()
                                - scrollView.getHeight() / 2; // 滚到屏幕中间位置

                        scrollView.smoothScrollTo(0, scrollTo);
                    });
                }
            }

            return insets;
        });

        setHeaderTitle("Registration");

        btnRegister = findViewById(R.id.btnRegister);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        tvLogin = findViewById(R.id.tvLogin);

        imgHeaderLogo.setVisibility(View.INVISIBLE);

        tvLogin.setText(Html.fromHtml("Already have an account? <u>Login</u>",
                Html.FROM_HTML_MODE_LEGACY));

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etRePassword.getText().toString().trim();
                String role = AppConstants.Role.PLAYER;

                // basic check
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                        || role.isEmpty() || fullName.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // password
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Users user = new Users(
                        null,       // uid 由 FirebaseAuth 创建后再写入
                        fullName,
                        email,
                        role
                );

                //save to database
                FirebaseHelper_Users.registerUserWithAuth(
                        email,
                        password,
                        user,
                        new FirebaseHelper_Users.RegisterCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(RegisterActivity.this, "Register failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });

        // go to  Login page
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isApproved(String role) {
        return role.equals(AppConstants.Role.PLAYER);
    }
}