(ns mongodb-new.mongodb-server.user-server
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [ring.util.codec :as encoder]
            [clojure.java.io :as cio]
            [clojure.edn :as edn]
            [mongodb-new.util.readedn :as read-edn]
            [monger.core :as mg]
            [monger.collection :as mc]
            [mongodb-new.util.response-util :as res]
            [mongodb-new.util.date-util :as date_util]
            )
  (:import [com.mongodb MongoOptions ServerAddress]
           (org.bson.types ObjectId)))

(def config (read-edn/load-edn-config "mongodb_new/conf/mongodb.edn"))

(def conn (let [^MongoOptions opts (mg/mongo-options (:option config))
                ^ServerAddress sa (mg/server-address (:host config) (:port config))]
            (mg/connect sa opts)))

(def db-name (mg/get-db conn (:db-name config)))
(def cu (:coll-user config))
(def cp (:coll-product config))
(def co (:coll-order config))

(defn get-one-userid
  [userid]
  (mc/find-one db-name cu {:userid userid}))

(defn get-one-userid-map
  [userid]
  (mc/find-one-as-map db-name cu {:userid userid}))

(defn get-one-product_id-map
  [product_id]
  (mc/find-one-as-map db-name cp {:product_id product_id}))

(defn add-user
  [userinfo]
  (mc/insert db-name cu userinfo))

(defn add-order
  [userid product_id]
  (let [orderinfo (-> {:_id (ObjectId.)
                       :userid userid
                       :product_id product_id}
                      date_util/createdAt
                      date_util/updatedAt )]
   (mc/insert db-name co orderinfo)))

(defn register
  [userinfo]

    (let [{:keys [userid]} userinfo
        exist (get-one-userid userid)]
    (cond (nil? userid) (res/failResponse 40001"userid is empty!")
          (some? exist) (res/failResponse 40002"userid has already been registered!")
          :else (do (add-user userinfo)
                    (res/succResponse "registered successfully!")))
  ))

(defn login
  [userinfo]

  (let [{:keys [userid userpassword]} userinfo
        exist (get-one-userid-map userid)]
    (cond (nil? userid) (res/failResponse 40001 "userid is empty!")
          (nil? exist) (res/failResponse 40003 "invalid userid!")
          :else (let [{:keys [password]} exist]
                  (if (= password userpassword)
                   (res/succResponse  exist)
                   (res/failResponse 40004 "wrong password!")))
                 )
    ))

(defn order
  [orderinfo]
  (let [{:keys [userid product_id]} orderinfo
        user-exist (get-one-userid-map userid)
        product-exist (get-one-product_id-map product_id)
        is-valid (and user-exist product-exist)]
    (cond (not is-valid) (res/failResponse 40011 "order request is invalid!")
          (nil? (add-order userid product_id)) (res/failResponse 50001 "create an order unsuccessfully!")
          :else (res/succResponse  {:status "succeed"}))))
