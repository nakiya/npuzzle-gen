(ns npuzzle.puzzle-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [npuzzle.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
