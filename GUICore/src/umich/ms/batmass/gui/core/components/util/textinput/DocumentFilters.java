/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package umich.ms.batmass.gui.core.components.util.textinput;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 *
 * @author dmitriya
 */
public abstract class DocumentFilters {
    
    public static PlainDocument getFilter(final String filteredCharsRegex) {
        PlainDocument doc = new PlainDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int off, String str, AttributeSet attr)
                    throws BadLocationException {
                fb.insertString(off, str.replaceAll(filteredCharsRegex, ""), attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int off, int len, String str, AttributeSet attr)
                    throws BadLocationException {
                fb.replace(off, len, str.replaceAll(filteredCharsRegex, ""), attr);
            }
        });
        return doc;
    }
    
    public static PlainDocument getDigitsOnlyFilter() {
        PlainDocument doc = new PlainDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
                    throws BadLocationException {
                fb.insertString(off, str.replaceAll("\\D++", ""), attr);  // remove non-digits
            }

            @Override
            public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
                    throws BadLocationException {
                fb.replace(off, len, str.replaceAll("\\D++", ""), attr);  // remove non-digits
            }
        });
        return doc;
    }
    
    public static PlainDocument getDigitsAndDotFilter() {
        PlainDocument doc = new PlainDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int off, String str, AttributeSet attr)
                    throws BadLocationException {
                fb.insertString(off, str.replaceAll("[^0-9\\.]", ""), attr);  // remove non-digits and dots
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int off, int len, String str, AttributeSet attr)
                    throws BadLocationException {
                String text = fb.getDocument().getText(0, fb.getDocument().getLength());
                Pattern dot = Pattern.compile("\\.");
                Matcher m = dot.matcher(text);
                StringBuffer sb = new StringBuffer();
                int cnt = 0;
                while(m.find()) {
                    cnt++;
                    if (cnt > 1)
                        m.appendReplacement(sb, "");
                }
                m.appendTail(sb);
                fb.replace(off, len, str.replaceAll("[^0-9\\.]", ""), attr);  // remove non-digits and dots
            }
        });
        return doc;
    }
}
