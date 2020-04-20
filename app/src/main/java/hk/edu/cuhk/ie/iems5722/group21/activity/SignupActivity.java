package hk.edu.cuhk.ie.iems5722.group21.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import hk.edu.cuhk.ie.iems5722.group21.R;
import hk.edu.cuhk.ie.iems5722.group21.connect.Common;

public class SignupActivity extends AppCompatActivity {

    private String url = "http://18.188.52.141/api/a3/signup";
    MessageDigest md;
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar =  findViewById(R.id.sign_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Button btn_signup = findViewById(R.id.btn_signup);
        final EditText Signup_username = findViewById(R.id.Signup_username);
        final EditText Signup_password = findViewById(R.id.Signup_password);

        random  = new Random(System.currentTimeMillis());

        try {
             md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Signup_username.getText()+"";
                String password = Signup_password.getText()+"";
                if(username.length()==0 || password.length()==0 ){
                    Toast t = Toast.makeText(SignupActivity.this,"empty content！", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                // 先MD5加密再发送
                md.update(password.getBytes());
                String encodepwd = new BigInteger(1,md.digest()).toString(16);
                // 随机id 9位
                int user_id = Math.abs(random.nextInt());
                SignupTask signupTask =
                       new SignupTask(SignupActivity.this, url, user_id, username, encodepwd);
                signupTask.execute();
                //   Log.v("username:",username);
                //   Log.v("password:",password);
                //   Log.v("endcodepassword:",encodepwd);
                //   Log.v("randomid" , String.valueOf(user_id));
            }
        });
    }

    //httpTask
    class SignupTask extends AsyncTask<Void,Integer,String> {

        private Context context;
        private String url;
        private int user_id;
        private String name;
        private String password;


        public SignupTask(Context context, String url, int user_id, String name, String password) {
            this.context = context;
            this.url = url;
            this.password = password;
            this.user_id = user_id;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {

            String status = Common.Post_Signup(url, user_id, name, password);
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
                        Toast t = Toast.makeText(SignupActivity.this,"Registration successful", Toast.LENGTH_LONG);
                        t.show();
                    }
                    else{
                        String reason = jsonObject.getString("message");
                        Log.v("reason", reason);
                        if(reason.equals("UserExisted")){
                           Toast t = Toast.makeText(SignupActivity.this,"This user already exists", Toast.LENGTH_LONG);
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
