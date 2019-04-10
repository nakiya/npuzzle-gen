(ns npuzzle.css
  (:require [garden.def :refer [defstyles]]
            [garden.units :as u :refer [px pt]]))

(defstyles screen
  [:h1 {:color "red"}]
  [:.puzzle-tile
   {:text-align :center
    :padding "15px"
    :font-size "20px"
    :border "1px solid black"
    :border-radius "5px"
    :box-shadow "1px 1px 1px 1px #888888"
    :background :burlywood
    :height "60px"
    :width "60px"
    :cursor :default}])
