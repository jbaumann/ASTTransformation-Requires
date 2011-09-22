import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.tools.ast.TransformTestHelper

class RequiresTransformTestHelperTest extends GroovyTestCase {
		
	public void testInvokeUnitTest() {
		def file = new File('./RequiresExample.groovy')
		assert file.exists()
 
		def invoker = new TransformTestHelper(new RequiresTransformation(), CompilePhase.CONVERSION)

		def clazz = invoker.parse(file)
		def out = clazz.newInstance()
		
		assert out.divide10By(2) == 5
		shouldFail { out.divide10By(0) }
   }
}
