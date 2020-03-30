package de.scrum_master.groovy_ast


import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.tools.ast.TransformTestHelper
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class RequiresTransformTestHelperTest {

	@Test
	public void testInvokeUnitTest() {
		def file = new File('src/test/groovy/de/scrum_master/groovy_ast/RequiresExample.groovy')
		assert file.exists()

		def invoker = new TransformTestHelper(new RequiresTransformation(), CompilePhase.CONVERSION)

		def clazz = invoker.parse(file)
		def out = clazz.newInstance()

		assert out.divide10By(2) == 5
		shouldFail { out.divide10By(0) }
   }
}
