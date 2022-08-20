package org.pipeman.pa.users;

import org.apache.commons.io.FilenameUtils;

public class May {
    public static boolean access(String path, String[] allowed, String[] denied) {
        for (String a : allowed) {
            if (FilenameUtils.wildcardMatch(path, a)) {
                for (String d : denied) {
                    if (FilenameUtils.wildcardMatch(path, d)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
