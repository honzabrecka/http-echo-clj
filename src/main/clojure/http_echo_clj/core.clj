(ns http-echo-clj.core
  (:gen-class
    :implements [org.apache.commons.daemon.Daemon])
  (:require
    [http-echo-clj.server :as server])
  (:import
    (org.apache.commons.daemon Daemon DaemonContext)))

;; Standalone JAR

(defn -main [& args]
  (server/init-server)
  (server/start-server!))

;; Commons Daemon

(defn -init [this ^DaemonContext context]
  (server/init-server (.getArguments context)))

(defn -start [this]
  (server/start-server!))

(defn -stop [this]
  (server/stop-server!))

(defn -destroy [this])
