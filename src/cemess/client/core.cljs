(ns cemess.client.core
  (:require [com.stuartsierra.component :as component]
            [datascript.core :as d]))

(defrecord DatascriptConnection [options]
  component/Lifecycle
  (start [this]
    (if (:instance this)
      this
      (assoc this :instance (d/create-conn options))))
  (stop [this]
    (assoc this :instance nil)))

(defrecord App [options datascript-connection]
  component/Lifecycle
  (start [this]
    (let [conn (:instance datascript-connection)]
      (prn (d/transact! conn [{:maker/name "Honda" :maker/country "Japan"}]))
      (d/transact! conn [{:db/id         -1
                          :maker/name    "BMW"
                          :maker/country "Germany"}
                         {:car/maker  -1
                          :car/name   "i525"
                          :car/colors ["red" "green" "blue"]}])
      (d/q '[:find ?e ?name
             :where
             [?e :maker/name "BMW"]
             [?c :car/maker ?e]
             [?c :car/name ?name]]
           @conn)))
  (stop [this] this))

(defn context [{:keys [schema]}]
  (component/system-map
    :datascript-connection (map->DatascriptConnection {:options schema})
    :component-registry {:test []}
    :app (component/using (map->App {:options nil}) [:datascript-connection])))

(def system (context {:schema {:car/maker {:db/type :db.type/ref}
                               :car/colors {:db/cardinality :db.cardinality/many}}}))

(component/start system)