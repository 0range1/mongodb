 (defproject mongodb-new "0.1.0-SNAPSHOT"
   :description "FIXME: write description"
   :dependencies [[org.clojure/clojure "1.10.0"]
                  [metosin/compojure-api "2.0.0-alpha30"]
                  [com.novemberain/monger "3.1.0"]
                  [ring "1.4.0"]
                  [ring/ring-mock "0.3.2"]
                  [ring/ring-jetty-adapter "1.7.1"]
                  [ring/ring-devel "1.7.1"]
                  [ring/ring-defaults "0.3.2"]
                  [ring/ring-codec "1.1.2"]
                  [clj-http "3.10.0"]
                  ]
   :plugins [[lein-cloverage "1.1.2"]]
   :ring {:handler mongodb-new.handler/app}
   :uberjar-name "server.jar"
   :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                   :plugins [[lein-ring "0.12.5"]]}})
