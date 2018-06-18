(defproject address-book "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.5.0"

  :ring {:handler address-book.core.handler/app
         :init    address-book.core.handler/init}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure             "1.6.1"]
                 [ring/ring-defaults    "0.3.2"]
                 [hiccup                "1.0.5"]
                 [environ               "1.1.0"]
                 [clj-wiite "0.1.0"]]

  :plugins        [[lein-ring             "0.12.4"]
                   [lein-environ          "1.1.0"]]

  :profiles {:test-local {:dependencies [[midje "1.9.1"]
                                         [javax.servlet/servlet-api "2.5"]
                                         [ring/ring-mock "0.3.2"]]

                          :plugins     [[lein-midje "3.2.1"]]}

             ;; Set these in ./profiles.clj
             :test-env-vars {}
             :dev-env-vars  {}

             :test [:test-local :test-env-vars]
             :dev  [:dev-env-vars]

             :production {:ring {:open-browser? false
                                 :stacktraces?  false
                                 :auto-reload?  false}}})
