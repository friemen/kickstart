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
Keyboard shortcut               | Description
--------------------------------|-------------------------------
<kbd>Ctrl-Space</kbd> |        Show commands
<kbd>Alt-F4</kbd> |            Close Lighttable
<kbd>Ctrl-z</kbd> |            Undo
<kbd>Ctrl-y</kbd> |            Redo
<kbd>Ctrl-d</kbd> |            Show/hide docstring of function
<kbd>TAB</kbd> |               Code completion, TAB chooses, ESC cancels
<kbd>Ctrl-Shift-Enter</kbd> |  Evaluate complete file
<kbd>Ctrl-Enter</kbd> |        Evaluate toplevel S-expression
<kbd>Ctrl-Tab</kbd> |          Switch editor tab
<kbd>Ctrl-1/2/3/...</kbd> |    Switch editor tab
<kbd>Ctrl-w</kbd> |            Close tab/file
<kbd>Ctrl-o</kbd> |            Open file
<kbd>Ctrl-s</kbd> |            Save file
<kbd>Ctrl-Right</kbd> |        Forward slurp (move right parens right)
<kbd>Ctrl-Left</kbd> |         Forward barf (move right parens left)
<kbd>Ctrl-Shift-Space</kbd> |  Select parent S-expression
<kbd>Ctrl-x</kbd> |            Cut selection into clipboard
<kbd>Ctrl-c</kbd> |            Copy selection to clipboard
<kbd>Ctrl-v</kbd> |            Insert clipboard contents
<kbd>Ctrl-.</kbd> |            Jump to definition
<kbd>Ctrl-,</kbd> |            Jump back from definition
<kbd>Ctrl-l</kbd> |            Goto line
<kbd>Ctrl-f</kbd> |            Find text in file
<kbd>Ctrl-ß</kbd> |            Font smaller
<kbd>Ctrl-=</kbd> |            Font bigger

