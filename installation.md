# Setting up Clojure

For getting started with Clojure development you need in general three pieces:

- [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads)
  (JavaSE 8 or higher, 7 works but end-of-lifecycle has already been
  announced by Oracle).
- [Leiningen](http://leiningen.org/), the build automation tool.
- An editor (like Emacs, Vim, Nightcode) or IDE (like IntelliJ, Eclipse, Lighttable).


Widely used IDEs / editors are
[IntelliJ+Cursive](https://cursiveclojure.com/),
[Emacs+CIDER](http://clojure-doc.org/articles/tutorials/emacs.html),
[Vim+Fireplace](http://www.neo.com/2014/02/25/getting-started-with-clojure-in-vim) and
[Eclipse+Counterclockwise](http://www.falkoriemenschneider.de/a__2014-05-27__Configuring-Eclipse-for-Clojure.html),
[LightTable](http://lighttable.com/).


## Hints for LightTable

In my opinion, LightTable is beginner-friendly but lacks some of the
more sophisticated editor features you'll find in Emacs or full-blown
IDEs, so for the long-term fun I recommend getting familiar with one
of the big ones.

You can control LightTable by commands which are made available on
Ctrl/Cmd+Space key.

To customize LightTable you can directly jump to user keymap and user behaviours. You can find both by hitting Ctrl/Cmd-Space and then typing "settings".

To setup a few paredit shortcuts add the following lines to the user keymap:
```clojure
      [:editor "ctrl-right" :paredit.grow.right]
      [:editor "ctrl-left" :paredit.shrink.right]
      [:editor "ctrl-alt-space" :paredit.select.parent]
```

To make LightTable always create a pair of parens add this line to user behaviours:
```clojure
      [:app :lt.objs.settings/pair-keymap-diffs]
```

Here are some other commands I use often:

  - toggle workspace tree
  - show connect bar
  - toggle console

Here are some keyboard shortcuts I use often:

  - Ctrl-Space        Show commands
  - Alt-F4            Close Lighttable
  - Ctrl-z            Undo
  - Ctrl-y            Redo
  - Ctrl-d            Show/hide docstring of function
  - TAB               Code completion, TAB chooses, ESC cancels
  - Ctrl-Shift-Enter  Evaluate complete file
  - Ctrl-Enter        Evaluate toplevel S-expression
  - Ctrl-Tab          Switch editor tab
  - Ctrl-1/2/3/...    Switch editor tab
  - Ctrl-w            Close tab/file
  - Ctrl-o            Open file
  - Ctrl-s            Save file
  - Ctrl-Right        Forward slurp (move right parens right)
  - Ctrl-Left         Forward barf (move right parens left)
  - Ctrl-Shift-Space  Select parent S-expression
  - Ctrl-x            Cut selection into clipboard
  - Ctrl-c            Copy selection to clipboard
  - Ctrl-v            Insert clipboard contents
  - Ctrl-.            Jump to definition
  - Ctrl-,            Jump back from definition
  - Ctrl-l            Goto line
  - Ctrl-f            Find text in file
  - Ctrl-ß            Font smaller
  - Ctrl-=            Font bigger

