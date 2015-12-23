(defproject tundra "0.1.0-SNAPSHOT"
  :description "Template web application."
  :url "https://github.com/maxcountryman/tundra"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts ^:replace ["-Xms512m" "-Xmx512m" "-server"]
  :dependencies [[clout "1.2.0"]
                 [com.cognitect/transit-cljs "0.8.225"]
                 [com.datomic/datomic-free "0.9.5206" :exclusions [joda-time]]
                 [figwheel-sidecar "0.5.0-SNAPSHOT" :scope "test"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.omcljs/om "1.0.0-alpha22"]
                 [ring/ring-jetty-adapter "1.3.0"]
                 [ring/ring-json "0.4.0"]]
  :source-paths ["src/clj" "src/cljs"]
  :main tundra.core)
