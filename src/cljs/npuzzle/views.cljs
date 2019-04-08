(ns npuzzle.views
  (:require
   [re-frame.core :as re-frame]
   [npuzzle.subs :as subs]
   [npuzzle.events :as ev]))

(defn- on-puzzle-size-changed [e]
  (re-frame/dispatch [::ev/change-puzzle-size (int (-> e .-target .-value))]))

(defn- on-tile-mouse-down [e]
  (let [idx (.getAttribute (-> e .-target) "data-tileindex")]
    (re-frame/dispatch [::ev/move-tile (int idx)])))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        sizes (re-frame/subscribe [::subs/size-choices])
        current-size (re-frame/subscribe [::subs/puzzle-size])
        puzzle (re-frame/subscribe [::subs/puzzle])]
    [:div
     [:h1 "Hello to " @name]
     [:div
      {:style {:padding "10px"}}
      (into [:select {:name "puzzle-size"
                      :on-change on-puzzle-size-changed
                      :defaultValue @current-size}]
            (for [size @sizes]
              (into [:option {:value size} size])))]
     (into [:div {:style {:display :inline-grid
                          :grid-template-columns (apply str (repeat @current-size "auto "))
                          :grid-gap "10px"
                          :border "2px solid black"}}]
           (map-indexed
             (fn [idx tile]
               [:div {:style {:text-align :center
                              :padding "15px"
                              :font-size "20px"
                              :border (if (not= :space tile) "1px solid black" :none)
                              :background (if (not= :space tile) :lightblue nil)
                              :height "25px"
                              :width "25px"
                              ;;cursor = default for disabling caret on hover over text
                              :cursor :default}
                      :onMouseDown on-tile-mouse-down
                      :data-tileindex idx}
                (if (= :space tile)
                  " "
                  tile)]) @puzzle))]))

