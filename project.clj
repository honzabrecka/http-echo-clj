(defproject http-echo-clj "0.3.0"
  :min-lein-version "2.6.1"
  :dependencies [[compojure "1.5.1"]
                 [http-kit "2.1.16"]
                 [javax.servlet/javax.servlet-api "3.1.0"]
                 [log4j/log4j "1.2.17"]
                 [org.apache.commons/commons-daemon "1.0.9"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/core.async "0.2.395"]
                 [com.cognitect/transit-clj "0.8.297"]]
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :test-paths ["src/test/clojure"]
  :resource-paths ["src/main/resource"]
  :main http-echo-clj.core
  :uberjar-name "server"
  :aot :all)
