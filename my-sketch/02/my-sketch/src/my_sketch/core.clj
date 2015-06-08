(ns my-sketch.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def screen-size 500)



(defn setup []
  (q/frame-rate 100)
  (q/color-mode :hsb)
  {:dot {:x 150 :y 200 :color (q/color 150 100 150)}})


(defn update-state [state]
  state)

(defn draw-state [state]
  (q/background 200)
  (let [dot (:dot state)]
    (q/fill (:color dot))
    (q/ellipse (:x dot) (:y dot) 30 30)))


(q/defsketch my-sketch
  :size [screen-size screen-size]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
