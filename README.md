# npuzzle

See in action: https://nakiya.github.io/npuzzle-gen/resources/public/index.html

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).


## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
