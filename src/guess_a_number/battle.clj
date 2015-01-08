(ns guess-a-number.battle
    (:require  [clojure.core.async :refer
                [timeout thread alt! alts! chan go-loop <! >! put! chan close!]]))

;; takes a number and a guess
;; returns :low if guess is to low
;; returns :high if guess is to high
;; returns :correct if guess is correct
(defn score-guess [number guess]
  (cond (< guess number) :low
        (> guess number) :high
        :else :correct))


(defn make-xducer [number]
  (comp (partial score-guess number)
         (fn [guess] (println "he guessed" guess) guess))
  
;; this is too cool! uses a monad^h^h^h^h^htransducer!1!!
(defn make-guess-channel [number]
  (chan 1 (map (make-xducer)))))

;; how can this not be part of clojure core?
(defn avg [coll]
  (/ (apply + coll) (count coll)))

;; poorly named function
;; computes a vector with new upper and lower limits and a new guess
(defn make-guess [dir [upper lower guess]]
  (let [result (condp = dir
                 :high [guess lower]
                  :low [upper guess])]
    (conj result (Math/round (double (avg result))))))

;; finally we managed to guess it, time to shut down
(defn done-guessing [guesses feedback]
  (println "I guessed it!")
  (close! guesses)
  (close! feedback))

;; the function implementing the guesser
;; basically, we create a upper and lower limit and a guess
;; then we put the guess on to a channel, wait for some feedback,
;; and either finish the game or make a new guess
(defn guesser [guesses feedback limit]
  (go-loop [state [limit -1 (int (/ limit 2))]]
    (>! guesses (last state))



    (alt! feedback ([result] (if (= :correct result)
                               (done-guessing guesses feedback)
                               (recur (make-guess result state)))))))

;; the function implementing the thinker
;; since we're using transducers, the numbers the guesser puts
;; on the channel is automagically translated into either
;; :high, :low, or :correct, so all we need to do is to put that
;; onto the feedback channel.
(defn thinker [guesses feedback number]
  (go-loop []
    (when (>! feedback (<! guesses))
      (recur))))

;; let the thinker and the guesser play, we give them a limit
(defn play-game [limit]
  (let [number (rand-int limit)
        guesses (make-guess-channel number)
        feedback (chan)]
    (println "the number to be guessed is" number)
    (thinker guesses feedback number)
    (guesser guesses feedback limit)))

(def out *out*)

(defn takes-a-while [chan x]
  (println "starting long running query")
  (thread (Thread/sleep 5000)
          (put! chan x)))

(defn run-it []
  (let [c (chan)]
    (takes-a-while c 5)
    (go-loop []
      (let [[val chnl] (alts! [c (timeout 500)])]
        (if (= c chnl)
          (println "got value at last" val)
          (do 
            (println "still waiting...")
            (recur)))))))
		
