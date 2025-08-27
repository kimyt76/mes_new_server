package com.jct.mes_new.config.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.StringUtils;

@UtilityClass
public class AmountUtil {

    public String amtToKor(String amt){
        String tmpamt = "";

        if (StringUtils.equals(amt, "0")) return "";

        amt = "000000000000" + amt.replaceAll(",", "");
        int j = 0;
        for (int i = amt.length(); i > 0; i--) {
            j++;
            String aaa = amt.substring(i - 1, i);
            if (!aaa.equals("0")) {
                if (j % 4 == 2) tmpamt = "십" + tmpamt;
                if (j % 4 == 3) tmpamt = "백" + tmpamt;
                if (j > 1 && j % 4 == 0) tmpamt = "천" + tmpamt;
            }
            String bbb = amt.substring(amt.length() - 8, amt.length() - 4);
            if (j == 5 && Integer.parseInt(bbb) > 0) tmpamt = "만" + tmpamt;
            String ccc = amt.substring(amt.length() - 12, amt.length() - 8);
            if (j == 9 && Integer.parseInt(ccc) > 0) tmpamt = "억" + tmpamt;
            String ddd = amt.substring(amt.length() - 16, amt.length() - 12);
            if (j == 13 && Integer.parseInt(ddd) > 0) tmpamt = "조" + tmpamt;
            if (aaa.equals("1")) tmpamt = "일" + tmpamt;
            if (aaa.equals("2")) tmpamt = "이" + tmpamt;
            if (aaa.equals("3")) tmpamt = "삼" + tmpamt;
            if (aaa.equals("4")) tmpamt = "사" + tmpamt;
            if (aaa.equals("5")) tmpamt = "오" + tmpamt;
            if (aaa.equals("6")) tmpamt = "육" + tmpamt;
            if (aaa.equals("7")) tmpamt = "칠" + tmpamt;
            if (aaa.equals("8")) tmpamt = "팔" + tmpamt;
            if (aaa.equals("9")) tmpamt = "구" + tmpamt;
        }

        return tmpamt;
    }
}
