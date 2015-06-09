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

(def addresses (atom {1 {:id 1 :name "Mini" :street "Foobar"}
                      2 {:id 2 :name "Donald" :street "Barbaz"}}))


;;-------------------------------------------------------------------
;; rendering utilities

(defn column
  "Returns a column description."
  [kw title]
  {:kw kw :title title})


(defn action-link
  [name action]
  (f/form-to [:post (str "/actions/" action)]
             [:input {:type "submit" :value name}]))


(defn render-row-actions
  [x]
  [:td {:width "200"}
   (action-link "Edit" (str "edit-address?id=" (:id x)))
   (action-link "Delete" (str "delete-address?id=" (:id x)))])


(defn render-row
  "Returns tr vector."
  [column-descs render-actions-fn x]
  [:tr (->> column-descs
            (map :kw)
            (map (fn [kw]
                   [:td (get x kw)])))
   (render-actions-fn x)])


(defn render-table
  "Return table vector."
  [column-descs render-actions-fn xs]
  [:table
   [:tr (->> column-descs
             (map :title)
             (map (fn [column-name]
                    [:th column-name])))
    [:th "Actions"]]
   (->> xs
        (map (partial render-row column-descs render-actions-fn)))])


(defn button
  [name]
  [:input {:type "submit" :value name}])


(defn text
  [label name]
  (seq [[:label label] [:input {:name name}]]))


;;-------------------------------------------------------------------
;; concrete views

(defn render-address-details-form
  []
  (f/form-to [:post "/actions/add-address"]
             [:div
              (text "Name" "name")[:br]
              (text "Street" "street")[:br]
              (button "Add")]))


(defn addresses-view
  [params]
  (seq [(render-address-details-form)
        (render-table [(column :name "Fullname")
                       (column :street "Street")]
                      render-row-actions
                      (->> @addresses
                           vals
                           (sort-by :street)))]))

(defn error-view
  [params]
  (seq [[:p "Sorry, an error occurred."]
        [:p (:message params)]]))



;;-------------------------------------------------------------------
;; actions

(defn parse-int [s]
  (Integer/parseInt (re-find #"\A-?\d+" s)))


(defn add-address
  [params]
  (swap! addresses
         (fn [as]
           (let [id (->> as keys (reduce max) (inc))]
             (assoc as id (-> params
                              (dissoc :action)
                              (assoc :id id))))))
  {:uri "/pages/addresses"})


(defn delete-address
  [params]
  (swap! addresses dissoc (-> params :id parse-int))
  {:uri "/pages/addresses"})



;;-------------------------------------------------------------------
;; generic action processing / view rendering

(defn process-action
  [actions-map action-name params]
  (println "Processing" action-name "with" params)
  (let [f      (some-> actions-map (get action-name) :action-fn deref)]
    (if f
      (let [{:keys [uri params]} (f params)]
        (redirect uri))
      (redirect (str "/pages/error?message=Action%20" action-name "%20unknown.")))))


(defn render-view
  [view-map view-name request]
  (let [f (some-> view-map (get view-name) :content-fn deref)]
    {:status 200
     :body (html5
            [:head
             [:link {:rel "stylesheet" :type "text/css" :href "/static/stylesheet.css"}]]
            [:body (if f
                     (f (:params request))
                     (str "Unable to find page " view-name))])}))


;;-------------------------------------------------------------------
;; actions and view registration

(def actions-map
  {"add-address" {:action-fn #'add-address}
   "delete-address" {:action-fn #'delete-address}})


(def view-map
  {"addresses" {:title "Addresses"
                :content-fn #'addresses-view}
   "error"     {:title "Internal error"
                :content-fn #'error-view}})


;;-------------------------------------------------------------------
;; routing

(defroutes app
  (POST "/actions/:action" [action :as r]
        (process-action actions-map action (:params r)))
  (GET "/pages/:view" [view :as r]
       (render-view view-map view r))
  (GET "/" [] (redirect "/pages/addresses"))
  (route/resources "/static")
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

