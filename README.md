# http-echo-clj

Test HTTP Echo Server echoing Clojure data.

Essentially, the server takes the request Ring request map and returns it encoded in the response body as Transit JSON.

# Running Locally

You can run the server locally by using:

```
lein run
```

Then the server is listening on `localhost:52173`.

# Running on a Linux server with Nginx

These instructions assume Ubuntu 14.04 is being used.

## JSVC

```
sudo apt-get install jsvc
```

## Nginx

```
sudo apt-get install nginx
```

Copy `http-echo-clj.conf` from the server subtree to `/etc/nginx/conf.d`. Edit it for your domain name, _etc._
Make it be owned by root.

```
sudo service nginx restart
```

## Server

Create `http-echo-clj` user with no shell:

```
sudo useradd -r -s /bin/false http-echo-clj
```

Copy `http-echo-clj` script from server subtree into `/etc/inid.d` and make sure it is owned and executable by `root`.

Install the script with the OS:
```
sudo update-rc.d http-echo-clj defaults
```

Make an `/opt/http-echo-clj` directory and make it be owned by `http-echo-clj`:

```
sudo mkdir /opt/http-echo-clj
sudo chown http-echo-clj /opt/http-echo-clj
sudo chgrp http-echo-clj /opt/http-echo-clj
```

Build the server by doing

```
lein uberjar
```

Copy the resulting `target/http-echo-clj-x.x.x-standalone.jar` to the `/opt/http-echo-clj` directory and make a symbolic link to it named `http-echo-clj-CURRENT-standalone.jar`.

Start up the api-server service:

```
sudo service http-echo-clj start
```

Check `/var/log/http-echo-clj.err` and `/var/log/http-echo-clj.out` for correct operation.

The server should now be responding to requests on port 80 for the domain name configured in `http-echo-clj.conf`.