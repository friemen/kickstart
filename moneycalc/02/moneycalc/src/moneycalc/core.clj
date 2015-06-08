(ns moneycalc.core
  (:require [org.httpkit.server :as server]
            [org.httpkit.client :as client]
            [clojure.string :as string]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))


;; tinker with data from Yahoo Finance web API

(def response @(client/get "http://download.finance.yahoo.com/d/quotes.csv?s=USDEUR=X&f=price"))
(def body (slurp (:body response)))
(def rate (first (string/split body #",")))
rate
(read-string rate)

(def url "http://download.finance.yahoo.com/d/quotes.csv?s=%s%s=X&f=price")

(format url "EUR" "USD")



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
