/*
 * Copyright 2016 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umich.ms.batmass.filesupport.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.openide.util.Exceptions;

/**
 * Very simple utilities for parsing delimited files containing only numbers. 
 * If you're looking for a more general parsing facility, you should look at
 * Apache Commons CSV, for example.
 * 
 * @author Dmitry Avtonomov
 */
public class DelimitedFiles {
    private DelimitedFiles() {}

    /**
     * Takes a delimited string of double values and parses it efficiently.
     * Does not support scientific notation or anything else apart from standard numbers with dots as decimal separators.
     * @param line the line to be parsed
     * @param delimiter delimiter separating columns
     * @param decimalSep the character used for separating decimal part of the number, typically '.'
     * @param parser the delegate that will be given all the parsed info about each number
     */
    public static void readLineOfNumbers(final String line, final char delimiter, final char decimalSep, final NumberParsingDelegate parser) {
        final char lineEndN = '\n';
        final char lineEndR = '\r';
        final int radix = 10;

        int lastDotPos = -1, lastNumberStartPos = 0, lastNumberLen = 0;
        int digit;


        int result = 0;
        boolean negative = false;
        int i = 0, len = line.length();
        int limit = -Integer.MAX_VALUE;
        int multmin;
        char firstChar, curChar;
        int curNumberIdx = 0;

        if (len > 0) {

            while (i < len) {
                lastNumberLen = 0;
                firstChar = line.charAt(i);
                if (firstChar < '0') { // Possible leading "+" or "-"
                    if (firstChar == '-') {
                        negative = true;
                        limit = Integer.MIN_VALUE;
                    } else if (firstChar != '+')
                        throw new IllegalStateException();

                    if (len == 1) // Cannot have lone "+" or "-"
                        throw new IllegalStateException();
                    i++;
                }
                multmin = limit / radix;
                while (i < len) {
                    // Accumulating negatively avoids surprises near MAX_VALUE
                    curChar = line.charAt(i);
                    if (curChar == delimiter) {
                        parser.parse(curNumberIdx, -result, lastNumberLen, lastDotPos);
                        curNumberIdx++;
                        i++;
                        lastDotPos = -1;
                        result = 0;
                        limit = -Integer.MAX_VALUE;
                        break;
                    } else if (curChar == '.') {
                        lastDotPos = lastNumberLen;
                        i++;
                    } else {
                        digit = Character.digit(curChar, radix);
                        if (digit < 0) {
                            throw new IllegalStateException();
                        }
                        if (result < multmin) {
                            throw new IllegalStateException();
                        }
                        result *= radix;
                        if (result < limit + digit) {
                            throw new IllegalStateException();
                        }
                        result -= digit;
                        lastNumberLen++;
                        i++;
                    }
                }
            }
            // Invoke the parser on the last number in the line
            parser.parse(curNumberIdx, -result, lastNumberLen, lastDotPos);
        }
    }

    public abstract static class NumberParsingDelegate {
        public abstract void parse(final int idx, int number, int length, int decimalPos);
    }

    public static double parseDouble(int number, int length, int decimalPos) {
        return number / Math.pow(10d, (length - decimalPos));
    }

    public static float parseFloat(int number, int length, int decimalPos) {
        return (float)parseDouble(number, length, decimalPos);
    }

    public static int parseInt(int number, int length, int decimalPos) {
        return number;
    }
    
    /**
     * Reads the first line from the provided stream and splits it by delimiter.
     * @param is The stream to read from
     * @param delimiter the delimiter
     * @return null in case of error. If successful, the returned array will contain
     * trimmed column names.
     */
    public static String[] readDelimitedHeader(InputStream is, final char delimiter) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            String[] split = line.split(new String(new char[]{delimiter}));
            for (int i = 0; i < split.length; i++)
                split[i] = split[i].trim();
            return split;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
