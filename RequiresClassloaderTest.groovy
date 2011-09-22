

class RequiresClassloaderTest extends GroovyTestCase {
			
	public void testInvokeUnitTest() {
		def file = new File('./RequiresExample.groovy')
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
