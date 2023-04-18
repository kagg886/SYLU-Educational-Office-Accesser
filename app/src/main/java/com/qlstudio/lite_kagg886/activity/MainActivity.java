package com.qlstudio.lite_kagg886.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.UserInfo;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    NavigationView navigationView = binding.navView;
                    if (msg.getData().containsKey("img")) {
                        ImageView img = navigationView.findViewById(R.id.nav_header_img);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            img.setImageBitmap(msg.getData().getParcelable("img", Bitmap.class));
                        } else {
                            img.setImageBitmap(msg.getData().getParcelable("img"));
                        }
                    }
                    TextView name = navigationView.findViewById(R.id.nav_header_title);
                    name.setText(msg.getData().getString("userName"));
                    TextView college = navigationView.findViewById(R.id.nav_header_subtitle);
                    college.setText(msg.getData().getString("college"));
                    break;
                case 1:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        GlobalApplication.getApplicationNoStatic().getPreferences().edit()
                .putBoolean("setting_nullfail", false)
                .apply();

        new Thread(() -> {
            SyluSession session = GlobalApplication.getApplicationNoStatic().getSession();
            Exception out = null;
            UserInfo info;
            do {
                try {
                    info = session.getUserInfo();
                    Message message = new Message();
                    message.what = 0;
                    if (GlobalApplication.getApplicationNoStatic().getPreferences().getBoolean("setting_show_avatar", true)) {
                        message.getData().putParcelable("img", BitmapFactory.decodeStream(session.getClient().url(info.getAvatar()).get().bodyStream()));
                    }
                    message.getData().putString("userName", info.getName());
                    message.getData().putString("college", info.getCollege());
                    handler.sendMessage(message);
                } catch (Exception e) {
                    if (e instanceof OfflineException) {
                        throw e;
                    }
                    out = e;
                }
            } while (out != null);
            //用户信息
        }).start();


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_schedule, R.id.nav_exam_info, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        //在onCreate里navView并未初始化，所以只能放在这
        binding.navView.findViewById(R.id.btn_exit).setOnClickListener((view) -> {
            GlobalApplication.getApplicationNoStatic().logout();
        });
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}