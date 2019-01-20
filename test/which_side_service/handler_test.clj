(ns which-side-service.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [which-side-service.handler :refer :all]))

(deftest test-app
  (testing "main route"
   ; determines the actual current date so either response is possible.
    (let [response (app (mock/request :get "/even"))]
      (is (= (:status response) 200))
      (is (some #{(:body response)} '("in front of house." "across the street")))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

  (testing "gives correct side of street"
    (is (= (which-side false 3) "in front of house."))
    (is (= (which-side false 4) "across the street."))
    (is (= (which-side true 4) "in front of house."))
    (is (= (which-side true 3) "across the street."))))
