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
import com.kushal.instamojo_flutter.helpers.MoneyUtil;
import com.kushal.instamojo_flutter.models.EMIOption;
import com.kushal.instamojo_flutter.models.EMIRate;

import java.math.BigDecimal;

public class EMIOptionsFragment extends BaseFragment {

    private PaymentDetailsActivity parentActivity;
    private LinearLayout optionsContainer;
    private EMIOption selectedBank;

    public EMIOptionsFragment() {
    }

    public static EMIOptionsFragment getInstance(EMIOption selectedBank) {
        EMIOptionsFragment optionsView = new EMIOptionsFragment();
        optionsView.setSelectedBank(selectedBank);
        return optionsView;
    }

    private static double getEmiAmount(String totalAmount, String rate, int tenure) {

        double parsedAmount = Double.parseDouble(totalAmount);
        return MoneyUtil.getMonthlyEMI(parsedAmount, new BigDecimal(rate), tenure);
    }

    private static double getTotalAmount(double emiAmount, int tenure) {
        return MoneyUtil.getRoundedValue(emiAmount * tenure, 2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emi_instamojo, container, false);
        parentActivity = (PaymentDetailsActivity) getActivity();
        inflateXML(view);
        loadOptions();
        return view;
    }

    @Override
    public void inflateXML(View view) {
        super.inflateXML(view);
        optionsContainer = view.findViewById(R.id.emi_view_container);
    }

    @Override
    public void onResume() {
        super.onResume();
        parentActivity.updateActionBarTitle(R.string.choose_an_emi_option);
    }

    public void setSelectedBank(EMIOption selectedBank) {
        this.selectedBank = selectedBank;
    }

    private void loadOptions() {
        optionsContainer.removeAllViews();
        String orderAmount = parentActivity.getOrder().getOrder().getAmount();

        for (final EMIRate option : selectedBank.getEmiRates()) {
            View optionView = LayoutInflater.from(getContext()).inflate(R.layout.emi_option_view,
                    optionsContainer, false);
            double emiAmount = getEmiAmount(orderAmount, option.getInterest(), option.getTenure());
            String emiAmountString = "₹" + emiAmount + " x " + option.getTenure() + " Months";
            String finalAmountString = "Total ₹" + getTotalAmount(emiAmount, option.getTenure()) + " @ "
                    + option.getInterest() + "% pa";
            ((TextView) optionView.findViewById(R.id.emi_amount)).setText(emiAmountString);
            ((TextView) optionView.findViewById(R.id.final_emi_amount)).setText(finalAmountString);
            optionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentActivity.loadFragment(CardFragment.getCardForm(
                            CardFragment.Mode.EMI, option.getTenure(), selectedBank.getBankCode()), true);
                }
            });
            optionsContainer.addView(optionView);
        }
    }
}
