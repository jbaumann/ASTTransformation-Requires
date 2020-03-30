package de.scrum_master.groovy_ast

class RequiresExample {
  @Requires("divisor != 0")
  int divide10By(divisor) {
    10 / divisor
  }

  static void main(String[] args) {
    println new RequiresExample().divide10By(5)
    println new RequiresExample().divide10By(0)
  }
}
