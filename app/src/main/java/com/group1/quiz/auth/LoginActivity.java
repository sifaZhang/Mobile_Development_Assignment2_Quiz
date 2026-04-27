package com.group1.quiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.group1.quiz.R;
import com.group1.quiz.models.Users;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Users;
import com.group1.quiz.utils.UserManager;
import com.group1.quiz.utils.UserPrefs;

public class LoginActivity extends BaseActivity {

    private Button btLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_login);
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

        setHeaderTitle("Login");

        btLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvLogin);

        imgHeaderLogo.setVisibility(View.INVISIBLE);

        tvRegister.setText(Html.fromHtml("Don't have an account? <u>Register</u>",
                Html.FROM_HTML_MODE_LEGACY));

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseHelper_Users helper = new FirebaseHelper_Users();

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = auth.getCurrentUser().getUid();

                                helper.loadUserInfo(uid, new FirebaseHelper_Users.UserCallback() {
                                    @Override
                                    public void onSuccess(Users user) {
                                        // 保存用户资料
                                        UserPrefs prefs = new UserPrefs(LoginActivity.this);
                                        prefs.saveLogin(user.getUid());
                                        UserManager.getInstance().setUser(user);

                                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                                        goToDashboard(user.getRole());
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(LoginActivity.this, "Load user info error", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}