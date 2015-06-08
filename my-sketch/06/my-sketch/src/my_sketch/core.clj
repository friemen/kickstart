(ns my-sketch.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def screen-size 500)

(defn setup []
  (q/frame-rate 100)
  (q/color-mode :hsb)
  {:dots [{:x 10 :y 10
           :vx 1 :vy 0
           :color (q/color 200 250 250)}
          {:x 10 :y 200
           :vx 1.5 :vy -1
           :color (q/color 300 250 250)}
          {:x 200 :y 100
           :vx -1.5 :vy -1
           :color (q/color 100 250 250)}]})

(defn move-dot
  [{:keys [x y vx vy] :as dot}]
  (let [newx (+ x vx)
        newy (+ y vy)
        newvx (if (or (> newx screen-size) (< newx 0)) (* -1 vx) vx)
        newvy (if (or (> newy screen-size) (< newy 0)) (* -1 vy) vy)]
    (assoc dot
       :x newx
       :y newy
       :vx newvx
       :vy (+ newvy 0.1))))


(defn update-state [state]
  {:dots (map move-dot (:dots state))})

(defn draw-state [state]
  (q/background 200)
  (doseq [dot (:dots state)]
    (q/fill (:color dot))
    (q/ellipse (:x dot) (:y dot) 30 30)))


(q/defsketch my-sketch
  :size [screen-size screen-size]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
