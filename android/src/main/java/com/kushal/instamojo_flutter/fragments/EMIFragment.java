package com.kushal.instamojo_flutter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kushal.instamojo_flutter.R;
import com.kushal.instamojo_flutter.activities.PaymentDetailsActivity;
import com.kushal.instamojo_flutter.helpers.Logger;
import com.kushal.instamojo_flutter.models.EMIOption;

public class EMIFragment extends BaseFragment {

    private static final String TAG = EMIFragment.class.getSimpleName();
    private PaymentDetailsActivity parentActivity;
    private LinearLayout emiBanksContainer;

    public EMIFragment() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emi_instamojo, container, false);
        parentActivity = (PaymentDetailsActivity) getActivity();
        inflateXML(view);
        loadBanks();
        return view;
    }

    @Override
    public void inflateXML(View view) {
        emiBanksContainer = view.findViewById(R.id.emi_view_container);
    }

    @Override
    public void onResume() {
        super.onResume();
        parentActivity.updateActionBarTitle(R.string.choose_your_credit_card);
    }

    private void loadBanks() {
        emiBanksContainer.removeAllViews();

        for (final EMIOption bank : parentActivity.getOrder().getPaymentOptions().getEmiOptions().getEmiOptions()) {
            View bankView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_instamojo, emiBanksContainer, false);
            ((TextView) bankView.findViewById(R.id.item_name)).setText(bank.getBankName());
            bankView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EMIOptionsFragment optionsView = EMIOptionsFragment.getInstance(bank);
                    parentActivity.loadFragment(optionsView, true);
                }
            });

            emiBanksContainer.addView(bankView);
        }

        Logger.d(TAG, "Loaded EMI Banks");
    }
}
