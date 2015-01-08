(ns guess-a-number.scandahoovian)

(def skriv-ut println)
(def les-svar read-line)
(def tilfeldig-mellom-null-og rand-int)
(def til-tall read-string)
(def mindre-enn <)
(def større-enn >)

(defmacro lag-kommando [name & fdecl]
  `(defn ~name ~@fdecl))

(defmacro la [bindings & body]
  `(let [~@bindings]
     ~@body))

(defmacro hvis-ikke [p & body]
  `(when-not ~p
     ~@body))

(defmacro sjekk [& clauses]
  `(cond ~@clauses))

(defmacro en-gang-til [& body]
  `(recur ~@body))

(defmacro snurr [bindings & body]
  `(loop [~@bindings]
     ~@body))

(def sett-sammen str)
