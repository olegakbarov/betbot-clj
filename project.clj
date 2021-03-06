(defproject betbot "0.1.0-SNAPSHOT"
  :description "Cluster of microservices orchestrated by Kubernetis based on Paxos"
  :url "http://betbot.io/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.0"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170" :scope "provided"]
                 [org.clojure/core.async "0.2.374"]
                 [com.taoensso/timbre "4.1.4"]              ; Clojure/Script logging
                 [org.clojure/core.match "0.3.0-alpha4"]    ; Pattern matching

                 [ring "1.4.0"]
                 [ring-server "0.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-json "0.4.0"]

                 [compojure "1.4.0"]                        ; backend routes
                 [prone "0.8.2"]                            ; exceptions middleware
                 [hiccup "1.0.5"]                           ; html generation
                 [environ "1.0.1"]                          ; config from env
                 [cheshire "5.5.0"]                         ; json parsing/generation
                 [clj-http "2.0.0"]                         ; http client for backend
                 [clj-time "0.11.0"]                        ; Time manipulation

                 ;; Database
                 [korma "0.4.2"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]

                 ;; Misc
                 [im.chit/cronj "1.4.4"]

                 ;; Frontend
                 [reagent "0.5.1"]                          ; React rendering wrapper
                 [re-frame "0.6.0"]                         ; data-flow library
                 [bidi "1.20.3"]                            ; frontend routing
                 [kibu/pushy "0.3.2"]                       ; HTML5 history
                 [cljs-http "0.1.39"]                       ; http library
                 ]

  :plugins [[lein-environ "1.0.1"]
            [lein-cljsbuild "1.1.1"]
            [lein-asset-minifier "0.2.2"
             :exclusions [org.clojure/clojure]]]

  :main betbot.server
  :uberjar-name "betbot.jar"
  :ring {:handler betbot.handler/app
         :init betbot.telegram.polling/start!
         :destroy betbot.telegram.polling/stop!}

  :clean-targets ^{:protect false} [:target-path
                                   [:cljsbuild :builds :app :compiler :output-dir]
                                   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets  {:assets
                   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs" "src/cljc"]
                             :compiler {:output-to "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :asset-path   "js/out"
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :less {:source-paths ["src/less"]
         :target-path "resources/public/css"}

  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.0"]
                                  [ring/ring-devel "1.4.0"]
                                  [lein-figwheel "0.5.0-2"]
                                  [org.clojure/clojurescript "1.7.170"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [pjstadig/humane-test-output "0.7.0"]]

                   :plugins [[lein-figwheel "0.5.0-2"]
                             [lein-ring "0.9.7"]
                             [org.clojure/clojurescript "1.7.170"]
                             [lein-less "1.7.5"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :cljsbuild {:builds {:app {:figwheel {:on-jsload "betbot.core/mount-root"}
                                              :compiler {:main "betbot.core"
                                                         :source-map true
                                                         :source-map-timestamp true}}}}

                   :figwheel {:css-dirs ["resources/public/css"]
                              :ring-handler betbot.handler/app}

                   :env {:dev true
                         :host "http://localhost:3000"
                         :database-url "postgres://betbot:yolo@localhost:5432/betbot"
                         :telegram-token "159316364:AAFbU5ibzv0OH3qzhcNx0CjGRcxQsznpC0s"}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :prep-tasks [["less" "once"]
                                    "compile"
                                    ["cljsbuild" "once"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true
                       :cljsbuild {:jar true
                                   :builds {:app
                                            {:compiler {:main "betbot.core"
                                                        :optimizations :advanced
                                                        :closure-defines {goog.DEBUG false}
                                                        :pretty-print false}}}}}})
