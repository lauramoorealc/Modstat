/*
 * Copyright (c) 2011 Automated Logic Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.controlj.green.modstat.checker;


import com.controlj.green.addonsupport.access.Location;
import com.controlj.green.addonsupport.access.SystemAccess;
import com.controlj.green.modstat.FlashStorage;
import com.controlj.green.modstat.Modstat;
import com.controlj.green.modstat.checks.ReportRow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlashStorageFree extends BaseChecker {
    public static final String FIELD_WARN_LIMIT = "warn";

    int warnLimit = 5000;

    public FlashStorageFree(String id) {
        super(id);
        name = "File Storage Space";
        description = "Check for small amounts of free file storage space (flash).";
        fieldNames.addAll(Arrays.asList(FIELD_WARN_LIMIT));
    }

    @Override @NotNull
    public String getFieldValue(String fieldName) throws InvalidFieldNameException {
        if (FIELD_WARN_LIMIT.equals(fieldName)) {
            return Integer.toString(warnLimit);
        }
        return super.getFieldValue(fieldName);
    }


    @Override
    public void setFieldValue(String fieldName, String value) throws InvalidFieldValueException, InvalidFieldNameException {
        if (FIELD_WARN_LIMIT.equals(fieldName)) {
            warnLimit = intValueFromString(value);
        } else {
            super.setFieldValue(fieldName, value);
        }
    }

    @NotNull
    @Override
    public String getConfigHTML() {
        return "Warning if less than " + getTextInputHTML(FIELD_WARN_LIMIT, "size=\"6\"")+
                " bytes of database space are available.";
    }


    @Override
    public List<ReportRow> check(Modstat modstat, SystemAccess access, Location location) {
        List<ReportRow> result = null;

        if (modstat.hasFlashStorage()) {
            FlashStorage storage = modstat.getFlashStorage();
            long free = storage.getFreeFileSize();


            if (free < warnLimit) {
                result = new ArrayList<ReportRow>();
                long available = storage.getTotalFileSize();
                result.add(ReportRow.warning("Only "+ countFormat.format(free)+" bytes out of "+
                        countFormat.format(available) + " bytes of file storage space are available (" +
                        percentFormat.format(((float)free) / available) + ")."));
            }
        }
        return result;
    }
}