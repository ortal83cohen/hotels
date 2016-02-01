package com.easytobook.api.utils;

import com.easytobook.api.contract.CreditCard;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * @author alex
 * @date 2015-06-30
 */
public class CreditCardUtilsTest {

    @Test
    public void testRecognize() {
        HashMap<String, String> cards = provideCards();

        for (String number : cards.keySet()) {
            String expected = cards.get(number);
            ArrayList<String> actual = CreditCardUtils.recognize(number);

            String message = "Number " + number + ", Expected: " + expected + ", Actual: " + Arrays.toString(actual.toArray());

            assertTrue(message, actual.contains(expected));

        }

    }


    private HashMap<String, String> provideCards() {
        HashMap<String, String> cards = new HashMap<>();
        cards.put("5351661060886523", CreditCard.MASTERCARD);

        cards.put("5446372985261324", CreditCard.MASTERCARD);
        cards.put("5373015003756620", CreditCard.MASTERCARD);
        cards.put("5187715913430564", CreditCard.MASTERCARD);
        cards.put("5264895055183654", CreditCard.MASTERCARD);
        cards.put("5195972021166461", CreditCard.MASTERCARD);
        cards.put("5118373278224307", CreditCard.MASTERCARD);
        cards.put("5259018749960620", CreditCard.MASTERCARD);
        cards.put("5236064381213751", CreditCard.MASTERCARD);
        cards.put("5456244634230496", CreditCard.MASTERCARD);
        cards.put("5450642865510187", CreditCard.MASTERCARD);

        cards.put("4916112817591", CreditCard.VISA);
        cards.put("4485051482882", CreditCard.VISA);
        cards.put("4532780364418", CreditCard.VISA);
        cards.put("4556207260990", CreditCard.VISA);
        cards.put("4716258476388", CreditCard.VISA);
        cards.put("4556241544623", CreditCard.VISA);

        cards.put("4024007149654359", CreditCard.VISA);
        cards.put("4024008747554359", CreditCard.VISA);
        cards.put("4485486580127634", CreditCard.VISA);
        cards.put("4716378868096626", CreditCard.VISA);
        cards.put("4716090175335239", CreditCard.VISA);
        cards.put("4024007132140681", CreditCard.VISA);

        cards.put("4024007104646400", CreditCard.VISA_DEBIT);
        cards.put("4024007149654359", CreditCard.VISA_DEBIT);
        cards.put("4024008747554359", CreditCard.VISA_DEBIT);

        cards.put("4539752109441855", CreditCard.CARTE_SI);

        cards.put("349094462287973", CreditCard.AMEX);
        cards.put("372524587824045", CreditCard.AMEX);
        cards.put("349875155280337", CreditCard.AMEX);
        cards.put("370743905353139", CreditCard.AMEX);
        cards.put("374054939195389", CreditCard.AMEX);

        cards.put("6011449364571888", CreditCard.DISCOVER);
        cards.put("6011301851490785", CreditCard.DISCOVER);
        cards.put("6011944502717991", CreditCard.DISCOVER);
        cards.put("6011478695125213", CreditCard.DISCOVER);

        cards.put("30435943860312", CreditCard.DINERS_CLUB);
        cards.put("30435943860312", CreditCard.DINERS_CLUB);
        cards.put("36034882741256", CreditCard.DINERS_CLUB);
        cards.put("38095471503140", CreditCard.DINERS_CLUB);
        cards.put("60495471503140", CreditCard.DINERS_CLUB);

        cards.put("180050915310715", CreditCard.JCB);
        cards.put("180080153173343", CreditCard.JCB);
        cards.put("180021866685074", CreditCard.JCB);

        cards.put("3337508959965286", CreditCard.JCB);
        cards.put("3088752004773797", CreditCard.JCB);
        cards.put("3096065300131093", CreditCard.JCB);
        cards.put("3528351651129409", CreditCard.JCB);

        cards.put("6706752004773797", CreditCard.LASER_DEBIT);
        cards.put("6771065300131093", CreditCard.LASER_DEBIT);
        cards.put("6709351651129409", CreditCard.LASER_DEBIT);

        cards.put("63045678987654323", CreditCard.LASER_DEBIT);
        cards.put("67067520047737975", CreditCard.LASER_DEBIT);
        cards.put("67710653001310937", CreditCard.LASER_DEBIT);
        cards.put("67093516511294092", CreditCard.LASER_DEBIT);

        cards.put("630456789876543234", CreditCard.LASER_DEBIT);
        cards.put("670675200477379754", CreditCard.LASER_DEBIT);
        cards.put("677106530013109378", CreditCard.LASER_DEBIT);
        cards.put("670935165112940923", CreditCard.LASER_DEBIT);

        cards.put("6304567898765432345", CreditCard.LASER_DEBIT);
        cards.put("6706752004773797546", CreditCard.LASER_DEBIT);
        cards.put("6771065300131093789", CreditCard.LASER_DEBIT);
        cards.put("6709351651129409234", CreditCard.LASER_DEBIT);

        cards.put("38945879662145", CreditCard.CARTE_BLANCHE);
        cards.put("94933333333333", CreditCard.CARTE_BLANCHE);
        cards.put("95933333333333", CreditCard.CARTE_BLANCHE);


        cards.put("4571007149654359", CreditCard.DANKORT);
        cards.put("4571008747554359", CreditCard.DANKORT);
        cards.put("4571752109441855", CreditCard.DANKORT);

        cards.put("4571007149654359", CreditCard.DELTA);
        cards.put("4671008747554359", CreditCard.DELTA);
        cards.put("4871752109441855", CreditCard.DELTA);
        cards.put("4137752109441855", CreditCard.DELTA);
        cards.put("4971752109441855", CreditCard.DELTA);
        cards.put("4462752109441855", CreditCard.DELTA);

        cards.put("5020071496543593", CreditCard.MAESTRO);
        cards.put("5038008747554353", CreditCard.MAESTRO);
        cards.put("6304752109441853", CreditCard.MAESTRO);
        cards.put("6759752109441853", CreditCard.MAESTRO);

        cards.put("502007149654359223", CreditCard.MAESTRO);
        cards.put("503800874755435923", CreditCard.MAESTRO);
        cards.put("630475210944185523", CreditCard.MAESTRO);
        cards.put("675975210944185523", CreditCard.MAESTRO);
        cards.put("5020071496543592255", CreditCard.MAESTRO);

        cards.put("5038008747554359224", CreditCard.MAESTRO);
        cards.put("6304752109441855222", CreditCard.MAESTRO);
        cards.put("6759752109441855222", CreditCard.MAESTRO);

        cards.put("4903071496543593", CreditCard.SWITCH);
        cards.put("4911008747554353", CreditCard.SWITCH);
        cards.put("4936752109441853", CreditCard.SWITCH);
        cards.put("5641822109441853", CreditCard.SWITCH);
        cards.put("6341822109441853", CreditCard.SWITCH);
        cards.put("6759822109441853", CreditCard.SWITCH);

        cards.put("490307149654359223", CreditCard.SWITCH);
        cards.put("491100874755435923", CreditCard.SWITCH);
        cards.put("493675210944185523", CreditCard.SWITCH);
        cards.put("564182109441855233", CreditCard.SWITCH);
        cards.put("634182109441855233", CreditCard.SWITCH);

        cards.put("675982109441855233", CreditCard.SWITCH);

        cards.put("4903071496543592255", CreditCard.SWITCH);
        cards.put("4911008747554359224", CreditCard.SWITCH);
        cards.put("4936752109441855222", CreditCard.SWITCH);
        cards.put("564182109441855222", CreditCard.SWITCH);
        cards.put("634182109441855222", CreditCard.SWITCH);
        cards.put("675982109441855222", CreditCard.SWITCH);

        cards.put("6221260096543593", CreditCard.CHINA_UNIONPAY);
        cards.put("6229259947554353", CreditCard.CHINA_UNIONPAY);
        cards.put("6240000009441853", CreditCard.CHINA_UNIONPAY);
        cards.put("6282000009441853", CreditCard.CHINA_UNIONPAY);
        cards.put("6288999909441853", CreditCard.CHINA_UNIONPAY);

        cards.put("62212600965435937", CreditCard.CHINA_UNIONPAY);
        cards.put("62292599475543537", CreditCard.CHINA_UNIONPAY);
        cards.put("62400000094418537", CreditCard.CHINA_UNIONPAY);
        cards.put("62699999394418537", CreditCard.CHINA_UNIONPAY);
        cards.put("62820000094418537", CreditCard.CHINA_UNIONPAY);
        cards.put("62889999094418537", CreditCard.CHINA_UNIONPAY);

        cards.put("622126009654359378", CreditCard.CHINA_UNIONPAY);
        cards.put("622925994755435378", CreditCard.CHINA_UNIONPAY);
        cards.put("624000000944185378", CreditCard.CHINA_UNIONPAY);
        cards.put("626999993944185378", CreditCard.CHINA_UNIONPAY);
        cards.put("628200000944185378", CreditCard.CHINA_UNIONPAY);
        cards.put("628899990944185378", CreditCard.CHINA_UNIONPAY);

        cards.put("6221260096543593789", CreditCard.CHINA_UNIONPAY);
        cards.put("6229259947554353789", CreditCard.CHINA_UNIONPAY);
        cards.put("6240000009441853789", CreditCard.CHINA_UNIONPAY);
        cards.put("6269999939441853789", CreditCard.CHINA_UNIONPAY);
        cards.put("6282000009441853789", CreditCard.CHINA_UNIONPAY);
        cards.put("6288999909441853789", CreditCard.CHINA_UNIONPAY);

        return cards;
    }
}
