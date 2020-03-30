package de.scrum_master.groovy_ast

public class Requires2Example {
	def a = 0

	@Requires2({divisor != a})
	public int divide10By(divisor) {
		10/divisor
	}
}
