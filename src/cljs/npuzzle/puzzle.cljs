(ns npuzzle.puzzle
  (:require [cljs.test :refer-macros [deftest testing is run-tests]]
            [clojure.zip :as zip]))

(defn- get-tiles [puzzle] (nth puzzle 0))
(defn- get-size [puzzle] (nth puzzle 1))

(defn- set-tiles [puzzle tiles] (assoc puzzle 0 tiles))

(defn- make-puzzle
  ([size]
   [(conj (vec (range 1 (* size size))) :space) size])
  ([tiles size]
   [tiles size]))

(defn- index->row-col [puzzle index]
  (let [size (get-size puzzle)]
    [(quot index size) (mod index size)]))

(defn- row-col->index [puzzle [row col]]
  (let [size (get-size puzzle)]
    (+ (* row size) col)))

(defn- get-tile [puzzle [row col]]
  ((get-tiles puzzle) (+ (* row (get-size puzzle)) col)))

(defn- get-index-of-space [puzzle]
  (.indexOf (get-tiles puzzle) :space))

(defn- get-space-coords [puzzle]
  (index->row-col puzzle (get-index-of-space puzzle)))

(defn- is-space? [puzzle coords]
  (= :space (get-tile puzzle coords)))

(defn- valid-coords? [puzzle [row col]]
  (let [size (get-size puzzle)]
    (and (>= row 0) (>= col 0)
         (< row size) (< col size))))

(defn- get-adjacent-coords [puzzle [row col]]
  (->> '([0 1] [-1 0] [0 -1] [1 0])
       (map (fn [[dr dc]]
              [(+ row dr) (+ col dc)]))
       (filter #(valid-coords? puzzle %))))

(defn- inversions [puzzle]
  (let [size (get-size puzzle)
        tiles (get-tiles puzzle)
        inversions-fn (fn [remaining-tiles found-tiles inversions]
                        (if-let [i (first remaining-tiles)]
                          (recur (rest remaining-tiles)
                                 (conj found-tiles i)
                                 (+ inversions (- (dec i) (count (filter found-tiles (range i))))))
                          inversions))]
    (inversions-fn (remove #{:space} tiles) #{} 0)))

(defn- is-solvable? [puzzle]
  (let [inv-cnt (inversions puzzle)
        size (get-size puzzle)
        [row col] (get-space-coords puzzle)]
    (if (odd? size)
      (even? inv-cnt)
      (if (even? (- size row))
        (odd? inv-cnt)
        (even? inv-cnt)))))

(defn- can-move? [puzzle coords]
  ;;Check if any one of the adjacent tiles is the space.
  (->> (get-adjacent-coords puzzle coords)
       (filter #(is-space? puzzle %))
       (first)))

(defn- swap-tiles [puzzle coord1 coord2]
  (let [index1 (row-col->index puzzle coord1)
        index2 (row-col->index puzzle coord2)
        tile1 (get-tile puzzle coord1)
        tile2 (get-tile puzzle coord2)
        tiles (get-tiles puzzle)]
    (set-tiles puzzle
               (-> tiles
                   (assoc index1 tile2)
                   (assoc index2 tile1)))))

(defn- move [puzzle coords]
  (if (can-move? puzzle coords)
    (swap-tiles puzzle coords (get-space-coords puzzle))
    puzzle))

(defn- random-move [puzzle]
  (let [space-coords (get-space-coords puzzle)
        movable-tiles (get-adjacent-coords puzzle space-coords)
        random-movable-tile (nth movable-tiles (rand-int (count movable-tiles)))]
    (move puzzle random-movable-tile)))

(defn- shuffle-puzzle
  "Shuffles by moving a random steps"
  ([puzzle]
   (let [num-moves (+ 100 (rand 200))]
     (shuffle puzzle num-moves)))
  ([puzzle num-moves]
   (if (> num-moves 0)
     (recur (random-move puzzle) (dec num-moves))
     puzzle)))



;;;; Tests

(deftest puzzle-tests
  (let [p1 (make-puzzle 3)]
    (testing "make-puzzle"
      (is (= [1 2 3 4 5 6 7 8 :space] (get-tiles p1)))
      (is (= 3 (get-size p1))))
    (testing "get-tile"
      (is (= 1 (get-tile p1 [0 0])))
      (is (= 2 (get-tile p1 [0 1])))
      (is (= 5 (get-tile p1 [1 1]))))
    (testing "get-index-of-space"
      (is (= 8 (get-index-of-space p1))))
    (testing "get-space-coords"
      (is (= [2 2] (get-space-coords p1))))
    (testing "is-space?"
      (is (is-space? p1 [2 2]))
      (is (not (is-space? p1 [2 1]))))
    (testing "index->row-col"
      (is (= [1 1] (index->row-col p1 4)))
      (is (= [2 3] (index->row-col (make-puzzle 5) 13))))
    (testing "row-col->index"
      (is (= 4 (row-col->index p1 [1 1])))
      (is (= 13 (row-col->index (make-puzzle 5) [2 3]))))
    (testing "valid-coords?"
      (is (valid-coords? p1 [1 1]))
      (is (not (valid-coords? p1 [2 3]))))
    (testing "get-adjacent-coords"
      (is (= #{[0 1] [1 0] [1 2] [2 1]} (into #{} (get-adjacent-coords p1 [1 1]))))
      (is (= #{[0 1] [1 0]} (into #{} (get-adjacent-coords p1 [0 0])))))
    (testing "can-move?"
      (is (can-move? p1 [1 2]))
      (is (can-move? p1 [2 1]))
      (is (not (can-move? p1 [1 1]))))
    (testing "move"
      (is (= [1 2 3 4 5 :space 7 8 6] (get-tiles (move p1 [1 2]))))
      (is (= [1 2 3 4 :space 5 7 8 6] (get-tiles (move (move p1 [1 2]) [1 1]))))
      (is (= [1 2 3 4 5 6 7 8 :space] (get-tiles (move p1 [0 1])))))
    (testing "inversions"
      (is (= 10 (inversions (make-puzzle [1 8 2 :space 4 3 7 6 5] 3))))
      (is (= 41 (inversions (make-puzzle [13 2 10 3 1 12 8 4 5 :space 9 6 15 14 11 7] 4))))
      (is (= 62 (inversions (make-puzzle [6 13 7 10 8 9 11 :space 15 2 12 5 14 3 1 4] 4))))
      (is (= 56 (inversions (make-puzzle [3 9 1 15 14 11 4 6 13 :space 10 12 2 7 8 5] 4)))))
    (testing "is-solvable?"
      (is (is-solvable? (make-puzzle [1 8 2 :space 4 3 7 6 5] 3)))
      (is (is-solvable? (make-puzzle [13 2 10 3 1 12 8 4 5 :space 9 6 15 14 11 7] 4)))
      (is (is-solvable? (make-puzzle [6 13 7 10 8 9 11 :space 15 2 12 5 14 3 1 4] 4)))
      (is (not (is-solvable? (make-puzzle [3 9 1 15 14 11 4 6 13 :space 10 12 2 7 8 5] 4)))))))

(run-tests)
