(defproject group-buy "0.1.0"
  :dependencies [
    [org.clojure/clojure "1.11.1"]
    [ring/ring-core "1.9.6"]
    [compojure "1.7.0"]
    [next.jdbc "1.3.894"]
    [buddy/buddy-auth "3.0.323"]
    [rum "0.12.11"]
    [cljs-ajax "0.8.4"]]

  :plugins [[lein-cljsbuild "1.1.8"]]

  :cljsbuild {:builds
              [{:source-paths ["frontend/src"]
                :compiler {:output-to "resources/public/js/main.js"
                           :optimizations :advanced}}]}
   :profiles {:test {:dependencies [[com.h2database/h2 "2.2.224"]
                                  [ring/ring-mock "0.4.0"]
                                  [cheshire "5.11.0"]]}
             :dev {:plugins [[com.jakemccrary/lein-test-refresh "0.25.0"]]}})