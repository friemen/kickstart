(ns moneycalc.core
  (:require [org.httpkit.server :as server]
            [org.httpkit.client :as client]
            [clojure.string :as string]
            [hiccup.core :as h]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))


;; Access currency exchange rate with Yahoo Finance web API

(def url "http://download.finance.yahoo.com/d/quotes.csv?s=%s%s=X&f=price")

(defn query-money-exchange-rate
  "Returns a floating point number that represents
  the conversion rate between two currencies.
  Typical symbols are EUR, USD, CAD, NZD, JPY, AUD, NOK.
  Returns nil if no rate can be found."
  [to-symbol from-symbol]
  (let [url       (format url to-symbol from-symbol)
        response @(client/get url)
        body      (slurp (:body response))
        rate      (read-string (first (string/split body #",")))]
    (if (number? rate)
      rate)))

;; uncomment to test it
#_(query-money-exchange-rate "EUR" "USD")


(defn render-rate
  [to-symbol from-symbol]
  [:tr
   [:td from-symbol]
   [:td to-symbol]
   [:td (query-money-exchange-rate to-symbol from-symbol)]])


(defn handler
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (h/html [:body
                  [:h1 "Hello Hiccup World"]
                  [:table
                   [:thead [:tr [:th "From"] [:th "To"] [:th "Rate"]]]
                   [:body (for [t ["EUR" "USD"] f ["JPY" "AUD"]]
                                   (render-rate t f))]]])})



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
