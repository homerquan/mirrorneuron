# Mirrorneuron Engine

A real-time decision engine

Readme:

Run CEP:
sbt run

Test CEP:
sbt test



Package into a jar (include deps):
sbt assembly

Run an interactive console(REPL):
sbt "run -c"

* telnet localhost 1981
* telnet localhost 1982

Branches:

stage_release -> dev build
prod_release -> rc build

## Requirement

Redis: ``docker run --name convospot-redis -d redis``


## Reference

AKKA Docs: https://doc.akka.io/docs/akka/2.5.6/scala/
Scala CLI opt parser: https://github.com/scopt/scopt