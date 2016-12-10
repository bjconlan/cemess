(ns cemess.client.core)

(defn ^:export bootstrap []
  (.appendChild js/document.body (.createTextNode js/document "text element!")))

(bootstrap)