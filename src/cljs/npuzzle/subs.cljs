(ns npuzzle.subs
  (:require
   [re-frame.core :as re-frame]
   [npuzzle.puzzle :as pzl]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::puzzle-size
 (fn [db]
   (pzl/get-size (:puzzle db))))

(re-frame/reg-sub
 ::size-choices
 (fn [db]
   (:puzzle-sizes db)))

(re-frame/reg-sub
 ::puzzle
 (fn [db]
   (pzl/get-tiles (:puzzle db))))
