package br.mikaelstl.filesystem;

import java.util.LinkedList;

public record Client (
  String ipAddress,
  LinkedList<File> files
) {}
