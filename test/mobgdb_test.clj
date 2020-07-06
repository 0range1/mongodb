(ns mobgdb_test
  (:require [clojure.test :refer :all]
            [mongodb-new.handler :refer :all]
            [cheshire.core :as cheshire]
            [ring.mock.request :as mock]
            [mongodb-new.mongodb-server.user-server :as us]))

(defn db_init
  "init  mongo db"
  [test-fn]
  (test-fn))

(testing "user register route"
  (let [response (app (-> (mock/request :post "/api/register")
                          (mock/json-body {:userid "test_user2"
                                           :password "test2_password"})))
        body (cheshire/parse-string (slurp (:body response)) true)]
    (is (= (:status response) 200))
    (is (= body {:isSuccess true
                 :errcode 0
                 :errmsg ""
                 :result "register succeed"})))

  (testing "user already register route"
    (let [response (app (-> (mock/request :post "/api/register")
                            (mock/json-body {:userid "test_user"
                                             :password "1000"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= body {:isSuccess false
                   :errcode 40002
                   :errmsg "userid has already been registered!"
                   :result {}}))))

  (testing "password is wrong"
    (let [response (app (-> (mock/request :post "/api/login")
                            (mock/json-body {:userid "001"
                                             :password "000"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= body {:isSuccess false
                   :errcode 40004
                   :errmsg "wrong password!"
                   :result {}}))))

  (testing "add an order"
    (let [response (app (-> (mock/request :post "/api/order")
                            (mock/json-body {:userid "006"
                                             :product_id "a"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= body {:isSuccess true
                   :errcode 0
                   :errmsg ""
                   :result "succeed"}))))

  )

(use-fixtures :once db_init)
(run-tests)