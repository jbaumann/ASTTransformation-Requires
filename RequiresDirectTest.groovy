class RequiresDirectTest extends GroovyTestCase {
		
	public void testInvokeUnitTest() {
		def out = new RequiresExample()

		assert out.divide10By(2) == 5

		def exceptionMessage = shouldFail { out.divide10By(0) }
		assert exceptionMessage =~ /Precondition violated/
   }
}
