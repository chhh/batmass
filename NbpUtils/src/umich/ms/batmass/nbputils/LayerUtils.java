/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.ms.batmass.nbputils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * Utilities for layer files manipulation.
 * @author Dmitry Avtonomov
 */
public abstract class LayerUtils {
    private LayerUtils(){};


    /**
     * Allowed directory name in the layer. Must start with a letter, and end with
     * a letter or a number.
     */
    public static final Pattern ALLOWED_CHARS_DIR_RE =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_\\-]*[A-Za-z0-9]+$");
    /** 
     * Allowed file name in the layer. Must start with a letter, and end with
     * a letter or a number.
     */
    public static final Pattern ALLOWED_CHARS_FILE_RE =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_\\-\\.]*[A-Za-z0-9]+$");
    
    /**
     * Just replaces dots with dashes.
     * @param name
     * @return 
     */
    public static String getLayerFriendlyInstanceName(String name) {
        return name.replace('.', '-') + ".instance";
    }

    /**
     * Checks a particular directory name for presence of forbidden characters.
     * @param name the name of the file, not it's path. In other words, it's just
     * a single element of the path.<br/>
     * <b>IMPORTANT:</b><br/>
     * <b>Must start with a letter, and end with a letter or a number.</b>
     * @return true, if the name is OK
     */
    public static boolean validateLayerDirName(CharSequence name) {
        Matcher matcher = ALLOWED_CHARS_DIR_RE.matcher(name);
        return matcher.matches();
    }

    /**
     * Checks a particular file name for presence of forbidden characters.
     * @param name the name of the file, not it's path. In other words, it's just
     * a single element of the path.
     * <b>IMPORTANT:</b><br/>
     * <b>Must start with a letter, and end with a letter or a number.</b>
     * @return true, if the name is OK
     */
    public static boolean validateLayerFileName(CharSequence name) {
        Matcher matcher = ALLOWED_CHARS_FILE_RE.matcher(name);
        return matcher.matches();
    }


    /**
     * Canonical name of the class, with all '.' (dots) replaced by '-'.
     * @param clazzName
     * @return
     */
    public static String getLayerFriendlyClassName(String clazzName) {
        return clazzName.replace('.', '-');
    }

    /**
     * Canonical name of the class, with all '.' (dots) replaced by '-'.
     * @param clazz
     * @return
     */
    public static String getLayerFriendlyClassName(Class<?> clazz) {
        return getLayerFriendlyClassName(clazz.getCanonicalName());
    }

    /**
     * Joins the elements of a path using '/'. Will NOT sanitize the inputs,
     * if {@code isInputSafe} parameter is true.
     * @param pathElemets
     * @return
     *
     * @see #getLayerPath(java.lang.String...)
     */
    public static String getLayerPathUnsafe(List<String> pathElemets) {
        return StringUtils.join(pathElemets, '/');
    }

    /**
     * Joins the elements of a path using '/'. Will NOT sanitize the inputs,
     * if {@code isInputSafe} parameter is true.
     * @param pathElemets
     * @return
     *
     * @see #getLayerPath(java.lang.String...)
     */
    public static String getLayerPathUnsafe(String... pathElemets) {
        return StringUtils.join(pathElemets, '/');
    }

    /**
     * <b/>WARNING: will modify the input list. Will remove empty and null elements
     * and replace leading/trailing slashes.</b>
     * Joins the elements of a path using '/'. Will trim any leading or trailing
     * slashes from the elements and also replace all forward slashes to
     * backward ones.
     * @param pathElemets
     * @return
     */
    public static String getLayerPath(List<String> pathElemets) {
        pathElemets.removeAll(Arrays.asList("", null));
        for (int i = 0; i < pathElemets.size(); i++) {
            String s = pathElemets.get(i);
            int sLen = s.length();
            s = s.replace('\\', '/');
            int subStrLo = 0;
            if (s.charAt(subStrLo) == '/') {
                subStrLo = 1;
            }
            int subStrHi = sLen;
            if (s.charAt(subStrHi - 1) == '/') {
                subStrHi = subStrHi - 1;
            }
            if (subStrLo != 0 || subStrHi != sLen) {
                s = s.substring(subStrLo, subStrHi);
            }
            pathElemets.set(i, s);
        }
        return getLayerPathUnsafe(pathElemets);
    }

    /**
     * Joins the elements of a path using '/'. Will trim any leading or trailing
     * slashes from the elements and also replace all forward slashes to
     * backward ones.
     * @param pathElemets
     * @return
     */
    public static String getLayerPath(String... pathElemets) {
        ArrayList<String> list = new ArrayList<>(pathElemets.length);
        list.addAll(Arrays.asList(pathElemets));
        return getLayerPath(list);
    }
}
