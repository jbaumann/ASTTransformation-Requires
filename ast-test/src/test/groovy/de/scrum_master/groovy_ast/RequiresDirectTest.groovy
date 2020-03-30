package de.scrum_master.groovy_ast

import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class RequiresDirectTest {
  @Test
  void testInvokeUnitTest() {
    def out = new RequiresExample()

    assert out.divide10By(2) == 5

    def exceptionMessage = shouldFail { out.divide10By(0) }
    println exceptionMessage
    assert exceptionMessage =~ /Precondition violated/
  }
}
