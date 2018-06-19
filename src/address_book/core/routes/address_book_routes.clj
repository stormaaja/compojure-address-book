(ns address-book.core.routes.address-book-routes
  (:require [ring.util.response :as response]
            [compojure.core :refer :all]
            [address-book.core.views.address-book-layout :refer [common-layout
                                                                 read-contact
                                                                 add-contact-form
                                                                 edit-contact]]
            [environ.core :refer [env]]
            [clj-wiite.core :as w]))

(defn create-watom []
  (let [state (w/watom (env :db-connection))]
    (when (nil? @state)
      (reset! state []))
    state))

(defonce contacts (create-watom))

(defn display-contact [contact contact-id]
  (if (not= (and contact-id (Integer. contact-id)) (:id contact))
    (read-contact contact)
    (edit-contact contact)))

(defn post-route [request]
  (let [id (inc (get (first (sort-by :id @contacts)) :id 0))
        name  (get-in request [:params :name])
        phone (get-in request [:params :phone])
        email (get-in request [:params :email])]
    (swap! contacts conj
           {:id id :name name :phone phone :email email})
    (response/redirect "/")))

(defn get-route [request]
  (let [contact-id (get-in request [:params :contact-id])]
    (common-layout
      (for [contact @contacts]
        (display-contact contact contact-id))
      (add-contact-form))))

(defn delete-route [request]
  (let [contact-id (Integer/parseInt (get-in request [:params :contact-id]))]
    (reset! contacts (filterv #(not= (:id %) contact-id) @contacts))
    (response/redirect "/")))

(defn find-index-of [f coll]
  (when-let [item (first
                    (filter #(f (second %)) (map-indexed vector coll)))]
    (first item)))

(defn update-route [request]
  (let [contact-id (Integer/parseInt (get-in request [:params :id]))
        name       (get-in request [:params :name])
        phone      (get-in request [:params :phone])
        email      (get-in request [:params :email])]
    (let [index (find-index-of #(= (:id %) contact-id) @contacts)]
      (swap! contacts assoc index
             {:id contact-id
              :name name
              :phone phone
              :email email}))
    (response/redirect "/")))

(defroutes address-book-routes
  (GET  "/"                   [] get-route)
  (POST "/post"               [] post-route)
  (GET  "/edit/:contact-id"   [] get-route)
  (POST "/edit/:contact-id"   [] update-route)
  (POST "/delete/:contact-id" [] delete-route))
