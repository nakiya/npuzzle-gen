(ns npuzzle.db
  (:require [npuzzle.puzzle :as pzl]))

(def default-db
   {:name "n-puzzle"
    :puzzle-sizes [3 4 5 6]
    :puzzle (pzl/shuffle-puzzle (pzl/make-puzzle 3))
    :size 3})
