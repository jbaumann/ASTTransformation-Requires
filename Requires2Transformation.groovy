import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.io.FileReaderSource
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class Requires2Transformation implements ASTTransformation {
	
	def annotationType = Requires2.class.name
	
	private boolean checkNode(astNodes, annotationType) {
		if (! astNodes) return false
		if (! astNodes[0]) return false
		if (! astNodes[1]) return false
		if (!(astNodes[0] instanceof AnnotationNode)) return false
		if (! astNodes[0].classNode?.name == annotationType) return false
		if (!(astNodes[1] instanceof MethodNode)) return false
		true
	}
   
	public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {

		if (!checkNode(astNodes, annotationType)) {
			addError("Internal Error: wrong arguments", astNodes[0], sourceUnit)
			return
		}

		MethodNode annotatedMethod = astNodes[1]
		def annotationExpression = astNodes[0].members.value
		
		if (annotationExpression.class != ClosureExpression) {
			addError("Internal Error: annotation doesn't contain closure", astNodes[0], sourceUnit)
			return
		}

		IfStatement block = createStatements(annotationExpression, sourceUnit)
		
		def methodStatements = annotatedMethod.code.statements
		methodStatements.add(0, block)
	}
	
	IfStatement createStatements(ClosureExpression closureAST, SourceUnit sourceUnit) {

		//String source = convertToSource(closureAST, sourceUnit)
		//def statements = "throw new Exception('Precondition violated: $source')"
		AstBuilder ab = new AstBuilder()
		//BlockStatement exception = ab.buildFromString(CompilePhase.SEMANTIC_ANALYSIS, false, statements)[0]
		
		BlockStatement exception = ab.buildFromCode {throw new Exception("Precondition violated in ${this.class}") }[0]
		
		List<ASTNode> res = ab.buildFromSpec {
			ifStatement {
				booleanExpression {
					not {
						methodCall {
							expression.add(closureAST)
							constant "call"
							argumentList {
							}
						}
					}
				}
				block {
					expression.add(exception)
				}
				empty()
			}
		}

		IfStatement is = res[0]
		return is
	}
	
	/**
	* Converts an ASTNode into the String source.
	*
	* @param node the ASTNode
	* @return the source the closure was created from
	*/
	private String convertToSource(ASTNode node, SourceUnit sourceUnit) {
		if (node == null) {
			addError("Error in convertToSource: node is null", node, sourceUnit)
			return ""
		}

		FileReaderSource sourceFileReader = sourceUnit.getSource()
		int first = node.getLineNumber()
		int last = node.getLastLineNumber()
		StringBuilder result = new StringBuilder();
		
		for (int line in first..last) {
			String content = sourceFileReader.getLine(line, null);
			if (content == null) {
				addError("Error accessing source for closure.", node, sourceUnit);
				content = ""
			}
			if (line == last) content = content[0 .. node.getLastColumnNumber()-1]
			if (line == first) content = content[node.getColumnNumber()-1 .. -1]

			result.append(content).append('\n');
		}
		result.toString().trim()
	}

	public void addError(String msg, ASTNode node, SourceUnit source) {
		int line = node.lineNumber
		int col = node.columnNumber
		SyntaxException se = new SyntaxException(msg + '\n', line, col)
		SyntaxErrorMessage sem = new SyntaxErrorMessage(se, source)
		source.errorCollector.addErrorAndContinue(sem)
	}
}