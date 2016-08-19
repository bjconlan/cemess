(ns cmess.server.parser
  (:refer-clojure :exclude [read])
  (:require [datomic.api :as d]))


;; =============================================================================
;; Reads

(defmulti read (fn [env k params] k))

(defmethod read :default
  [_ k _]
  {:value {:error (str "No handler for read key " k)}})

(defmethod read :q
  [{:keys [conn query]} _ {:keys [params]}]
  {:value (apply d/q query (cons (d/db conn) params))})

;; =============================================================================
;; Mutations

(defmulti mutate (fn [env k params] k))

(defmethod mutate :default
  [_ k _]
  {:value {:error (str "No handler for mutation key " k)}})

(defmethod mutate :transact
  [{:keys [conn]} _ {:keys [params body]}]
  {:value {:keys [:todos/list]}
   :action (fn []
             @(d/transact conn body))})
