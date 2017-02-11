(ns cemess.client.core
  (:require [com.stuartsierra.component :as component]
            [datascript.core :as d]
            [goog.dom :as dom]
            [cemess.client.editor :as editor]))

;(slider of first transaction/date -> last tx date)
;(slider tags)
;(multiple dbs (per... dimension?)

;(prn conn)
#_ (if showscrub
     (entity get history ()))

;https://www.w3.org/TR/dom/#element
(def seed [{:db/id -1 :node/value "foobar"}
           {:db/id -2 :element/tag-name "h1" :node/value "heading"}
           {:db/id -3 :node/value "bar"}
           {:db/id              -3
            :node/child-nodes [{:db/id -1} {:db/id -2}]
            :element/id         "test"
            :element/tag-name   dom/TagName.DIV
            :element/attributes [{:attribute/name "style" :attribute/value "color: red"}
                                 {:attribute/name "class" :attribute/value "basic"}]}])

(def schema {:element/attributes {:db/cardinality :db.cardinality/many
                                  :db/valueType   :db.type/ref
                                  :db/isComponent true}
             :element/id         {:db/unique :db.unique/identity}
             :node/child-nodes   {:db/cardinality :db.cardinality/many                                                  ;https://www.w3.org/TR/dom/#node
                                  :db/valueType   :db.type/ref}})

(defrecord App [options conn]
  component/Lifecycle
  (start [this] (d/transact! conn seed))
  (stop [this] this))

(defn- context [_]
  (component/system-map
    :conn (d/create-conn schema)
    ;:component-registry {}
    :app (component/using (map->App {:options nil}) [:conn])))

(def system (context {}))

(component/start system)
(editor/init)