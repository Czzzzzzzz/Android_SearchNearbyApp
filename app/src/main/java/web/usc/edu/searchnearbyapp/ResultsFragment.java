package web.usc.edu.searchnearbyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.dynamic.IFragmentWrapper;

import okhttp3.internal.Util;
import web.usc.edu.searchnearbyapp.constants.MyConstants;
import web.usc.edu.searchnearbyapp.constants.SharePreferenceConstants;
import web.usc.edu.searchnearbyapp.util.LocalStorageHandler;
import web.usc.edu.searchnearbyapp.util.Utils;
import web.usc.edu.searchnearbyapp.widgets.Autocomplete;


public class ResultsFragment extends Fragment{

    private final String TAG = "ResultsFragment";

    private Button btn_search;
    private Button btn_clear;
    private EditText et_keyword;
    private Spinner spinner_category;
    private EditText et_radius;
    private RadioGroup mRadioGroup;
    private AutoCompleteTextView mEtSpecifiedLocation;
    private TextView mErrorMsgKeyword;
    private TextView mErrorMsgLocation;

    private ProgressDialog mProgressDialog;

    Autocomplete mAutocomplete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        et_keyword = (EditText)view.findViewById(R.id.et_keyword);
        spinner_category = (Spinner)view.findViewById(R.id.spinner_category);
        et_radius = (EditText)view.findViewById(R.id.et_radius);
        mRadioGroup = (RadioGroup)view.findViewById(R.id.radio_group_from);
        btn_search = (Button) view.findViewById(R.id.btn_search);
        btn_clear = (Button) view.findViewById(R.id.btn_clear);
        mEtSpecifiedLocation = (AutoCompleteTextView)view.findViewById(R.id.et_specified_location);
        mErrorMsgKeyword = (TextView) view.findViewById(R.id.error_msg_keyword);
        mErrorMsgLocation = (TextView) view.findViewById(R.id.error_msg_location);

        mEtSpecifiedLocation.setEnabled(false);

        createAutocompleteView();

        addListenerToViews();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        mAutocomplete.stopAutoManage(getActivity());
    }

    private void createAutocompleteView() {
        mAutocomplete = new Autocomplete();
        mAutocomplete.createAutoCompleteView(getActivity(), mEtSpecifiedLocation, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //do nothing
            }
        });
    }

    private void addListenerToViews() {

        btn_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_keyword.setText("");
                spinner_category.setSelection(0);
                et_radius.setText("Enter Distance (default 10 miles)");
                mRadioGroup.check(R.id.radio_current_location);

                mErrorMsgKeyword.setVisibility(View.GONE);
                mErrorMsgLocation.setVisibility(View.GONE);
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.radio_current_location:
                        mEtSpecifiedLocation.setEnabled(false);
                        mErrorMsgLocation.setVisibility(View.GONE);
                        break;
                    case R.id.radio_specified_location:
                        mEtSpecifiedLocation.setEnabled(true);
                        break;
                }

            }
        });
    }

    private void verify() {
        String keyword = et_keyword.getText().toString();
        String specifiedLocation = mEtSpecifiedLocation.getText().toString();
        int checkedRadio = mRadioGroup.getCheckedRadioButtonId();
        String category = spinner_category.getSelectedItem().toString().toLowerCase().replace(" ", "_");

        String radius =  et_radius.getText().toString();
        if (radius.equals(""))
            radius = Utils.convertMilesToMeters("10");
        else
            radius = Utils.convertMilesToMeters(radius);

        String flag;

        float latitude = LocalStorageHandler.readFloatFromSharedPreference(getActivity(), MyConstants.latitude);
        float longitude = LocalStorageHandler.readFloatFromSharedPreference(getActivity(), MyConstants.longitude);
        String location = "" + latitude + "," + longitude;

        boolean keywordFlag;
        if (keyword.equals("")) {
            keywordFlag = false;
            mErrorMsgKeyword.setVisibility(View.VISIBLE);
        } else {
            keywordFlag = true;
            mErrorMsgKeyword.setVisibility(View.GONE);
        }

        boolean locationFlag;
        if (checkedRadio == R.id.radio_specified_location && specifiedLocation.equals("")) {
            locationFlag = false;
            mErrorMsgLocation.setVisibility(View.VISIBLE);
            flag = "location";
        }
        else if (checkedRadio == R.id.radio_current_location) {
            locationFlag = true;
            mErrorMsgLocation.setVisibility(View.GONE);
            flag = "here";
        }
        else {
            locationFlag = true;
            mErrorMsgLocation.setVisibility(View.GONE);
            flag = "location";
            location = mEtSpecifiedLocation.getText().toString();
        }

        if (keywordFlag && locationFlag) {
            String url = MyConstants.HOST_NAME + MyConstants.relative_directory + MyConstants.SEARCH_PHP + "?keyword=" + keyword
                    + "&category=" + category + "&radius=" + radius + "&location=" + location + "&flag=" + flag;
            Log.d(TAG, "onClick: " + url);
            searchHttpRequest(url);
        }
        else {
            CharSequence text = "Please fix all fields with errors";
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }

    }

    private void searchHttpRequest(String url)
    {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Feteching results");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);

                mProgressDialog.dismiss();

                LocalStorageHandler.saveInSharedPreference(getActivity().getApplicationContext(), "test", "test value");

                Intent intent = new Intent(getActivity(), SearchedResultsActivity.class);
                intent.putExtra(MyConstants.DATA, response);

                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });

        queue.add(stringRequest);
    }

}