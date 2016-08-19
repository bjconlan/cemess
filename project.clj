(defproject cemess "0.0.1-SNAPSHOT"
  :jvm-opts ^:replace ["-Xms512m" "-Xmx512m" "-server"]
  :dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                 [org.clojure/clojurescript "1.9.216"]
                 [com.datomic/datomic-free "0.9.5394" :exclusions [org.slf4j/*]]
                 [com.cognitect/transit-clj "0.8.288"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [com.stuartsierra/component "0.3.1"]
                 [ring/ring-core "1.6.0-beta5"]
                 [ring/ring-jetty-adapter "1.6.0-beta5"]
                 [ch.qos.logback/logback-classic "1.1.7"]
                 [org.clojure/tools.logging "0.3.1"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "1.1.3"]]
                   :dependencies [[figwheel-sidecar "0.5.4-7"]]}}
  :clean-targets [:target-path "out"]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main       "cemess.client.core"
                                       :asset-path "js/out"
                                       :output-dir "resources/public/js/out"
                                       :output-to  "resources/public/js/main.js"
                                       ;:source-map true
                                       }}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:optimizations :advanced
                                       :pretty-print  false
                                       :main       "cemess.client.core"
                                       :asset-path "out"
                                       :output-dir "target/out"
                                       :output-to  "target/main.min.js"
                                       :source-map "target/main.min.js.map"}}]})