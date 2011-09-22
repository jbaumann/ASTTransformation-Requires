public class RequiresExample {
	def a = 0
	
	@Requires("divisor != 0")
	@SuppressWarnings("all")
	public int divide10By(divisor) {
		10/divisor
	}
}
