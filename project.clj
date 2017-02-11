(defproject cemess "0.0.1-SNAPSHOT"
  :jvm-opts ^:replace ["-Xms512m" "-Xmx512m" "-server"]
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.473"]
                 ;[com.datomic/datomic-free "0.9.5544" :exclusions [org.slf4j/*]]
                 [datascript "0.15.5"]
                 [cljsjs/inferno "1.2.2-0"]
                 ;[com.cognitect/transit-clj "0.8.297"]
                 ;[com.cognitect/transit-cljs "0.8.239"]
                 ;[com.stuartsierra/component "0.3.1"]
                 ;[ring/ring-core "1.6.0-beta6"]
                 ;[ring/ring-jetty-adapter "1.6.0-beta6"]
                 ;[ch.qos.logback/logback-classic "1.1.8"]
                 ;[org.clojure/tools.logging "0.3.1"]
                 ]
  :profiles {:dev {:plugins [[lein-cljsbuild "1.1.5"]]
                   :dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.9"]]}}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :clean-targets [:target-path "out"]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/cemess/client"]
                        :figwheel     true
                        :compiler     {:main       "cemess.client.core"
                                       :asset-path "js/out"
                                       :output-dir "resources/public/js/out"
                                       :output-to  "resources/public/js/main.js"}}
                       {:id           "prod"
                        :source-paths ["src/cemess/client"]
                        :compiler     {:optimizations :advanced
                                       :pretty-print  false
                                       :main       "cemess.client.core"
                                       :asset-path "out"
                                       :output-dir "target/out"
                                       :output-to  "target/main.min.js"
                                       :source-map "target/main.min.js.map"}}]})