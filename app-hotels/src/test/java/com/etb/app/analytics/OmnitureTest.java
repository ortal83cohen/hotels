package com.etb.app.analytics;

import com.easytobook.api.model.SearchRequest;
import com.etb.app.model.Location;

import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author alex
 * @date 2015-11-29
 */
public class OmnitureTest {


    @Test
    public void testCreateContextRequestData() {
        SearchRequest request = new SearchRequest();
        request.setType(new Location());
        ((Location)request.getType()).setTitle("Alaska");
        request.getDateRange().from.set(2015, Calendar.DECEMBER, 4, 6, 30, 5);
        request.getDateRange().to.set(2015, Calendar.DECEMBER, 6, 6, 30, 6);
        request.setNumberOfPersons(3);

        Omniture omniture = mock(Omniture.class);
        when(omniture.createContextRequestData(request)).thenCallRealMethod();

        Calendar today = Calendar.getInstance(Locale.getDefault());
        today.set(2015, Calendar.NOVEMBER, 27, 23, 15, 7);
        when(omniture.getToday()).thenReturn(today);

        HashMap<String, Object> actual = omniture.createContextRequestData(request);

        HashMap<String, Object> expected = new HashMap<>();
        expected.put(Omniture.KEY_PLATFORM, "android");
        expected.put(Omniture.KEY_LOCATION_TITLE, "Alaska");
        expected.put(Omniture.KEY_DATE_FROM, "2015-12-04");
        expected.put(Omniture.KEY_DATE_UNTIL, "2015-12-06");
        expected.put(Omniture.KEY_NIGHTS, 2);
        expected.put(Omniture.KEY_LEADTIME, 7);
        expected.put(Omniture.KEY_PERSONS, "3|0|3");
        expected.put(Omniture.KEY_ROOMS, 1);

        assertEquals(expected,actual);
    }

}
