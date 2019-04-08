(ns npuzzle.puzzle
  (:require [cljs.test :refer-macros [deftest testing is run-tests]]
            [clojure.zip :as zip]))

(defn get-tiles [puzzle] (nth puzzle 0))
(defn get-size [puzzle] (nth puzzle 1))

(defn set-tiles [puzzle tiles] (assoc puzzle 0 tiles))

(defn make-puzzle
  ([size]
   [(conj (vec (range 1 (* size size))) :space) size])
  ([tiles size]
   [tiles size]))

(defn index->row-col [puzzle index]
  (let [size (get-size puzzle)]
    [(quot index size) (mod index size)]))

(defn row-col->index [puzzle [row col]]
  (let [size (get-size puzzle)]
    (+ (* row size) col)))

(defn get-tile [puzzle [row col]]
  ((get-tiles puzzle) (+ (* row (get-size puzzle)) col)))

(defn get-index-of-space [puzzle]
  (.indexOf (get-tiles puzzle) :space))

(defn get-space-coords [puzzle]
  (index->row-col puzzle (get-index-of-space puzzle)))

(defn is-space? [puzzle coords]
  (= :space (get-tile puzzle coords)))

(defn valid-coords? [puzzle [row col]]
  (let [size (get-size puzzle)]
    (and (>= row 0) (>= col 0)
         (< row size) (< col size))))

(defn get-adjacent-coords [puzzle [row col]]
  (->> '([0 1] [-1 0] [0 -1] [1 0])
       (map (fn [[dr dc]]
              [(+ row dr) (+ col dc)]))
       (filter #(valid-coords? puzzle %))))

(defn inversions [puzzle]
  (let [size (get-size puzzle)
        tiles (get-tiles puzzle)
        inversions-fn
        (fn [remaining-tiles found-tiles inversions]
          (if-let [i (first remaining-tiles)]
            (recur (rest remaining-tiles)
                   (conj found-tiles i)
                   (+ inversions (- (dec i) (count (filter found-tiles (range i))))))
            inversions))]
    (inversions-fn (remove #{:space} tiles) #{} 0)))

(defn is-solvable? [puzzle]
  (let [inv-cnt (inversions puzzle)
        size (get-size puzzle)
        [row col] (get-space-coords puzzle)]
    (if (odd? size)
      (even? inv-cnt)
      (if (even? (- size row))
        (odd? inv-cnt)
        (even? inv-cnt)))))

(defn can-move? [puzzle coords]
  ;;Check if any one of the adjacent tiles is the space.
  (->> (get-adjacent-coords puzzle coords)
       (filter #(is-space? puzzle %))
       (first)))

(defn swap-tiles [puzzle coord1 coord2]
  (let [index1 (row-col->index puzzle coord1)
        index2 (row-col->index puzzle coord2)
        tile1 (get-tile puzzle coord1)
        tile2 (get-tile puzzle coord2)
        tiles (get-tiles puzzle)]
    (set-tiles puzzle
               (-> tiles
                   (assoc index1 tile2)
                   (assoc index2 tile1)))))

(defn move [puzzle coords]
  (if (can-move? puzzle coords)
    (swap-tiles puzzle coords (get-space-coords puzzle))
    puzzle))

(defn random-move [puzzle]
  (let [space-coords (get-space-coords puzzle)
        movable-tiles (get-adjacent-coords puzzle space-coords)
        random-movable-tile (nth movable-tiles (rand-int (count movable-tiles)))]
    (move puzzle random-movable-tile)))

(defn shuffle-puzzle
  "Shuffles by moving a random steps"
  ([puzzle]
   (let [num-moves (+ 100 (rand 200))]
     (shuffle-puzzle puzzle num-moves)))
  ([puzzle num-moves]
   (if (> num-moves 0)
     (recur (random-move puzzle) (dec num-moves))
     puzzle)))
