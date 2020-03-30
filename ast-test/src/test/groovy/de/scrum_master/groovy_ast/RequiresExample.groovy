package de.scrum_master.groovy_ast

public class RequiresExample {
	def a = 0

	@Requires("divisor != 0")
	@SuppressWarnings("all")
	public int divide10By(divisor) {
		10/divisor
	}

	public static void main(String[] args) {
		println new RequiresExample().divide10By(5)
		println new RequiresExample().divide10By(0)
	}
}
