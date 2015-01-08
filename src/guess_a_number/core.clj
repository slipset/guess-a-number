(ns guess-a-number.core
  (:require [guess-a-number.scandahoovian :refer :all]))

(lag-kommando still [spørsmål]
              (skriv-ut spørsmål)
              (les-svar))

(lag-kommando spør-om-tall [spørsmål]
              (til-tall (still spørsmål)))

(lag-kommando tenk-på-et-tall-mellom-null-og [grense]
  (tilfeldig-mellom-null-og grense))

(lag-kommando spør-hva-er-grensen []
  (spør-om-tall "Hva er grensen?"))

(lag-kommando spør-om-å-gjette-på-tallet []
  (spør-om-tall "Hva tror du tallet er?"))
  
(lag-kommando gjettespillet []            
  (la [grense (spør-hva-er-grensen)
       tall (tenk-på-et-tall-mellom-null-og grense)]
      (skriv-ut "Ok, jeg tenker på et tall!")
      (snurr []
       (la [forslag (spør-om-å-gjette-på-tallet)]
           (skriv-ut "Du gjettet" forslag)
           (hvis-ikke (= forslag tall)
                      (sjekk
                       (mindre-enn forslag tall)  (skriv-ut "For lavt")
                       (større-enn forslag tall)  (skriv-ut "For høyt"))
                      (en-gang-til))))
      (skriv-ut "Riktig! Du klarte det!")))

(lag-kommando tenkespillet []
              (la [grense (spør-hva-er-grensen)]
                  (spør "Er du klar?")
                  (snurr [gjetting 0]
                         (la [spørsmål (sett-sammen "Er tallet " gjetting "?")
                              svar (still spørsmål)]
                             (hvis-ikke (= svar "Riktig!")
                                        (en-gang-til (+ 1 gjetting)))))
                  (skriv-ut "Jippi! Jeg klarte det!")))

(defn rund-av [n]
  (-> n
      double
      Math/round))


(defn spill-tenk-litt-smart []
  (let [grense (spør-hva-er-grensen)]
    (les-det-som-skrives "Er du klar?")
    (loop [[størst minst gjetting] [grense -1 (int (/ grense 2))]]
      (let [svar (les-det-som-skrives
                  (str "Er tallet " gjetting "?"))]
        (if (= svar "Riktig!")
          (println "Jippi! Jeg klarte det!")
          (recur (condp = svar
                   "For høyt" [gjetting minst
                               (rund-av (+ (/ (- gjetting minst) 2) minst))]
                   "For lavt" [størst gjetting
                               (rund-av (+ (/ (- gjetting minst) 2) gjetting))]
                   [størst minst gjetting])))))))
(defn mange-spill []
  (loop []
    (spill-gjett)
    (let [nytt-spill (les-det-som-skrives "Vil du spille en gang til?")]
      (when (= nytt-spill "ja")
        (recur)))))
