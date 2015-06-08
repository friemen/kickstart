(ns moneycalc.core
  (:require [org.httpkit.server :as server]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))



(defn handler
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<html><body><h1>Hello ClojureBridge World</h1></body></html>"})






(def app (wrap-defaults #'handler site-defaults))


;; -------------------------------------------------------------------
;; http server start/stop infrastructure

(defonce http-server (atom nil))

(defn stop!
  "Stops the http server if started."
  []
  (when-let [shutdown-fn @http-server]
    (shutdown-fn)
    (reset! http-server nil)
    :stopped))


(defn start!
  "Starts http server, which is reachable on http://localhost:8080"
  []
  (stop!)
  (reset! http-server (server/run-server #'app {:port 8080}))
  :started)


(stop!)
(start!)
