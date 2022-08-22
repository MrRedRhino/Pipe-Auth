package org.pipeman.pa.permissions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DomainPermission implements JSONString {
    private final String[] allowDomains;
    private final String[] denyDomains;

    private final String[] allowPatterns;
    private final String[] denyPatterns;

    private final String[] domains;

    private final String[] patterns;

    public DomainPermission(String[] domains, String[] patterns) {
        this.domains = domains;
        this.patterns = patterns;
        List<String> daList = new ArrayList<>();
        List<String> ddList = new ArrayList<>();

        for (String domain : domains) {
            if (domain.startsWith("!")) {
                ddList.add(domain.substring(1));
            } else {
                daList.add(domain);
            }
        }

        this.allowDomains = new String[daList.size()];
        for (int i = 0; i < allowDomains.length; i++) {
            allowDomains[i] = daList.get(i);
        }
        this.denyDomains = new String[ddList.size()];
        for (int i = 0; i < denyDomains.length; i++) {
            denyDomains[i] = ddList.get(i);
        }

        ////////////////////////////////////////

        List<String> paList = new ArrayList<>();
        List<String> pdList = new ArrayList<>();

        for (String pattern : patterns) {
            if (pattern.startsWith("!")) {
                pdList.add(pattern.substring(1));
            } else {
                paList.add(pattern);
            }
        }

        this.allowPatterns = new String[paList.size()];
        for (int i = 0; i < allowPatterns.length; i++) {
            allowPatterns[i] = paList.get(i);
        }

        this.denyPatterns = new String[pdList.size()];
        for (int i = 0; i < denyPatterns.length; i++) {
            denyPatterns[i] = pdList.get(i);
        }
    }

    public boolean appliesForDomain(String domain) {
        for (String allow : allowDomains) {
            if (WildCardHelper.matchesDomain(domain, allow)) {
                for (String deny : denyDomains) {
                    if (WildCardHelper.matchesDomain(domain, deny)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean allowsPath(String path) {
        for (String allow : allowPatterns) {
            if (WildCardHelper.matchesPath(path, allow)) {
                for (String deny : denyPatterns) {
                    if (WildCardHelper.matchesPath(path, deny)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static DomainPermission from(JSONObject object) {
        JSONArray jDomains = object.getJSONArray("domains");
        JSONArray jPatterns = object.getJSONArray("patterns");

        String[] domains = new String[jDomains.length()];
        for (int i = 0; i < domains.length; i++) {
            domains[i] = (String) jDomains.get(i);
        }

        String[] patterns = new String[jPatterns.length()];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = (String) jPatterns.get(i);
        }
        return new DomainPermission(domains, patterns);
    }

    @Override
    public String toJSONString() {
        return new JSONObject(Map.of("domains", domains, "patterns", patterns)).toString(4);
    }
}
