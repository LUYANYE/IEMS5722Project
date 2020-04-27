package hk.edu.cuhk.ie.iems5722.group21.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import hk.edu.cuhk.ie.iems5722.group21.R;
import hk.edu.cuhk.ie.iems5722.group21.connect.Common;

public class LoginActivity extends AppCompatActivity {

    MessageDigest md;
    private String url = "http://18.188.52.141/api/a3/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn_login = findViewById(R.id.btn_login);
        Button btn_registered = findViewById(R.id.btn_registered);
        Button btn_copyright = findViewById(R.id.about_us);

        final EditText login_username = findViewById(R.id.login_username);
        final EditText login_password = findViewById(R.id.login_password);

        //md5加密
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 登录按钮
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login_username.getText()+"";
                String password = login_password.getText()+"";
                if(username.length()==0 || password.length()==0 ){
                    Toast t = Toast.makeText(LoginActivity.this,"empty content！", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                md.update(password.getBytes());
                String encodepwd = new BigInteger(1,md.digest()).toString(16);

                VerificationTask verificationTask =
                        new VerificationTask(LoginActivity.this, url, username, encodepwd);
                verificationTask.execute();

            }
        });

        // 注册按钮
        btn_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = LoginActivity.this;
                Intent intent = new Intent(context, SignupActivity.class);
                context.startActivity(intent);
            }
        });

        // 进入著作声明页面按钮
        btn_copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = LoginActivity.this;
                Intent intent = new Intent(context, AboutusActivity.class);
                context.startActivity(intent);
            }
        });
    }

    //httpTask
    class VerificationTask extends AsyncTask<Void,Integer,String> {

        private Context context;
        private String url;
        private String name;
        private String password;


        public VerificationTask(Context context, String url, String name, String password) {
            this.context = context;
            this.url = url;
            this.password = password;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {

            String status = Common.Post_Login(url, name, password);
            //第三个参数为String 所以此处return一个String类型的数据
            return status;
        }

        @Override
        protected void onPostExecute(String JsonString) {

            //@test
            Log.v("fetch",JsonString);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(JsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject != null) {
                try {
                    String status = jsonObject.getString("status");
                    Log.v("status", status);
                    if(status.equals("OK")){
                        Toast t = Toast.makeText(LoginActivity.this,"Login success", Toast.LENGTH_LONG);
                        t.show();
                        String user_id = jsonObject.getString("userid");
                        Context context = LoginActivity.this;
                        Intent intent = new Intent(context, MainActivity.class);
                        Bundle b = new Bundle();
                        Log.v("name", name);
                        Log.v("user_id", user_id);
                        b.putString("name",name);
                        b.putString("user_id",user_id);
                        intent.putExtras(b);
                        context.startActivity(intent);
                    }
                    else{
                        String reason = jsonObject.getString("message");
                        Log.v("reason", reason);
                        if(reason.equals("UserNotExisted")){
                            Toast t = Toast.makeText(LoginActivity.this,"This user is not exists", Toast.LENGTH_LONG);
                            t.show();
                        }
                        if(reason.equals("Worrypwd")){
                            Toast t = Toast.makeText(LoginActivity.this,"Password is not correct", Toast.LENGTH_LONG);
                            t.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
