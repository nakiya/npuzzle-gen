(ns npuzzle.db
  (:require [npuzzle.puzzle :as pzl]))

(def default-db
   {:name "15-puzzle"
    :puzzle-sizes [3 4 5]
    :puzzle (pzl/make-puzzle 3)
    :size 3})
