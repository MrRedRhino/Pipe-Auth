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
  - Checks if the user of the token passed as a cookie or header called "token" may access the URL specified in the header "X-Original-URI". Returns 200 if the token is invalid but the default user may access the URL *or* if the default user may not access the URL but the user who belongs to the token may access it.
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
  - Generates a login token if the username (provided as a header called "name") and the password (provided as a header called "password") match with a user declared in `users.json`. That token will be returned as a cookie and in a json response.
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
  "default": {
    "allowed-paths": [
      "/*"
    ],
    "denied-paths": [
      "/private/*"
    ]
  },
  "users": [
    {
      "password": "super-strong-password",
      "name": "cool-user",
      "allowed-paths": [
        "/*"
      ],
      "denied-paths": [],
      "last-token-update": 0
    }
  ]
}
```
With this configuration, anyone can access any path except "/private". The user "cool-user" can access any path. 
