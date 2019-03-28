(ns npuzzle.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [npuzzle.puzzle-test]))

(doo-tests 'npuzzle.puzzle-test)
