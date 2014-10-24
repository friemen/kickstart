(ns helloweb.core
  (:require [org.httpkit.server :as httpkit]
            [compojure.handler :as handler]
            [ring.util.response :refer [redirect response]]
            [hiccup.page :refer [html5]]
            [hiccup.form :as f]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]))

;;-------------------------------------------------------------------
;; default data

(def addresses (atom {1 {:name "Mini" :street "Foobar"}
                      2 {:name "Donald" :street "Barbaz"}}))

;;-------------------------------------------------------------------
;; rendering utilities

(defn column
  "Returns a column description."
  [kw title]
  {:kw kw :title title})


(defn render-row
  "Returns tr vector."
  [column-descs x]
  [:tr (->> column-descs
            (map :kw)
            (map (fn [kw]
                   [:td (get x kw)])))])


(defn render-table
  "Return table vector."
  [column-descs xs]
  [:table
   [:tr (->> column-descs
             (map :title)
             (map (fn [column-name]
                    [:th column-name])))]
   (->> xs
        (map (partial render-row column-descs)))])


;;-------------------------------------------------------------------
;; central handler that only renders data

(defn handler
  [request]
  {:status 200
   :body (html5
          [:head
           [:link {:rel "stylesheet" :type "text/css" :href "public/stylesheet.css"}]]
          [:body (render-table [(column :name "Fullname")
                                (column :street "Street")]
                               (->> @addresses
                                    vals
                                    (sort-by :street)))])})

;;-------------------------------------------------------------------
;; routing

(defroutes app
  (GET "/" [] handler)
  (route/resources "/public")
  (route/not-found "Not found"))

  
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
  (reset! http-server (httpkit/run-server (handler/site #'app) {:port 8080}))
  :started) 

