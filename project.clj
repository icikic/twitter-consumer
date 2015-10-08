(defproject twitter-consumer "0.1.0-SNAPSHOT"
  :description "Simple http server"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [slingshot "0.12.2"]
                 [twitter-api "0.7.8"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/clojurescript "1.7.48"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.12"]
                 [log4j/log4j "1.2.17"]
                 [cljs-http "0.1.37"]
                 [netty-ring-adapter "0.4.6"]
                 [om "0.7.3"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-pdo "0.1.1"]
            [lein-cljsbuild "1.1.0"]]
  :ring {:handler twitter-consumer.handler/app}
  ;  :aliases {"up" ["pdo" "cljsbuild" "auto" "dev," "ring" "server-headless"]}
  :aliases {"up" ["ring" "server-headless"]}
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/cljs"]
                        :compiler     {:output-to     "resources/public/js/app.js"
                                       :output-dir    "resources/public/js/out"
                                       :optimizations :none
                                       :source-map    true}}]}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}}
  :repl-options {:init-ns twitter-consumer.handler }
  ;:main twitter-consumer.handler
  )
