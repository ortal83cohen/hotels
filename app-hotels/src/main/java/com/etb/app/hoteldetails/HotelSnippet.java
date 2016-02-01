package com.etb.app.hoteldetails;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.easytobook.api.model.Accommodation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 * @date 2015-04-29
 */
public class HotelSnippet implements Parcelable {
    public static final Creator<HotelSnippet> CREATOR = new Creator<HotelSnippet>() {
        public HotelSnippet createFromParcel(Parcel in) {
            return new HotelSnippet(in);
        }

        public HotelSnippet[] newArray(int size) {
            return new HotelSnippet[size];
        }
    };
    private static final int MAX_CREDIT_CARD_ITEMS = 8;
    private static final int MAX_MAIN_FACILITIES_ITEMS = 8;
    private static final int MAX_IMAGES_ITEMS = 30;
    private final int rateId;
    private final int position;
    private Accommodation acc;

    public HotelSnippet(Accommodation acc, int rateId, int position) {
        this.acc = acc;
        this.rateId = rateId;
        this.position = position;
    }

    public HotelSnippet(Accommodation acc, int rateId) {
        this.acc = acc;
        this.rateId = rateId;
        this.position = -1;
    }

    public HotelSnippet(Parcel in) {
        position = in.readInt();
        acc = new Accommodation();
        acc.id = in.readInt();
        acc.name = in.readString();
        acc.starRating = in.readInt();
        acc.images = new ArrayList<>();
        String[] imagesUrl = new String[MAX_IMAGES_ITEMS];
        in.readStringArray(imagesUrl);
        for (String imageUrl : imagesUrl) {
            if (imageUrl == null || imageUrl.equals("")) {
                continue;
            }
            acc.images.add(imageUrl);
        }
        acc.location = new Accommodation.Location();
        acc.location.lat = in.readFloat();
        acc.location.lon = in.readFloat();
        acc.mainFacilities = new ArrayList<Integer>();
        int[] facilities = new int[MAX_MAIN_FACILITIES_ITEMS];
        in.readIntArray(facilities);
        for (int i : facilities) {
            if (i != 0) {
                acc.mainFacilities.add(i);
            }
        }
        acc.summary = new Accommodation.Summary();
        acc.summary.reviewCount = in.readInt();
        acc.summary.reviewScore = in.readFloat();
        acc.summary.city = in.readString();
        acc.summary.country = in.readString();
        acc.details = new Accommodation.Details();
        acc.details.generalDescription = in.readString();
        acc.details.checkInFrom = in.readString();
        acc.details.checkOutFrom = in.readString();
        acc.details.nrOfRooms = in.readInt();
        String[] creditCardsCods = new String[MAX_CREDIT_CARD_ITEMS];
        String[] creditCardsNames = new String[MAX_CREDIT_CARD_ITEMS];
        in.readStringArray(creditCardsCods);
        in.readStringArray(creditCardsNames);
        acc.details.postpaidCreditCards = new ArrayList<>();
        for (int i = 0; i < MAX_CREDIT_CARD_ITEMS; i++) {
            if (creditCardsCods[i] != null && creditCardsNames[i] != null) {
                acc.details.postpaidCreditCards.add(new Accommodation.Details.PostpaidCreditCard(creditCardsNames[i], creditCardsCods[i]));
            }
        }
        int numberFacilities = in.readInt();
        String[] facilitiesId = new String[numberFacilities];
        String[] facilitiesName = new String[numberFacilities];
        int[] facilitiesCategory = new int[numberFacilities];
        boolean[] facilitiesDataFree = new boolean[numberFacilities];
        int[] facilitiesDataLocation = new int[numberFacilities];
        in.readStringArray(facilitiesId);
        in.readStringArray(facilitiesName);
        in.readIntArray(facilitiesCategory);
        in.readBooleanArray(facilitiesDataFree);
        in.readIntArray(facilitiesDataLocation);
        acc.facilities = new ArrayList<>();
        for (int i = 0; i < numberFacilities; i++) {
            if (facilitiesId[i] != null && facilitiesName[i] != null && facilitiesCategory[i] != 0) {
                if (facilitiesDataLocation[i] != -1) {
                    acc.facilities.add(new Accommodation.Facility(facilitiesId[i], facilitiesName[i], facilitiesCategory[i], new Accommodation.Facility.Data(facilitiesDataFree[i], facilitiesDataLocation[i])));
                } else {
                    acc.facilities.add(new Accommodation.Facility(facilitiesId[i], facilitiesName[i], facilitiesCategory[i], null));

                }
            }
        }
        acc.details.petsPolicy = new Accommodation.Details.PetsPolicy();
        acc.details.petsPolicy.petsAllowed = in.readInt();
        acc.details.petsPolicy.petsAllowedOnRequest = in.readInt() == 0 ? false : true;

        this.rateId = in.readInt();
        if (this.rateId != 0) {
            Accommodation.Rate rate = new Accommodation.Rate();
            Bundle ratesBundle = in.readBundle();
            rate.rateId = rateId;
            rate.name = ratesBundle.getString("rateName");
            rate.capacity = ratesBundle.getInt("capacity");
            rate.rateKey = ratesBundle.getString("rateKey");
            rate.baseRate = (HashMap<String, Double>) ratesBundle.getSerializable("displayBaseRate");
            rate.totalNetRate = (HashMap<String, Double>) ratesBundle.getSerializable("totalNetRate");
            rate.displayPrice = (HashMap<String, Double>) ratesBundle.getSerializable("displayPrice");
            rate.displayBaseRate = (HashMap<String, Double>) ratesBundle.getSerializable("displayBaseRate");
            rate.taxesAndFees = (ArrayList<Accommodation.Rate.TaxesAndFees>) ratesBundle.getSerializable("taxesAndFees");

            rate.payment = new Accommodation.Rate.Payment();
            rate.payment.prepaid = (HashMap<String, Double>) ratesBundle.getSerializable("payment.prepaid");
            rate.payment.postpaid = (HashMap<String, Double>) ratesBundle.getSerializable("payment.postpaid");
            rate.paymentMethods = (ArrayList<Accommodation.Rate.PaymentMethods>) ratesBundle.getSerializable("paymentMethods");
            // Tags
            boolean[] values = new boolean[5];
            in.readBooleanArray(values);
            Accommodation.Rate.Tags tags = new Accommodation.Rate.Tags();
            tags.breakfastIncluded = values[0];
            tags.earlyBird = values[1];
            tags.freeCancellation = values[2];
            tags.lastMinute = values[3];
            tags.nonRefundable = values[4];
            rate.tags = tags;

            acc.rates = new ArrayList<>();
            acc.rates.add(rate);
        }

    }

    public static HotelSnippet from(Accommodation acc, int rateId, int position) {
        return new HotelSnippet(acc, rateId, position);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeInt(this.acc.id);
        dest.writeString(this.acc.name);
        dest.writeInt(this.acc.starRating);

        int i = 0;
        String[] images = new String[MAX_IMAGES_ITEMS];
        for (String image : getImagesUrl()) {
            if (i == MAX_IMAGES_ITEMS) {
                continue;
            }
            images[i++] = image;
        }
        dest.writeStringArray(images);
        dest.writeFloat(this.acc.location.lat);
        dest.writeFloat(this.acc.location.lon);
        i = 0;
        int[] mainFacilities = new int[MAX_MAIN_FACILITIES_ITEMS];
        if (this.acc.mainFacilities != null) {
            for (int mainFacility : this.acc.mainFacilities) {
                mainFacilities[i++] = mainFacility;

            }
        }
        dest.writeIntArray(mainFacilities);
        dest.writeInt(this.acc.summary.reviewCount);
        dest.writeFloat(this.acc.summary.reviewScore);
        dest.writeString(this.acc.summary.city);
        dest.writeString(this.acc.summary.country);
        dest.writeString(this.acc.details.generalDescription);
        dest.writeString(this.acc.details.checkInFrom);
        dest.writeString(this.acc.details.checkOutFrom);
        dest.writeInt(this.acc.details.nrOfRooms);

        i = 0;
        String[] creditCardsCods = new String[MAX_CREDIT_CARD_ITEMS];
        String[] creditCardsNames = new String[MAX_CREDIT_CARD_ITEMS];
        if (this.acc.details != null && this.acc.details.postpaidCreditCards != null) {
            for (Accommodation.Details.PostpaidCreditCard postpaidCreditCard : this.acc.details.postpaidCreditCards) {
                creditCardsCods[i] = postpaidCreditCard.code;
                creditCardsNames[i++] = postpaidCreditCard.name;
            }
        }
        dest.writeStringArray(creditCardsCods);
        dest.writeStringArray(creditCardsNames);

        i = 0;
        int facilitiesSize = this.acc.facilities == null ? 0 : this.acc.facilities.size();
        String[] facilitiesId = new String[facilitiesSize];
        String[] facilitiesName = new String[facilitiesSize];
        int[] facilitiesCategory = new int[facilitiesSize];
        boolean[] facilitiesDataFree = new boolean[facilitiesSize];
        int[] facilitiesDataLocation = new int[facilitiesSize];
        if (this.acc.facilities != null) {
            for (Accommodation.Facility facility : this.acc.facilities) {
                facilitiesId[i] = facility.id;
                facilitiesName[i] = facility.name;
                facilitiesCategory[i] = facility.category;
                if (facility.data != null) {
                    facilitiesDataFree[i] = facility.data.free;
                    facilitiesDataLocation[i] = facility.data.location;
                } else {
                    facilitiesDataLocation[i] = -1;
                }
                i++;
            }
        }
        dest.writeInt(facilitiesSize);
        dest.writeStringArray(facilitiesId);
        dest.writeStringArray(facilitiesName);
        dest.writeIntArray(facilitiesCategory);
        dest.writeBooleanArray(facilitiesDataFree);
        dest.writeIntArray(facilitiesDataLocation);

        if (this.acc.details.petsPolicy == null) {
            this.acc.details.petsPolicy = new Accommodation.Details.PetsPolicy();
        }
        dest.writeInt(this.acc.details.petsPolicy.petsAllowed);
        dest.writeInt(this.acc.details.petsPolicy.petsAllowedOnRequest ? 1 : 0);

        Accommodation.Rate rate = acc.getRateById(this.rateId);
        if (rate == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(this.rateId);
            Bundle rateBundle = new Bundle();
            rateBundle.putString("rateName", rate.name);
            rateBundle.putInt("capacity", rate.capacity);
            rateBundle.putInt("rateId", rate.rateId);
            rateBundle.putSerializable("displayBaseRate", rate.baseRate);
            rateBundle.putSerializable("totalNetRate", rate.totalNetRate);
            rateBundle.putSerializable("displayPrice", rate.displayPrice);
            rateBundle.putSerializable("displayBaseRate", rate.displayBaseRate);
            rateBundle.putSerializable("taxesAndFees", rate.taxesAndFees);
            rateBundle.putSerializable("payment.postpaid", rate.payment.postpaid);
            rateBundle.putSerializable("payment.prepaid", rate.payment.prepaid);
            rateBundle.putSerializable("paymentMethods", rate.paymentMethods);

            dest.writeBundle(rateBundle);

            Accommodation.Rate.Tags tags = rate.tags;
            boolean[] values = new boolean[]{
                    tags.breakfastIncluded,
                    tags.earlyBird,
                    tags.freeCancellation,
                    tags.lastMinute,
                    tags.nonRefundable
            };
            dest.writeBooleanArray(values);
        }

    }

    public int getPosition() {
        return position;
    }

    public Accommodation getAccommodation() {
        return acc;
    }

    public boolean hasRates() {
        return acc.rates != null && acc.rates.size() > 0;
    }


    public int geId() {
        return acc.id;
    }

    public String getName() {
        return acc.name;
    }

    public int getStarRating() {
        return acc.starRating;
    }

    public String getImageUrl() {
        return (acc.images != null && !acc.images.isEmpty()) ? acc.images.get(0) : null;
    }

    public ArrayList<String> getImagesUrl() {
        if (acc.images == null) {
            return new ArrayList<String>();
        }
        return acc.images;
    }

    public int getReviewCount() {
        return acc.summary.reviewCount;
    }

    public double getBaseRate(String currency) {
        return acc.getFirstRate().baseRate.get(currency);
    }

    public double getTotalNetRate(String currency) {
        return acc.getFirstRate().totalNetRate.get(currency);
    }

    public float getReviewScore() {
        return acc.summary.reviewScore;
    }

    public Accommodation.Rate.Tags getRateTags() {
        return (acc.getFirstRate().tags != null) ? acc.getFirstRate().tags : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public Accommodation.Location getLocation() {
        return acc.location;
    }

    public ArrayList<Integer> getMainFacilities() {
        return acc.mainFacilities;
    }

    public ArrayList<Accommodation.Facility> getFacilities() {
        return acc.facilities;
    }

    public String getDescription() {
        return acc.details.generalDescription;
    }

    public String getcheckOutFrom() {
        return acc.details.checkOutFrom;
    }

    public String getcheckInFrom() {
        return acc.details.checkInFrom;
    }

    public int getNumberOfRooms() {
        return acc.details.nrOfRooms;
    }

    public ArrayList<Accommodation.Details.PostpaidCreditCard> getPostpaidCreditCards() {
        return acc.details.postpaidCreditCards;
    }

    public String getCancellationPolicy() {
        return "";
    }

    public Accommodation.Details.PetsPolicy getPetsPolicy() {
        return acc.details.petsPolicy;
    }

    public String getCity() {
        return acc.summary.city;
    }

    public String getCountry() {
        return acc.summary.country;
    }

    public String getPostpaidCurrency() {
        return acc.postpaidCurrency;
    }

    public int getSelectedRateId() {
        return this.rateId;
    }
}
