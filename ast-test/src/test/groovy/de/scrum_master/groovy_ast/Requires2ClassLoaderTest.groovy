package de.scrum_master.groovy_ast

import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class Requires2ClassLoaderTest {
  @Test
  void testInvokeUnitTest() {
    def file = new File('src/test/groovy/de/scrum_master/groovy_ast/Requires2Example.groovy')
    assert file.exists()

    GroovyClassLoader invoker = new GroovyClassLoader()
    def clazz = invoker.parseClass(file)
    def out = clazz.newInstance()

    assert out.divide10By(2) == 5

    def exceptionMessage = shouldFail { out.divide10By(0) }
    println exceptionMessage
    assert exceptionMessage =~ /Precondition violated/
  }
}
