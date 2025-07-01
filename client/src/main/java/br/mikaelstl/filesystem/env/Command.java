package br.mikaelstl.filesystem.env;

@FunctionalInterface
public interface Command<T, U> {
  void accept(T arg1, U arg2);
}
