(ns helloweb.core
  (:require [org.httpkit.server :as httpkit]
            [compojure.handler :as handler]
            [ring.util.response :refer [redirect response]]
            [ring.util.codec :refer [url-encode]]
            [hiccup.page :refer [html5]]
            [hiccup.form :as f]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]))


;;-------------------------------------------------------------------
;; Concepts

;; A Template-Function is a two-arg function
;;   [View hiccup-vector -> hiccup-vector] 
;;
;; A Render-Function is a two-arg function
;;   [params-map state-map -> hiccup-vector]
;;
;; An Action-Function is a two-arg function
;;   [params-map state-map -> Action-Result]
;;
;; An Action-Result is a map
;;   :view-id      Id of a view
;;   :params       Params to be added to URI used for redirection
;;   :state        A map containing state to be attached to the session
;;
;; A View is a map
;;  :content-fn    A var pointing to a Render-Function
;;  :template-fn   A var pointing to a Template-Function
;;  :title         The title to be used in head section
;;
;; An Action is a map
;;  :action-fn     A var pointing to a Action-Function
;;  


;;-------------------------------------------------------------------
;; Utils

(defn parse-int
  "Returns an integer from String s. Throws NumberFormatException if s
  cannot be parsed."
  [s]
  (Integer/parseInt (re-find #"\A-?\d+" s)))


(defn into-component-values
  "Takes all values from params and assocs them as :value entry in the
  corresponding components state map."
  [state params]
  (reduce (fn [state [k v]]
            (assoc-in state [k :value] v))
          state
          params))


(defn with-params
  "Appends a URL parameter string derived from m to path."
  [path m]
  (if (seq m)
    (->> m
         (map (fn [[k v]]
                (str (-> k name url-encode) "=" (-> v str url-encode))))
         (clojure.string/join "&")
         (str path "?"))
    path))


(defn without
  "Keeps all key value pairs from m expect those whose key is in ks."
  [m & ks]
  (reduce dissoc m ks))


(defn only
  "Keeps only key value pairs from m for keys listed in ks."
  [m & ks]
  (->> m
       (filter (comp (set ks) first))
       (into {})))


;;-------------------------------------------------------------------
;; default data

(def addresses (atom {1 {:id 1 :name "Mini" :street "Foobar"}
                      2 {:id 2 :name "Donald" :street "Barbaz"}}))


;;-------------------------------------------------------------------
;; component rendering functions

(defn column
  "Returns a column description map."
  [kw title]
  {:kw kw :title title})


(defn action-link
  "Returns hiccup data for an action link."
  [name action params state]
  (f/form-to [:post (with-params (str "/actions/" action) params)]
             [:input {:type "submit" :value name}]))


(defn render-row-actions
  "Returns a hiccup td vector containing action links for item x."
  [x]
  [:td
   (action-link "Edit" "edit-address" (only x :id) nil)
   (action-link "Delete" "delete-address" (only x :id) nil)])


(defn render-row
  "Returns a hiccup tr vector."
  [column-descs render-actions-fns x]
  [:tr (->> column-descs
            (map :kw)
            (map (fn [kw]
                   [:td (get x kw)])))
   (render-row-actions x)])


(defn render-table
  "Return a hiccup table vector."
  [column-descs render-actions-fn {:keys [items]}]
  (if (seq items)
    [:table
     [:tr (->> column-descs
               (map :title)
               (map (fn [column-name]
                      [:th column-name])))
      [:th "Actions"]]
     (->> items
          (map (partial render-row column-descs render-actions-fn)))]
    [:p "No items to display."]))


(defn button
  "Returns a hiccup submit input vector."
  [name state]
  [:input {:type "submit" :value (or (:name state) name)}])


(defn text
  "Returns a seq of hiccup vectors for label and text input."
  [name label {:keys [value message]}]
  (seq [[:label label] [:input {:name name :value value}] message]))


(defn hidden
  "Returns a hiccup hidden input vector."
  [name {:keys [value]}]
  [:input {:type "hidden" :name name :value value}])


;;-------------------------------------------------------------------
;; redirection


(defn action-result
  ([view-id params state]
     {:view-id view-id
      :state state
      :params (if state
                (assoc params :keep 1)
                params)})
  ([view-id params]
     (action-result view-id params nil))
  ([view-id]
     (action-result view-id {} nil)))


(defn error-redirect
  "Returns a redirection response map with message attached to the
  session state."
  [message]
  (-> (redirect (with-params "/pages/error" {:keep 1}))
      (assoc-in [:session :state :error]
                message)))


(defn action-redirect
  "Returns a redirection response map with the given state attached to
  the session."
  [{:keys [view-id params state] :as action-result}]
  (-> (redirect (with-params (str "/pages/" view-id) params))
      (assoc-in [:session :state]
                state)))

;;-------------------------------------------------------------------
;; concrete views

(defn render-address-details-form
  [state]
  (f/form-to [:post "/actions/add-address"]
             [:div
              (hidden "id" (-> state :id))
              (text "name" "Name" (-> state :name))[:br]
              (text "street" "Street" (-> state :street))[:br]
              (button "Add" (-> state :add))]))


(defn addresses-view
  [params state]
  (seq [(render-address-details-form (if-let [id (some-> params :edit parse-int)]
                                       (into-component-values state (get @addresses id))
                                       state))
        (render-table [(column :id "No")
                       (column :name "Fullname")
                       (column :street "Street")]
                      render-row-actions
                      {:items (->> @addresses
                                   vals
                                   (sort-by :name))})]))

(defn address-view
  [params state]
  (let [id (parse-int (:id params))]
    [:p (-> @addresses (get id) :name)]))


(defn error-view
  [params state]
  (seq [[:p "Sorry, an error occurred."]
        [:p (-> state :error)]]))


(defn default-template
  [view contents]
  (seq [[:head
         [:link {:rel "stylesheet" :type "text/css" :href "/static/stylesheet.css"}]]
        [:title (:title view)]
        [:body contents]]))


;;-------------------------------------------------------------------
;; concrete actions

(defn add-address
  [params state]
  (if (< (count (:name params)) 1)
    (action-result "addresses"
                   {}
                   (assoc-in state [:name :message] "Invalid"))
    (do
      (swap! addresses
             (fn [as]
               (let [id (if (-> params :id empty?)
                          ;; calculate new id
                          (if (seq as) 
                            (->> as keys (reduce max) (inc))
                            1)
                          ;; or take existing
                          (-> params :id parse-int))]
                 (assoc as id (-> params
                                  (dissoc :action)
                                  (assoc :id id))))))
      (action-result "addresses"))))


(defn edit-address
  [params state]
  (action-result "addresses"
                 {:edit (-> params :id parse-int)}
                 (assoc-in state [:add :name] "Update")))


(defn delete-address
  [params state]
  (swap! addresses dissoc (-> params :id parse-int))
  (action-result "addresses"))


;;-------------------------------------------------------------------
;; actions and view registration

(def action-map
  {"add-address"      {:action-fn #'add-address}
   "delete-address"   {:action-fn #'delete-address}
   "edit-address"     {:action-fn #'edit-address}})


(def view-map
  {"addresses" {:title "Addresses"
                :content-fn #'addresses-view
                :template-fn #'default-template}
   "address"   {:title "Address"
                :content-fn #'address-view
                :template-fn #'default-template}
   "error"     {:title "Internal error"
                :content-fn #'error-view
                :template-fn #'default-template}})



;;-------------------------------------------------------------------
;; generic action processing / view rendering


(defn process-action
  [action-map action-id {:keys [params session]}]
  (println "Processing" action-id "with" params)
  (let [f (some-> action-map (get action-id) :action-fn deref)]
    (if f
      (try 
        (let [action-result (f params (into-component-values
                                              (-> session :state)
                                              params))]
          (action-redirect action-result))
        (catch Exception ex
          (do (.printStackTrace ex)
              (error-redirect (.getMessage ex)))))
      (do (println "Action" action-id "unknown")
          (error-redirect (str "Action " action-id " unknown."))))))


(defn render-view
  [view-map view-id {:keys [params session]}]
  (println "Rendering" view-id)
  (let [view                  (get view-map view-id)
        {:keys [content-fn
                title
                template-fn]} view
        content-fn            (and content-fn @content-fn)
        template-fn           (or (and template-fn @template-fn) default-template)]
    {:status 200
     :body (html5 (if content-fn
                    (template-fn view (content-fn params (if (:keep params) (-> session :state))))
                    (str "Unable to find page " view-id)))}))


;;-------------------------------------------------------------------
;; routing

(defroutes app
  (POST "/actions/:action-id" [action-id :as r]
        (process-action action-map action-id r))
  (GET "/pages/:view-id" [view-id :as r]
       (render-view view-map view-id r))
  (GET "/" []
       (redirect "/pages/addresses"))
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

