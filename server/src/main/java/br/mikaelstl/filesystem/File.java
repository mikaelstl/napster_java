package br.mikaelstl.filesystem;

import java.math.BigDecimal;

public record File(
  String filename,
  BigDecimal size
) {}
