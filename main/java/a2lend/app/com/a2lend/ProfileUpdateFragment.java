package a2lend.app.com.a2lend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Igbar on 1/23/2018.
 */

public class ProfileUpdateFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_profile,null);
        //super.onCreateView(inflater, container, savedInstanceState);
        // on create

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  created
        Toast.makeText(getContext(), "Profile Fragment", Toast.LENGTH_SHORT).show();

        TextView user_profile_name = (TextView) getActivity().findViewById(R.id.user_profile_name);
        TextView PemailViewText = (TextView) getActivity().findViewById(R.id.PemailViewText);

        user_profile_name.setText("Update Profile");
        PemailViewText.setText("Enter only the relevant information !!");

        EditText nameEditText = (EditText) getActivity().findViewById(R.id.ProfileUserName);
        EditText emailEditText = (EditText) getActivity().findViewById(R.id.ProfileUserName);
        EditText phoneEditText = (EditText) getActivity().findViewById(R.id.ProfileUserName);

        FirebaseUser user = DataAccess.getUser();

        // init EditText With The details
        nameEditText.setText(user.getDisplayName());
        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhoneNumber());

    }




}
