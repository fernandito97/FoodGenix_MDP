package com.genix.foodgenix.client.Register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.genix.foodgenix.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IdentitasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IdentitasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IdentitasFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_identitas, container, false);
    }
}
