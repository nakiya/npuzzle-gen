(ns npuzzle.puzzle-test
  (:require [cljs.test :refer-macros [deftest testing is run-tests]]
            [npuzzle.puzzle :as pzl]))

(deftest puzzle-tests
  (let [p1 (pzl/make-puzzle 3)]
    (testing "make-puzzle"
      (is (= [1 2 3 4 5 6 7 8 :space] (pzl/get-tiles p1)))
      (is (= 3 (pzl/get-size p1))))
    (testing "get-tile"
      (is (= 1 (pzl/get-tile p1 [0 0])))
      (is (= 2 (pzl/get-tile p1 [0 1])))
      (is (= 5 (pzl/get-tile p1 [1 1]))))
    (testing "get-index-of-space"
      (is (= 8 (pzl/get-index-of-space p1))))
    (testing "get-space-coords"
      (is (= [2 2] (pzl/get-space-coords p1))))
    (testing "is-space?"
      (is (pzl/is-space? p1 [2 2]))
      (is (not (pzl/is-space? p1 [2 1]))))
    (testing "index->row-col"
      (is (= [1 1] (pzl/index->row-col p1 4)))
      (is (= [2 3] (pzl/index->row-col (pzl/make-puzzle 5) 13))))
    (testing "row-col->index"
      (is (= 4 (pzl/row-col->index p1 [1 1])))
      (is (= 13 (pzl/row-col->index (pzl/make-puzzle 5) [2 3]))))
    (testing "valid-coords?"
      (is (pzl/valid-coords? p1 [1 1]))
      (is (not (pzl/valid-coords? p1 [2 3]))))
    (testing "get-adjacent-coords"
      (is (= #{[0 1] [1 0] [1 2] [2 1]} (into #{} (pzl/get-adjacent-coords p1 [1 1]))))
      (is (= #{[0 1] [1 0]} (into #{} (pzl/get-adjacent-coords p1 [0 0])))))
    (testing "can-move?"
      (is (pzl/can-move? p1 [1 2]))
      (is (pzl/can-move? p1 [2 1]))
      (is (not (pzl/can-move? p1 [1 1]))))
    (testing "move"
      (is (= [1 2 3 4 5 :space 7 8 6] (pzl/get-tiles (pzl/move p1 [1 2]))))
      (is (= [1 2 3 4 :space 5 7 8 6] (pzl/get-tiles (pzl/move (pzl/move p1 [1 2]) [1 1]))))
      (is (= [1 2 3 4 5 6 7 8 :space] (pzl/get-tiles (pzl/move p1 [0 1])))))
    (testing "inversions"
      (is (= 10 (pzl/inversions (pzl/make-puzzle [1 8 2 :space 4 3 7 6 5] 3))))
      (is (= 41 (pzl/inversions (pzl/make-puzzle [13 2 10 3 1 12 8 4 5 :space 9 6 15 14 11 7] 4))))
      (is (= 62 (pzl/inversions (pzl/make-puzzle [6 13 7 10 8 9 11 :space 15 2 12 5 14 3 1 4] 4))))
      (is (= 56 (pzl/inversions (pzl/make-puzzle [3 9 1 15 14 11 4 6 13 :space 10 12 2 7 8 5] 4)))))
    (testing "is-solvable?"
      (is (pzl/is-solvable? (pzl/make-puzzle [1 8 2 :space 4 3 7 6 5] 3)))
      (is (pzl/is-solvable? (pzl/make-puzzle [13 2 10 3 1 12 8 4 5 :space 9 6 15 14 11 7] 4)))
      (is (pzl/is-solvable? (pzl/make-puzzle [6 13 7 10 8 9 11 :space 15 2 12 5 14 3 1 4] 4)))
      (is (not (pzl/is-solvable? (pzl/make-puzzle [3 9 1 15 14 11 4 6 13 :space 10 12 2 7 8 5] 4)))))
    (testing "shuffle-puzzle"
      (is (pzl/is-solvable? (pzl/shuffle-puzzle (pzl/make-puzzle 4))))
      (is (pzl/is-solvable? (pzl/shuffle-puzzle (pzl/make-puzzle 6))))
      (is (pzl/is-solvable? (pzl/shuffle-puzzle (pzl/make-puzzle 9)))))))

(run-tests)
