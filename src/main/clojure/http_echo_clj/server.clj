(ns http-echo-clj.server
  (:require
    [clojure.core.async :as async :refer [take!]]
    [clojure.tools.logging :refer [info warn error]]
    [compojure.core :refer [defroutes GET PUT POST DELETE HEAD OPTIONS PATCH]]
    [compojure.handler :refer [site]]
    [http-echo-clj.handlers :refer [handle-get handle-post handle-put handle-delete handle-head handle-options handle-patch]]
    [org.httpkit.server :as http-server]
    [ring.middleware.multipart-params.byte-array :refer [byte-array-store]])
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream)))

(defn handle-async
  [handler req]
  (http-server/with-channel req channel
    (take! (handler req) #(http-server/send! channel %))))

(defroutes all-routes
  (GET "*" [] (partial handle-async handle-get))
  (POST "*" [] (partial handle-async handle-post))
  (PUT "*" [] (partial handle-async handle-put))
  (DELETE "*" [] (partial handle-async handle-delete))
  (HEAD "*" [] (partial handle-async handle-head))
  (OPTIONS "*" [] (partial handle-async handle-options))
  (PATCH "*" [] (partial handle-async handle-patch)))

(defonce server (atom nil))

(defn init-server
  [& args])

(defn start-server!
  []
  (info "Starting server...")
  (reset! server (http-server/run-server
                   (site #'all-routes {:multipart {:store (byte-array-store)}})
                   {:port 52173}))
  (info "Server started."))

(defn stop-server!
  []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 1000ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (info "Stopping server...")
    (@server :timeout 1000)
    (reset! server nil)
    (info "Server stopped.")))

(defn restart-server!
  []
  (stop-server!)
  (start-server!))
