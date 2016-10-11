// Copyright (C) 2016 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.common.communication;

import java.util.*;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class InnerIds {

    private static final LinkedHashMap<String, Integer> ids;
    private static final Map<Integer,String> revert;
    private static final Map<Integer, Boolean> intValues;
    private static final Map<Integer, Boolean> taken;
    private static int next = 20000000;

    static {
        ids = new LinkedHashMap<>();
        revert = new HashMap<>();
        intValues = new HashMap<>();
        taken = new HashMap<>();
    }


    /**
     * @param rawId raw id as String
     * @return inner id as int
     */
    public static int getId(String rawId) {
        if(rawId.length() < 10) {
            //take the int if possible
            try {
                int r = Integer.parseInt(rawId, 10);
                return r;
            } catch (NumberFormatException e) {

            }
        }

        if (!ids.containsKey(rawId)) return -1;
        return ids.get(rawId);
    }

    /**
     * @param innerId inner user id as int
     * @return raw id as string
     */
    public static String getId(int innerId) {

        //check whether it was necessary to generate an id
        if (revert.containsKey(innerId)) return revert.get(innerId);

        //check if the id was just parsed
        if(intValues.containsKey(innerId)) return "" + innerId;

        return null;
    }

    /**
     * @param rawId rawId user id as String
     * @return available inner id as int
     */
    public static int getNextId(String rawId) {
        if (ids.containsKey(rawId)) {
            return ids.get(rawId);
        }

        // System.out.println(rawId + ":" + rawId.length());
        if(rawId.length() < 10) {
            //take the int if possible
            try {
                int r = Integer.parseInt(rawId, 10);

                if(!taken.containsKey(r)) {
                    intValues.put(r, true);
                    taken.put(r, true);

                }
                return r;
            } catch (NumberFormatException e) {

            }
        }

        while (taken.containsKey(next)) {
            next++;
        }

        int innerId = next;
        ids.put(rawId, innerId);
        revert.put(innerId, rawId);
        taken.put(innerId, true);

        return innerId;
    }

    private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static String decToNewBase(int dec, int base) {
        StringBuilder sb = new StringBuilder();

        int num = dec;
        int rem;

        while (num > 0) {
            rem = num % base;
            sb.reverse().append(digits[rem]).reverse();

            num = num / base;
        }

        return sb.toString();
    }

    public static String decTo36base(int dec) {
        return decToNewBase(dec, digits.length);
    }


}
