package org.pipeman.pa.permissions;

public class May {
    public static boolean access(String path, String domain, DomainPermission[] perms) {
        for (DomainPermission perm : perms) {
            if (perm.appliesForDomain(domain)) {
                return perm.allowsPath(path);
            }
        }
        return false;
    }
}
