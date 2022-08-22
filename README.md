# Pipe-Auth

---
Pipe-Auth is a java authentication server/api compatible with nginx's auth-request module.

When started, this programm will generate a `users.json` and a `config.properties` file.

This is a basic nginx config which uses this server to check authentication of every request. \
"auth-domain" is the address where this server runs.

```nginx
location / {
    auth_request /auth;

    error_page 401 = @error401;

    try_files $uri $uri/ =404;
}

location @error401 {
    return 302 https://auth-domain/login?redirect=$scheme://$http_host$request_uri;
}

location = /auth {
    internal;
    proxy_pass              https://auth-domain/api/is-authorized;
    proxy_pass_request_body off;
    proxy_set_header        Content-Length "";
    proxy_set_header        X-Original-URI $request_uri;
    proxy_intercept_errors  on;
}
```

### Routes:

- **/login**:
    - Serves the login html file specified in `config.properties`


- **/api/is-authorized**:
    - Checks if the user of the token passed as a cookie or header called "token" may access the URL specified in the
      header "X-Original-URI" in the domain specified in a header called "X-Original-Domain". Returns 200 if the token is invalid but the default user may access the URL *or* if the
      default user may not access the URL but the user who belongs to the token may access it.
    - **Responses:**
        - 200: URL may be accessed
          ```json
          {
            "authorized": true
          }
          ```
        - 401: The URL may not be accessed or the token is invalid
          ```json
          {
            "authorized": false
          }
          ```

- **/api/login**:
    - Generates a login token if the username (provided as a header called "name") and the password (provided as a
      header called "password") match with a user declared in `users.json`. That token will be returned as a cookie and
      in a json response.
    - **Responses:**
        - 200: Success, login was successful
          ```json
          {
            "authorized": true,
            "token": "<the generated token>"
          }
          ```
        - 401: The provided username and password don't match
          ```json
          {
            "authorized": false,
            "token": ""
          }
          ```


- **/api/logout**:
    - Makes any token (passed as a cookie called "token" or header called "token") invalid.
    - **Responses:**
        - 200

---

#### Example `users.json` file:

```json
{
   "default": [
      {
         "patterns": [
            "/**",
            "!/private/**"
         ],
         "domains": [
            "files.domain"
         ]
      },
      {
         "patterns": [
            "/**"
         ],
         "domains": [
            "**.domain"
         ]
      }
   ],
   "users": [
      {
         "password": "super-strong-password",
         "name": "cool-user",
         "domain-permissions": [
            {
               "patterns": [
                  "/**"
               ],
               "domains": [
                  "**.domain"
               ]
            }
         ],
         "last-token-update": 0
      }
   ]
}
```

With this configuration, anyone can access any path except "/private" on "files.domain". The user "cool-user" can access
any path on any domain.

## Note:

- Only the first matching domain is taken into account to check if the path matches.
- Use "**" to block all subdirectories, "*" only blocks *one* directory \
  "!/private/" would only block "/private/asd/" or "/private/hello.txt" but not "/private/asd/hello.txt"
- If no domain or pattern matches, access is denied

---

### Pattern Language

| Example          | Description                                                            |
|------------------|------------------------------------------------------------------------|
| *.pipe           | Matches a path that represents a file name ending in .pipe             |
| *.*              | Matches file names containing a dot                                    |
| *.{pipeman,auth} | Matches file names ending with .pipeman or .auth                       |
| foo.?            | Matches file names starting with foo. and a single character extension |
| /home/*/*        | Matches /home/gus/data                                                 |
| /home/**         | Matches /home/gus and /home/gus/data on UNIX platforms                 |

The following rules are used to interpret glob patterns:

- The * character matches zero or more characters of a name component without crossing directory boundaries.
- The ** characters matches zero or more characters crossing directory boundaries.
- The ? character matches exactly one character of a name component.
- The backslash character (\\) is used to escape characters that would otherwise be interpreted as special characters.
  The expression \\ matches a single backslash and "\\{" matches a left brace for example.
- The [ ] characters are a bracket expression that match a single character of a name component out of a set of
  characters. For example, [abc] matches "a", "b", or "c". The hyphen ( -) may be used to specify a range so [a-z]
  specifies a range that matches from "a" to "z" (inclusive). These forms can be mixed so [abce-g] matches "a", "b", "c"
  , "e", "f" or "g". If the character after the [ is a ! then it is used for negation so [!a-c] matches any character
  except "a", "b", or "c".
- Within a bracket expression the *, ? and \ characters match themselves. The ( -) character matches itself if it is the
  first character within the brackets, or the first character after the ! if negating.
- The { } characters are a group of subpatterns, where the group matches if any subpattern in the group matches.
- The "," character is used to separate the subpatterns. Groups