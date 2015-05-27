package com.gcm.haxorware.gcmexcel.Facebook;

/**
 * Created by haxorware on 25/5/15.
 */
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcm.haxorware.gcmexcel.MainActivity;
import com.gcm.haxorware.gcmexcel.R;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements OnRequestSocialPersonCompleteListener {
    private String message = "Need simple social networks integration? Check this lbrary:";
    private String TAG = "GCM";

    private static final String NETWORK_ID = "NETWORK_ID";
    private SocialNetwork socialNetwork;
    private int networkId;
    private ImageView photo;
    private TextView name;
    private TextView id;
    private TextView info;
    private Button friends;
    private Button share;
    private RelativeLayout frame;

    public static ProfileFragment newInstannce(int id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(NETWORK_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        networkId = getArguments().containsKey(NETWORK_ID) ? getArguments().getInt(NETWORK_ID) : 0;
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        frame = (RelativeLayout) rootView.findViewById(R.id.frame);
        photo = (ImageView) rootView.findViewById(R.id.imageView);
        name = (TextView) rootView.findViewById(R.id.name);
        id = (TextView) rootView.findViewById(R.id.id);
        info = (TextView) rootView.findViewById(R.id.info);
        /*friends = (Button) rootView.findViewById(R.id.friends);
        friends.setOnClickListener(friendsClick);
        share = (Button) rootView.findViewById(R.id.share);
        share.setOnClickListener(shareClick);*/
        colorProfile(networkId);

        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        socialNetwork.requestCurrentPerson();

        MainActivity.showProgress("Loading social person");
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                socialNetwork.logout();
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        String fbid="";
        String fbname="";
        String fbinfo="";
        String gcmid="";

        SharedPreferences.Editor editor = MainActivity.preferences.edit();
        editor.putString("id",socialPerson.id);
        editor.putString("name",socialPerson.name);
        editor.putString("info", socialPerson.toString());
        editor.putString("gcmid", "");

        editor.apply();


        MainActivity.gcmcheck();
        fbid = MainActivity.preferences.getString("id", "");
        fbname = MainActivity.preferences.getString("name", "");
        fbinfo = MainActivity.preferences.getString("info", "");
        gcmid = MainActivity.preferences.getString("gcmid", "");
        MainActivity.hideProgress();
        name.setText(fbname);
        id.setText(fbid);
        String socialPersonString = fbinfo;
        String infoString = socialPersonString.substring(socialPersonString.indexOf("{")+1, socialPersonString.lastIndexOf("}"));
        info.setText(infoString.replace(", ", "\n"));
        Picasso.with(getActivity())
                .load(socialPerson.avatarURL)
                .into(photo);
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        MainActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private void colorProfile(int networkId){
        int color = getResources().getColor(R.color.dark);
        int image = R.mipmap.ic_launcher;
        switch (networkId) {
            case FacebookSocialNetwork.ID:
                color = getResources().getColor(R.color.facebook);
                image = R.drawable.com_facebook_profile_picture_blank_square;
                break;
        }
        frame.setBackgroundColor(color);
        name.setTextColor(color);
        photo.setBackgroundColor(color);
        photo.setImageResource(image);
    }

    private AlertDialog.Builder alertDialogInit(String title, String message) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setCancelable(true);
        return ad;
    }

}