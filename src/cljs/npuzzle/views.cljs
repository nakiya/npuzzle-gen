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

(defn- shuffle-again [e]
  (re-frame/dispatch [::ev/shuffle-puzzle]))

(defn- tile-div [idx tile]
  (let [is-solved? (re-frame/subscribe [::subs/is-solved?])]
    [:div {:style {:text-align :center
                   :padding "15px"
                   :font-size "20px"
                   :border (if (not= :space tile) "1px solid black" :none)
                   :background (if (not= :space tile) :lightblue nil)
                   :height "60px"
                   :width "60px"
                   ;;cursor = default for disabling caret on hover over text
                   :cursor :default}
           :onMouseDown (if @is-solved? nil on-tile-mouse-down)
           :data-tileindex idx}
     (if (= :space tile)
       " "
       tile)]))

(defn- tiles-container [puzzle size tile-fn]
  (into [:div {:style {:display :inline-grid
                       :grid-template-columns (apply str (repeat size "auto "))
                       :grid-gap "5px"
                       :border "2px solid black"}}]
        (map-indexed
         (fn [idx tile]
           (tile-fn idx tile)) puzzle)))

(defn- size-selector [sizes current-size]
  [:div.input-field.
   (into [:select {:name "puzzle-size"
                   :on-change on-puzzle-size-changed
                   :defaultValue current-size}]
         (for [size sizes]
           (into [:option {:value size} size])))
   [:label "Select size"]])

(defn- win-part []
  [:div
   [:h2 {:style {:color :green}} "Solved!"]
   [:button.waves-effect.waves-light.btn
    {:onClick shuffle-again}
    "Play again"]])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        sizes (re-frame/subscribe [::subs/size-choices])
        current-size (re-frame/subscribe [::subs/puzzle-size])
        puzzle (re-frame/subscribe [::subs/puzzle])
        is-solved? (re-frame/subscribe [::subs/is-solved?])]
    [:div.container
     [:h1 {:style {:color :skyblue}} "Hello to " @name]
     [:div
      {:style {:padding "10px"}}
      [:div.row
       [:div.col.s2 (size-selector @sizes @current-size)]]
       [:button.waves-effect.waves-light.btn.
        {:onClick shuffle-again} "Shuffle"]]
     (tiles-container @puzzle @current-size tile-div)
     (if @is-solved?
       (win-part))]))
