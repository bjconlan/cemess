(ns cmess.client.parser
  (:require [om.next :as om]))

(defmulti read om/dispatch)

(defmethod read :default [{:keys [state]} k _]
  (js/console.info ":default>" state k))

(defmethod read :q
  [{:keys [state]} l _]
  (let [st @state
        x (js/alert st)]
    {:value (get st :q)}
    {:remote true}))

(defmulti mutate om/dispatch)

(defmethod mutate :default [_ _ _]
  {:remote true})