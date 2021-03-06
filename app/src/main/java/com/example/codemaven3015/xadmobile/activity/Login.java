package com.example.codemaven3015.xadmobile.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.codemaven3015.xadmobile.R;
import com.example.codemaven3015.xadmobile.api.VolleyJSONRequest;
import com.example.codemaven3015.xadmobile.fragment.FragmentOtpVarification;
import com.example.codemaven3015.xadmobile.helper.GenericTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public static ViewPager mViewPager;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = this.getSharedPreferences("User_Info", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        EditText et1, et2, et3, et4, phoneEditText,firstNameEditText,lastNameEditText;
        Button verfyButton,changePhoneButton,resendButton;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;

            int i = getArguments().getInt(ARG_SECTION_NUMBER);
            if (i == 0) {
                rootView = inflater.inflate(R.layout.fragment_login, container, false);
                Button button = rootView.findViewById(R.id.phoneButton);
                firstNameEditText=rootView.findViewById(R.id.name_editText);
                lastNameEditText=rootView.findViewById(R.id.nameLast_editText);
                phoneEditText = rootView.findViewById(R.id.login_editText);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(inputValidation()) {
                            callNext();
                        }
                    }
                });
            } else {
                rootView = inflater.inflate(R.layout.fragment_otp, container, false);
                setWidgets(rootView);
            }
            return rootView;
        }
        private boolean inputValidation(){
            if(phoneEditText.getText().length()<10){
                phoneEditText.setError("Invalid phone number");
                return false;
            }else if(phoneEditText.getText().toString().trim().isEmpty()){
                phoneEditText.setError("Enter phone number");
                return false;
            }else if(firstNameEditText.getText().toString().isEmpty()){
                firstNameEditText.setError("Enter first Name");
                return false;

            }else if(lastNameEditText.getText().toString().isEmpty()){
                lastNameEditText.setError("Enter Last name");
                return false;
            }
            return true;
        }
        private void callNext() {
            String url = "http://xadnew.quickbooksupport365.com/service/register.php";
            HashMap<String, String> params = new HashMap<>();
            params.put("register","1");
            params.put("mobile_no",phoneEditText.getText().toString());
            params.put("first_name",firstNameEditText.getText().toString());
            params.put("last_name",lastNameEditText.getText().toString());
            VolleyJSONRequest volleyJSONRequest = new VolleyJSONRequest(getContext(),url,params);
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Sending OTP");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            volleyJSONRequest.executeStringRequest(new VolleyJSONRequest.VolleyJSONRequestInterface() {
                @Override
                public void onSuccess(JSONObject obj) {
                    progressDialog.hide();

                    try {
                        String status = obj.getString("status");
                        if(status.equalsIgnoreCase("success")){
                            editor.putString("PHONE",phoneEditText.getText().toString());
                            JSONObject objData = new JSONObject();
                            objData = obj.getJSONObject("data");
                            editor.putString("LAST_ID",objData.getString("last_insert_id"));
                            editor.putString("FIRST_NAME",firstNameEditText.getText().toString());
                            editor.commit();
                            mViewPager.setCurrentItem(1);

                        }else{
                            String msg=obj.getString("message");
                            Toast.makeText(getContext(),msg,
                                    Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(VolleyError error) {
                    progressDialog.hide();

                }
            });
//            volleyJSONRequest.executeRequest(new VolleyJSONRequest.VolleyJSONRequestInterface() {
//                @Override
//                public void onSuccess(JSONObject obj) {
//                    Log.e("Res",obj.toString());
//                    try {
//                        String status = obj.getString("status");
//                        if(status.equalsIgnoreCase("success")){
//                            editor.putString("PHONE",phoneEditText.getText().toString());
//                            JSONObject objData = new JSONObject();
//                            objData = obj.getJSONObject("data");
//                            editor.putString("LAST_ID",objData.getString("last_insert_id"));
//                            editor.commit();
//                            mViewPager.setCurrentItem(1);
//
//                        }else{
//                            String msg=obj.getString("message");
//                            Toast.makeText(getContext(),msg,
//                                    Toast.LENGTH_LONG).show();
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(VolleyError error) {
//                    Toast.makeText(getContext(),error.getMessage().toString(),
//                            Toast.LENGTH_LONG).show();
//                }
//            });
//            //mViewPager.setCurrentItem(1);
        }

        private void setWidgets(View v) {
            verfyButton = v.findViewById(R.id.verfyButton);
            changePhoneButton = v.findViewById(R.id.changePhoneButton);
            resendButton = v.findViewById(R.id.resendButton);
            et1 = v.findViewById(R.id.editText1);
            et2 = v.findViewById(R.id.editText2);
            et3 = v.findViewById(R.id.editText3);
            et4 = v.findViewById(R.id.editText4);
            et1.addTextChangedListener(new GenericTextWatcher(et1,et2,et3,et4));
            et2.addTextChangedListener(new GenericTextWatcher(et2,et2,et3,et4));
            et3.addTextChangedListener(new GenericTextWatcher(et3,et2,et3,et4));
            et4.addTextChangedListener(new GenericTextWatcher(et4,et2,et3,et4));
            verfyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verfyOTP();
                }
            });

            changePhoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeNumber();
                }
            });

            resendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resendButtonotp();
                }
            });
        }

        private void changeNumber() {
            mViewPager.setCurrentItem(0);
        }

        private void verfyOTP() {

            String url = "http://xadnew.quickbooksupport365.com/service/register.php";
            HashMap<String, String> params = new HashMap<>();
            params.put("varify","1");
            params.put("mobile_no",sharedPreferences.getString("PHONE",""));
            params.put("otp","1234");
            params.put("last_inser_id",sharedPreferences.getString("LAST_ID",""));
            VolleyJSONRequest volleyJSONRequest = new VolleyJSONRequest(getContext(),url,params);
            volleyJSONRequest.executeStringRequest(new VolleyJSONRequest.VolleyJSONRequestInterface() {
                @Override
                public void onSuccess(JSONObject obj) {
                    Log.e("Res",obj.toString());
                    mViewPager.setCurrentItem(2);
                    try {
                        String status = obj.getString("status");
                        if(status.equalsIgnoreCase("success")){
                            mViewPager.setCurrentItem(2);

                        }else{
                            String msg=obj.getString("message");
                            Toast.makeText(getContext(),msg,
                                    Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(VolleyError error) {
                    Toast.makeText(getContext(),error.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                }
            });

            //mViewPager.setCurrentItem(2);
        }
        private void resendButtonotp(){

        }


    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return PlaceholderFragment.newInstance(0);

                case 1:
                    return PlaceholderFragment.newInstance(1);

                case 2:
                    FragmentOtpVarification fragmentOtpVarification = new FragmentOtpVarification();
                    return fragmentOtpVarification;

                 default:
                        return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }





}


