# Clojure Kickstart Curriculum

This is a list of notes supporting a half or full day Clojure
Kickstart Workshop.  The goal is to get everyone up and running on a
concrete example and explain basic Clojure concepts and functions.

[General links and hints useful for getting started with Clojure](https://github.com/friemen/cugb/blob/master/getting-started.md)

# Installation

## Which software do I need?

For Clojure development you'll need three things:

- Java SDK 1.7 or higher
- [Leiningen](http://leiningen.org/) (the Clojure build automation tool)
- IDE / editor

Widely used IDEs / editors are
[IntelliJ+Cursive](https://cursiveclojure.com/),
[Eclipse+Counterclockwise](http://www.falkoriemenschneider.de/a__2014-05-27__Configuring-Eclipse-for-Clojure.html),
[Emacs+CIDER](http://clojure-doc.org/articles/tutorials/emacs.html),
[Vim+Fireplace](http://www.neo.com/2014/02/25/getting-started-with-clojure-in-vim) and
[LightTable](http://lighttable.com/).


## Your first project

Here's what you can do to make sure that your environment works.

In your IDE create a new Clojure project `helloweb` (or use the
Leiningen command `lein new helloweb`).  Replace contents of
project.clj and src/helloweb/core.clj with the contents found in this
[Gist](https://gist.github.com/friemen/45c2ea90737ac42a6a80).

Start the REPL, compile the helloweb.core namespace, switch into
helloweb.core (using a shortcut or evaluate in the REPL `(ns
helloweb.core)`) and start the http server in the REPL with the
expression `(start!)`.

Open a browser window, go to http://localhost:8080 and see a weird
output which represents the http request as a Clojure map.

Change the following piece in helloweb.core

```clojure
(defn app
  [request]
  {:status 200
   :body request})
```

to read like

```clojure
(defn app
  [request]
  {:status 200
   :body "Hello World"})
```

and re-evaluate the toplevel expression app in your IDE. Refresh the
browser window. You should see "Hello World" printed.

*Exercise*: Practise the round-trip of editing, re-evaluation and testing
 some snippet in the REPL.


## Explain project tooling

- Leiningen commands
- Profile
- Brief project.clj walkthrough
- Dependency declaration


## Introduction to the general syntax

- S-expressions
- Comma as whitespace
- Comments and docstrings
- Scalar types (keyword, number, string, boolean, character)
- Data structures (list, map, vector, set)
- Reader macros `#` and `@`, especially `#_`
- Paredit (a.k.a structural editing)

*Exercise*: Find out and get familiar with keyboard shortcuts for
slurp, barf, raise, join, split, copy+cut+paste, reindent using the
snippet `(1 2 3) (a b c)`.


## Explain helloweb.core

- Namespace semantics (dynamic map symbol -> var -> anything)
- Namespace declaration (`:require` and `:import`)
- Correspondence and differences to `require` and `import` in the REPL
- What exactly do `def`, `defonce` and `defn`?
- Walk through the functions in core

## Use the REPL

- Querying the namespace (`ns-publics`, `ns-interns`, `ns-refers`)
- Unmapping symbols `ns-unmap`
- `(require '[clojure.repl :refer :all])`
- Use `source` and `doc`
- `(require '[clojure.pprint :refer :all])`
- Use `pprint` and `*1`
- Introduce `use` as shortcut in the REPL.


*Exercise*: Define a new function that adds a list of numbers (Tip: use
 `reduce`), invoke it, query it in the namespace and unmap it.


# A more in-depth introduction to the language

As preparation, create a new file/namespace helloweb.playground to
avoid mixing exercises with webapp.

## More foundational stuff

- Functions are values
- Immutability as default
- Purity, the bang `!` and `do`
- Comparing values
- Thruthiness
- Nil is not the empty list (but close to)
- Exception handling
- General programming strategy in Clojure:
  - reduce your task to a data transformation problem
  - then use powerful core library to solve it
  - introduce APIs or even DSLs only when really necessary


## Datastructures and functions on those

- Vector `get`, `nth`, `conj`, `pop`, `peek`, `vector` vs. `vec`
- List `cons`, `conj`, `pop`, `peek`
- Set `conj`, `disj`, `contains?` (and alternative with set in fn position)
- Map `assoc`, `dissoc`, `get` (and alternatives), `keys`, `vals`, `conj`, `merge`
- Working with nested data `get-in`, `update-in`, thread-first `->`
- Common functions `empty`, `empty?`, `seq`, `first`, `rest`/`next`

*Exercise*: Create a concrete address "collection" in Clojure. Each address has
:name, :street, :numbers entry, where :numbers points to a vector of strings.
Use `update-in` to add a number to a particular address.


## Symbol binding and destructuring

- `let`
- Vector destructuring
- Map destructuring
- Destructuring in argument lists
- Other forms with bindings `for`, `binding`, 


## Branching and looping

- Conditional expressions `if`/`when` and friends, `cond`, `condp` and `case`.
- List comprehensions `for`
- Sideeffects `doseq`, `dotimes`
- loop/recur (with and without explicit `loop`)


## Sequence abstraction

- Explain idea of sequences and laziness `realized?`, `doall`, `dorun`
- From nil to sequence `cons`
- From collection to sequence `seq`
- Typical combinators `map`, `filter`/`remove`, `concat`, `mapcat`, `take`,
  `drop`, `reverse`, `interleave`, ...
- Producing infinite sequences `repeat`, `range`, `repeatedly`, `iterate`
- Terminal functions `into`, `reduce`, `count`, ...
- More power: `sort`, `distinct`, `group-by`, `partition-by`, `frequencies`, ...


*Exercise*: Create function `(indexof xs x)` that returns the first
 index of x in xs or nil, if x is not in xs. Tip: use `(map vector
 (range) xs)` to combine numerical index with item.

- Introduce thread-last `->>`

*Exercise*: Reorder the indexof implementation using `->>`.

- DIY with recursion, `lazy-seq` and `cons`

*Exercise*: Create a function `(countdown n)` that returns a lazy seq
of the values n, n-1, ..., 1.

## Functions on functions

- Pass-through `identity`
- Always return same value `constantly`
- Application to collection `apply`
- Partial application `partial`
- Composition `comp`
- Complementary predicate `complement`
- Juxtaposition `juxt`


*Exercise*: Rewrite `indexof` function so that the predicate passed to
 filter is created using partial and compose ("point-free style").


## Language topics intentionally omitted from the introduction

- Interop
- Record/Protocol
- Reftypes / STM
- Delay, Promise, Future
- Multimethod
- Dynamic scope
- Macros
- Transients (Volatile?)

# Extending helloweb


## Overview of web development in Clojure

- Explain basic idea of web development in Clojure
- Dedicated composable libraries for routing, rendering, validation,
  security, session management etc.
- Explain Ring Spec
	- Request and Response map
	- Handler
	- Middleware
	- Adapter
- Show what `site` does.
- Show example of how a tx boundary can be introduced with a HOF.

## Render HTML with Hiccup

- Explain the idea of Hiccup, which roles do vectors and maps play?
- Rendering HTML becomes a problem of assembling a suitable vector.
- Functions quickly start to form a kind of component-oriented DSL.

*Exercise*: Take your address collection to the helloweb.core namespace.
Then play around with Hiccups `html5` function, for example pass
`[:p "Boom!"]` to it, or `[:p {:foo "bar"} "Boom!"]`.


*Exercise*: Write functions that render the addresses collection in a
 table.


## Add routing with Compojure

- Explain the basic problem
- Show `defroutes`, `GET` and `POST`
- Explain parameter destructuring

*Exercise*: Add routing, link page to a stylesheet.css.


## Add address to collection

- State/Identity and atoms
- Explain reset! vs. swap!


*Exercise*: Add a form and another route and eventually a
 side-effecting function that conjoins the params map to the
 addresses.


# Introduction to Typed Clojure

For now, see [core.typed intro](https://github.com/friemen/cugb/tree/master/typed)
of Clojure Usergroup Bonn.

