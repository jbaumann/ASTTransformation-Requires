package de.xinaris.groovy_ast

class Requires2Example {
  def a = 0

  @Requires2({ divisor != a })
  int divide10By(divisor) {
    10 / divisor
  }

  static void main(String[] args) {
    println new Requires2Example().divide10By(5)
    println new Requires2Example().divide10By(0)
  }
}
