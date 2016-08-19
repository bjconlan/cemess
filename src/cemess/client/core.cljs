(ns cemess.client.core)

(defn ^:export bootstrap []
  (.replaceChild js/document "text element"))