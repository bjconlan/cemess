(ns cmess.client.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [cognitect.transit :as transit]
            [cmess.client.parser :as parser])
  (:import [goog.net XhrIo]))

(defui Root
  static om/IQueryParams
  (params [this]
    {:query '[:find [(pull ?e [*]) ...]
              :where [?e :element/name _]
                     [(missing? $ ?e :element/_children)]]})
  static om/IQuery
  (query [this]
    '[{:q ?query}])

  Object
  (render [this]
    (let [{:keys [q]} (om/props this)]
      (when q (js/alert q))
      (dom/section #js {:id "main"}
                   (dom/textarea #js {:id "query"})
                   (dom/input #js {:type    "button"
                                   :value   "Go"
                                   ;; FIXME work out how to update qureies
                                   :onClick (fn [_]
                                              (om/set-query! this '[{:q {:query '[:find [(pull ?e [*]) ...] :where [?e :attribute/name]]}}]))})))))

(def reconciler
  (let [xhr-post-fn (fn [{:keys [remote]} cb]
                      (.send XhrIo ""
                             (fn [_] (this-as this (cb (transit/read (transit/reader :json) (.getResponseText this)))))
                             "POST" (transit/write (transit/writer :json) remote)
                             #js {"Content-Type" "application/transit+json"}))]
    (om/reconciler {:state     (atom {})
                    :normalize true
                    :parser    (om/parser {:read parser/read :mutate parser/mutate})
                    :send      xhr-post-fn})))

(om/add-root! reconciler Root (or (.getElementById js/document "root") (let [root (.createElement js/document "div")]
                                                                        (.setAttribute root "id" "root")
                                                                        (.appendChild js/document.body root))))