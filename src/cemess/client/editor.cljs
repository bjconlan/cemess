(ns cemess.client.editor
  (:require [cljsjs.inferno]
            [datascript.core :as d]
            [goog.dom :as dom]))

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

(defn createVNode [{:keys [:element/tag-name :element/id :element/attributes :node/child-nodes :node/value] :as node}]
  (if tag-name
    (js/Inferno.createVNode 2
                            tag-name
                            (clj->js (reduce (fn [r {:keys [:attribute/name :attribute/value]}] (assoc r name value)) {:id id} attributes))
                            (clj->js (cond->> value child-nodes (conj (mapv createVNode child-nodes)))))
    (js/Inferno.createVNode 1 nil nil (or value node))))

(defn init []
  (let [{:keys [conn]} cemess.client.core/system
        x  (d/entity (d/db conn) [:element/id "test"])
        el (or (.getElementById js/document "root")
               (let [el (dom/createDom "div" (clj->js {"id" "root"}))]
                 (dom/append js/document.body el) el))]
    (js/Inferno.render
      (createVNode x)
      #_(js/Inferno.createVNode 2 (name :div)
                              #js {:className "test"}
                              #js ["Hello World!" (js/Inferno.createVNode 2 (name :div) #js {:className "test"} "Hello World!")]) el)))