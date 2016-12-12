(ns cemess.client.core
  (:require [com.stuartsierra.component :as component]
            [datascript.core :as d]
            [goog.dom :as dom]))

;https://www.w3.org/TR/dom/#element
(def seed [{:db/id -1
            :node/value "foobar"}
           {:db/id -2
            :node/child-nodes [{:db/id -1}]
            :element/id "test"
            :element/tag-name dom/TagName.DIV
            :element/attributes {:attribute/name "style" :attribute/value "color: red"}}])
(def conn (d/create-conn {:element/attributes {:db/cardinality :db.cardinality/many
                                               :db/valueType :db.type/ref
                                               :db/isComponent true}
                          :element/id         {:db/unique :db.unique/identity}
                          :node/child-nodes   {:db/cardinality :db.cardinality/many                                     ;https://www.w3.org/TR/dom/#node
                                               :db/valueType :db.type/ref}}))

(d/transact! conn seed)

(into {} (d/entity @conn [:element/id "test"]))

(defn createNode [{:keys [:element/tag-name :element/id :element/attributes :node/child-nodes :node/value] :as node}]
  (if tag-name
    (let [el (dom/createElement tag-name)]
      (some->> id (.setAttribute el "id"))
      (doseq [{:keys [:attribute/name :attribute/value]} attributes]
        (.setAttribute el name value))
      (doseq [node child-nodes]
        (.appendChild el (createNode (into {} node))))
      el)
    (some-> value (dom/createTextNode))))

(dom/append js/document.body (createNode (d/entity @conn [:element/id "test"])))

#_(defrecord App [options dom-conn data-conn]
  component/Lifecycle
  (start [this]
    #_(let [conn data-conn]
      (d/transact! conn [{:maker/name "Honda" :maker/country "Japan"}])
      (d/transact! conn [{:db/id         -1
                          :maker/name    "BMW"
                          :maker/country "Germany"}
                         {:car/maker  -1
                          :car/name   "i525"
                          :car/colors ["red" "green" "blue"]}])
      (prn (d/q '[:find ?e ?name
                  :where
                  [?e :maker/name "BMW"]
                  [?c :car/maker ?e]
                  [?c :car/name ?name]]
                @conn))))
  (stop [this] this))

#_(defn- context [_]
  (component/system-map
    :dom-conn (d/conn-from-datoms [{:maker/name "Honda" :maker/country "Japan"}])
    :data-conn (d/conn-from-datoms [] {:car/maker {:db/type :db.type/ref}
                                       :car/colors {:db/cardinality :db.cardinality/many}})                                                                                   ;pretty generic name, perhaps 'content' or 'customizations'
    :component-registry {}
    :app (component/using (map->App {:options nil}) [:dom-conn :data-conn])))

#_(def system (context {}))

#_(component/start system)