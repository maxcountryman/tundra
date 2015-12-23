# tundra

*A template Om Next application*

![Tundra](https://upload.wikimedia.org/wikipedia/commons/8/87/Tundra_in_Siberia.jpg)

This is a template application meant to be used as a starting point for a web
application written entirely in Clojure and ClojureScript. It includes the
machinery connect the backend with the frontend via Om Next's Datomic-like
API.

## Status

Work-in-progress and is **NOT** ready for public consumption.

## Usage

From the project directory:

```sh
rlwrap lein run -m clojure.main script/figwheel.clj
```

This will launch the application. Once this process is running, you can visit
the development server in your browser at
[http://localhost:3449](http://localhost:3449).

## License

Copyright © 2015 Max Countryman

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
