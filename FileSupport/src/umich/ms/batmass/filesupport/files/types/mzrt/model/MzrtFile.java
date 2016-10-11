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
package umich.ms.batmass.filesupport.files.types.mzrt.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import umich.ms.batmass.data.core.api.DataLoadingException;

/**
 *
 * @author Dmitry Avtonomov
 */
public class MzrtFile {
    Path file;
    Map<String, Integer> header;
    List<CSVRecord> records;
    int[] indexesMzRtColorOpacity = null;
    
    private static final String HEAD_MZLO = "mzLo";
    private static final String HEAD_MZHI = "mzHi";
    private static final String HEAD_RTLO = "rtLo";
    private static final String HEAD_RTHI = "rtHi";
    private static final String HEAD_COLOR = "color";
    private static final String HEAD_OPACITY = "opacity";
    
    public MzrtFile(Path file) {
        if (!Files.exists(file))
            throw new IllegalArgumentException("File doesn't exist.");
        if (!Files.isRegularFile(file))
            throw new IllegalArgumentException("File must be a regular file.");
        this.file = file;
        indexesMzRtColorOpacity = new int[6];
        Arrays.fill(indexesMzRtColorOpacity, -1);
    }
    
    public void load() throws DataLoadingException {
        
        char delimiter;
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            List<String> lines = new ArrayList<>();
            String line;
            int numTestLines = 10;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines.add(line);
                    if (lines.size() >= numTestLines)
                        break;
                }
            }
            
            delimiter = guessDelimiter(lines);
        } catch (IOException ex) {
            throw new DataLoadingException(ex);
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            CSVFormat fmt = CSVFormat.newFormat(delimiter);
            fmt = fmt.withHeader()
                .withIgnoreEmptyLines(true)
                .withTrim(true)
                .withIgnoreHeaderCase(true);
            
            CSVParser parser = fmt.parse(br);
            
            records = parser.getRecords();
            header = parser.getHeaderMap();
            
            
            String[] colNames = {HEAD_MZLO, HEAD_MZHI, HEAD_RTLO, HEAD_RTHI};
            for (int i = 0; i < colNames.length; i++) {
                Integer index = header.get(colNames[i]);
                if (index == null)
                    throw new DataLoadingException(String.format("Missing header column [%s]", colNames[i]));
                indexesMzRtColorOpacity[i] = index;
            }
            Integer indexColor = header.get(HEAD_COLOR);
            if (indexColor != null && indexColor >= 0)
                indexesMzRtColorOpacity[4] = indexColor;
            Integer indexOpacity = header.get(HEAD_OPACITY);
            if (indexOpacity != null && indexOpacity >= 0)
                indexesMzRtColorOpacity[5] = indexOpacity;
                
            
        } catch (IOException ex) {
            throw new DataLoadingException(ex);
        }
    }

    public Path getFile() {
        return file;
    }

    public Map<String, Integer> getHeader() {
        return header;
    }

    public List<CSVRecord> getRecords() {
        return records;
    }

    public int[] getIndexesMzRtColorOpacity() {
        return indexesMzRtColorOpacity;
    }
    
    private char guessDelimiter(List<String> lines) throws DataLoadingException {
        char[] delimiters = {' ', '\t', ',', ';'};
        int [][] counts = new int[lines.size()][delimiters.length]; // [line][delimiter]
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                for (int k = 0; k < delimiters.length; k++) {
                    if (line.charAt(j) == delimiters[k]) {
                        counts[i][k]++;
                    }
                }
            }
        }
        
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < delimiters.length; i++) {
            char delimiter = delimiters[i];
            boolean isSameCounts = true;
            int cntPrev = counts[0][i];
            for (int j = 0; j < lines.size(); j++) {
                if (counts[j][i] != cntPrev) {
                    isSameCounts = false;
                    break;
                }
                cntPrev = counts[j][i];
            }
            if (isSameCounts) {
                map.put(delimiter, cntPrev);
            }
        }
        
        Character delimiter = null;
        Integer count = -1;
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue() > count) {
                count = entry.getValue();
                delimiter = entry.getKey();
            }
        }
        if (count <= 0)
            throw new DataLoadingException("Could not auto-detect delimiter");
        
        return delimiter;    
    }
}
