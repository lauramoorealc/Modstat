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

package com.controlj.green.modstat.section;

import com.controlj.green.modstat.LineSource;
import com.controlj.green.modstat.Modstat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecondaryArcnetStatSection extends ModstatSection {
    //Secondary ARC156 cumulative diagnostics since last reset:
    //  Rx READY
    //  Tx READY
    //  SlaveResets=1
    //  RxCmd=1976670
    private static final Matcher validLine = Pattern.compile("\\s+(.+)\\s*=\\s*(\\d+)\\s*").matcher("");


    public SecondaryArcnetStatSection(LineSource source, Modstat modstat) {
        super(source, modstat);
    }

    @Override
    public boolean lookForSection() {
        boolean foundSection = false;
        String parts[];

        if (source.getCurrentLine().startsWith("Secondary ARC156 cumulative diagnostics")) {
            String stateLine = source.nextLine();
            if (stateLine.startsWith("  Rx ")) {
                modstat.setSecondaryArcnetRxState(stateLine.substring(5));
            }
            stateLine = source.nextLine();
            if (stateLine.startsWith("  Tx ")) {
                modstat.setSecondaryArcnetTxState(stateLine.substring(5));
            }
            while ((parts = matchesStart(source.nextLine(), validLine)) != null) {
                String name = parts[0].trim();
                try {
                    long count = Long.parseLong(parts[1]);
                    modstat.getSecondaryArcnetStats().put(name, count);
                    foundSection = true;
                } catch (NumberFormatException e) { }
            }
        }

        return foundSection;
    }
}