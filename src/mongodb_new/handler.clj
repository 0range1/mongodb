(ns mongodb-new.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [mongodb-new.mongodb-server.user-server :as user]
            [ring.adapter.jetty :as jetty]))

(s/defschema Pizza
  {:name s/Str
   (s/optional-key :description) s/Str
   :size (s/enum :L :M :S)
   :origin {:country (s/enum :FI :PO)
            :city s/Str}})

(s/defschema USERINFO
  {:userid s/Str
   (s/optional-key :sex) s/Str
   (s/optional-key :country) s/Str
   :password s/Str})

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Mongodb-new"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]}}}

    (context "/api" []
      :tags ["api"]

      (POST "/register" []
        :summary "register"
        :body [userinfo USERINFO]
        (println "userid is " (:userid userinfo))
        (ok (user/register userinfo)))


      (POST "/login" []
        :body [userinfo {:userid s/Str
                         :userpassword s/Str}]
        :summary "login"
        (let [response (ok (user/login userinfo))]
          (if (:isSuccess response) (assoc-in (ok response) [:session :iderntity] (:userid userinfo))
                                    (ok response))))

      (POST "/logout" []
        :summary "logout"
        :body [userid {:userid s/Str}]
        (assoc-in (ok) [:session :identity] nil))

      (POST "/order" []
        :body [orderinfo {:userid s/Str
                           :product_id s/Str}]
        :summary "add an order"
        (ok (user/order orderinfo))
        )

      )))

(defn -main [& args]
  (jetty/run-jetty app {:port 3000}))