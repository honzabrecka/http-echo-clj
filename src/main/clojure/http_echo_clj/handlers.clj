(ns http-echo-clj.handlers
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clojure.tools.logging :refer [info warn error]]
    [cognitect.transit :as transit]
    [clojure.core.async :as async :refer [go]]
    [ring.util.codec :refer [form-decode]])
  (:import
    (java.io ByteArrayOutputStream InputStream)))

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

(defn input-stream->bytes
  [input-stream]
  (let [baos (ByteArrayOutputStream. 4096)]
    (io/copy input-stream baos)
    (into [] (.toByteArray baos))))

(defn textual-content-type?
  [content-type]
  (and (some? content-type)
       (or (string/starts-with? content-type "text/")
           (contains?
             #{"application/json"
               "application/xml"}
             content-type))))

(defn extract-content-type
  [req]
  (get (:headers req) "content-type"))

(defn extract-character-encoding
  [req]
  (get (:headers req) "character-encoding"))

(defn sanitize-req
  [req]
  (-> req
    (select-keys [:server-port
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
                  :character-encoding])
    (update :body (fn [v]
                    (if (instance? InputStream v)
                      (if (-> req extract-content-type textual-content-type?)
                        (slurp v :encoding (extract-character-encoding req))
                        (input-stream->bytes v))
                      v)))))

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
