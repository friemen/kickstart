(ns my-sketch.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def screen-size 500)


(defn setup []
  (q/frame-rate 100)
  (q/color-mode :hsb)
  ;; initially the state is the empty map
  {})


(defn update-state [state]
  state)

(defn draw-state [state]
  (q/background 200)
  (q/fill (q/color 250 200 250))
  (q/ellipse 150 200 30 30))


(q/defsketch my-sketch
  :size [screen-size screen-size]
  :setup setup
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode])
