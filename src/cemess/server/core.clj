(ns cemess.server.core
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [datomic.api :as d]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :refer [wrap-resource]]
            [cemess.server.middleware :as middleware]))

(defrecord DatomicConnection [options]
  component/Lifecycle
  (start [this]
    (if (:instance this)
      this
      (let [uri (:uri options)]
        (d/create-database uri)
        (log/info (str "Starting/Connecting Datomic Service on " uri))
        (assoc this :instance (d/connect uri)))))
  (stop [this]
    (when-let [conn (:instance this)]
      (log/info "Stopping Datomic Service")
      (d/release conn))
    (assoc this :instance nil)))

(defrecord HttpServer [options handler datomic-connection]
  component/Lifecycle
  (start [this]
    (if (:instance this)
      this
      (assoc this :instance (jetty/run-jetty
                              (-> (fn [req] (handler (assoc req :datomic-connection (:instance datomic-connection))))
                                  (wrap-resource (:static-resources-path options))
                                  (middleware/wrap-transit-response)
                                  (middleware/wrap-transit-params))
                              (assoc options :join? false)))))
  (stop [this]
    (when-let [server (:instance this)]
      (.stop server)
      (.join server))
    (assoc this :instance nil)))

(defn context [{:keys [datomic http]}]
  (component/system-map
    :datomic-connection (map->DatomicConnection {:options datomic})
    ;; KISS, simple query binding to page response built on the client using a streaming builders (focus on client
    ;; first). I have a feeling maybe just using the datomic rest api might be enough here for the moment.
    :handler (fn [_]
               {:status  200
                :headers {"Content-Type" "application/transit+json"}
                :body    nil})
    :http-server (component/using (map->HttpServer {:options http})
                                  [:handler :datomic-connection])))

(def system (context (clojure.edn/read-string (slurp (clojure.java.io/resource "config.edn")))))

(alter-var-root #'system component/start)

(doall (->> ["dev-resources/schema.edn" "dev-resources/seed.edn"]
            (map (comp read-string slurp))
            (map (partial d/transact (get-in system [:datomic-connection :instance])))))

(d/q '[:find [(pull ?e [*]) ...]
       :where [?e :element/name _] [(missing? $ ?e :element/_children)]] (d/db (get-in system [:datomic-connection :instance])))
;(alter-var-root #'system component/stop)