(ns npuzzle.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [npuzzle.core-test]))

(doo-tests 'npuzzle.core-test)
