(ns http-echo-clj.handlers
  (:require
    [clojure.tools.logging :refer [info warn error]]
    [cognitect.transit :as transit]
    [clojure.core.async :as async :refer [go]]
    [ring.util.codec :refer [form-decode]])
  (:import
    (java.io ByteArrayOutputStream)))

(defn clj->transit
  [v]
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json)]
    (transit/write writer v)
    (.toString out)))

(defn make-response
  [code body]
  {:status  code
   :headers {"Content-Type" "application/json"}
   :body    (clj->transit body)})

(defn req->query-params
  [req]
  (if-let [query-string (:query-string req)]
    (form-decode query-string)
    {}))

(defn sanitize-req
  [req]
  (select-keys req [:server-port
                    :server-name
                    :remote-addr
                    :uri
                    :query-string
                    :scheme
                    :request-method
                    :headers
                    :body
                    :content-type
                    :content-length
                    :character-encoding]))

(defn handle
  [req]
  (let [query-params (req->query-params req)]
    (go
      (try
        (let [sanitized-req (sanitize-req req)]
          (info sanitized-req)
          (make-response 200 sanitized-req))
        (catch Throwable t
          (warn t)
          (make-response 400 {:failed (.getMessage t)}))))))

(defn handle-get
  [req]
  (handle req))

(defn handle-post
  [req]
  (handle req))

(defn handle-put
  [req]
  (handle req))

(defn handle-delete
  [req]
  (handle req))

(defn handle-head
  [req]
  (handle req))

(defn handle-options
  [req]
  (handle req))

(defn handle-patch
  [req]
  (handle req))
