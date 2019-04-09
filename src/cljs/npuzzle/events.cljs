(ns npuzzle.events
  (:require
   [re-frame.core :as re-frame]
   [npuzzle.db :as db]
   [npuzzle.puzzle :as pzl]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::change-puzzle-size
 (fn-traced [db [_ size]]
            (let [rnd-puzzle (pzl/shuffle-puzzle (pzl/make-puzzle (int size)))]
              (-> db
                  (assoc :size size)
                  (assoc :puzzle rnd-puzzle)))))

(re-frame/reg-event-db
 ::shuffle-puzzle
 (fn-traced [db [_ _]]
            (let [size (:size db)]
              (assoc db :puzzle (pzl/shuffle-puzzle (pzl/make-puzzle size))))))

(re-frame/reg-event-db
 ::move-tile
 (fn-traced [db [_ tile-index]]
            (let [coords (pzl/index->row-col (:puzzle db) tile-index)
                  npz (pzl/move (:puzzle db) coords)]
              (assoc db :puzzle npz))))
