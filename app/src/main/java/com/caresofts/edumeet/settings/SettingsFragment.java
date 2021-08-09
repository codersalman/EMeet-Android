package com.caresofts.edumeet.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.caresofts.edumeet.MainActivity;
import com.caresofts.edumeet.R;
import com.caresofts.edumeet.bean.UserBean;
import com.caresofts.edumeet.firebase_db.DatabaseManager;
import com.caresofts.edumeet.profile.ProfileActivity;
import com.caresofts.edumeet.utils.AppConstants;
import com.caresofts.edumeet.utils.SharedObjects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment implements DatabaseManager.OnDatabaseDataChanged {

    SharedObjects sharedObjects;
    @BindView(R.id.txtUserName)
    TextView txtUserName;
    @BindView(R.id.imgUser)
    CircularImageView imgUser;
    @BindView(R.id.txtEmail) TextView txtEmail;
    @BindView(R.id.llProfile) LinearLayout llProfile;
    @BindView(R.id.llRateUs) LinearLayout llRateUs;
    @BindView(R.id.llLogout) LinearLayout llLogout;
    @BindView(R.id.llinsta) LinearLayout llinsta;
    @BindView(R.id.llpri) LinearLayout llpri;
    UserBean userBean;
    DatabaseManager databaseManager ;


    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        sharedObjects = new SharedObjects(getActivity());
        databaseManager = new DatabaseManager(getActivity());
        databaseManager.setDatabaseManagerListener(this);
        setUserData();
        return view;
    }


    @OnClick({R.id.llProfile,R.id.llRateUs,R.id.llpri, R.id.llshare,R.id.llLogout})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.llProfile:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;
            case R.id.llLogout:
                ((MainActivity) getActivity()).removeAllPreferenceOnLogout();
                break;
            case R.id.llRateUs:
                if (SharedObjects.isNetworkConnected(getActivity())) {
                    final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                } else {
                    AppConstants.showAlertDialog(getString(R.string.err_internet),getActivity());
                }
                break;
            case R.id.llinsta:
                if (SharedObjects.isNetworkConnected(getActivity())) {
                    final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                } else {
                    AppConstants.showAlertDialog(getString(R.string.err_internet),getActivity());
                }
                break;
            case R.id.llpri:
                if (SharedObjects.isNetworkConnected(getActivity())) {
                   try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privecy))));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privecy))));
                    }
                } else {
                    AppConstants.showAlertDialog(getString(R.string.err_internet),getActivity());
                }
                break;

            case R.id.llshare:
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Edumeet");
                String shareMessage = "\nDownload this  cool app for online meetings and classrooms\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + appPackageName + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));

        }
    }
    public void setUserData() {
        userBean = sharedObjects.getUserInfo();
        if (userBean != null) {
            if (!TextUtils.isEmpty(userBean.getProfile_pic())) {
                Picasso.get().load(userBean.getProfile_pic())
                        .error(R.drawable.ic_account).into(imgUser);
            } else {
                imgUser.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.avatar));
            }

            if (!TextUtils.isEmpty(userBean.getName())) {
                txtUserName.setText( userBean.getName());
            } else {
                txtUserName.setText("Hi, User ");
            }
            if (!TextUtils.isEmpty(userBean.getEmail())) {
                txtEmail.setText( userBean.getEmail());
            } else {
                txtEmail.setText("email@app.com");
            }
            databaseManager.getMeetingHistoryByUser(sharedObjects.getUserInfo().getId());
        } else {
            txtUserName.setText("Hi, ");
            imgUser.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.avatar));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDataChanged(String url, DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError error) {

    }
}
