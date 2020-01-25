package com.kushal.instamojo_flutter.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kushal.instamojo_flutter.R;
import com.kushal.instamojo_flutter.activities.PaymentDetailsActivity;
import com.kushal.instamojo_flutter.adapters.WalletListAdapter;
import com.kushal.instamojo_flutter.helpers.Constants;
import com.kushal.instamojo_flutter.helpers.Logger;
import com.kushal.instamojo_flutter.models.Wallet;
import com.kushal.instamojo_flutter.models.WalletOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 */
public class WalletFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static final String TAG = WalletFragment.class.getSimpleName();
    private PaymentDetailsActivity parentActivity;
    private ListView mWalletListView;
    private TextView headerTextView;

    /**
     * Creates a new Instance of Fragment.
     */
    public WalletFragment() {
        // Required empty public constructor
    }

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_form_instamojo, container, false);
        parentActivity = (PaymentDetailsActivity) getActivity();
        inflateXML(view);
        loadAllWallets();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        headerTextView.setText(R.string.choose_your_wallet);
        parentActivity.updateActionBarTitle(R.string.wallets);
        parentActivity.showSearchOption(getString(R.string.search_your_wallet), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        parentActivity.hideSearchOption();
    }

    @Override
    public void inflateXML(View view) {
        mWalletListView = view.findViewById(R.id.list_container);
        headerTextView = view.findViewById(R.id.header_text);
        Logger.d(TAG, "Inflated XML");
    }

    private void loadAllWallets() {
        loadWallets("");
    }

    private void loadWallets(String query) {
        final WalletOptions walletOptions = parentActivity.getOrder().getPaymentOptions().getWalletOptions();
        final List<Wallet> filteredWallets = new ArrayList<>();
        for (final Wallet wallet : walletOptions.getWallets()) {
            if (wallet.getName().toLowerCase(Locale.US).contains(query.toLowerCase(Locale.US))) {
                filteredWallets.add(wallet);
            }
        }

        WalletListAdapter adapter = new WalletListAdapter(getActivity(), filteredWallets);
        mWalletListView.setAdapter(adapter);
        mWalletListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.URL, walletOptions.getSubmissionURL());
                bundle.putString(Constants.POST_DATA, walletOptions.getPostData(filteredWallets.get(position).getId()));
                parentActivity.startPaymentActivity(bundle);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        loadWallets(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        loadWallets(newText);
        return false;
    }
}
