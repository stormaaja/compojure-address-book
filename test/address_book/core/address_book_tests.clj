(ns address-book.core.address-book-tests
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [address-book.core.handler :refer :all]
            [clj-wiite.core :as w]
            [environ.core :refer [env]]))

(facts "Example GET and POST tests"
  (with-state-changes [(before :facts (reset! (w/watom (env :db-connection)) []))
                       (after  :facts (reset! (w/watom (env :db-connection)) []))]

  (fact "Test GET request to / route returns expected contacts"
    (let [watom (w/watom (env :db-connection))]
      (reset! watom [{:id 1 :name "JT" :phone "(321)" :email "JT@JT.com"}
                     {:id 2 :name "Utah" :phone "(432)" :email "J@Buckeyes.com"}]))
    (let [response (app (mock/request :get "/"))]
      (:status response) => 200
      (:body response) => (contains "<div class=\"column-1\">JT</div>")
      (:body response) => (contains "<div class=\"column-1\">Utah</div>")))

  (fact "Test POST to /post creates a new contact"
    (count (deref (w/watom (env :db-connection)))) => 0
    (let [response (app (mock/request :post "/post" {:name "Some Guy" :phone "(123)" :email "a@a.cim"}))]
      (:status response) => 302
      (count (deref (w/watom (env :db-connection)))) => 1))

  (fact "Test UPDATE a post request to /edit/<contact-id> updates desired contact information"
    (let [watom (w/watom (env :db-connection))]
      (reset! watom [{:id 1 :name "JT" :phone "(321)" :email "JT@JT.com"}]))
    (let [response (app (mock/request :post "/edit/1" {:id "1" :name "Jrock" :phone "(999) 888-7777" :email "jrock@test.com"}))]
      (:status response) => 302
      (let [watom (w/watom (env :db-connection))]
        (count @watom) => 1
        (first @watom) => {:id 1 :name "Jrock" :phone "(999) 888-7777" :email "jrock@test.com"})))

    (fact "Test DELETED a post to /delete/<contact-id> deletes desired contact from database"
      (let [watom (w/watom (env :db-connection))]
        (reset! watom [{:id 1 :name "JT" :phone "(321)" :email "JT@JT.com"}])
        (count @watom) => 1)
      (let [response (app (mock/request :post "/delete/1" {:id 1}))]
        (count (deref (w/watom (env :db-connection)))) => 0))))
