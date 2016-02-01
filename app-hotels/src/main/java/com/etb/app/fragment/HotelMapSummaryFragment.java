package com.etb.app.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easytobook.api.model.Accommodation;
import com.etb.app.App;
import com.etb.app.R;
import com.etb.app.adapter.HotelViewHolder;
import com.etb.app.hoteldetails.HotelSnippet;
import com.etb.app.model.HotelListRequest;

import butterknife.ButterKnife;

/**
 * @author user
 * @date 2015-05-25
 */
public class HotelMapSummaryFragment extends BaseFragment {

    private static final String EXTRA_SNIPPET = "snippet";
    private HotelSnippet mHotelSnippet;
    private HotelViewHolder.Listener mListener;

    public static HotelMapSummaryFragment newInstance(Accommodation acc) {
        HotelMapSummaryFragment fragment = new HotelMapSummaryFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SNIPPET, new HotelSnippet(acc, 0, -1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_hotel_summary, container, false);
        ButterKnife.bind(this, view);

        mHotelSnippet = getArguments().getParcelable(EXTRA_SNIPPET);

        Resources r = getActivity().getResources();
        int pictureWidth = r.getDimensionPixelSize(R.dimen.listview_image_width);
        int pictureHeight = r.getDimensionPixelSize(R.dimen.listview_image_height);

        HotelViewHolder hotelViewHolder = new HotelViewHolder(view, getActivity(), pictureWidth,pictureHeight, getPriceRender(), mListener);
        HotelListRequest request = getHotelsRequest();
        hotelViewHolder.assignItem(mHotelSnippet.getAccommodation(), request.getNumberOfRooms(), mHotelSnippet.getPosition());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (HotelViewHolder.Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HotelMapSummaryFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_SNIPPET, mHotelSnippet);
        super.onSaveInstanceState(outState);
    }

}
