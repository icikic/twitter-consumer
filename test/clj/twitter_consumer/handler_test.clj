(ns twitter-consumer.handler-test
  (:require [clojure.core :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [twitter-consumer.handler :refer :all]))

(deftest test-user-tweets
  (testing "/tweets/:user"
    (with-redefs [do-async (fn [_] (println ">!! async"))]
      (let [response (app (mock/request :get "/tweets/LFC?count=1"))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8"))))))

(deftest test-search-tweets
  (testing "/tweets?q="
    (with-redefs [do-async (fn [_] (println ">!! async"))]
      (let [response (app (mock/request :get "/tweets?q=LFC&count=1"))]
        (is (= (:status response) 200))
        (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8"))))))

(deftest test-app
  (testing "Not found"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 404)))))